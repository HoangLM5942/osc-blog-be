package app.onestepcloser.blog.utility;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtil {

    private DateUtil() {}

    public enum FORMAT {
        F1("dd/MM/yyyy"),
        F2("yyyy-MM-dd"),
        F3("yyyyMMdd_HHmmss"),
        F4("MMM dd, yyyy");

        private final String format;

        FORMAT(String format) {
            this.format = format;
        }

        public String getFormat() {
            return this.format;
        }
    }

    public static Date getCurrentDateTime() {
        return new Date();
    }

    public static long getCurrentTime() {
        return getCurrentDateTime().getTime();
    }

    public static String getCurrentDateTime(String pattern) {
        return convertDateToString(getCurrentDateTime(), pattern);
    }

    public static String convertDateToString(Date date, String pattern) {
        if (StringUtils.isBlank(pattern) || date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    public static Date convertStringToDate(String str, String pattern) {
        if (StringUtils.isBlank(pattern) || StringUtils.isBlank(str)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(str);
        } catch (Exception e) {
            return null;
        }
    }
}
