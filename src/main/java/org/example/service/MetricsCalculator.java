package org.example.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.example.model.ABCMetrics;
import org.example.model.ClassInfo;
import org.example.model.JarAnalysisResult;
import org.example.model.JarAnalysisResult.ABCSummary;
import org.example.model.JarAnalysisResult.InheritanceMetrics;
import org.example.model.MethodInfo;

public class MetricsCalculator {
    private static final String JAVA_LANG_OBJECT = "java/lang/Object";
    private static final Set<MethodInfo> OBJECT_METHODS = Set.of(
            new MethodInfo("equals", "(Ljava/lang/Object;)Z"),
            new MethodInfo("hashCode", "()I"),
            new MethodInfo("toString", "()Ljava/lang/String;"),
            new MethodInfo("clone", "()Ljava/lang/Object;"),
            new MethodInfo("finalize", "()V")
    );

    public JarAnalysisResult calculate(String jarFileName, List<ClassInfo> classes) {
        Map<String, ClassInfo> classMap = classes.stream()
                .collect(Collectors.toMap(
                        ClassInfo::getName,
                        Function.identity(),
                        (ex, re) -> re
                ));

        int interfaceCount = 0;
        int classCount = 0;
        for (ClassInfo ci : classes) {
            if (ci.isInterface()) {
                interfaceCount++;
            } else {
                classCount++;
            }
        }

        int maxDepth = 0;
        int totalDepth = 0;
        for (ClassInfo ci : classes) {
            int depth = calculateInheritanceDepth(ci, classMap);
            totalDepth += depth;
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }
        double avgDepth = classes.isEmpty() ? 0 : (double) totalDepth / classes.size();

        ABCMetrics totalAbc = new ABCMetrics();
        for (ClassInfo ci : classes) {
            totalAbc.add(ci.getAbcMetrics());
        }

        int totalOverridden = 0;
        int classesWithMethods = 0;
        for (ClassInfo ci : classes) {
            if (!ci.isInterface()) {
                totalOverridden += countOverriddenMethods(ci, classMap);
                classesWithMethods++;
            }
        }
        double avgOverridden = classesWithMethods == 0 ? 0 : (double) totalOverridden / classesWithMethods;

        int totalFields = 0;
        for (ClassInfo ci : classes) {
            totalFields += ci.getFieldCount();
        }
        double avgFields = classes.isEmpty() ? 0 : (double) totalFields / classes.size();

        return new JarAnalysisResult(
                jarFileName,
                classCount,
                interfaceCount,
                new InheritanceMetrics(maxDepth, avgDepth),
                ABCSummary.from(totalAbc),
                avgOverridden,
                avgFields
        );
    }

    private int calculateInheritanceDepth(ClassInfo classInfo, Map<String, ClassInfo> classMap) {
        int depth = 1;
        String current = classInfo.getSuperName();

        while (current != null && !JAVA_LANG_OBJECT.equals(current)) {
            depth++;
            ClassInfo superClass = classMap.get(current);
            current = (superClass != null) ? superClass.getSuperName() : null;
        }

        return depth;
    }

    private int countOverriddenMethods(ClassInfo classInfo, Map<String, ClassInfo> classMap) {
        Set<MethodInfo> parentMethods = new HashSet<>(OBJECT_METHODS);

        collectMethodsFromSuperClass(classInfo.getSuperName(), classMap, parentMethods);

        for (String iface : classInfo.getInterfaces()) {
            collectMethodsFromInterface(iface, classMap, parentMethods);
        }

        int count = 0;
        for (MethodInfo method : classInfo.getMethods()) {
            if (method.isConstructor() || method.isStaticInitializer()) {
                continue;
            }
            if (parentMethods.contains(method)) {
                count++;
            }
        }

        return count;
    }

    private void collectMethodsFromSuperClass(
            String superClassName,
            Map<String, ClassInfo> classMap,
            Set<MethodInfo> result
    ) {
        if (superClassName == null || JAVA_LANG_OBJECT.equals(superClassName)) {
            return;
        }

        ClassInfo superClass = classMap.get(superClassName);
        if (superClass == null) {
            return;
        }

        for (MethodInfo method : superClass.getMethods()) {
            if (!method.isConstructor() && !method.isStaticInitializer()) {
                result.add(method);
            }
        }

        collectMethodsFromSuperClass(superClass.getSuperName(), classMap, result);

        for (String iface : superClass.getInterfaces()) {
            collectMethodsFromInterface(iface, classMap, result);
        }
    }

    private void collectMethodsFromInterface(
            String interfaceName,
            Map<String, ClassInfo> classMap,
            Set<MethodInfo> result
    ) {
        if (interfaceName == null) {
            return;
        }

        ClassInfo iface = classMap.get(interfaceName);
        if (iface == null) {
            return;
        }

        for (MethodInfo method : iface.getMethods()) {
            if (!method.isStaticInitializer()) {
                result.add(method);
            }
        }

        for (String parentInterface : iface.getInterfaces()) {
            collectMethodsFromInterface(parentInterface, classMap, result);
        }
    }
}

