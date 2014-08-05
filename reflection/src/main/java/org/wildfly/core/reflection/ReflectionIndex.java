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

package org.wildfly.core.reflection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A reflection index, which caches accessible members of classes, organized by class loader.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class ReflectionIndex {
    private static final RuntimePermission GET_REFLECTION_INDEX = new RuntimePermission("getReflectionIndex");

    private final ConcurrentMap<ClassLoader, ClassLoaderIndex> indexes = new ConcurrentHashMap<>();

    private ReflectionIndex() {}

    /**
     * Get the class index for the given class.
     *
     * @param clazz the class
     * @param <T> the class type
     * @return the class index
     */
    public <T> ClassIndex<T> getIndex(final Class<T> clazz) {
        return getClassLoaderIndexForClass(clazz).getIndex(clazz);
    }

    /**
     * Drop a class loader from the index so that it can be collected.  Note that if other class loaders reference
     * this class loader, the collection will not occur until those references are cleared.  If a new index is created
     * for the class loader, it is possible that there will be a mix of old and new references for it.
     *
     * @param classLoader the class loader
     */
    public void dropClassLoader(final ClassLoader classLoader) {
        indexes.remove(classLoader);
    }

    /**
     * Drop a class from the index to allow redefinitions by a special-purpose Java agent to be used.
     *
     * @param clazz the class to drop
     */
    public void dropClass(final Class<?> clazz) {
        // todo privileged
        final ClassLoader classLoader = clazz.getClassLoader();
        final ClassLoaderIndex classLoaderIndex = indexes.get(classLoader);
        if (classLoaderIndex != null) {
            classLoaderIndex.dropClass(clazz);
        }
    }

    /**
     * Create a new reflection index instance.  Whenever possible, instances should be shared for efficiency.
     *
     * @return the new reflection index
     * @throws SecurityException if there is a security manager present and the caller does not have
     *      the {@code getReflectionIndex} {@link RuntimePermission}
     */
    public static ReflectionIndex create() throws SecurityException {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(GET_REFLECTION_INDEX);
        }
        return new ReflectionIndex();
    }

    <T> ClassIndex<T> getIndexPrivileged(final Class<T> clazz) {
        return getClassLoaderIndexForClass(clazz).getIndexPrivileged(clazz);
    }

    private <T> ClassLoaderIndex getClassLoaderIndexForClass(final Class<T> clazz) {
        final ClassLoader classLoader = clazz.getClassLoader();
        ClassLoaderIndex index = indexes.get(classLoader);
        if (index == null) {
            ClassLoaderIndex appearing = indexes.putIfAbsent(classLoader, index = new ClassLoaderIndex(this, classLoader));
            if (appearing != null) index = appearing;
        }
        return index;
    }
}
