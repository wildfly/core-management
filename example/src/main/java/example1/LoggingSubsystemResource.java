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

package example1;

import java.util.Map;
import org.wildfly.core.management.annotation.AttributeGroup;
import org.wildfly.core.management.annotation.RootResource;
import org.wildfly.core.management.annotation.Provides;
import org.wildfly.core.management.annotation.SubResource;
import org.wildfly.core.management.annotation.XmlName;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@CoreLogging_1_0
@Provides("logging")
@XmlName("subsystem")
@RootResource(
    type = "subsystem",
    name = "logging"
)
public interface LoggingSubsystemResource extends SubsystemResource {

    @SubResource(children = { FileHandlerResource.class }, referenceAs = "core.logging.handlers")
    Map<String, HandlerResource> getHandlers();

    @SubResource
    Map<String, LoggerResource> getLoggers();

    @AttributeGroup
    LoggerInfo getRootLogger();
}
