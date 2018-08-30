package com.artemis.kahn.service;

import com.artemis.kahn.component.AsyncThread;
import com.artemis.kahn.dao.mongo.entity.SiteConfig;
import com.artemis.kahn.dao.mongo.repo.SiteConfigDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SiteConfigService {

	@Autowired
	private SiteConfigDao siteConfigDao;


	private Map<String, SiteConfig> siteConfigMap = new HashMap<String, SiteConfig>();

	public static final String[] ROOT_DOMAINS = { ".com.cn", ".net.cn", ".org.cn", ".gov.cn", ".com", ".cn", ".co", ".net",
			".org", ".me", ".biz", ".name", ".info", ".so", ".tel", ".mobi", ".asia", ".cc", ".tv", ".公司", ".网络", ".中国" };

	private SiteConfigService() {
//		siteConfigMap = toMap(siteConfigDao.findAll());
//		new AsyncThread(1000 * 10, new AsyncThread.Call() {
//			@Override
//			public void invoke() {
//				siteConfigMap = toMap(siteConfigDao.findAll());
//			}
//		});
	}

	public SiteConfig getSiteConfigByRoot(String root) {
		return this.siteConfigMap.get(root);
	}

	private Map<String, SiteConfig> toMap(List<SiteConfig> siteConfigs) {
		Map<String, SiteConfig> ret = new LinkedHashMap<String, SiteConfig>();
		for (SiteConfig siteConfig : siteConfigs) {
			ret.put(siteConfig.getRoot(), siteConfig);
		}
		return ret;
	}

	public Map<String, SiteConfig> getSiteConfigMap() {
		return new HashMap<String, SiteConfig>(siteConfigMap);
	}

//	public List<String> getAllRoots() {
//		List<SiteConfig> siteConfigs = getAllSiteConfig();
//		Collections.sort(siteConfigs, new Comparator<SiteConfig>() {
//			@Override
//			public int compare(SiteConfig o1, SiteConfig o2) {
//				if(o1.getShotRate() > o2.getShotRate()) {
//					return -1;
//				}else if(o1.getShotRate() < o2.getShotRate()) {
//					return 1;
//				}else {
//					if(o1.getCreationDate().after(o2.getCreationDate())) {
//						return 1;
//					}else if(o1.getCreationDate().before(o2.getCreationDate())) {
//						return -1;
//					}
//				}
//				return 0;
//			}
//		});
//
//		List<String> ret = new ArrayList<String>();
//		for(SiteConfig cfg : siteConfigs) {
//			ret.add(cfg.getRoot());
//		}
//
//		return ret;
//	}

	public List<SiteConfig> getAllSiteConfig() {
		return new ArrayList<SiteConfig>(this.getSiteConfigMap().values());
	}

	public SiteConfig parseUrl(String url) {
		String s = StringUtils.substringAfter(url, "http://");
		s = StringUtils.substringBefore(s, "/");

		String root = null;
		for (String r : ROOT_DOMAINS) {
			if (s.endsWith(r)) {
				s = StringUtils.substringBeforeLast(s, r);
				s = StringUtils.substringAfterLast(s, ".");
				root = s + r;
				break;
			}
		}
		if (root == null) {
			return null;
		}

		return getSiteConfigByRoot(root);
	}

}
