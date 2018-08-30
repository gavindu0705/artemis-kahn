<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#compress>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>数据中心-运行状态-${jobVo.name!}</title>
	<link rel="stylesheet" type="text/css" href="/static/css/main.css" />
	<#include "../common_js.ftl" />
	<style type="text/css">
		.loading{width: 24px;height: 25px;}
	</style>
</head>
<body>
	<div class="main-content">
		<div style="padding-top:10px;padding-left:10px;padding-left:10px;">
			<a href="/job/urlRoad?jobId=${jobVo.id}&sessionId=${jobVo.sessionId}">追踪</a>
			<a href="/job/listPage?jobId=${jobVo.id}">配置</a>
			<a href="/job/testUrl?jobId=${jobVo.id}" target="_blank">测试</a>
			<a href="/job/jobSeed?jobId=${jobVo.id}">种子</a>
		</div>
		<hr>
		
		<div style="padding-top:10px;padding-left:10px;">
			<table border=1 style="width:800px;">
				<tr>
					<td colspan="4" style="text-align:center;">
						<h1>${jobVo.name!}</h1>
						<a style = "color:red;"href="/job/record?jobId=${jobVo.id}&sessionId=${jobVo.sessionId}">(查看运行记录)</a>
					</td>
				</tr>
				<tr>
					<td style="width:120px;">
						任务号
					</td>
					<td style="width:260px;">
						<h1>${jobVo.id}</h1>
					</td>
                    <td style="width:200px;">根域名</td>
                    <td style="width:260px;">${jobVo.root!}</td>
				</tr>
				<tr>
                    <td>定时器</td>
                    <td>
						<#if jobVo.interval == 0>
                            无
						<#else>
						${t_util.timesString(jobVo.interval!)}
						</#if>
                    </td>
                    <td>运行方式</td>
                    <td>
						<#if jobVo.crawlWay == 0>
                            性能优先
						<#elseif jobVo.crawlWay == 1>
                            准度优先
						</#if>
					</td>
				</tr>
				<tr>
                    <td>运行时长</td>
                    <td>
						<#if jobVo.worktime?? && jobVo.worktime gt 0>
						${t_util.timesString(jobVo.worktime!)}
						<#else>
                            不限
						</#if>
                    </td>
                    <td>优先级</td>
                    <td>
						<#if jobVo.priority == 0>
                            普通
						<#elseif jobVo.priority == 1>
                            快速
						<#elseif jobVo.priority == 2>
                            最快
						</#if>
					</td>
				</tr>
                <tr>
                    <td>开始时间</td>
                    <td><#if jobVo.startDate??>${t_util.formatDate(jobVo.startDate!, 'yy-MM-dd HH:mm:ss')}</#if></td>
                    <td>状态</td>
                    <td>
						<#if jobVo.status == 1>
							<#if jobVo.status == 1>
                                <img  src="/static/images/light.png" style="width:12px;height:12px;">
							</#if>
                            <span style="font-weight: bold;color:green;">运行中</span>
                            (
                            <a href="javascript:if(confirm('确认停止？'))location.href='/job/stop?jobId=${jobVo.id}'"><span style="color:red;font-size: 10px;">停止</span></a>
                            <a href="javascript:if(confirm('确认停止？'))location.href='/job/suspend?jobId=${jobVo.id}'"><span style="color:#A38A08;font-size: 10px;">暂停</span></a>
                            )
						<#elseif jobVo.status == 0>
                            <span>待命</span>
                            <a href="javascript:if(confirm('确认开始？'))location.href='/job/start?jobId=${jobVo.id}'">开始</a>
						<#elseif jobVo.status == 2>
                            <span>完成</span>
                            <a href="javascript:if(confirm('确认开始？'))location.href='/job/start?jobId=${jobVo.id}'">开始</a>
						<#elseif jobVo.status == 3>
                            <span>暂停</span>
                            (
                            <a href="javascript:if(confirm('确认停止？'))location.href='/job/stop?jobId=${jobVo.id}'"><span style="color:red;font-size: 10px;">停止</span></a>
                            <a href="javascript:if(confirm('确认重启？'))location.href='/job/restart?jobId=${jobVo.id}'"><span style="color:green;font-size: 10px;">恢复</span></a>
                            )
						</#if>
                    </td>
                </tr>
				<tr>
                    <td>结束时间</td>
                    <td><#if jobVo.endDate??>${t_util.formatDate(jobVo.endDate!, 'yy-MM-dd HH:mm:ss')}</#if></td>
                    <td>Metadata待处理</td>
                    <td><span style="color: #D60404;font-weight: bold;">${jobMetaCount!}</span></td>
				</tr>
				<tr>
					<td>网页抓取量</td>
					<td>${jobVo.crawlCount!}</td>
					<td>异常总量</td>
					<td>${jobVo.errCount!}</td>
				</tr>
				<tr>
					<td>网页分析量</td>
					<td>${jobVo.taskCount!}</td>
					<td>Metadata总量</td>
					<td>
						<span style="font-weight: bold;">${jobVo.metaCount!}</span>
					</td>
				</tr>
			</table>

            <br>
            <table border=1 style="width:95%;">
                <tr>
                    <th colspan="${urlsStatusNameMap?keys?size}">运行详细状态</th>
                </tr>
                <tr style="font-size: 10px;font-weight: bold;">
					<#list urlsStatusNameMap?keys as key>
                        <td style="text-align: center;">${key}</td>
					</#list>
                </tr>

                <tr style="height: 42px;">
					<#list urlsStatusNameMap?keys as key>
                        <td style="text-align: center;">
                            <a id="stat_${key}" status="${key!}" vstatus="${urlsStatusNameMap[key!]}" href="javascript:;">
								<img id="stat_${key}_img" class="loading" src="/static/images/loading.gif">
								<span id="stat_${key}_span" style="display: none;"></span>
							</a>
                        </td>
					</#list>
                </tr>
            </table>

			<div id="details"></div>
		</div>
	</div>
	<script type="text/javascript">
        var _interval = setInterval(function(){
            $.each($("a[id^='stat_']"), function(i, obj) {
				var id = $(obj).attr('id');
                $.get('/job/statusCount?jobId=${jobVo.id!}&status='+$(obj).attr('vstatus'), function(data){
                    $("#" + id + "_img").hide();
                    $("#" + id + "_span").show();
                    $("#" + id + "_span").html(data.count);
                })
            })
            clearInterval(_interval);
        }, 3000);

        $("a[id^='stat_']").bind('click', function(){
            $("#details").html('<img src="/static/images/loading.gif">');

			var obj = this;
            var id = $(obj).attr('id');
            $.get('/job/statusCount?jobId=${jobVo.id!}&status='+$(obj).attr('vstatus'), function(data){
                $("#" + id + "_img").hide();
                $("#" + id + "_span").show();
                $("#" + id + "_span").html(data.count);
            })

			$("#details").load('/job/statDetail?jobId=${jobVo.id!}&status=' + $(obj).attr('vstatus'));
        })
	</script>
</body>
</html>
</#compress>