<br>
<table border="1" style="padding-top: 10px;width:95%;">
    <tr>
        <th colspan="3">
            ${statusEnum!}详细运行记录
        </th>
    </tr>
    <#if jobVo?? && ((urlsList?? && urlsList?size gt 0) || (pendsList?? && pendsList?size gt 0))>
        <#list urlsList! as it>
            <tr>
                <td style="width: 160px;">${t_util.formatDate(it.creationDate!, 'yyyy-MM-dd HH:mm:ss')}</td>
                <td style="width: 110px;"	>
                    <input type="button" value="追踪" onclick="javascript:window.open('/job/urlRoad?jobId=${jobVo.id}&sessionId=${jobVo.sessionId}&requestUrl=${t_util.encode(it.id)}')"> <input type="button" value="测试" onclick="javascript:window.open('/job/testUrl?jobId=${jobVo.id}&url=${t_util.encode(it.id)}')">
                </td>
                <td>
                    <a href="${it.id}" target="_blank">${it.id}</a>
                </td>
            </tr>
        </#list>

        <#list pendsList! as it>
            <tr>
                <td style="width: 160px;">${t_util.formatDate(it.creationDate!, 'yyyy-MM-dd HH:mm:ss')}</td>
                <td style="width: 110px;"	>
                    <input type="button" value="追踪" onclick="javascript:window.open('/job/urlRoad?jobId=${jobVo.id}&sessionId=${jobVo.sessionId}&requestUrl=${t_util.encode(it.id)}')"> <input type="button" value="测试" onclick="javascript:window.open('/job/testUrl?jobId=${jobVo.id}&url=${t_util.encode(it.id)}')">
                </td>
                <td>
                    <a href="${it.id}" target="_blank">${it.id}</a>
                </td>
            </tr>
        </#list>
    <#else>
        <td colspan="3" style="text-align: center;">
            ${statusEnum!}暂无数据
        </td>
    </#if>
</table>