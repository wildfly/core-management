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

package org.wildfly.core.management.processor.apt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;

import org.kohsuke.MetaInfServices;
import org.wildfly.core.management.annotation.CompositeOperation;
import org.wildfly.core.management.annotation.Schema;

import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import org.wildfly.core.management.processor.model.SystemDescription;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@MetaInfServices(javax.annotation.processing.Processor.class)
public final class Processor implements javax.annotation.processing.Processor {

    private ProcessingEnvironment env;
    private MessagerPlus msg;
    private ArrayList<ModelGenerator> modelGenerators;
    private final SystemDescription.Builder builder = SystemDescription.Builder.create();

    public Set<String> getSupportedOptions() {
        return Collections.emptySet();
    }

    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Schema.class.getName());
    }

    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public void init(final ProcessingEnvironment processingEnv) {
        env = processingEnv;
        final ServiceLoader<ModelGenerator> sl = ServiceLoader.load(ModelGenerator.class, Processor.class.getClassLoader());
        final ArrayList<ModelGenerator> modelGenerators = new ArrayList<>();
        final Iterator<ModelGenerator> iterator = sl.iterator();
        for (;;) try {
            if (! iterator.hasNext()) break;
            final ModelGenerator modelGenerator = iterator.next();
            modelGenerators.add(modelGenerator);
        } catch (ServiceConfigurationError e) {
            msg.errorf("Failed to load a generator implementation: %s", e);
        }
        // determinism
        Collections.sort(modelGenerators, new Comparator<ModelGenerator>() {
            public int compare(final ModelGenerator o1, final ModelGenerator o2) {
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
        });
        this.modelGenerators = modelGenerators;
    }

    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        msg.info("Processing model source annotations...");
        MessagerPlus msg = this.msg;
        if (msg == null) {
            msg = this.msg = new MessagerPlus(env.getMessager());
        }
        final TypeElement schemaElement = env.getElementUtils().getTypeElement(Schema.class.getName());
        if (annotations.contains(schemaElement)) {
            SchemaProcessor schemaProcessor = new SchemaProcessor(env, roundEnv, msg, builder);

            for (TypeElement typeElement : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(Schema.class))) {
                if (typeElement.getKind() == ElementKind.ANNOTATION_TYPE) {
                    msg.infof("Processing schema for %s", typeElement);
                    schemaProcessor.processSchema(typeElement);
                } else {
                    msg.reqWarn(typeElement, "Ignoring @Schema annotation on wrong element type");
                }
            }
            for (TypeElement typeElement : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(CompositeOperation.class))) {
                if (typeElement.getKind() == ElementKind.INTERFACE) {
                    msg.error(typeElement, "Composite operations are not yet supported");
                    /*ctxt.processCompositeOperation(typeElement);*/
                } else {
                    msg.reqWarn(typeElement, "Ignoring @CompositeOperation annotation on wrong element type");
                }
            }
            if (! roundEnv.errorRaised()) {
                SystemDescription systemDescription = builder.build();
                ArrayList<ModelGenerator> modelGenerators = this.modelGenerators;
                if (modelGenerators != null) for (ModelGenerator generator : modelGenerators) {
                    generator.generate(env, roundEnv, msg, systemDescription);
                }
            }
        }
        return true;
    }

    public Iterable<? extends Completion> getCompletions(final Element element, final AnnotationMirror annotation, final ExecutableElement member, final String userText) {
        return Collections.emptyList();
    }
}
