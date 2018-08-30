package com.artemis.kahn.listener;

import com.artemis.kahn.crawler.AdvisorThread;
import com.artemis.kahn.crawler.DoctorThread;
import com.artemis.kahn.crawler.GoalCollector;
import com.artemis.kahn.crawler.HarvestCollector;
import com.artemis.kahn.crawler.MasterSpider;
import com.artemis.kahn.crawler.TimerThread;
import com.artemis.kahn.service.UrlsService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SpiderStartupListener implements ServletContextListener {

    @Autowired
    UrlsService urlsService;

    @Autowired
    GoalCollector goalCollector;

    @Autowired
    HarvestCollector harvestCollector;

    @Autowired
    MasterSpider masterSpider;

    @Autowired
    AdvisorThread advisorThread;

    @Autowired
    DoctorThread doctorThread;

    @Autowired
    TimerThread timerThread;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        urlsService.updateUrlsCrawlQueToInit();

        // 收集目标
        goalCollector.start();

        // 收集成果
        harvestCollector.start();

		// 蜘蛛抓取
		masterSpider.start();

		// 任务通知
		advisorThread.start();

		// 抓取处理
		doctorThread.start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
