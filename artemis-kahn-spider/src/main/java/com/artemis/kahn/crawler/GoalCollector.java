package com.artemis.kahn.crawler;

import com.artemis.kahn.core.bean.Goal;
import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.SiteConfig;
import com.artemis.kahn.dao.mongo.entity.Urls;
import com.artemis.kahn.service.JobService;
import com.artemis.kahn.service.SiteConfigService;
import com.artemis.kahn.service.UrlsService;
import com.artemis.kahn.spider.GoalHolder;
import com.artemis.kahn.spider.PriorityEnum;
import com.artemis.kahn.util.DataUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 抓取目标收集线程
 *
 * @author xiaoyu
 */

@Component
public class GoalCollector extends Thread {
    public static final Logger LOG = LoggerFactory.getLogger(GoalCollector.class);

    private GoalHolder goalsHolder = GoalHolder.getInstance();

    @Autowired
    private JobService jobService;
    @Autowired
    private UrlsService urlsService;
    @Autowired
    private SiteConfigService siteConfigService;

    private static final int BATCH_COUNT = 100;

    public static final AtomicLong GOALS_COUNT = new AtomicLong(0);

    public void run() {
        while (true) {
            try {
                // 如果goals holder里面的数据超过50%，则放慢获取速度
                if (goalsHolder.size() >= GoalHolder.GOALS_HOLDER_SIZE * 0.5) {
                    Thread.sleep(1 * 1000);
                    continue;
                }

                List<Urls> list = fetchUrls();
                if (list.size() <= 0) {
                    Thread.sleep(3 * 1000);
                } else {
                    List<Urls> goalsList = new ArrayList<Urls>(list.size());
                    for (Urls u : list) {
                        if (!jobService.isJobRunning(u.getJobId())) {
                            continue;
                        }
                        goalsList.add(u);
						/*这段逻辑不需要了
						// 检查是否有缓存页面
						if (fileDataService.isExpired(u.getId(), u.getJobId())) {
							goalsList.add(u);
						} else {
							urlsService.updateUrlsCrawlSuc(u.getId(), u.getJobId(), u.getSessionId(), u.getReferer(), false);
						}*/
                    }

                    List<String> goalsIdList = new ArrayList<String>();
                    CollectionUtils.collect(goalsList, new Transformer() {
                        @Override
                        public String transform(Object input) {
                            return ((Urls) input).getId();
                        }
                    }, goalsIdList);

                    urlsService.updateUrlsCrawlQue(goalsIdList);
                    for (Urls u : goalsList) {
                        goalsHolder.put(new Goal(u.getJobId(), u.getSessionId(), u.getId(), u.getCharset(), u.getReferer()));
                        GOALS_COUNT.incrementAndGet();
                    }
                }

            } catch (InterruptedException e) {
                LOG.error("", e);
            }
        }
    }

    public static void main(String[] args) {
        GoalCollector sd = new GoalCollector();
        sd.run();
    }

    @SuppressWarnings("unchecked")
    private List<Urls> fetchUrls() {
        Map<String, SiteConfig> cfgMap = siteConfigService.getSiteConfigMap();
        int runningSize = getRunningJobSize();
        if (runningSize == 0) {
            return Collections.EMPTY_LIST;
        }

        int limit = (BATCH_COUNT / runningSize <= 0) ? 1 : (BATCH_COUNT / runningSize);

        Map<String, List<Urls>> urlsMap = new HashMap<String, List<Urls>>();
        for (PriorityEnum priority : new PriorityEnum[]{PriorityEnum.HIGHEST, PriorityEnum.HIGH, PriorityEnum.NORMAL}) {
            List<String> roots = new ArrayList<String>(cfgMap.keySet());
            for (String root : roots) {
                List<Urls> urlsList = fetchUrls(priority, cfgMap.get(root), limit);
                if (urlsList.size() > 0) {
                    urlsMap.put(root, urlsList);
                    cfgMap.remove(root);
                }
            }
        }

        List<Urls> ret = new ArrayList<Urls>();
        for (String root : urlsMap.keySet()) {
            ret = DataUtil.combineList(ret, urlsMap.get(root));
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    private List<Urls> fetchUrls(PriorityEnum priority, SiteConfig cfg, int limit) {
        if (priority == null || cfg == null) {
            return Collections.EMPTY_LIST;
        }

        // 该优先级的任务列表
        List<Job> jobs = this.getRunningJobs(cfg.getRoot(), priority);
        if (jobs.size() <= 0) {
            return Collections.EMPTY_LIST;
        }

        int batchSize = (int) (limit / jobs.size());

        List<Urls> ret = new ArrayList<Urls>();
        for (Job job : jobs) {
            List<Urls> list = urlsService.findUrlsByJobId(job.getId(), Urls.StatusEnum.CRAWL_INIT, (batchSize <= 3 ? 3 : batchSize));
            ret = DataUtil.combineList(ret, list);
        }

        return ret;
    }

    private int getRunningJobSize() {
        int ret = 0;
        Map<String, Job> jobMap = jobService.getJobMap();
        for (String jobId : jobMap.keySet()) {
            if (jobMap.get(jobId).getStatus() == Job.RUNNING) {
                ret++;
            }
        }
        return ret;
    }

    private List<Job> getRunningJobs(String root, PriorityEnum priority) {
        List<Job> ret = new ArrayList<Job>();
        Map<String, Job> jobMap = jobService.getJobMap();
        for (String jobId : jobMap.keySet()) {
            Job job = jobMap.get(jobId);
            if (job.getStatus() == Job.RUNNING && job.getRoot().equals(root) && job.getPriority() == priority.getPriority()) {
                ret.add(jobMap.get(jobId));
            }
        }
        return ret;
    }

}
