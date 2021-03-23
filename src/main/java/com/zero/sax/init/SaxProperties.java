package com.zero.sax.init;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sax")
public class SaxProperties {
    // sax.root
    private String root;
    // sax.max-file-size
    private long maxFileSize;
    // sax.domain
    private String domain;
    // sax.protocol
    private String protocol;

    public SaxProperties get() {
        return this;
    }

    public String getRoot() {
        if(root.endsWith("/")) {
            root = root.substring(0, root.length()-1);
        }
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
