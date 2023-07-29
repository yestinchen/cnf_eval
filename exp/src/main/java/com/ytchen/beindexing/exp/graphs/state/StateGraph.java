package com.ytchen.beindexing.exp.graphs.state;

import java.util.ArrayList;
import java.util.List;

import static com.ytchen.beindexing.exp.utils.StringUtils.join;

public class StateGraph<T> {

    List<T> rootState = new ArrayList<>();

    public List<T> getRootState() {
        return rootState;
    }

    public void setRootState(List<T> rootState) {
        this.rootState = rootState;
    }

    @Override
    public String toString() {
        return join(rootState, "\n");
    }
}
