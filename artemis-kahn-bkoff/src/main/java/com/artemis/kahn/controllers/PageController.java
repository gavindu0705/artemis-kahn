package com.artemis.kahn.controllers;


import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.Page;
import com.artemis.kahn.dao.mongo.entity.Task;
import com.artemis.kahn.dao.mongo.repo.JobDao;
import com.artemis.kahn.dao.mongo.repo.PageDao;
import com.artemis.kahn.dao.mongo.repo.TaskDao;
import com.artemis.kahn.model.PageModel;
import com.artemis.kahn.vo.PageVo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/page")
public class PageController {

    @Autowired
    JobDao jobDao;

    @Autowired
    PageDao pageDao;

    @Autowired
    TaskDao taskDao;

    @RequestMapping(value = "/list")
    public Object list(String jobId, ModelMap modelMap) {
        Job job = jobDao.findById(jobId);
        List<Page> pages = pageDao.findPageByJobId(jobId);
        modelMap.put("job", job);
        List<PageVo> pageVoList = new ArrayList<PageVo>();

        for (Page page : pages) {
            List<Task> tasks = taskDao.findTaskByJobPageId(jobId, page.getId());
            PageVo pageVo = new PageVo();
            BeanUtils.copyProperties(page, pageVo);
            pageVo.setTasks(tasks);
            pageVoList.add(pageVo);
        }
        modelMap.put("pageVoList", pageVoList);
        return "/page/list";
    }

    @RequestMapping(value = "/create")
    public String create(String jobId, String pageId, ModelMap modelMap) {
        Job job = jobDao.findById(jobId);
        modelMap.put("job", job);
        if (pageId != null) {
            modelMap.put("page", this.pageDao.findById(pageId));
        }
        return "/page/create";
    }

    @RequestMapping(value = "/delete")
    public Object delete(String jobId, String pageId) {
        this.pageDao.deleteById(pageId);
        this.taskDao.deleteByPageId(pageId);
        return "/job/list?jobId=" + jobId;
    }

    @RequestMapping(value = "/save")
    public String save(@Valid PageModel model) {
        if (model == null || StringUtils.isBlank(model.getName()) || StringUtils.isBlank(model.getPatterns())) {
            return "redirect:/job/list?jobId=" + model.getJobId();
        }
        if (model.getId() != null) {
            DBObject data = new BasicDBObject();
            data.put("job_id", model.getJobId());
            data.put("name", model.getName());
            data.put("expires", model.getExpires());
            data.put("patterns", split(model.getPatterns()));
            data.put("err_tag", split(model.getErrTag()));
            data.put("suc_tag", split(model.getSucTag()));
            data.put("c_date", new Date());
            this.pageDao.updateById(data, model.getId());
        } else {
            Page page = new Page();
            page.setJobId(model.getJobId());
            page.setName(model.getName());
            page.setExpires(model.getExpires());
            page.setStatus(0);
            page.setPatterns(split(model.getPatterns()));
            page.setErrTag(split(model.getErrTag()));
            page.setSucTag(split(model.getSucTag()));
            page.setCreationDate(new Date());
            this.pageDao.save(page);
        }

        return "redirect:/page/list?jobId=" + model.getJobId();
    }

    private List<String> split(String s) {
        List<String> ret = new ArrayList<String>();
        if (StringUtils.isNotBlank(s)) {
            String[] pts = s.split("\r\n");
            for (int i = 0; i < pts.length; i++) {
                if (StringUtils.isNotBlank(pts[i])) {
                    ret.add(pts[i]);
                }
            }
        }
        return ret;
    }

}
