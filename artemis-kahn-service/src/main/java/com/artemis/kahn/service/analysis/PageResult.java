package com.artemis.kahn.service.analysis;

import com.artemis.kahn.component.InvokeShell;
import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.Metadata;
import com.artemis.kahn.dao.mongo.entity.Page;
import com.artemis.kahn.dao.mongo.entity.Urls;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dxy on 2016/5/9.
 */
public class PageResult {
    private Page page;
    private Urls url;
    private Job job;
    private List<String> hrefs;
    private Metadata metadata;

    public PageResult(Urls url, Page page, Job job) {
        this.url = url;
        this.job = job;
        this.page = page;
    }

    public Urls getUrl() {
        return url;
    }

    public Page getPage() {
        return page;
    }

    public Job getJob() {
        return job;
    }

    public void addExeHref(String href, String shell) throws IllegalAccessException, NoSuchMethodException, InstantiationException {
        if(StringUtils.isNotBlank(shell)) {
            addHref((String)InvokeShell.invoke(shell, href));
        }else {
            addHref(href);
        }
    }

    public void addHref(String href) {
        if(href != null) {
            if(hrefs == null) {
                hrefs = new ArrayList<String>();
            }
            hrefs.add(href);
        }
    }

    public void addHrefs(List<String> hrefList) {
        if(hrefList != null) {
            if(hrefs == null) {
                hrefs = new ArrayList<String>();
            }
            hrefs.addAll(hrefList);
        }
    }

    public void addExeMeta(String key, Object value, String shell) throws IllegalAccessException, NoSuchMethodException, InstantiationException {
        if((value instanceof String) && StringUtils.isNotBlank(shell)) {
            addMeta(key, InvokeShell.invoke(shell, value));
        }else {
            addMeta(key, value);
        }
    }

    public void addMeta(String key, Object value) {
        if(key != null && value != null) {
            if(metadata == null) {
                metadata = new Metadata();
                metadata.setUrl(url.getId());
                metadata.setReferer(url.getReferer());
                metadata.setSessionId(url.getSessionId());
                metadata.setJobId(job.getId());
                metadata.setCat(job.getCat());
                metadata.setData(new HashMap<String, Object>());
                metadata.setCreationDate(new Date());
            }
            metadata.getData().put(key, value);
        }
    }


    public List<Urls> getHrefs() {
        if(hrefs == null) {
            return Collections.emptyList();
        }

        List<Urls> ret = new ArrayList<Urls>(hrefs.size());
        for(String href : hrefs) {
            Urls u = new Urls();
            u.setId(href);
            u.setJobId(job.getId());
            u.setPriority(job.getPriority());
            u.setSessionId(url.getSessionId());
            u.setRoot(job.getRoot());
            u.setCharset(url.getCharset());
            u.setParams(url.getParams());
            u.setReferer(url.getId());
            ret.add(u);
        }

        return ret;
    }


    public Metadata getMetadata() {
        if(metadata == null) {
            return null;
        }

        if(url.getParams() != null && url.getParams().size() > 0) {
            if(metadata.getData() == null) {
                metadata.setData(new HashMap<String, Object>());
            }
            metadata.getData().putAll(url.getParams());
        }
        return metadata;
    }


}
