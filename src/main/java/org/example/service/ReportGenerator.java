package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.model.JarAnalysisResult;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReportGenerator {
    private static final String SEPARATOR = "═".repeat(60);
    private static final String THIN_SEPARATOR = "─".repeat(60);

    private final ObjectMapper objectMapper;

    public ReportGenerator() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void printToConsole(JarAnalysisResult result) {
        printToConsole(result, System.out);
    }

    public void printToConsole(JarAnalysisResult result, PrintStream out) {
        out.println();
        out.println(SEPARATOR);
        out.println("  JAR BYTECODE ANALYSIS REPORT");
        out.println(SEPARATOR);
        out.println();

        out.printf("  File: %s%n", result.jarFileName());
        out.printf("  Total classes: %d%n", result.totalClasses());
        out.printf("  Total interfaces: %d%n", result.totalInterfaces());
        out.println();

        out.println(THIN_SEPARATOR);
        out.println("  INHERITANCE METRICS");
        out.println(THIN_SEPARATOR);
        out.printf("  Maximum inheritance depth: %d%n", result.inheritance().maxDepth());
        out.printf("  Average inheritance depth: %.2f%n", result.inheritance().averageDepth());
        out.println();

        out.println(THIN_SEPARATOR);
        out.println("  ABC METRICS");
        out.println(THIN_SEPARATOR);
        out.printf("  Assignments (A): %d%n", result.abc().totalAssignments());
        out.printf("  Branches (B): %d%n", result.abc().totalBranches());
        out.printf("  Conditions/Calls (C): %d%n", result.abc().totalConditions());
        out.printf("  ABC Magnitude: %.2f%n", result.abc().magnitude());
        out.println();

        out.println(THIN_SEPARATOR);
        out.println("  CLASS STRUCTURE METRICS");
        out.println(THIN_SEPARATOR);
        out.printf("  Average overridden methods per class: %.2f%n", result.averageOverriddenMethods());
        out.printf("  Average fields per class: %.2f%n", result.averageFieldsPerClass());
        out.println();

        out.println(SEPARATOR);
    }

    public void writeToJson(JarAnalysisResult result, Path outputPath) throws IOException {
        String json = objectMapper.writeValueAsString(result);
        Files.writeString(outputPath, json);
    }
}

