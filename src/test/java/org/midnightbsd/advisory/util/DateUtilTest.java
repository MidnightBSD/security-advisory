package org.midnightbsd.advisory.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author Lucas Holt
 */
public class DateUtilTest {

    private Date specificDate() {
        //Generate a date for Jan. 9, 2013, 10:11:12 AM
        Calendar cal = Calendar.getInstance();
        cal.set(2013, Calendar.JANUARY, 9, 10, 11, 12); //Year, month, day of month, hours, minutes and seconds
        return cal.getTime();
    }

    @Test
    public void subtractDays() {
        Date result = DateUtil.subtractDays(specificDate(), 1);
        assertEquals("Tue Jan 08 10:11:12 EST 2013", result.toString());
    }

    @Test
    public void yesterday() {
        Date now = Calendar.getInstance().getTime();
        assertEquals(DateUtil.subtractDays(now, 1).toString(), DateUtil.yesterday().toString());
    }

    @Test
    public void formatCveApiDate() {
        String result = DateUtil.formatCveApiDate(specificDate());
        assertEquals("2013-01-09T15:11:12:000 UTC", result);
    }
}
