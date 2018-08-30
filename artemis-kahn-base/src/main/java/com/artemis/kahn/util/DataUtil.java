package com.artemis.kahn.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class DataUtil {

    /**
     * 全角转半角
     *
     * @param input
     * @return
     */
    public static String toDBCcase(String input) {
        if (StringUtils.isBlank(input))
            return "";
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c);
        return returnString;
    }

    public static <E> List<List<E>> listSplit(List<E> array, int splitNum) {
        List<List<E>> ret = new ArrayList<List<E>>();
        if (array == null || splitNum < 2) {
            return ret;
        }

        if (array instanceof Set || array instanceof HashSet) {
            array = new ArrayList(array);
        }

        List<E> tmpArray = array;
        while (true) {
            if (tmpArray.size() < splitNum) {
                if (tmpArray.size() > 0) {
                    ret.add(tmpArray);
                }
                break;
            }
            ret.add(tmpArray.subList(0, splitNum));
            tmpArray = tmpArray.subList(splitNum, tmpArray.size());
        }

        return ret;
    }


    // 字节码转换成16进制字符串
    public static String byte2hex(byte bytes[]) {
        StringBuffer retString = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            retString.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1).toUpperCase());
        }
        return retString.toString();
    }

    // 将16进制字符串转换成字节码
    public static byte[] hex2byte(String hex) {
        byte[] bts = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bts;
    }


    public static Map<String, String> parse(String parameter, String splitString, String equalChar) {
        Map<String, String> ret = new HashMap<String, String>();
        String[] parameters = StringUtils.splitByWholeSeparator(parameter, splitString);
        for (String s : parameters) {
            String[] o = StringUtils.splitByWholeSeparator(s, equalChar);
            if (o.length == 2) {
                ret.put(o[0], o[1]);
            }
        }
        return ret;
    }

    public static Map<String, String> stringToMap(String str, String split, String eq) {
        Map<String, String> ret = new HashMap<String, String>();
        if (str == null) {
            return ret;
        }

        String[] pairs = str.split(split);
        for (int i = 0; i < pairs.length; i++) {
            String[] arrs = pairs[i].split(eq);
            if (arrs.length == 2) {
                ret.put(arrs[0], arrs[1]);
            } else if (arrs.length == 1) {
                ret.put(arrs[0], "");
            }
        }
        return ret;
    }

    /**
     * 运行linux命令
     *
     * @param cmd
     * @return
     * @throws IOException
     */
    public static String runShell(String cmd) {
        Process p = null;
        InputStream fis = null;
        InputStreamReader isr = null;
        StringBuffer sb = new StringBuffer();
        try {
            p = Runtime.getRuntime().exec(cmd);
            fis = p.getInputStream();
            isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }

            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    System.out.println("error in runLinuxCommand close InputStreamReader");
                    e.printStackTrace();
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.out.println("error in runLinuxCommand close InputStream");
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public static String[] SITE_SUFFIXS = {".com.cn", ".com", ".cn", ".net", ".org"};

    public static String convertUrlToRoot(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        for (String suffix : SITE_SUFFIXS) {
            if (url.indexOf(suffix) > -1) {
                String s = StringUtils.substringBefore(url, suffix);
                if (s.indexOf(".") > -1) {
                    s = StringUtils.substringAfterLast(s, ".");
                    if (StringUtils.isNotBlank(s)) {
                        return s + suffix;
                    }
                } else if (s.indexOf("://") > -1) {
                    s = StringUtils.substringAfterLast(s, "://");
                    if (StringUtils.isNotBlank(s)) {
                        return s + suffix;
                    }
                }
                return StringUtils.substringBefore(url.replace("http://", ""), "/");
            }
        }

        return StringUtils.substringBefore(url.replace("http://", ""), "/");
    }

    /**
     * 按比例混合两个数组
     *
     * @param list0
     * @param list1
     * @return
     */
    public static <T> List<T> combineList(List<T> list0, List<T> list1) {
        if (list0 == null) {
            list0 = new ArrayList<T>();
        }
        if (list1 == null) {
            list1 = new ArrayList<T>();
        }
        if (list0.size() == 0) {
            return list1;
        }
        if (list1.size() == 0) {
            return list0;
        }

        ArrayList<T> mainList = (ArrayList<T>) list0;
        ArrayList<T> slaveList = (ArrayList<T>) list1;
        if (list1.size() > list0.size()) {
            mainList = (ArrayList<T>) list1;
            slaveList = (ArrayList<T>) list0;
        }

        int gap = mainList.size() / slaveList.size();
        for (int i = 0; i < slaveList.size(); i++) {
            mainList.add(i * gap + gap + i, slaveList.get(i));
        }
        return mainList;
    }

    /**
     * 解压
     *
     * @param content
     * @return
     * @throws IOException
     */
    public static byte[] unzip(byte[] content) throws IOException {
        byte[] buff = new byte[8192];
        ByteArrayInputStream bis = new ByteArrayInputStream(content);
        GZIPInputStream inputStream = new GZIPInputStream(bis);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (true) {
            int len = inputStream.read(buff);
            if (len > 0) {
                bos.write(buff, 0, len);
            } else {
                break;
            }
        }
        return bos.toByteArray();
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

    public static final Pattern pattern = Pattern.compile("charset=\"?([\\w\\d-]+)\"?;?", Pattern.CASE_INSENSITIVE);

    public static String matchCharset(String input) {
        if (input == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
