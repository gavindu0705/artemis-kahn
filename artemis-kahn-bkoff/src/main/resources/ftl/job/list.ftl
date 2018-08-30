<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#compress>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>数据中心-任务列表</title>
    <link rel="stylesheet" type="text/css" href="/static/css/main.css" />
    <link rel="stylesheet" type="text/css" href="/static/css/my.css" />
	<#include "../common_js.ftl" />
    <script type="text/javascript">

    </script>
</head>
<body>
<div class="main-content">
    <div class="menuButton">
        <a href="/job/create" class="create">创建任务</a>
    </div>
    <table>
        <tr>
            <th>待抓取总量</th>
            <th>待处理总量</th>
            <th>待发布总量</th>
        </tr>
        <tr>
            <td id="urlsCount">获取中...</td>
            <td id="pendsCount">获取中...</td>
            <td id="metaCount">获取中...</td>
        </tr>
    </table>
    <div id = "table_box" style = "padding-top:10px;">
        <div style="background:#EDEEEF;margin:0 2px 5px 2px;padding-left:5px;border:1px solid #CCC;border-radius: 5px;">
            <ul class="smenu">
                <li>
                    <span class="cat">运行状态：</span>
                    <div class="item">
                        <a href="/job/list?cat=${cat!}&keyword=${keyword!}" <#if !(status??)>class="check"</#if> >不限</a>
                        <a href="/job/list?status=1&cat=${cat!}&keyword=${keyword!}" <#if status?? && status == 1>class="check"</#if>>运行中(${runningCount!0})</a>
                        <a href="/job/list?status=3&cat=${cat!}&keyword=${keyword!}" <#if status?? && status == 3>class="check"</#if>>暂停(${suspendCount!0})</a>
                    </div>
                </li>
                <li>
                    <#--<span class="cat">标&nbsp;&nbsp;签：</span>
                    <div class="item">
                        <a href="/job/list?status=${status!}&keyword=${keyword!}" <#if !(cat??) || (cat?? && cat='') >class="check"</#if>>不限</a>
							 <#list catList! as c>
					    		<a href="/job/list?cat=${c!}&status=${status!}&keyword=${keyword!}" <#if cat?? && c == cat!>class='check'</#if> >${c!}</a>
							 </#list>
                    </div>-->
                </li>
            </ul>
        </div>
        <div class="res2" style="margin:0 2px 5px 2px;padding-left:5px;display:block;height:25px;">
            <div class="stip">
                共查询到<b style="color:red;">${jobVoList?size}</b>条结果
            </div>
            <div class="skey">
 <#--               <form id="sform" action="list">
                    <input type="hidden" name="cat" value="${cat!}">
                    <input type="hidden" name="status" value="${status!}">
                    <input type="text" value="${keyword!}" name="keyword" id="keyword" style="width:400px;" placeholder="任务号、任务名称、根域名...">
                    <input type="submit" value="查询">
                    <a href="/job/list?cat=${c!}&status=${status!}" style="font-size:12px;">清除条件</a>
                </form>-->
            </div>
        </div>

	<#--
    <div>
        <ul class="nav-tabs" style="margin-bottom:-1px;">
            <li <#if !cat?? && (!status?? || status == -1)>class="active"</#if> >
                <a style="font-size: 12px;" href="/job/list?status=-1" class="list">全部(${jobCount!0})</a>
            </li>
            <li <#if !cat?? && status?? && status == 1>class="active"</#if>>
                <a style="font-size: 12px;" href="/job/list?status=1" class="list">运行中(${runningCount!0})</a>
            </li>
            <li <#if !cat?? && status?? && status == 3>class="active"</#if>>
                <a style="font-size: 12px;" href="/job/list?status=3" class="list">暂停(${suspendCount!0})</a>
            </li>
            <#list catList! as c>
                <li <#if cat! == c>class="active"</#if>>
                    <a href="/job/list?cat=${c!}" class="list">${c!}</a>
                </li>
            </#list>
          </ul>
      </div>
      -->

        <table class="list" >
            <tr>
                <th style="width:200px;">任务号</th>
                <th>名称</th>
                <th style="width:120px;text-align:left;">根域</th>
                <th style="width:35px;">定时</th>
                <th style="width:35px;">运行</th>
                <th style="width:35px;">权重</th>
                <th style="width:120px;text-align:center;">状态</th>
                <th style="width:240px;text-align:center;">操作</th>
            </tr>
				<#list jobVoList as it>
					<tr <#if it.status == 1>style="background-color: #D5FCD5;border: 1px solid #ccc"<#elseif it.status == 3>style="background-color: #D3DA8B;border: 1px solid #ccc"</#if> >
                        <td>
						<#--
                        <#if it.nonStop == true><img src="/images/loop.png" title="持续运行"></#if>
                        -->
							${it.id}
                        </td>
                        <td>
							<#if it.sessionId??>
                                <a href="/job/stat?jobId=${it.id}&sessionId=${it.sessionId}">${it.name}</a>
							<#else>
								${it.name}
							</#if>
                        </td>
                        <td>
							${it.root!}
                        </td>
                        <td>
							<#if it.interval == 0>
                                -
							<#else>
								${t_util.timesString(it.interval!)}
							</#if>
                        </td>
                        <td>
							<#if it.worktime?? && it.worktime gt 0>
								${t_util.timesString(it.worktime!)}
							<#else>
								不限
							</#if>
                        </td>
                        <td>
							<#if it.priority == 0>
                                普通
							<#elseif it.priority == 1>
								快速
							<#elseif it.priority == 2>
								最快
							</#if>
                        </td>
                        <td>
							<#if it.status == 1>
                                <span style="color:green;">运行</span>
                                <a href="javascript:if(confirm('确认停止？ 任务：【${it.name!}】'))location.href='/job/stop?jobId=${it.id}'"><span style="color:red;">停止</span></a>
                                <a href="javascript:if(confirm('确认暂停？ 任务：【${it.name!}】'))location.href='/job/suspend?jobId=${it.id}'"><span style="color:#A38A08">暂停</span></a>
							<#elseif it.status == 0>
								<span>待命</span>
								<a href="javascript:if(confirm('确认开始？ 任务：【${it.name!}】'))location.href='/job/start?jobId=${it.id}'">开始</a>
							<#elseif it.status == 2>
								<span>完成</span>
								<a href="javascript:if(confirm('确认开始？'))location.href='/job/start?jobId=${it.id}'">开始</a>
							<#elseif it.status == 3>
								<span style="color:#A38A08;">暂停</span>
								<a href="javascript:if(confirm('确认停止？ 任务：【${it.name!}】'))location.href='/job/stop?jobId=${it.id}'"><span style="color:red;">停止</span></a>
								<a href="javascript:if(confirm('确认恢复运行？'))location.href='/job/restart?jobId=${it.id}'"><span style="color:green;">恢复</span></a>
							</#if>
                        </td>
                        <td style="text-align:left;">
                            <a href="/job/create?jobId=${it.id}">编辑</a>
                            <a href="/page/list?jobId=${it.id}">配置</a>
                            <a href="/seed/list?jobId=${it.id}">种子</a>
                            <#--<a href="javascript:if(confirm('确认复制？')){location.href='/job/copyJob?jobId=${it.id}'}">复制</a>-->
                            <a name="cleanMeta" href="javascript:;" ref="/job/clean?jobId=${it.id}">清除</a>
                            <a name="deleteJob" href="javascript:;" ref="/job/delete?jobId=${it.id}">删除</a>
                        </td>
                    </tr>
				</#list>
        </table>
    </div>
</div>

<script type="text/javascript">
    $("a[id^='moveup_']").click(function(){
        var ref = $(this).attr("ref");
        $.get(ref, function(data){
            location.reload();
        });
    });

    $("a[id^='movetop_']").click(function(){
        if(confirm('确认置顶？')) {
            $.get($(this).attr("ref"), function(data){
                location.reload();
            });
        }
    });

    $("a[name='cleanMeta']").click(function(){
        if(confirm('确认清除？')) {
            jQuery.ajax({
                type: "get",
                url: $(this).attr("ref"),
                dataType:'json',
                success: function(data){
                    if(data.msg != "1") {
                        alert("清除失败！")
                    }
                    location.reload();
                }
            });
        }
    });

    $("a[name='deleteJob']").click(function(){
        if(confirm('确认删除任务？')) {
            jQuery.ajax({
                type: "get",
                url: $(this).attr("ref"),
                dataType:'json',
                success: function(data){
                    if(data.msg != "1") {
                        alert("删除失败！")
                    }
                    location.reload();
                }
            });
        }
    });

    $(function(){
        jQuery.ajax({
            type: "get",
            url: "../job/getCounts",
            dataType:'json',
            success: function(data){
                if(data.msg != "1") {
                    alert("查询失败！");
                } else {
                    $("#urlsCount").html(data.urlsCount);
                    $("#pendsCount").html(data.pendsCount);
                    $("#metaCount").html(data.metaCount);
                }
            }
        });
    });

</script>
</body>
</html>
</#compress>