package org.example.model;

public record JarAnalysisResult(
        String jarFileName,
        int totalClasses,
        int totalInterfaces,
        InheritanceMetrics inheritance,
        ABCSummary abc,
        double averageOverriddenMethods,
        double averageFieldsPerClass
) {
    public record InheritanceMetrics(
            int maxDepth,
            double averageDepth
    ) {
    }

    public record ABCSummary(
            int totalAssignments,
            int totalBranches,
            int totalConditions,
            double magnitude
    ) {
        public static ABCSummary from(ABCMetrics metrics) {
            return new ABCSummary(
                    metrics.getAssignments(),
                    metrics.getBranches(),
                    metrics.getConditions(),
                    metrics.calculateMagnitude()
            );
        }
    }
}

