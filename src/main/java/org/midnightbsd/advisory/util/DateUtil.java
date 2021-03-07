package org.midnightbsd.advisory.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Lucas Holt
 */
@Slf4j
public class DateUtil {
    public static Date yesterday() {
        return subtractDays(Calendar.getInstance().getTime(), 1);
    }

    public static Date subtractDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    public static String formatCveApiDate(final Date date) {
        if (date == null)
            return null;

        // they recommend yyyy-MM-dd'T'HH:mm:ss:SSS z but the api seems picky about the time zone format

        // 2019-01-01T00:00:00:000 UTC-05:00
        return formatDate(date, "yyyy-MM-dd'T'HH:mm:ss:'000' z");
    }

    public static String formatDate(final Date date, final String dateFormat) {
           if (date == null)
               return null;

           try {
               final SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);
               formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
               return formatter.format(date);
           } catch (final Exception e) {
               log.error("Could not convert date {}", date, e);
           }

           return null;
       }
}
