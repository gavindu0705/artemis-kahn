package com.artemis.kahn.analysis;

import com.artemis.kahn.dao.mongo.entity.Metadata;
import com.artemis.kahn.dao.mongo.entity.Pends;
import com.artemis.kahn.dao.mongo.entity.Urls;
import com.artemis.kahn.dao.mongo.repo.MetadataDao;
import com.artemis.kahn.service.JobService;
import com.artemis.kahn.service.JobStatService;
import com.artemis.kahn.service.UrlsService;
import com.artemis.kahn.service.analysis.PageResult;
import com.artemis.kahn.service.analysis.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class TaskerThread implements Runnable {
    public static final Logger LOG = LoggerFactory.getLogger(TaskerThread.class);

    @Autowired
    JobService jobService;
    @Autowired
    TaskService taskService;
    @Autowired
    PendsHolder pendsHolder;
    @Autowired
    MetadataDao metadataDao;
    @Autowired
    UrlsService urlsService;
    @Autowired
    JobStatService jobStatService;
//    private BloomsContainer bloomsContainer = BloomsContainer.getInstance(TaskConfig.getInstance().getBloomFilePath());

    @Override
    public void run() {
        while (true) {
            Pends pends = pendsHolder.get();
            if (pends == null) {
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            // 判断任务是否执行中
            if (!jobService.isJobRunning(pends.getJobId())) {
                continue;
            }

            // 页面解析出的数据
            PageResult result = null;
            try {
                result = taskService.doAnalysis(pends);
            }catch (Exception e) {
                this.urlsService.pendsToErrsTaskErr(pends);
                LOG.error("", e);
            }

            if (result != null) {
                Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    saveMetadata(metadata);
//                    MasterTasker.META_INC.incrementAndGet();
//                    MasterTasker.M_META_INC.incrementAndGet();
                }

                List<Urls> hrefs = result.getHrefs();
                if (hrefs != null) {
                    int ucount = saveUrlsToCrawlInit(hrefs);
//                    MasterTasker.URLS_INC.addAndGet(ucount);
//                    MasterTasker.M_URLS_INC.addAndGet(ucount);
                }
            }

//            MasterTasker.PROCESS_INC.incrementAndGet();
//            MasterTasker.M_PROCESS_INC.incrementAndGet();
        }
    }

    /**
     * 保存元数据
     *
     * @param metadata
     * @return
     */
    public int saveMetadata(Metadata metadata) {
        try {
            if (metadata != null && metadata.getUrl() != null) {
                // 记录日志
                jobStatService.increaseMetaCount(metadata.getJobId(), metadata.getSessionId());
                this.metadataDao.save(metadata);
                return 1;
            }
        } catch (Exception e) {
            LOG.error("jobId:" + metadata.getJobId() + ",url:" + metadata.getUrl(), e);
        }
        return 0;
    }

    /**
     * 保存待抓取的URL
     *
     * @param urls
     * @return
     */
    public int saveUrlsToCrawlInit(List<Urls> urls) {
        if (urls == null || urls.size() == 0) {
            return 0;
        }

        List<Urls> crawlList = new ArrayList<Urls>();
        for (Urls u : urls) {
//            if (!bloomsContainer.exists(u.getJobId(), u.getSessionId(), u.getId())) {
                u.setId(u.getId().trim());
                if (StringUtils.isBlank(u.getCharset())) {
                    u.setCharset(null);
                }
                crawlList.add(u);
//                bloomsContainer.add(u.getJobId(), u.getSessionId(), u.getId());
//            }
        }

        urlsService.addUrlsCrawlInit(crawlList);
        return crawlList.size();
    }


}
