package com.artemis.kahn.service;


import com.artemis.kahn.component.AsyncThread;
import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.JobStat;
import com.artemis.kahn.dao.mongo.entity.Pends;
import com.artemis.kahn.dao.mongo.entity.Seed;
import com.artemis.kahn.dao.mongo.entity.Urls;
import com.artemis.kahn.dao.mongo.repo.JobDao;
import com.artemis.kahn.dao.mongo.repo.JobStatDao;
import com.artemis.kahn.dao.mongo.repo.MetadataDao;
import com.artemis.kahn.dao.mongo.repo.SeedDao;
import com.artemis.kahn.util.DataUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class JobService {

	public static final Logger LOG = LoggerFactory.getLogger(JobService.class);

	@Autowired
	private JobDao jobDao ;
	@Autowired
	private JobStatDao jobStatDao;
	@Autowired
	private SeedDao seedDao;
	@Autowired
	private MetadataDao metadataDao;
	@Autowired
	private UrlsService urlsService;
	@Autowired
	private JobStatService jobStatService;


	private Map<String, Job> jobMap = new HashMap<String, Job>();
	private List<String> hasPendUrlJobs = new ArrayList<String>();

	private BlockingQueue<JobStat> JOB_STAT_QUEUE = new LinkedBlockingQueue<JobStat>(10000000);


	private JobService() {
//		jobMap = toMap(jobDao.findAll());
//		new AsyncThread(1000 * 3, new AsyncThread.Call() {
//			public void invoke() {
//				reloadJobs();
//			}
//		});
//
//		new AsyncThread(1000 * 30, new AsyncThread.Call() {
//			public void invoke() {
//				jobStatConsumer();
//			}
//		});

	}

	public void jobStatConsumer() {
		List<JobStat> jobStats = new ArrayList<JobStat>();
		JOB_STAT_QUEUE.drainTo(jobStats);

		Map<DBObject, DBObject> updateMap = new HashMap<DBObject, DBObject>();
		for (JobStat jobStat : jobStats) {
			DBObject q = new BasicDBObject();
			q.put("job_id", jobStat.getJobId());
			q.put("session_id", jobStat.getSessionId());

			if (!updateMap.containsKey(q)) {
				updateMap.put(q, new BasicDBObject().append("crawl_count", 0).append("task_count", 0).append("err_count", 0)
						.append("meta_count", 0));
			}

			DBObject data = updateMap.get(q);
			data.put("crawl_count", (Integer) data.get("crawl_count") + jobStat.getCrawlCount());
			data.put("task_count", (Integer) data.get("task_count") + jobStat.getTaskCount());
			data.put("err_count", (Integer) data.get("err_count") + jobStat.getErrCount());
			data.put("meta_count", (Integer) data.get("meta_count") + jobStat.getMetaCount());
		}

		for (DBObject q : updateMap.keySet()) {
			String jobId = (String) q.get("job_id");
			String sessionId = (String) q.get("session_id");

			int crawlCount = (Integer) updateMap.get(q).get("crawl_count");
			int taskCount = (Integer) updateMap.get(q).get("task_count");
			int errCount = (Integer) updateMap.get(q).get("err_count");
			int metaCount = (Integer) updateMap.get(q).get("meta_count");

			JobStat jobStat = jobStatDao.findJobStat(jobId, sessionId);
			if (jobStat != null) {
				DBObject udata = new BasicDBObject();
				udata.put("crawl_count", jobStat.getCrawlCount() + crawlCount);
				udata.put("task_count", jobStat.getTaskCount() + taskCount);
				udata.put("err_count", jobStat.getErrCount() + errCount);
				udata.put("meta_count", jobStat.getMetaCount() + metaCount);
				jobStatDao.update(udata, q);
			}
		}
	}

	public void increaseCrawlCount(String jobId, String sessionId) {
		JOB_STAT_QUEUE.add(new JobStat(jobId, sessionId, 1, 0, 0, 0));
	}

	public void increaseTaskCount(String jobId, String sessionId) {
		JOB_STAT_QUEUE.add(new JobStat(jobId, sessionId, 0, 1, 0, 0));
	}

	public void increaseErrCount(String jobId, String sessionId) {
		JOB_STAT_QUEUE.add(new JobStat(jobId, sessionId, 0, 0, 1, 0));
	}

	public void increaseMetaCount(String jobId, String sessionId) {
		JOB_STAT_QUEUE.add(new JobStat(jobId, sessionId, 0, 0, 0, 1));
	}

	public void reloadJobs() {
		jobMap = toMap(jobDao.findAll());
	}

	private Map<String, Job> toMap(List<Job> jobs) {
		Map<String, Job> ret = new HashMap<String, Job>();
		for (Job job : jobs) {
			ret.put(job.getId(), job);
		}
		return ret;
	}

	public int getJobStatus(String jobId) {
		if (jobMap.containsKey(jobId)) {
			return jobMap.get(jobId).getStatus();
		}
		return 0;
	}

	public Job getJobById(String jobId) {
		if (jobMap.containsKey(jobId)) {
			return jobMap.get(jobId);
		}
		return null;
	}

	public boolean isJobRunning(String jobId) {
		if (jobMap.containsKey(jobId) && jobMap.get(jobId).getStatus() == Job.RUNNING) {
			return true;
		}
		return false;
	}

	public boolean hasJobRunning(String root){
		if(root == null){
			return false;
		}
		if(jobMap == null){
			return false;
		}
		for(Job job : jobMap.values()){
			if(job.getRoot().equals(root) && job.getStatus() == Job.RUNNING){
				return true;
			}
		}
		return false;
	}

	public boolean isJobSuspend(String jobId) {
		if (jobMap.containsKey(jobId) && jobMap.get(jobId).getStatus() == Job.SUSPEND) {
			return true;
		}
		return false;
	}

	public boolean isWatingCrawlJob(String jobId){
		return hasPendUrlJobs.size() > 0 && hasPendUrlJobs.contains(jobId);
	}

	public void cleanDeadJob(String jobId) {
		urlsService.deleteUrlsByJobId(jobId);
		urlsService.deletePendsByJobId(jobId);
	}

	public void cleanMetadata(String jobId) {
		metadataDao.delete(new BasicDBObject("job_id", jobId));
	}

	public List<Job> findAllRunningJobs() {
		DBObject q = new BasicDBObject();
		q.put("status", Job.RUNNING);
		return this.jobDao.findAll(q);
	}

	public List<Job> findAllJobs() {
		return this.jobDao.findAll();
	}

	public boolean isJobActive(String jobId) {
		Job job = this.jobDao.findById(jobId);
		if (job == null || job.getStatus() == Job.STOPPING) {
			return false;
		}

		int tryCount = 0;
		while (true) {
			List<Urls> urlsList = urlsService.findUrlsByJobId(jobId, 1);
			if (urlsList.size() > 0) {
				return true;
			}

			List<Pends> pendsList = urlsService.findPendsByJobId(jobId, 1);
			if (pendsList.size() > 0) {
				return true;
			}

			tryCount++;
			if (tryCount >= 3) {
				return false;
			}

			try {
				Thread.sleep(60 * 1000);
				continue;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 开始任务
	 *
	 * @param jobId
	 */
	public void startJob(String jobId) {
		String sessionId = ObjectId.get().toString();
		// 更新这个任务的所有页面为正常
		// this.pageDao.updatePageNormalByJobId(jobId);

		DBObject data = new BasicDBObject();
		data.put("status", Job.RUNNING);
		// 产生一个本次运行的标志ID
		data.put("session_id", sessionId);
		data.put("start_date", new Date());
		data.put("end_date", null);
		jobDao.updateById(data, jobId);
		this.reloadJobs();

		// 日志记录
		jobStatService.startJobStat(jobId, sessionId);
		Iterator<Seed> iter = seedDao.iteratorSeedByJobId(jobId, Seed.StatusEnum.ENABLE);
		while(iter.hasNext()) {
			Seed sed = iter.next();
			if(!sed.getIsTemplate()) {
				urlsService.addUrlsCrawlInit(sed.getUrl(), null, sed.getCharset(), jobId, sessionId, DataUtil.stringToMap(sed.getParams(), ",", ":"));
			}
		}

	}

	/**
	 * 任务停止
	 *
	 * @param jobId
	 */
	public void stopJob(String jobId) {
		Job job = this.getJobById(jobId);
		urlsService.deletePendsByJobId(jobId);
		urlsService.deleteUrlsByJobId(jobId);

		DBObject data = new BasicDBObject();
		data.put("status", Job.STOPPING);
		data.put("end_date", new Date());
		jobDao.updateById(data, jobId);
		reloadJobs();

		jobStatService.endJobStat(job.getId(), job.getSessionId());
	}

	/**
	 * 任务完成
	 *
	 * @param jobId
	 */
	public void doneJob(String jobId) {
		Job job = this.getJobById(jobId);

		urlsService.deletePendsByJobId(jobId);
		urlsService.deleteUrlsByJobId(jobId);

		DBObject data = new BasicDBObject();
		data.put("status", Job.DONE);
		data.put("end_date", new Date());
		jobDao.updateById(data, jobId);
		reloadJobs();

		jobStatService.endJobStat(job.getId(), job.getSessionId());
	}

	/**
	 * 任务暂停
	 *
	 * @param jobId
	 */
	public void suspendJob(String jobId) {
		DBObject data = new BasicDBObject();
		data.put("status", Job.SUSPEND);
		jobDao.updateById(data, jobId);
	}

	/**
	 * 任务重启
	 * @param jobId
	 */
	public void restartJob(String jobId) {
		DBObject data = new BasicDBObject();
		data.put("status", Job.RUNNING);
		jobDao.updateById(data, jobId);
	}

	public Map<String, Job> getJobMap() {
		return jobMap;
	}

}
