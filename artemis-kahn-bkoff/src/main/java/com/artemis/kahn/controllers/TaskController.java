package com.artemis.kahn.controllers;

import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.Page;
import com.artemis.kahn.dao.mongo.entity.Task;
import com.artemis.kahn.dao.mongo.repo.JobDao;
import com.artemis.kahn.dao.mongo.repo.PageDao;
import com.artemis.kahn.dao.mongo.repo.TaskDao;
import com.artemis.kahn.model.TaskModel;
import com.artemis.kahn.utils.InvokeShell;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/task")
public class TaskController {

    @Autowired
    JobDao jobDao;

    @Autowired
    PageDao pageDao;

    @Autowired
    TaskDao taskDao;


    @RequestMapping(value = "/list")
    public Object list(String jobId, String pageId, ModelMap modelMap) {
        Job job = jobDao.findById(jobId);
        Page page = pageDao.findById(pageId);
        List<Task> tasks = null;
        if (job != null && page != null) {
            tasks = taskDao.findTaskByJobPageId(jobId, pageId);
        }
        modelMap.put("job", job);
        modelMap.put("page", page);
        modelMap.put("tasks", tasks);
        return "/task/list";
    }

    @RequestMapping(value = "/create")
    public Object create(String jobId, String pageId, String taskId, String clazz, ModelMap modelMap) {
        Job job = jobDao.findById(jobId);
        Page page = pageDao.findById(pageId);
        modelMap.put("job", job);
        modelMap.put("page", page);

        if (taskId != null) {
            Task task = taskDao.findById(taskId);
            modelMap.put("clazz", task.getClazz());
            modelMap.put("task", task);
        } else {
            modelMap.put("clazz", clazz);
        }

        return "/task/create";
    }

    @RequestMapping(value = "/save")
    public Object save(@Valid TaskModel taskModel) {
        Task task = new Task();
        BeanUtils.copyProperties(taskModel, task);
        task.setCreationDate(new Date());
        taskDao.save(task);
        return "redirect:/task/list?jobId=" + taskModel.getJobId() + "&pageId=" + taskModel.getPageId();
    }

    @RequestMapping(value = "/validateShell")
    @ResponseBody
    public Object validateShellAction(String shell) {
        Map<String, String> data = new HashMap<String, String>();
        if (InvokeShell.precompiler(shell)) {
            data.put("status", "1");
        } else {
            data.put("status", "0");
        }
        return data;
    }
}
