/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.core.management.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.wildfly.core.management.Access;
import org.wildfly.core.management.AttributeValidator;
import org.wildfly.core.management.RunLevel;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * A resource attribute.  An attribute can be:
 * <ul>
 *     <li>A simple type: any primitive or {@link String}</li>
 *     <li>A map (ordered {@link java.util.Map}, or {@link java.util.SortedMap}) of simple type to any valid attribute type</li>
 *     <li>A collection ({@link java.util.List}, ordered {@link java.util.Set}, or {@link java.util.SortedSet}) of simple type</li>
 *     <li>An array (ordered) of simple type</li>
 *     <li>An attribute group type ({@link AttributeGroup})</li>
 *     <li>A complex attribute type ({@link AttributeType})</li>
 * </ul>
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Retention(CLASS)
@Target(METHOD)
public @interface Attribute {

    /**
     * The attribute name.  If not specified, the attribute name is taken from the method name.
     *
     * @return the attribute name
     */
    String name() default "";

    /**
     * The accessibility of the value of this attribute.
     *
     * @return the accessibility
     */
    Access access() default Access.READ_WRITE;

    /**
     * The maximum run level at which this attribute can be mutated.
     *
     * @return the run level
     */
    RunLevel changeRunLevel() default RunLevel.RUNNING;

    /**
     * Set to signify that the attribute can be specified as an expression, clear to signify only literal values.
     *
     * @return {@code true} to allow expression values
     */
    boolean expr() default false;

    Class<? extends AttributeValidator>[] validators() default {};
}
