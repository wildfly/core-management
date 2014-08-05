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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;

/**
 * A path address for an operation.
 *
 * @author Brian Stansberry
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class PathAddress implements Iterable<PathElement> {

    /**
     * An empty address.
     */
    public static final PathAddress EMPTY_ADDRESS = new PathAddress(Collections.<PathElement>emptyList());

    /**
     * Creates a PathAddress from the given ModelNode address.  The given node is expected
     * to be an address node.
     *
     * @param node the node (cannot be {@code null})
     *
     * @return the update identifier
     */
    public static PathAddress pathAddress(final ModelNode node) {
        if (node.isDefined()) {
            final List<Property> props = node.asPropertyList();
            if (props.size() == 0) {
                return EMPTY_ADDRESS;
            } else {
                final ArrayList<PathElement> values = new ArrayList<>();
                for (final Property prop : props) {
                    values.add(new PathElement(prop.getName(), prop.getValue().asString()));
                }
                return new PathAddress(Collections.unmodifiableList(values));
            }
        } else {
            return EMPTY_ADDRESS;
        }
    }

    public static PathAddress pathAddress(List<PathElement> elements) {
        if (elements.size() == 0) {
            return EMPTY_ADDRESS;
        }
        return new PathAddress(Collections.unmodifiableList(new ArrayList<>(elements)));
    }

    public static PathAddress pathAddress(PathElement... elements) {
        return pathAddress(Arrays.<PathElement>asList(elements));
    }

    public static PathAddress pathAddress(String key, String value) {
        return pathAddress(PathElement.pathElement(key, value));
    }

    public static PathAddress pathAddress(PathAddress parent, PathElement... elements) {
        return parent.append(elements);
    }

    private final List<PathElement> pathAddressList;

    PathAddress(final List<PathElement> pathAddressList) {
        this.pathAddressList = pathAddressList;
    }

    /**
     * Gets the element at the given index.
     *
     * @param index the index
     * @return the element
     *
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    public PathElement getElement(int index) {
        final List<PathElement> list = pathAddressList;
        return list.get(index);
    }

    /**
     * Gets the last element in the address.
     *
     * @return the element, or {@code null} if {@link #size()} is zero.
     */
    public PathElement getLastElement() {
        final List<PathElement> list = pathAddressList;
        return list.size() == 0 ? null : list.get(list.size() - 1);
    }

    /**
     * Get a portion of this address using segments starting at {@code start} (inclusive).
     *
     * @param start the start index
     * @return the partial address
     */
    public PathAddress subAddress(int start) {
        final List<PathElement> list = pathAddressList;
        return new PathAddress(list.subList(start, list.size()));
    }

    /**
     * Get a portion of this address using segments between {@code start} (inclusive) and {@code end} (exclusive).
     *
     * @param start the start index
     * @param end the end index
     * @return the partial address
     */
    public PathAddress subAddress(int start, int end) {
        return new PathAddress(pathAddressList.subList(start, end));
    }

    /**
     * Create a new path address by appending more elements to the end of this address.
     *
     * @param additionalElements the elements to append
     * @return the new path address
     */
    public PathAddress append(List<PathElement> additionalElements) {
        final ArrayList<PathElement> newList = new ArrayList<>(pathAddressList.size() + additionalElements.size());
        newList.addAll(pathAddressList);
        newList.addAll(additionalElements);
        return pathAddress(newList);
    }

    /**
     * Create a new path address by appending more elements to the end of this address.
     *
     * @param additionalElements the elements to append
     * @return the new path address
     */
    public PathAddress append(PathElement... additionalElements) {
        final ArrayList<PathElement> newList = new ArrayList<>(pathAddressList.size() + additionalElements.length);
        newList.addAll(pathAddressList);
        Collections.addAll(newList, additionalElements);
        return pathAddress(newList);
    }

    /**
     * Create a new path address by appending more elements to the end of this address.
     *
     * @param address the address to append
     * @return the new path address
     */
    public PathAddress append(PathAddress address) {
        return append(address.pathAddressList);
    }

    public PathAddress append(String key, String value) {
        return append(PathElement.pathElement(key, value));
    }

    public PathAddress append(String key) {
        return append(PathElement.pathElement(key));
    }


    /**
     * Navigate to this address in the given model node.
     *
     * @param model the model node
     * @param create {@code true} to create the last part of the node if it does not exist
     * @return the submodel
     * @throws NoSuchElementException if the model contains no such element
     */
    public ModelNode navigate(ModelNode model, boolean create) throws NoSuchElementException {
        final Iterator<PathElement> i = pathAddressList.iterator();
        while (i.hasNext()) {
            final PathElement element = i.next();
            if (create && ! i.hasNext()) {
                if(element.isMultiTarget()) {
                    throw new IllegalStateException();
                }
                model = model.require(element.getKey()).get(element.getValue());
            } else {
                model = model.require(element.getKey()).require(element.getValue());
            }
        }
        return model;
    }

    /**
     * Navigate to, and remove, this address in the given model node.
     *
     * @param model the model node
     * @return the submodel
     * @throws NoSuchElementException if the model contains no such element
     */
    public ModelNode remove(ModelNode model) throws NoSuchElementException {
        final Iterator<PathElement> i = pathAddressList.iterator();
        while (i.hasNext()) {
            final PathElement element = i.next();
            if (i.hasNext()) {
                model = model.require(element.getKey()).require(element.getValue());
            } else {
                final ModelNode parent = model.require(element.getKey());
                    model = parent.remove(element.getValue()).clone();
            }
        }
        return model;
    }

    /**
     * Convert this path address to its model node representation.
     *
     * @return the model node list of properties
     */
    public ModelNode toModelNode() {
        final ModelNode node = new ModelNode().setEmptyList();
        for (PathElement element : pathAddressList) {
            final String value;
            if(element.isMultiTarget() && ! element.isWildcard()) {
                value = '[' + element.getValue() + ']';
            } else {
                value = element.getValue();
            }
            node.add(element.getKey(), value);
        }
        return node;
    }

    /**
     * Check whether this address applies to multiple targets.
     *
     * @return <code>true</code> if the address can apply to multiple targets, <code>false</code> otherwise
     */
    public boolean isMultiTarget() {
        for(final PathElement element : pathAddressList) {
            if(element.isMultiTarget()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the size of this path, in elements.
     *
     * @return the size
     */
    public int size() {
        return pathAddressList.size();
    }

    /**
     * Iterate over the elements of this path address.
     *
     * @return the iterator
     */
    @Override
    public ListIterator<PathElement> iterator() {
        return pathAddressList.listIterator();
    }

    @Override
    public int hashCode() {
        return pathAddressList.hashCode();
    }

    /**
     * Determine whether this object is equal to another.
     *
     * @param other the other object
     * @return {@code true} if they are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof PathAddress && equals((PathAddress)other);
    }

    /**
     * Determine whether this object is equal to another.
     *
     * @param other the other object
     * @return {@code true} if they are equal, {@code false} otherwise
     */
    public boolean equals(PathAddress other) {
        return this == other || other != null && pathAddressList.equals(other.pathAddressList);
    }

    @Override
    public String toString() {
        return toModelNode().toString();
    }
}
