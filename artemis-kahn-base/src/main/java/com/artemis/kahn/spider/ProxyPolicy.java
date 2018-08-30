package com.artemis.kahn.spider;


import com.artemis.kahn.core.bean.Goal;
import com.artemis.kahn.core.bean.Harvest;

import java.io.IOException;

/**
 * 抓取实现策略
 * 
 * @author duxiaoyu
 * 
 */
public interface ProxyPolicy {

	public Harvest apply(Goal goal) throws IOException;
}
