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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
class CollectionUtil {
    private CollectionUtil() {}

    static <K, V> Map<K, V> getMap(Map<K, V> orig) {
        if (orig == null || orig.size() == 0) {
            return Collections.emptyMap();
        } else if (orig.size() == 1) {
            final Map.Entry<K, V> entry = orig.entrySet().iterator().next();
            return Collections.singletonMap(entry.getKey(), entry.getValue());
        } else {
            return Collections.unmodifiableMap(new TreeMap<>(orig));
        }
    }

    static <E> List<E> getList(List<E> orig, Class<E> elemType) {
        if (orig == null || orig.isEmpty()) {
            return Collections.emptyList();
        } else if (orig.size() == 1) {
            return Collections.singletonList(orig.get(0));
        } else {
            return Collections.unmodifiableList(Arrays.asList(orig.toArray(createArray(elemType, orig.size()))));
        }
    }

    @SuppressWarnings("unchecked")
    private static <E> E[] createArray(Class<E> elemType, int length) {
        return (E[]) Array.newInstance(elemType, length);
    }
}
