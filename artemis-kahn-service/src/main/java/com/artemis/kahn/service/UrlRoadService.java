package com.artemis.kahn.service;

import com.artemis.kahn.dao.mongo.entity.UrlRoad;
import com.artemis.kahn.dao.mongo.entity.Urls;
import com.artemis.kahn.dao.mongo.repo.UrlRoadDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 记录一个URL的执行流程
 *
 * @author xiaoyu
 */

@Service
public class UrlRoadService {
    public static final Logger LOG = LoggerFactory.getLogger(UrlRoadService.class);

    @Autowired
    private UrlRoadDao urlRoadDao;

    public static final int MAX_QUEUE_SIZE = 1000000;
    private BlockingQueue<UrlRoad> URL_ROAD_QUEUE = new LinkedBlockingQueue<UrlRoad>(MAX_QUEUE_SIZE);
    public static final int CONSUMER_THREAD_SIZE = Integer.getInteger("urlroad.consumer.thread.size", 1);


    private UrlRoadService() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                consumer();
//            }
//        }).start();
    }

    public int getUrlRoadQueueSize() {
        return URL_ROAD_QUEUE.size();
    }

    private void consumer() {
        for (int i = 0; i < CONSUMER_THREAD_SIZE; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            UrlRoad urlRoad = URL_ROAD_QUEUE.take();
//							urlRoadDao.save(urlRoad);
                        } catch (InterruptedException e) {
                            LOG.error("take error. current size:" + URL_ROAD_QUEUE.size(), e);
                        }
                    }
                }
            });
            t.setName("UR-" + i);
            t.start();
        }

    }

    public void addUrlRoad(String jobId, String sessionId, String requestUrl, String refererUrl, Urls.StatusEnum status) {
        if (jobId == null || StringUtils.isBlank(requestUrl)) {
            return;
        }
        UrlRoad urlRoad = new UrlRoad();
        urlRoad.setJobId(jobId);
        urlRoad.setSessionId(sessionId);
        urlRoad.setRequestUrl(requestUrl);
        urlRoad.setMd5Request(MD5HASH(requestUrl));

        if (StringUtils.isNotBlank(refererUrl)) {
            urlRoad.setRefererUrl(refererUrl);
            urlRoad.setMd5Referer(MD5HASH(refererUrl));
        }
        urlRoad.setStatus(status.getStatus());
        urlRoad.setCreationDate(new Date());
        try {
            URL_ROAD_QUEUE.put(urlRoad);
        } catch (InterruptedException e) {
            LOG.error("put error. current size:" + URL_ROAD_QUEUE.size(), e);
        }
    }

    public List<UrlRoad> findUrlRoadByRefererUrl(String jobId, String sessionId, String refererUrl) {
        DBObject q = new BasicDBObject();
        q.put("md5_referer", MD5HASH(refererUrl));
        q.put("job_id", jobId);
        q.put("session_id", sessionId);
        DBObject sq = new BasicDBObject();
        sq.put("c_date", 1);
        return this.urlRoadDao.findAll(q, sq);
    }

    public int findUrlRoadByRefererUrlCount(String jobId, String sessionId, String refererUrl) {
        DBObject q = new BasicDBObject();
        q.put("md5_referer", MD5HASH(refererUrl));
        q.put("job_id", jobId);
        if (sessionId != null) {
            q.put("session_id", sessionId);
        }
        return (int) this.urlRoadDao.findCount(q);
    }


    public List<UrlRoad> findUrlRoadByRequestUrl(String jobId, String sessionId, String requestUrl) {
        DBObject q = new BasicDBObject();
        q.put("md5_request", MD5HASH(requestUrl));
        q.put("job_id", jobId);
        if (sessionId != null) {
            q.put("session_id", sessionId);
        }
        DBObject sq = new BasicDBObject();
        sq.put("c_date", 1);
        return this.urlRoadDao.findAll(q, sq);
    }


    public int findUrlRoadByRequestUrlCount(String jobId, String sessionId, String requestUrl) {
        DBObject q = new BasicDBObject();
        q.put("md5_request", MD5HASH(requestUrl));
        q.put("job_id", jobId);
        if (sessionId != null) {
            q.put("session_id", sessionId);
        }
        return (int) this.urlRoadDao.findCount(q);
    }


    public UrlRoad findEarliestUrlRoad() {
        List<UrlRoad> list = urlRoadDao.findAll(new BasicDBObject(), new BasicDBObject("$natural", 1), 1);
        if (list != null) {
            return list.get(0);
        }
        return null;
    }

    public String MD5HASH(String s) {
        return DigestUtils.md5Hex(s) + Math.abs(s.hashCode());
    }
}
