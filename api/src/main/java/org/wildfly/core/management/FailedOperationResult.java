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
 * An operation result representing a failure.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class FailedOperationResult implements OperationResult {
    private final OperationResult parent;
    private final PathAddress address;
    private final String name;
    private final String description;

    public FailedOperationResult(final OperationResult parent, final PathAddress address, final String name, final String description) {
        this.parent = parent;
        this.address = address;
        this.name = name;
        this.description = description;
    }

    public OperationResult getParent() {
        return parent;
    }

    public PathAddress getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public OperationStatus getStatus() {
        return OperationStatus.FAILURE;
    }
}
