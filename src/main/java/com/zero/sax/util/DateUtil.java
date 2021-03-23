package com.zero.sax.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static final long MILL_24H = 3600_1000 * 24;
    private static final String PATTERN_yyyyMMdd = "yyyy-MM-dd";
    //
    private static final ThreadLocal<SimpleDateFormat> LOCAL_FORMATTER_yyyyMMdd = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(PATTERN_yyyyMMdd);
        };
    };

    public static Calendar timestampToCalendar(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        c.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));    // TODO: ShangHai
        return c;
    }

    public static Date fromFormat(String dateStr) throws ParseException {
        return LOCAL_FORMATTER_yyyyMMdd.get().parse(dateStr);
    }

    public static long toTimestamp(Date date) {
        if(date == null) {
            return 0L;
        }
        return date.getTime();
    }

    public static long fromStr(String dateStr) {
        try {
            return toTimestamp(fromFormat(dateStr));
        } catch (Exception e) {
            return 0L;
        }
    }
}
