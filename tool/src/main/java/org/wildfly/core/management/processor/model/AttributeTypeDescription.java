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

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import org.wildfly.core.management.processor.model.value.PrimitiveAttributeValueTypeDescription;
import org.wildfly.core.management.processor.model.value.SimpleAttributeValueTypeDescription;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class AttributeTypeDescription extends AbstractNamedDescription {
    private final TypeElement typeElement;
    private final List<SimpleAttributeValueTypeDescription> components;

    AttributeTypeDescription(Builder builder) {
        super(builder);
        if ((typeElement = builder.getTypeElement()) == null) {
            throw new IllegalArgumentException("Type element is null");
        }
        components = builder.getComponents();
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public List<SimpleAttributeValueTypeDescription> getComponents() {
        return components;
    }

    public static class Builder extends AbstractNamedDescription.Builder {
        private TypeElement typeElement;
        private List<SimpleAttributeValueTypeDescription> components;

        Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public TypeElement getTypeElement() {
            return typeElement;
        }

        public void setTypeElement(final TypeElement typeElement) {
            this.typeElement = typeElement;
        }

        public List<SimpleAttributeValueTypeDescription> getComponents() {
            return CollectionUtil.getList(components, SimpleAttributeValueTypeDescription.class);
        }

        public void addComponent(PrimitiveAttributeValueTypeDescription item) {
            doAddComponent(item);
        }

        private void doAddComponent(final SimpleAttributeValueTypeDescription item) {
            if (item == null) {
                return;
            }
            if (components == null) {
                components = new ArrayList<>();
            }
            components.add(item);
        }

        public AttributeTypeDescription build() {
            return new AttributeTypeDescription(this);
        }
    }
}
