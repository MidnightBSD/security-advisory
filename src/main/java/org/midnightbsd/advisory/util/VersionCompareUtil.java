package org.midnightbsd.advisory.util;

public class VersionCompareUtil {

    private VersionCompareUtil() {}

    public static int transform(String val) {
        if (val.equals("~")) {
            return -1;
        } else if (val.matches("^\\d$")) {
            return Integer.parseInt(val) * 1 + 1;
        } else if (val.matches("^[A-Za-z]$")) {
            return (int) val.charAt(0);
        } else {
            return (int) val.charAt(0) + 128;
        }
    }

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
