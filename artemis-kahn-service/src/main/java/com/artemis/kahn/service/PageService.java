package com.artemis.kahn.service;

import com.artemis.kahn.dao.mongo.entity.Page;
import com.artemis.kahn.dao.mongo.repo.PageDao;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PageService {

    @Autowired
    PageDao pageDao;


    private final static LoadingCache<String, List<Page>> PAGES_CACHE = CacheBuilder.newBuilder().concurrencyLevel(32)
            .maximumSize(500).expireAfterWrite(300, TimeUnit.SECONDS)
            .build(new CacheLoader<String, List<Page>>() {
                @Override
                public List<Page> load(String key) throws Exception {
//                    return pageDao.findPageByJobId(key);
                    return null;
                }
            });


    private final static LoadingCache<UrlKey, Page> URL_MATCH_CACHE =CacheBuilder.newBuilder().concurrencyLevel(32)
            .maximumSize(2000).expireAfterWrite(300, TimeUnit.SECONDS)
            .build(new CacheLoader<UrlKey, Page>() {
                public Page load(UrlKey k) throws Exception {
                    List<Page> pages = PAGES_CACHE.get(k.getJobId());
                    for (Page page : pages) {
                        for (String patt : page.getPatterns()) {
                            Matcher matcher = Pattern.compile(patt).matcher(k.getUrl());
                            if (matcher.matches()) {
                                return page;
                            }
                        }
                    }
                    return new Page();
                }
            });

    /**
     * 获取页面
     *
     * @param jobId
     * @return
     */
    public List<Page> findJobPages(String jobId) {
        return PAGES_CACHE.getUnchecked(jobId);
    }

    /**
     * 由于配置错误等原因，有可能匹配出多个页面
     * @param jobId
     * @param url
     * @return
     */
    public List<Page> matchPages(String jobId, String url) {
        List<Page> findPages = findJobPages(jobId);
        List<Page> matchPages = Lists.newArrayList();
        Set<String> pageIdCache = Sets.newHashSet();
        if(CollectionUtils.isNotEmpty(findPages)) {
            for(Page page: findPages) {
                List<String> patterns = page.getPatterns();
                if(CollectionUtils.isEmpty(patterns)) {
                    continue;
                }
                for(String pattern: patterns) {
                    boolean matches = Pattern.compile(pattern).matcher(url).matches();
                    if(matches && pageIdCache.add(page.getId())) {
                        matchPages.add(page);
                    }
                }
            }
        }
        return matchPages;
    }

    /**
     * 匹配页面
     *
     * @param jobId
     * @param url
     * @return
     */
    public Page matchPage(String jobId, String url) {
        //MapMaker中不能保存value为null的值
        Page page = URL_MATCH_CACHE.getUnchecked(new UrlKey(jobId, url));
        if (page.getId() == null) {
            return null;
        }
        return page;
    }


    class UrlKey {
        private String jobId;
        private String url;

        public UrlKey(String jobId, String url) {
            this.jobId = jobId;
            this.url = url;
        }

        public String getJobId() {
            return jobId;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return jobId + "-" + DigestUtils.md5Hex(url) + url.hashCode();
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }
}
