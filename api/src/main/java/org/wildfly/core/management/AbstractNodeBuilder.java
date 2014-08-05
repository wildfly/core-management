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
 * Infrastructure class used by generated node builder implementations.
 *
 * @param <N> the node type
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public abstract class AbstractNodeBuilder<N extends Node> implements NodeBuilder {
    private final String name;
    private final NodeBuilder parent;

    /**
     * Construct a new instance.
     *
     * @param parent the parent node
     * @param name the name of the node being built
     */
    protected AbstractNodeBuilder(final NodeBuilder parent, final String name) {
        this.parent = parent;
        this.name = name;
    }

    /**
     * Construct this node and all child nodes.
     *
     * @param parentNode the node under which to nest, or {@code null} for an ultimate root
     * @return the constructed node
     */
    protected abstract AbstractMutableNode<N> construct(AbstractMutableNode<?> parentNode);

}
