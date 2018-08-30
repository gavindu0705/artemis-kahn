package com.artemis.kahn.crawler;

import com.artemis.kahn.component.AsyncThread;
import com.artemis.kahn.core.bean.Harvest;
import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.Urls;
import com.artemis.kahn.service.JobService;
import com.artemis.kahn.service.UrlsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DoctorThread extends Thread {
	public static final Logger LOG = LoggerFactory.getLogger(DoctorThread.class);

	@Autowired
	private JobService jobService;

	@Autowired
	private UrlsService urlsService;

	// 最多重试次数
	public static final int MAX_RETRY_COUNT = 3;



	@Override
	public void run() {
		// 出错重试
		new AsyncThread("retry", 3 * 1000, new AsyncThread.Call() {
			@Override
			public void invoke() {
				retryErrors();
			}
		});

		// 检测任务是否已经完成
		new AsyncThread(10 * 1000, new AsyncThread.Call() {
			@Override
			public void invoke() {
				List<Job> jobs = jobService.findAllRunningJobs();
				for (Job job : jobs) {
//					// 如果该任务永久运行
//					if (job.isNonStop()) {
//						continue;
//					}
					// 是否是活跃任务
					if (!jobService.isJobActive(job.getId())) {
						jobService.doneJob(job.getId());
						continue;
					}

					// 是否超出设定运行时长
					if (isWorktime(job)) {
//						LOG.print("[job-stop] {}-{} out of date. so stopped it", new Object[] { job.getId(), job.getName() });
						jobService.doneJob(job.getId());
						continue;
					}
				}
			}
		});

		// 处理未运行的任务
		new AsyncThread(60 * 1000, new AsyncThread.Call() {
			@Override
			public void invoke() {
				List<Job> jobs = jobService.findAllJobs();
				for (Job job : jobs) {
					// 清理没有运行的任务
					if (!jobService.isJobRunning(job.getId()) && !jobService.isJobSuspend(job.getId())) {
						jobService.cleanDeadJob(job.getId());
					}
				}
			}
		});

	}

	/**
	 * 是否已经达到设定时间上限
	 *
	 * @param job
	 * @return
	 */
	private boolean isWorktime(Job job) {
		if (job.getWorktime() <= 0) {
			return false;
		}

		if (new Date().getTime() - job.getStartDate().getTime() > job.getWorktime() * 1000) {
			return true;
		}

		return false;
	}

	/**
	 * 出错重试
	 */
	private void retryErrors() {
		List<Urls> urlsList = urlsService.findUrlsCrawlErr(1000);
		if (urlsList.size() == 0) {
			return;
		}

		List<String> deleteIdList = new ArrayList<String>();
		for (Urls urls : urlsList) {
			int maxRetryCount = MAX_RETRY_COUNT;
			// 如果状态码是404，最多重试3次
			if (urls.getStatusCode() == Harvest.StatusEnum.NOTFOUND4.getCode()) {
				maxRetryCount = 3;
			}

			// 如果大于最大出错次数，舍弃
			if (urls.getErrors() > maxRetryCount) {
				deleteIdList.add(urls.getId());
			} else {
				urlsService.updateUrlsCrawlErrToInit(urls.getId(), urls.getErrors());
			}
		}
		// 批量删除
		urlsService.deleteUrls(deleteIdList);
	}
}
