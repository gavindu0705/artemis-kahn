package com.artemis.kahn.analysis;

import com.artemis.kahn.dao.mongo.entity.Pends;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 抓取成果持有器
 *
 * @author xiaoyu
 *
 */

public class PendsHolder {
	private BlockingQueue<Pends> pendsQueue = new LinkedBlockingQueue<Pends>(100000);
	private static PendsHolder instance = new PendsHolder();

	private PendsHolder() {

	}

	public static PendsHolder getInstance() {
		return instance;
	}

	public void put(Pends pends) {
		try {
			pendsQueue.put(pends);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Pends get() {
		return pendsQueue.poll();
	}

	public int size() {
		return pendsQueue.size();
	}
}
