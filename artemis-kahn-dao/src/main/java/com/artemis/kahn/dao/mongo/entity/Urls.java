package com.artemis.kahn.dao.mongo.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

@Document(collection = "urls")
public class Urls {

    public enum StatusEnum {
        CRAWL_INIT(0), //
        CRAWL_QUE(1), //
        CRAWL_SUC(2), //
        CRAWL_ERR(3), //
        CRAWL_400(4), //
        CRAWL_500(5), //

        TASK_INIT(6), //
        TASK_QUE(7), //
        TASK_SUC(8), //
        TASK_ERR(9), //
        TASK_NO_JOB(10), //
        TASK_NO_PAGE(11), //
        TASK_NO_TASK(12), //
        TASK_NO_FILE(13), //
        ;
        private final int status;

        StatusEnum(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public static StatusEnum parseUrlsStatus(int status) {
            for (StatusEnum st : StatusEnum.values()) {
                if (status == st.getStatus()) {
                    return st;
                }
            }
            return null;
        }
    }

    @Id
    private String id;
    private int status; // 0-待抓取 1-抓取队列中 2-抓取成功 3-抓取错误 4-400错误 5-500错误 6-等待任务处理

    @Field("job_id")
    private String jobId;
    private String charset;
    private String referer;
    private int errors;

    @Field("status_code")
    private int statusCode; // URL请求状态码
    private Map<String, String> params;

    @Field("c_date")
    private Date creationDate;
    private String root;

    @Field("session_id")
    private String sessionId;

    @Field("seed_id")
    private String seedId;
    private int priority;// 优先级 0-5递增,数字越大优先级越高

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSeedId() {
        return seedId;
    }

    public void setSeedId(String seedId) {
        this.seedId = seedId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
