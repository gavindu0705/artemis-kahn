<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#compress>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>数据中心-${job.name}-种子列表</title>
    <link rel="stylesheet" type="text/css" href="/static/css/main.css" />
	<#include "../common_js.ftl" />

    <style type="text/css">
        #tabs1 { text-align:left; width:100%;padding-top: 10px; }
        .menu1box { position:relative; overflow:hidden; height: 50px; text-align:left; }
        #menu1 { position:absolute; top:0; left:0; z-index:1; }
        #menu1 li { float:left; display:block; cursor:pointer;   width: 130px; text-align:center; line-height:21px; height: 50px; padding-top: 13px;  border: 1px solid #faf2cc;background: #cccccc;border-radius: 8px 8px 0px 0px;}
        #menu1 li.hover { background:#fff; border-left:1px solid #DBD3D3; border-top:1px solid #DBD3D3; border-right:1px solid #DBD3D3; }
        .main1box { clear:both; margin-top:-1px; border:1px solid #DBD3D3; height:181px; width:99%; margin-left: 2px;}
        #main1 ul { display: none; }
        #main1 ul.block { display: block; }
        #menu1 li a.hover{background: #9C9C9C}
    </style>

    <script type="text/javascript">
        $(document).ready(function(){
            $("a[id^='edit_']").click(function() {
                var idx = $(this).attr('id').replace('edit_', '');
                $("#ua_" + idx).hide();
                $("#pa_" + idx).hide();
                $("#ca_" + idx).hide();
                $("#edit_" + idx).hide();
                $("#clk_" + idx).show();
                $("#uipt_" + idx).show();
                $("#pipt_" + idx).show();
                $("#cipt_" + idx).show();
            });

            $("a[id^='clk_']").click(function() {
                var idx = $(this).attr('id').replace('clk_', '');
                var seedId = $("#seid_" + idx).val();
                var newSeed = $("#uipt_" + idx).val();
                var newParam = $("#pipt_" + idx).val();
                var newCharset = $("#cipt_" + idx).val();

                jQuery.ajax({
                    type: 'GET',
                    url: '/seed/edit',
                    data: "seedId=" + seedId + "&newSeed=" + encodeURIComponent(newSeed) + "&newParam=" + encodeURIComponent(newParam) + "&newCharset=" + encodeURIComponent(newCharset),
                    dataType: 'json',
                    timeout: 100000,
                    success: function(data) {
                        location.reload();
                    },
                    error : function() {

                    }
                });

            });
        });
    </script>
</head>
<body>
<div class="main-content">
    <div style="padding-top:10px;padding-left:10px;padding-left:10px;">
        <a href="/job/urlRoad?jobId=${job.id}&sessionId=${job.sessionId}">追踪</a>
        <a href="/job/listPage?jobId=${job.id}">配置</a>
        <a href="/job/testUrl?jobId=${job.id}">测试</a>
        <a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">运行状态</a>
    </div>
    <hr>
    <div style="padding-top:10px;padding-left:560px;">
        <a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">${job.name!}</a>
    </div>

    <div style = "padding-top:10px;">
        <form action="/seed/save" method="post">
            <table border=0 style="width:99%;">
                <tr>
                    <td>
                        <textarea name="seeds" style="width:100%;height:100px;"></textarea>
                        <input type="hidden" name="jobId" value="${job.id}">
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="submit" value="增加种子/模板"><span style="color:red;font-size:8px;">(一行一个)</span>
                    </td>
                </tr>
            </table>
        </form>
    </div>


    <div id="tabs1">
        <div class="menu1box">
            <ul id="menu1">
                <li <#if !status??>class="hover"</#if> ><a href="/list/seeds?jobId=${job.id}">全部种子(${seeds?size})</a></li>
            </ul>
        </div>
        <div class="main1box">
        <#--种子开始-->
            <div id = "table_box">
                <div style="padding:10px 5px 2px">
                    <table>
                        <tr>
                            <td>
										<#--<#if !keyword?? || keyword == ''>
                                            <input type="button" id="disableAll" value="禁用全部">
                                            <input type="button" id="enableAll" value="启用全部">
                                            <input type="button" id="deleteAll" value="删除全部">
                                        </#if>-->
                            </td>
                            <td style="text-align:right">
                                <#--<form id="queryForm" action="/job/jobSeed" method="get">
                                    <input type="hidden" name="status" value="${status!}">
                                    <input type="hidden" name="jobId" value="${job.id}">
                                    <input type="text" style="width:600px;" name="keyword" value="${keyword!}">
                                    <input id="queryBtn" type="button" value="查找">
                                </form>
                                <a href="/job/jobSeed?jobId=${job.id}">清除条件</a>-->
                            </td>
                        </tr>
                    </table>
                </div>

                <div style="padding:10px 5px 2px">
                    <form id="seedForm" method="post">
                        <input id="_jobId" type="hidden" name="jobId" value="${job.id}">
                        <input type="hidden" id="type" name="type" value="">
                        <table class="list">
                            <tr>
										<#if !showTemplate?? || showTemplate == 0>
											<th style="width:170px;">
                                                <input type="checkbox" id="checkall">
                                                <input type="button" id="disableChecked" value="禁用"><input type="button" id="enableChecked" value="启用"><input type="button" id="deleteChecked" value="删除">
                                            </th>
											<th style="width:110px;">操作</th>
                                        </#if>
                                <th>种子</th>
                                <th style="width:180px;">参数</th>
                                <th style="width:80px;">编码</th>
                                <th style="width:100px;">操作</th>
                                <th style="width:130px;">时间</th>
                            </tr>

									<#list seeds! as it>
										<tr>
                                            <td style="text-align:center;">
                                                <label for="urls_${it_index}" style="cursor:pointer;">
                                                    <input type="checkbox" id="ids_${it_index}" name="ids" value="${it.id!}" tabIndex=${it_index}>
													<#if it.status==0>
														正常
                                                    <#else>
														<span style="color:red;font-weight:bold;">禁用</span>
                                                    </#if>
                                                </label>
                                            </td>
                                            <td>
												<#--<#if !(it.isTemplate??) || !it.isTemplate>
                                                    <input type="button" value="追踪" onclick="javascript:window.open('/job/urlRoad?jobId=${job.id}&sessionId=${job.sessionId}&requestUrl=${t_util.encode(it.url)}')">
                                                    <input type="button" value="测试" onclick="javascript:window.open('/job/testUrl?jobId=${job.id}&charset=${it.charset!}&url=${t_util.encode(it.url)}&extraParams=${t_util.encode(it.params!)}')">
                                                </#if>-->
                                            </td>
                                            <td>
                                                <a href="<#if !(it.isTemplate??) || !it.isTemplate>${it.url!}<#else>javascript:;</#if>" id="ua_${it_index}" target="_blank">${it.url}</a>
                                                <input style="display:none;width:100%" id="uipt_${it_index}" type="text" name="url" value="${it.url!}" />
                                            </td>
                                            <td>
                                                <span id="pa_${it_index}">${it.params!}</span>
                                                <input style="display:none;width:100%" id="pipt_${it_index}" type="text" name="params" value="${it.params!}" />
                                            </td>
                                            <td>
                                                <span id="ca_${it_index}">${it.charset!}</span>
                                                <input style="display:none;width:100%" id="cipt_${it_index}" type="text" name="charset" value="${it.charset!}" />
                                            </td>
                                            <td>
                                                <input type="hidden" id="seid_${it_index}" value="${it.id}">
                                                <a id="edit_${it_index}" href="javascript:;">编辑</a>
                                                <a style="display:none;" id="clk_${it_index}" href="javascript:;">确定</a>
                                            </td>
                                            <td>
                                            </td>
                                        </tr>
                                    </#list>
                        </table>
                    </form>
                </div>
            </div>
        <#--种子结束-->
        </div>
    </div>
    <br />
    <br />
</div>

<script type="text/javascript">
    $("#checkall").click(function(){
        if($(this).attr('checked') == 'checked') {
            $("input[name='ids']").attr('checked', 'checked');
        }else {
            $("input[name='ids']").removeAttr('checked');
        }
    })

    $("#disableChecked").click(function(){
        var cbox = $("input[name='ids']:checked");
        if(cbox.length > 0) {
            if(confirm('确认禁用这' + cbox.length + "个种子？")) {
                var jobId = $("#_jobId").val();
                var idList = [];
                $.each(cbox, function(i, obox){
                    idList.push($(obox).val());
                })

                $.get(
                        '/seed/disable',
                        {jobId:jobId, ids:idList.join()},
                        function (data) {
                            location.reload();
                        }
                )
            }
        }else {
            alert("没有选中项！");
        }
    })

    $("#enableChecked").click(function(){
        var cbox = $("input[name='ids']:checked");
        if(cbox.length > 0) {
            if(confirm('确认启用这' + cbox.length + "个种子？")) {
                var jobId = $("#_jobId").val();
                var idList = [];
                $.each(cbox, function(i, obox){
                    idList.push($(obox).val());
                })

                $.post(
                        '/seed/enable',
                        {jobId:jobId, ids:idList.join()},
                        function (data) {
                            location.reload();
                        }
                )
            }
        }else {
            alert("没有选中项！");
        }
    })


    $("#deleteChecked").click(function(){
        var cbox = $("input[name='ids']:checked");
        if(cbox.length > 0) {
            if(confirm('确认删除这' + cbox.length + "个种子！！！？？？")) {
                var jobId = $("#_jobId").val();
                var idList = [];
                $.each(cbox, function(i, obox){
                    idList.push($(obox).val());
                })

                $.get(
                        '/seed/delete',
                        {jobId:jobId, ids:idList.join()},
                        function (data) {
                            location.reload();
                        }
                )
            }
        }else {
            alert("没有选中项！");
        }
    })

    $("#disableAll").click(function(){
        if(confirm('确认禁用所有种子吗？')) {
            var jobId = $("#_jobId").val();
            $.get(
                    '/job/disableSeed',
                    {type:'all', jobId:jobId},
                    function (data) {
                        location.reload();
                    }
            )
        }
    })

    $("#enableAll").click(function(){
        if(confirm('确认启用所有种子吗？')) {
            var jobId = $("#_jobId").val();
            $.get(
                    '/job/enableSeed',
                    {type:'all', jobId:jobId},
                    function (data) {
                        location.reload();
                    }
            )
        }
    })

    $("#deleteAll").click(function(){
        if(confirm('确认删除该任务的所有种子吗！！！？？？')) {
            var jobId = $("#_jobId").val();
            $.get(
                    '/job/delete',
                    {type:'all', jobId:jobId},
                    function (data) {
                        location.reload();
                    }
            )
        }
    })

    $("#queryBtn").click(function(){
        $("#queryForm").submit();
    })

</script>
</body>
</html>
</#compress>