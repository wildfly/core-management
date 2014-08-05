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
 * An unresolved node.
 *
 * @param <N> the resolved node type
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface Unresolved<N extends Node> {

    /**
     * Get the resolution of this node.  If the unresolved node does not have a resolution context
     * available, or if the node is part of a container in {@link RunLevel#STOPPED} state, then an exception is thrown.
     *
     * @return the resolved node
     * @throws IllegalStateException if there is no resolution context or the container is stopped
     * @throws IllegalArgumentException if the container is stopped and the object's resolution context cannot resolve this object
     */
    N getResolved() throws IllegalStateException, IllegalArgumentException;

    /**
     * Get the resolution of this node as evaluated by the given resolution context.  The returned object will not remain
     * up to date with respect to the original object (in part because updates might not be valid with respect to the
     * given resolution context).
     *
     * @param resolutionContext the resolution context
     * @return the resolved node
     * @throws IllegalArgumentException if the current unresolved instance is not resolvable with the given context
     */
    N getResolved(ExpressionResolutionContext resolutionContext) throws IllegalArgumentException;
}
