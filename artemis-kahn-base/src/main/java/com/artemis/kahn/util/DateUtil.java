package com.artemis.kahn.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {


    /**
     * 把date truncate到日期为止,去掉时分秒
     *
     * @param date
     * @return
     */
    public static Date truncateDay(Date date) {
        return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
    }

    /**
     * 转化日期成yyyymmddhhmmss的String格式
     *
     * @param date
     * @return
     */
    public static String formatToTightYYYYMMDDhhmmss(Date date) {
        return formatByPattern(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 转化日期成yyyymmddhhmmss的String格式
     *
     * @param date
     * @return
     */
    public static String formatToYYYYMMDDhhmmss(Date date) {
        if (date == null)
            return "";
        String result = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            result = format.format(date);
        } catch (Exception e) {
            return "";
        }
        return result;
    }

    public static String formatByPattern(Date date, String pattern) {
        if (date == null)
            return "";
        String result = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            result = format.format(date);
        } catch (Exception e) {
            return "";
        }
        return result;
    }

    public static Date getBeforeDay(Date date, int num) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -num);

        return truncateDay(cal.getTime());
    }


}
