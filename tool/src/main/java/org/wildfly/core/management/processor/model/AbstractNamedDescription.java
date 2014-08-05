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

import org.wildfly.core.management.processor.NameUtils;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public abstract class AbstractNamedDescription extends AbstractDescription {

    private final String xmlName;
    private final String dmrName;
    private final String javaName;

    AbstractNamedDescription(Builder builder) {
        super(builder);
        if ((javaName = builder.getJavaName()) == null) {
            throw new IllegalArgumentException("No Java-style node name given");
        }
        final String xmlName = builder.getXmlName();
        this.xmlName = xmlName == null ? NameUtils.xmlify(xmlName) : xmlName;
        final String dmrName = builder.getDmrName();
        this.dmrName = dmrName == null ? xmlName : dmrName;
    }

    public String getXmlName() {
        return xmlName;
    }

    public String getDmrName() {
        return dmrName;
    }

    public String getJavaName() {
        return javaName;
    }

    public static abstract class Builder extends AbstractDescription.Builder {
        private String javaName;
        private String xmlName;
        private String dmrName;

        Builder() {
        }

        public String getJavaName() {
            return javaName;
        }

        public void setJavaName(final String javaName) {
            this.javaName = javaName;
        }

        public String getXmlName() {
            return xmlName;
        }

        public void setXmlName(final String xmlName) {
            this.xmlName = xmlName;
        }

        public String getDmrName() {
            return dmrName;
        }

        public void setDmrName(final String dmrName) {
            this.dmrName = dmrName;
        }

        public abstract AbstractNamedDescription build();
    }
}
