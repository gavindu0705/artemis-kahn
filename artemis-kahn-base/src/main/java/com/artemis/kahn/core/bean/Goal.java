package com.artemis.kahn.core.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 抓取目标
 *
 * @author xiaoyu
 *
 */
public class Goal {
	public final static String CHARSET = "UTF-8";
	public final static int TIMEOUT = 10000;

	private String jobId;
	private String sessionId;
	private String url;
	private String charset;
	private String referer;
	private int timeout;
	private boolean renderWithJS; //是否需要JS渲染
	private Map<String, String> headers;

	public Goal(String jobId, String sessionId, String url, String charset, String referer) {
		this.jobId = jobId;
		this.sessionId = sessionId;
		this.url = url;
		this.charset = (charset == null ? CHARSET : charset);
		this.referer = referer;
		this.timeout = (timeout > TIMEOUT ? TIMEOUT : timeout);
	}

	public Goal(String jobId, String sessionId, String url, String charset, String referer, boolean renderWithJS) {
		this.jobId = jobId;
		this.sessionId = sessionId;
		this.url = url;
		this.charset = (charset == null ? CHARSET : charset);
		this.referer = referer;
		this.timeout = (timeout > TIMEOUT ? TIMEOUT : timeout);
		this.renderWithJS = renderWithJS;
	}

	public Goal(String jobId, String sessionId, String url, String charset, String referer, boolean renderWithJS, Map<String, String> headers) {
		this.jobId = jobId;
		this.sessionId = sessionId;
		this.url = url;
		this.charset = charset;
		this.referer = referer;
		this.headers = headers;
		this.renderWithJS = renderWithJS;
		this.timeout = (timeout > TIMEOUT ? TIMEOUT : timeout);
	}

	public Goal(String jobId, String sessionId, String url, String charset, String referer, Map<String, String> headers) {
		this.jobId = jobId;
		this.sessionId = sessionId;
		this.url = url;
		this.charset = charset;
		this.referer = referer;
		this.headers = headers;
		this.timeout = (timeout > TIMEOUT ? TIMEOUT : timeout);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
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

	public boolean isRenderWithJS() {
		return renderWithJS;
	}

	public void setRenderWithJS(boolean renderWithJS) {
		this.renderWithJS = renderWithJS;
	}

	public Map<String, Object> getAsParams() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("url", url);
		ret.put("charset", charset);
		ret.put("timeout", timeout);
		ret.put("referer", referer);
		ret.put("job_id", jobId);
		ret.put("session_id", sessionId);
		return ret;
	}

}
