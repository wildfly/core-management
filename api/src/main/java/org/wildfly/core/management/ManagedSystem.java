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

import java.util.IdentityHashMap;

/**
 * A managed system.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class ManagedSystem {

    // Immutable state

    /**
     * The path element key which defines the model type of this managed system.
     */
    private final String rootPathKey;

    /**
     * The root resource of this managed system.
     */
    private final AbstractMutableResourceNode<?> rootResource;

    private final Object lock = new Object();

    // Mutable state

    private volatile State state = new State();

    ManagedSystem(final String rootPathKey, final AbstractMutableResourceNode<?> rootResource) {
        this.rootPathKey = rootPathKey;
        this.rootResource = rootResource;
    }

    public RunLevel getRunLevel() {
        return state.getRunLevel();
    }

    public OperationResult executeOperation(Operation operation) {
        return null;
    }

    public boolean changeRunLevel(RunLevel oldLevel, RunLevel newLevel) {
        throw new UnsupportedOperationException();
    }

    <N extends Node> N getNode(final AbstractMutableNode<N> base) {
        return state.getResource(base);
    }

    Object getLock() {
        return lock;
    }

    final class State {
        private final IdentityHashMap<AbstractMutableNode<?>, AbstractNode> nodeMap;
        private final RunLevel runLevel;

        State() {
            this(RunLevel.STOPPED);
        }

        State(final RunLevel runLevel) {
            this(new IdentityHashMap<AbstractMutableNode<?>, AbstractNode>(), runLevel);
        }

        State(final IdentityHashMap<AbstractMutableNode<?>, AbstractNode> nodeMap, final RunLevel runLevel) {
            this.nodeMap = nodeMap;
            this.runLevel = runLevel;
        }

        State(final RunLevel runLevel, State other) {
            this(other.nodeMap, runLevel);
        }

        State(final IdentityHashMap<AbstractMutableNode<?>, AbstractNode> nodeMap, State other) {
            this(nodeMap, other.runLevel);
        }

        <N extends Node> N getResource(AbstractMutableNode<N> mutableResource) {
            return mutableResource.cast(nodeMap.get(mutableResource));
        }

        <N extends Node> N putResource(AbstractMutableNode<N> mutableNode, N newValue) {
            return mutableNode.cast(nodeMap.put(mutableNode, (AbstractNode) mutableNode.cast(newValue)));
        }

        RunLevel getRunLevel() {
            return runLevel;
        }
    }
}
