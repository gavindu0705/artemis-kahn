package com.artemis.kahn.controllers;


import com.artemis.kahn.dao.mongo.JobRepo;
import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.model.JobModel;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/job")
public class JobController {

    @Autowired
    JobRepo jobRepo;

    @RequestMapping(value = "/list")
    public String list(String name, ModelMap modelMap) {
        List<Job> list = jobRepo.findAll();
        System.out.println(list.size());
        modelMap.put("jobs", list);
        return "/job/list";
    }

    @RequestMapping(value = "/create")
    public String create(String name, ModelMap modelMap) {
        return "/job/create";
    }

    @RequestMapping(value = "/save")
    public String save(@Valid JobModel jobModel) {
        Job job = new Job();
        BeanUtils.copyProperties(jobModel, job);
        job.setStatus(0);
        job.setCreationDate(new Date());
        job.setSessionId(ObjectId.get().toString());
        jobRepo.save(job);
        System.out.println(job.getId());
        return "/job/create";
    }
}