<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="Visual Knowledge Graph Editing Tool">
<meta name="author" content="ales.woska@gmail.com">

<link href="<c:url value="/resources/img/favicon.png" />" rel="icon" type="image/png" />

<link href="<c:url value="/resources/css/bootstrap.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/bootstrap-theme.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/fix.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/vkgmt.css" />" rel="stylesheet">

<script src="<c:url value="/resources/js/jquery-1.12.3.js" />"></script>
<script src="<c:url value="/resources/js/angular.js" />"></script>
<script src="<c:url value="/resources/js/angular-route.js" />"></script>
<script src="<c:url value="/resources/js/bootstrap.js" />"></script>
<script src="<c:url value="/resources/config/config.js" />"></script>
<script src="<c:url value="/resources/js/layout.js" />"></script>

<title>Visual Knowledge Graph Editing Tool Home</title>
</head>

<body>
	<div ng-app="vkgmt" ng-controller="layoutController">
		<div ng-include="'menu.html'"></div>

		<!-- MESSAGES -->
		<div class="messages" ng-show="messages.length > 0">
			<div ng-repeat="message in messages" class="alert alert-{{message.type}} fade in">
			    <button type="button" class="close" ng-click="cancelErrorMessage(message);"><span>&times;</span></button>
			    <strong>{{message.caption}}</strong> {{message.text}}
			</div>
		</div>

		<div class="container">
		
			<div class="page-header">
				<h1>List of available layouts</h1>
			</div>
			
			
			<div id="layoutListLoading">
				<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span> Loading...
			</div>
			
			<div id="layoutList" style="display: none;" class="row">
				<div class="list-group">
					<div ng-repeat="screenLayout in layouts" class="list-group-item">
						<a target="_self" href="edit_layout.html?uri={{screenLayout.uri.uri | urlencode }}">{{screenLayout.name}}</a>
						<a class="removeLink" ng-click="removeLayout(screenLayout)"><span class="glyphicon glyphicon-remove"></span></a>
					</div>
				</div>
				
				<a href="edit_layout.html" target="_self" type="button" class="btn btn-default btn-success">
					<span class="glyphicon glyphicon-plus"></span> Add a new layout
				</a>
			</div>
	
		</div>
	</div>
	
</body>
</html>
