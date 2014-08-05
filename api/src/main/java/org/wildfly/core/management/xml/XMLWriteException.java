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

import javax.xml.stream.XMLStreamException;

/**
 * An XML write exception with a human-readable location.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class XMLWriteException extends XMLStreamException {

    private static final long serialVersionUID = 5595236876114680273L;

    /**
     * Constructs a new {@code XMLWriteException} instance.  The message is left blank ({@code null}), and no cause is
     * specified.
     */
    public XMLWriteException() {
        this(XMLLocation.UNKNOWN);
    }

    /**
     * Constructs a new {@code XMLWriteException} instance with an initial message.  No cause is specified.
     *
     * @param msg the message
     */
    public XMLWriteException(final String msg) {
        this(msg, XMLLocation.UNKNOWN);
    }

    /**
     * Constructs a new {@code XMLWriteException} instance with an initial cause.  If a non-{@code null} cause is
     * specified, its message is used to initialize the message of this {@code XMLWriteException}; otherwise the message
     * is left blank ({@code null}).
     *
     * @param cause the cause
     */
    public XMLWriteException(final Throwable cause) {
        this(XMLLocation.UNKNOWN, cause);
    }

    /**
     * Constructs a new {@code XMLWriteException} instance with an initial message and cause.
     *
     * @param msg the message
     * @param cause the cause
     */
    public XMLWriteException(final String msg, final Throwable cause) {
        this(msg, XMLLocation.UNKNOWN, cause);
    }

    /**
     * Constructs a new {@code XMLWriteException} instance.  The message is left blank ({@code null}), and no cause is
     * specified.
     *
     * @param location the exception location
     */
    public XMLWriteException(final XMLLocation location) {
        this(location, null);
    }

    /**
     * Constructs a new {@code XMLWriteException} instance with an initial message.  No cause is specified.
     *
     * @param msg the detail message
     * @param location the exception location
     */
    public XMLWriteException(final String msg, final XMLLocation location) {
        this(msg, location, null);
    }

    /**
     * Constructs a new {@code XMLWriteException} instance with an initial cause.  If a non-{@code null} cause is
     * specified, its message is used to initialize the message of this {@code XMLWriteException}; otherwise the message
     * is left blank ({@code null}).
     *
     * @param location the exception location
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public XMLWriteException(final XMLLocation location, final Throwable cause) {
        this("Write error", location, cause);
    }

    /**
     * Constructs a new {@code XMLWriteException} instance with an initial message and cause.
     *
     * @param msg the detail message
     * @param location the exception location
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public XMLWriteException(final String msg, final XMLLocation location, final Throwable cause) {
        super(location.toString() + msg, cause);
        this.location = location;
    }

    /**
     * Constructs a {@code XMLWriteException} using information gleaned from the given stream exception.
     *
     * @param streamException the exception to acquire information from
     * @param fileName the file name to use in the location, if any
     */
    public XMLWriteException(final XMLStreamException streamException, final String fileName) {
        this(clean(streamException.getMessage()), XMLLocation.toXMLLocation(fileName, streamException.getLocation()), streamException.getCause());
    }

    private static String clean(String original) {
        if (original.startsWith("ParseError at [row,col]:[")) {
            final int idx = original.indexOf("Message: ");
            return idx == -1 ? original : original.substring(idx + 9);
        } else {
            return original;
        }
    }

    /**
     * Convert the given stream exception to a write exception with no associated file name.  If the original
     * exception already is an instance of {@code XMLWriteException} then it is simply returned.
     *
     * @param streamException the stream exception to convert
     * @return the parse exception
     */
    public static XMLWriteException toWriteException(XMLStreamException streamException) {
        if (streamException instanceof XMLWriteException) {
            return (XMLWriteException) streamException;
        } else {
            return new XMLWriteException(streamException, null);
        }
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}
