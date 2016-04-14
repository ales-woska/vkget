<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
	<jsp:include page="fragments/head.jsp" />
	<title>Visual Knowledge Graph Editing Tool</title>
</head>

<body>
	<jsp:include page="fragments/menu.jsp" />

	<div style="position: relative;">
		<canvas id="panelCanvas" height="700px" width="1500px"></canvas>
		<c:out value="${tables}" escapeXml="false" />
	</div>

	<jsp:include page="fragments/endscripts.jsp" />
</body>
</html>
