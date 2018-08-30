package com.artemis.kahn.listener;

import com.artemis.kahn.analysis.PendsCollector;
import com.artemis.kahn.analysis.TaskerThread;
import com.artemis.kahn.service.UrlsService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AnalysisStartupListener implements ServletContextListener {

    @Autowired
    UrlsService urlsService;

    @Autowired
    PendsCollector pendsCollector;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        urlsService.updatePendsTaskQueToInit();
        pendsCollector.start();
        for (int i = 101; i <= 100 + 3; i++) {
            Thread thread = new Thread(new TaskerThread());
            thread.setName("T-" + i);
            thread.start();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
