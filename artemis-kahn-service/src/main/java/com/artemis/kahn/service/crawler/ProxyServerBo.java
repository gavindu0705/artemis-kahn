package com.artemis.kahn.service.crawler;

import com.artemis.kahn.dao.mongo.entity.ProxyServer;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Map;

/**
 * Created by dxy on 2015/9/30.
 */
public class ProxyServerBo {
    private String id;
    private String cat;
    private String service;
    private String domain;
    private int port;
    private String contextPath;
    private boolean isZip;
    private int connectThread;
    private int visitFreq;
    private int restartFreq;
    private Date creationDate;
    private int status;

    private int currentConnectThread;
    private Map<String, Long> rootLastVisitTime;

    public ProxyServerBo() {

    }

    public ProxyServerBo(ProxyServer proxyServer) {
        BeanUtils.copyProperties(proxyServer, this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public boolean isZip() {
        return isZip;
    }

    public void setIsZip(boolean isZip) {
        this.isZip = isZip;
    }

    public int getConnectThread() {
        return connectThread;
    }

    public void setConnectThread(int connectThread) {
        this.connectThread = connectThread;
    }

    public int getVisitFreq() {
        return visitFreq;
    }

    public void setVisitFreq(int visitFreq) {
        this.visitFreq = visitFreq;
    }

    public int getRestartFreq() {
        return restartFreq;
    }

    public void setRestartFreq(int restartFreq) {
        this.restartFreq = restartFreq;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getCurrentConnectThread() {
        return currentConnectThread;
    }

    public void setCurrentConnectThread(int currentConnectThread) {
        this.currentConnectThread = currentConnectThread;
    }

    public Map<String, Long> getRootLastVisitTime() {
        return rootLastVisitTime;
    }

    public void setRootLastVisitTime(Map<String, Long> rootLastVisitTime) {
        this.rootLastVisitTime = rootLastVisitTime;
    }
}