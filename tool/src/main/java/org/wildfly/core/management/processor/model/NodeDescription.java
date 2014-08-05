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

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public abstract class NodeDescription extends AbstractNamedDescription {

    private final String xmlTypeName;

    private final NodeClassDescription nodeClassDescription;

    NodeDescription(Builder builder) {
        super(builder);
        if ((nodeClassDescription = builder.getNodeClassDescription()) == null) {
            throw new IllegalArgumentException("No node class description given");
        }
        final String xmlTypeName = builder.getXmlTypeName();
        this.xmlTypeName = xmlTypeName == null ? getXmlName() + "Type" : xmlTypeName;
    }

    public String getXmlTypeName() {
        return xmlTypeName;
    }

    public NodeClassDescription getNodeClassDescription() {
        return nodeClassDescription;
    }

    public abstract static class Builder extends AbstractNamedDescription.Builder {
        private String xmlTypeName;
        private NodeClassDescription nodeClassDescription;

        Builder() {
        }

        public String getXmlTypeName() {
            return xmlTypeName;
        }

        public void setXmlTypeName(final String xmlTypeName) {
            this.xmlTypeName = xmlTypeName;
        }

        public NodeClassDescription getNodeClassDescription() {
            return nodeClassDescription;
        }

        public void setNodeClassDescription(final NodeClassDescription nodeClassDescription) {
            this.nodeClassDescription = nodeClassDescription;
        }

        public abstract NodeDescription build();
    }
}
