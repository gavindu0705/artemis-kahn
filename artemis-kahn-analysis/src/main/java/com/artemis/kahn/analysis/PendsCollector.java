package com.artemis.kahn.analysis;

import com.artemis.kahn.dao.mongo.entity.Pends;
import com.artemis.kahn.service.JobService;
import com.artemis.kahn.service.UrlsService;
import com.artemis.kahn.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 抓取成果收集线程
 *
 * @author xiaoyu
 */

@Service
public class PendsCollector extends Thread {

    public static final Logger LOG = LoggerFactory.getLogger(PendsCollector.class);

    public static final int BATCH_LIMIT = 2000;

    @Autowired
    JobService jobService;
    @Autowired
    UrlsService urlsService;


    @Override
    public void run() {
        while (true) {
            try {
                if (PendsHolder.getInstance().size() > 5000) {
                    Thread.sleep(1 * 1000);
//					LOG.print(pendsHolder.size() + " pends in queue, sleep 1000ms");
                    continue;
                }

                List<Pends> pendsList = getInitPends();
                if (pendsList.size() <= 0) {
                    Thread.sleep(3 * 1000);
                    continue;
                }
                for (Pends pends : pendsList) {
                    if (!jobService.isJobRunning(pends.getJobId())) {
                        continue;
                    }
                    PendsHolder.getInstance().put(pends);
                }
            } catch (InterruptedException e) {
                LOG.error("", e);
            }
        }
    }

    private List<Pends> getInitPends() {
        List<Pends> pendsList = urlsService.findPendsTaskInit(BATCH_LIMIT);
        if (pendsList.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<String> idList = new ArrayList<String>();
        for (Pends pends : pendsList) {
            idList.add(pends.getId());
        }

        List<List<String>> dataList = DataUtil.listSplit(idList, 500);
        for (List<String> ids : dataList) {
            urlsService.updatePendsTaskQue(ids);
        }

//		for (Pends pends : pendsList) {
//			urlsService.updatePendsTaskQue(pends.getId());
//		}

        return pendsList;
    }

}
