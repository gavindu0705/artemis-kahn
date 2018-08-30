package com.artemis.kahn.dao.mongo.entity;

import com.artemis.kahn.dao.mongo.persistence.MongoEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "site_config")
public class SiteConfig implements MongoEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6923598350823335406L;
	private String id;
	private String name;
	private String root;
	private String charset;
	private Double visitFreq;
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

	@Column(name = "c_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "charset")
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "root")
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	@Column(name = "visit_freq")
	public Double getVisitFreq() {
		return visitFreq;
	}

	public void setVisitFreq(Double visitFreq) {
		this.visitFreq = visitFreq;
	}
}
