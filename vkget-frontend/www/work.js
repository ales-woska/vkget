var app = angular.module('vkget', []);

app.config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    $httpProvider.defaults.timeout = 5000;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
}]);

app.config(function($locationProvider) {
	$locationProvider.html5Mode({
        enabled: true,
        requireBase: false
	});
});

app.filter('urlencode', function() {
	return function(input) {
		return window.encodeURIComponent(input);
	};
});

app.directive('ngRightClick', function($parse) {
    return function(scope, element, attrs) {
        var fn = $parse(attrs.ngRightClick);
        element.bind('contextmenu', function(event) {
            scope.$apply(function() {
                event.preventDefault();
                fn(scope, {$event:event});
            });
        });
    };
});

app.controller('dataController', function($scope, $http, $filter, $window, $location) {
	$scope.endpoint = 'http://dbpedia.org/sparql';
	$scope.endpointType = 'other';
	$scope.changes = [];
	$scope.updateScript = '';
	$scope.selectedTd = {};
	$scope.addRowObject = {};
	$scope.removeLinkedPropertyObject = {};
	$scope.addLinkedPropertyObject = {};
	$scope.filters = {};
	$scope.sort = {
        column: 0,
        reverse: true
    };
	$scope.messages = [];
	
	if ($location.search()['endpoint']) {
		$scope.endpoint = $location.search()['endpoint'];
	}
	
	if ($location.search()['type']) {
		$scope.endpointType = $location.search()['type'];
	}
	
	$scope.editCell = function() {
		var instance = $scope.selectedTd.instance;
		var rdfProperty = $scope.selectedTd.property;
		var oldValue = $scope.oldCellValue;
		var newValue = $scope.editCellValue;
		$scope.commonAction(instance.uri.uri, rdfProperty.property, oldValue, newValue);
		rdfProperty.value = newValue;
		$('#editCellModal').modal('hide');
	};
	
	$scope.openAddRowModal = function() {
		$('#addRowModal').modal('show');
	};
	
	$scope.openRemoveLinkedPropertyModal = function() {
		$('#removeLinkedPropertyModal').modal('show');
	};
	
	$scope.openAddLinkedPropertyModal = function() {
		$('#addLinkedPropertyModal').modal('show');
	};
	
	$scope.openEditCellModal = function() {
		var instance = $scope.selectedTd.instance;
		var rdfProperty = $scope.selectedTd.property;
		for (var i = 0; i < instance.literalProperties.length; i++) {
			var currProperty = instance.literalProperties[i].property;
			if (currProperty == rdfProperty.property) {
				$scope.oldCellValue = rdfProperty.value;
				$scope.editCellValue = rdfProperty.value;
			}
		}
		$('#editCellModal').modal('show');
	};
	
	$scope.closeEditCellModal = function() {
		$('#editCellModal').modal('hide');
		$scope.hideContextMenu();
	};
	
	$scope.closeAddRowModal = function() {
		$('#addRowModal').modal('hide');
		$scope.hideContextMenu();
	};
	
	$scope.closeRemoveLinkedPropertyModal = function() {
		$('#removeLinkedPropertyModal').modal('hide');
		$scope.hideContextMenu();
	};
	
	$scope.closeAddLinkedPropertyModal = function() {
		$('#addLinkedPropertyModal').modal('hide');
		$scope.hideContextMenu();
	};
	
	$scope.removeCell = function() {
		var instance = $scope.selectedTd.instance;
		var property = $scope.selectedTd.property;
		var oldValue = null;
		for (var i = 0; i < instance.literalProperties.length; i++) {
			var currProperty = instance.literalProperties[i].property;
			if (currProperty == property.property) {
				oldValue = property.value;
				property.value = null;
			}
		}
		$scope.commonAction(instance.uri.uri, property.property, oldValue, '');
	};
	
	$scope.addRow = function() {
		var table = $scope.selectedTd.table;
		var addRowObject = $scope.addRowObject;
		var addRowUri = addRowObject['uri'];
		var newInstance = {
				uri: addRowUri,
				type: table.type,
				literalProperties: [],
				objectProperties: []
			};
		for (var key in addRowObject) {
			if (key == 'uri') {
				continue;
			}
			var change = {
				uri: addRowUri,
				property: key,
				oldValue: null,
				newValue: addRowObject[key]
			};
			$scope.changes.push(change);
			newInstance.literalProperties.push({
				property: key,
				value: addRowObject[key],
			});
		}
		table.instances.push(newInstance);
		$scope.addRowObject = {};
		$scope.hideContextMenu();
		$('#addRowModal').modal('hide');
	};
	
	$scope.removeRow = function() {
		var table = $scope.selectedTd.table;
		var instance = $scope.selectedTd.instance;
		for (var i = 0; i < instance.literalProperties.length; i++) {
			var change = {
				uri: instance.uri,
				property: instance.literalProperties[i].property,
				oldValue: instance.literalProperties[i].value,
				newValue: null
			};
			$scope.changes.push(change);
		}
		var index = table.instances.indexOf(instance);
		table.instances.splice(index, 1);
		$scope.hideContextMenu();
	};
	
	$scope.addLinkedProperty = function() {
		var addLinkedPropertyObject = $scope.addLinkedPropertyObject;
		var instance = $scope.selectedTd.instance;
		var property = {
			property: addLinkedPropertyObject['property']
		};
		var value = addLinkedPropertyObject['value'];
		var change = {
			uri: instance.uri,
			property: property,
			oldValue: null,
			newValue: value
		};
		instance.objectProperties.push({
			property: property,
			subjectUri: instance.uri,
			objectUri: {
				uri: value
			}
		});
		$scope.changes.push(change);
		$scope.addLinkedPropertyObject = {};
		$('#addLinkedPropertyModal').modal('hide');
		$scope.hideContextMenu();
	};
	
	$scope.removeLinkedProperty = function() {
		var removeLinkedPropertyObject = $scope.removeLinkedPropertyObject;
		var instance = $scope.selectedTd.instance;
		
		for (var property in removeLinkedPropertyObject) {
			for (value in removeLinkedPropertyObject[property]) {
				var change = {
					uri: instance.uri,
					property: property,
					oldValue: value,
					newValue: null
				};
				$scope.changes.push(change);
				
				$scope.removeLinkedPropertyObject = {};
				for (var i = 0; i < instance.objectProperties.length; i++) {
					var objectProperty = instance.objectProperties[i];
					if (objectProperty.property.property == property) {
						instance.objectProperties.splice(i, 1);
					}
				}
			}
		}
		$('#removeLinkedPropertyModal').modal('hide');
		$scope.hideContextMenu();
	};
	
	$scope.commonAction = function(uri, property, oldValue, newValue) {
		var change = {
			uri: uri,
			property: property,
			oldValue: oldValue,
			newValue: newValue
		};
		$scope.changes.push(change);
		$scope.hideContextMenu();
	};
	
	$scope.contextMenu = function(table, instance, property) {
		var $contextMenu = $("#contextMenu");
		$("body").on("contextmenu", "table td", function(e) {
			if ($scope.selectedTd.element) {
				$scope.selectedTd.element.css({
					'background-color': ""
				});
			}
			$scope.selectedTd = {
				element: $(e.target),
				table: table,
				instance: instance,
				property: property
				
			};
			$scope.selectedTd.element.css({
				'background-color': "red"
			});
			$contextMenu.css({
				display : "block",
				left : e.pageX,
				top : e.pageY
			});
			return false;
		});
	};

	$scope.hideContextMenu = function() {
		if ($scope.selectedTd.element) {
			$scope.selectedTd.element.css({
				'background-color': ""
			});
		}
		$('#contextMenu').hide();
		$scope.selectedTd = {};
	};
	
	$scope.discardChanges = function() {
		if (confirm("Are you sure to discard all changes?")) {
			var original = $scope.originalDataModel;
			$scope.dataModel = JSON.parse(JSON.stringify(original));
			$scope.changes = [];
		}
	};
	
	$scope.confirmChanges = function() {
		var changes = $scope.changes;
		$http({
            url : 'http://localhost:8090/changes',
            method : "POST",
            data : changes
        }).then(function(response) {
            $scope.updateScript = response.data;
            $('#updateScriptLoading').hide();
            $('#updateScriptTextarea').show();
            
        }, function(response) {
            alert('ERROR');
        });
		$('#changesModal').modal('show');
	};
	
	$scope.saveChanges = function() {
		if (confirm("Are you sure to save all changes?")) {
			$scope.originalDataModel = JSON.parse(JSON.stringify($scope.dataModel));
			$scope.changes = [];
			$('#changesModal').modal('hide');
		}
	};
	
	$scope.closeChangeModal = function() {
		$('#changesModal').modal('hide');
	};
	
	$scope.download = function() {
		var blob = new Blob([$scope.updateScript], { type: "application/json;charset=utf-8;" });			
		var downloadLink = angular.element('<a></a>');
                    downloadLink.attr('href', window.URL.createObjectURL(blob));
                    downloadLink.attr('download', 'updateScript.rdf');
		downloadLink[0].click();
	};
	
	$http.get("http://localhost:8090/layouts")
	    .then(function(response) {
	        $scope.layouts = response.data;
			$('#layoutLoading').hide();
			$('#layoutSelect').show();
			$scope.layout = $scope.layouts[0].uri.uri;
	    });
	
	$scope.reloadTable = function(instanceTable, instance) {
		var tableType = '';
		for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
			var lineLayout = $scope.screenLayout.lineLayouts[i];
			if (lineLayout.fromType.type == instanceTable.type.type) {
				tableType = lineLayout.toType.type;
				if (!tableType) {
					continue;
				}
				
				var filter = {
					limit: 40,
					uriFilters: {},
					columnFilters: {}				
				};
				for (var j = 0; j < instance.objectProperties.length; j++) {
					var objectProperty = instance.objectProperties[j];
					var uri = objectProperty.subjectUri.uri;
					var property = objectProperty.property.property;
					filter.uriFilters[property] = uri;
				}
				
				var endpoint = $scope.endpoint;
				var type = $scope.endpointType;
				var layoutUri = $scope.screenLayout.uri.uri;
				
				var request = {
					tableType: tableType,
					filter: filter,
					endpoint: endpoint,
					type: type,
					layoutUri: layoutUri
				};

				$scope.loadTableData(request);
			} else if (lineLayout.toType.type == instanceTable.type.type) {
				tableType = lineLayout.fromType.type;
				var property = lineLayout.property.property;
				if (!tableType || !property) {
					continue;
				}
				
				var filter = {
					limit: 40,
					uriFilters: {},
					columnFilters: {}				
				};
				filter.uriFilters[property] = instance.uri.uri;
				
				var endpoint = $scope.endpoint;
				var type = $scope.endpointType;
				var layoutUri = $scope.screenLayout.uri.uri;
				
				var request = {
					tableType: tableType,
					filter: filter,
					endpoint: endpoint,
					type: type,
					layoutUri: layoutUri
				};
				
				$scope.loadTableData(request);
			}
		}
	};
	
	$scope.filterChanged = function(sourceTable, propertyName) {
		
		var filter = {
			limit: 40,
			uriFilters: {},
			columnFilters: {}				
		};
		
		var tableFilters = $scope.filters[sourceTable.type.type];
		if (tableFilters) {
			for (var i = 0; i < sourceTable.columns.length; i++) {
				var property = sourceTable.columns[i];
				var propertyFilter = tableFilters[property.property];
				if (propertyName == property.property && propertyFilter.length < 2) {
					return;
				}
				filter.columnFilters[property.property] = propertyFilter;
			}
		}
		
		var endpoint = $scope.endpoint;
		var type = $scope.endpointType;
		var layoutUri = $scope.screenLayout.uri.uri;
		var tableType = sourceTable.type.type;
		
		var request = {
			tableType: tableType,
			filter: filter,
			endpoint: endpoint,
			type: type,
			layoutUri: layoutUri
		};
		
		$scope.loadTableData(request);
		
	};
	
	$scope.loadTableData = function(request) {
		$http.post('http://localhost:8090/data/table', request)
        .success(function (data, status, headers, config) {
        	var newInstances = data;
			for (var i = 0; i < $scope.dataModel.tables.length; i++) {
				if ($scope.dataModel.tables[i].type.type == request.tableType) {
					for (var j = 0; j < newInstances.length; j++) {
						if (!containsInstance($scope.dataModel.tables[i], newInstances[j])) {
							$scope.dataModel.tables[i].instances.push(newInstances[j]);
							$scope.originalDataModel.tables[i].instances.push(newInstances[j]);
						}
					}
				}
			}
        })
        .error(function (data, status, header, config) {
        	var message = {
    				caption: data.error,
    				text: data.message,
    				type: 'danger'
    	    	};
        	$scope.messages.push(message);
        });
	};
	
	$scope.run = function() {
		$('#dataSourceModal').modal('hide');
		$('#loading').show();
		
		var endpoint = $scope.endpoint;
		var type = $scope.endpointType;
		var layoutUri = $scope.layout;
	
		$http.get("http://localhost:8090/data?endpoint=" + endpoint + "&type=" + type + "&layoutUri=" + layoutUri)
		    .success(function(response) {
		    	var dataModel = response;
				for (var i = 0; i < dataModel.tables.length; i++) {
					var table = dataModel.tables[i];
					var scope = {};
					scope['table'] = table;
					$scope.order(scope, 0);
				}
				$scope.dataModel = dataModel;
				$scope.originalDataModel = JSON.parse(JSON.stringify(dataModel));
				
				$http.get("http://localhost:8090/layout?uri=" + layoutUri)
				    .success(function(response) {
				        var layout = response;
				        $scope.screenLayout = layout;
				        var layouts = {};
				        for (var key = 0; key < layout.blockLayouts.length; key++) {
				        	var bl = layout.blockLayouts[key];
				        	layouts[bl.forType.type] = bl;
				        }
						for (var i = 0; i < layout.lineLayouts.length; i++) {
							polyline(layout.lineLayouts[i]);
						}
				        $scope.layouts = layouts;
						
						$('#loading').hide();
						$('#workspace').show();
				    })
			        .error(function (data, status, header, config) {
			        	var message = {
			    				caption: data.error,
			    				text: data.message,
			    				type: 'danger'
			    	    	};
			        	$scope.messages.push(message);
						$('#loading').hide();
			        });
				
		    })
	        .error(function (data, status, header, config) {
	        	var message = {
	    				caption: data.error,
	    				text: data.message,
	    				type: 'danger'
	    	    	};
	        	$scope.messages.push(message);
				$('#loading').hide();
	        });
	};
	
	$scope.getObjectPropertyTitle = function(property) {
		var title = property;
		for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
			if ($scope.screenLayout.lineLayouts[i].property == property) {
				title = $scope.screenLayout.lineLayouts[i].label.labelSource;
			};
		}
		return title;
	};
	
	$scope.getObjectPropertyValue = function(uri) {
		for (var i = 0; i < $scope.dataModel.tables.length; i++) {
			var table = $scope.dataModel.tables[i];
			for (var j = 0; j < table.instances.length; j++) {
				var instance = table.instances[j];
				if (instance.uri.uri == uri) {
					for (var k = 0; k < instance.literalProperties.length; k++) {
						var literalProperty = instance.literalProperties[k];
						if (literalProperty.property.property == 'rdfs:label') {
							return literalProperty.value;
						}
					}
				}
			}
		}
		return "???";
	};
	
	$scope.getLinkedPropertyInstances = function(type, property) {
		for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
			var lineLayout = $scope.screenLayout.lineLayouts[i];
			if (lineLayout.fromType.type == type && lineLayout.property.property == property) {
				var toType = lineLayout.toType.type;
				for (var j = 0; j < $scope.dataModel.tables.length; j++) {
					var table = $scope.dataModel.tables[j];
					if (table.type.type == toType) {
						return table.instances;
					}
				}
			}
		}
		
	};
	
	$scope.cancelErrorMessage = function(message) {
		var index = $scope.messages.indexOf(message);
		if (index > -1) {
			$scope.messages.splice(index, 1);
		}
	};
	
	$scope.order = function(scope, index) {
		$scope.sort.column = index;
		$scope.sort.reverse = !$scope.sort.reverse; 
		var reverse = $scope.sort.reverse;
        scope.table.instances = $filter('orderBy')(scope.table.instances, $scope.mySorter, reverse);
    };
	
	$scope.mySorter = function(item) {
		var sortByColumn = $scope.sort.column;
		return item.literalProperties[sortByColumn].value;
	};
	
	$scope.selectInstance = function(table, instance) {
		if (table.selectedInstance == instance) {
			delete table.selectedInstance;
		} else {
			table.selectedInstance = instance;
			$scope.reloadTable(table, instance);
		}
	};
	
	$scope.filterLineLayouts = function(lineLayout) {
		if (!lineLayout || !$scope.selectedTd || !$scope.selectedTd.table) {
			return true;
		}
		var type = $scope.selectedTd.table.type.type;
		if (lineLayout.fromType.type == type) {
			return true;
		} else {
			return false;
		}
	};
	
	$scope.filterProperties = function(literalProperty) {
		if (literalProperty.property.property == 'URI') {
			return false;
		} else {
			return true;
		}
	};
	
	$scope.filterInstances = function(instance) {
		var show = true;
		if ($scope.screenLayout && $scope.screenLayout.lineLayouts) {
			for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
				var lineLayout = $scope.screenLayout.lineLayouts[i];
				if (lineLayout.toType.type == instance.type.type) {
					var sourceTable = getTableByType($scope.dataModel, lineLayout.fromType.type);
					if (sourceTable && sourceTable.selectedInstance && sourceTable.selectedInstance.objectProperties) {
						var partShow = false;
						for (var j = 0; j < sourceTable.selectedInstance.objectProperties.length; j++) {
							var objectProperty = sourceTable.selectedInstance.objectProperties[j];
							if (objectProperty.property.property == lineLayout.property.property) {
								if (!objectProperty.objectUri || !instance.uri) {
									continue;
								}
								if (objectProperty.objectUri.uri == instance.uri.uri) {
									partShow = true;
								}
							}
						}
						show = show && partShow;
					}
				} else if (lineLayout.fromType.type == instance.type.type) {
					var targetTable = getTableByType($scope.dataModel, lineLayout.toType.type);
					if (targetTable && targetTable.selectedInstance && targetTable.selectedInstance.objectProperties) {
						var partShow = false;
						for (var j = 0; j < instance.objectProperties.length; j++) {
							var objectProperty = instance.objectProperties[j];
							if (objectProperty.property.property == lineLayout.property.property) {
								if (!objectProperty.objectUri || !targetTable.selectedInstance.uri) {
									continue;
								}
								if (objectProperty.objectUri.uri == targetTable.selectedInstance.uri.uri) {
									partShow = true;
								}
							}
						}
						show = show && partShow;
					}
				}
			}
		}
		
		var tableFilters = $scope.filters[instance.type.type];
		if (tableFilters) {
			for (var i = 0; i < instance.literalProperties.length; i++) {
				var literalProperty = instance.literalProperties[i];
				var propertyFilter = tableFilters[literalProperty.property.property];
				if (!propertyFilter) {
					continue;
				}
				if (!literalProperty.value) {
					show = false;
				}
				var value = literalProperty.value.toString();
				if (value.toLowerCase().indexOf(propertyFilter.toLowerCase()) == -1) {
					show = false;
				}
			}
		}
		
		return show;
	};
	
	$scope.isNumeric = function(value) {
		return angular.isNumber(value);
	};
});

function polyline(lineLayout) {
	if (!lineLayout) {
		return;
	}
	var polyline = "";
	for (var i = 0; i < lineLayout.points.length; i += 1) {
		var point = lineLayout.points[i];
		var space = "";
		if (polyline != "") {
			space = " ";
		}
		polyline += space + point.x + "," + point.y;
	}
	lineLayout.polyline = polyline;
};

function containsInstance(table, instance) {
	for (var i = 0; i < table.instances.length; i++) {
		if (table.instances[i].uri.uri == instance.uri.uri) {
			return true;
		}
	}
	return false;
}

function getTableByType(dataModel, type) {
	for (var i = 0; i < dataModel.tables.length; i++) {
		var table = dataModel.tables[i];
		var tableType = dataModel.tables[i].type.type;
		if (tableType == type) {
			return table;
		}
	}
	return null;
}