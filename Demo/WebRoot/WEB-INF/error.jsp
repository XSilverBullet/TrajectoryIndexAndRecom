<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>ERROR</title>
		<link rel="stylesheet" type="text/css" href="/css/common.css"/>
	</head>
	<body>
		对不起，出错啦！请重试(╯﹏╰) <br/>
		<s:property value="#request.message" />
	</body>
</html>

