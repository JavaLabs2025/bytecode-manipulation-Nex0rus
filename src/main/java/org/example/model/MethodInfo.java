package org.example.model;

import java.util.Objects;

public record MethodInfo(String name, String descriptor) {
    public MethodInfo {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(descriptor, "descriptor cannot be null");
    }

    public boolean isConstructor() {
        return "<init>".equals(name);
    }

    public boolean isStaticInitializer() {
        return "<clinit>".equals(name);
    }

    @Override
    public String toString() {
        return name + descriptor;
    }
}

