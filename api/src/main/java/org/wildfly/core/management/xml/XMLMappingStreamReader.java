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

package org.wildfly.core.management.xml;

import javax.xml.stream.XMLStreamReader;
import org.wildfly.core.management.AbstractRootResourceBuilder;

/**
 * A mapping XML stream reader that can dynamically load parsers for specific namespaces, and which wraps many
 * JAXP methods with versions that use a more human-friendly exception type.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface XMLMappingStreamReader extends XMLStreamReader {
    // overridden members

    int next() throws XMLParseException;

    void require(int type, String namespaceURI, String localName) throws XMLParseException;

    String getElementText() throws XMLParseException;

    int nextTag() throws XMLParseException;

    boolean hasNext() throws XMLParseException;

    void close() throws XMLParseException;

    int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLParseException;

    XMLLocation getLocation();


    // useful utilities

    void consumeContent() throws IllegalStateException, XMLParseException;




    // xs:any helpers

    /**
     * Parse a single element of the given type.  The populated builder is returned.
     *
     * @param baseType the root resource class
     * @param <R> the type of the root resource node
     * @return a root resource builder which is populated with the XML content
     * @throws XMLParseException if a parsing error occurs
     */
    <R extends RootResourceNode> AbstractRootResourceBuilder<? extends R> parseAny(Class<R> baseType) throws XMLParseException;


}
