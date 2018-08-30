<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#compress>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>数据中心-URL_ROAD</title>
	<link rel="stylesheet" type="text/css" href="/static/css/main.css" />
	<#include "../common_js.ftl" />
	<script type="text/javascript">
		
	</script>
	<style type="text/css">
		.box_ok {
            float: left;background: #61ec6d;border:  1px solid #ddd;width: 150px;height: 50px;
		}
		.box_err {
            float: left;background: #ec1a1f;border:  1px solid #ddd;width: 150px;height: 50px;
		}
	</style>
</head>
<body>

	<div class="main-content">
		<div style="padding-top:10px;padding-left:10px;padding-left:10px;">
			<a href="/job/create?jobId=${job.id}">编辑</a>
			<a href="/job/listPage?jobId=${job.id}">配置</a>
			<a href="/job/testUrl?jobId=${job.id}">测试</a>
			<a href="/job/jobSeed?jobId=${job.id}">种子</a>
			<a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">运行状态</a>
		</div>
		<hr>
		
		<div style="padding-top:10px;padding-left:480px;">
			<a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">${job.name!}</a>
		</div>
		
		<div id="table_box" style="padding-top:10px;padding-left:10px;">
			<form action="/job/urlRoad">
				<input type="hidden" name="jobId" value="${jobId!}">
				<input type="hidden" name="sessionId" value="${sessionId!}">
				<input id="requestUrl" type="text" name="requestUrl" value="${requestUrl!}" style="width:800px;" placeholder="需要追踪的链接...">
				<input id="submitBtn" type="submit" value="追踪">
			</form>
		</div>
		<br>
		<#if originRefererUrlList??>
            <div id = "table_box" style = "padding-top:10px;padding-left:10px;">
                <table style="border: 1px solid #2a9c05">
                    <tr>
                        <th>来源链接<small>(<strong style="color:red;">${originRefererUrlList?size!}</strong>)</small></th>
                    </tr>
					<#list originRefererUrlList as originRefererUrl>
                        <tr>
                            <td>
                                <input type="button" onclick="javascript:location.href='/job/urlRoad?jobId=${jobId!}&sessionId=${sessionId!}&requestUrl=${t_util.encode(originRefererUrl)}'" value="追踪" />
                                <input type="button" onclick="javascript:location.href='/job/showFileData?jobId=${jobId!}&sessionId=${sessionId!}&requestUrl=${t_util.encode(originRefererUrl)}'" value="内链" />
                                <a href="${originRefererUrl!}" target="_blank">${originRefererUrl!}</a>
                            </td>
                        </tr>
					</#list>
                </table>
            </div>
		</#if>
		<div>
			<#--<img style="float: left; padding-left: 25%;" src="/static/images/arrow_down.png"/>-->
		</div>
		<#if urlRoadMap??>
			<div id = "table_box" style = "padding-top:10px;padding-left:10px;">
				<table style="border: 1px solid #2a9c05">
					<tr>
						<th colspan="2">抓取流程<small>(<strong style="color:red;">${urlRoadMap?size!}</strong>)</small></th>
					</tr>
					<#if urlRoadMap?? && urlRoadMap?size gt 0>
						<tr>
							<td>
								<input type="button" onclick="javascript:location.href='/job/urlRoad?jobId=${jobId!}&sessionId=${sessionId!}&requestUrl=${t_util.encode(requestUrl)}'" value="追踪" />
								<input type="button" onclick="javascript:window.open('/job/showFileData?jobId=${jobId!}&sessionId=${sessionId!}&requestUrl=${t_util.encode(requestUrl)}')" value="内链" />
								<a href="${requestUrl!}" target="_blank">${requestUrl!}</a>
							</td>
						</tr>
						<#list urlRoadMap?keys as key>
							<tr>
								<td>
									<#assign finalStauts=0 />
									<#list urlRoadMap[key] as it>
										<#assign bt>
											<#if it.status == 3||it.status == 4||it.status == 5||it.status == 9||it.status == 10||it.status == 11||it.status == 12||it.status == 13>
												box_err
											<#else>
												box_ok
											</#if>
										</#assign>
										<div class="${bt}">
											<div style="margin-left: 6px;">
												<span>${t_util.formatDate(it.creationDate!, 'yy-MM-dd HH:mm:ss')}</span><br>
												<b>${urlsStatusCodeMap[it.status?string]}</b>
											</div>
										</div>
										<#if it_index lt (urlRoadMap[key]?size - 1)>
											<div style="float: left;">
												<img src="/static/images/forward.png" style="padding-top: 10px;"/>
											</div>
										</#if>
										<#assign finalStauts=it.status />
									</#list>

									<#if finalStauts == 8>
										<div style="float: left;">
											<img src="/static/images/forward.png" style="padding-top: 10px;"/>
										</div>
										<div style="float: left;">
											<img src="/static/images/done.png" />
										</div>
									<#elseif finalStauts gt 8>
										<div style="float: left;">
											<img src="/static/images/forward.png" style="padding-top: 10px;"/>
										</div>
										<div style="float: left;">
											<img src="/static/images/no.png" />
										</div>
									</#if>
								</td>
							</tr>
						</#list>
					</#if>
				</table>
			</div>
		</#if>
        <div>
            <img style="float: left; padding-left: 25%;" src="/static/images/arrow_down.png"/>
        </div>
		<#if targetRequestUrlList??>
			<div id = "table_box" style = "padding-top:10px;padding-left:10px;">
				<table style="border: 1px solid #2a9c05">
					<tr>
						<th>产生链接<small>(<strong style="color:red;">${targetRequestUrlList?size!}</strong>)</small></th>
					</tr>
					<#list targetRequestUrlList as targetRequestUrl>
						<tr>
							<td>
								<input type="button" onclick="javascript:location.href='/job/urlRoad?jobId=${jobId!}&sessionId=${sessionId!}&requestUrl=${t_util.encode(targetRequestUrl)}'" value="追踪" />
                                <input type="button" onclick="javascript:window.open('/job/showFileData?jobId=${jobId!}&sessionId=${sessionId!}&requestUrl=${t_util.encode(targetRequestUrl)}')" value="内链" />
								<a href="${targetRequestUrl!}" target="_blank">${targetRequestUrl!}</a>
							</td>
						</tr>
					</#list>
				</table>
			</div>
		</#if>
	</div>
</body>
</html>
</#compress>