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

/**
 * Base class for all mutable node classes.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public abstract class AbstractMutableNode<N extends Node> implements Node {

    private final ManagedSystem database;
    private final AbstractMutableNode<?> parent;
    private final Class<N> nodeType;

    protected AbstractMutableNode(final NodeConfiguration<N> configuration) {
        database = configuration.getDatabase();
        parent = configuration.getParent();
        nodeType = configuration.getNodeType();
    }

    public final String getName() {
        return getCurrent().getName();
    }

    public final Node getParent() {
        return parent;
    }

    protected final N getCurrent() {
        return database.getNode(this);
    }

    protected final Object writeReplace() {
        return getCurrent();
    }

    /**
     * Cast a node to the type of this resource.
     *
     * @param node the resource to cast
     * @return the node, properly cast
     * @throws ClassCastException if the node is of the wrong type
     */
    protected N cast(Node node) throws ClassCastException {
        return nodeType.cast(node);
    }
}
