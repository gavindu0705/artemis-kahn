package com.artemis.kahn.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PageModel {

    private String id;

    @NotBlank
    private String jobId;

    @NotBlank
    private String name;

    private String patterns;

    private Long expires;
    private String errTag;
    private String sucTag;

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

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }


    public String getErrTag() {
        return errTag;
    }

    public void setErrTag(String errTag) {
        this.errTag = errTag;
    }

    public String getSucTag() {
        return sucTag;
    }

    public void setSucTag(String sucTag) {
        this.sucTag = sucTag;
    }

    public String getPatterns() {
        return patterns;
    }

    public void setPatterns(String patterns) {
        this.patterns = patterns;
    }
}
