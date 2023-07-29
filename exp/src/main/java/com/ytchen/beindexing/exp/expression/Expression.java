package com.ytchen.beindexing.exp.expression;

public class Expression<LEFT, OP, RIGHT> {
    LEFT left;
    OP op;
    RIGHT right;

    public LEFT getLeft() {
        return left;
    }

    public void setLeft(LEFT left) {
        this.left = left;
    }

    public OP getOp() {
        return op;
    }

    public void setOp(OP op) {
        this.op = op;
    }

    public RIGHT getRight() {
        return right;
    }

    public void setRight(RIGHT right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return left+" " + op+" "+ right;
    }
}
