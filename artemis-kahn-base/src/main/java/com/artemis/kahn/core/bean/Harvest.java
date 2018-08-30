package com.artemis.kahn.core.bean;

/**
 * 爬虫的爬取结果
 *
 * @author xiaoyu
 *
 */
public class Harvest {


	private String jobId;
	private String sessionId;
	private String url;
	private int statusCode;
	private String mimeType;
	private byte[] content;
	private String captor;
	private String referer;
	private String root;
	private String proxy;
	private long time;

	Harvest() {

	}

	public Harvest(String jobId, String sessionId, String url, int statusCode) {
		this.jobId = jobId;
		this.sessionId = sessionId;
		this.url = url;
		this.statusCode = statusCode;
	}

	public Harvest(String jobId, String sessionId, String url, int statusCode, String captor) {
		this.jobId = jobId;
		this.sessionId = sessionId;
		this.url = url;
		this.captor = captor;
		this.statusCode = statusCode;
	}

	public Harvest(String jobId, String sessionId, String url, int statusCode, String captor, byte[] content) {
		this.jobId = jobId;
		this.sessionId = sessionId;
		this.url = url;
		this.captor = captor;
		this.statusCode = statusCode;
		this.content = content;
	}

	public Harvest(String jobId, String sessionId, String url, int statusCode, String mimeType, byte[] content, String captor,
				   String referer) {
		this.jobId = jobId;
		this.sessionId = sessionId;
		this.url = url;
		this.statusCode = statusCode;
		this.mimeType = mimeType;
		this.content = content;
		this.captor = captor;
		this.referer = referer;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCaptor() {
		return captor;
	}

	public void setCaptor(String captor) {
		this.captor = captor;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public enum StatusEnum {

		NONE(0), // NONE
		ERROR(1), // 抓取错误
		URI_SYNTAX(2),//URL非法

		SUCCESS(200), // 成功

		REDIRECT(300), //
		REDIRECT1(301), //
		REDIRECT2(302), //

		NOTFOUND(400), //
		NOTFOUND4(404), //

		SERVER_ERROR(500), //
		SERVER_ERROR1(501), //
		SERVER_ERROR2(502), //
		SERVER_ERROR3(503), //
		SERVER_ERROR4(504), //
		SERVER_ERROR5(505), //
		;

		public final int code;

		private StatusEnum(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static StatusEnum parse(int code) {
			for (StatusEnum e : StatusEnum.values()) {
				if (e.getCode() == code) {
					return e;
				}
			}
			return StatusEnum.NONE;
		}
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
