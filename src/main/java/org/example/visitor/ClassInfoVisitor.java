package org.example.visitor;

import org.example.model.ABCMetrics;
import org.example.model.ClassInfo;
import org.example.model.ClassInfo.Builder;
import org.example.model.MethodInfo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassInfoVisitor extends ClassVisitor {
    private final Builder builder;
    private final ABCMetrics abcMetrics;

    public ClassInfoVisitor() {
        super(Opcodes.ASM9);
        this.builder = ClassInfo.builder();
        this.abcMetrics = new ABCMetrics();
    }

    @Override
    public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces
    ) {
        builder.name(name)
               .superName(superName)
               .addInterfaces(interfaces)
               .isInterface((access & Opcodes.ACC_INTERFACE) != 0);
        
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(
            int access,
            String name,
            String descriptor,
            String signature,
            Object value
    ) {
        builder.incrementFieldCount();
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
    ) {
        MethodInfo methodInfo = new MethodInfo(name, descriptor);
        builder.addMethod(methodInfo);

        ABCMetrics methodMetrics = new ABCMetrics();
        
        return new ABCMetricsMethodVisitor(methodMetrics) {

            @Override
            public void visitEnd() {
                abcMetrics.add(methodMetrics);
                super.visitEnd();
            }
        };
    }

    @Override
    public void visitEnd() {
        builder.abcMetrics(abcMetrics);
        super.visitEnd();
    }

    public ClassInfo getClassInfo() {
        return builder.build();
    }
}

