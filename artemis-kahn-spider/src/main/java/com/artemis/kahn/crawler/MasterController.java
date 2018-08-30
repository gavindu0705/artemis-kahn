package com.artemis.kahn.crawler;

import com.artemis.kahn.service.UrlsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 爬虫程序入口
 *
 * @author duxiaoyu
 *
 */

@Component
public class MasterController extends HttpServlet {

	public static final Logger LOG = LoggerFactory.getLogger(MasterController.class);

	@Autowired
	private UrlsService urlsService;


	public void init(ServletConfig config) throws ServletException {
//		LOG.print("--------------------------------###--------------------------------------------------");
//		LOG.print("spiders starting " + new Date());
//		Map<String, String> propertiesMap = SpidersConfig.getInstance().getPropertiesAsMap();
//		for (String key : propertiesMap.keySet()) {
//			LOG.print("\t" + key + " : " + propertiesMap.get(key));
//		}
//		LOG.print("spiders started " + new Date());
//		LOG.print("--------------------------------###--------------------------------------------------");
		platformCleaner();
		initSpidersThreads();
	}

	private void platformCleaner() {
		urlsService.updateUrlsCrawlQueToInit();
	}

	private void initSpidersThreads() {
		// 收集目标
		GoalCollector goaslCollector = new GoalCollector();
		goaslCollector.start();

		// 收集成果
		HarvestCollector harvestThread = new HarvestCollector();
		harvestThread.start();

		// 蜘蛛抓取
		MasterSpider spiders = new MasterSpider();
		spiders.start();

		// 任务通知
		AdvisorThread advisor = new AdvisorThread();
		advisor.start();

		// 抓取处理
		DoctorThread doctor = new DoctorThread();
		doctor.start();

		// 定时器
		TimerThread timer = new TimerThread();
		timer.start();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
//		LOG.print("[service OK]");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}

	public void destroy() {

	}
}
