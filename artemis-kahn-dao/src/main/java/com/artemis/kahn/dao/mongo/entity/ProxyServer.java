package com.artemis.kahn.dao.mongo.entity;

import com.artemis.kahn.dao.mongo.persistence.MongoEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@Entity(name = "proxy_server")
public class ProxyServer implements MongoEntity {


    private String id;
    private String cat;
    private String service;
    private String domain;
    private int port;
    private int httpsPort;
    private String contextPath;
    private Boolean isZip = false;
    private int connectThread;
    private int visitFreq;
    private int restartFreq;
    private int status;
    private Date creationDate;
    private int  meanRestartTimeOnDay;
    private String publicIp;
    private Date restartDate;

    public enum StatusEnum {
        ENABLE(0), //
        DISABLE(1), //
        RESTART(2), //
        ERROR(3), //
        ;
        private final int status;

        StatusEnum(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public static StatusEnum parse(Integer status) {
            if (status == null) {
                return null;
            }
            for (StatusEnum st : StatusEnum.values()) {
                if (status == st.getStatus()) {
                    return st;
                }
            }
            return null;
        }
    }


    @Id
    @GeneratedValue
    @Column(name = ID)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Column(name = "cat")
    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    @Column(name = "service")
    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Column(name = "domain")
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Column(name = "port")
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Column(name = "https_port")
    public int getHttpsPort() {
        return httpsPort;
    }

    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

    @Column(name = "context_path")
    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Column(name = "is_zip")
    public Boolean getIsZip() {
        return isZip;
    }

    public void setIsZip(Boolean isZip) {
        this.isZip = isZip;
    }

    @Column(name = "connect_thread")
    public int getConnectThread() {
        return connectThread;
    }

    public void setConnectThread(int connectThread) {
        this.connectThread = connectThread;
    }

    @Column(name = "visit_freq")
    public int getVisitFreq() {
        return visitFreq;
    }

    public void setVisitFreq(int visitFreq) {
        this.visitFreq = visitFreq;
    }

    @Column(name = "restart_freq")
    public int getRestartFreq() {
        return restartFreq;
    }

    public void setRestartFreq(int restartFreq) {
        this.restartFreq = restartFreq;
    }

    @Column(name = "c_date")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Column(name = "restart_date")
    public Date getRestartDate() {
        return restartDate;
    }

    public void setRestartDate(Date restartDate) {
        this.restartDate = restartDate;
    }

    @Column(name = "public_ip")
    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    @Column(name = "mean_restart_time_on_day")
    public int getMeanRestartTimeOnDay() {
        return meanRestartTimeOnDay;
    }

    public void setMeanRestartTimeOnDay(int meanRestartTimeOnDay) {
        this.meanRestartTimeOnDay = meanRestartTimeOnDay;
    }

    @Transient
    public String getReadableMeanRestartTimeOnDay() {
        if(meanRestartTimeOnDay <= 60){
            return meanRestartTimeOnDay + "s";
        }
        if(meanRestartTimeOnDay % 60 == 0){
            return (meanRestartTimeOnDay / 60) + "s";
        }
        return (meanRestartTimeOnDay / 60) + "min" + (meanRestartTimeOnDay % 60) + "s";
    }


}
