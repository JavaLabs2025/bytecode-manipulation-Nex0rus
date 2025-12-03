package org.example.model;

public final class ABCMetrics {
    private int assignments;
    private int branches;
    private int conditions;

    public ABCMetrics() {
        this(0, 0, 0);
    }

    public ABCMetrics(int assignments, int branches, int conditions) {
        this.assignments = assignments;
        this.branches = branches;
        this.conditions = conditions;
    }

    public void incrementAssignments() {
        assignments++;
    }

    public void incrementBranches() {
        branches++;
    }

    public void incrementConditions() {
        conditions++;
    }

    public void incrementConditions(int delta) {
        conditions += delta;
    }

    public void add(ABCMetrics other) {
        this.assignments += other.assignments;
        this.branches += other.branches;
        this.conditions += other.conditions;
    }

    public int getAssignments() {
        return assignments;
    }

    public int getBranches() {
        return branches;
    }

    public int getConditions() {
        return conditions;
    }

    public double calculateMagnitude() {
        return Math.sqrt(
                (double) assignments * assignments +
                (double) branches * branches +
                (double) conditions * conditions
        );
    }

    @Override
    public String toString() {
        return String.format("ABC(A=%d, B=%d, C=%d, magnitude=%.2f)",
                assignments, branches, conditions, calculateMagnitude());
    }
}

