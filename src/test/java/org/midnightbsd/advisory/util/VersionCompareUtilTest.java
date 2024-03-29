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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionCompareUtilTest {

    @Test
    void testNumericVersions() {
        assertEquals(0,VersionCompareUtil.compare("1.0.0", "1.0.0"));
        assert VersionCompareUtil.compare("1.0.0", "1.0.1") < 0;
        assert VersionCompareUtil.compare("1.0.1", "1.0.0") > 0;
        assertEquals(0,VersionCompareUtil.compare("1.0.1", "1.0.1"));
    }

    @Test
    void testMixedVersions() {
        assertEquals(0,VersionCompareUtil.compare("1.0.0a", "1.0.0a"));
        assert VersionCompareUtil.compare("1.0.0a", "1.0.0b") < 0;
        assert VersionCompareUtil.compare("b", "a") > 0;
        assertEquals(0, VersionCompareUtil.compare("1a", "1a"));
    }

    @Test
    void testTransformTilde() {
        assertEquals(-1, VersionCompareUtil.transform("~"));
    }
}
