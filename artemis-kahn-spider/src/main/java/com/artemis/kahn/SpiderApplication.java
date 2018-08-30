package com.artemis.kahn;

import com.artemis.kahn.config.PropertiesPreparedListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.artemis.kahn.dao.mapper")
@ServletComponentScan
public class SpiderApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpiderApplication.class);
        //添加一个配置文件初始化的监听器
        application.addListeners(new PropertiesPreparedListener());
        application.run(args);
    }

}
