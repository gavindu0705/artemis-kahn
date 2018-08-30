package com.artemis.kahn.crawler;

import com.artemis.kahn.core.bean.Goal;
import com.artemis.kahn.core.bean.Harvest;
import com.artemis.kahn.spider.GoalHolder;
import com.artemis.kahn.spider.HarvestHolder;
import com.artemis.kahn.spider.ProxyPolicy;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 蜘蛛执行线程
 *
 * @author duxiaoyu
 *
 */
public class SpidersThread extends Thread {
	public static final Logger LOG = LoggerFactory.getLogger(SpidersThread.class);

	private ProxyPolicy proxyPolicy;
	private GoalHolder goalsHolder = GoalHolder.getInstance();
	private HarvestHolder harvestHolder = HarvestHolder.getInstance();

	// private static final long START_MILLS = System.currentTimeMillis() - 1;

	public static final AtomicLong TOTAL_INC = new AtomicLong(0);
	public static final AtomicLong SUC_INC = new AtomicLong(0);
	public static final AtomicLong ERR_INC = new AtomicLong(0);

	public static final AtomicLong M_TOTAL_INC = new AtomicLong(0);
	public static final AtomicLong M_SUC_INC = new AtomicLong(0);
	public static final AtomicLong M_ERR_INC = new AtomicLong(0);

	// private static final int STAT_SKIP_SIZE =
	// SpidersConfig.getInstance().getStatLogSkipSize();
	private static final boolean IS_ENABLE_DETAILLOG = false;

	public SpidersThread(ProxyPolicy proxyPolicy) {
		this.proxyPolicy = proxyPolicy;
	}

	@Override
	public void run() {
		while (true) {
			Goal goal = goalsHolder.get();
			if (goal == null) {
				try {
					Thread.sleep(10 + RandomUtils.nextInt(1, 90));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}

			long lstart = System.currentTimeMillis();
			Harvest har = null;
			try {
				har = proxyPolicy.apply(goal);
			} catch (IOException e) {
				LOG.error("single goal proxy error ", e);
			} finally {

			}

			long secs = System.currentTimeMillis() - lstart;
			if (har != null) {
				if (har.getStatusCode() == Harvest.StatusEnum.SUCCESS.getCode()) {
					SUC_INC.incrementAndGet();
					M_SUC_INC.incrementAndGet();
				} else if (har.getStatusCode() == Harvest.StatusEnum.ERROR.getCode()) {
					ERR_INC.incrementAndGet();
					M_ERR_INC.incrementAndGet();
				} else {
					ERR_INC.incrementAndGet();
					M_ERR_INC.incrementAndGet();
				}
				// 放入成果收集器
				harvestHolder.put(har);

				if (IS_ENABLE_DETAILLOG || har.getStatusCode() != Harvest.StatusEnum.SUCCESS.getCode()) {
//					LOG.print("[{}] {} {}ms {}", new Object[] { har.getStatusCode(), har.getCaptor(), secs, har.getUrl() });
				}
			} else {
				ERR_INC.incrementAndGet();
				M_ERR_INC.incrementAndGet();
				harvestHolder.put(new Harvest(goal.getJobId(), goal.getSessionId(), goal.getUrl(), Harvest.StatusEnum.ERROR
						.getCode()));
			}

			M_TOTAL_INC.incrementAndGet();
			TOTAL_INC.incrementAndGet();
			// if (TOTAL_INC.get() % STAT_SKIP_SIZE == 0) {
			// long currMills = System.currentTimeMillis();
			// double sp = roundDouble((double) SUC_INC.get() / (double)
			// ((currMills - START_MILLS) / 1000));
			// LOG.print("{}/{}(T/F) {}/s {} goal:{} har:{}", new Object[] {
			// SUC_INC.get(), ERR_INC.get(), sp, TOTAL_INC.get(),
			// goalsHolder.size(), harvestHolder.size() });
			// }
		}
	}
}
