/*
 * Copyright (c) 2023 Lucas Holt
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package org.midnightbsd.advisory.util;

public class VersionCompareUtil {

    private VersionCompareUtil() {}

    public static int transform(String val) {
        if (val.equals("~")) {
            return -1;
        } else if (val.matches("^\\d$")) {
            return Integer.parseInt(val) * 1 + 1;
        } else if (val.matches("^[A-Za-z]$")) {
            return val.charAt(0);
        } else {
            return val.charAt(0) + 128;
        }
    }

    /**
     * Compares two version strings and returns an integer indicating their relative order.
     *
     * @param ver1 The first version string to compare.
     * @param ver2 The second version string to compare.
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     * <p>
     * The version strings are split into segments based on non-digit characters. Each segment is then transformed using the {@link #transform(String)} method.
     * The transformed segments are compared one by one, from left to right. If a segment in the first version is greater than the corresponding segment in the second version,
     * a positive integer is returned. If a segment in the first version is less than the corresponding segment in the second version, a negative integer is returned.
     * If all segments are equal, zero is returned.
     *
     */
    public static int compare(String ver1, String ver2) {
        String[] a = ver1.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
        String[] b = ver2.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");

        int[] transformedA = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            transformedA[i] = transform(a[i]);
        }

        int[] transformedB = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            transformedB[i] = transform(b[i]);
        }

        for (int i = 0; i < Math.max(a.length, b.length); i++) {
            int aVal = i < a.length ? transformedA[i] : 0;
            int bVal = i < b.length ? transformedB[i] : 0;

            if (aVal > bVal) {
                return 1;
            } else if (aVal < bVal) {
                return -1;
            }
        }

        return 0;
    }
}
