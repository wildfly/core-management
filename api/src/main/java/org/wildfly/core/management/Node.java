/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013 Red Hat, Inc., and individual contributors
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

package org.wildfly.core.management;

import javax.xml.stream.XMLStreamWriter;
import org.jboss.dmr.ModelNode;
import org.wildfly.core.management.xml.XMLWriteException;

/**
 * Base interface type for all management model nodes (including resources, attribute groups, etc.).
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface Node {

    /**
     * Get the name of this node.
     *
     * @return the name of the node, or {@code null} if there is no name
     */
    String getName();

    /**
     * Get the parent of this node, or {@code null} if it has no parent.
     *
     * @return the parent, or {@code null}
     */
    Node getParent();

    /**
     * Get a DMR representation of this node.
     *
     * @return the DMR node
     */
    ModelNode toModelNode();

    /**
     * Navigate to a nested resource, if available.
     *
     * @param pathElement the nested resource's address element
     * @return the resource, or {@code null} if it was not found or is not available in this context
     * @throws UnsupportedOperationException if this node is not a resource node
     */
    Node navigate(PathElement pathElement) throws UnsupportedOperationException;

    /**
     * Write this node's content as XML.
     *
     * @param writer the target writer
     * @throws XMLWriteException if the write failed for some reason (e.g. an I/O error)
     */
    void toXML(XMLStreamWriter writer) throws XMLWriteException;
}
