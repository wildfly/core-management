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

package org.wildfly.core.management.processor.generator.core;

import static org.jboss.jdeparser.JTypes._;

import java.io.IOException;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import org.jboss.jdeparser.FormatPreferences;
import org.jboss.jdeparser.JClassDef;
import org.jboss.jdeparser.JClassFile;
import org.jboss.jdeparser.JDeparser;
import org.jboss.jdeparser.JFiler;
import org.jboss.jdeparser.JMod;
import org.jboss.jdeparser.JSources;
import org.jboss.jdeparser.JTypes;
import org.kohsuke.MetaInfServices;
import org.wildfly.core.management.Unresolved;
import org.wildfly.core.management.processor.apt.MessagerPlus;
import org.wildfly.core.management.processor.apt.ModelGenerator;
import org.wildfly.core.management.processor.model.AttributeDescription;
import org.wildfly.core.management.processor.model.NodeClassDescription;
import org.wildfly.core.management.processor.model.NodeMemberDescription;
import org.wildfly.core.management.processor.model.SystemDescription;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@MetaInfServices(ModelGenerator.class)
public final class CoreGenerator implements ModelGenerator {

    public void generate(final ProcessingEnvironment env, final RoundEnvironment roundEnv, final MessagerPlus msg, final SystemDescription systemDescription) {
        msg.info("Generating model source files...");
        final JSources sources = JDeparser.createSources(JFiler.newInstance(env.getFiler()), new FormatPreferences());

        // generate all node classes
        for (Map.Entry<String, NodeClassDescription> entry : systemDescription.getNodeClassesByName().entrySet()) {
            final NodeClassDescription nodeClassDescription = entry.getValue();
            final String nodeClassName = entry.getKey();

            // unresolved interface
            generateUnresolvedInterface(sources, nodeClassDescription, nodeClassName);

            // main builder interface

            // resolved class

            // resolved class proxy

            // unresolved class

            // unresolved class proxy
        }

        if (! roundEnv.errorRaised()) try {
            sources.writeSources();
        } catch (IOException e) {
            msg.errorf("Failed to write sources: %s", e);
        }
    }

    private void generateUnresolvedInterface(final JSources sources, final NodeClassDescription nodeClassDescription, final String nodeClassName) {
        final String unresolvedName = nodeClassName + "UnresolvedResource";
        final JClassFile unresolvedFile = sources.createSourceFile(unresolvedName, unresolvedName + ".java");
        final JClassDef unresolvedClass = unresolvedFile._class(JMod.PUBLIC, unresolvedName);

        unresolvedClass._implements(_(Unresolved.class).typeArg(nodeClassName));

        for (NodeMemberDescription memberDescription : nodeClassDescription.getMembers()) {
            final String methodName = memberDescription.getExecutableElement().getSimpleName().toString();
            if (memberDescription instanceof AttributeDescription) {
                final AttributeDescription attributeDescription = (AttributeDescription) memberDescription;
                if (true /* supports expressions */) {
                    unresolvedClass.method(0, String.class, attributeDescription.getJavaName());
                } else {
                    unresolvedClass.method(0, JTypes.typeOf(memberDescription.getExecutableElement().getReturnType()), methodName);
                }
            } else {
                unresolvedClass.method(0, JTypes.typeOf(memberDescription.getExecutableElement().getReturnType()), methodName);
            }
        }
    }
}
