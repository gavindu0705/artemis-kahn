package com.artemis.kahn;

import com.artemis.kahn.config.PropertiesPreparedListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.artemis.kahn.dao.mapper")
public class BkoffApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BkoffApplication.class);

        //添加一个配置文件初始化的监听器
        application.addListeners(new PropertiesPreparedListener());
        application.run(args);
    }

}
