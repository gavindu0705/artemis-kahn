<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>功能示例</title>
    <link rel="stylesheet" type="text/css" href="/static/css/main.css" />
    <link rel="stylesheet" type="text/css" href="/static/css/codemirror/codemirror.css">
    <script src="/static/css/codemirror/codemirror.js"></script>
    <script src="/static/css/codemirror/addon/edit/matchbrackets.js"></script>
    <script src="/static/css/codemirror/mode/groovy.js"></script>

</head>
<body>


<textarea id="example">
/**
 * 字符串处理
 */
public class GShell {
    /**
     * 字符串替换
     *
     * @param str
     * @param searchString
     * @param replacement
     * @return
     */
    public final String replace(String str, String searchString, String replacement) {
        if (str != null) {
            return str.replace(searchString, replacement);
        }
        return str;
    }

    /**
     * 字符串截取
     *
     * @param str
     * @param separator
     * @return
     */
    public final String substringAfter(String str, String separator) {
        return org.apache.commons.lang.StringUtils.substringAfter(str, separator);
    }

    /**
     * 字符串截取
     *
     * @param str
     * @param separator
     * @return
     */
    public final String substringBefore(String str, String separator) {
        return org.apache.commons.lang.StringUtils.substringBefore(str, separator);
    }

    /**
     * 字符串截取
     *
     * @param str
     * @param open
     * @param close
     * @return
     */
    public final String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        if (str.indexOf(open) == -1 || str.indexOf(close) == -1) {
            return str;
        }
        return org.apache.commons.lang.StringUtils.substringBetween(str, open, close);
    }

    /**
     * 拆分并取值
     *
     * @param str
     * @param separatorChar
     * @param index
     * @return
     */
    public final String splitGet(String str, String separatorChar, int index) {
        if (str == null) {
            return null;
        }

        if (separatorChar == null) {
            return str;
        }

        String[] arrs = str.split(separatorChar);
        if (arrs != null && arrs.length > index) {
            return arrs[index];
        }

        return org.apache.commons.lang.StringUtils.EMPTY;
    }

}
</textarea>

<script type="text/javascript">
    var editor = CodeMirror.fromTextArea(document.getElementById("example"), {
        lineNumbers: true,
        matchBrackets: true,
        mode: "text/x-groovy",
        readOnly : true,
        disabled : true
    });
    editor.setSize("100%","100%");
</script>

</body>
</html>