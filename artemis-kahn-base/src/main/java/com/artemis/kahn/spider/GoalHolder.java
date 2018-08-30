package com.artemis.kahn.spider;

import com.artemis.kahn.core.bean.Goal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 抓取目标持有器
 *
 * @author xiaoyu
 *
 */
public class GoalHolder {
	public static final Logger LOG = LoggerFactory.getLogger(GoalHolder.class);


	public static final int GOALS_HOLDER_SIZE = 10000;
	private BlockingQueue<Goal> goalsQueue = new LinkedBlockingQueue<Goal>(GOALS_HOLDER_SIZE);
	private static GoalHolder instance = new GoalHolder();


	private GoalHolder() {

	}

	public static GoalHolder getInstance() {
		return instance;
	}

	public void put(Goal goal) {
		try {
			goalsQueue.put(goal);
		} catch (InterruptedException e) {
			LOG.error("put error. size:", e);
		}
	}

	public Goal get() {
		try {
			return goalsQueue.take();
		} catch (InterruptedException e) {
			LOG.error("put error. size:", e);
		}
		return null;
	}

	public int size() {
		return goalsQueue.size();
	}

}
