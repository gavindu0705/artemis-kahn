<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#compress>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>数据中心-创建Job</title>
	<link rel="stylesheet" type="text/css" href="/static/css/main.css" />
	<script type="text/javascript">
	</script>
</head>
<body>
	<div class="main-content">
        <div class="menuButton">
            <a href="/job/list" class="create">任务列表</a>
        </div>

		<div id = "table_box">
			<form action="/job/save" onsubmit="return submitForm();">
				<table>
					<tr>
						<th style="width: 200px;">列</th>
                        <th>值</th>
					</tr>
					<tr>
						<td>名称</td>
						<td>
							<#if job??>
								<input type="hidden" name="id" value="${job.id}">
							</#if>
							<input style="width:260px;" type="text" name="name" value="<#if job??>${job.name}</#if>">
						</td>
					</tr>
					<tr>
						<td>标签</td>
						<td>
							<input style="width:260px;" type="text" name="cat" value="<#if job??>${job.cat!}</#if>" >
						</td>
					</tr>
					<tr>
						<td>根域名</td>
						<td>
                            <input style="width:260px;" type="text" name="root" value="<#if job??>${job.root!}</#if>" >
						</td>
					</tr>
					<tr>
						<td>优先级</td>
						<td>
							<input id="priority0" name="priority" type="radio" value="0" <#if !(job??) || (job?? && job.priority==0)>checked</#if>/><label for="priority0">普通</label>
                            <input id="priority1" name="priority" type="radio" value="1" <#if job?? && job.priority==1>checked</#if>/><label for="priority1">快速</label>
                            <input id="priority2" name="priority" type="radio" value="2" <#if job?? && job.priority==2>checked</#if>/><label for="priority2">最快</label>
						</td>
					</tr>
					<tr>
						<td>定时器</td>
						<td>
							<select style="width:260px;" name="interval">
								<option value="0">不需要定时器</option>
								<option value="3600" <#if job?? && job.interval==3600>selected</#if> >1小时</option>
								<option value="7200" <#if job?? && job.interval==7200>selected</#if> >2小时</option>
								<option value="14400" <#if job?? && job.interval==14400>selected</#if> >4小时</option>
								<option value="28800" <#if job?? && job.interval==28800>selected</#if> >8小时</option>
								<option value="43200" <#if job?? && job.interval==43200>selected</#if> >12小时</option>
								<option value="86400" <#if job?? && job.interval==86400>selected</#if> >1天</option>
								<option value="172800" <#if job?? && job.interval==172800>selected</#if> >2天</option>
								<option value="259200" <#if job?? && job.interval==259200>selected</#if> >3天</option>
                                <option value="345600" <#if job?? && job.interval==345600>selected</#if> >4天</option>
                                <option value="432000" <#if job?? && job.interval==432000>selected</#if> >5天</option>
                                <option value="518400" <#if job?? && job.interval==518400>selected</#if> >6天</option>
                                <option value="604800" <#if job?? && job.interval==604800>selected</#if> >7天</option>
                                <option value="864000" <#if job?? && job.interval==864000>selected</#if> >10天</option>
                                <option value="1209600" <#if job?? && job.interval==1209600>selected</#if> >14天</option>
                                <option value="1814400" <#if job?? && job.interval==1814400>selected</#if> >21天</option>
                                <option value="2592000" <#if job?? && job.interval==2592000>selected</#if> >30天</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>运行时长</td>
						<td>
							<select style="width:260px;" name="worktime">
								<option value="0">运行时长不限</option>
								<option value="3600" <#if job?? && job.worktime==3600>selected</#if> >1小时</option>
								<option value="7200" <#if job?? && job.worktime==7200>selected</#if> >2小时</option>
								<option value="14400" <#if job?? && job.worktime==14400>selected</#if> >4小时</option>
								<option value="28800" <#if job?? && job.worktime==28800>selected</#if> >8小时</option>
								<option value="43200" <#if job?? && job.worktime==43200>selected</#if> >12小时</option>
								<option value="86400" <#if job?? && job.worktime==86400>selected</#if> >1天</option>
								<option value="172800" <#if job?? && job.worktime==172800>selected</#if> >2天</option>
								<option value="259200" <#if job?? && job.worktime==259200>selected</#if> >3天</option>
                                <option value="345600" <#if job?? && job.worktime==345600>selected</#if> >4天</option>
                                <option value="432000" <#if job?? && job.worktime==432000>selected</#if> >5天</option>
                                <option value="518400" <#if job?? && job.worktime==518400>selected</#if> >6天</option>
								<option value="604800" <#if job?? && job.worktime==604800>selected</#if> >7天</option>
                                <option value="864000" <#if job?? && job.worktime==864000>selected</#if> >10天</option>
							</select>
						</td>
					</tr>
					<#--
					<tr>
						<td>任务性质</td>
                        <td>
                            <input id="mosi0" name="job.mosi" type="radio" value="0" <#if !(job??) || (job?? && job.mosi == 0)>checked</#if> /><label for="mosi0">Normal</label>
                            <input id="mosi1" name="job.mosi" type="radio" value="1" <#if job?? && job.mosi==1>checked</#if>/><label for="mosi1">Finder</label>
						</td>
					</tr>
					-->

                    <tr>
                        <td>运行方式</td>
                        <td>
                            <input id="crawlWay0" name="crawlWay" type="radio" value="0" <#if !(job??) || (job?? && job.crawlWay == 0)>checked</#if> /><label for="crawlWay0">性能优先</label>
                            <input id="crawlWay1" name="crawlWay" type="radio" value="1" <#if job?? && job.crawlWay==1>checked</#if>/><label for="crawlWay1">准度优先</label>
                        </td>
                    </tr>

					<#--
                    <tr>
                        <td>停止方式</td>
                        <td>
                            <input id="nonStop0" name="job.nonStop" type="radio" value="0" <#if !(job??) || (job?? && !job.nonStop)>checked</#if>/><label for="nonStop0">自动停止任务</label>
                            <input id="nonStop1" name="job.nonStop" type="radio" value="1" <#if job?? && job.nonStop>checked</#if>/><label for="nonStop1">不自动停止任务</label>
                        </td>
                    </tr>
					-->

					<tr>
						<td>序号</td>
						<td>
							<input type="text" name="sequence" size="5" value="<#if job??>${job.sequence!}</#if>" >
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>
							<input type="submit" id="submit" value="提交">
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	
	<script type="text/javascript">

	</script>
</body>
</html>
</#compress>