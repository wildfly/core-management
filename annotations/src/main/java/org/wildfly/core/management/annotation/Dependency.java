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
import org.wildfly.core.management.RunLevel;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Declare that this resource depends on a specific {@link Provides} from a specific {@link ResourceType}.  A
 * resource with this annotation will not be added without a satisfactory provider unless {@code optional} is {@code true}.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Retention(CLASS)
@Target(METHOD)
public @interface Dependency {

    /**
     * The resource type required.
     *
     * @return the resource type
     */
    Class<?> type();

    /**
     * The names required.
     *
     * @return the names
     */
    String[] names();

    /**
     * Dependency is optional ({@code true}) or required ({@code false}).
     *
     * @return {@code true} for optional, {@code false} for required
     */
    boolean optional() default false;

    /**
     * The maximum run level that must be used to modify this dependency.
     *
     * @return the run level
     */
    RunLevel runLevel() default RunLevel.STOPPED;


}
