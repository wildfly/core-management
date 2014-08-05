/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.core.management.processor.model;

import javax.lang.model.element.ExecutableElement;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public abstract class AbstractNamedMemberResourceDescription extends ResourceDescription implements NodeMemberDescription {
    private final NodeClassDescription containingClass;
    private final ExecutableElement executableElement;

    AbstractNamedMemberResourceDescription(final Builder builder) {
        super(builder);
        if ((containingClass = builder.getContainingClass()) == null) {
            throw new IllegalArgumentException("Containing class is null");
        }
        if ((executableElement = builder.getExecutableElement()) == null) {
            throw new IllegalArgumentException("Executable element is null");
        }
    }

    public NodeClassDescription getContainingClass() {
        return containingClass;
    }

    public ExecutableElement getExecutableElement() {
        return executableElement;
    }

    public static abstract class Builder extends ResourceDescription.Builder {
        private NodeClassDescription containingClass;
        private ExecutableElement executableElement;

        Builder() {
        }

        public NodeClassDescription getContainingClass() {
            return containingClass;
        }

        public void setContainingClass(final NodeClassDescription containingClass) {
            this.containingClass = containingClass;
        }

        public ExecutableElement getExecutableElement() {
            return executableElement;
        }

        public void setExecutableElement(final ExecutableElement executableElement) {
            this.executableElement = executableElement;
        }

        public abstract AbstractNamedMemberResourceDescription build();
    }
}