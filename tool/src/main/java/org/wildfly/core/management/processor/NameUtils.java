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

import java.util.Locale;
import org.wildfly.core.management.annotation.Schema;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class NameUtils {

    private NameUtils() {
    }

    public static String without(String name, String suffix) {
        return name.endsWith(suffix) ? name.substring(0, name.length() - suffix.length()) : name;
    }

    public static String singular(String plural) {
        if (plural.endsWith("ies")) {
            return plural.substring(0, plural.length() - 3) + "y";
        } else if (plural.endsWith("s")) {
            return plural.substring(0, plural.length() - 1);
        } else {
            return plural;
        }
    }

    public static String wordsToTypeCamelHumps(String[] words) {
        final StringBuilder b = new StringBuilder();
        for (String word : words) {
            if ("JBoss".equalsIgnoreCase(word)) {
                b.append("JBoss");
            } else if ("OSGi".equalsIgnoreCase(word)) {
                b.append("OSGi");
            } else {
                final int first = word.codePointAt(0);
                b.appendCodePoint(Character.toUpperCase(first));
                b.append(word.substring(Character.charCount(first)).toLowerCase(Locale.ENGLISH));
            }
        }
        return b.toString();
    }

    public static String wordsToVarCamelHumps(String[] words) {
        final int length = words.length;
        if (length == 0) return "";
        final StringBuilder b = new StringBuilder();
        String word = words[0];
        b.append(word.toLowerCase(Locale.ENGLISH));
        for (int i = 1; i < length; i++) {
            word = words[i];
            if ("JBoss".equalsIgnoreCase(word)) {
                b.append("JBoss");
            } else if ("OSGi".equalsIgnoreCase(word)) {
                b.append("OSGi");
            } else {
                final int first = word.codePointAt(0);
                b.appendCodePoint(Character.toUpperCase(first));
                b.append(word.substring(Character.charCount(first)).toLowerCase(Locale.ENGLISH));
            }
        }
        return b.toString();
    }

    public static String wordsToSeparatedLower(String[] words, int sep) {
        final int length = words.length;
        if (length == 0) return "";
        final StringBuilder b = new StringBuilder();
        for (String word : words) {
            if (b.length() > 0) b.appendCodePoint(sep);
            b.append(word.toLowerCase(Locale.ENGLISH));
        }
        return b.toString();
    }

    public static String wordsToSeparatedUpper(String[] words, int sep) {
        final int length = words.length;
        if (length == 0) return "";
        final StringBuilder b = new StringBuilder();
        for (String word : words) {
            if (b.length() > 0) b.appendCodePoint(sep);
            b.append(word.toUpperCase(Locale.ENGLISH));
        }
        return b.toString();
    }

    private static String[] camelHumpsToWords0(String camelHumps, int start, int len) {
        if (start == camelHumps.length()) {
            return new String[len];
        }
        int idx = start;
        // skip capital prefix
        if (camelHumps.startsWith("JBoss", idx)) {
            idx += 5;
        } else if (camelHumps.startsWith("OSGi", idx)) {
            idx += 4;
        } else {
            idx += 1;
        }
        int c;
        int l;
        while (idx < camelHumps.length()) {
            c = camelHumps.codePointAt(idx);
            l = Character.charCount(c);
            if (Character.isUpperCase(c)) {
                String[] words = camelHumpsToWords0(camelHumps, idx, len + 1);
                words[len] = camelHumps.substring(start, idx);
                return words;
            }
            idx += l;
        }
        String[] words = camelHumpsToWords0(camelHumps, idx, len + 1);
        words[len] = camelHumps.substring(start, idx);
        return words;
    }

    public static String[] camelHumpsToWords(String camelHumps) {
        return camelHumpsToWords0(camelHumps, 0, 0);
    }

    private static String[] separatedToWords0(String separated, int cp, int start, int len) {
        int idx = separated.indexOf(cp, start);
        final String[] val;
        if (idx == -1) {
            val = new String[len + 1];
            val[len] = separated.substring(start);
        } else {
            val = separatedToWords0(separated, cp, idx + 1, len + 1);
            val[len] = separated.substring(start, idx);
        }
        return val;
    }

    public static String[] separatedToWords(String separated, int cp) {
        return separatedToWords0(separated, cp, 0, 0);
    }

    /*
     foo-bar-a-baz -> FooBarABaz
    */
    public static String classify(String xmlName) {
        return wordsToTypeCamelHumps(separatedToWords(xmlName, '-'));
    }

    /*
     AFunnyXMLKindAThing -> a-funny-xml-kind-a-thing
    */
    public static String xmlify(String camelHumpsName) {
        return wordsToSeparatedLower(camelHumpsToWords(camelHumpsName), '-');
    }

    /*
     AFunnyXMLKindAThing -> a.funny.xml.kind.a.thing
    */
    public static String namespaceify(String camelHumpsName) {
        return wordsToSeparatedLower(camelHumpsToWords(camelHumpsName), '.');
    }

    /*
     AFunnyXMLKindAThing -> A_FUNNY_XML_KIND_A_THING
    */
    public static String constify(String camelHumpsName) {
        return wordsToSeparatedUpper(camelHumpsToWords(camelHumpsName), '_');
    }

    /*
     BLAFunnyJavaKindAThing -> blaFunnyJavaKindAThing
     FOO -> foo
     Foo -> foo
     foo -> foo
     FOOBar -> fooBar
    */
    public static String fieldify(String camelHumpsName) {
        return wordsToVarCamelHumps(camelHumpsToWords(camelHumpsName));
    }

    public static String buildNamespace(final Schema.Kind kind, final String namespace, final String version) {
        StringBuilder b = new StringBuilder(64);
        if (kind == Schema.Kind.SYSTEM) {
            b.append("sys:");
        } else {
            b.append("ext:");
        }
        b.append(namespace).append(':').append(version);
        return b.toString();
    }
}
