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
 * Internal class for supporting forward-compatibility of generated node classes.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class NodeConfiguration<N> {

    private final ManagedSystem database;
    private final AbstractMutableNode<?> parent;
    private final Class<N> nodeType;

    NodeConfiguration(final ManagedSystem database, final AbstractMutableNode<?> parent, final Class<N> nodeType) {
        this.database = database;
        this.parent = parent;
        this.nodeType = nodeType;
    }

    ManagedSystem getDatabase() {
        return database;
    }

    AbstractMutableNode<?> getParent() {
        return parent;
    }

    Class<N> getNodeType() {
        return nodeType;
    }
}
