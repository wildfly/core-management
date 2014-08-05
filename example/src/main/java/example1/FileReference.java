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

package example1;

import org.wildfly.core.management.AttributeValidator;
import org.wildfly.core.management.Node;
import org.wildfly.core.management.ResourceLink;
import org.wildfly.core.management.annotation.Attribute;
import org.wildfly.core.management.annotation.AttributeType;
import org.wildfly.core.management.annotation.Reference;
import org.wildfly.core.management.annotation.Required;
import org.jboss.msc.txn.ValidateContext;

/**
* @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
*/
@AttributeType(validators = FileReference.Validator.class)
public interface FileReference {
    @Required @Attribute
    String getFileName();

    @Reference(scopeName = "core.paths", monitor = true)
    ResourceLink<PathResource> getRelativeTo();

    class Validator implements AttributeValidator<Node, FileReference> {
        public static final Validator INSTANCE = new Validator();

        public void validate(final Node node, final String attributeName, final FileReference previousValue, final FileReference newValue, final ValidateContext validatorContext) {
            final String fileName = newValue.getFileName();
            final String relativeToName = newValue.getRelativeTo().getName();
            if (fileName.startsWith("/") && ! relativeToName.isEmpty()) {
                validatorContext.addProblem("File name may not be absolute if relative-to is given");
            } else if (! fileName.startsWith("/") && relativeToName.isEmpty()) {
                validatorContext.addProblem("File name may not be relative if no relative-to is given");
            }
            validatorContext.complete();
        }
    }
}
