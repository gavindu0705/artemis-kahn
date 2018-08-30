package com.artemis.kahn.dao.mongo.entity;

import com.artemis.kahn.dao.mongo.persistence.MongoEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "job")
public class Job implements MongoEntity {

    /**
     *
     */
    private static final long serialVersionUID = -5949664535473011531L;
    private String id;
    private String name;
    private Integer interval;
    private int worktime;
    private String cat;
    private String root;
    private int status;
    private int priority;
    private Date creationDate;
    private String sessionId;
    private int sequence;
    private int crawlWay;//抓取方式 0-性能优先 1-抓全优先
    private int retryTimes; //出错重试次数
    private boolean renderWithJS = false; //是否需要js渲染
    private boolean debug = false;

    private Date startDate;
    private Date endDate;

    public static final int STOPPING = 0; // 初始停止

    public static final int RUNNING = 1; // 运行中

    public static final int DONE = 2; // 任务完成

    public static final int SUSPEND = 3; // 任务暂停

    /**
     * 抓取方式定义
     */
    public enum CrawlWayEnum {
        SPEED(0), //
        WHOLE(1), //
        ;
        private final int code;

        CrawlWayEnum(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
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

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "interval")
    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    @Column(name = "cat")
    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    @Column(name = "c_date")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Column(name = "status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "end_date")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "priority")
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Column(name = "worktime")
    public int getWorktime() {
        return worktime;
    }

    public void setWorktime(int worktime) {
        this.worktime = worktime;
    }

    @Column(name = "session_id")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Column(name = "sequence")
    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Column(name = "root")
    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    @Column(name = "crawl_way")
    public int getCrawlWay() {
        return crawlWay;
    }

    public void setCrawlWay(int crawlWay) {
        this.crawlWay = crawlWay;
    }

    @Column(name = "retry_times")
    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    @Column(name = "render_js")
    public boolean getRenderWithJS() {
        return renderWithJS;
    }

    public void setRenderWithJS(boolean renderWithJS) {
        this.renderWithJS = renderWithJS;
    }

    @Column(name = "render_js")
    public boolean setRenderWithJS() {
        return renderWithJS;
    }

    @Column(name = "debug")
    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean getDebug(){
        return debug;
    }

}
