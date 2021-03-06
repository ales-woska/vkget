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
<script src="<c:url value="/resources/config/config.js" />"></script>
<script src="<c:url value="/resources/js/work.js" />"></script>

<title>Visual Knowledge Graph Management Tool Workbench</title>
</head>

<body ng-app="vkgmt" ng-controller="dataController">
	<div ng-include="'menu.html'"></div>

	<!-- MESSAGES -->
	<div class="messages" ng-show="messages.length > 0">
		<div ng-repeat="message in messages" class="alert alert-{{message.type}} fade in">
		    <button type="button" class="close" ng-click="cancelErrorMessage(message);"><span>&times;</span></button>
		    <strong>{{message.caption}}</strong> {{message.text}}
		</div>
	</div>
	
	<div id="loading">
		<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span> Loading...
	</div>
	
	<div id="workspace" style="display: none;">
		<a id="addTableButton" type="button" class="btn btn-default" ng-hide="changes.length == 0" ng-click="confirmChanges();">
			Manage changes
		</a>
		<a id="addLineButton" type="button" class="btn btn-default btn-danger" ng-hide="changes.length == 0" ng-click="discardChanges();">
			Discard changes
		</a>

		<div ng-repeat="table in dataModel.tables" class="disabledTable" ng-if="table.disabled"
			style="left: {{layouts[table.type.type].left}}px;
				top: {{layouts[table.type.type].top}}px;
				width: {{layouts[table.type.type].width}}px;
				height: {{layouts[table.type.type].height + 85}}px;">
			<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span> Loading...
		</div>	
		
		<table ng-repeat="table in dataModel.tables" class="vkgmt_table"
			style="background-color: {{layouts[table.type.type].background}};
				color: {{layouts[table.type.type].fontColor}};
				font-size: {{layouts[table.type.type].fontSize}}px;
				left: {{layouts[table.type.type].left}}px;
				top: {{layouts[table.type.type].top}}px;
				width: {{layouts[table.type.type].width}}px;
				border: {{layouts[table.type.type].lineThickness}}px {{layouts[table.type.type].lineType}} {{layouts[table.type.type].lineColor}};">
			
			<thead>
				<tr>
					<th class="caption" colspan="{{table.columns.length}}">
						{{table.label}} ({{(table.instances | filter : filterInstances).length}}/{{table.instances.length}})
						
						<button class="load-more" ng-hide="table.instances.length == 0" ng-click="loadMoreData(table);">load more</button>
					</th>
				</tr>
				<tr>
					<th class="columnCaption" ng-repeat="property in table.columns" 
						style="width: {{layouts[table.type.type].width / table.columns.length}}px;"
						ng-click="order(this, $index);">
						{{property.label}}
					</th>
				</tr>
				<tr>
					<th ng-repeat="property in table.columns" style="width: {{layouts[table.type.type].width / table.columns.length}}px;">
						<input type="text" class="filter" placeholder="search..."
							ng-model="filters[table.type.type][property.property]" ng-change="filterChanged(table, property.property);" />
					</th>
				</tr>
			</thead>
			
			<tbody style="height: {{layouts[table.type.type].height}}px;">
				<tr ng-repeat="instance in table.instances | filter : filterInstances" 
					ng-click="selectInstance(table, instance);" 
					ng-class="instance == table.selectedInstance ? 'selectedInstance' : ''">
					<td ng-repeat="property in instance.literalProperties" 
						ng-right-click="contextMenu(table, instance, property)"
						style="width: {{layouts[table.type.type].width / instance.literalProperties.length}}px; 
							{{isNumeric(property.value) ? 'text-align: right;' : ''}}">
						{{property.value}}
						
						<span class="error" ng-repeat="error in property.errors"
							ng-attr-title="{{error.description}}"
							ng-click="errorClicked(error, property); $event.stopPropagation();"
							ng-style="getLiteralErrorClass(error);">!</span>
							
						<div ng-if="$last">
							<div ng-repeat="op in instance.objectProperties">
								<div class="error" ng-repeat="error in op.errors" 
									ng-attr-title="{{error.description}}"
									ng-click="errorClicked(error, op); $event.stopPropagation();"
									ng-style="getObjectErrorClass(error);">!</div>
							</div>
						</div>
					</td>
				</tr>
			</tbody>
							
		</table>
		
		<svg>
			<g ng-repeat="lineLayout in screenLayout.lineLayouts">
				<polyline
					fill="none"
					stroke="{{lineLayout.lineColor}}"
					stroke-width="{{lineLayout.lineThickness}}"
					stroke-linecap="round"
					ng-attr-stroke-dasharray="{{lineLayout.lineType == 'DASHED' ? '5,5' : (lineLayout.lineType == 'DOTTED' ? '1,5' : '')}}"
					ng-attr-points="{{lineLayout.polyline}}" />
			</g>
		</svg>
		
		<div ng-repeat="layout in screenLayout.lineLayouts" class="vkgmt_line_title"
			style="color: {{layout.fontColor}}; font-size: {{layout.fontSize}}px; left: {{layout.left}}px; top: {{layout.top}}px;">
			{{layout.label.labelSource}}
		</div>

	</div>

	<div id="dataSourceModal" class="modal bs-example-modal-lg" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
			
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>&times;</span>
					</button>
					<h4 class="modal-title">Choose data source</h4>
				</div>
				
				<form class="form-horizontal" ng-submit="run();">
					<div class="modal-body">
						<div class="form-group">
							<label for="forType" class="col-sm-2 control-label">SPARQL Endpoint</label>
							<div class="col-sm-6">
								<input type="text" required ng-model="connectionInfo.endpoint" class="form-control" placeholder="http://" name="endpoint" />
								<select class="form-control" ng-options="endpoint as endpoint for endpoint in storedEndpoints" ng-model="selectedEndpoint"
									ng-change="connectionInfo.endpoint = selectedEndpoint">
									<option value="">Choose from stored endpoints</option>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="forType" class="col-sm-2 control-label">Endpoint type</label>
							<div class="col-sm-6">
								<select class="form-control" required ng-model="connectionInfo.type">
									<option value="virtuoso">Virtuoso</option>
									<option value="jena">Jena</option>
									<option value="other">Other</option>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="forType" class="col-sm-2 control-label">Use named graph</label>
							<div class="col-sm-1">
								<input type="checkbox" ng-model="connectionInfo.useNamedGraph" />
							</div>
							<div class="col-sm-4">
								<input type="text" class="form-control" ng-show="connectionInfo.useNamedGraph == true" ng-required="connectionInfo.useNamedGraph == true" ng-model="connectionInfo.namedGraph" placeholder="Named graph" />
							</div>
						</div>
						<div class="form-group">
							<label for="forType" class="col-sm-2 control-label">Use authorization</label>
							<div class="col-sm-1">
								<input type="checkbox" ng-model="connectionInfo.useAuthorization" />
							</div>
							<div class="col-sm-4">
								<input type="text" class="form-control" ng-show="connectionInfo.useAuthorization == true" ng-required="connectionInfo.useAuthorization == true" ng-model="connectionInfo.username" placeholder="Username" />
								<input type="text" class="form-control" ng-show="connectionInfo.useAuthorization == true" ng-required="connectionInfo.useAuthorization == true" ng-model="connectionInfo.password" placeholder="Password" />
							</div>
						</div>
						<div class="form-group">
							<label for="forType" class="col-sm-2 control-label">Layout</label>
							<div class="col-sm-6">
								<span id="layoutLoading"><span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span> Loading...</span>
								<select id="layoutSelect" class="form-control" required style="display: none;"
									required ng-model="layout" ng-options="layout.uri.uri | urlencode as layout.name for layout in layouts">
									<option value="">Select layout</option>
								</select>
							</div>
						</div>
					</div>
					
					<div class="modal-footer">
						<input type="submit" class="btn btn-default" value="Run" />
					</div>
					
				</form>
			</div>
		</div>
	</div>

	<div id="changesModal" class="modal bs-example-modal-lg" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
			
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>&times;</span>
					</button>
					<h4 class="modal-title">Changes update script</h4>
				</div>
				
				<form class="form-horizontal">
					<div class="modal-body">
						<span id="updateScriptLoading"><span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span> Loading...</span>
						<textarea id="updateScriptTextarea" style="display: none;" ng-model="updateScript"></textarea>
					</div>
					<div class="modal-footer">
						<a type="button" class="btn btn-default" ng-click="download();" />Save...</a>
						<input type="submit" class="btn btn-default btn-danger" value="Save changes" ng-click="saveChanges();" />
						<input type="submit" class="btn btn-default" value="Close" ng-click="closeChangeModal();" />
					</div>
					
				</form>
			</div>
		</div>
	</div>

	<div id="editCellModal" class="modal bs-example-modal-lg" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
			
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>&times;</span>
					</button>
					<h4 class="modal-title">Edit cell value</h4>
				</div>
				
				<form class="form-horizontal">
					<div class="modal-body">
						<div class="form-group">
							<label for="forType" class="col-sm-4 control-label">Old value</label>
							<div class="col-sm-6">
								<input type="text" class="form-control" ng-model="oldCellValue" disabled="disabled" />
							</div>
						</div>
						<div class="form-group">
							<label for="forType" class="col-sm-4 control-label">New value</label>
							<div class="col-sm-6">
								<input type="text" class="form-control" ng-model="editCellValue" />
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<input type="button" class="btn btn-default" value="Cancel" ng-click="closeEditCellModal();" />
						<input type="submit" class="btn btn-default" value="Save" ng-click="editCell();" />
					</div>
				</form>
			</div>
		</div>
	</div>

	<div id="addRowModal" class="modal bs-example-modal-lg" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>&times;</span>
					</button>
					<h4 class="modal-title">Add a new row</h4>
				</div>
				<form class="form-horizontal" ng-submit="addRow();">
					<div class="modal-body">
						<div class="form-group">
							<label for="uri" class="col-sm-4 control-label">URI</label>
							<div class="col-sm-6">
								<input type="text" class="form-control" placeholder="URI" name="uri" required ng-model="addRowObject['uri']" />
							</div>
						</div>
						<div class="form-group" ng-repeat="literalProperty in selectedTd.instance.literalProperties | filter : filterProperties">
							<label class="col-sm-4 control-label">{{literalProperty.property.property}}</label>
							<div class="col-sm-6">
								<input type="text" class="form-control" ng-model="addRowObject[literalProperty.property.property]" />
							</div>
						</div>
						<div class="modal-footer">
							<input type="button" class="btn btn-default" value="Cancel" ng-click="closeAddRowModal();" />
							<input type="submit" class="btn btn-default" value="Save" />
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>

	<div id="removeLinkedPropertyModal" class="modal bs-example-modal-lg" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>&times;</span>
					</button>
					<h4 class="modal-title">Remove linked property</h4>
				</div>
				<form class="form-horizontal" ng-submit="removeLinkedProperty();">
					<div class="modal-body">
						<div class="form-group" ng-if="selectedTd.instance.objectProperties.length > 0">
							<div class="col-sm-10">
							<table>
								<thead>
									<tr><th>&nbsp;</th><th>Property</th><th>Value</th><th>URI</th></tr>
								</thead>
								<tbody>
									<tr ng-repeat="objectProperty in selectedTd.instance.objectProperties">
										<td><input type="checkbox" ng-model="removeLinkedPropertyObject[objectProperty.property.property][objectProperty.objectUri.uri]" /></td>
									    <td style="padding: 10px;">{{getObjectPropertyTitle(objectProperty.property.property);}}</td>
									    <td style="padding: 10px;">{{getObjectPropertyValue(objectProperty.objectUri.uri);}}</td>
									    <td style="padding: 10px;">{{objectProperty.objectUri.uri}}</td>
									</tr>
								</tbody>
							</table>
							</div>
						</div>
						<div class="form-group" ng-if="selectedTd.instance.objectProperties.length == 0">
							<div class="col-sm-6">No linked properties.</div>
						</div>
						<div class="modal-footer">
							<input type="button" class="btn btn-default" value="Cancel" ng-click="closeRemoveLinkedPropertyModal();" />
							<input ng-if="selectedTd.instance.objectProperties" type="submit" class="btn btn-default" value="Remove selected" />
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>

	<div id="addLinkedPropertyModal" class="modal bs-example-modal-lg" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>&times;</span>
					</button>
					<h4 class="modal-title">Add linked property</h4>
				</div>
				<form class="form-horizontal" ng-submit="addLinkedProperty();">
					<div class="modal-body">
						<div class="form-group">
							<label class="col-sm-4 control-label">Property</label>
							<div class="col-sm-6">
								<select required class="form-control" ng-model="addLinkedPropertyObject['property']"
									ng-options="lineLayout.property.property as getObjectPropertyTitle(lineLayout.property.property) for lineLayout in screenLayout.lineLayouts | filter: filterLineLayouts">
								</select>
							</div>
						</div>
						<div class="form-group" ng-if="addLinkedPropertyObject['property']">
							<label class="col-sm-4 control-label">Value</label>
							<div class="col-sm-6">
								<select required class="form-control" ng-model="addLinkedPropertyObject['value']" 
									ng-options="instance.uri.uri as getObjectPropertyValue(instance.uri.uri) for instance in getLinkedPropertyInstances(selectedTd.table.type.type, addLinkedPropertyObject['property'])">
								</select>
							</div>
						</div>
						<div class="modal-footer">
							<input type="button" class="btn btn-default" value="Cancel" ng-click="closeAddLinkedPropertyModal();" />
							<input ng-if="addLinkedPropertyObject['property'] && addLinkedPropertyObject['value']" type="submit" class="btn btn-default" value="Save" />
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>

	<div id="contextMenu" class="dropdown clearfix">
		<ul class="dropdown-menu" role="menu">
			<li><a tabindex="-1" href="#" ng-click="openEditCellModal();">Edit cell</a></li>
			<li><a tabindex="-1" href="#" ng-click="removeCell();">Clear cell</a></li>
			<li class="divider"></li>
			<li><a tabindex="-1" href="#" ng-click="openAddRowModal();">Add row</a></li>
			<li><a tabindex="-1" href="#" ng-click="removeRow();">Remove row</a></li>
			<li class="divider"></li>
			<li><a tabindex="-1" href="#" ng-click="openAddLinkedPropertyModal();">Add linked property</a></li>
			<li><a tabindex="-1" href="#" ng-click="openRemoveLinkedPropertyModal();">Remove linked property</a></li>
			<li class="divider"></li>
			<li><a id="closeContextMenu" tabindex="-1" href="#" ng-click="hideContextMenu();">Close</a></li>
		</ul>
	</div>
	
	<script type="text/javascript">
	    $(window).load(function(){
	        $('#dataSourceModal').modal('show');
	    });
	</script>
	
</body>
</html>
