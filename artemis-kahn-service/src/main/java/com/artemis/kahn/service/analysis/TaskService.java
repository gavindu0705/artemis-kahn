package com.artemis.kahn.service.analysis;

import com.artemis.kahn.component.InvokeShell;
import com.artemis.kahn.dao.mongo.entity.FileData;
import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.Page;
import com.artemis.kahn.dao.mongo.entity.Pends;
import com.artemis.kahn.dao.mongo.entity.Task;
import com.artemis.kahn.dao.mongo.repo.MetadataDao;
import com.artemis.kahn.dao.mongo.repo.TaskDao;
import com.artemis.kahn.service.FileDataService;
import com.artemis.kahn.service.JobService;
import com.artemis.kahn.service.PageService;
import com.artemis.kahn.service.UrlsService;
import com.artemis.kahn.spider.selector.JQuerySelector;
import com.artemis.kahn.spider.selector.Selector;
import com.artemis.kahn.util.DataUtil;
import com.artemis.kahn.util.UrlUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

@Service
public class TaskService {

    public static final Logger LOG = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private JobService jobService;
    @Autowired
    private PageService pageService;
    @Autowired
    private MetadataDao metadataDao;
    @Autowired
    private UrlsService urlsService;
    @Autowired
    private FileDataService fileDataService;

    public PageResult doAnalysis(Pends pends) throws Exception {
        if (!isPendsAvaliable(pends)) {
            return null;
        }

        String url = pends.getId();
        String jobId = pends.getJobId();

        FileData fileData = getFileData(url);

        if (fileData == null) {
            this.urlsService.pendsToErrsTaskNOFILE(pends);
            return null;
        }

        try {
            String content = null;
            try {
                content = convertContent(fileData.getContent(), pends.getCharset());
            } catch (UnsupportedEncodingException e) {
                LOG.error("jobId:" + jobId + "\turl:" + url, e);
                this.urlsService.pendsToErrsTaskNOJOB(pends);
                return null;
            }

            if (content == null) {
                return null;
            }

            Job job = jobService.getJobById(jobId);
            if (job == null) {
                this.urlsService.pendsToErrsTaskNOJOB(pends);
                return null;
            }

            // 匹配到的页面
            Page page = pageService.matchPage(jobId, url);
            if (page == null) {
                this.urlsService.pendsToErrsTaskNOPAGE(pends);
                return null;
            }

            List<Task> tasks = this.findTasks(job.getId(), page.getId());
            if (tasks == null || tasks.size() == 0) {
                this.urlsService.pendsToErrsTaskNOTASK(pends);
                return null;
            }

            Selector jquerySelector = new JQuerySelector(Jsoup.parse(content));
            PageResult res = new PageResult(pends, page, job);
            for (Task task : tasks) {
                if ("CLICK".equals(task.getClazz())) {
                    List<String> refs = jquerySelector.href(task.getSelector(), url);
                    if (refs != null) {
                        for (String ref : refs) {
                            res.addExeHref(ref, task.getShell());
                        }
                    }
                } else if ("ATTR_CLICK".equals(task.getClazz())) {
                    List<String> attrs = jquerySelector.attr(task.getSelector(), task.getAttr());
                    if (attrs != null) {
                        for (String attr : attrs) {
                            res.addExeHref(UrlUtils.toFullUrl(attr, url), task.getShell());
                        }
                    }
                } else if ("TEXT_CLICK".equals(task.getClazz())) {
                    res.addExeHref(UrlUtils.toFullUrl(jquerySelector.text(task.getSelector()), url), task.getShell());
                } else if ("CUTSOURCE_CLICK".equals(task.getClazz())) {
//                    res.addHref(UrlUtils.toFullUrl((String)InvokeShell.invoke(task.getShell(), content), url));
                    Object obj = InvokeShell.invoke(task.getShell(), content);
                    if (obj instanceof String) {
                        res.addHref(UrlUtils.toFullUrl((String) obj, url));
                    } else if (obj instanceof List && ((List) obj).size() > 0) {
                        List results = ((List) obj);
                        for (int i = 0; i < results.size(); i++) {
                            res.addHref(UrlUtils.toFullUrl((String) results.get(i), url));
                        }
                    }
                } else if ("TEXT".equals(task.getClazz())) {
                    res.addExeMeta(task.getKey(), jquerySelector.text(task.getSelector()), task.getShell());
                } else if ("HTML".equals(task.getClazz())) {
                    res.addExeMeta(task.getKey(), jquerySelector.html(task.getSelector()), task.getShell());
                } else if ("ATTR".equals(task.getClazz())) {
                    res.addExeMeta(task.getKey(), jquerySelector.attr(task.getSelector(), task.getAttr()), task.getShell());
                } else if ("SOURCE".equals(task.getClazz())) {
                    res.addExeMeta(task.getKey(), content, task.getShell());
                } else if ("LINK".equals(task.getClazz())) {
                    res.addExeMeta(task.getKey(), jquerySelector.href(task.getSelector(), url), task.getShell());
                } else if ("CUTSOURCE".equals(task.getClazz())) {
                    res.addExeMeta(task.getKey(), content, task.getShell());
                }
            }

            urlsService.updatePendsTaskSuc(pends.getId(), pends.getJobId(), pends.getSessionId(), pends.getReferer());
            return res;
        } catch (Exception e) {
//			LOG.error("", e);
//			this.urlsService.pendsToErrsTaskErr(pends);
//			return null;
            throw e;
        }
    }

    public String convertContent(byte[] data, String charset) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(charset)) {
            charset = DataUtil.matchCharset(new String(data));
        }

        String content = null;
        if (StringUtils.isBlank(charset)) {
            content = new String(data);
        } else {
            content = new String(data, charset);
        }

        return content;
    }

    private List<Task> findTasks(String jobId, String pageId) {
        List<Task> tasks = taskDao.findTaskByJobPageId(jobId, pageId);
        if (tasks == null) {
            return Collections.EMPTY_LIST;
        }
        return tasks;
    }

    private boolean isPendsAvaliable(Pends pends) {
        if (pends == null || pends.getId() == null) {
            return false;
        }

        String url = pends.getId();
        String jobId = pends.getJobId();
        if (url == null || jobId == null) {
            return false;
        }

        return true;
    }

    private FileData getFileData(String url) {
        FileData fileData = fileDataService.findByUrl(url);
        if (fileData == null || fileData.getContent() == null || fileData.getContent().length == 0) {
            return null;
        }

        return fileData;
    }


    public long findMetadataCount() {
        return this.metadataDao.findCount();
    }

    public long findMetadataCount(String jobId) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        return this.metadataDao.findCount(q);
    }

}
