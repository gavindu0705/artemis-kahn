package com.artemis.kahn.service;

import com.artemis.kahn.dao.mongo.entity.JobStat;
import com.artemis.kahn.dao.mongo.repo.JobStatDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JobStatService {

    public static final Logger LOG = LoggerFactory.getLogger(JobStatService.class);

    @Autowired
    JobStatDao jobStatDao;

    @Autowired
    JobService jobService;


    public void startJobStat(String jobId, String sessionId) {
        JobStat jobStat = new JobStat();
        jobStat.setJobId(jobId);
        jobStat.setSessionId(sessionId);
        jobStat.setStartDate(new Date());

        jobStat.setCrawlCount(0);
        jobStat.setTaskCount(0);
        jobStat.setErrCount(0);
        jobStat.setMetaCount(0);
        jobStat.setPubCount(0);

        jobStat.setCreationDate(new Date());

        this.jobStatDao.save(jobStat);
    }

    public void endJobStat(String jobId, String sessionId) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        q.put("session_id", sessionId);
        DBObject data = new BasicDBObject();
        data.put("end_date", new Date());
        this.jobStatDao.update(data, q);
    }

    public void increaseCrawlCount(String jobId, String sessionId) {
        jobService.increaseCrawlCount(jobId, sessionId);
    }

    public void increaseTaskCount(String jobId, String sessionId) {
        jobService.increaseTaskCount(jobId, sessionId);
    }

    public void increaseErrCount(String jobId, String sessionId) {
        jobService.increaseErrCount(jobId, sessionId);
    }

    public void increaseMetaCount(String jobId, String sessionId) {
        jobService.increaseMetaCount(jobId, sessionId);
    }

}
