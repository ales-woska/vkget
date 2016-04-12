<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
	<jsp:include page="fragments/head.jsp" />
	<title>Visual Knowledge Graph Editing Tool - Layouts</title>
	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/joint.min.css" />
	<script src="${pageContext.request.contextPath}/js/lodash.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/backbone-min.js"></script>
	<script src="${pageContext.request.contextPath}/js/joint.min.js"></script>
</head>

<body>
	<jsp:include page="fragments/menu.jsp" />

	<div class="container theme-showcase">
	
	
		<c:if test="${empty uri}">
			<div class="page-header">
				<h1>Create new layout</h1>
			</div>
		</c:if>
		<c:if test="${not empty uri}">
			<div class="page-header">
				<h1>Edit layout <c:out value="${screenLayout.name}" /> </h1>
			</div>
		</c:if>
		
		<div>
			<a href="layout" type="button" class="btn btn-default btn-success" aria-label="Left Align">
				Save
			</a>
			<a href="layout" type="button" class="btn btn-default btn-warning" aria-label="Left Align">
				Cancel
			</a>
		</div>
		
		<button id="showBlockForm" type="button" class="btn btn-primary" data-toggle="modal" data-target="#blockModal" style="display: none;">Block</button>
		<button id="showLineForm" type="button" class="btn btn-primary" data-toggle="modal" data-target="#lineModal" style="display: none;">Line</button>

	</div>
	

	<div>
		<div id="myholder" style="border: 1px solid gray; margin: 10px; width: 1419px; position: relative;">
			<script type="text/javascript">
				var graph = new joint.dia.Graph;
	
				var paper = new joint.dia.Paper({
					el : $('#myholder'),
					model : graph,
					width: 1417,
					gridSize : 5
				});

				paper.on('cell:pointerdblclick', function (element) {document.getElementById('showBlockForm').click();});
	
				function addTable() {
					var rect = new joint.shapes.basic.Rect({
						position : {
							x : 0,
							y : 0
						},
						size : {
							width : 100,
							height : 100
						},
						attrs : {
							rect : {
								fill : 'silver'
							},
							text : {
								text : 'new_table',
								fill : 'black'
							}
						}
					});
					graph.addCell(rect);
				}
				
				<c:forEach var="bl" items="${screenLayout.blockLayouts}">
					var rect = new joint.shapes.basic.Rect({
						id : '<c:out value="${bl.forType}" />',
						position : {
							x : <c:out value="${bl.left}"/>,
							y : <c:out value="${bl.top}"/>
						},
						size : {
							width : <c:out value="${bl.width}"/>,
							height : <c:out value="${bl.height}"/>
						},
						attrs : {
							rect : {
								fill : '<c:out value="${bl.background}"/>'
							},
							text : {
								text : '<c:out value="${bl.forType}" />',
								fill : 'black'
							}
						}
					});
					graph.addCell(rect);
				</c:forEach>
	
				<c:forEach var="ll" items="${screenLayout.lineLayouts}">
					var link = new joint.dia.Link({
						source : {
							id : '<c:out value="${ll.fromClass}" />'
						},
						target : {
							id : '<c:out value="${ll.toClass}" />'
						}
					});
					link.on('change:vertices', function (element) {document.getElementById('showLineForm').click();});
					graph.addCell(link);
				</c:forEach>
				
			</script>
			<a href="#" type="button" class="btn btn-default btn-success" aria-label="Left Align" style="position: absolute; top: 10px; right: 10px;"
				onclick="addTable();">
				Add table
			</a>
		</div>
	</div>


	<div id="blockModal" class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<jsp:include page="block_form.jsp" />
			</div>
		</div>
	</div>


	<div id="lineModal" class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<jsp:include page="line_form.jsp" />
			</div>
		</div>
	</div>

	<jsp:include page="fragments/endscripts.jsp" />
</body>
</html>
