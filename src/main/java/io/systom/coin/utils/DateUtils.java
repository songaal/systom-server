package io.systom.coin.utils;

/*
 * create joonwoo 2018. 7. 10.
 * 
 */
public class DateUtils {

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
}