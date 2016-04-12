<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
	<jsp:include page="fragments/head.jsp" />
	<title>Visual Knowledge Graph Editing Tool - Layouts</title>
</head>

<body>
	<jsp:include page="fragments/menu.jsp" />

	<div class="container theme-showcase">
	
		<div class="page-header">
			<h1>List of available layouts</h1>
		</div>
		
		<div class="row">
			<div class="list-group">
				<c:forEach var="layout" items="${layouts}">
					<a href="layout?uri=<c:out value="${layout.uri}"/>" class="list-group-item"><c:out value="${layout.name}"/></a>
				</c:forEach>
			</div>
			
			<a href="layout/new" type="button" class="btn btn-default btn-success" aria-label="Left Align">
				<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add new layout
			</a>
		</div>


	</div>

	<jsp:include page="fragments/endscripts.jsp" />
</body>
</html>
