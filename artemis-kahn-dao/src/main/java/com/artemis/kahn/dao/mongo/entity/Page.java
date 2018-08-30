package com.artemis.kahn.dao.mongo.entity;

import com.artemis.kahn.dao.mongo.persistence.MongoEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity(name="page")
public class Page implements MongoEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3778850527623833755L;
	private String id;
	private String jobId;
	private String name;
	private long expires;
	private List<String> patterns;
	private List<String> errTag;
	private List<String> sucTag;
	private int status;
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

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "patterns")
	public List<String> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}

	@Column(name = "c_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "expires")
	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

//	@Column(name = "stop_patterns")
//	public List<String> getStopPatterns() {
//		return stopPatterns;
//	}
//
//	public void setStopPatterns(List<String> stopPatterns) {
//		this.stopPatterns = stopPatterns;
//	}

	@Column(name = "status")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "err_tag")
	public List<String> getErrTag() {
		return errTag;
	}

	public void setErrTag(List<String> errTag) {
		this.errTag = errTag;
	}

	@Column(name = "suc_tag")
	public List<String> getSucTag() {
		return sucTag;
	}

	public void setSucTag(List<String> sucTag) {
		this.sucTag = sucTag;
	}
}
