package com.artemis.kahn.controllers;


import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.entity.Seed;
import com.artemis.kahn.dao.mongo.repo.JobDao;
import com.artemis.kahn.dao.mongo.repo.SeedDao;
import com.artemis.kahn.service.JobService;
import com.artemis.kahn.service.PageService;
import com.artemis.kahn.util.DataUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seed")
public class SeedController {

    @Autowired
    PageService pageService;

    @Autowired
    JobService jobService;

    @Autowired
    JobDao jobDao;

    @Autowired
    SeedDao seedDao;


    @RequestMapping(value = "/list")
    public Object list(String jobId, ModelMap modelMap) {
        Job job = jobDao.findById(jobId);
        modelMap.put("job", job);
        List<Seed> seeds = seedDao.findSeedByJobId(jobId, 0, Integer.MAX_VALUE);
        modelMap.put("seeds", seeds);
        return "/seed/list";
    }

    @RequestMapping(value = "/save")
    public Object save(String jobId, String seeds) {
        if (StringUtils.isBlank(jobId)) {
            return "redirect:/job/list";
        }

        if (StringUtils.isBlank(seeds)) {
            return "redirect:/seed/list?jobId=" + jobId;
        }

        List<String> seedList = parseToSingles(seeds);
        for (String s : seedList) {
            s = s.trim();
            String[] content = s.split("\\t| ");
            List<String> items = new ArrayList<String>();
            for (int i = 0; i < content.length; i++) {
                if (!(content[i].trim().equals(""))) {
                    items.add(content[i]);
                }
            }
            //判断集合中种子 参数和编码有哪些

            String[] keys = new String[]{"GBK", "gbk", "UTF-8", "utf-8"};

            Seed entity = new Seed();
            entity.setCreationDate(new Date());
            entity.setJobId(jobId);
            if (items.size() == 3) {
                entity.setParams(items.get(1));
                entity.setCharset(items.get(2));
            }
            if (items.size() == 2 && items.get(1).startsWith("city")) {
                entity.setParams(items.get(1));
            }
            if (items.size() == 2) {
                for (int index = 0; index < keys.length; index++) {
                    if (keys[index].equals(items.get(1))) {
                        entity.setCharset(items.get(1));
                    }
                }
            }

            entity.setStatus(Seed.StatusEnum.ENABLE.getStatus());
            entity.setUrl(items.get(0));
            String[] dynamicParams = StringUtils.substringsBetween(s, "${", "}");
            if (dynamicParams != null && dynamicParams.length > 0) {
                entity.setIsTemplate(true);
            }
            saveOrUpdateSeed(entity);
        }

        return "redirect:/seed/list?jobId=" + jobId;
    }

    @RequestMapping(value = "/edit")
    @ResponseBody
    public Object edit(String seedId, String newSeed, String newParam, String newCharset) {
        Map<String, String> ret = new HashMap<String, String>();
        if (seedId == null) {
            return ret;
        }

        DBObject data = new BasicDBObject();
        if (StringUtils.isNotBlank(newSeed) && (newSeed.startsWith("http://") || newSeed.startsWith("https://"))) {
            data.put("url", newSeed.trim());
            String[] dynamicParams = StringUtils.substringsBetween(newSeed.trim(), "${", "}");
            if (dynamicParams != null && dynamicParams.length > 0) {
                data.put("is_template", true);
            }
        }

        if (StringUtils.isNotBlank(newParam)) {
            newParam = DataUtil.toDBCcase(newParam);
            Map<String, String> paramsMap = DataUtil.parse(newParam, ",", ":");
            Map<String, Object> proMap = new HashMap<String, Object>();
            for (String key : paramsMap.keySet()) {
                if (StringUtils.isBlank(paramsMap.get(key))) {
                    continue;
                }
                proMap.put(key, paramsMap.get(key).trim());
            }

            String params = DataUtil.mapToString(proMap, ",");
            if (StringUtils.isNotBlank(params)) {
                data.put("params", params);
            }
        }

        if (StringUtils.isNotBlank(newCharset)) {
            data.put("charset", newCharset.trim());
        } else {
            data.put("charset", "");
        }

        if (data.toMap().size() > 0) {
            seedDao.updateById(data, seedId);
        }
        ret.put("success", "1");
        return ret;
    }


    @RequestMapping(value = "/disable")
    @ResponseBody
    public Object disable(String jobId, @RequestParam("ids") List<String> ids) {
        Map<String, String> ret = new HashMap<String, String>();
        if (StringUtils.isBlank(jobId) || ids == null) {
            return ret;
        }

        for (String id : ids) {
            this.seedDao.updateById(id, Seed.StatusEnum.DISABLE);
        }

        return ret;
    }

    @RequestMapping(value = "/enable")
    @ResponseBody
    public Object enable(String jobId, @RequestParam("ids") List<String> ids) {
        Map<String, String> ret = new HashMap<String, String>();
        if (StringUtils.isBlank(jobId) || ids == null) {
            return ret;
        }

        for (String id : ids) {
            this.seedDao.updateById(id, Seed.StatusEnum.ENABLE);
        }

        return ret;
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(String jobId, String seedId, @RequestParam("ids") List<String> ids) {
        Map<String, String> ret = new HashMap<String, String>();
        if (StringUtils.isBlank(jobId)) {
            return ret;
        }

        if (ids != null) {
            this.seedDao.deleteByIds(ids);
        } else if (seedId != null) {
            this.seedDao.deleteById(seedId);
        }

        return ret;
    }


    private void saveOrUpdateSeed(Seed seed) {
        Seed obj = this.seedDao.findSeedByUrl(seed.getJobId(), seed.getUrl());
        seed.setCreationDate(new Date());
        if (obj != null) {
            this.seedDao.deleteById(obj.getId());
            this.seedDao.save(seed);
        } else {
            this.seedDao.save(seed);
        }
    }

    private List<String> parseToSingles(String seeds) {
        String[] seedArray = seeds.split("\\r\n");
        List<String> ret = new ArrayList<String>();
        for (String seed : seedArray) {
            if (!seed.startsWith("http://") && !seed.startsWith("https://")) {
                continue;
            }
            int a = seed.indexOf("${");
            int b = seed.indexOf("-");
            int c = seed.indexOf("}");
            if (a < b && b < c) {
                String str = StringUtils.substringBetween(seed, "${", "}");
                if (str.indexOf("-") > -1) {
                    String[] arrs = str.split("-");
                    int min = Integer.parseInt(arrs[0]);
                    int max = Integer.parseInt(arrs[1]);
                    for (int i = min; i <= max; i++) {
                        ret.add(seed.replace("${" + str + "}", String.valueOf(i)));
                    }
                }
            } else {
                ret.add(seed);
            }
        }
        return ret;
    }
}
