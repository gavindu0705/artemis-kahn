<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#compress>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>数据中心-任务页面</title>
	<link rel="stylesheet" type="text/css" href="/static/css/main.css" />

	<script type="text/javascript">
				
	</script>
</head>
<body>
	<div class="main-content">
		<div style="padding-top:10px;padding-left:10px;padding-left:10px;">
			<a href="/job/urlRoad?jobId=${job.id}&sessionId=${job.sessionId}">追踪</a>
			<a href="/job/testUrl?jobId=${job.id}" target="_blank">测试</a>
			<a href="/job/jobSeed?jobId=${job.id}">种子</a>
			<a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">运行状态</a>
		</div>
		<hr>
		
		<div>
			<h1 style="text-align:center;">
				<a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">${job.name}</a>
			</h1>
		</div>
		
		<div class="menuButton">
			<a href="/page/create?jobId=${job.id}" class="create">创建页面</a>
		</div>
		<div id = "table_box" style = "padding-top:2px;">
			<table class="list" >
				<tr>
					<th>页面名称</th>
					<th>匹配正则</th>
					<th>任务</th>
					<th>发现周期</th>
					<th>创建时间</th>
					<th>操作</th>
				</tr>
				<#list pages as it>
					<tr class="odd">
						<td>
							<a href="/job/createPage?jobId=${job.id}&pageId=${it.id}">${it.name}</a>
						</td>
						<td>
							<#if it.patterns??>
							<#list it.patterns as p>
								${p}<br>
							</#list>
							</#if>
						</td>
						<td>
							<#if it.tasks??>
								<a href="/task/list?pageId=${it.id}&jobId=${job.id}">任务项</a>(${it.tasks?size})
							<#else>
								<a href="/task/list?pageId=${it.id}&jobId=${job.id}">任务项</a>(0)
							</#if>
						</td>
						<td>
							<#if it.expires! == -1>
								当次抓取
							<#else>
								${t_util.timesString(it.expires! * 60)}
							</#if>
						</td>
						<td>--</td>
						<td>
							<a href="javascript:if(confirm('确认删除？页面：${it.name!}'))location.href='/job/deletePage?pageId=${it.id}&jobId=${job.id}'">删除</a>
						</td>
					</tr>
				</#list>
			</table>
		</div>
	</div>
</body>
</html>
</#compress>