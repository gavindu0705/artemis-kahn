package com.artemis.kahn.config;

import org.springframework.core.env.Environment;

public class PropertiesBean {

    private static PropertiesBean INSTANCE = null;
    private Environment environment;

    PropertiesBean(Environment environment) {
        this.environment = environment;
    }

    public static PropertiesBean initialize(Environment environment) {
        if (INSTANCE == null) {
            INSTANCE = new PropertiesBean(environment);
        }
        return INSTANCE;
    }

    public static PropertiesBean getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("PropertiesBean has not been initialized");
        }
        return INSTANCE;
    }

    public Integer getIntegerValue(String key) {
        return environment.getProperty(key, Integer.class);
    }

    public Integer getIntegerValue(String key, Integer defaultValue) {
        return environment.getProperty(key, Integer.class, defaultValue);
    }


    public String getStringValue(String key) {
        return environment.getProperty(key, String.class);
    }

    public String getStringValue(String key, String defaultValue) {
        return environment.getProperty(key, String.class, defaultValue);
    }

}
