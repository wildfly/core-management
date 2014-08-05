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
 * A model extension.  Implementations of this interface are typically generated.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface Extension {
    /**
     * Get the extension name.
     *
     * @return the extension name
     */
    String getName();

    /**
     * Get the extension version.
     *
     * @return the extension version
     */
    String getVersion();

    /**
     * Get the root resource builder for the given root element, or {@code null} if the element name is
     * not recognized.
     *
     * @param rootElementName the root element name
     * @return the root resource builder
     */
    RootResourceBuilder getRootResourceBuilder(String rootElementName);
}
