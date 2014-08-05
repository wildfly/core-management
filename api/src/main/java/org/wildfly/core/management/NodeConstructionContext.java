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
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface NodeConstructionContext {

    /**
     * Perform initial node registration, if any.  This may wire an immutable node instance into
     * the mutable database, or it may simply return the original instance.
     *
     * @param original the immutable node
     * @param <N> the node type
     * @return the node to install in the immutable instance
     */
    <N extends Node> N registerNode(N original);

    /**
     * The identity node construction context.
     */
    NodeConstructionContext IDENTITY = new NodeConstructionContext() {
        public <N extends Node> N registerNode(final N original) {
            return original;
        }
    };
}
