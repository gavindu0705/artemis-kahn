package com.artemis.kahn.controllers;

import com.artemis.kahn.core.bean.Goal;
import com.artemis.kahn.core.bean.Harvest;
import com.artemis.kahn.dao.mongo.entity.FileData;
import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.JobStat;
import com.artemis.kahn.dao.mongo.entity.Pends;
import com.artemis.kahn.dao.mongo.entity.Seed;
import com.artemis.kahn.dao.mongo.entity.UrlRoad;
import com.artemis.kahn.dao.mongo.entity.Urls;
import com.artemis.kahn.dao.mongo.repo.JobDao;
import com.artemis.kahn.dao.mongo.repo.JobStatDao;
import com.artemis.kahn.dao.mongo.repo.PageDao;
import com.artemis.kahn.dao.mongo.repo.SeedDao;
import com.artemis.kahn.dao.mongo.repo.SiteConfigDao;
import com.artemis.kahn.dao.mongo.repo.TaskDao;
import com.artemis.kahn.model.JobModel;
import com.artemis.kahn.service.FileDataService;
import com.artemis.kahn.service.JobService;
import com.artemis.kahn.service.UrlRoadService;
import com.artemis.kahn.service.UrlsService;
import com.artemis.kahn.service.analysis.PageResult;
import com.artemis.kahn.service.analysis.TaskService;
import com.artemis.kahn.service.crawler.NginxProxyImpl;
import com.artemis.kahn.util.DataUtil;
import com.artemis.kahn.vo.JobVo;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/job")
public class Job2Controller {
    @Autowired
    private JobDao jobDao;
    @Autowired
    private SeedDao seedDao;
    @Autowired
    private PageDao pageDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private JobStatDao jobStatDao;
    @Autowired
    private JobService jobService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UrlsService urlsService;
    @Autowired
    private FileDataService fileDataService;
    @Autowired
    private UrlRoadService urlRoadService;

    @RequestMapping(value = "/crawl")
    public Object crawl(String jobId, String url, ModelMap modelMap) {
        FileData fileData = this.fileDataService.findByUrl(url);
        List<Seed> seeds = this.seedDao.findAllSeedByJobId(jobId, Seed.StatusEnum.ENABLE);
        if (seeds.size() > 0) {
            try {
                String content = new String(fileData.getContent(), seeds.get(0).getCharset());
                modelMap.put("content", content);
            } catch (Exception e) {

            }
        }
        return "/job/crawl";
    }

    @RequestMapping(value = "/showFileData")
    public Object showFileData(String requestUrl, String charset, ModelMap modelMap) throws UnsupportedEncodingException {
        FileData fileData = fileDataService.findByUrl(requestUrl);
        if (fileData != null) {
            if (StringUtils.isBlank(charset)) {
                charset = DataUtil.matchCharset(new String(fileData.getContent()));
            }

            String content = null;
            if (StringUtils.isBlank(charset)) {
                content = new String(fileData.getContent());
            } else {
                content = new String(fileData.getContent(), charset);
            }
            modelMap.put("content", content);
        }
        return "/job/showFileData";
    }

    @RequestMapping("urlRoad")
    public Object urlRoad(String jobId, String sessionId, String requestUrl, ModelMap modelMap) {
        Job job = jobDao.findById(jobId);
        modelMap.put("job", job);
        if (StringUtils.isBlank(requestUrl)) {
            List<Seed> seeds = this.seedDao.findAllSeedByJobId(job.getId(), Seed.StatusEnum.ENABLE);
            if (seeds.size() > 0) {
                modelMap.put("seed", seeds.get(0));
            }
            return "/job/urlRoad";
        }

        DBObject sq = new BasicDBObject();
        sq.put("c_date", 1);
        List<UrlRoad> urlRoadList = urlRoadService.findUrlRoadByRequestUrl(jobId, sessionId, requestUrl);
        Map<String, List<UrlRoad>> urlRoadMap = new LinkedHashMap<String, List<UrlRoad>>();
        List<String> originRefererUrlList = new ArrayList<String>();
        for (UrlRoad urlRoad : urlRoadList) {
            if (urlRoadMap.containsKey(urlRoad.getRequestUrl())) {
                List<UrlRoad> list = urlRoadMap.get(urlRoad.getRequestUrl());
                list.add(urlRoad);
                urlRoadMap.put(urlRoad.getRequestUrl(), list);
            } else {
                List<UrlRoad> list = new ArrayList<UrlRoad>();
                list.add(urlRoad);
                urlRoadMap.put(urlRoad.getRequestUrl(), list);
            }

            if (urlRoad.getRefererUrl() != null && (!originRefererUrlList.contains(urlRoad.getRefererUrl()))) {
                originRefererUrlList.add(urlRoad.getRefererUrl());
            }
        }
        modelMap.put("urlRoadMap", urlRoadMap);
        modelMap.put("originRefererUrlList", originRefererUrlList);

        List<UrlRoad> targetUrlRoadList = urlRoadService.findUrlRoadByRefererUrl(jobId, sessionId, requestUrl);
        List<String> targetRequestUrlList = new ArrayList<String>();
        for (UrlRoad urlRoad : targetUrlRoadList) {
            if (urlRoad.getRequestUrl() != null && (!targetRequestUrlList.contains(urlRoad.getRequestUrl()))) {
                targetRequestUrlList.add(urlRoad.getRequestUrl());
            }
        }

        modelMap.put("targetRequestUrlList", targetRequestUrlList);
        return "/job/urlRoad";
    }

    @RequestMapping(value = "/list")
    public String list(ModelMap modelMap) {
        DBObject q = new BasicDBObject();
        modelMap.put("runningCount", (int) jobDao.findCount(new BasicDBObject(q.toMap()).append("status", Job.RUNNING)));
        modelMap.put("suspendCount", (int) jobDao.findCount(new BasicDBObject(q.toMap()).append("status", Job.SUSPEND)));
        List<Job> jobList = jobDao.findAll(q, new BasicDBObject("c_date", 1), 10000000);
        List<JobVo> voList = new ArrayList<JobVo>();
        for (Job job : jobList) {
            JobVo vo = convertJobToVo(job);
            if (vo != null) {
                voList.add(vo);
            }
        }
        modelMap.put("jobVoList", voList);
        return "/job/list";
    }

    @RequestMapping(value = "/record")
    public Object record(String jobId, ModelMap modelMap) {
        if (jobId == null || !ObjectId.isValid(jobId)) {
            return "/job/record";
        }
        Job job = jobDao.findById(jobId);
        if (job == null) {
            return "/job/record";
        }
        List<JobStat> list = jobStatDao.findAll(new BasicDBObject("job_id", jobId), new BasicDBObject("start_date", -1), 10000000);
        if (list == null || list.size() == 0) {
            return "/job/record";
        }
        modelMap.put("name", job.getName());
        modelMap.put("jobStats", list);
        return "/job/record";
    }

    @RequestMapping(value = "/stat")
    public Object stat(String jobId, ModelMap modelMap) {
        Job job = jobDao.findById(jobId);
        if (job != null) {
            modelMap.put("jobMetaCount", taskService.findMetadataCount(jobId));
            modelMap.put("jobVo", convertJobToVo(job));
        }
        Map<String, Integer> urlsStatusNameMap = new LinkedHashMap<String, Integer>();
        for (Urls.StatusEnum statusEnum : Urls.StatusEnum.values()) {
            urlsStatusNameMap.put(statusEnum.name(), statusEnum.getStatus());
        }
        modelMap.put("urlsStatusNameMap", urlsStatusNameMap);
        return "/job/stat";
    }


    @RequestMapping(value = "/statDetail")
    public Object statDetail(String jobId, Integer status, ModelMap modelMap) {
        if (jobId != null) {
            Job job = jobDao.findById(jobId);
            if (job != null) {
                modelMap.put("jobVo", convertJobToVo(job));
                if (status != null) {
                    if (status.intValue() > 5) {
                        modelMap.put("pendsList", urlsService.findPendsByJobId(job.getId(), Urls.StatusEnum.parseUrlsStatus(status.intValue()), 50));
                    } else {
                        modelMap.put("urlsList", urlsService.findUrlsByJobId(job.getId(), Urls.StatusEnum.parseUrlsStatus(status.intValue()), 50));
                    }
                }
            }
        }
        return "/job/statDetail";
    }


    @RequestMapping(value = "/statusCount")
    @ResponseBody
    public Object statusCount(String jobId, Integer status) {
        Map<String, Object> data = new HashMap<String, Object>();
        if (jobId != null) {
            Job job = jobDao.findById(jobId);
            if (job != null && status != null) {
                if (status.intValue() > 5) {
                    data.put("count", (int) urlsService.findPendsCount(job.getId(), Urls.StatusEnum.parseUrlsStatus(status.intValue())));
                } else {
                    data.put("count", (int) urlsService.findUrlsCount(job.getId(), Urls.StatusEnum.parseUrlsStatus(status.intValue())));
                }
            }
        }
        return data;
    }

    private JobVo convertJobToVo(Job job) {
        if (job == null) {
            return null;
        }

        JobVo jobVo = new JobVo();
        BeanUtils.copyProperties(job, jobVo);
        JobStat jobStat = jobStatDao.findJobStat(job.getId(), job.getSessionId());
        if (jobStat != null) {
            jobVo.setSessionId(job.getSessionId());
            jobVo.setStartDate(jobStat.getStartDate());
            jobVo.setEndDate(jobStat.getEndDate());
            jobVo.setCrawlCount(jobStat.getCrawlCount());
            jobVo.setTaskCount(jobStat.getTaskCount());
            jobVo.setErrCount(jobStat.getErrCount());
            jobVo.setMetaCount(jobStat.getMetaCount());
        } else {
            jobVo.setStartDate(null);
            jobVo.setEndDate(null);
        }

        return jobVo;
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(String jobId) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("msg", 1);
        this.jobDao.deleteById(jobId);
        this.pageDao.deleteByJobId(jobId);
        this.taskDao.deleteByJobId(jobId);
        this.seedDao.deleteByJobId(jobId);
        return data;
    }

    @RequestMapping(value = "/create")
    public String create(String jobId, ModelMap modelMap) {
        modelMap.put("job", jobDao.findById(jobId));
        return "/job/create";
    }

    @RequestMapping(value = "/save")
    public String save(@Valid JobModel jobModel) {
        Job job = new Job();
        BeanUtils.copyProperties(jobModel, job);
        job.setStatus(0);
        job.setCreationDate(new Date());
        job.setSessionId(ObjectId.get().toString());
        jobDao.save(job);
        System.out.println(job.getId());
        return "/job/create";
    }

    @RequestMapping(value = "/start")
    public Object start(String jobId) {
        if (jobId == null) {
            return "redirect:/job/list";
        }
        jobService.startJob(jobId);
        return "redirect:/job/stat?jobId=" + jobId;
    }

    @RequestMapping(value = "/restart")
    public Object restart(String jobId) {
        if (jobId == null) {
            return "redirect:/job/list";
        }
        jobService.restartJob(jobId);
        return "redirect:/job/stat?jobId=" + jobId;
    }

    @RequestMapping(value = "/suspend")
    public Object suspendAction(String jobId) {
        if (jobId == null) {
            return "redirect:/job/list";
        }
        jobService.suspendJob(jobId);
        return "redirect:/job/stat?jobId=" + jobId;
    }

    @RequestMapping(value = "/stop")
    public Object stop(String jobId) {
        if (jobId == null) {
            return "redirect:/job/list";
        }
        jobService.stopJob(jobId);
        return "redirect:/job/stat?jobId=" + jobId;
    }

    @RequestMapping(value = "/clean")
    @ResponseBody
    public Object cleanAction(String jobId) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("msg", 1);
        if (jobId == null) {
            data.put("msg", 0);
            return data;
        }
        jobService.cleanMetadata(jobId);
        return data;
    }


    @RequestMapping(value = "/getCounts")
    @ResponseBody
    public Object getCounts() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("msg", 1);
        data.put("urlsCount", urlsService.findUrlsCount());
        data.put("pendsCount", urlsService.findPendsCount());
        data.put("metaCount", taskService.findMetadataCount());
        return data;
    }

    @RequestMapping(value = "/testUrl")
    public Object testUrl(String jobId, String sessionId, String url, String charset, String extraParams, ModelMap modelMap) {
        if (jobId == null) {
            return "redirect:/job/list";
        }

        modelMap.put("job", this.jobDao.findById(jobId));
        if (url != null && charset != null) {
            modelMap.put("url", url);
            String referer = "http://www.qq.com";
            Harvest har = testAndSaveCrawlData(jobId, sessionId, url, charset, referer);
            if (har != null) {
                modelMap.put("captor", har.getCaptor());
            }

            if (har != null && har.getContent() != null) {
                try {
                    modelMap.put("content", taskService.convertContent(har.getContent(), charset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            if (har != null && har.getStatusCode() == Harvest.StatusEnum.SUCCESS.getCode()) {
                PageResult pageResult = null;
                try {
                    pageResult = testProductData(url, jobId, charset, referer, DataUtil.stringToMap(extraParams, ",", ":"));
                } catch (Exception e) {
                    modelMap.put("msg", "抓取发生异常，请重试一下：" + e.toString());
                }

                if (pageResult != null) {
                    modelMap.put("hrefs", pageResult.getHrefs());
                    modelMap.put("metadata", pageResult.getMetadata());
                }
            } else {
                modelMap.put("msg", "抓取不成功");
            }

        } else {
            modelMap.put("msg", "请输入URL");
        }

        return "/job/testUrl";
    }


    private PageResult testProductData(String url, String jobId, String charset, String referer, Map<String, String> extraParams) throws Exception {
        Pends pends = new Pends();
        pends.setId(url);

        pends.setCharset(charset);
        pends.setCreationDate(new Date());
        pends.setErrors(0);
        pends.setJobId(jobId);
        pends.setParams(extraParams);
        pends.setReferer(referer);
        pends.setStatus(Urls.StatusEnum.TASK_INIT.getStatus());

        return this.taskService.doAnalysis(pends);
    }

    private Harvest testAndSaveCrawlData(String jobId, String sessionId, String url, String charset, String referer) {
        Goal goal = new Goal(jobId, sessionId, url, charset, referer);
        NginxProxyImpl adslProxy = new NginxProxyImpl();
        Harvest harvest = adslProxy.apply(goal);
        if (harvest.getStatusCode() == 200) {
            fileDataService.saveFileData(harvest.getUrl(), harvest.getContent());
        }

        return harvest;
    }
}
