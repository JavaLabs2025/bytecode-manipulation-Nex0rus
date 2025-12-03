package org.example.service;

import org.example.model.ClassInfo;
import org.example.visitor.ClassInfoVisitor;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JarProcessor {
    private static final Logger log = Logger.getLogger(JarProcessor.class.getName());

    public List<ClassInfo> process(Path jarPath) throws IOException {
        List<ClassInfo> classes = new ArrayList<>();

        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (isClassFile(entry)) {
                    classes.add(processClassEntry(jarFile, entry));
                }
            }
        }

        log.info(() -> String.format("Processed %d classes from %s", classes.size(), jarPath.getFileName()));
        return classes;
    }

    private boolean isClassFile(JarEntry entry) {
        return !entry.isDirectory() && entry.getName().endsWith(".class");
    }

    private ClassInfo processClassEntry(JarFile jarFile, JarEntry entry) {
        try (InputStream inputStream = jarFile.getInputStream(entry)) {
            ClassReader classReader = new ClassReader(inputStream);
            ClassInfoVisitor collector = new ClassInfoVisitor();
            
            classReader.accept(collector, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            
            return collector.getClassInfo();
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to process class: " + entry.getName(), e);
        }
    }
}

