package com.example.downloadvideo.minioConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public class MinioProp {
    public static final String MINIO_BUCKET = "video";

    private String endpoint="http://localhost:9293";//连接url
    public String getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    public String getAccesskey() {
        return accesskey;
    }
    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }
    public String getSecretKey() {
        return secretKey;
    }
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    private String accesskey="root";//用户名
    private String secretKey="password";//密码

    
}
