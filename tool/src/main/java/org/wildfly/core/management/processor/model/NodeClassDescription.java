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
import java.util.List;

import javax.lang.model.element.TypeElement;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class NodeClassDescription extends AbstractNamedDescription {

    /**
     * The element to report errors against; represents the interface of the node declaration.
     */
    private final TypeElement typeElement;
    private final NodeClassDescription superClass;
    private final List<NodeMemberDescription> members;

    NodeClassDescription(Builder builder) {
        super(builder);
        if ((typeElement = builder.getTypeElement()) == null) {
            throw new IllegalArgumentException("Source element is null");
        }
        superClass = builder.getSuperClass();
        members = builder.getMembers();
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public NodeClassDescription getSuperClass() {
        return superClass;
    }

    public List<NodeMemberDescription> getMembers() {
        // unmodifiable
        return members;
    }

    public static class Builder extends AbstractNamedDescription.Builder {

        private TypeElement typeElement;
        private NodeClassDescription superClass;
        private List<NodeMemberDescription> members;

        Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public TypeElement getTypeElement() {
            return typeElement;
        }

        public void setTypeElement(final TypeElement typeElement) {
            this.typeElement = typeElement;
        }

        public NodeClassDescription getSuperClass() {
            return superClass;
        }

        public void setSuperClass(final NodeClassDescription superClass) {
            this.superClass = superClass;
        }

        public List<NodeMemberDescription> getMembers() {
            return CollectionUtil.getList(members, NodeMemberDescription.class);
        }

        public void addMember(AttributeDescription attributeDescription) {
            doAddMember(attributeDescription);
        }

        public void addMember(SubResourceDescription subResourceDescription) {
            doAddMember(subResourceDescription);
        }

        public void addMember(AttributeGroupDescription attributeGroupDescription) {
            doAddMember(attributeGroupDescription);
        }

        private void doAddMember(final NodeMemberDescription item) {
            if (item == null) {
                return;
            }
            if (members == null) {
                members = new ArrayList<>();
            }
            members.add(item);
        }

        public NodeClassDescription build() {
            return new NodeClassDescription(this);
        }
    }
}
