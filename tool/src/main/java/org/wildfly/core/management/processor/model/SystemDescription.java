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

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.TypeElement;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class SystemDescription extends AbstractDescription {

    private final Map<String, SchemaDescription> schemas;
    private final Map<String, AttributeTypeDescription> attributeTypes;
    private final Map<String, NodeClassDescription> nodeClasses;

    SystemDescription(final Builder builder) {
        super(builder);
        schemas = builder.getSchemas();
        attributeTypes = builder.getAttributeTypes();
        nodeClasses = builder.getNodeClasses();
    }

    public Map<String, SchemaDescription> getSchemasByName() {
        return schemas;
    }

    public Map<String, AttributeTypeDescription> getAttributeTypesByName() {
        return attributeTypes;
    }

    public Map<String, NodeClassDescription> getNodeClassesByName() {
        return nodeClasses;
    }

    public SchemaDescription getSchema(String name) {
        return name == null ? null : schemas.get(name);
    }

    public SchemaDescription getSchema(TypeElement annotatedElement) {
        return annotatedElement == null ? null : schemas.get(annotatedElement.getQualifiedName().toString());
    }

    public AttributeTypeDescription getAttributeType(String name) {
        return name == null ? null : attributeTypes.get(name);
    }

    public AttributeTypeDescription getAttributeType(TypeElement annotatedElement) {
        return annotatedElement == null ? null : attributeTypes.get(annotatedElement.getQualifiedName().toString());
    }

    public NodeClassDescription getNodeClass(String name) {
        return name == null ? null : nodeClasses.get(name);
    }

    public NodeClassDescription getNodeClass(TypeElement annotatedElement) {
        return annotatedElement == null ? null : nodeClasses.get(annotatedElement.getQualifiedName().toString());
    }

    public static class Builder extends AbstractDescription.Builder {

        private Map<String, SchemaDescription> schemas;
        private Map<String, AttributeTypeDescription> attributeTypes;
        private Map<String, NodeClassDescription> nodeClasses;

        Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Map<String, SchemaDescription> getSchemas() {
            return CollectionUtil.getMap(schemas);
        }

        public SchemaDescription getSchema(String name) {
            return schemas == null || name == null ? null : schemas.get(name);
        }

        public SchemaDescription getSchema(TypeElement annotatedElement) {
            return schemas == null || annotatedElement == null ? null : schemas.get(annotatedElement.getQualifiedName().toString());
        }

        public void addSchema(SchemaDescription description) {
            if (description == null) {
                return;
            }
            Map<String, SchemaDescription> schemas = this.schemas;
            if (schemas == null) {
                schemas = this.schemas = new HashMap<>();
            }
            schemas.put(description.getSchemaElement().getQualifiedName().toString(), description);
        }

        public Map<String, AttributeTypeDescription> getAttributeTypes() {
            return CollectionUtil.getMap(attributeTypes);
        }

        public AttributeTypeDescription getAttributeType(String name) {
            return attributeTypes == null || name == null ? null : attributeTypes.get(name);
        }

        public AttributeTypeDescription getAttributeType(TypeElement annotatedElement) {
            return attributeTypes == null || annotatedElement == null ? null : attributeTypes.get(annotatedElement.getQualifiedName().toString());
        }

        public void addAttributeType(AttributeTypeDescription description) {
            if (description == null) {
                return;
            }
            Map<String, AttributeTypeDescription> attributeTypes = this.attributeTypes;
            if (attributeTypes == null) {
                attributeTypes = this.attributeTypes = new HashMap<>();
            }
            attributeTypes.put(description.getTypeElement().getQualifiedName().toString(), description);
        }

        public Map<String, NodeClassDescription> getNodeClasses() {
            return CollectionUtil.getMap(nodeClasses);
        }

        public NodeClassDescription getNodeClass(String name) {
            return nodeClasses == null || name == null ? null : nodeClasses.get(name);
        }

        public NodeClassDescription getNodeClass(TypeElement annotatedElement) {
            return nodeClasses == null || annotatedElement == null ? null : nodeClasses.get(annotatedElement.getQualifiedName().toString());
        }

        public void addNodeClass(NodeClassDescription description) {
            if (description == null) {
                return;
            }
            Map<String, NodeClassDescription> nodeClasses = this.nodeClasses;
            if (nodeClasses == null) {
                nodeClasses = this.nodeClasses = new HashMap<>();
            }
            nodeClasses.put(description.getTypeElement().getQualifiedName().toString(), description);
        }

        public SystemDescription build() {
            return new SystemDescription(this);
        }
    }
}
