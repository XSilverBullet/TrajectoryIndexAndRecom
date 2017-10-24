<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="decorator" uri="sitemesh-decorator" %>
<%@ taglib prefix="page" uri="sitemesh-page"%>

<!DOCTYPE html>
<html lang="zh-CN">
	<head>
		<title><decorator:title default="welcome"/></title>
		
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta http-equiv="Content-Type" content="text/html"/>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		
		<link rel="stylesheet" href="http://libs.baidu.com/bootstrap/3.0.3/css/bootstrap.min.css">
		<script src="http://libs.baidu.com/jquery/1.10.2/jquery.min.js"></script>
		<script src="http://libs.baidu.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
		<!--  <script src="scripts/bootstrap.autocomplete.js"></script>-->
		<script type="text/javascript" src="scripts/common.js"></script>
	</head>
	<body>
		<decorator:body />
    	<div id='messageBox' class="fade modal" aria-hidden="true">
	    	<div class="modal-dialog">
		    	<div class="modal-content">
		    		<div id="contentForMessageBox" class="modal-body"></div>
		    		<div id="buttonsForMessageBox" class="modal-footer">
		    			<button class="btn btn-primary" id="ensureButtonForMessageBox">Ensure</button>
		    			<button class="btn btn-primary" id="cancelButtonForMessageBox">Cancel</button>
		    		</div>
	    		</div>
    		</div>
    	</div>
	</body>
</html>