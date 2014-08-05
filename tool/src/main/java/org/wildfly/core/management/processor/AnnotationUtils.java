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

package org.wildfly.core.management.processor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.wildfly.core.management.annotation.XmlName;
import org.wildfly.core.management.annotation.XmlTypeName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class AnnotationUtils {

    private AnnotationUtils() {
    }

    public static boolean isLocalSource(ProcessingEnvironment env, TypeElement element) {
        final Elements elements = env.getElementUtils();
        final Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement == null) {
            // it's a package or type param
            return false;
        }
        final ElementKind enclosingKind = enclosingElement.getKind();
        if (enclosingKind != ElementKind.PACKAGE) {
            return isLocalSource(env, (TypeElement) enclosingElement);
        } else try {
            env.getFiler().getResource(StandardLocation.SOURCE_PATH, elements.getPackageOf(element).getQualifiedName().toString(), element.getSimpleName().toString() + ".java");
            return true;
        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            env.getMessager().printMessage(Diagnostic.Kind.ERROR, "Problem locating source element '" + element + "'", element);
            return false;
        }
    }

    public static AnnotationMirror getAnnotation(Elements elements, Element annotatedElement, Class<? extends Annotation> annotation) {
        return getAnnotation(elements, annotatedElement, annotation.getName());
    }

    public static AnnotationMirror getAnnotation(Elements elements, Element annotatedElement, String annotationName) {
        if (annotatedElement == null) {
            return null;
        }
        for (AnnotationMirror mirror : elements.getAllAnnotationMirrors(annotatedElement)) {
            if (((TypeElement)mirror.getAnnotationType().asElement()).getQualifiedName().toString().equals(annotationName)) {
                return mirror;
            }
        }
        return null;
    }

    public static AnnotationValue getAnnotationValue(AnnotationMirror mirror, String name) {
        if (mirror == null) {
            return null;
        }
        final Set<? extends Map.Entry<? extends ExecutableElement,? extends AnnotationValue>> entries = mirror.getElementValues().entrySet();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : entries) {
            if (entry.getKey().getSimpleName().toString().equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static <E extends Enum<E>> E getAnnotationValueEnumConst(AnnotationMirror mirror, String name, Class<E> type) {
        return enumConstValue(type, getAnnotationValue(mirror, name));
    }

    public static String getAnnotationValueString(AnnotationMirror mirror, String name) {
        return stringValue(getAnnotationValue(mirror, name));
    }

    public static String getAnnotationValueClassName(AnnotationMirror mirror, String name) {
        return classNameValue(getAnnotationValue(mirror, name));
    }

    public static String stringValue(AnnotationValue value) {
        if (value == null) return null;
        return (String) value.getValue();
    }

    public static TypeMirror classValue(AnnotationValue value) {
        if (value == null) return null;
        return (TypeMirror) value.getValue();
    }

    public static String classNameValue(AnnotationValue value) {
        if (value == null) return null;
        final TypeMirror typeMirror = classValue(value);
        return typeMirror instanceof PrimitiveType ? ((PrimitiveType)typeMirror).getKind().toString().toLowerCase(Locale.ENGLISH) : ((DeclaredType)typeMirror).asElement().getSimpleName().toString();
    }

    public static VariableElement enumValue(AnnotationValue value) {
        return value == null ? null : (VariableElement) value.getValue();
    }

    public static String enumNameValue(AnnotationValue value) {
        final VariableElement element = enumValue(value);
        return element == null ? null : element.getSimpleName().toString();
    }

    public static <E extends Enum<E>> E enumConstValue(Class<E> type, AnnotationValue value) {
        final String name = enumNameValue(value);
        return name == null ? null : Enum.valueOf(type, name);
    }

    public static boolean booleanValue(AnnotationValue value, boolean defVal) {
        if (value == null) return defVal;
        return ((Boolean) value.getValue()).booleanValue();
    }

    public static int intValue(AnnotationValue value, int defVal) {
        if (value == null) return defVal;
        return ((Integer) value.getValue()).intValue();
    }

    public static String[] stringArrayValue(AnnotationValue value) {
        if (value == null) return null;
        @SuppressWarnings("unchecked")
        final List<? extends AnnotationValue> list = (List<? extends AnnotationValue>) value.getValue();
        final String[] array = new String[list.size()];
        int i = 0;
        for (AnnotationValue annotationValue : list) {
            array[i++] = stringValue(annotationValue);
        }
        return array;
    }

    public static TypeMirror[] classArrayValue(AnnotationValue value) {
        if (value == null) return null;
        @SuppressWarnings("unchecked")
        final List<? extends AnnotationValue> list = (List<? extends AnnotationValue>) value.getValue();
        final TypeMirror[] array = new TypeMirror[list.size()];
        int i = 0;
        for (AnnotationValue annotationValue : list) {
            array[i++] = classValue(annotationValue);
        }
        return array;
    }

    public static String[] classNameArrayValue(AnnotationValue value) {
        if (value == null) return null;
        @SuppressWarnings("unchecked")
        final List<? extends AnnotationValue> list = (List<? extends AnnotationValue>) value.getValue();
        final String[] array = new String[list.size()];
        int i = 0;
        for (AnnotationValue annotationValue : list) {
            array[i++] = classNameValue(annotationValue);
        }
        return array;
    }

    static Map<String, AnnotationMirror> mirrorListToMap(List<? extends AnnotationMirror> mirrors) {
        if (mirrors.isEmpty()) return Collections.emptyMap();
        final LinkedHashMap<String, AnnotationMirror> map = new LinkedHashMap<>();
        for (AnnotationMirror mirror : mirrors) {
            final DeclaredType type = mirror.getAnnotationType();
            final TypeElement annotationTypeElement = (TypeElement) type.asElement();
            final String annotationName = annotationTypeElement.getQualifiedName().toString();
            map.put(annotationName, mirror);
        }
        return map;
    }

    static Map<String, AnnotationValue> mirrorValuesToMap(AnnotationMirror mirror) {
        final LinkedHashMap<String, AnnotationValue> map = new LinkedHashMap<>();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
            map.put(entry.getKey().getSimpleName().toString(), entry.getValue());
        }
        return map;
    }

    public static String getAnnotationName(final AnnotationMirror mirror) {
        return ((TypeElement)mirror.getAnnotationType().asElement()).getQualifiedName().toString();
    }

    public static boolean annotationIs(final AnnotationMirror mirror, final Class<?> testClass) {
        return getAnnotationName(mirror).equals(testClass.getName());
    }

    static String getXmlTypeName(final Elements elements, final Element typeElement, final String xmlName) {
        String xmlTypeName = AnnotationUtils.getAnnotationValueString(AnnotationUtils.getAnnotation(elements, typeElement, XmlTypeName.class), "value");
        if (xmlTypeName == null) xmlTypeName = xmlName + "-type";
        return xmlTypeName;
    }

    static String getXmlName(final Elements elements, final Element element) {
        return getXmlName(elements, element, element.getSimpleName().toString());
    }

    static String getXmlName(final Elements elements, final Element element, final String baseName) {
        String xmlName = AnnotationUtils.getAnnotationValueString(AnnotationUtils.getAnnotation(elements, element, XmlName.class), "value");
        if (xmlName == null) xmlName = NameUtils.xmlify(baseName);
        return xmlName;
    }
}
