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

package org.wildfly.core.management.processor.apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import org.wildfly.core.management.annotation.XmlName;
import org.wildfly.core.management.processor.NameUtils;
import org.wildfly.core.management.processor.model.AbstractNamedDescription;
import org.wildfly.core.management.processor.model.AttributeGroupDescription;
import org.wildfly.core.management.processor.model.NodeClassDescription;
import org.wildfly.core.management.processor.model.ResourceDescription;
import org.wildfly.core.management.processor.model.RootResourceDescription;
import org.wildfly.core.management.processor.model.SchemaDescription;
import org.wildfly.core.management.processor.model.SystemDescription;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
class SchemaProcessor {

    static final List<Modifier> EXPECTED_FIELD_MODS = Arrays.asList(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC);
    static final List<Modifier> EXPECTED_METHOD_MODS = Arrays.asList(Modifier.PUBLIC, Modifier.ABSTRACT);
    private final ProcessingEnvironment env;
    private final RoundEnvironment roundEnv;
    private final MessagerPlus msg;

    private final SystemDescription.Builder systemDescriptionBuilder;

    public SchemaProcessor(final ProcessingEnvironment env, final RoundEnvironment roundEnv, final MessagerPlus msg, final SystemDescription.Builder systemDescriptionBuilder) {
        this.env = env;
        this.roundEnv = roundEnv;
        this.msg = msg;
        this.systemDescriptionBuilder = systemDescriptionBuilder;
    }

    public ProcessingEnvironment getEnv() {
        return env;
    }

    public RoundEnvironment getRoundEnv() {
        return roundEnv;
    }

    public void processSchema(final TypeElement schemaAnnotatedElement) {
        AnnotationMirror retentionAnnotation = null;
        AnnotationMirror targetAnnotation = null;
        AnnotationMirror schemaAnnotation = null;
        AnnotationMirror deprecatedAnnotation = null;
        AnnotationMirror documentedAnnotation = null;
        AnnotationMirror inheritedAnnotation = null;
        for (AnnotationMirror annotationMirror : schemaAnnotatedElement.getAnnotationMirrors()) {
            final TypeElement annotationTypeElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
            final String annotationClassName = annotationTypeElement.getQualifiedName().toString();
            switch (annotationClassName) {
                case "org.wildfly.core.management.annotation.Schema": schemaAnnotation = annotationMirror; break;
                case "java.lang.annotation.Target": targetAnnotation = annotationMirror; break;
                case "java.lang.annotation.Retention": retentionAnnotation = annotationMirror; break;
                case "java.lang.annotation.Documented": documentedAnnotation = annotationMirror; break;
                case "java.lang.annotation.Inherited": inheritedAnnotation = annotationMirror; break;
                case "java.lang.Deprecated": deprecatedAnnotation = annotationMirror; break;

                default: {
                    if (annotationClassName.startsWith("org.wildfly.core.management.annotation")) {
                        msg.error(schemaAnnotatedElement, annotationMirror, "Annotation is not allowed on attribute property");
                        break;
                    }
                    // ignore
                    break;
                }
            }
        }
        if (inheritedAnnotation != null) {
            msg.error(schemaAnnotatedElement, inheritedAnnotation, "Schema annotations may not be @Inherited");
        }
        if (documentedAnnotation == null) {
            msg.reqWarn(schemaAnnotatedElement, "Schema annotations should be @Documented");
        }
        if (retentionAnnotation != null) {
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : retentionAnnotation.getElementValues().entrySet()) {
                final AnnotationValue annotationValue = entry.getValue();
                final Object value = annotationValue.getValue();
                switch (entry.getKey().getSimpleName().toString()) {
                    case "value": {
                        if (! (value instanceof VariableElement)) {
                            msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Expected an enum RetentionPolicy value for value");
                            break;
                        }
                        RetentionPolicy retentionPolicy = RetentionPolicy.valueOf(((VariableElement)value).getConstantValue().toString());
                        if (retentionPolicy != RetentionPolicy.SOURCE) {
                            msg.reqWarn(schemaAnnotatedElement, retentionAnnotation, annotationValue, "Schema member should be annotated with a retention policy of SOURCE");
                        }
                        break;
                    }
                    default: {
                        unknownAnnotationArgument(schemaAnnotatedElement, schemaAnnotation, annotationValue);
                        break;
                    }
                }
            }
        } else {
            msg.reqWarn(schemaAnnotatedElement, "Schema member should be annotated with a retention policy of SOURCE");
        }
        if (targetAnnotation != null) {
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : retentionAnnotation.getElementValues().entrySet()) {
                final AnnotationValue annotationValue = entry.getValue();
                final Object value = annotationValue.getValue();
                switch (entry.getKey().getSimpleName().toString()) {
                    case "value": {
                        if (value instanceof VariableElement) {
                            ElementType elementType;
                            elementType = ElementType.valueOf(((VariableElement) value).getConstantValue().toString());
                            if (elementType != ElementType.TYPE) {
                                msg.reqWarn(schemaAnnotatedElement, retentionAnnotation, annotationValue, "Schema member should be annotated with a single target of TYPE");
                            }
                        } else if (value instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<? extends AnnotationValue> values = (List<? extends AnnotationValue>) value;
                            if (values.size() == 1) {
                                ElementType elementType;
                                elementType = ElementType.valueOf(((VariableElement) values.get(0).getValue()).getConstantValue().toString());
                                if (elementType != ElementType.TYPE) {
                                    msg.reqWarn(schemaAnnotatedElement, retentionAnnotation, annotationValue, "Schema member should be annotated with a single target of TYPE");
                                }
                            } else {
                                msg.reqWarn(schemaAnnotatedElement, retentionAnnotation, annotationValue, "Schema member should be annotated with a single target of TYPE");
                            }
                        } else {
                            msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Expected an enum TargetType value for value");
                            break;
                        }
                        if (! (value instanceof VariableElement)) {
                            break;
                        }
                        break;
                    }
                    default: {
                        unknownAnnotationArgument(schemaAnnotatedElement, schemaAnnotation, annotationValue);
                        break;
                    }
                }
            }
        }

        if (schemaAnnotation != null) {
            if (schemaAnnotatedElement.getKind() != ElementKind.ANNOTATION_TYPE) {
                msg.error(schemaAnnotatedElement, schemaAnnotation, "Only an annotation type may be annotated as a Schema");
                return;
            }
            final SchemaDescription.Builder builder = SchemaDescription.Builder.create();
            processSchemaAnnotation(schemaAnnotatedElement, schemaAnnotation, builder);
            // now, find all elements for this one
            processAllSchemaMembers(schemaAnnotatedElement, schemaAnnotation, builder);
            if (! roundEnv.errorRaised()) {
                systemDescriptionBuilder.addSchema(builder.build());
            }
            return;
        }
        // no schema found - weird but not really a problem
        return;
    }

    private void processSchemaAnnotation(TypeElement schemaAnnotatedElement, AnnotationMirror schemaAnnotation, SchemaDescription.Builder builder) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : schemaAnnotation.getElementValues().entrySet()) {
            final AnnotationValue annotationValue = entry.getValue();
            final Object value = annotationValue.getValue();
            switch (entry.getKey().getSimpleName().toString()) {
                case "schemaLocation": {
                    if (! (value instanceof String)) {
                        msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Expected a String value for schemaLocation");
                        break;
                    }
                    final URI uri;
                    try {
                        uri = new URI(value.toString());
                    } catch (URISyntaxException e) {
                        msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Namespace schema location '%s' is not valid: %s", value, e);
                        break;
                    }
                    final String path = uri.getPath();
                    if (path == null) {
                        msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Namespace schema location '%s' does not have a path component", value);
                        break;
                    }
                    final String fileName = path.substring(path.lastIndexOf('/') + 1);
                    if (! fileName.endsWith(".xsd")) {
                        msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Namespace schema location '%s' must specify a file name ending in \".xsd\"", value);
                        break;
                    }
                    builder.setSchemaLocation(uri);
                    builder.setSchemaFileName(fileName);
                    break;
                }
                case "version": {
                    if (! (value instanceof String)) {
                        msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Expected a String value for version");
                        break;
                    }
                    builder.setVersion(value.toString());
                    break;
                }
                case "kind": {
                    if (! (value instanceof VariableElement)) {
                        msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Expected an enum Kind value for kind");
                        break;
                    }
                    builder.setVersion(((VariableElement)value).getConstantValue().toString());
                    break;
                }
                case "namespace": {
                    if (! (value instanceof String)) {
                        msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Expected a String value for namespace");
                        break;
                    }
                    builder.setNamespace(value.toString());
                    break;
                }
                case "compatibilityNamespaces": {
                    if (! (value instanceof List)) {
                        msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Expected an array of Strings value for compatibilityNamespaces");
                        break;
                    }
                    @SuppressWarnings("unchecked")
                    final List<? extends AnnotationValue> list = (List<? extends AnnotationValue>) value;
                    for (int i = 0; i < list.size(); i++) {
                        final AnnotationValue val = list.get(i);
                        final Object value1 = val.getValue();
                        if (! (value1 instanceof String)) {
                            msg.errorf(schemaAnnotatedElement, schemaAnnotation, annotationValue, "Element %d of compatibilityNamespaces list is not a String", Integer.valueOf(i));
                            continue;
                        }
                        builder.addCompatNamespace((String) value1);
                    }
                    break;
                }
                default: {
                    unknownAnnotationArgument(schemaAnnotatedElement, schemaAnnotation, annotationValue);
                    break;
                }
            }
        }
    }

    private void processAllSchemaMembers(final TypeElement schemaAnnotatedElement, final AnnotationMirror schemaAnnotation, final SchemaDescription.Builder schemaBuilder) {
        Set<? extends Element> schemaElements = roundEnv.getElementsAnnotatedWith(schemaAnnotatedElement);
        for (Element itemElement : schemaElements) {
            if (! (itemElement instanceof TypeElement) || itemElement.getKind() != ElementKind.INTERFACE) {
                msg.errorf(itemElement, "Schema member items must be interfaces");
                continue;
            }
            TypeElement itemTypeElement = (TypeElement) itemElement;

            AnnotationMirror rootResourceAnnotation = null;
            AnnotationMirror xmlNameAnnotation = null;

            for (AnnotationMirror annotationMirror : itemTypeElement.getAnnotationMirrors()) {
                final TypeElement annotationTypeElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
                final String annotationClassName = annotationTypeElement.getQualifiedName().toString();
                switch (annotationClassName) {
                    case "org.wildfly.core.management.annotation.RootResource": rootResourceAnnotation = annotationMirror; break;
                    case "org.wildfly.core.management.annotation.XmlName": xmlNameAnnotation = annotationMirror; break;
                    default: {
                        if (annotationClassName.startsWith("org.wildfly.core.management.annotation")) {
                            msg.error(schemaAnnotatedElement, annotationMirror, "Annotation is not allowed on attribute property");
                            break;
                        }
                        // ignore
                        break;
                    }
                }
            }

            if (rootResourceAnnotation != null) {
                RootResourceDescription.Builder builder = RootResourceDescription.Builder.create();
                processRootResource(schemaAnnotatedElement, rootResourceAnnotation, xmlNameAnnotation, builder);
                if (! roundEnv.errorRaised()) {
                    final RootResourceDescription rootResourceDescription = builder.build();
                    schemaBuilder.addRootResourceDescription(rootResourceDescription);
                }
            } else {
                msg.errorf(schemaAnnotatedElement, schemaAnnotation, "Element is part of schema %s but is not a valid schema member type", schemaAnnotation);

            }
        }
    }

    private void processRootResource(final TypeElement schemaAnnotatedElement, final AnnotationMirror rootResourceAnnotation, final AnnotationMirror xmlNameAnnotation, final RootResourceDescription.Builder builder) {
        processResource(schemaAnnotatedElement, xmlNameAnnotation, builder);
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : rootResourceAnnotation.getElementValues().entrySet()) {
            final AnnotationValue annotationValue = entry.getValue();
            final Object value = annotationValue.getValue();
            switch (entry.getKey().getSimpleName().toString()) {
                case "type": {
                    if (! (value instanceof String)) {
                        msg.error(schemaAnnotatedElement, rootResourceAnnotation, annotationValue, "Expected String value for type");
                        break;
                    }
                    // todo not sure...
                    break;
                }
                case "name": {
                    if (! (value instanceof String)) {
                        msg.error(schemaAnnotatedElement, rootResourceAnnotation, annotationValue, "Expected String value for name");
                        break;
                    }
                    // todo not sure...
                    break;
                }
                default: {
                    unknownAnnotationArgument(schemaAnnotatedElement, rootResourceAnnotation, annotationValue);
                    break;
                }
            }
        }
    }

    private void processResource(final TypeElement schemaAnnotatedElement, final AnnotationMirror xmlNameAnnotation, final ResourceDescription.Builder builder) {
        processXmlNameAnnotation(schemaAnnotatedElement, xmlNameAnnotation, builder);
        builder.setNodeClassDescription(getOrCreateNodeClass(schemaAnnotatedElement));
    }

    private void processNodeClass(final TypeElement nodeClassElement, final NodeClassDescription.Builder builder) {
        processNamedItem(nodeClassElement, builder);
        builder.setTypeElement(nodeClassElement);
        final DeclaredType superclassDeclaredType = (DeclaredType) nodeClassElement.getSuperclass();
        if (superclassDeclaredType != null) {
            final TypeElement superclass = (TypeElement) superclassDeclaredType.asElement();
            builder.setSuperClass(getOrCreateNodeClass(superclass));
        }
        for (Element element : nodeClassElement.getEnclosedElements()) {
            if (element instanceof VariableElement) {
                if (element.getKind() != ElementKind.FIELD) {
                    msg.errorf(element, "Expected field, found element of type %s", element.getKind());
                    continue;
                } else if (!element.getModifiers().containsAll(EXPECTED_FIELD_MODS)) {
                    msg.errorf(element, "Expected public static final field, found modifiers %s", element.getModifiers());
                    continue;
                }
                // fields are unused right now
                continue;
            } else if (element instanceof ExecutableElement) {
                if (element.getKind() != ElementKind.METHOD) {
                    msg.errorf(element, "Expected method, found element of type %s", element.getKind());
                    continue;
                } else if (! element.getModifiers().containsAll(EXPECTED_METHOD_MODS)) {
                    msg.errorf(element, "Expected public abstract method, found modifiers %s", element.getModifiers());
                    continue;
                }
                processNodeClassElement((ExecutableElement) element, builder);
            } else if (element instanceof TypeElement) {
                // unused right now
                continue;
            } else {
                msg.error(element, "Encountered unexpected/unknown element");
                continue;
            }
        }
    }

    private NodeClassDescription getOrCreateNodeClass(final TypeElement nodeClassElement) {
        NodeClassDescription description = systemDescriptionBuilder.getNodeClass(nodeClassElement);
        if (description == null) {
            final NodeClassDescription.Builder builder = NodeClassDescription.Builder.create();
            processNodeClass(nodeClassElement, builder);
            if (roundEnv.errorRaised()) {
                description = null;
            } else {
                description = builder.build();
                systemDescriptionBuilder.addNodeClass(description);
            }
        }
        return description;
    }

    private void processNodeClassElement(final ExecutableElement element, final NodeClassDescription.Builder builder) {
        final String elementName = element.getSimpleName().toString();
        final String javaName;
        if (elementName.startsWith("is")) {
            javaName = elementName.substring(2);
        } else if (elementName.startsWith("get")) {
            javaName = elementName.substring(3);
        } else {
            msg.errorf(element, "Method name \"%s\" is not valid for a management node interface property (expected getXxx or isXxx)", elementName);
            return;
        }
        // set a default singular name; it may be overridden via a name="" annotation property
        // this value won't be used except for sub-resources and collections/maps/etc.
        String singularJavaName = NameUtils.singular(javaName);

        // analyze return type
        final TypeMirror returnType = element.getReturnType();
        if (returnType instanceof PrimitiveType) {
            // simple type
        } else if (returnType instanceof DeclaredType) {
            // validate generics
            final DeclaredType declaredType = (DeclaredType) returnType;
            final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            final TypeElement typeElement = (TypeElement) declaredType.asElement();
            if (typeElement.getTypeParameters().size() != typeArguments.size()) {
                msg.error(element, "Type argument count must match declared type's type parameter count");
                return;
            }
        } else if (returnType instanceof ArrayType) {
            msg.errorf(element, "Array types are not allowed; use a List<%s> instead", ((ArrayType) returnType).getComponentType());
            return;
        } else {
            msg.errorf(element, "Unsupported management node interface property return type %s", returnType);
            return;
        }

        // examine annotations
        AnnotationMirror subResourceAnnotation = null;
        AnnotationMirror attributeGroupAnnotation = null;
        AnnotationMirror attributeAnnotation = null;
        AnnotationMirror xmlNameAnnotation = null;
        AnnotationMirror virtualAnnotation = null;
        AnnotationMirror defaultAnnotation = null;
        AnnotationMirror defaultBooleanAnnotation = null;
        AnnotationMirror defaultIntAnnotation = null;
        AnnotationMirror defaultLongAnnotation = null;
        AnnotationMirror xmlRenderAnnotation = null;
        AnnotationMirror enumeratedAnnotation = null;

        for (final AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            final TypeElement annotationTypeElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
            final String annotationTypeName = annotationTypeElement.getQualifiedName().toString();
            switch (annotationTypeName) {
                // allowed
                case "org.wildfly.core.management.annotation.SubResource": subResourceAnnotation = annotationMirror; break;
                case "org.wildfly.core.management.annotation.AttributeGroup": attributeGroupAnnotation = annotationMirror; break;
                case "org.wildfly.core.management.annotation.Attribute": attributeAnnotation = annotationMirror; break;
                case "org.wildfly.core.management.annotation.XmlName": xmlNameAnnotation = annotationMirror; break;
                case "org.wildfly.core.management.annotation.Virtual": virtualAnnotation = annotationMirror; break;
                case "org.wildfly.core.management.annotation.Default": defaultAnnotation = annotationMirror; break;
                case "org.wildfly.core.management.annotation.DefaultBoolean": defaultBooleanAnnotation = annotationMirror; break;
                case "org.wildfly.core.management.annotation.DefaultInt": defaultIntAnnotation = annotationMirror; break;
                case "org.wildfly.core.management.annotation.DefaultLong": defaultLongAnnotation = annotationMirror; break;
                case "org.wildfly.core.management.annotation.XmlRender": xmlRenderAnnotation = annotationMirror; break;
                case "org.wildfly.core.management.annotation.Enumerated": enumeratedAnnotation = annotationMirror; break;

                // forbidden
                default: {
                    if (annotationTypeName.startsWith("org.wildfly.core.management.annotation")) {
                        msg.error(element, annotationMirror, "Annotation is not allowed on attribute property");
                    }
                    break;
                }
            }
        }

        // report invalid annotation combinations

        if (subResourceAnnotation != null) {
            if (attributeGroupAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, attributeGroupAnnotation);
            }
            if (attributeAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, attributeAnnotation);
            }
            if (virtualAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, virtualAnnotation);
            }
            if (defaultAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, defaultAnnotation);
            }
            if (defaultBooleanAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, defaultBooleanAnnotation);
            }
            if (defaultIntAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, defaultIntAnnotation);
            }
            if (defaultLongAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, defaultLongAnnotation);
            }
        } else if (attributeGroupAnnotation != null) {
            if (virtualAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, virtualAnnotation);
            }
            if (defaultAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, defaultAnnotation);
            }
            if (defaultBooleanAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, defaultBooleanAnnotation);
            }
            if (defaultIntAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, defaultIntAnnotation);
            }
            if (defaultLongAnnotation != null) {
                reportInvalidCombination(element, subResourceAnnotation, defaultLongAnnotation);
            }
        }

        // now, decide the right action to take

        if (subResourceAnnotation != null) {
            // it's a sub-resource
            // perform more specific return type validation
            if (!(returnType instanceof DeclaredType)) {
                msg.errorf(element, "Unsupported sub-resource type %s", returnType);
                return;
            }

        } else if (attributeGroupAnnotation != null) {
            // it's an attribute group
            // perform more specific return type validation
            if (!(returnType instanceof DeclaredType)) {
                msg.errorf(element, "Unsupported attribute group type %s", returnType);
                return;
            }
            final TypeElement returnTypeElement = (TypeElement) ((DeclaredType) returnType).asElement();
            if (returnTypeElement.getQualifiedName().toString().startsWith("java.")) {
                // indication that the type is probably invalid
                msg.error(element, "JDK types are not supported as attribute groups");
                return;
            }
            if (returnTypeElement.getKind() != ElementKind.INTERFACE) {
                msg.error(element, "Attribute group types must be interfaces");
                return;
            }

            AttributeGroupDescription.Builder agBuilder = AttributeGroupDescription.Builder.create();
            // start with Java name
            agBuilder.setJavaName(javaName);
            agBuilder.setDmrName(NameUtils.xmlify(javaName));
            agBuilder.setXmlName(NameUtils.xmlify(javaName));

            agBuilder.setNodeClassDescription(getOrCreateNodeClass((TypeElement) ((DeclaredType) returnType).asElement()));

            final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = attributeGroupAnnotation.getElementValues();
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
                final AnnotationValue annotationValue = entry.getValue();
                final Object value = annotationValue.getValue();
                switch (entry.getKey().getSimpleName().toString()) {
                    case "name": {
                        if (! (value instanceof String)) {
                            msg.errorf(element, attributeGroupAnnotation, annotationValue, "Expected a String value for name");
                            break;
                        }
                        agBuilder.setDmrName((String) value);
                        break;
                    }
                    case "required": {
                        if (! (value instanceof Boolean)) {
                            msg.errorf(element, attributeGroupAnnotation, annotationValue, "Expected a boolean value for required");
                            break;
                        }
                        agBuilder.setRequired(((Boolean) value).booleanValue());
                        break;
                    }
                    case "anonymous": {
                        if (! (value instanceof Boolean)) {
                            msg.errorf(element, attributeGroupAnnotation, annotationValue, "Expected a boolean value for anonymous");
                            break;
                        }
                        agBuilder.setPrefixAddress(! ((Boolean) value).booleanValue());
                        break;
                    }
                    default: {
                        unknownAnnotationArgument(element, attributeGroupAnnotation, annotationValue);
                        break;
                    }
                }
            }
            if (xmlNameAnnotation != null) {
                processXmlNameAnnotation(element, xmlNameAnnotation, agBuilder);
            }

            if (! roundEnv.errorRaised()) {
                builder.addMember(agBuilder.build());
            }
        } else if (attributeAnnotation != null) {

        } else {
            msg.error(element, "Superfluous attribute property");
        }

    }

    private void unknownAnnotationArgument(final Element element, final AnnotationMirror annotationMirror, final AnnotationValue annotationValue) {
        msg.error(element, annotationMirror, annotationValue, "Unknown annotation argument");
    }

    private void reportInvalidCombination(final Element element, final AnnotationMirror firstAnnotation, final AnnotationMirror invalidAnnotation) {
        msg.errorf(element, invalidAnnotation, "%s is not allowed with %s", invalidAnnotation, firstAnnotation);
    }

    private void processNamedItem(final TypeElement namedItemElement, final AbstractNamedDescription.Builder builder) {
        String javaName = builder.getJavaName();
        if (javaName == null) {
            javaName = namedItemElement.getSimpleName().toString();
        }
        builder.setJavaName(javaName);
        String dmrName = builder.getDmrName();
        if (dmrName == null) {
            dmrName = NameUtils.xmlify(javaName);
        }
        builder.setDmrName(dmrName);
        getXmlName(namedItemElement, builder);
    }

    private void processXmlNameAnnotation(final Element element, final AnnotationMirror annotationMirror, final AbstractNamedDescription.Builder builder) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            final AnnotationValue annotationValue = entry.getValue();
            final Object value = annotationValue.getValue();
            switch (entry.getKey().getSimpleName().toString()) {
                case "value": {
                    if (! (value instanceof String)) {
                        msg.error(element, annotationMirror, annotationValue, "Expected String value for value");
                        break;
                    }
                    builder.setXmlName((String) value);
                    break;
                }
                default: {
                    msg.error(element, annotationMirror, annotationValue, "Unknown annotation argument");
                    break;
                }
            }
        }
    }

    private void getXmlName(final TypeElement element, final AbstractNamedDescription.Builder builder) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            final TypeElement annotationTypeElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
            if (annotationTypeElement.getQualifiedName().toString().equals(XmlName.class.getName())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
                    final AnnotationValue annotationValue = entry.getValue();
                    final Object value = annotationValue.getValue();
                    switch (entry.getKey().getSimpleName().toString()) {
                        case "value": {
                            if (! (value instanceof String)) {
                                msg.error(element, annotationMirror, annotationValue, "Expected String value for value");
                                break;
                            }
                            builder.setXmlName((String) value);
                            break;
                        }
                        default: {
                            msg.error(element, annotationMirror, annotationValue, "Unknown annotation argument");
                            break;
                        }
                    }
                }
            }
        }
    }
}
