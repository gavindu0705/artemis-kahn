package com.artemis.kahn.spider.selector;

import java.util.List;

public interface Selector {

    List<String> href(String selector, String refererUrl);

    List<String> attr(String selector, String attrName);

    String html(String selector);

    String text(String selector);

}
