package com.artemis.kahn.utils;


import com.artemis.kahn.util.DataUtil;
import com.artemis.kahn.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class TemplateUtils {
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
     * 全角转半角
     *
     * @param input
     * @return
     */
    public static String toDBCcase(String input) {
        return DataUtil.toDBCcase(input);
    }

    /**
     * 去除不支持的编码字符
     *
     * @param input
     * @return
     */
    public static String cutUnvalidCode(String input) {
        return input.replaceAll("[^\\x00-\\x80\uFE30-\uFFA0\u4E00-\u9FA5]+", "");
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

    /**
     * 格式化日期
     */
    public static String dateFormat(Date date, String pattern) {
        if (null == date || null == pattern)
            return "";

        return DateUtil.formatByPattern(date, pattern);
    }

    /**
     * 获取date前days天的日期
     *
     * @param date
     * @param days
     * @return
     */
    public static Date getBeforeDay(Date date, int days) {
        if (null == date) {
            date = new Date();
        }
        return DateUtil.getBeforeDay(date, days);
    }

    public static String replace(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement);
    }

    /**
     * 获取1~size的随机数
     *
     * @param size
     * @return
     */
    public static int getRandomIndex(int size) {
        return (int) (Math.random() * size) + 1;
    }

    public static String formatDate(Date publishDate) {
        return formatDate(publishDate, "yyyy-MM-dd");
    }

    public static String formatDate(Date publishDate, String pattern) {
        if (publishDate == null) {
            return "";
        }
        return DateUtil.formatByPattern(publishDate, pattern);
    }

    public static String formatCurrentDate(String pattern) {
        return formatDate(new Date(), pattern);
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
        return DataUtil.mapToString(map, split);
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
