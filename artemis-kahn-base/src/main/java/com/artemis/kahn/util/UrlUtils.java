package com.artemis.kahn.util;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 相对/绝对URL路径转换
 */
public class UrlUtils {
    public static final String QMARK = "?";

    /**
     * URL转换
     *
     * @param href    相对/绝对URL
     * @param referer 来源URL
     * @return
     */
    public static String toFullUrl(String href, String referer) {
        return toFullUrl(href, referer, null);
    }

    /**
     * URL转换
     *
     * @param href    相对/绝对URL
     * @param referer 来源URL
     * @param baseUrl 全局URL
     * @return
     */
    public static String toFullUrl(String href, String referer, String baseUrl) {
        if (StringUtils.isBlank(href) || href.startsWith("javascript")) {
            return null;
        }

        if (href.startsWith("http://")) {
            return href;
        }

        try {
            URL absoluteUrl = null;
            if (StringUtils.isNotBlank(baseUrl)) {
                absoluteUrl = new URL(baseUrl);
            } else if (StringUtils.isNotBlank(referer)) {
                if (referer.indexOf(QMARK) > 0) {
                    absoluteUrl = new URL(StringUtils.substringBefore(referer, "?"));
                } else {
                    absoluteUrl = new URL(referer);
                }
            }

            if (absoluteUrl == null) {
                return null;
            }

            if (href.startsWith(QMARK)) {
                return absoluteUrl.toString() + href;
            }

            return new URL(absoluteUrl, href).toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
