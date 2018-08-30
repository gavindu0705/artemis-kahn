package com.artemis.kahn.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MasterListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {



    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
