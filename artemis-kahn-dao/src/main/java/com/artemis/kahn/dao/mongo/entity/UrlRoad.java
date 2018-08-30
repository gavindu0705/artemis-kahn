package com.artemis.kahn.dao.mongo.entity;

import com.artemis.kahn.dao.mongo.persistence.MongoEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "url_road")
public class UrlRoad implements MongoEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3094578597740372928L;

	private String id;
	private String jobId;
	private String sessionId;
	private String requestUrl;
	private String refererUrl;
	private String md5Request;
	private String md5Referer;
	private Integer status;
	private Date creationDate;

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

	@Column(name = "c_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "session_id")
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Column(name = "request_url")
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	@Column(name = "referer_url")
	public String getRefererUrl() {
		return refererUrl;
	}

	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "md5_request")
	public String getMd5Request() {
		return md5Request;
	}

	public void setMd5Request(String md5Request) {
		this.md5Request = md5Request;
	}

	@Column(name = "md5_referer")
	public String getMd5Referer() {
		return md5Referer;
	}

	public void setMd5Referer(String md5Referer) {
		this.md5Referer = md5Referer;
	}
}
