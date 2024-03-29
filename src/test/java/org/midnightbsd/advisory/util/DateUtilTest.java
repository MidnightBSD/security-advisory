/*
 * Copyright (c) 2017-2021 Lucas Holt
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

import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** @author Lucas Holt */
class DateUtilTest {

  private Date specificDate() {
    // Generate a date for Jan. 9, 2013, 10:11:12 AM
    Calendar cal = Calendar.getInstance();
    cal.set(
        2013,
        Calendar.JANUARY,
        9,
        10,
        11,
        12); // Year, month, day of month, hours, minutes and seconds
    return cal.getTime();
  }

  @Test
  void subtractDays() {
    Date result = DateUtil.subtractDays(specificDate(), 1);
    Assertions.assertEquals("Tue Jan 08 10:11:12 EST 2013",result.toString());
  }

  @Test
  void yesterday() {
    Date now = Calendar.getInstance().getTime();
    Assertions.assertEquals(DateUtil.subtractDays(now, 1).toString(),DateUtil.yesterday().toString());
  }

  @Test
  void formatCveApiDate() {
    String result = DateUtil.formatCveApiDate(specificDate());
    Assertions.assertEquals("2013-01-09T15:11:12.000-05:00",result); // hardcoded for EST for now
  }

  @Test
  void testGetCveApiDate() {
    var result = DateUtil.getCveApiDate("2019-01-01T00:00Z");
    Assertions.assertEquals("Tue Jan 01 00:00:00 EST 2019",result.toString());
  }
}
