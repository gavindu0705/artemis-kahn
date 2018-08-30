package com.artemis.kahn.controllers;


import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.Page;
import com.artemis.kahn.dao.mongo.repo.JobDao;
import com.artemis.kahn.dao.mongo.repo.PageDao;
import com.artemis.kahn.model.PageModel;
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

    @RequestMapping(value = "/list")
    public Object list(String jobId, ModelMap modelMap) {
        Job job = jobDao.findById(jobId);
        List<Page> pages = null;
        if (job != null) {
            pages = pageDao.findPageByJobId(jobId);
        }

        modelMap.put("job", job);
        modelMap.put("pages", pages);
        return "/page/list";
    }

    @RequestMapping(value = "/create")
    public String create(String jobId, ModelMap modelMap) {
        Job job = jobDao.findById(jobId);
        modelMap.put("job", job);
        return "/page/create";
    }

    @RequestMapping(value = "/save")
    public String save(@Valid PageModel model) {
        Page page = new Page();
        BeanUtils.copyProperties(model, page);
        page.setStatus(0);
        page.setCreationDate(new Date());
        page.setPatterns(split(model.getPatternStr()));
        page.setErrTag(split(model.getErrTagStr()));
        page.setSucTag(split(model.getSucTagStr()));
        pageDao.save(page);
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
