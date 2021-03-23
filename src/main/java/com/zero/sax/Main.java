package com.zero.sax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = {"com.zero.sax"})
@SpringBootApplication
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Main.class, args);
        logger.info("Application start with {}beans: {}", applicationContext.getBeanDefinitionCount(), applicationContext.getBeanDefinitionNames());
    }
}
