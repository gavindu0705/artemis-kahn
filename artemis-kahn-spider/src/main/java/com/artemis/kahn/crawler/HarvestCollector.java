package com.artemis.kahn.crawler;

import com.artemis.kahn.core.bean.Harvest;
import com.artemis.kahn.service.FileDataService;
import com.artemis.kahn.service.UrlsService;
import com.artemis.kahn.spider.HarvestHolder;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 抓取成果收集线程
 *
 * @author xiaoyu
 */

@Component
public class HarvestCollector extends Thread {

    public static final Logger LOG = LoggerFactory.getLogger(HarvestCollector.class);

    @Autowired
    private UrlsService urlsService;

    @Autowired
    private FileDataService fileDataService;

    private HarvestHolder harvestHolder = HarvestHolder.getInstance();


    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        final Harvest harvest = harvestHolder.get();
                        if (harvest == null) {
                            try {
                                Thread.sleep(RandomUtils.nextInt(1, 1000));
                            } catch (InterruptedException e) {
                                LOG.error("", e);
                            }
                            continue;
                        }

                        if (harvest.getStatusCode() == Harvest.StatusEnum.SUCCESS.getCode()) {
                            processSuccess(harvest);
                        } else {
                            processError(harvest);
                        }
                    }
                }
            });
            t.setName("H-" + i);
            t.start();
        }
    }

    private void processError(Harvest harvest) {
        urlsService.updateUrlsCrawlErr(harvest.getUrl(), harvest.getJobId(), harvest.getSessionId(), harvest.getReferer(),
                Harvest.StatusEnum.parse(harvest.getStatusCode()));
    }

    private void processSuccess(Harvest har) {
        fileDataService.saveFileData(har.getUrl(), har.getContent());
        urlsService.updateUrlsCrawlSuc(har.getUrl(), har.getJobId(), har.getSessionId(), har.getReferer(), false);
    }
}
