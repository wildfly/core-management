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

package org.wildfly.core.management._private;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@MessageBundle(projectCode = "WFCM")
public interface CoreManagementMessages {

    CoreManagementMessages MESSAGES = Messages.getBundle(CoreManagementMessages.class);

    // Address messages

    @Message(id = 1, value = "Invalid resource address element '%s'. The key '%s' is not valid for an element in a resource address.")
    String invalidPathElementKey(String element, String key);

    @Message(id = 2, value = "Invalid resource address element '%s'. The value '%s' is not valid for an element in a resource address. Character '%s' is not allowed.")
    String invalidPathElementValue(String element, String value, char character);
}
