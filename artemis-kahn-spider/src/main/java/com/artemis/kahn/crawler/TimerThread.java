package com.artemis.kahn.crawler;

import com.artemis.kahn.component.AsyncThread;
import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时器
 *
 * @author xiaoyu
 *
 */

@Component
public class TimerThread extends Thread {
	public static final Logger LOG = LoggerFactory.getLogger(TimerThread.class);


	private JobService jobService;

	@Override
	public void run() {
		new AsyncThread(60 * 1000, new AsyncThread.Call() {
			@Override
			public void invoke() {
				List<Job> jobs = jobService.findAllJobs();
				for (Job job : jobs) {
					//判断job是否已经到了该执行的时候了
					if (job.getInterval() > 0 && (job.getStatus() == Job.DONE || job.getStatus() == Job.STOPPING) && isJobTimeUp(job)) {
//						LOG.print("job {} {} will starting ...", new Object[] { job.getId(), job.getName() });
						jobService.startJob(job.getId());
					}
				}
			}
		});
	}

	private boolean isJobTimeUp(Job job) {
		if (job.getStartDate() == null) {
			return false;
		}
		long diff = System.currentTimeMillis() - job.getStartDate().getTime();
//		LOG.print("job:{},diff:{},interval:{},last:{}", new Object[]{job.getId(), diff, job.getInterval(), DateUtil.formatToTightYYYYMMDDhhmmss(job.getStartDate())});
		if (diff > (long)(job.getInterval() * 1000L)) {
			return true;
		}
		return false;
	}

}
