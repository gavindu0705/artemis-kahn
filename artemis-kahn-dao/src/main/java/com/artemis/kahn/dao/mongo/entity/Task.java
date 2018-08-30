package com.artemis.kahn.dao.mongo.entity;

import com.artemis.kahn.dao.mongo.persistence.MongoEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.Map;

@Entity(name = "task")
public class Task implements MongoEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6800025657552981396L;
	private String id;
	private String jobId;
	private String pageId;
	private String clazz;
	private String caption;
	private Map<String, String> params;
	private String key;
	private String selector;
	private String attr;
	private String shell;
	private Date creationDate;

	public enum ClazzEnum {
		TEXT("TEXT"), //
		HTML("HTML"), //
		ATTR("ATTR"), //
		LINK("LINK"), //
		SOURCE("SOURCE"), //
		CLICK("CLICK"), //
		;
		private final String clazz;

		ClazzEnum(String clazz) {
			this.clazz = clazz;
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

	@Column(name = "job_id")
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	@Column(name = "page_id")
	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	@Column(name = "c_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "clazz")
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	@Column(name = "caption")
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	@Column(name = "params")
	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Column(name = "key")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Column(name = "selector")
	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	@Column(name = "attr")
	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	@Column(name = "shell")
	public String getShell() {
		return shell;
	}

	public void setShell(String shell) {
		this.shell = shell;
	}
}
