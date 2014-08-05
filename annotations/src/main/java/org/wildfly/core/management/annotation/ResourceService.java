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
import org.jboss.msc.service.ServiceMode;
import org.wildfly.core.management.RunLevel;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Declare an associated service for a resource or attribute group.  If declared on an attribute
 * group's interface, the name will automatically be qualified with the attribute group name.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Retention(CLASS)
@Target(TYPE)
public @interface ResourceService {

    /**
     * The service name within this resource's associated container.
     *
     * @return the service name
     */
    String name();

    /**
     * The minimum run level at which this service may be installed.  Must not be {@link RunLevel#STOPPED}.
     *
     * @return the run level
     */
    RunLevel installed() default RunLevel.RUNNING;

    /**
     * The service mode to apply to installed, but not active, resource services.
     *
     * @return the install mode
     */
    ServiceMode installMode() default ServiceMode.ON_DEMAND;

    /**
     * The minimum run level at which this service may be active.  If given, must be greater than or equal to
     * the {@link #installed()} run level.  By default, all installed services are immediately active.
     *
     * @return the run level
     */
    RunLevel active() default RunLevel.RUNNING;

    /**
     * The service mode to apply to active resource services.
     *
     * @return the active mode
     */
    ServiceMode activeMode() default ServiceMode.ACTIVE;

    /**
     * The service type to instantiate.
     *
     * @return the service type
     */
    Class<?> type();
}
