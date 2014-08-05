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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import org.wildfly.core.management.annotation.Schema;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class SchemaDescription extends AbstractDescription {
    private final List<RootResourceDescription> rootResourceDescriptions;
    private final String namespace;
    private final String version;
    private final Schema.Kind kind;
    private final URI schemaLocation;
    private final String schemaFileName;
    private final List<String> compatNamespaces;
    private final String xmlNamespace;

    private final TypeElement schemaElement;

    private final boolean external;

    SchemaDescription(Builder builder) {
        super(builder);
        rootResourceDescriptions = builder.getRootResourceDescriptions();
        if ((namespace = builder.getNamespace()) == null) {
            throw new IllegalArgumentException("Null namespace");
        }
        if ((version = builder.getVersion()) == null) {
            throw new IllegalArgumentException("Null version");
        }
        final Schema.Kind kind = builder.getKind();
        this.kind = kind == null ? Schema.Kind.EXTENSION : kind;
        if ((schemaLocation = builder.getSchemaLocation()) == null) {
            throw new IllegalArgumentException("Null schema location");
        }
        if ((schemaFileName = builder.getSchemaFileName()) == null) {
            throw new IllegalArgumentException("Null schema file name");
        }
        compatNamespaces = builder.getCompatNamespaces();
        if ((xmlNamespace = builder.getXmlNamespace()) == null) {
            throw new IllegalArgumentException("Null XML namespace");
        }
        if ((schemaElement = builder.getSchemaElement()) == null) {
            throw new IllegalArgumentException("Null schema type element");
        }
        external = builder.isExternal();
    }

    public List<RootResourceDescription> getRootResourceDescriptions() {
        return rootResourceDescriptions;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getVersion() {
        return version;
    }

    public Schema.Kind getKind() {
        return kind;
    }

    public URI getSchemaLocation() {
        return schemaLocation;
    }

    public String getSchemaFileName() {
        return schemaFileName;
    }

    public List<String> getCompatNamespaces() {
        return compatNamespaces;
    }

    public String getXmlNamespace() {
        return xmlNamespace;
    }

    public TypeElement getSchemaElement() {
        return schemaElement;
    }

    public boolean isExternal() {
        return external;
    }

    public static class Builder extends AbstractDescription.Builder {
        private List<RootResourceDescription> rootResourceDescriptions;
        private String namespace;
        private String version;
        private Schema.Kind kind;
        private URI schemaLocation;
        private String schemaFileName;
        private List<String> compatNamespaces;
        private String xmlNamespace;
    
        private TypeElement schemaElement;
    
        private boolean external;
    
        Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(final String namespace) {
            this.namespace = namespace;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(final String version) {
            this.version = version;
        }

        public Schema.Kind getKind() {
            return kind;
        }

        public void setKind(final Schema.Kind kind) {
            this.kind = kind;
        }

        public URI getSchemaLocation() {
            return schemaLocation;
        }

        public void setSchemaLocation(final URI schemaLocation) {
            this.schemaLocation = schemaLocation;
        }

        public String getSchemaFileName() {
            return schemaFileName;
        }

        public void setSchemaFileName(final String schemaFileName) {
            this.schemaFileName = schemaFileName;
        }

        public String getXmlNamespace() {
            return xmlNamespace;
        }

        public void setXmlNamespace(final String xmlNamespace) {
            this.xmlNamespace = xmlNamespace;
        }

        public TypeElement getSchemaElement() {
            return schemaElement;
        }

        public void setSchemaElement(final TypeElement schemaElement) {
            this.schemaElement = schemaElement;
        }

        public boolean isExternal() {
            return external;
        }

        public void setExternal(final boolean external) {
            this.external = external;
        }

        public List<RootResourceDescription> getRootResourceDescriptions() {
            return CollectionUtil.getList(rootResourceDescriptions, RootResourceDescription.class);
        }

        public void addRootResourceDescription(RootResourceDescription item) {
            if (item == null) {
                return;
            }
            if (rootResourceDescriptions == null) {
                rootResourceDescriptions = new ArrayList<>();
            }
            rootResourceDescriptions.add(item);
        }

        public List<String> getCompatNamespaces() {
            return CollectionUtil.getList(compatNamespaces, String.class);
        }

        public void addCompatNamespace(String item) {
            if (item == null) {
                return;
            }
            if (compatNamespaces == null) {
                compatNamespaces = new ArrayList<>();
            }
            compatNamespaces.add(item);
        }

        public SchemaDescription build() {
            return new SchemaDescription(this);
        }
    }
}
