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

import static java.lang.Integer.signum;
import static java.lang.Math.min;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A reflection index for a particular class, which includes only members of this class.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class ClassIndex<T> {

    @SuppressWarnings("rawtypes")
    private static final Comparator<Constructor> CONSTRUCTOR_COMPARATOR = new Comparator<Constructor>() {
        public int compare(final Constructor o1, final Constructor o2) {
            return compareParameters(o1.getParameterTypes(), o2.getParameterTypes());
        }
    };
    private static final Comparator<Method> METHOD_COMPARATOR = new Comparator<Method>() {
        public int compare(final Method o1, final Method o2) {
            int res;
            res = o1.getName().compareTo(o2.getName());
            if (res == 0) res = compareParameters(o1.getParameterTypes(), o2.getParameterTypes());
            if (res == 0) res = o1.getReturnType().getName().compareTo(o2.getReturnType().getName());
            return res;
        }
    };
    private static final Comparator<Field> FIELD_COMPARATOR = new Comparator<Field>() {
        public int compare(final Field o1, final Field o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    /**
     * The actual class being indexed.
     */
    private final Class<T> indexedClass;
    /**
     * The index of the class loader this class is defined in.
     */
    private final ClassLoaderIndex classLoaderIndex;
    /**
     * A shortcut reference to the super class index.
     */
    private final ClassIndex<? super T> superClassIndex;
    /**
     * The fields on this class, sorted by name.
     */
    private final Field[] fields;
    /**
     * The constructors on this class, sorted by parameter type name.
     */
    private final Constructor<T>[] constructors;
    /**
     * The methods on this class, sorted by name, then parameter type names, then return type.
     */
    private final Method[] methods;
    /**
     * A list view of the method array.
     */
    private List<Method> methodList;

    ClassIndex(final ClassLoaderIndex classLoaderIndex, final Class<T> indexedClass, final ClassIndex<? super T> superClassIndex) {
        this.classLoaderIndex = classLoaderIndex;
        this.indexedClass = indexedClass;
        this.superClassIndex = superClassIndex;

        // -- fields --
        final Field[] fields = indexedClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        Arrays.sort(fields, FIELD_COMPARATOR);
        this.fields = fields;

        // -- methods --
        final Method[] methods = indexedClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
        }
        Arrays.sort(methods, METHOD_COMPARATOR);
        this.methods = methods;
        methodList = Arrays.asList(this.methods);

        // -- constructors --
        @SuppressWarnings("unchecked")
        final Constructor<T>[] constructors = (Constructor<T>[]) indexedClass.getDeclaredConstructors();
        for (Constructor<T> constructor : constructors) {
            constructor.setAccessible(true);
        }
        Arrays.sort(constructors, CONSTRUCTOR_COMPARATOR);
        this.constructors = constructors;
    }

    private static int compareParameters(final Class<?>[] left, final Class<?>[] right) {
        final int ll = left.length;
        final int rl = right.length;
        final int ml = min(ll, rl);
        int res;
        for (int i = 0; i < ml; i ++) {
            res = left[i].getName().compareTo(right[i].getName());
            if (res != 0) {
                return res;
            }
        }
        return signum(ll - rl);
    }

    private static int compareParameters(final Class<?>[] left, final String[] right) {
        final int ll = left.length;
        final int rl = right.length;
        final int ml = min(ll, rl);
        int res;
        for (int i = 0; i < ml; i ++) {
            res = left[i].getName().compareTo(right[i]);
            if (res != 0) {
                return res;
            }
        }
        return signum(ll - rl);
    }

    /**
     * Get the class loader index.
     *
     * @return the class loader index
     */
    ClassLoaderIndex getClassLoaderIndex() {
        return classLoaderIndex;
    }

    /**
     * Get the index of the superclass of this indexed type.
     *
     * @return the index of the superclass of this indexed type
     */
    public ClassIndex<? super T> getSuperClassIndex() {
        return superClassIndex;
    }

    /**
     * Get the class indexed by this object.
     *
     * @return the class
     */
    public Class<T> getIndexedClass() {
        return indexedClass;
    }

    /**
     * Get a field declared on this object.
     *
     * @param name the field name
     * @return the field, or {@code null} if no field of that name exists
     */
    public Field getField(String name) {
        Field field;
        int idx, res;
        int min = 0;
        int max = fields.length - 1;

        while (min <= max) {
            idx = (min + max) >>> 1;
            field = fields[idx];
            res = field.getName().compareTo(name);

            if (res < 0) {
                min = idx + 1;
            } else if (res > 0) {
                max = idx - 1;
            } else {
                return field;
            }
        }
        return null;
    }

    /**
     * Get a collection of fields declared on this object.
     *
     * @return The (possibly empty) collection of all declared fields on this object
     */
    public Collection<Field> getFields() {
        return Collections.unmodifiableCollection(Arrays.asList(fields));
    }

    /**
     * Get a method declared on this object.
     *
     * @param returnType the method return type
     * @param name       the name of the method
     * @param paramTypes the parameter types of the method
     * @return the method, or {@code null} if no method of that description exists
     */
    public Method getMethod(Class<?> returnType, String name, Class<?>... paramTypes) {
        Method method;

        int idx, res;
        int min = 0;
        int max = methods.length - 1;

        while (min <= max) {
            idx = (min + max) >>> 1;
            method = methods[idx];
            // first name
            res = method.getName().compareTo(name);
            // then param types
            if (res == 0) res = compareParameters(method.getParameterTypes(), paramTypes);
            // then return type
            if (res == 0) res = method.getReturnType().getName().compareTo(returnType.getName());

            if (res < 0) {
                min = idx + 1;
            } else if (res > 0) {
                max = idx - 1;
            } else {
                return method;
            }
        }
        return null;
    }

    /**
     * Get the canonical method declared on this object.
     *
     * @param method the method to look up
     * @return the canonical method object, or {@code null} if no matching method exists
     */
    public Method getMethod(Method method) {
        return getMethod(method.getReturnType(), method.getName(), method.getParameterTypes());
    }

    /**
     * Get a method declared on this object.
     *
     * @param returnType     the method return type name
     * @param name           the name of the method
     * @param paramTypeNames the parameter type names of the method
     * @return the method, or {@code null} if no method of that description exists
     */
    public Method getMethod(String returnType, String name, String... paramTypeNames) {
        Method method;

        int idx, res;
        int min = 0;
        int max = methods.length - 1;

        while (min <= max) {
            idx = (min + max) >>> 1;
            method = methods[idx];
            // first name
            res = method.getName().compareTo(name);
            // then param types
            if (res == 0) res = compareParameters(method.getParameterTypes(), paramTypeNames);
            // then return type
            if (res == 0) res = method.getReturnType().getName().compareTo(returnType);

            if (res < 0) {
                min = idx + 1;
            } else if (res > 0) {
                max = idx - 1;
            } else {
                return method;
            }
        }
        return null;
    }

    /**
     * Get a list of methods declared on this object.
     *
     * @param name       the name of the method
     * @param paramTypes the parameter types of the method
     * @return the (possibly empty) list of methods matching the description
     */
    public List<Method> getMethods(String name, Class<?>... paramTypes) {
        Method method;

        int idx, res;
        int min = 0;
        int max = methods.length - 1;

        while (min <= max) {
            idx = (min + max) >>> 1;
            method = methods[idx];
            // first name
            res = method.getName().compareTo(name);
            // then param types
            if (res == 0) res = compareParameters(method.getParameterTypes(), paramTypes);

            if (res < 0) {
                min = idx + 1;
            } else if (res > 0) {
                max = idx - 1;
            } else {
                // match is found; record its location for future reference
                final int pos = idx;
                final int start, end;

                // find last entry before matched set (could be -1)
                min = 0;
                max = pos - 1;

                while (min <= max) {
                    idx = (min + max) >>> 1;
                    method = methods[idx];
                    // first name
                    res = method.getName().compareTo(name);
                    // then param types
                    if (res == 0) res = compareParameters(method.getParameterTypes(), paramTypes);
                    assert res <= 0;
                    if (res == 0) {
                        max = idx - 1;
                    } else { // res == -1
                        min = idx + 1;
                    }
                }
                // if res is -1 then idx is at the last entry before the matches; otherwise it is (0) at the first entry that matches
                start = idx - res;

                // find first entry after matched set (could be methods.length)
                min = pos + 1;
                max = methods.length - 1;

                while (min <= max) {
                    idx = (min + max) >>> 1;
                    method = methods[idx];
                    // first name
                    res = method.getName().compareTo(name);
                    // then param types
                    if (res == 0) res = compareParameters(method.getParameterTypes(), paramTypes);
                    assert res >= 0;
                    if (res > 0) {
                        max = idx - 1;
                    } else {
                        min = idx + 1;
                    }
                }
                // if res is 1 then idx is at the first entry past the matches; otherwise it is (0) at the last entry that matches
                end = idx - res;

                return Collections.unmodifiableList(methodList.subList(start, end));
            }
        }
        // no matches
        return Collections.emptyList();
    }

    /**
     * Get a collection of methods declared on this object.
     *
     * @param name           the name of the method
     * @param paramTypeNames the parameter type names of the method
     * @return the (possibly empty) collection of methods matching the description
     */
    public Collection<Method> getMethods(String name, String... paramTypeNames) {
        Method method;

        int idx, res;
        int min = 0;
        int max = methods.length - 1;

        while (min <= max) {
            idx = (min + max) >>> 1;
            method = methods[idx];
            // first name
            res = method.getName().compareTo(name);
            // then param types
            if (res == 0) res = compareParameters(method.getParameterTypes(), paramTypeNames);

            if (res < 0) {
                min = idx + 1;
            } else if (res > 0) {
                max = idx - 1;
            } else {
                // match is found; record its location for future reference
                final int pos = idx;
                final int start, end;

                // find last entry before matched set (could be -1)
                min = 0;
                max = pos - 1;

                while (min <= max) {
                    idx = (min + max) >>> 1;
                    method = methods[idx];
                    // first name
                    res = method.getName().compareTo(name);
                    // then param types
                    if (res == 0) res = compareParameters(method.getParameterTypes(), paramTypeNames);
                    assert res <= 0;
                    if (res == 0) {
                        max = idx - 1;
                    } else { // res == -1
                        min = idx + 1;
                    }
                }
                // if res is -1 then idx is at the last entry before the matches; otherwise it is (0) at the first entry that matches
                start = idx - res;

                // find first entry after matched set (could be methods.length)
                min = pos + 1;
                max = methods.length - 1;

                while (min <= max) {
                    idx = (min + max) >>> 1;
                    method = methods[idx];
                    // first name
                    res = method.getName().compareTo(name);
                    // then param types
                    if (res == 0) res = compareParameters(method.getParameterTypes(), paramTypeNames);
                    assert res >= 0;
                    if (res > 0) {
                        max = idx - 1;
                    } else {
                        min = idx + 1;
                    }
                }
                // if res is 1 then idx is at the first entry past the matches; otherwise it is (0) at the last entry that matches
                end = idx - res;

                return Collections.unmodifiableList(methodList.subList(start, end));
            }
        }
        // no matches
        return Collections.emptyList();
    }

    /**
     * Get a collection of methods declared on this object by method name.
     *
     * @param name the name of the method
     * @return the (possibly empty) collection of methods with the given name
     */
    public Collection<Method> getAllMethods(String name) {
        Method method;

        int idx, res;
        int min = 0;
        int max = methods.length - 1;

        while (min <= max) {
            idx = (min + max) >>> 1;
            method = methods[idx];
            // first name
            res = method.getName().compareTo(name);

            if (res < 0) {
                min = idx + 1;
            } else if (res > 0) {
                max = idx - 1;
            } else {
                // match is found; record its location for future reference
                final int pos = idx;
                final int start, end;

                // find last entry before matched set (could be -1)
                min = 0;
                max = pos - 1;

                while (min <= max) {
                    idx = (min + max) >>> 1;
                    method = methods[idx];
                    // first name
                    res = method.getName().compareTo(name);
                    assert res <= 0;
                    if (res == 0) {
                        max = idx - 1;
                    } else { // res == -1
                        min = idx + 1;
                    }
                }
                // if res is -1 then idx is at the last entry before the matches; otherwise it is (0) at the first entry that matches
                start = idx - res;

                // find first entry after matched set (could be methods.length)
                min = pos + 1;
                max = methods.length - 1;

                while (min <= max) {
                    idx = (min + max) >>> 1;
                    method = methods[idx];
                    // first name
                    res = method.getName().compareTo(name);
                    assert res >= 0;
                    if (res > 0) {
                        max = idx - 1;
                    } else {
                        min = idx + 1;
                    }
                }
                // if res is 1 then idx is at the first entry past the matches; otherwise it is (0) at the last entry that matches
                end = idx - res;

                return Collections.unmodifiableList(methodList.subList(start, end));
            }
        }
        // no matches
        return Collections.emptyList();
    }

    /**
     * Get a collection of methods declared on this object by method name and parameter count.
     *
     * @param name       the name of the method
     * @param paramCount the number of parameters
     * @return the (possibly empty) collection of methods with the given name and parameter count
     */
    public Collection<Method> getAllMethods(String name, int paramCount) {
        Method method;

        int idx, res;
        int min = 0;
        int max = methods.length - 1;

        while (min <= max) {
            idx = (min + max) >>> 1;
            method = methods[idx];
            // first name
            res = method.getName().compareTo(name);

            if (res < 0) {
                min = idx + 1;
            } else if (res > 0) {
                max = idx - 1;
            } else {
                // match is found; record its location for future reference
                final int pos = idx;
                final int start, end;

                // find last entry before matched set (could be -1)
                min = 0;
                max = pos - 1;

                while (min <= max) {
                    idx = (min + max) >>> 1;
                    method = methods[idx];
                    // first name
                    res = method.getName().compareTo(name);
                    assert res <= 0;
                    if (res == 0) {
                        max = idx - 1;
                    } else { // res == -1
                        min = idx + 1;
                    }
                }
                // if res is -1 then idx is at the last entry before the matches; otherwise it is (0) at the first entry that matches
                start = idx - res;

                // find first entry after matched set (could be methods.length)
                min = pos + 1;
                max = methods.length - 1;

                while (min <= max) {
                    idx = (min + max) >>> 1;
                    method = methods[idx];
                    // first name
                    res = method.getName().compareTo(name);
                    assert res >= 0;
                    if (res > 0) {
                        max = idx - 1;
                    } else {
                        min = idx + 1;
                    }
                }
                // if res is 1 then idx is at the first entry past the matches; otherwise it is (0) at the last entry that matches
                end = idx - res;

                final ArrayList<Method> list = new ArrayList<>();
                for (int i = start; i < end; i ++) {
                    method = methods[i];
                    if (method.getParameterTypes().length == paramCount) list.add(method);
                }
                return list;
            }
        }
        // no matches
        return Collections.emptyList();
    }

    /**
     * Get a collection of methods declared on this object.
     *
     * @return the (possibly empty) collection of all declared methods
     */
    public Collection<Method> getMethods() {
        return Collections.unmodifiableList(methodList);
    }

    /**
     * Get the full collection of constructors declared on this object.
     *
     * @return the constructors
     */
    public Collection<Constructor<T>> getConstructors() {
        return Collections.unmodifiableList(Arrays.asList(constructors));
    }

    /**
     * Get a constructor declared on this class.
     *
     * @param paramTypes the constructor argument types
     * @return the constructor, or {@code null} of no such constructor exists
     */
    public Constructor<T> getConstructor(Class<?>... paramTypes) {
        Constructor<T> constructor;

        int idx, res;
        int min = 0;
        int max = methods.length - 1;

        while (min <= max) {
            idx = (min + max) >>> 1;
            constructor = constructors[idx];
            // just param types
            res = compareParameters(constructor.getParameterTypes(), paramTypes);

            if (res < 0) {
                min = idx + 1;
            } else if (res > 0) {
                max = idx - 1;
            } else {
                return constructor;
            }
        }
        return null;
    }

    /**
     * Get a constructor declared on this class.
     *
     * @param paramTypeNames the constructor argument type names
     * @return the constructor, or {@code null} of no such constructor exists
     */
    public Constructor<T> getConstructor(String... paramTypeNames) {
        Constructor<T> constructor;

        int idx, res;
        int min = 0;
        int max = methods.length - 1;

        while (min <= max) {
            idx = (min + max) >>> 1;
            constructor = constructors[idx];
            // just param types
            res = compareParameters(constructor.getParameterTypes(), paramTypeNames);

            if (res < 0) {
                min = idx + 1;
            } else if (res > 0) {
                max = idx - 1;
            } else {
                return constructor;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    <T> ClassIndex<T> checked(final Class<T> clazz) {
        if (clazz == indexedClass) {
            return (ClassIndex<T>) this;
        } else {
            throw new IllegalArgumentException("Classes do not match");
        }
    }
}
