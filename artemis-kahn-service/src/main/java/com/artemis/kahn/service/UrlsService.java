package com.artemis.kahn.service;

import com.artemis.kahn.component.AsyncThread;
import com.artemis.kahn.core.bean.Harvest;
import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.Pends;
import com.artemis.kahn.dao.mongo.entity.Urls;
import com.artemis.kahn.dao.mongo.repo.PendsDao;
import com.artemis.kahn.dao.mongo.repo.UrlsDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * URL
 *
 * @author xiaoyu
 */
@Service
public class UrlsService {

    public static final Logger LOG = LoggerFactory.getLogger(UrlsService.class);

    @Autowired
    private UrlsDao urlsDao;
    @Autowired
    private PendsDao pendsDao;
    @Autowired
    private JobService jobService;
    @Autowired
    private JobStatService jobStatService;
    @Autowired
    private UrlRoadService urlRoadService;

    public static final BlockingQueue<String> URLS_CRAWL_SUC_QUEUE = new LinkedBlockingQueue<String>(10000);
    public static final BlockingQueue<Urls> URLS_CRAWL_ERR_QUEUE = new LinkedBlockingQueue<Urls>(10000);
    public static final BlockingQueue<String> PENDS_TASK_SUC_QUEUE = new LinkedBlockingQueue<String>(10000);

    private UrlsService() {
//        new AsyncThread("crawl-suc", 10, new AsyncThread.Call() {
//
//            public void invoke() {
//                List<String> urlsIdList = new ArrayList<String>(200);
//                URLS_CRAWL_SUC_QUEUE.drainTo(urlsIdList, 200);
//                if (urlsIdList.size() > 0) {
//                    updateUrlsCrawlSuc(urlsIdList);
//                }
//                if (urlsIdList.size() < 100) {
//                    try {
//                        Thread.sleep(3 * 1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        new AsyncThread("crawl-err", 10, new AsyncThread.Call() {
//
//            public void invoke() {
//                List<Urls> urlsList = new ArrayList<Urls>(500);
//                URLS_CRAWL_ERR_QUEUE.drainTo(urlsList, 500);
//                if (urlsList.size() > 0) {
//                    updateUrlsCrawlErr(urlsList);
//                }
//
//                if (URLS_CRAWL_ERR_QUEUE.size() < 1000) {
//                    try {
//                        Thread.sleep(3 * 1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        new AsyncThread("task-suc", 10, new AsyncThread.Call() {
//
//            public void invoke() {
//                List<String> idList = new ArrayList<String>(1000);
//                PENDS_TASK_SUC_QUEUE.drainTo(idList, 1000);
//                if (idList.size() > 0) {
//                    deletePendsByIds(idList);
//                }
//                if (PENDS_TASK_SUC_QUEUE.size() < 100) {
//                    try {
//                        Thread.sleep(3 * 1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

    }

    /**
     * 插入需要抓取的URL
     *
     * @param url
     * @param referer
     * @param charset
     */
    public void addUrlsCrawlInit(String url, String referer, String charset, String jobId, String sessionId,
                                 Map<String, String> params) {
        if (url != null && jobId != null) {
            Job job = jobService.getJobById(jobId);
            if (job == null) {
                return;
            }

            Urls entity = new Urls();
            entity.setId(url.trim());
            if (StringUtils.isNotBlank(charset)) {
                entity.setCharset(charset);
            }
            entity.setReferer(referer);
            entity.setStatus(Urls.StatusEnum.CRAWL_INIT.getStatus());
            entity.setJobId(jobId);
            entity.setParams(params);
            entity.setPriority(job.getPriority());
            entity.setSessionId(job.getSessionId());
            entity.setRoot(job.getRoot());
            entity.setCreationDate(new Date());

            addUrlsCrawlInit(Arrays.asList(entity));
        }
    }

    /**
     * 插入需要抓取的URL
     *
     * @param urlsList
     */
    public void addUrlsCrawlInit(List<Urls> urlsList) {
        if (urlsList == null || urlsList.size() <= 0) {
            return;
        }

        List<Urls> crawlList = new ArrayList<Urls>(urlsList.size());
        for (Urls urls : urlsList) {
            if (urls.getId() == null || urls.getJobId() == null) {
                continue;
            }

            Job job = jobService.getJobById(urls.getJobId());
            if (job == null) {
                continue;
            }

            urls.setJobId(job.getId());
            urls.setStatus(Urls.StatusEnum.CRAWL_INIT.getStatus());
            urls.setPriority(job.getPriority());
            urls.setSessionId(job.getSessionId());
            urls.setRoot(job.getRoot());
            urls.setCreationDate(new Date());
            crawlList.add(urls);
            urlRoadService.addUrlRoad(job.getId(), job.getSessionId(), urls.getId(), urls.getReferer(),
                    Urls.StatusEnum.CRAWL_INIT);
        }
        for (Urls u : crawlList) {
            try {
                urlsDao.save(u);
            } catch (Exception e) {
                LOG.error("jobId:" + u.getJobId() + "\turl:" + u.getId(), e);
            }
        }
    }

    /**
     * 按JOB_ID查询
     *
     * @param jobId
     * @param limit
     * @return
     */
    public List<Urls> findUrlsByJobId(String jobId, int limit) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        return urlsDao.findAll(q, limit);
    }

    public List<Urls> findUrlsByJobId(String jobId, Urls.StatusEnum statusEnum, int limit) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        q.put("status", statusEnum.getStatus());
        DBObject sq = new BasicDBObject();
        sq.put("c_date", 1);
        return urlsDao.findAll(q, sq, limit);
    }

    /**
     * 待抓取的URL数量
     *
     * @return
     */
    public long findUrlsCrawlInitCount() {
        DBObject q = new BasicDBObject();
        q.put("status", Urls.StatusEnum.CRAWL_INIT.getStatus());
        return urlsDao.findCount(q);
    }

    public long findUrlsCount() {
        return urlsDao.findCount();
    }

    // public long findUrlsCrawlInitCount(String jobId) {
    // DBObject q = new BasicDBObject();
    // q.put("job_id", jobId);
    // q.put("status", Urls.CRAWL_INIT);
    // return urlsDao.findCount(q);
    // }

    /**
     * 取出抓取成功的URL
     *
     * @param limit
     * @return
     */
    public List<Urls> findUrlsCrawlSuc(int limit) {
        DBObject q = new BasicDBObject();
        q.put("status", Urls.StatusEnum.CRAWL_SUC.getStatus());
        DBObject sq = new BasicDBObject();
        sq.put("c_date", 1);
        return urlsDao.findAll(q, sq, limit);
    }

    /**
     * 取出需要被重新抓取的URL
     *
     * @param limit
     * @return
     */
    public List<Urls> findUrlsCrawlErr(int limit) {
        DBObject q = new BasicDBObject();
        q.put("status", Urls.StatusEnum.CRAWL_ERR.getStatus());
        DBObject sq = new BasicDBObject();
        sq.put("c_date", 1);
        return urlsDao.findAll(q, sq, limit);
    }

    /**
     * 更新URL状态为抓取排队中
     *
     * @param id
     */
    public void updateUrlsCrawlQue(String id) {
        DBObject data = new BasicDBObject();
        data.put("status", Urls.StatusEnum.CRAWL_QUE.getStatus());
        urlsDao.updateById(data, id);
    }

    /**
     * 更新urls状态到#Urls.CRAWL_QUE#
     *
     * @param idList
     */
    public void updateUrlsCrawlQue(List<String> idList) {
        if (idList.size() <= 0) {
            return;
        }
        DBObject data = new BasicDBObject();
        data.put("status", Urls.StatusEnum.CRAWL_QUE.getStatus());
        urlsDao.update(data, new BasicDBObject("_id", new BasicDBObject("$in", idList)));
    }

    /**
     * 更新urls状态为抓取成功
     *
     * @param url
     * @param jobId
     * @param sessionId
     * @param referer
     * @param isSync    是否及时更新
     */
    public void updateUrlsCrawlSuc(String url, String jobId, String sessionId, String referer, boolean isSync) {
        if (jobId == null || sessionId == null || url == null) {
            return;
        }

        if (isSync) {
            this.updateUrlsCrawlSuc(Arrays.asList(url));
        } else {
            try {
                URLS_CRAWL_SUC_QUEUE.put(url);
            } catch (InterruptedException e) {
                LOG.error("", e);
            }
        }

        // 记录抓取成功日志
        jobStatService.increaseCrawlCount(jobId, sessionId);
        urlRoadService.addUrlRoad(jobId, sessionId, url, referer, Urls.StatusEnum.CRAWL_SUC);
    }

    /**
     * 更新状态为抓取错误
     *
     * @param url
     * @param jobId
     * @param sessionId
     * @param referer
     * @param statusCode
     */
    public void updateUrlsCrawlErr(String url, String jobId, String sessionId, String referer, Harvest.StatusEnum statusCode) {
        if (jobId == null || sessionId == null || url == null) {
            return;
        }

        Urls urls = new Urls();
        urls.setId(url);
        urls.setStatusCode(statusCode.getCode());
        try {
            URLS_CRAWL_ERR_QUEUE.put(urls);
        } catch (InterruptedException e) {
            LOG.error("", e);
        }

        // 记录日志
        jobStatService.increaseErrCount(jobId, sessionId);
        urlRoadService.addUrlRoad(jobId, sessionId, url, referer, Urls.StatusEnum.CRAWL_ERR);
    }

    /**
     * 更新urls状态到#Urls.CRAWL_SUC#
     *
     * @param idList
     */
    private void updateUrlsCrawlSuc(List<String> idList) {
        if (idList.size() <= 0) {
            return;
        }

        if (idList.size() == 1) {
            DBObject data = new BasicDBObject();
            data.put("status", Urls.StatusEnum.CRAWL_SUC.getStatus());
            data.put("status_code", Harvest.StatusEnum.SUCCESS.getCode());
            urlsDao.updateById(data, idList.get(0));
        } else {
            DBObject data = new BasicDBObject();
            data.put("status", Urls.StatusEnum.CRAWL_SUC.getStatus());
            data.put("status_code", Harvest.StatusEnum.SUCCESS.getCode());
            urlsDao.update(data, new BasicDBObject("_id", new BasicDBObject("$in", idList)));
        }
    }

    /**
     * 更新urls状态到#Urls.CRAWL_ERR#,并且增加错误数
     *
     * @param urlsList
     */
    private void updateUrlsCrawlErr(List<Urls> urlsList) {
        if (urlsList.size() <= 0) {
            return;
        }

        // 进行分组后的批量更新
        Map<Integer, List<String>> statusCodeIdsMap = new HashMap<Integer, List<String>>();
        for (Urls obj : urlsList) {
            List<String> ids = null;
            if (statusCodeIdsMap.containsKey(obj.getStatusCode())) {
                ids = statusCodeIdsMap.get(obj.getStatusCode());
            } else {
                ids = new ArrayList<String>();
            }
            ids.add(obj.getId());
            statusCodeIdsMap.put(obj.getStatusCode(), ids);
        }

        for (Integer statusCode : statusCodeIdsMap.keySet()) {
            DBObject data = new BasicDBObject();
            data.put("status", Urls.StatusEnum.CRAWL_ERR.getStatus());
            data.put("status_code", statusCode);
            urlsDao.update(data, new BasicDBObject("_id", new BasicDBObject("$in", statusCodeIdsMap.get(statusCode))));
        }
    }

    /**
     * 修改为待抓取，并且增加出错次数
     *
     * @param id
     */
    public void updateUrlsCrawlErrToInit(String id, int errorCount) {
        DBObject data = new BasicDBObject();
        data.put("status", Urls.StatusEnum.CRAWL_INIT.getStatus());
        data.put("errors", errorCount + 1);
        urlsDao.updateById(data, id);
    }

    /**
     * URL抓取待处理更新到抓取初始化
     */
    public void updateUrlsCrawlQueToInit() {
        DBObject q = new BasicDBObject();
        q.put("status", Urls.StatusEnum.CRAWL_QUE.getStatus());
        DBObject data = new BasicDBObject();
        data.put("status", Urls.StatusEnum.CRAWL_INIT.getStatus());
        urlsDao.update(data, q);// status_1
    }

    /**
     * 删除urls
     *
     * @param idList
     */
    public void deleteUrls(List<String> idList) {
        if (idList == null || idList.size() == 0) {
            return;
        }
        this.urlsDao.delete(new BasicDBObject("_id", new BasicDBObject("$in", idList)));
    }

    public void deletePendsById(String id) {
        this.pendsDao.deleteById(id);
    }

    public void deletePendsByIds(List<String> idList) {
        if (idList == null || idList.size() == 0) {
            return;
        }
        LOG.error("delete pends: " + idList.size());
        this.pendsDao.delete(new BasicDBObject("_id", new BasicDBObject("$in", idList)));
    }

    /**
     * 按任务删除
     *
     * @param jobId
     */
    public void deleteUrlsByJobId(String jobId) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        urlsDao.delete(q);// job_id_1_status_1_c_date_1
    }

    public List<Pends> findPendsTaskInit(int limit) {
        DBObject q = new BasicDBObject();
        q.put("status", Urls.StatusEnum.TASK_INIT.getStatus());
        DBObject sq = new BasicDBObject();
        sq.put("c_date", 1);
        return pendsDao.findAll(q, sq, limit);
    }

    /**
     * 待处理的URL数量
     *
     * @return
     */
    public long findPendsTaskInitCount() {
        DBObject q = new BasicDBObject();
        q.put("status", Urls.StatusEnum.TASK_INIT.getStatus());
        return pendsDao.findCount(q);
    }

    public long findPendsCount() {
        return pendsDao.findCount();
    }

    public long findPendsTaskInitCount(String jobId) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        q.put("status", Urls.StatusEnum.TASK_INIT.getStatus());
        return pendsDao.findCount(q);
    }

    public List<Pends> findPendsByJobId(String jobId, int limit) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        return pendsDao.findAll(q, null, limit);
    }

    public List<Pends> findPendsByJobId(String jobId, Urls.StatusEnum statusEnum, int limit) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        q.put("status", statusEnum.getStatus());
        DBObject sq = new BasicDBObject();
        sq.put("c_date", 1);
        return pendsDao.findAll(q, sq, limit);
    }

    public List<Pends> findPendsByJobIdStatus(String jobId, int status, int limit) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        q.put("status", status);
        return pendsDao.findAll(q, null, limit);
    }

    /**
     * URL任务待处理更新到任务初始化
     */
    public void updatePendsTaskQueToInit() {
        DBObject q = new BasicDBObject();
        q.put("status", Pends.StatusEnum.TASK_QUE.getStatus());
        DBObject data = new BasicDBObject();
        data.put("status", Pends.StatusEnum.TASK_INIT.getStatus());
        pendsDao.update(data, q);
    }

    /**
     * 更新URL状态为任务处理排队中
     *
     * @param id
     */
    public void updatePendsTaskQue(String id) {
        DBObject data = new BasicDBObject();
        data.put("status", Pends.StatusEnum.TASK_QUE.getStatus());
        pendsDao.updateById(data, id);
    }

    public void updatePendsTaskQue(List<String> idList) {
        if (idList.size() > 0) {
            pendsDao.update(new BasicDBObject("status", Pends.StatusEnum.TASK_QUE.getStatus()), new BasicDBObject("_id", new BasicDBObject("$in", idList)));
        }
    }

    public void updatePendsTaskSuc(String id, String jobId, String sessionId, String referer) {
        // 记录任务处理日志
        jobStatService.increaseTaskCount(jobId, sessionId);
        urlRoadService.addUrlRoad(jobId, sessionId, id, referer, Urls.StatusEnum.TASK_SUC);
//		this.deletePendsById(id);
        try {
            PENDS_TASK_SUC_QUEUE.put(id);
        } catch (InterruptedException e) {
            LOG.error("", e);
        }
    }

    public void pendsToErrsTaskErr(Pends pends) {
        this.deletePendsById(pends.getId());
        urlRoadService.addUrlRoad(pends.getJobId(), pends.getSessionId(), pends.getId(), pends.getReferer(),
                Urls.StatusEnum.TASK_ERR);
    }

    public void pendsToErrsTaskNOFILE(Pends pends) {
        this.deletePendsById(pends.getId());
        urlRoadService.addUrlRoad(pends.getJobId(), pends.getSessionId(), pends.getId(), pends.getReferer(),
                Urls.StatusEnum.TASK_NO_FILE);
    }

    public void pendsToErrsTaskNOJOB(Pends pends) {
        this.deletePendsById(pends.getId());
        urlRoadService.addUrlRoad(pends.getJobId(), pends.getSessionId(), pends.getId(), pends.getReferer(),
                Urls.StatusEnum.TASK_NO_JOB);
    }

    public void pendsToErrsTaskNOPAGE(Pends pends) {
        this.deletePendsById(pends.getId());
        urlRoadService.addUrlRoad(pends.getJobId(), pends.getSessionId(), pends.getId(), pends.getReferer(),
                Urls.StatusEnum.TASK_NO_PAGE);
    }

    public void pendsToErrsTaskNOTASK(Pends pends) {
        this.deletePendsById(pends.getId());
        urlRoadService.addUrlRoad(pends.getJobId(), pends.getSessionId(), pends.getId(), pends.getReferer(),
                Urls.StatusEnum.TASK_NO_TASK);
    }

    public void deletePendsByJobId(String jobId) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        pendsDao.delete(q);
    }

    public Pends findPendsById(String id) {
        return this.pendsDao.findById(id);
    }

    /**
     * urls转换成pends
     *
     * @param urlsList
     */
    public void urlsToPends(List<Urls> urlsList) {
        if (urlsList == null || urlsList.size() == 0) {
            return;
        }

        List<String> idList = new ArrayList<String>();
        for (Urls urls : urlsList) {
            try {
                Pends pends = new Pends();
                BeanUtils.copyProperties(pends, urls);
                pends.setStatus(Pends.StatusEnum.TASK_INIT.getStatus());
                pendsDao.save(pends);
            } catch (Exception e) {
                LOG.error("", e);
            }
            idList.add(urls.getId());
        }

        urlsDao.delete(new BasicDBObject("_id", new BasicDBObject("$in", idList)));
    }

    public long findUrlsCount(Urls.StatusEnum statusEnum) {
        DBObject q = new BasicDBObject();
        q.put("status", statusEnum.getStatus());
        return urlsDao.findCount(q);// status_1
    }

    public long findUrlsCount(String jobId, Urls.StatusEnum statusEnum) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        q.put("status", statusEnum.getStatus());
        return urlsDao.findCount(q);// job_id_1_status_1_c_date_1
    }

    public long findPendsCount(Urls.StatusEnum statusEnum) {
        DBObject q = new BasicDBObject();
        q.put("status", statusEnum.getStatus());
        return pendsDao.findCount(q);
    }

    public long findPendsCount(String jobId, Urls.StatusEnum statusEnum) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        q.put("status", statusEnum.getStatus());
        return pendsDao.findCount(q);
    }

    public Urls findById(String urlsId) {
        return urlsDao.findById(urlsId);
    }

    // public long findErrsCount(UrlsStatusEnum statusEnum) {
    // DBObject q = new BasicDBObject();
    // q.put("status", statusEnum.getStatus());
    // return errsDao.findCount(q);
    // }
    //
    // public long findErrsCount(String jobId, UrlsStatusEnum statusEnum) {
    // DBObject q = new BasicDBObject();
    // q.put("job_id", jobId);
    // q.put("status", statusEnum.getStatus());
    // return errsDao.findCount(q);
    // }

}
