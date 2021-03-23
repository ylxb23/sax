package com.zero.sax.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

@Component
public class SaxApplicationMonitor implements ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(SaxApplicationMonitor.class);
    @Resource
    private SaxProperties saxProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        initSaxRootPath(saxProperties.getRoot());
    }

    // check Sax root path valid, r/w enable, is exist ? ok : create.
    private void initSaxRootPath(String path) {
        try {
            File root = new File(path);
            if(!root.exists()) {
                if(root.mkdirs()) {
                    root.setReadable(true);
                    root.setWritable(true);
                    root.setExecutable(false);
                }
            } else {
                root.setReadable(true);
                root.setWritable(true);
                root.setExecutable(false);
            }
            logger.info("SaxRoot init success, path:{}", path);
        } catch (Exception e) {
            logger.error("SaxRoot not valid, path:{}", path, e);
            System.exit(-1);
        }
    }
}
