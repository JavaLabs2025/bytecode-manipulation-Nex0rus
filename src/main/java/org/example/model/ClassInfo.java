package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ClassInfo {
    private final String name;
    private final String superName;
    private final List<String> interfaces;
    private final Set<MethodInfo> methods;
    private final int fieldCount;
    private final ABCMetrics abcMetrics;
    private final boolean isInterface;

    private ClassInfo(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name cannot be null");
        this.superName = builder.superName;
        this.interfaces = List.copyOf(builder.interfaces);
        this.methods = Set.copyOf(builder.methods);
        this.fieldCount = builder.fieldCount;
        this.abcMetrics = builder.abcMetrics;
        this.isInterface = builder.isInterface;
    }

    public String getName() {
        return name;
    }

    public String getSuperName() {
        return superName;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public Set<MethodInfo> getMethods() {
        return methods;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public ABCMetrics getAbcMetrics() {
        return abcMetrics;
    }

    public boolean isInterface() {
        return isInterface;
    }

    @Override
    public String toString() {
        return String.format("ClassInfo{name='%s', super='%s', interfaces=%s, methods=%d, fields=%d}",
                name, superName, interfaces, methods.size(), fieldCount);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String superName;
        private final List<String> interfaces = new ArrayList<>();
        private final Set<MethodInfo> methods = new HashSet<>();
        private int fieldCount = 0;
        private ABCMetrics abcMetrics = new ABCMetrics();
        private boolean isInterface = false;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder superName(String superName) {
            this.superName = superName;
            return this;
        }

        public Builder addInterface(String interfaceName) {
            if (interfaceName != null) {
                this.interfaces.add(interfaceName);
            }
            return this;
        }

        public Builder addInterfaces(String[] interfaceNames) {
            if (interfaceNames != null) {
                for (String name : interfaceNames) {
                    addInterface(name);
                }
            }
            return this;
        }

        public Builder addMethod(MethodInfo method) {
            this.methods.add(method);
            return this;
        }

        public Builder incrementFieldCount() {
            this.fieldCount++;
            return this;
        }

        public Builder abcMetrics(ABCMetrics abcMetrics) {
            this.abcMetrics = abcMetrics;
            return this;
        }

        public Builder isInterface(boolean isInterface) {
            this.isInterface = isInterface;
            return this;
        }

        public ClassInfo build() {
            return new ClassInfo(this);
        }
    }
}

