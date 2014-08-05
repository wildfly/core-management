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

import javax.xml.stream.XMLStreamReader;
import org.jboss.dmr.ModelNode;
import org.wildfly.core.management.xml.XMLParseException;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface OperationBuilder extends NodeBuilder {

    /**
     * Populate the operation builder from the given model node.
     *
     * @param modelNode the model node from which the resource should be populated
     */
    void fillFromModelNode(ModelNode modelNode);

    /**
     * Populate the operation builder from the given XML.
     *
     * @param reader the XML source
     *
     * @throws XMLParseException if a parse error occurs
     */
    void fillFromXML(XMLStreamReader reader) throws XMLParseException;
}
