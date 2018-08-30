<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>数据中心-创建处理任务</title>
	<link rel="stylesheet" type="text/css" href="/static/css/main.css" />
    <link rel="stylesheet" type="text/css" href="/static/css/codemirror/codemirror.css">
	<#include "../common_js.ftl" />
    <script src="/static/css/codemirror/codemirror.js"></script>
    <script src="/static/css/codemirror/addon/edit/matchbrackets.js"></script>
    <script src="/static/css/codemirror/mode/groovy.js"></script>

</head>
<body>
	<div class="main-content">
		<div style="padding-top:10px;padding-left:10px;padding-left:10px;">
			<a href="/job/create?jobId=${job.id}">编辑</a>
			<a href="/job/urlRoad?jobId=${job.id}&sessionId=${job.sessionId}">追踪</a>
			<a href="/job/testUrl?jobId=${job.id}" target="_blank">测试</a>
			<a href="/seed/list?jobId=${job.id}">种子</a>
		</div>
		<hr>
		<div>
			<h1 style="text-align:center;">${job.name}</h1>
		</div>
		
		<div style="padding-left:5px; font-size:10px;">
			>>&nbsp;<a href="/job/stat?jobId=${job.id}&sessionId=${job.sessionId}">${job.name}</a>
			/&nbsp;<a href="/page/list?jobId=${job.id}">${page.name}</a>&nbsp;
			/&nbsp;<a href="/task/list?jobId=${job.id}&pageId=${page.id}">任务项</a>&nbsp;
			<#if task??>
				/&nbsp;编辑任务
			<#else>
				/&nbsp;创建任务
			</#if>
		</div>
		
		<div id = "table_box" style = "padding-top:5px;">
			<form id="mainForm" action="/task/save" method="post">
				<input type="hidden" name="jobId" value="${job.id}">
				<input type="hidden" name="pageId" value="${page.id}">
				<#if task??>
					<input type="hidden" name="id" value="${task.id}">
				</#if>
				<table>
					<tr>
						<th style="width: 230px;">列</th>
                        <th>值</th>
					</tr>
					<tr>
						<td>处理方案</td>
						<td>
							<#if task??>
								<#if task.clazz == 'TEXT' || task.clazz == 'com.artemis.core.resovler.JqueryTextSelector'>
                                    文本提取
								<#elseif task.clazz == 'HTML' || task.clazz == 'com.artemis.core.resovler.JqueryHtmlSelector'>
                                    HTML提取
								<#elseif task.clazz == 'ATTR' || task.clazz == 'com.artemis.core.resovler.JqueryAttrSelector'>
                                    属性提取
								<#elseif task.clazz == 'CLICK' || task.clazz == 'com.artemis.core.resovler.JqueryAllLinksSelector'>
                                    点击链接进入
								<#elseif task.clazz == 'ATTR_CLICK'>
                                    属性提取进入
								<#elseif task.clazz == 'TEXT_CLICK'>
                                    文本提取进入
								<#elseif task.clazz == 'CUTSTR_CLICK'>
                                    截源码进入
								<#elseif task.clazz == 'SOURCE' || task.clazz == 'com.artemis.core.resovler.PageHtmlSelector'>
                                    源码提取
								<#elseif task.clazz == 'LINK'>
                                    点击链接
								<#elseif task.clazz == 'CUTSTR'>
									截源码
								</#if>
								<input type="hidden" name="clazz" value="${task.clazz!}">
							<#else>
								<select id="_clazz" name="clazz">
									<option>请选择</option>
									<optgroup label="提取类">
										<option value="TEXT" <#if clazz! == 'TEXT'>selected</#if> >文本提取</option>
										<option value="HTML" <#if clazz! == 'HTML'>selected</#if>>HTML提取</option>
										<option value="ATTR" <#if clazz! == 'ATTR'>selected</#if>>属性提取</option>
										<option value="LINK" <#if clazz! == 'LINK'>selected</#if>>链接提取</option>
                                        <option value="CUTSOURCE" <#if clazz! == 'CUTSOURCE'>selected</#if>>源码截取</option>
										<option value="SOURCE" <#if clazz! == 'SOURCE'>selected</#if>>源码提取</option>
									</optgroup>
									<optgroup label="操作类">
										<option value="CLICK" <#if clazz! == 'CLICK'>selected</#if>>点击链接进入</option>
                                        <option value="ATTR_CLICK" <#if clazz! == 'ATTR_CLICK'>selected</#if>>属性提取进入</option>
                                        <option value="TEXT_CLICK" <#if clazz! == 'TEXT_CLICK'>selected</#if>>文本提取进入</option>
                                        <option value="CUTSOURCE_CLICK" <#if clazz! == 'CUTSOURCE_CLICK'>selected</#if>>源码截取进入</option>
									</optgroup>
								</select>
							</#if>
						</td>
					</tr>
					<#if clazz! == 'TEXT' || clazz! == 'HTML' || clazz! == 'ATTR' || clazz! == 'LINK' || clazz! == 'SOURCE' || clazz! == 'CUTSOURCE'>
						<tr>
							<td>变量名</td>
							<td>
								<input type="text" placeholder="对应metadata中data.key" name="key" value="<#if task??>${task.key!}</#if>" style="width:600px;"/>
							</td>
						</tr>
					</#if>
					<#if clazz! == 'TEXT' || clazz! == 'HTML' || clazz! == 'ATTR' || clazz! == 'LINK' || clazz! == 'CLICK' || clazz! == 'ATTR_CLICK' || clazz! == 'TEXT_CLICK'>
                        <tr>
                            <td>选择器</td>
                            <td>
                                <input type="text" placeholder="JQuery选择器" name="selector" value="<#if task??>${task.selector!}</#if>" style="width:600px;"/>
                            </td>
                        </tr>
					</#if>

					<#if clazz! == 'ATTR' || clazz! == 'ATTR_CLICK'>
						<tr>
							<td>属性</td>
							<td>
								<input type="text" placeholder="元素属性" name="attr" value="<#if task??>${task.attr!}</#if>" style="width:600px;"/>
							</td>
						</tr>
					</#if>

					<#if clazz! == 'TEXT' || clazz! == 'HTML' || clazz! == 'ATTR' || clazz! == 'LINK' || clazz! == 'SOURCE' || clazz! == 'CUTSOURCE' || clazz! == 'CLICK' || clazz! == 'ATTR_CLICK' || clazz! == 'TEXT_CLICK' || clazz! == 'CUTSOURCE_CLICK'>
						<tr>
							<td>说明</td>
							<td>
								<input type="text" placeholder="简要描述该字段" name="caption" value="<#if task??>${task.caption!}</#if>" style="width:600px;">
							</td>
						</tr>
                        <tr>
                            <td>脚本</td>
                            <td>
								<textarea id="shell" name="shell" rows="100" cols="120"><#if task?? && task.shell??>${task.shell!}<#else>${shellTemplate!}</#if> </textarea>
								<a href="gshell" style="font-size:8px;float: right;" target="_blank">查看功能方法</a>
                            </td>
                        </tr>
					</#if>

					<tr>
						<td>&nbsp;</td>
						<td>
							<input id="subBtn" type="button" value="提交" >
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>

	<script type="text/javascript">
		$("#_clazz").change(function(){
			location.href = "/task/create?jobId=${job.id!}&pageId=${page.id!}&clazz=" + $(this).val();
		})

        var editor = CodeMirror.fromTextArea(document.getElementById("shell"), {
            lineNumbers: true,
            matchBrackets: true,
            mode: "text/x-groovy"
        });

        $("#subBtn").click(function(){
            $.post(
				"validateShell",
				{shell : editor.getValue()},
				function(data) {
					if(data.status != "1") {
						alert("脚本编译错误！无法保存！")
					}else {
						$("#mainForm").submit();
					}
				}
            )

            return false;
        })

	</script>

</body>
</html>