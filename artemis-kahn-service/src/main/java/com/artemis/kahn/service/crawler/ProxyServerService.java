package com.artemis.kahn.service.crawler;

import com.artemis.kahn.util.DataUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


@Service
public class ProxyServerService {
	private static ProxyServerService INSTANCE = null;
	private static Map<ProxyKey, ProxyServerBo> PROXY_SERVERS = new Hashtable<ProxyKey, ProxyServerBo>();

	/**
	 * 开始使用
	 *
	 * @param proxyKey
	 * @param root
	 */
	public synchronized void startUsing(ProxyKey proxyKey, String root) {
		ProxyServerBo server = PROXY_SERVERS.get(proxyKey);
		Map<String, Long> visitMap = (server.getRootLastVisitTime() == null ? new HashMap<String, Long>() : server.getRootLastVisitTime());
		visitMap.put(root, System.currentTimeMillis());
		server.setRootLastVisitTime(visitMap);
		server.setCurrentConnectThread(server.getCurrentConnectThread() + 1);
		PROXY_SERVERS.put(proxyKey, server);
	}

	/**
	 * 代理使用完成
	 *
	 * @param proxyKey
	 * @param root
	 */
	public synchronized void finish(ProxyKey proxyKey, String root) {
		ProxyServerBo server = PROXY_SERVERS.get(proxyKey);
		server.setCurrentConnectThread(server.getCurrentConnectThread() - 1);
	}


	/**
	 * 选择一个最优的代理服务器
	 *
	 * @param url
	 * @return
	 */
	public synchronized ProxyServerBo selectionOptimal(String url) {
		String root = DataUtil.convertUrlToRoot(url);
		return null;
	}

}
