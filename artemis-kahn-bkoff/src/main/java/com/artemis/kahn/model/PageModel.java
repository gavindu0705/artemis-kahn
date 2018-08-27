package com.artemis.kahn.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PageModel {

    private String id;

    @NotBlank
    private String jobId;

    @NotBlank
    private String name;

    @NotNull
    private Long expires;
    private String patternStr;
    private String errTagStr;
    private String sucTagStr;

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

    public String getPatternStr() {
        return patternStr;
    }

    public void setPatternStr(String patternStr) {
        this.patternStr = patternStr;
    }

    public String getErrTagStr() {
        return errTagStr;
    }

    public void setErrTagStr(String errTagStr) {
        this.errTagStr = errTagStr;
    }

    public String getSucTagStr() {
        return sucTagStr;
    }

    public void setSucTagStr(String sucTagStr) {
        this.sucTagStr = sucTagStr;
    }
}
