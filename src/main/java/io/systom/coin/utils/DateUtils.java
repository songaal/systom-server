package io.systom.coin.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/*
 * create joonwoo 2018. 7. 10.
 * 
 */
public class DateUtils {

    private static String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";
    private static String defaultTimeZone = "Asia/Seoul";

    public static String getDateString() {
        return getDateString(null, null);
    }
    public static String getDateString(String dateFormat) {
        return getDateString(dateFormat, null);
    }
    public static String getDateString(String dateFormat, String timeZone) {
        dateFormat = dateFormat == null ? defaultDateFormat : dateFormat;
        timeZone = timeZone == null ? defaultTimeZone : timeZone;
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        return new SimpleDateFormat(dateFormat).format(Calendar.getInstance(tz).getTime());
    }

    public static Calendar getDate() {
        return getDate(null);
    }
    public static Calendar getDate(String timeZone) {
        timeZone = timeZone == null ? defaultTimeZone : timeZone;
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        return Calendar.getInstance(tz);
    }


    public static String formatDate (Integer y, Integer m, Integer d) {
        String strY = "";
        String strM = "";
        String strD = "";
        if (y != null) {
            strY = String.valueOf(y);
        }
        if (m != null) {
            strM = m < 10 ? "0" + String.valueOf(m) : String.valueOf(m);
        }
        if (d != null) {
            strD = d < 10 ? "0" + String.valueOf(d) : String.valueOf(d);
        }
        return strY + strM + strD;
    }

    public static String formatDate (Integer y, Integer m, Integer d, Integer hh, Integer mm, Integer ss) {
        String strY = "";
        String strM = "";
        String strD = "";
        String strHH = "";
        String strMM = "";
        String strSS = "";

        if (y != null) {
            strY = String.valueOf(y);
        }
        if (m != null) {
            strM = m < 10 ? "0" + String.valueOf(m) : String.valueOf(m);
        }
        if (d != null) {
            strD = d < 10 ? "0" + String.valueOf(d) : String.valueOf(d);
        }
        if (hh != null) {
            strHH = hh < 10 ? "0" + String.valueOf(hh) : String.valueOf(hh);
        }
        if (d != null) {
            strMM = mm < 10 ? "0" + String.valueOf(mm) : String.valueOf(mm);
        }
        if (d != null) {
            strSS = ss < 10 ? "0" + String.valueOf(ss) : String.valueOf(ss);
        }
        return strY + strM + strD + strHH + strMM + strSS;
    }

}