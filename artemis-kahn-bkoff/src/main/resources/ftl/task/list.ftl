<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#compress>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>数据中心-任务处理项</title>
	<link rel="stylesheet" type="text/css" href="/static/css/main.css" />

	<script type="text/javascript">
				
	</script>
</head>
<body>
	<div class="main-content">
		<div style="padding-top:10px;padding-left:10px;padding-left:10px;">
			<a href="/job/create?jobId=${job.id}">编辑</a>
			<a href="/job/urlRoad?jobId=${job.id}&sessionId=${job.sessionId}">追踪</a>
			<a href="/job/testUrl?jobId=${job.id}" target="_blank">测试</a>
			<a href="/job/jobSeed?jobId=${job.id}">种子</a>
		</div>
		<hr>
		<div>
			<h1 style="text-align:center;">${job.name}</h1>
		</div>
		<div class="menuButton">
			<a href="/task/create?jobId=${job.id}&pageId=${page.id}" class="create">创建</a>
		</div>
		<div style="padding-left:5px; font-size:10px;">
			>>&nbsp;<a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">${job.name}</a>
			/&nbsp;<a href="/page/list?jobId=${job.id}">${page.name}</a>&nbsp;/&nbsp;任务项
		</div>
		<div id = "table_box" style = "padding-top:5px;">
			<table >
				<tr>
                    <th>说明</th>
                    <th>操作类型</th>
					<th>变量名</th>
					<th>选择器</th>
                    <th>属性</th>
					<th>操作</th>
				</tr>
				<#list tasks as it>
					<tr class="odd">
						<td>
							<a href="/task/create?taskId=${it.id}&jobId=${job.id}&pageId=${page.id}">${it.caption!}</a>
						</td>
						<td>
							<#if it.clazz! == 'TEXT'>
								文本提取
							<#elseif it.clazz! == 'HTML'>
                                HTML提取
							<#elseif it.clazz! == 'ATTR'>
								属性提取
							<#elseif it.clazz! == 'LINK'>
								链接提取
							<#elseif it.clazz! == 'SOURCE'>
								源码提取
							<#elseif it.clazz! == 'CUTSOURCE'>
                                源码截取
							<#elseif it.clazz! == 'CLICK'>
								点击链接进入
							<#elseif it.clazz! == 'ATTR_CLICK'>
								属性提取进入
							<#elseif it.clazz! == 'TEXT_CLICK'>
                                文本提取进入
							<#elseif it.clazz! == 'CUTSOURCE_CLICK'>
                                截取源码进入
							</#if>
						</td>
                        <td>${it.key!}</td>
                        <td>${it.selector!}</td>
                        <td>${it.attr!}</td>
						<td>
							<a href="javascript:if(confirm('确认删除？'))location.href='/task/delete?id=${it.id}&jobId=${job.id}&pageId=${page.id}'">删除</a>
						</td>
					</tr>
				</#list>
			</table>
		</div>
	</div>
</body>
</html>
</#compress>