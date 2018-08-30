<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#compress>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>数据中心-${job.name}-测试抓取</title>
	<link rel="stylesheet" type="text/css" href="/static/css/main.css" />
	<#include "../common_js.ftl" />

	<script type="text/javascript">
				
	</script>
</head>
<body>
	<div class="main-content">
		<div style="padding-top:10px;padding-left:10px;padding-left:10px;">
			<a href="/job/urlRoad?jobId=${job.id}&sessionId=${job.sessionId}">追踪</a>
			<a href="/page/list?jobId=${job.id}">配置</a>
			<a href="/seed/list?jobId=${job.id}">种子</a>
			<a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">运行状态</a>
		</div>
		<hr>
		<div style="padding-top:10px;padding-left:10px;padding-left:480px;">
			<a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">${job.name!}</a>
		</div>		
		<div class="menuButton">
			<form id="testForm" action="/job/testUrl">
				<input type="hidden" name="jobId" value="${job.id}">
				<table class="list">
					<tr>
						<td>URL:</td>
						<td>
							<input type="text" name="url" value="${url!}" style="width:360px;"><#if msg??><span style="color:red;font-size:8px;">${msg!}</span></#if>
						</td>
					</tr>
					<tr>
						<td>参数:</td>
						<td>
							<input type="text" name="extraParams" value="${extraParams!}" style="width:360px;">
						</td>
					</tr>
					<tr>
						<td>编码:</td>
						<td>
							<input type="text" name="charset" value="${charset!}" style="width:360px;">
						</td>
					</tr>
                    <tr>
                        <td>显示源码:</td>
                        <td>
                            <input type="radio" name="showContent" value="0" <#if showContent?? && showContent! == 0>checked</#if> />否
                            <input type="radio" name="showContent" value="1" <#if showContent?? && showContent! == 1>checked</#if> />是
                        </td>
                    </tr>
					<tr>
						<td>&nbsp;</td>
						<td>
                            <input id="startTest" type="submit" value="开始测试">
						</td>
					</tr>

				</table>
			</form>
		</div>
		
		<div id = "table_box" style = "padding-top:2px;">
			<table class="list">
                <tr>
					<td style="width: 120px;">抓取代理</td>
                    <td>${captor!}</td>
                </tr>
				<#if showContent?? && showContent == 1>
					<tr>
						<td style="width: 120px;">源码</td>
						<td>
							<textarea rows="5" cols="100" style="width: 100%">
								${content!}
							</textarea>
						</td>
					</tr>
				</#if>
			</table>
			<#if hrefs??>
				<table class="list">
					<tr>
						<th style="width:60px;">&nbsp;</th>
						<th style="width:860px;">URL (${hrefs?size}条)</th>
						<th style="width:40px;">编码</th>
						<th style="width:80px;">参数</th>
					</tr>
					<#list hrefs as it>
						<tr>
							<td>
								<input type="button" value="测试" onclick="javascript:location.href='/job/testUrl?jobId=${job.id}&charset=${charset!}&url=${t_util.encode(it.id!)}&extraParams=${t_util.encode(t_util.mapToString(it.params!, ','))}'">
                                <#--<input type="button" value="检查" onclick="javascript:location.href='http://192.168.0.62:8077/artemis-task/task?jobId=${job.id}&sessionId=${job.sessionId!}&url=${t_util.encode(it.id!)}'">-->
							</td>
							<td><a href="${it.id}" target="_blank">${it.id}</a></td>
							<td>${it.charset!}</td>
							<td>${t_util.mapToString(it.params!, ',')}</td>
						</tr>
					</#list>
				</table>
			</#if>
			<hr>
			<#if metadata??>
				<table class="list">
					<tr>
						<td style="width:60px;">URL：</td>
						<td>${metadata.url!}</td>
					</tr>
					<tr>
						<td>cat：</td>
						<td>${metadata.cat!}</td>
					</tr>
					<tr>
						<td>JOB_ID：</td>
						<td>${metadata.jobId!}</td>
					</tr>
					<tr>
						<td>数据：</td>
						<td>
							${t_util.metadataToString(metadata.data!, '<br>')}
						</td>
					</tr>
				</table>
			</#if>
		</div>

	</div>
	
	<script type="text/javascript">
		$("#startTest").click(function(){
			$(this).val("抓取中...");
			$(this).attr("disabled", true);
			$("#testForm").submit();
		});
	</script>
</body>
</html>
</#compress>