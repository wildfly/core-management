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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamWriter;

/**
 * An XML stream writer with additional useful utilities, and automatic formatting capabilities.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface XMLExtendedStreamWriter extends XMLStreamWriter {

    // overridden methods

    @Override
    void writeStartElement(String localName) throws XMLWriteException;

    @Override
    void writeStartElement(String namespaceURI, String localName) throws XMLWriteException;

    @Override
    void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLWriteException;

    @Override
    void writeEmptyElement(String namespaceURI, String localName) throws XMLWriteException;

    @Override
    void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLWriteException;

    @Override
    void writeEmptyElement(String localName) throws XMLWriteException;

    @Override
    void writeEndElement() throws XMLWriteException;

    @Override
    void writeEndDocument() throws XMLWriteException;

    @Override
    void close() throws XMLWriteException;

    @Override
    void flush() throws XMLWriteException;

    @Override
    void writeAttribute(String localName, String value) throws XMLWriteException;

    @Override
    void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLWriteException;

    @Override
    void writeAttribute(String namespaceURI, String localName, String value) throws XMLWriteException;

    @Override
    void writeNamespace(String prefix, String namespaceURI) throws XMLWriteException;

    @Override
    void writeDefaultNamespace(String namespaceURI) throws XMLWriteException;

    @Override
    void writeComment(String data) throws XMLWriteException;

    @Override
    void writeProcessingInstruction(String target) throws XMLWriteException;

    @Override
    void writeProcessingInstruction(String target, String data) throws XMLWriteException;

    @Override
    void writeCData(String data) throws XMLWriteException;

    @Override
    void writeDTD(String dtd) throws XMLWriteException;

    @Override
    void writeEntityRef(String name) throws XMLWriteException;

    @Override
    void writeStartDocument() throws XMLWriteException;

    @Override
    void writeStartDocument(String version) throws XMLWriteException;

    @Override
    void writeStartDocument(String encoding, String version) throws XMLWriteException;

    @Override
    void writeCharacters(String text) throws XMLWriteException;

    @Override
    void writeCharacters(char[] text, int start, int len) throws XMLWriteException;

    @Override
    String getPrefix(String uri) throws XMLWriteException;

    @Override
    void setPrefix(String prefix, String uri) throws XMLWriteException;

    @Override
    void setDefaultNamespace(String uri) throws XMLWriteException;

    @Override
    void setNamespaceContext(NamespaceContext context) throws XMLWriteException;

    // handy utilities

    /**
     * Write an attribute whose value is a typical XSD list type.
     *
     * @param localName the attribute local name
     * @param values the attribute values
     * @throws XMLWriteException if a write error has occurred
     */
    void writeAttribute(String localName, String[] values) throws XMLWriteException;

    /**
     * Write an attribute whose value is a typical XSD list type.
     *
     * @param namespaceURI the attribute namespace URI
     * @param localName the attribute local name
     * @param values the attribute values
     * @throws XMLWriteException if a write error has occurred
     */
    void writeAttribute(String namespaceURI, String localName, String[] values) throws XMLWriteException;

    /**
     * Write an attribute whose value is a typical XSD list type.
     *
     * @param prefix the attribute prefix
     * @param namespaceURI the attribute namespace URI
     * @param localName the attribute local name
     * @param values the attribute values
     * @throws XMLWriteException if a write error has occurred
     */
    void writeAttribute(String prefix, String namespaceURI, String localName, String[] values) throws XMLWriteException;

    /**
     * Write an attribute whose value is a typical XSD list type.
     *
     * @param localName the attribute local name
     * @param values the attribute values
     * @throws XMLWriteException if a write error has occurred
     */
    void writeAttribute(String localName, Iterable<String> values) throws XMLWriteException;

    /**
     * Write an attribute whose value is a typical XSD list type.
     *
     * @param namespaceURI the attribute namespace URI
     * @param localName the attribute local name
     * @param values the attribute values
     * @throws XMLWriteException if a write error has occurred
     */
    void writeAttribute(String namespaceURI, String localName, Iterable<String> values) throws XMLWriteException;

    /**
     * Write an attribute whose value is a typical XSD list type.
     *
     * @param prefix the attribute prefix
     * @param namespaceURI the attribute namespace URI
     * @param localName the attribute local name
     * @param values the attribute values
     * @throws XMLWriteException if a write error has occurred
     */
    void writeAttribute(String prefix, String namespaceURI, String localName, Iterable<String> values) throws XMLWriteException;

    // namespace utilities

    /**
     * Get a subsidiary stream writer which has a different default namespace for unqualified elements.  The parent
     * writer cannot be used until all of the child writer's started element(s) (if any) are ended.  The child writer
     * cannot be used after the parent writer is used again to write data.  Child writer(s) must not be closed.  As a
     * special case, if the new URI is the same as the parent's unqualified namespace URI, the parent writer might be
     * returned, but robust consumers should make no assumptions either way.
     *
     * @param uri the URI to use
     * @return the subsidiary writer
     * @see #writeStartElement(String)
     * @see #writeEmptyElement(String)
     */
    XMLExtendedStreamWriter setUnqualifiedNamespace(String uri);
}
