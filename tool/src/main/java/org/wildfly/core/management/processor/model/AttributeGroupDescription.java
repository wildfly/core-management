/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
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

package org.wildfly.core.management.processor.model;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class AttributeGroupDescription extends AbstractNamedMemberNodeDescription {
    private final boolean required;
    private final boolean wrapperElement;
    private final boolean prefixAddress;

    AttributeGroupDescription(final Builder builder) {
        super(builder);
        required = builder.isRequired();
        wrapperElement = builder.isWrapperElement();
        prefixAddress = builder.isPrefixAddress();
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isWrapperElement() {
        return wrapperElement;
    }

    public boolean isPrefixAddress() {
        return prefixAddress;
    }

    public static class Builder extends AbstractNamedMemberNodeDescription.Builder {

        private boolean required = true;
        private boolean wrapperElement = false;
        private boolean prefixAddress = true;

        Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(final boolean required) {
            this.required = required;
        }

        public boolean isWrapperElement() {
            return wrapperElement;
        }

        public void setWrapperElement(final boolean wrapperElement) {
            this.wrapperElement = wrapperElement;
        }

        public boolean isPrefixAddress() {
            return prefixAddress;
        }

        public void setPrefixAddress(final boolean prefixAddress) {
            this.prefixAddress = prefixAddress;
        }

        public AttributeGroupDescription build() {
            return new AttributeGroupDescription(this);
        }
    }
}
