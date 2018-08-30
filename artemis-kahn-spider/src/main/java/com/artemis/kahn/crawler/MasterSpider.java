package com.artemis.kahn.crawler;

import com.artemis.kahn.component.AsyncThread;
import com.artemis.kahn.service.crawler.NginxProxyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 抓取目标分发控制线程
 *
 * @author xiaoyu
 *
 */

@Component
public class MasterSpider extends Thread {
	public static final Logger LOG = LoggerFactory.getLogger(MasterSpider.class);

	private static final int STAT_SKIP_SECS = 30;

	@Override
	public void run() {
		if (STAT_SKIP_SECS > 0) {
			new AsyncThread(STAT_SKIP_SECS * 1000, new AsyncThread.Call() {
				@Override
				public void invoke() {
//					double spiderSpeed = roundDouble((double) SpidersThread.M_SUC_INC.get() / (double) STAT_SKIP_SECS);
//					double collectorSpeed = roundDouble((double) GoalCollector.GOALS_COUNT.get() / (double) STAT_SKIP_SECS);
//					LOG.print(
//							"[{}/{}/{}] {}/{}/{} spider:{}/s collector:{}/s goal:{} harv:{} road:{} us:{} ue:{}",
//							new Object[] { SpidersThread.SUC_INC.get(), SpidersThread.ERR_INC.get(),
//									SpidersThread.TOTAL_INC.get(), SpidersThread.M_SUC_INC.get(), SpidersThread.M_ERR_INC.get(),
//									SpidersThread.M_TOTAL_INC.get(), spiderSpeed, collectorSpeed,
//									GoalHolder.getInstance().size(), HarvestHolder.getInstance().size(),
//									UrlRoadService.getInstance().getUrlRoadQueueSize(), UrlsService.URLS_CRAWL_SUC_QUEUE.size(),
//									UrlsService.URLS_CRAWL_ERR_QUEUE.size() });
					SpidersThread.M_TOTAL_INC.set(0);
					SpidersThread.M_ERR_INC.set(0);
					SpidersThread.M_SUC_INC.set(0);
					GoalCollector.GOALS_COUNT.set(0);
				}
			});
		}

		for (int i = 1000; i < 1000 + 3; i++) {
			SpidersThread thread = new SpidersThread(new NginxProxyImpl());
			thread.setName("S-" + i);
			thread.setDaemon(true);
//			LOG.print("starting " + thread.getName() + "  ...");
			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			thread.start();
		}

	}

	private double roundDouble(double d) {
		return new BigDecimal(d).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
