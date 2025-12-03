package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.example.model.ClassInfo;
import org.example.model.JarAnalysisResult;
import org.example.service.JarProcessor;
import org.example.service.MetricsCalculator;
import org.example.service.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarAnalyzerApp {
    private static final Logger log = LoggerFactory.getLogger(JarAnalyzerApp.class);

    private final JarProcessor jarProcessor;
    private final MetricsCalculator metricsCalculator;
    private final ReportGenerator reportGenerator;

    public JarAnalyzerApp() {
        this.jarProcessor = new JarProcessor();
        this.metricsCalculator = new MetricsCalculator();
        this.reportGenerator = new ReportGenerator();
    }

    public void analyze(String jarPath, String outputPath) {
        try {
            Path jarFilePath = Path.of(jarPath);
            validateInputFile(jarFilePath);

            System.out.println("Processing JAR file: " + jarFilePath.getFileName());
            List<ClassInfo> classes = jarProcessor.process(jarFilePath);

            if (classes.isEmpty()) {
                log.warn("No classes found in the JAR file");
            }

            System.out.println("Calculating metrics...");
            String jarFileName = jarFilePath.getFileName().toString();
            JarAnalysisResult result = metricsCalculator.calculate(jarFileName, classes);

            if (outputPath == null) {
                reportGenerator.printToConsole(result);
            } else {
                writeToFile(result, outputPath);
            }
        } catch (Exception e) {
            log.error("Application run failed", e);
            System.exit(1);
        }
    }

    private void writeToFile(JarAnalysisResult result, String outputPath) throws IOException {
        Path outputFilePath = Path.of(outputPath);
        reportGenerator.writeToJson(result, outputFilePath);
        System.out.println("JSON report written to: " + outputFilePath.toAbsolutePath());
    }

    private void validateInputFile(Path jarPath) {
        if (!Files.exists(jarPath)) {
            throw new IllegalArgumentException("Input file does not exist: " + jarPath);
        }

        if (!Files.isRegularFile(jarPath)) {
            throw new IllegalArgumentException("Input path is not a file: " + jarPath);
        }

        if (!jarPath.toString().toLowerCase().endsWith(".jar")) {
            throw new IllegalArgumentException("Input file must be a JAR file: " + jarPath);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage:");
            System.out.println("java -jar analyzer.jar <input.jar> - output to console");
            System.out.println("java -jar analyzer.jar <input.jar> <output.json> - output to file");
            System.exit(1);
        }

        String inputJar = args[0];
        String outputJson = args.length == 2 ? args[1] : null;

        JarAnalyzerApp app = new JarAnalyzerApp();
        app.analyze(inputJar, outputJson);
    }
}
