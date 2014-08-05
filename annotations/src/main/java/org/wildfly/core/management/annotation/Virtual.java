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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * A virtual attribute or resource derived from a service on this resource.  The property is only available if the
 * container run level is sufficient to start the service.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Target({ TYPE, METHOD })
@Retention(CLASS)
public @interface Virtual {

    /**
     * The service name.  If not given, the service name is inherited from the resource to attributes.  If no service
     * name is given on the resource (or the resource is not virtual), a compile error will result. The resource or
     * attribute will be present when the service's run level is met.
     *
     * @return the service name
     */
    String service() default "";

    /**
     * The property of the service.  If omitted from an attribute, the value of the service itself is used and the
     * virtual attribute must be read-only, otherwise accessor methods are expected to be present for the defined access
     * type of this attribute (else a compile error will result).  May only be applied to attributes.
     *
     * @return the property
     */
    String property() default "";
}
