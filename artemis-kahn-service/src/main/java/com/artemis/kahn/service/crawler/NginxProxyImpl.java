package com.artemis.kahn.service.crawler;

import com.artemis.kahn.core.UserAgents;
import com.artemis.kahn.core.bean.Goal;
import com.artemis.kahn.core.bean.Harvest;
import com.artemis.kahn.dao.mongo.entity.Page;
import com.artemis.kahn.service.PageService;
import com.artemis.kahn.spider.ProxyPolicy;
import com.artemis.kahn.util.DataUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 利用NGINX ADSL代理服务
 *
 * @author duxiaoyu
 */

@Service
public class NginxProxyImpl implements ProxyPolicy {

    public static final Logger LOG = LoggerFactory.getLogger(NginxProxyImpl.class);

    @Autowired
    private PageService pageService;

    @Autowired
    private ProxyServerService proxyServerService;

    public NginxProxyImpl() {
    }


    private ProxyServerBo selectProxy(Goal goal) {
        while (true) {
            ProxyServerBo proxy = proxyServerService.selectionOptimal(goal.getUrl());
            if (proxy != null) {
                return proxy;
            } else {
                try {
//                    LOG.print("proxy is busying now! retry after 1 seconds ...");
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Harvest apply(Goal goal) {
        Harvest harvest = null;
        ProxyServerBo proxy = proxyServerService.selectionOptimal(goal.getUrl());
        String root = DataUtil.convertUrlToRoot(goal.getUrl());
        ProxyKey proxyKey = new ProxyKey(proxy.getDomain(), proxy.getPort());
        long startTime = 0;
        try {
            //标记开始使用代理
            proxyServerService.startUsing(proxyKey, root);
            startTime = System.currentTimeMillis();
            harvest = invokeHttpClient(proxy, goal);
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {

            } else {
                LOG.error("jobId:" + goal.getJobId() + ",url:" + goal.getUrl(), e);
            }
            harvest = new Harvest(goal.getJobId(), goal.getSessionId(), goal.getUrl(), Harvest.StatusEnum.ERROR.getCode(), proxy.getDomain());
        } finally {
            long endTime = System.currentTimeMillis();
            proxyServerService.finish(proxyKey, root);
            boolean stat = false;
            if (harvest.getStatusCode() == Harvest.StatusEnum.SUCCESS.getCode() || harvest.getStatusCode() == Harvest.StatusEnum.SERVER_ERROR.getCode()
                    || harvest.getStatusCode() == Harvest.StatusEnum.NOTFOUND4.getCode()) {
                stat = true;
            }
//            saveLog(harvest, proxy, root, startTime, endTime, stat);
        }

        return harvest;
    }

//	private void saveLog(Harvest harvest, ProxyServerBo proxy, String root,
//			long startTime, long endTime, boolean stat) {
//		if(harvest != null) {
//			ProxyipReqLog log = new ProxyipReqLog();
//			if(stat) {
//				Integer time = (int) (endTime - startTime);
//				if(harvest.getContent() != null) {
//					Integer size = harvest.getContent().length;
//					log.setSize(size);
//				}
//				log.setTime(time);
//			}
//			log.setCode(harvest.getStatusCode());
//			log.setRetryCount(0);
//			log.setDomain(root);
//			log.setIp(proxy.getDomain());
//			log.setPort(proxy.getPort());
//			log.setUpdateTime(new Date());
//			log.setStat(stat);
//			ProxyipReqLogDao.getInstance().save(log);
//		}
//	}

    /**
     * openConnection实现
     *
     * @param proxy
     * @param goal
     * @return
     * @throws IOException
     */
    public Harvest invokeOpenConnection(ProxyServerBo proxy, Goal goal) throws IOException {
        Harvest harvest = null;
        try {
            String url = goal.getUrl();
            String root = DataUtil.convertUrlToRoot(goal.getUrl());
            URL netUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) netUrl.openConnection(nginxNetProxy(goal, proxy));
            connection.setReadTimeout(10 * 1000);
            connection.setConnectTimeout(10 * 1000);
            connection.addRequestProperty("Referer", goal.getReferer());
            connection.addRequestProperty("User-Agent", UserAgents.get());
            connection.addRequestProperty("Content-Encoding", "gzip");
            connection.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            connection.addRequestProperty("Connection", "close");
            connection.addRequestProperty("Host", StringUtils.substringBefore(goal.getUrl(), root) + root);

            if (connection.getResponseCode() == Harvest.StatusEnum.SUCCESS.getCode()) {
                byte[] data = IOUtils.toByteArray(connection.getInputStream());
                if ("gzip".equals(connection.getHeaderField("Content-Encoding"))) {
                    data = DataUtil.unzip(data);
                }

                String charset = goal.getCharset();
                if (StringUtils.isBlank(charset)) {
                    charset = matchCharset(connection.getHeaderField("Content-Type"));
                    if (StringUtils.isBlank(charset)) {
                        charset = matchCharset(new String(data));
                    }
                }

                String content = null;
                if (StringUtils.isBlank(charset)) {
                    content = new String(data);
                } else {
                    content = new String(data, charset);
                }
                if (content == null || content.indexOf("你的访问过于频繁") > -1 || content.indexOf("您的访问速度太快了") > -1) {
                    harvest = new Harvest(goal.getJobId(), goal.getSessionId(), goal.getUrl(), Harvest.StatusEnum.ERROR.getCode(), proxy.getDomain());
                } else {
                    harvest = new Harvest(goal.getJobId(), goal.getSessionId(), goal.getUrl(), connection.getResponseCode(), connection.getContentType(), data, proxy.getDomain(), goal.getReferer());
                }
            } else {
                harvest = new Harvest(goal.getJobId(), goal.getSessionId(), goal.getUrl(), connection.getResponseCode(), proxy.getDomain());
            }
        } catch (IOException e) {
            throw e;
        }

        return harvest;
    }


    /**
     * httpClient实现
     *
     * @param proxy
     * @param goal
     * @return
     * @throws IOException
     */
    public Harvest invokeHttpClient(ProxyServerBo proxy, Goal goal) throws Exception {
        Harvest harvest = null;
        try {
            String url = goal.getUrl();
//            String root = DataUtil.convertUrlToRoot(goal.getUrl());
            HttpGet httpRequest = new HttpGet();
            URI uri = new URI(url);
            httpRequest.setURI(uri);
            httpRequest.setHeader("Connection", "close");
            httpRequest.setHeader("User-Agent", UserAgents.get());
            httpRequest.setHeader("Accept-Encoding", "gzip");
            httpRequest.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
            if (StringUtils.isNotBlank(goal.getReferer())) {
                httpRequest.setHeader("referer", goal.getReferer());
            }
            if (null != goal.getHeaders() && !goal.getHeaders().isEmpty()) {
                for (String name : goal.getHeaders().keySet()) {
                    httpRequest.setHeader(name, goal.getHeaders().get(name));
                }
            }

            HttpParams httpParams = httpRequest.getParams();
            httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 10 * 1000);
            httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);

            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getDomain(), url.startsWith("https:") ? 8888 : proxy.getPort()));

            HttpResponse response = httpClient.execute(httpRequest);
            if (response.getStatusLine().getStatusCode() == Harvest.StatusEnum.SUCCESS.getCode()) {
                HttpEntity entity = response.getEntity();
                if (response.getFirstHeader("Content-Encoding") != null && "gzip".equals(response.getFirstHeader("Content-Encoding").getValue())) {
                    entity = new GzipDecompressingEntity(entity);
                }
                if (entity != null) {
                    byte[] data = EntityUtils.toByteArray(entity);
                    String charset = goal.getCharset();
                    if (StringUtils.isBlank(charset)) {
                        charset = matchCharset(response.getFirstHeader("Content-Type").getValue());
                        if (StringUtils.isBlank(charset)) {
                            charset = matchCharset(new String(data));
                        }
                    }
                    String content = null;
                    if (StringUtils.isBlank(charset)) {
                        content = new String(data);
                    } else {
                        content = new String(data, charset);
                    }

                    Page page = pageService.matchPage(goal.getJobId(), url);
                    if (page != null && page.getErrTag() != null && page.getErrTag().size() > 0 && containsAnyOf(content, page.getErrTag())) {
                        harvest = new Harvest(goal.getJobId(), goal.getSessionId(), goal.getUrl(), Harvest.StatusEnum.ERROR.getCode(), proxy.getDomain(), data);
                        return harvest;
                    }


                    if (page != null && page.getSucTag() != null && page.getSucTag().size() > 0 && !containsAllOf(content, page.getSucTag())) {
                        harvest = new Harvest(goal.getJobId(), goal.getSessionId(), goal.getUrl(), Harvest.StatusEnum.ERROR.getCode(), proxy.getDomain(), data);
                        return harvest;
                    }

                    harvest = new Harvest(goal.getJobId(), goal.getSessionId(), goal.getUrl(), response.getStatusLine().getStatusCode(), response.getEntity().getContentType().getValue(), data, proxy.getDomain(), goal.getReferer());
                    return harvest;
                }
            } else {
                harvest = new Harvest(goal.getJobId(), goal.getSessionId(), goal.getUrl(), response.getStatusLine().getStatusCode(), proxy.getDomain());
            }


        } catch (Exception e) {
            throw e;
        }

        return harvest;
    }

    private boolean containsAllOf(String str, List<String> tags) {
        if (StringUtils.isBlank(str)) {
            return false;
        }

        if (tags == null) {
            return true;
        }

        boolean f = true;
        for (String t : tags) {
            if (!str.contains(t)) {
                f = false;
                break;
            }
        }

        return f;
    }

    private boolean containsAnyOf(String str, List<String> tags) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        if (tags == null) {
            return false;
        }

        for (String t : tags) {
            if (str.contains(t)) {
                return true;
            }
        }

        return false;
    }


    public static final Pattern pattern = Pattern.compile("charset=\"?([\\w\\d-]+)\"?;?", Pattern.CASE_INSENSITIVE);

    private String matchCharset(String input) {
        if (input == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private Proxy nginxNetProxy(Goal goal, ProxyServerBo proxy) {
        String ip = proxy.getDomain().replace("http://", "");
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, proxy.getPort()));
    }

}
