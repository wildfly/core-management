/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
import org.wildfly.core.management.RunLevel;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * A listener on an attribute or resource.
 */
@Retention(CLASS)
@Target({TYPE, METHOD})
public @interface Listener {

    /**
     * The listener type.  Should implement one or more of the four MSC task traits.
     *
     * @return the listener type
     *
     * @see org.jboss.msc.txn.Executable
     * @see org.jboss.msc.txn.Validatable
     * @see org.jboss.msc.txn.Revertible
     * @see org.jboss.msc.txn.Committable
     */
    Class<?> value();

    /**
     * The run level at which the listener applies.  In order to transition between run levels, all
     * matching listeners must be invoked successfully.
     *
     * @return the run level
     */
    RunLevel runLevel() default RunLevel.RUNNING;

    /**
     * The resource root under which this listener applies.  This listener will
     * not execute under any other root.
     *
     * @return the resource root
     */
    Class<? extends Node> root() default Node.class;
}
