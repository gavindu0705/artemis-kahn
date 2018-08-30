package com.artemis.kahn.dao.mongo.entity;

import com.artemis.kahn.dao.mongo.persistence.MongoEntity;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "file_data")
public class FileData implements MongoEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3757955748307016725L;
	private String id;
	private String md5;
	private String url;
	private byte[] content;
	private Date creationDate;

	public static String md5(String url) {
		return DigestUtils.md5Hex(url) + Math.abs(url.hashCode());
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

	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "content")
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	@Column(name = "c_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "md5")
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

}
