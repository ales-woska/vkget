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
<script src="<c:url value="/resources/js/bootstrap.js" />"></script>

<title>Visual Knowledge Graph Management Tool Home</title>
</head>

<body>
	<div ng-app>
		<div ng-include="'menu.html'"></div>
		
		<div id="jumbotron" class="jumbotron">
			<div class="container">
				<h1>Visual Knowledge Graph Editing Tool</h1>
				<p>Edit your RDF data in a new web application.</p>
				<p>
					<a class="btn btn-primary btn-lg" href="#" role="button" onclick="document.getElementById('jumbotron').style.display = 'none';">Cool &raquo;</a>
				</p>
			</div>
		</div>
	
		<div class="container">
			<div class="row">
				<div class="col-md-4">
					<h2>Layout</h2>
					<p>To create or modify layouts for your work banch you can use
						easy settings editing tool.</p>
					<p>
						<a class="btn btn-default" href="layout.html" role="button">Layouts &raquo;</a>
					</p>
				</div>
				<div class="col-md-4">
					<h2>Work</h2>
					<p>Load your graph and edit its values in workbach.</p>
					<p>
						<a class="btn btn-default" href="work.html" role="button">Workbanch &raquo;</a>
					</p>
				</div>
			</div>
	
			<hr>
	
			<footer>
				<p>&copy; 2016 KSI MFF UK</p>
			</footer>
		</div>
		
	</div>

</body>
</html>
