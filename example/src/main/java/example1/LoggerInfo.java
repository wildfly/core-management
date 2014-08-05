/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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

import java.util.List;
import org.wildfly.core.management.Node;
import org.wildfly.core.management.ResourceLink;
import org.wildfly.core.management.annotation.Attribute;
import org.wildfly.core.management.annotation.Enumerated;
import org.wildfly.core.management.annotation.XmlRender;

import static org.wildfly.core.management.annotation.XmlRender.As.ELEMENT;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface LoggerInfo extends Node {

    @Attribute
    List<ResourceLink<? extends HandlerResource>> getHandlers();

    @Attribute(validators = { /* FilterStringValidator.class */ })
    @XmlRender(as = ELEMENT)
    String getFilter();

    @Attribute
    @Enumerated({"OFF", "FATAL", "ERROR", "WARN", "WARNING", "NOTICE", "INFO", "CONFIG", "DEBUG", "FINE", "FINER", "FINEST", "TRACE", "ALL"})
    String getLevel();
}
