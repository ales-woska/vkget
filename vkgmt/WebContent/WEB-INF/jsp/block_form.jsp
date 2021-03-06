<div id="blockForm{{currBlock.forType.type | colons}}" class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog">

	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="exampleModalLabel">Block layout</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal">
					<div class="form-group">
						<label for="forType" class="col-sm-2 control-label">For rdf:type</label>
						<div class="col-sm-6">
							<input type="text" class="form-control" placeholder="Class" ng-model="currBlock.forType.type"
								ng-focus="currBlock.forType.oldValue = currBlock.forType.type"
								ng-change="updateForType({forType: currBlock.forType})">
						</div>
					</div>
					<div class="form-group">
						<label for="labelTypes" class="col-sm-2 control-label">Table header</label>
						<div class="col-sm-4">
						    <select class="form-control" ng-model="currBlock.label.type">
								<option value="URI">URI</option>
								<option value="CONSTANT">Constant</option>
							</select>
						</div>
					</div>
					<div class="form-group" ng-show="currBlock.label.type == 'CONSTANT'">
						<label class="col-sm-2 control-label">Header source</label>
						<div class="col-sm-4">
							<input type="text" class="form-control" placeholder="Label" ng-model="currBlock.label.labelSource">
						</div>
					</div>
					<div class="form-group">
						<label for="labelTypes" class="col-sm-2 control-label">Columns</label>
						<div class="col-sm-10">
							<table>
								<thead>
									<tr><th>Property</th><th>Caption type</th><th>Caption</th><th>Language</th><th>Aggr function</th><th></th></tr>
								</thead>
								<tbody>
									<tr ng-repeat="columnLayout in currBlock.properties">
										<td>
											<input type="text" class="form-control" placeholder="Property" 
												ng-disabled="columnLayout.property.property == 'URI' || columnLayout.property.property == 'rdfs:label' || columnLayout.property.property == 'skos:prefLabel' || columnLayout.property.property == 'dc:title'" 
												ng-model="columnLayout.property.property" />
										</td>
										<td>
											<select class="form-control" ng-model="columnLayout.label.type">
												<option value="URI">URI</option>
												<option value="CONSTANT">Constant</option>
											</select>
										</td>
										<td>
											<input type="text" class="form-control" placeholder="Label" ng-model="columnLayout.label.labelSource" ng-show="columnLayout.label.type != 'URI' && columnLayout.label.type != 'LABEL'" />
										</td>
										<td>
											<select class="form-control" ng-options="l.value as l.label for l in langs" 
												ng-model="columnLayout.label.lang"
												ng-show="columnLayout.property.property != 'URI' && columnLayout.property.property">
												<option value="">Choose language</option>
											</select>
										</td>
										<td>
											<select class="form-control" ng-model="columnLayout.aggregateFunction">
												<option value="NOTHING">None</option>
												<option value="MIN">Minimum</option>
												<option value="MAX">Maximum</option>
												<option value="AVG">Average</option>
												<option value="MEDIAN">Mean</option>
											</select>
										</td>
										<td><a type="button" ng-click="removeProperty({blockLayout: currBlock, property: columnLayout})"><span class="glyphicon glyphicon-remove"></span></a></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					
					<div class="form-group">
						<label for="labelTypes" class="col-sm-2 control-label">Add a new column</label>
						<div class="col-sm-10">
							<table>
								<tbody>
									<tr>
										<td>Property</td>
										<td>
											<select class="form-control" ng-model="currBlock.toAddProperty.columnType">
												<option value="">Choose new column type</option>
												<option value="URI" selected>URI</option>
												<option value="LABEL">Label</option>
												<option value="CUSTOM">Custom</option>
											</select>
										</td>
										<td>
											<span ng-show="currBlock.toAddProperty.columnType == 'CUSTOM'">
												<input type="text" class="form-control" placeholder="Property" ng-model="currBlock.toAddProperty.property.property" />
											</span>
											<span ng-show="currBlock.toAddProperty.columnType == 'LABEL'">
												<select class="form-control" ng-model="currBlock.toAddProperty.labelType" ng-init="currBlock.toAddProperty.labelType = 'rdfs:label'">
													<option value="rdfs:label">rdfs:label</option>
													<option value="dc:title">dc:title</option>
													<option value="skos:prefLabel">skos:prefLabel</option>
												</select>
											</span>
										</td>
										<td>
											<select class="form-control" ng-options="l.value as l.label for l in langs" 
												ng-model="currBlock.toAddProperty.label.lang" 
												ng-show="currBlock.toAddProperty.columnType != 'URI' && currBlock.toAddProperty.columnType">
												<option value="">Choose language</option>
											</select>
										</td>
									</tr>
									<tr>
										<td>Header</td>
										<td>
											<select class="form-control" ng-model="currBlock.toAddProperty.label.type" ng-init="currBlock.toAddProperty.label.type = 'URI'">
												<option value="URI">URI</option>
												<option value="CONSTANT">Constant</option>
											</select>
										</td>
										<td><input type="text" class="form-control" placeholder="Label" ng-model="currBlock.toAddProperty.label.labelSource" ng-show="currBlock.toAddProperty.label.type == 'CONSTANT'" /></td>
									</tr>
									<tr>
										<td>Aggregate function</td>
										<td>
											<select class="form-control" ng-model="currBlock.toAddProperty.aggregateFunction" ng-init="currBlock.toAddProperty.aggregateFunction = 'NOTHING'">
												<option value="NOTHING">None</option>
												<option value="MIN">Minimum</option>
												<option value="MAX">Maximum</option>
												<option value="AVG">Average</option>
												<option value="MEDIAN">Mean</option>
											</select>
										</td>
									</tr>
									<tr>
										<td></td>
										<td>
											<button type="button" class="btn btn-default" ng-click="addProperty({blockLayout: currBlock})">Add</button>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					
					<div class="form-group">
						<label for="lineType" class="col-sm-2 control-label">Line type</label>
						<div class="col-sm-4">
							<select class="form-control" ng-model="currBlock.lineType">
								<option value="SOLID">Solid</option>
								<option value="DOTTED">Dotted</option>
								<option value="DASHED">Dashed</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="fontColor" class="col-sm-2 control-label">Font color</label>
						<div class="col-sm-10">
							<input type="color" value="#000000" ng-model="currBlock.fontColor" />
						</div>
					</div>
					<div class="form-group">
						<label for="background" class="col-sm-2 control-label">Background</label>
						<div class="col-sm-10">
							<input type="color" value="#ffffff" ng-model="currBlock.background" />
						</div>
					</div>
					<div class="form-group">
						<label for="lineColor" class="col-sm-2 control-label">Line color</label>
						<div class="col-sm-10">
							<input type="color" value="#000000" ng-model="currBlock.lineColor" />
						</div>
					</div>
					<div class="form-group">
						<label for="fontSize" class="col-sm-2 control-label">Font size</label>
						<div class="col-sm-2">
							<input type="number" class="form-control" placeholder="10" ng-model="currBlock.fontSize" />
						</div>
					</div>
					<div class="form-group">
						<label for="lineThickness" class="col-sm-2 control-label">Line Thickness</label>
						<div class="col-sm-2">
							<input type="number" class="form-control" placeholder="1" ng-model="currBlock.lineThickness" />
						</div>
					</div>
					<div class="form-group">
						<label for="height" class="col-sm-2 control-label">Height</label>
						<div class="col-sm-2">
							<input type="number" class="form-control" placeholder="100" ng-model="currBlock.height" />
						</div>
					</div>
					<div class="form-group">
						<label for="width" class="col-sm-2 control-label">Width</label>
						<div class="col-sm-2">
							<input type="number" class="form-control" placeholder="100" ng-model="currBlock.width" />
						</div>
					</div>
					<div class="form-group">
						<label for="left" class="col-sm-2 control-label">Left</label>
						<div class="col-sm-2">
							<input type="number" class="form-control" placeholder="0" ng-model="currBlock.left" />
						</div>
					</div>
					<div class="form-group">
						<label for="top" class="col-sm-2 control-label">Top</label>
						<div class="col-sm-2">
							<input type="number" class="form-control" placeholder="0" ng-model="currBlock.top" />
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
	
	
</div>