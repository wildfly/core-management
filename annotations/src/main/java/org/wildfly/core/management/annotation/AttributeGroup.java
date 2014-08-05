/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * An attribute group.  Must be associated with a property whose type is an interface and whose methods are all
 * attributes.  These attributes are grouped for organizational or appearance purposes, but are still
 * a part of the enclosing resource.  Attribute groups may contain one or more attributes of any valid attribute
 * type.  They are rendered as DMR OBJECTs or XML container elements in addition to being represented by a nested
 * interface.
 */
@Retention(CLASS)
@Target(METHOD)
public @interface AttributeGroup {

    /**
     * The attribute group name.  Defaults to the property name of the method.
     *
     * @return the name
     */
    String name() default "";

    /**
     * Attribute group must be present.
     *
     * @return {@code true} for required, {@code false} for optional
     */
    boolean required() default true;

    /**
     * Attribute has no name in the model (though it may still have a wrapper element and XML name).
     *
     * @return {@code true} if the attribute has no name
     */
    boolean anonymous() default false;
}
