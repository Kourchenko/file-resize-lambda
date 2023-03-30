package com.kourchenko.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    private String URL;

    private String resizeMode;

    private Integer height;

    private Integer width;

    private String bucket;

    private String key;

    private String s3AccessKey;

    private String s3SecretKey;

    private String s3Region;

    @JsonProperty("URL")
    public String getURL() {
        return this.URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @JsonProperty("resizeMode")
    public String getResizeMode() {
        return this.resizeMode;
    }

    public void setResizeMode(String resizeMode) {
        this.resizeMode = resizeMode;
    }

    @JsonProperty("height")
    public Integer getHeight() {
        return this.height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @JsonProperty("width")
    public Integer getWidth() {
        return this.width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    @JsonProperty("bucket")
    public String getBucket() {
        return this.bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @JsonProperty("key")
    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("s3AccessKey")
    public String getS3AccessKey() {
        return this.s3AccessKey;
    }

    public void setS3AccessKey(String s3AccessKey) {
        this.s3AccessKey = s3AccessKey;
    }

    @JsonProperty("s3SecretKey")
    public String getS3SecretKey() {
        return this.s3SecretKey;
    }

    public void setS3SecretKey(String s3SecretKey) {
        this.s3SecretKey = s3SecretKey;
    }

    @JsonProperty("s3Region")
    public String getS3Region() {
        return this.s3Region;
    }

    public void setS3Region(String s3Region) {
        this.s3Region = s3Region;
    }
}
