/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013 Red Hat, Inc., and individual contributors
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

package org.wildfly.core.management;

import static java.lang.Integer.signum;
import static java.lang.Math.min;

import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class VersionUtils {

    private static final Pattern VERSION_PATTERN = Pattern.compile("\\d+(?:\\.\\d+)*");

    private VersionUtils() {
    }

    /**
     * Determine whether a given version string is valid.
     *
     * @param versionString the version string
     * @return {@code true} if it is valid, {@code false} otherwise
     */
    public static boolean isVersionValid(String versionString) {
        return VERSION_PATTERN.matcher(versionString).matches();
    }

    private static int[] versionToSegments(String versionString, int idx, int i) {
        int c, d, a;
        c = versionString.codePointAt(idx);
        idx = versionString.offsetByCodePoints(idx, 1);
        d = Character.digit(c, 10);
        if (d == -1) {
            throw new IllegalArgumentException("String is not in version format");
        }
        a = d;
        for (;;) {
            c = versionString.codePointAt(idx);
            idx = versionString.offsetByCodePoints(idx, 1);
            if (c == '.') {
                final int[] ints = versionToSegments(versionString, idx + 1, i + 1);
                ints[i] = a;
                return ints;
            }
            d = Character.digit(c, 10);
            if (d == -1) {
                throw new IllegalArgumentException("String is not in version format");
            }
            a = a * 10 + d;
            if (idx == versionString.length()) {
                final int[] ints = new int[i + 1];
                ints[i] = a;
                return ints;
            }
        }
    }

    public static String segmentsToVersion(int[] segments) {
        final StringBuilder b = new StringBuilder(segments.length * 3);
        for (int idx = 0; idx < segments.length; idx++) {
            b.append(segments[idx]);
            if (idx < segments.length - 1) {
                b.append('.');
            }
        }
        return b.toString();
    }

    public static int[] versionToSegments(String versionString) {
        return versionToSegments(versionString, 0, 0);
    }

    public static int compareVersions(String versionA, String versionB) {
        final int[] componentsA = versionToSegments(versionA), componentsB = versionToSegments(versionB);
        final int max = min(componentsA.length, componentsB.length);
        int res;
        for (int i = 0; i < max; i ++) {
            res = signum(componentsA[i] - componentsB[i]);
            if (res != 0) {
                return res;
            }
        }
        // reverse so that shorter versions sort above longer versions
        return signum(componentsB.length - componentsA.length);
    }

    public static final Comparator<String> VERSION_COMPARATOR = new Comparator<String>() {
        public int compare(final String o1, final String o2) {
            return compareVersions(o1, o2);
        }
    };
}
