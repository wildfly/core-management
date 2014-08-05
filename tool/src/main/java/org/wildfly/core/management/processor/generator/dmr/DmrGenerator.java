/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.core.management.processor.generator.dmr;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.tools.StandardLocation;
import org.jboss.dmr.ModelNode;
import org.wildfly.core.management.processor.apt.MessagerPlus;
import org.wildfly.core.management.processor.apt.ModelGenerator;
import org.wildfly.core.management.processor.model.SchemaDescription;
import org.wildfly.core.management.processor.model.SystemDescription;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class DmrGenerator implements ModelGenerator {

    public void generate(final ProcessingEnvironment env, final RoundEnvironment roundEnv, final MessagerPlus msg, final SystemDescription systemDescription) {
        msg.info("Generating model description documents (DMR)...");
        for (Map.Entry<String, SchemaDescription> entry : systemDescription.getSchemasByName().entrySet()) {
            final ModelNode schemaNode = new ModelNode();
            // XXX actually generate the content here...


            if (! roundEnv.errorRaised()) {
                try {
                    try (OutputStream stream = env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "META-INF/" + entry.getValue().getSchemaElement().getQualifiedName().toString() + ".dmr").openOutputStream()) {
                        schemaNode.writeExternal(stream);
                    }
                } catch (IOException e) {
                    msg.errorf(entry.getValue().getSchemaElement(), "Failed to write model description document: %s", e);
                }
            }
        }
    }
}
