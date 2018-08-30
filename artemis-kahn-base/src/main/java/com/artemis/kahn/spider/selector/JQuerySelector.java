package com.artemis.kahn.spider.selector;


import com.artemis.kahn.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class JQuerySelector implements Selector {

    private Document document;

    public JQuerySelector(Document document) {
        this.document = document;
    }

    @Override
    public List<String> href(String selector, String refererUrl) {
        if (document == null || StringUtils.isBlank(selector)) {
            return null;
        }

        List<String> ret = new ArrayList<String>();
        Elements elements = document.body().select(selector);

        String baseUrl = null;
        Elements baseEles = document.getElementsByTag("base");
        if (baseEles != null && baseEles.size() == 1) {
            baseUrl = baseEles.get(0).attr("href");
        }

        for (Element element : elements) {
            String url = UrlUtils.toFullUrl(element.attr("href"), refererUrl, baseUrl);
            if (url != null) {
                ret.add(url);
            }
        }
        return ret;
    }


    @Override
    public List<String> attr(String selector, String attrName) {
        if (document == null || StringUtils.isBlank(selector) || StringUtils.isBlank(attrName)) {
            return null;
        }

        String[] selectors = selector.split(",");
        if (selectors == null || selectors.length == 0) {
            return null;
        }

        Elements elements = null;
        for (int i = 0; i < selectors.length; i++) {
            Element body = document.body();
            if(body != null) {
                elements = body.select(selector);
            }else {
                elements = document.select(selector);
            }
            if (elements != null && elements.size() > 0) {
                break;
            }
        }

        List<String> list = new ArrayList<String>();
        if (elements != null) {
            for (Element element : elements) {
                list.add(element.attr(attrName));
            }
        }

        return list;
    }

    @Override
    public String html(String selector) {
        if (document == null || StringUtils.isBlank(selector)) {
            return null;
        }

        String[] selectors = selector.split(",");
        Elements elements = null;
        for (int i = 0; i < selectors.length; i++) {
            elements = document.body().select(selectors[i]);
            if (elements != null && elements.size() > 0) {
                break;
            }
        }

        StringBuilder ret = new StringBuilder();
        if (elements != null) {
            for (Element element : elements) {
                ret.append(element.html());
            }
        }

        return ret.toString();
    }

    @Override
    public String text(String selector) {
        if (document == null || StringUtils.isBlank(selector)) {
            return null;
        }

        String[] selectors = selector.split(",");
        Elements elements = null;
        for (int i = 0; i < selectors.length; i++) {
            elements = document.body().select(selectors[i]);
            if (elements != null && elements.size() > 0) {
                break;
            }
        }

        StringBuilder ret = new StringBuilder();
        if (elements != null) {
            for (Element element : elements) {
                ret.append(element.text());
            }
        }

        return ret.toString();
    }


}
