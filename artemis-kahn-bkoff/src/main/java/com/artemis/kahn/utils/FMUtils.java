package com.artemis.kahn.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class FMUtils {
    /**
     * 截取字符串
     *
     * @param str
     * @param beginIndex
     * @param endIndex
     * @return
     */
    public static String cutString(String str, int beginIndex, int endIndex) {
        return str.substring(beginIndex, endIndex);
    }

    /**
     * 获取字符在字符串中的index
     *
     * @param str
     * @param strChar
     * @return
     */
    public static int indexOf(String str, String strChar) {
        return str.indexOf(strChar);
    }


    /**
     * 编码
     *
     * @param s
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, "UTF-8");
    }


    public static String replace(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement);
    }

    public static String upper(String s) {
        if (s == null) {
            return "";
        }
        return s.toUpperCase();
    }

    public static String lower(String s) {
        if (s == null) {
            return "";
        }
        return s.toLowerCase();
    }

    public static boolean contains(String source, String target) {
        if (StringUtils.isBlank(source)) {
            return false;
        }

        if (target == null) {
            return false;
        }

        if ("".equals(target)) {
            return true;
        }

        return source.contains(target);
    }

    public static String mapToString(Map<String, Object> map, String split) {
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            if (StringUtils.isNotBlank(key)) {
                builder.append(key).append(":").append(map.get(key)).append(split);
            }
        }

        String s = builder.toString();
        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1);
        }

        return s;
    }

    public static String metadataToString(Map<String, Object> map, String split) {
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            if (StringUtils.isNotBlank(key)) {
                if ("content".equals(key)) {
                    builder.append(key).append(":").append("<textarea style='width:98%' rows=\"3\" cols=\"40\">" + map.get(key) + "</textarea>").append(split);
                } else {
                    builder.append(key).append(":").append(map.get(key)).append(split);
                }
            }
        }

        String s = builder.toString();
        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1);
        }

        return s;
    }

    public static String timesString(long seconds) {
        if (seconds >= 60 && seconds < 60 * 60) {
            String s = seconds / 60 + "分";
            if (seconds % 60 > 0) {
                s = s + seconds % 60 + "秒";
            }
            return s;
        } else if (seconds >= (60 * 60) && seconds < (60 * 60 * 24)) {
            String s = seconds / 3600 + "时";
            if (seconds % 3600 > 0) {
                s = s + timesString(seconds % 3600);
            }
            return s;
        } else if (seconds >= (60 * 60 * 24)) {
            String s = seconds / (60 * 60 * 24) + "天";
            if (seconds % (60 * 60 * 24) > 0) {
                s = s + timesString(seconds % (60 * 60 * 24));
            }
            return s;
        } else {
            return seconds + "秒";
        }
    }

    public static String join(List<String> list, String separator) {
        String s = StringUtils.join(list, separator);
        return s;
    }
}
