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
import org.wildfly.core.management.Access;
import org.wildfly.core.management.annotation.XmlRender;
import org.wildfly.core.management.processor.model.value.AttributeValueTypeDescription;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class AttributeDescription extends AbstractNamedMemberDescription implements NodeMemberDescription {
    private final AttributeValueTypeDescription valueType;
    private final Access access;
    private final boolean required;
    private final boolean wrapperElement;
    private final XmlRender.As renderAs;

    AttributeDescription(Builder builder) {
        super(builder);
        if ((valueType = builder.getValueType()) == null) {
            throw new IllegalArgumentException("Value type is null");
        }
        final Access access = builder.getAccess();
        this.access = access == null ? Access.READ_WRITE : access;
        required = builder.isRequired();
        wrapperElement = builder.isWrapperElement();
        final XmlRender.As renderAs = builder.getRenderAs();
        this.renderAs = renderAs == null ? XmlRender.As.ELEMENT : renderAs;
    }

    public AttributeValueTypeDescription getValueType() {
        return valueType;
    }

    public Access getAccess() {
        return access;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isWrapperElement() {
        return wrapperElement;
    }

    public XmlRender.As getRenderAs() {
        return renderAs;
    }

    public static class Builder extends AbstractNamedMemberDescription.Builder {
        private NodeClassDescription containingClass;
        private ExecutableElement executableElement;
        private AttributeValueTypeDescription valueType;
        private Access access;
        private boolean required = true;
        private boolean wrapperElement = false;
        private XmlRender.As renderAs;

        Builder() {
        }

        public static Builder create() {
            return new Builder();
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

        public AttributeValueTypeDescription getValueType() {
            return valueType;
        }

        public void setValueType(final AttributeValueTypeDescription valueType) {
            this.valueType = valueType;
        }

        public Access getAccess() {
            return access;
        }

        public void setAccess(final Access access) {
            this.access = access;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(final boolean required) {
            this.required = required;
        }

        public boolean isWrapperElement() {
            return wrapperElement;
        }

        public void setWrapperElement(final boolean wrapperElement) {
            this.wrapperElement = wrapperElement;
        }

        public XmlRender.As getRenderAs() {
            return renderAs;
        }

        public void setRenderAs(final XmlRender.As renderAs) {
            this.renderAs = renderAs;
        }

        public AttributeDescription build() {
            return new AttributeDescription(this);
        }
    }
}
