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

import org.wildfly.core.management.xml.XMLMappingStreamReader;
import org.wildfly.core.management.xml.XMLParseException;

/**
 * Infrastructure class used by generated root resource builder implementations.
 *
 * @param <R> the root resource type
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public abstract class AbstractRootResourceBuilder<R extends RootResourceNode> extends AbstractResourceBuilder<R> implements RootResourceBuilder {

    /**
     * Construct a new instance.
     *
     * @param parent the parent builder
     * @param name the name of this root resource, or {@code null} for none
     */
    protected AbstractRootResourceBuilder(final NodeBuilder parent, final String name) {
        super(parent, name);
    }

    /**
     * Construct this resource node and all child nodes.
     *
     * @param parentNode the node under which to nest, or {@code null} for an ultimate root
     * @return the constructed node
     */
    protected abstract AbstractMutableRootResourceNode<R> construct(AbstractMutableNode<?> parentNode);

    /**
     * Populate this builder instance from the given XML.
     *
     * @param reader the XML to read from
     * @throws XMLParseException if the XML is not properly formed or is not valid
     */
    public abstract void fillFromXML(XMLMappingStreamReader reader) throws XMLParseException;
}
