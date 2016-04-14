<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
	<jsp:include page="WEB-INF/jsp/fragments/head.jsp" />
	<title>Visual Knowledge Graph Editing Tool Home</title>
</head>

<body>
	<jsp:include page="WEB-INF/jsp/fragments/menu.jsp" />
	
	<div ng-app="">
 	<p>Name : <input type="text" ng-model="name"></p>
 	<h1>Hello {{name}}</h1>
</div>

	<div id="jumbotron" class="jumbotron">
      <div class="container">
        <h1>Visual Knowledge Graph Editing Tool</h1>
        <p>Edit your RDF data in a beautiful graphic web application.</p>
        <p><a class="btn btn-primary btn-lg" href="#" role="button" onclick="document.getElementById('jumbotron').style.display = 'none';">Cool &raquo;</a></p>
      </div>
    </div>

    <div class="container">
      <div class="row">
        <div class="col-md-4">
          <h2>Layout</h2>
          <p>To create or modify layouts for your work banch you can use easy settings editing tool.</p>
          <p><a class="btn btn-default" href="http://localhost:8080/visual-knowledge-graph-editing-tool/layout" role="button">Layouts &raquo;</a></p>
        </div>
        <div class="col-md-4">
          <h2>Work</h2>
          <p>Load your graph and edit its values in workbach.</p>
          <p><a class="btn btn-default" href="http://localhost:8080/visual-knowledge-graph-editing-tool/work" role="button">Workbanch &raquo;</a></p>
       </div>
        <div class="col-md-4">
          <h2>Need more help?</h2>
          <p>For more information you can visit our help pages.</p>
          <p><a class="btn btn-default" href="http://localhost:8080/visual-knowledge-graph-editing-tool/help" role="button">View details &raquo;</a></p>
        </div>
      </div>

      <hr>

      <footer>
        <p>&copy; 2016 KSI MFF UK</p>
      </footer>
    </div>

	<jsp:include page="WEB-INF/jsp/fragments/endscripts.jsp" />
</body>
</html>
