<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#compress>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>数据中心-创建页面</title>
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
		<br>
		<div>
			<h1 style="text-align:center;">
				【${job.name}】
			</h1>
		</div>
		
		<div style="padding-left:5px; font-size:10px;">
			>>&nbsp;<a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">${job.name}</a>
			<#if page??>
				/&nbsp;<a href="/job/listPage?jobId=${job.id}">页面规则</a>&nbsp;/&nbsp;${page.name}
			 <#else>
			 	/&nbsp;<a href="/job/listPage?jobId=${job.id}">页面规则</a>&nbsp;/&nbsp;创建
			 </#if>
		</div>
		
		<div id = "table_box" style = "padding-left:5px; padding-top:5px;">
			<form action="/page/save">
				<table>
					<tr>
						<td>名称</td>
						<td>
							<input type="hidden" name="jobId" value="${job.id}">
							<#if page??>
								<input type="hidden" name="id" value="${page.id}">
							</#if>
							<input type="text" name="name" value="<#if page??>${page.name}</#if>">
						</td>
					</tr>
					<tr>
						<td>PASS PATTERN</td>
						<td>
							<textarea name="patternStr" style="width:900px;height:80px;"><#if page?? && page.patterns??>${t_util.join(page.patterns!, '\r\n')}</#if></textarea>
						</td>
					</tr>

                    <tr>
                        <td>错误标签</td>
                        <td>
                            <textarea name="errTagStr" style="width:900px;height:80px;"><#if page?? && page.errTag??>${t_util.join(page.errTag!, '\r\n')}</#if></textarea>
                        </td>
                    </tr>

                    <tr>
                        <td>正确标签</td>
                        <td>
                            <textarea name="sucTagStr" style="width:900px;height:80px;"><#if page?? && page.sucTag??>${t_util.join(page.sucTag!, '\r\n')}</#if></textarea>
                        </td>
                    </tr>

                    <tr>
                        <td>发现周期</td>
                        <td>
							<select name="expires">
                                <option value="-1" <#if page?? && page.expires == -1>selected</#if>>当次抓取</option>
                                <option value="60" <#if page?? && page.expires == 60>selected</#if>>1小时</option>
                                <option value="1440" <#if page?? && page.expires == 1440>selected</#if>>1天</option>
                                <option value="10080" <#if page?? && page.expires == 10080>selected</#if>>7天</option>
                                <option value="43200" <#if page?? && page.expires == 43200>selected</#if>>30天</option>
                                <option value="525600" <#if page?? && page.expires == 525600>selected</#if>>1年</option>
							</select>
                        </td>
                    </tr>

					<tr>
						<td>&nbsp;</td>
						<td>
							<input type="submit" value="提交">
						</td>
					</tr>
				</table>
			</form>
			
		</div>
	</div>
</body>
</html>
</#compress>