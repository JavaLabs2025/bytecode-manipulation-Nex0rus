package org.example.visitor;

import org.example.model.ABCMetrics;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ABCMetricsMethodVisitor extends MethodVisitor {
    private final ABCMetrics metrics;

    public ABCMetricsMethodVisitor(ABCMetrics metrics) {
        super(Opcodes.ASM9);
        this.metrics = metrics;
    }


    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        if (isStoreInstruction(opcode)) {
            metrics.incrementAssignments();
        }
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitIincInsn(int varIndex, int increment) {
        metrics.incrementAssignments();
        super.visitIincInsn(varIndex, increment);
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        metrics.incrementBranches();
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInvokeDynamicInsn(
            String name,
            String descriptor,
            Handle bootstrapMethodHandle,
            Object... bootstrapMethodArguments
    ) {
        metrics.incrementBranches();
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (opcode == Opcodes.NEW) {
            metrics.incrementBranches();
        }
        super.visitTypeInsn(opcode, type);
    }


    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (opcode != Opcodes.GOTO && opcode != Opcodes.JSR) {
            metrics.incrementConditions();
        }
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        metrics.incrementConditions(labels.length);
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        metrics.incrementConditions(labels.length);
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    private boolean isStoreInstruction(int opcode) {
        return switch (opcode) {
            case Opcodes.ISTORE,
                 Opcodes.LSTORE,
                 Opcodes.FSTORE,
                 Opcodes.DSTORE,
                 Opcodes.ASTORE -> true;
            default -> false;
        };
    }
}

