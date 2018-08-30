package com.artemis.kahn.crawler;

import com.artemis.kahn.dao.mongo.entity.Urls;
import com.artemis.kahn.service.UrlsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务通知者
 *
 * @author xiaoyu
 */

@Component
public class AdvisorThread extends Thread {
    public static final Logger LOG = LoggerFactory.getLogger(AdvisorThread.class);

    @Autowired
    private UrlsService urlsService;

    @Override
    public void run() {
        while (true) {
            try {
                List<Urls> urlsList = urlsService.findUrlsCrawlSuc(2000);
                urlsService.urlsToPends(urlsList);
                if (urlsList.size() <= 100) {
                    Thread.sleep(3 * 1000);
                }
            } catch (InterruptedException e) {
                LOG.error("", e);
            }
        }
    }
}
