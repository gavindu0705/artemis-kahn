package com.artemis.kahn.spider;

import com.artemis.kahn.core.bean.Harvest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 抓取成果持有器
 *
 * @author xiaoyu
 *
 */
public class HarvestHolder {
	public static final Logger LOG = LoggerFactory.getLogger(HarvestHolder.class);

	private BlockingQueue<Harvest> harvestQueue = new LinkedBlockingQueue<Harvest>(20000);
	private static HarvestHolder instance = new HarvestHolder();

	private HarvestHolder() {

	}

	public static HarvestHolder getInstance() {
		return instance;
	}

	public void put(Harvest harvest) {
		try {
			harvestQueue.put(harvest);
		} catch (InterruptedException e) {
			LOG.error("put error. size:" + harvestQueue.size(), e);
		}
	}

	public Harvest get() {
		try {
			return harvestQueue.take();
		} catch (InterruptedException e) {
			LOG.error("take error. size:" + harvestQueue.size(), e);
		}
		return null;
	}

	public int size() {
		return harvestQueue.size();
	}
}
