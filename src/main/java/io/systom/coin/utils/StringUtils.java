package io.systom.coin.utils;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class StringUtils {
    private static Random _RNG = new Random(System.currentTimeMillis());

    public StringUtils() {
    }

    public static int length(String str) {
        return str == null ? 0 : str.length();
    }

    public static boolean equalOrEmpty(String s1, String s2) {
        s1 = s1 == null ? "" : s1;
        s2 = s2 == null ? "" : s2;
        return s1.equals(s2);
    }

    public static char lastChar(String str) {
        int index = str != null ? str.length() - 1 : -1;
        return index < 0 ? '\u0000' : str.charAt(index);
    }

    public static boolean isEmpty(String str) {
        return length(str) == 0;
    }

    public static boolean isBlank(String str) {
        if (str == null) {
            str = "";
        }

        for(int ii = 0; ii < str.length(); ++ii) {
            if (!Character.isWhitespace(str.charAt(ii))) {
                return false;
            }
        }

        return true;
    }

    public static String trim(String str) {
        if (str == null) {
            return "";
        } else if (str.length() == 0) {
            return "";
        } else if (!Character.isWhitespace(str.charAt(0)) && !Character.isWhitespace(str.charAt(str.length() - 1))) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder(str);

            while(sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
                sb.deleteCharAt(0);
            }

            while(sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
                sb.deleteCharAt(sb.length() - 1);
            }

            return sb.toString();
        }
    }

    public static String trimToNull(String str) {
        str = trim(str);
        return str.length() == 0 ? null : str;
    }

    public static String padLeft(String str, int len, char c) {
        if (str == null) {
            str = "";
        }

        if (str.length() >= len) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder(len);

            for(int ii = len - str.length(); ii > 0; --ii) {
                sb.append(c);
            }

            sb.append(str);
            return sb.toString();
        }
    }

    public static String padRight(String str, int len, char c) {
        if (str == null) {
            str = "";
        }

        if (str.length() >= len) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder(len);
            sb.append(str);

            while(sb.length() < len) {
                sb.append(c);
            }

            return sb.toString();
        }
    }

    public static boolean contains(String str, String segment) {
        if (str != null && segment != null) {
            if (segment.length() == 0) {
                return true;
            } else {
                return str.indexOf(segment) >= 0;
            }
        } else {
            return false;
        }
    }

    public static boolean containsIgnoreCase(String str, String segment) {
        return str != null && segment != null ? contains(str.toUpperCase(), segment.toUpperCase()) : false;
    }

    public static String repeat(char c, int count) {
        char[] chars = new char[count];

        for(int ii = 0; ii < count; ++ii) {
            chars[ii] = c;
        }

        return new String(chars);
    }

    public static byte[] toUTF8(String str) {
        try {
            return str == null ? new byte[0] : str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new RuntimeException("UTF-8 not supported", var2);
        }
    }

    public static String fromUTF8(byte[] bytes) {
        try {
            return bytes == null ? "" : new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new RuntimeException("UTF-8 not supported", var2);
        }
    }

    public static String unescape(String src) {
        if (src == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder(src);

            for(int ii = 0; ii < sb.length(); ++ii) {
                if (sb.charAt(ii) == '\\') {
                    sb.deleteCharAt(ii);
                    if (sb.charAt(ii) != 'u' && sb.charAt(ii) != 'U') {
                        if (sb.charAt(ii) == 'b') {
                            sb.setCharAt(ii, '\b');
                        } else if (sb.charAt(ii) == 't') {
                            sb.setCharAt(ii, '\t');
                        } else if (sb.charAt(ii) == 'n') {
                            sb.setCharAt(ii, '\n');
                        } else if (sb.charAt(ii) == 'f') {
                            sb.setCharAt(ii, '\f');
                        } else if (sb.charAt(ii) == 'r') {
                            sb.setCharAt(ii, '\r');
                        }
                    } else {
                        int c = (hex2dec(sb.charAt(ii + 1)) << 12) + (hex2dec(sb.charAt(ii + 2)) << 8) + (hex2dec(sb.charAt(ii + 3)) << 4) + hex2dec(sb.charAt(ii + 4));
                        sb.setCharAt(ii, (char)c);
                        sb.delete(ii + 1, ii + 5);
                    }
                }
            }

            return sb.toString();
        }
    }

    public static int parseDigit(char c, int base) {
        int value = -1;
        if (c >= '0' && c <= '9') {
            value = c - 48;
        } else if (c >= 'a' && c <= 'z') {
            value = c - 97 + 10;
        } else if (c >= 'A' && c <= 'Z') {
            value = c - 65 + 10;
        }

        if (value >= base) {
            value = -1;
        }

        return value;
    }

//    public static String intern(String str) {
//        return _canon.intern(str);
//    }

    public static String randomString(String chars, int minLength, int maxLength) {
        StringBuilder sb = new StringBuilder(maxLength);
        int len = minLength + _RNG.nextInt(maxLength - minLength + 1);

        for(int ii = 0; ii < len; ++ii) {
            sb.append(chars.charAt(_RNG.nextInt(chars.length())));
        }

        return sb.toString();
    }

    public static String randomAlphaString(int minLength, int maxLength) {
        return randomString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", minLength, maxLength);
    }

    public static String extractLeft(String source, String target) {
        if (source == null) {
            source = "";
        }

        if (target == null) {
            target = "";
        }

        if (target.length() == 0) {
            return source;
        } else {
            int idx = source.indexOf(target);
            return idx < 0 ? source : source.substring(0, idx);
        }
    }

    public static String extractRight(String source, String target) {
        if (source == null) {
            source = "";
        }

        if (target == null) {
            target = "";
        }

        if (target.length() == 0) {
            return "";
        } else {
            int idx = source.indexOf(target);
            return idx < 0 ? "" : source.substring(idx + target.length());
        }
    }

    public static String extractLeftOfLast(String source, String target) {
        if (source == null) {
            source = "";
        }

        if (target == null) {
            target = "";
        }

        if (target.length() == 0) {
            return source;
        } else {
            int idx = source.lastIndexOf(target);
            return idx < 0 ? source : source.substring(0, idx);
        }
    }

    public static String extractRightOfLast(String source, String target) {
        if (source == null) {
            source = "";
        }

        if (target == null) {
            target = "";
        }

        if (target.length() == 0) {
            return "";
        } else {
            int idx = source.lastIndexOf(target);
            return idx < 0 ? "" : source.substring(idx + target.length());
        }
    }

    public static boolean isIn(String str, String... targets) {
        String[] arr$ = targets;
        int len$ = targets.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String target = arr$[i$];
            if (str == null && target == null) {
                return true;
            }

            if (str != null && str.equals(target)) {
                return true;
            }
        }

        return false;
    }

    public static String valueOf(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private static int hex2dec(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        } else if (c >= 'A' && c <= 'F') {
            return c - 65 + 10;
        } else if (c >= 'a' && c <= 'f') {
            return c - 97 + 10;
        } else {
            throw new IllegalArgumentException("not a hex digit: " + c);
        }
    }

    //genAsciiCode 에 국한됨
    static char[] myChars;
    static {
        myChars = new char[62];
        for (int i = 0; i < 62; i++) {
            int j = 0;
            if (i < 10) {
                j = i + 48;
            } else if (i > 9 && i <= 35) {
                j = i + 55;
            } else {
                j = i + 61;
            }
            myChars[i] = (char) j;
        }
    }
    public static String genAsciiCode(int len) {
        Random myRand = new Random();
        StringBuilder key = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            key.append(myChars[myRand.nextInt(62)]);
        }
        return key.toString();
    }
}
