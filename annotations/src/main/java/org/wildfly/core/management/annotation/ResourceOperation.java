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
import org.wildfly.core.management.Node;
import org.wildfly.core.management.ResourceOperationHandler;
import org.wildfly.core.management.RunLevel;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Define an operation on a resource and its subtypes.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Retention(CLASS)
@Target(TYPE)
public @interface ResourceOperation {
    String name() default "";

    /**
     * Specify whether this operation is global (usable from any resource address).
     *
     * @return {@code true} if the operation is global, {@code false} if it is confined to the resource it was defined on
     */
    boolean global() default false;

    Class<? extends ResourceOperationHandler<?, ?>> value();

    /**
     * The run level of this operation.  Below this run level, the operation is not
     * available.
     *
     * @return the run level
     */
    RunLevel runLevel() default RunLevel.MANAGEMENT;

    /**
     * The resource root under which this operation applies.  This operation will
     * not be available under any other root.
     *
     * @return the resource root
     */
    Class<? extends Node> root() default Node.class;
}
