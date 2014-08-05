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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class ClassLoaderIndex {
    private final ReflectionIndex reflectionIndex;
    private final ClassLoader classLoader;

    private final ConcurrentMap<String, ClassIndex<?>> classes = new ConcurrentHashMap<>();

    ClassLoaderIndex(final ReflectionIndex reflectionIndex, final ClassLoader classLoader) {
        this.reflectionIndex = reflectionIndex;
        this.classLoader = classLoader;
    }

    <T> ClassIndex<T> getIndex(Class<T> clazz) {
        final ClassLoader loader = clazz.getClassLoader();
        if (classLoader != loader) {
            throw new IllegalArgumentException("Wrong class loader");
        }
        final String name = clazz.getName();
        ClassIndex<T> index = classes.get(name).checked(clazz);
        if (index == null) {
            ClassIndex<?> appearing = classes.putIfAbsent(name, index = createIndex(clazz));
            if (appearing != null) {
                return appearing.checked(clazz);
            }
        }
        return index;
    }

    private <T> ClassIndex<T> createIndex(final Class<T> clazz) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            return AccessController.doPrivileged(new PrivilegedAction<ClassIndex<T>>() {
                public ClassIndex<T> run() {
                    return createIndexPrivileged(clazz);
                }
            });
        }
        return createIndexPrivileged(clazz);
    }

    <T> ClassIndex<T> getIndexPrivileged(Class<T> clazz) {
        final ClassLoader loader = clazz.getClassLoader();
        if (classLoader != loader) {
            throw new IllegalArgumentException("Wrong class loader");
        }
        final String name = clazz.getName();
        ClassIndex<T> index = classes.get(name).checked(clazz);
        if (index == null) {
            ClassIndex<?> appearing = classes.putIfAbsent(name, index = createIndexPrivileged(clazz));
            if (appearing != null) {
                return appearing.checked(clazz);
            }
        }
        return index;
    }

    private <T> ClassIndex<T> createIndexPrivileged(final Class<T> clazz) {
        final Class<? super T> superclass = clazz.getSuperclass();
        final ClassIndex<? super T> superIndex = superclass == null ? null : reflectionIndex.getIndexPrivileged(superclass);
        return new ClassIndex<>(ClassLoaderIndex.this, clazz, superIndex);
    }

    void dropClass(final Class<?> clazz) {
        classes.remove(clazz.getName());
    }
}
