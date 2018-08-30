<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#compress>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>${name!}全部运行记录</title>
    <link rel="stylesheet" type="text/css" href="/static/css/main.css" />
	<#include "../common_js.ftl" />
    <style type = "text/css"> th,td{text-align:center;}</style>
</head>
<body>
<div class="main-content">

    <div style="padding-top:10px;">
			<#if  jobStats??>
					<#list jobStats as it>
			<a href="/job/urlRoad?jobId=${it.jobId}&sessionId=${it.sessionId}">追踪</a>
			<a href="/page/list?jobId=${it.jobId}">配置</a>
			<a href="/job/testUrl?jobId=${it.jobId}" target="_blank">测试</a>
			<a href="/seed/list?jobId=${it.jobId}">种子</a>
						<#break>
					</#list>
			</#if>
    </div>
    <hr>

    <div id = "table_box" style = "padding-top:25px;">
        <table class = "showJobAllRecord" >
            <tr style="background-color: #6EC3C9;border: 1px solid #000000">
                <th>任务名</th>
                <th>会话ID</th>
                <th>开始时间</th>
                <th>结束时间</th>
                <th>网页总量</th>
                <th>处理总量</th>
                <th>异常总量</th>
                <th>元数据量</th>
            </tr>
				<#if  jobStats??>
					<#list jobStats as it>
						<tr style="background-color: #ECECEC;border: 1px solid #ffffff">
                            <td>${name!}</td>
                            <td>${it.sessionId}</td>
                            <td>${t_util.formatDate(it.startDate!, 'yyyy-MM-dd HH:mm:ss')}</td>
                            <td>
								<#if it.endDate??>
									${t_util.formatDate(it.endDate!, 'yyyy-MM-dd HH:mm:ss')}
								</#if>
                            </td>
                            <td>${it.crawlCount!}</td>
                            <td>${it.taskCount!}</td>
                            <td>${it.errCount!}</td>
                            <td>${it.metaCount!}</td>
                        </tr>
					</#list>
				</#if>
        </table>
    </div>
</div>

</body>

</html>
</#compress>