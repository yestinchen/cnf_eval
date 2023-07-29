package com.ytchen.beindexing.exp;

import com.ytchen.beindexing.exp.cnf.*;
import com.ytchen.beindexing.exp.cnf.enhanced.EnhancedBatchCNFAlgorithm;
import com.ytchen.beindexing.exp.cnf.enhanced.EnhancedCNFAlgorithm;
import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.graphs.be.*;
import com.ytchen.beindexing.exp.graphs.builder.*;
import com.ytchen.beindexing.exp.graphs.eval.CNFEvaluator;
import com.ytchen.beindexing.exp.graphs.obj.*;
import com.ytchen.beindexing.exp.utils.MemoryMeasurement;
import com.ytchen.beindexing.exp.utils.Ticker;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.ytchen.beindexing.exp.utils.ControlUtils.loop;

public class Main {

    enum Methods {
        StateGraph, EVAL, PAPER_EVAL;
    }

    enum StateAlgorithms {
        NAIVE, SSG, MFS;
    }

    public static void stateGraph(String[] args) throws IOException {
        // args: algorithm, filePath, duration, minCount, frameLimit, executeTimes
        StateAlgorithms algorithm = StateAlgorithms.valueOf(args[0]);
        String file = args[1];
        int duration = Integer.valueOf(args[2]);
        int minCount = Integer.valueOf(args[3]);
        int frameLimit = Integer.valueOf(args[4]);
        int executeTimes = Integer.valueOf(args[5]);
        int frameRate = Integer.valueOf(args.length > 6 ? args[6]: "1");
        duration = duration / frameRate;
        minCount = minCount / frameRate;

        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);
        // load only car & person.
        List<String> allowed = Arrays.asList("car", "person", "truck", "bus");
        for (CompactedObjectSequence seq: frames) {
            if (seq == null) continue;
            for (int i =seq.getSequence().size()-1;  i>=0 ;i--) {
                if (seq.getSequence().get(i).getIds().size() == 0) {
                    seq.getSequence().remove(i);
                } else if (!allowed.contains(seq.getSequence().get(i).getClazz())) {
                    seq.getSequence().remove(i);
                }

            }
        }
        for (CompactedObjectSequence seq : frames) {
            if (seq == null || seq.getSequence() == null) continue;
            for (CompactedObj obj : seq.getSequence()) {
                obj.setIds(new ArrayList<>(new HashSet<>(obj.getIds())));
            }
        }
        frames.setSequence(frames.getSequence().subList(0, frameLimit));
        // frame rate.
        for (int i = frames.getSequence().size() -1 ; i>=0 ; i--) {
            if (i % frameRate != 0) {
                frames.getSequence().remove(i);
            }
        }
        frames.getSequence().forEach(i -> { if (i != null) i.sort(); });
        IStateBuilder builder = null;
        switch(algorithm) {
            case MFS:
                builder = new MFSBuilder(duration, minCount);
                break;
            case NAIVE:
                builder = new NaiveBuilder(duration, minCount);
                break;
            case SSG:
                builder = new SSGBuilder(duration, minCount);
                break;
        }
        // start ticking.
        IStateBuilder finalBuilder = builder;
        Ticker ticker = new Ticker();
        List<MemoryMeasurement> memoryMeasurements = new ArrayList<>();
        loop(executeTimes, () -> {
            // prepare.
            System.gc();
            MemoryMeasurement memoryMeasurement = new MemoryMeasurement();
            ticker.startTick();
            int frameCount = 0;
            for (CompactedObjectSequence seq : frames) {
                if (seq != null) {
                    seq.sort();
                    finalBuilder.feed(seq);
                }
                frameCount ++;
                if(frameCount % 100 == 0) {
                    ticker.stopTick();
                    System.out.println("#frame: " + frameCount);
                    ticker.startTick();
                }
            }
            ticker.stopTick();
            // clean up.
            finalBuilder.reset();
            System.gc();
            memoryMeasurements.add(memoryMeasurement);
        });
        System.out.println("average time: " + (ticker.getTotal() / executeTimes));
        System.out.println("---------");
        MemoryMeasurement avgMeasure = MemoryMeasurement.merge(memoryMeasurements);
        avgMeasure.getValues().forEach(System.out::println);
    }

    enum EvalAlgorithm {
        CNF, ECNF, BATCH_CNF, BATCH_ECNF;
    }

    public static void evaluate(String[] args) throws IOException {
        // args: evalAlgorithm, cnfPath, algorithm, filePath, duration, minCount, frameLimit, executeTimes.
        EvalAlgorithm evalAlgorithm = EvalAlgorithm.valueOf(args[0]);
        String cnfFile = args[1];
        StateAlgorithms stateAlgorithm = StateAlgorithms.valueOf(args[2]);
        String file = args[3];
        int duration = Integer.valueOf(args[4]);
        int minCount = Integer.valueOf(args[5]);
        int frameLimit = Integer.valueOf(args[6]);
        int executeTimes = Integer.valueOf(args[7]);

        // prepare.
        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);

        List<String> allowed = Arrays.asList("car", "person", "truck", "bus");
        for (CompactedObjectSequence seq: frames) {
            if (seq == null) continue;
            for (int i =seq.getSequence().size()-1;  i>=0 ;i--) {
                if (seq.getSequence().get(i).getIds().size() == 0) {
                    seq.getSequence().remove(i);
                } else if (!allowed.contains(seq.getSequence().get(i).getClazz())) {
                    seq.getSequence().remove(i);
                }

            }
        }
        for (CompactedObjectSequence seq : frames) {
            if (seq == null || seq.getSequence() == null) continue;
            for (CompactedObj obj : seq.getSequence()) {
                obj.setIds(new ArrayList<>(new HashSet<>(obj.getIds())));
            }
        }
        frames.setSequence(frames.getSequence().subList(0, frameLimit));


        for (CompactedObjectSequence seq : frames) {
            if (seq == null || seq.getSequence() == null) continue;
            for (CompactedObj obj : seq.getSequence()) {
                obj.setIds(new ArrayList<>(new HashSet<>(obj.getIds())));
            }
        }

        IStateBuilder builder = null;
        switch(stateAlgorithm) {
            case MFS:
                builder = new MFSBuilder(duration, minCount);
                break;
            case NAIVE:
                builder = new NaiveBuilder(duration, minCount);
                break;
            case SSG:
                builder = new SSGBuilder(duration, minCount);
                break;
        }
        IStateBuilder finalBuilder = builder;

        CNFEvaluator evaluator = null;
        Ticker ticker = new Ticker();
        if (evalAlgorithm == EvalAlgorithm.CNF || evalAlgorithm == EvalAlgorithm.ECNF) {
            ICNFEvaluator cnfAlgorithm = null;
            switch (evalAlgorithm) {
                case CNF:
                    cnfAlgorithm = CNFAlgorithm.fromFile(cnfFile);
                    break;
                case ECNF:
                    cnfAlgorithm = EnhancedCNFAlgorithm.fromFile(cnfFile + "2");
                    break;
            }
            evaluator = new CNFEvaluator(cnfAlgorithm,
                    (x) -> {
                        ticker.startTick();
                        List<CompactedObjectSequence> seq = finalBuilder.feed(x);
                        ticker.stopTick();
                        if (seq == null) return null;
                        return seq.stream().map(i -> i.genAssignment()).collect(Collectors.toList());
                    });
        } else if (evalAlgorithm == EvalAlgorithm.BATCH_CNF || evalAlgorithm == EvalAlgorithm.BATCH_ECNF) {
            ICNFBatchEvaluator batchAlgorithm = null;
            switch(evalAlgorithm) {
                case BATCH_CNF:
                    batchAlgorithm = CNFBatchAlgorithm.fromFile(cnfFile);
                    break;
                case BATCH_ECNF:
                    batchAlgorithm = EnhancedBatchCNFAlgorithm.fromFile(cnfFile + "2");
                    break;
            }
            evaluator = new CNFEvaluator(batchAlgorithm,
                    (x) -> {
                        ticker.startTick();
                        List<CompactedObjectSequence> seq = finalBuilder.feed(x);
                        ticker.stopTick();
                        if (seq == null) return null;
                        return seq.stream().map(i -> i.genAssignment()).collect(Collectors.toList());
                    });
        }

        // start ticking.
        long startTime = System.currentTimeMillis();
        CNFEvaluator finalEvaluator = evaluator;
        loop(executeTimes, () -> {
            int frameCount = 0;
            for (CompactedObjectSequence seq : frames) {
                if (seq == null) continue;
                List<Integer> result = finalEvaluator.eval(seq);
                frameCount ++;
                if(frameCount % 100 == 0) {
                    ticker.stopTick();
                    ticker.startTick();
                }
            }
            System.gc();
            finalBuilder.reset();
        });
        long endTime = System.currentTimeMillis();
        System.out.println("average state time: " + ticker.getTotal() / executeTimes);
        System.out.println("average time: " + ((endTime - startTime) / executeTimes));

    }

    enum FullEvalAlgorithm {
        // normal evaluation, optimized evaluation.
        NAIVE_EVAL,
        SSG_EVAL, SSG_OEVAL, SSG_NAIVE,
        MFS_EVAL, MFS_OEVAL;
    }

    public static void paperEval(String[] args) throws IOException {
        FullEvalAlgorithm evalAlgorithm = FullEvalAlgorithm.valueOf(args[0]);
        String cnfFile = args[1];
        String file = args[2];
        int duration = Integer.valueOf(args[3]);
        int minCount = Integer.valueOf(args[4]);
        int frameLimit = Integer.valueOf(args[5]);
        int executeTimes = Integer.valueOf(args[6]);

        // prepare.
        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);

        List<String> allowed = Arrays.asList("car", "person", "truck", "bus");
        for (CompactedObjectSequence seq: frames) {
            if (seq == null) continue;
            for (int i =seq.getSequence().size()-1;  i>=0 ;i--) {
                if (seq.getSequence().get(i).getIds().size() == 0) {
                    seq.getSequence().remove(i);
                } else if (!allowed.contains(seq.getSequence().get(i).getClazz())) {
                    seq.getSequence().remove(i);
                }

            }
        }
        for (CompactedObjectSequence seq : frames) {
            if (seq == null || seq.getSequence() == null) continue;
            for (CompactedObj obj : seq.getSequence()) {
                obj.setIds(new ArrayList<>(new HashSet<>(obj.getIds())));
            }
        }
        frames.setSequence(frames.getSequence().subList(0, frameLimit));

        for (CompactedObjectSequence seq : frames) {
            if (seq == null || seq.getSequence() == null) continue;
            for (CompactedObj obj : seq.getSequence()) {
                obj.setIds(new ArrayList<>(new HashSet<>(obj.getIds())));
            }
        }
        SequenceEvaluator evaluator = null;

        switch (evalAlgorithm) {
            case NAIVE_EVAL:
                NaiveBuilder stateBuilder1 =
                        new NaiveBuilder(duration, minCount);
                ICNFEvaluator algorithm1 = EnhancedCNFAlgorithm.fromFile(cnfFile+"2");
                evaluator = new DefaultSequenceEvaluator(stateBuilder1, new ICNFEvaluator() {
                    @Override
                    public List<Integer> evaluate(List<Tuple2<String, Integer>> assignment) {
                        List<Integer> result = algorithm1.evaluate(assignment);
//                        evalTime1.stopTick();
                        return result;
                    }
                });
                break;
            case SSG_EVAL:
                SSGBuilder stateBuilder = new SSGBuilder(duration, minCount);
                ICNFEvaluator algorithm = EnhancedCNFAlgorithm.fromFile(cnfFile + "2");

                evaluator = new DefaultSequenceEvaluator(stateBuilder, new ICNFEvaluator() {
                    @Override
                    public List<Integer> evaluate(List<Tuple2<String, Integer>> assignment) {
//                        evalTime1.startTick();
                        List<Integer> result = algorithm.evaluate(assignment);
//                        evalTime1.stopTick();
                        return result;
                    }
                });
                break;
            case MFS_EVAL:
                MFSBuilder stateBuilderMFS =
                        new MFSBuilder(duration, minCount);
                ICNFEvaluator algorithmMFS = EnhancedCNFAlgorithm.fromFile(cnfFile + "2");

                evaluator = new DefaultSequenceEvaluator(stateBuilderMFS, new ICNFEvaluator() {
                    @Override
                    public List<Integer> evaluate(List<Tuple2<String, Integer>> assignment) {
//                        evalTime1.startTick();
                        List<Integer> result = algorithmMFS.evaluate(assignment);
//                        evalTime1.stopTick();
                        return result;
                    }
                });
                break;
            case MFS_OEVAL:
                ICNFEvaluator algorithmMFS2 = EnhancedCNFAlgorithm.fromFile(cnfFile + "2");
                evaluator = new MFSIntegratedEvaluatorV2(duration, minCount, algorithmMFS2);
                break;
            case SSG_OEVAL:
                ICNFEvaluator algorithm2 = EnhancedCNFAlgorithm.fromFile(cnfFile + "2");
                evaluator = new SSGSimplifiedIntegratedEvaluator(
                        duration, minCount, algorithm2
                );
                break;
        }

        // start ticking.
        Ticker ticker = new Ticker();
        SequenceEvaluator finalEvaluator = evaluator;
        loop(executeTimes, () -> {
            ticker.startTick();
            int frameCount = 0;
            for (CompactedObjectSequence seq : frames) {
                if (seq != null) {
                    List<Integer> result = finalEvaluator.evaluate(seq);
                }
                frameCount ++;
            }
            ticker.stopTick();
            finalEvaluator.reset();
            System.gc();
        });
        System.out.println("average time: " + (ticker.getTotal() / executeTimes));
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Please indicating the method to run, choices: "+Methods.values());
            System.exit(0);
        }
        String[] params = Arrays.asList(args).subList(1,args.length).toArray(new String [0]);
        // first arg should be the method.
        switch(Methods.valueOf(args[0])){
            case StateGraph:
                stateGraph(params);
                break;
            case EVAL:
                evaluate(params);
                break;
            case PAPER_EVAL:
                paperEval(params);
                break;
        }
    }
}
