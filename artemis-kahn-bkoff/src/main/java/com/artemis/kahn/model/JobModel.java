package com.artemis.kahn.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class JobModel {

    private String id;

    @NotEmpty(message="名称不能为空")
    private String name;

    @NotNull
    private Integer interval;

    @NotNull
    private Integer worktime;

    @NotEmpty
    private String cat;

    @NotNull
    private String root;

    @NotNull
    private Integer priority;

    @NotNull
    private Integer crawlWay;//抓取方式 0-性能优先 1-抓全优先


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getWorktime() {
        return worktime;
    }

    public void setWorktime(Integer worktime) {
        this.worktime = worktime;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getCrawlWay() {
        return crawlWay;
    }

    public void setCrawlWay(Integer crawlWay) {
        this.crawlWay = crawlWay;
    }

}
