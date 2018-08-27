package com.artemis.kahn.dao.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = "page")
public class Page {

    @Id
    private String id;

    @Field("job_id")
    private String jobId;
    private String name;
    private long expires;
    private List<String> patterns;

    @Field("err_tags")
    private List<String> errTags;

    @Field("suc_tags")
    private List<String> sucTags;
    private int status;

    @Field("c_date")
    private Date creationDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public List<String> getErrTags() {
        return errTags;
    }

    public void setErrTags(List<String> errTags) {
        this.errTags = errTags;
    }

    public List<String> getSucTags() {
        return sucTags;
    }

    public void setSucTags(List<String> sucTags) {
        this.sucTags = sucTags;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
