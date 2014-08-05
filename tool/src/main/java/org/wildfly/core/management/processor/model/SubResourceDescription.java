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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class SubResourceDescription extends AbstractNamedMemberResourceDescription {
    private final ResourceTypeDescription resourceType;
    private final String type;
    private final boolean requiresUnique;
    private final List<ResourceDescription> knownChildren;

    public SubResourceDescription(final Builder builder) {
        super(builder);
        if ((resourceType = builder.getResourceType()) == null) {
            // blah
        }
        if ((type = builder.getType()) == null) {
            //
        }
        requiresUnique = builder.isRequiresUnique();
        knownChildren = builder.getKnownChildren();
    }

    public static class Builder extends AbstractNamedMemberResourceDescription.Builder {
        private ResourceTypeDescription resourceType;
        private String type;
        private boolean requiresUnique;
        private List<ResourceDescription> knownChildren;

        Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public ResourceTypeDescription getResourceType() {
            return resourceType;
        }

        public void setResourceType(final ResourceTypeDescription resourceType) {
            this.resourceType = resourceType;
        }

        public String getType() {
            return type;
        }

        public void setType(final String type) {
            this.type = type;
        }

        public boolean isRequiresUnique() {
            return requiresUnique;
        }

        public void setRequiresUnique(final boolean requiresUnique) {
            this.requiresUnique = requiresUnique;
        }

        public List<ResourceDescription> getKnownChildren() {
            final List<ResourceDescription> knownChildren = this.knownChildren;
            if (knownChildren == null || knownChildren.isEmpty()) {
                return Collections.emptyList();
            } else if (knownChildren.size() == 1) {
                return Collections.singletonList(knownChildren.get(0));
            } else {
                return Collections.unmodifiableList(Arrays.asList(knownChildren.toArray(new ResourceDescription[knownChildren.size()])));
            }
        }

        public void addKnownChild(ResourceDescription description) {
            if (description == null) {
                return;
            }
            if (knownChildren == null) {
                knownChildren = new ArrayList<>();
            }
            knownChildren.add(description);
        }

        public SubResourceDescription build() {
            return new SubResourceDescription(this);
        }
    }
}
