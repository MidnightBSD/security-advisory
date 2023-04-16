package org.midnightbsd.advisory.util;

import org.junit.jupiter.api.Test;

class VersionCompareUtilTest {

    @Test
    void testNumericVersions() {
        assert VersionCompareUtil.compare("1.0.0", "1.0.0") == 0;
        assert VersionCompareUtil.compare("1.0.0", "1.0.1") < 0;
        assert VersionCompareUtil.compare("1.0.1", "1.0.0") > 0;
        assert VersionCompareUtil.compare("1.0.1", "1.0.1") == 0;
    }

    @Test
    void testMixedVersions() {
        assert VersionCompareUtil.compare("1.0.0a", "1.0.0a") == 0;
        assert VersionCompareUtil.compare("1.0.0a", "1.0.0b") < 0;
        assert VersionCompareUtil.compare("b", "a") > 0;
        assert VersionCompareUtil.compare("1a", "1a") == 0;
    }
}
