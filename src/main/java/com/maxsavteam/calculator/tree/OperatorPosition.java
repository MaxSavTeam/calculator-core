package com.maxsavteam.calculator.tree;

public class OperatorPosition implements Comparable<OperatorPosition> {
    public final int level;
    public final int priority;
    public final int position;

    public OperatorPosition(int level, int priority, int position) {
        this.level = level;
        this.priority = priority;
        this.position = position;
    }

    @Override
    public int compareTo(OperatorPosition o) {
        if (level != o.level)
            return Integer.compare(level, o.level);
        if (priority != o.priority)
            return Integer.compare(priority, o.priority);
        return Integer.compare(position, o.position);
    }
}
