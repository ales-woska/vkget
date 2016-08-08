var app = angular.module('vkgmt', []);

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
	$scope.changesServiceUrl = serverAddress + '/changes';
	$scope.dataServiceUrl = serverAddress + '/data';
	$scope.layoutsServiceUrl = serverAddress + '/layouts';
	$scope.layoutServiceUrl = serverAddress + '/layout';
	$scope.storedEndpoints = endpointList;
	
	$scope.connectionInfo = {
		endpoint: 'http://localhost:3030/data',
		type: 'virtuoso',
		useNamedGraph: true,
		useAutorization: false,
		namedGraph: 'http://mff.cuni.cz/testGraph',
		username: '',
		password: ''
	};
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
		$scope.connectionInfo.endpoint = $location.search()['endpoint'];
	}
	
	if ($location.search()['type']) {
		$scope.connectionInfo.type = $location.search()['type'];
	}
	
	if ($location.search()['namedGraph']) {
		$scope.connectionInfo.type = $location.search()['namedGraph'];
		if ($scope.connectionInfo.type) {
			$scope.connectionInfo.useNamedGraph = true;
		}
	}
	
	if ($location.search()['username']) {
		$scope.connectionInfo.username = $location.search()['username'];
		if ($scope.connectionInfo.username) {
			$scope.connectionInfo.useAuthorization = true;
		}
	}
	
	if ($location.search()['password']) {
		$scope.connectionInfo.password = $location.search()['password'];
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
			var removeMap = removeLinkedPropertyObject[property];
			for (value in removeMap) {
				for (var i = 0; i < instance.objectProperties.length; i++) {
					var objectProperty = instance.objectProperties[i];
					var valueMatch = removeMap[objectProperty.objectUri.uri];
					if (objectProperty.property.property == property && valueMatch) {
						var change = {
							uri: instance.uri,
							property: property,
							oldValue: value,
							newValue: null
						};
						$scope.changes.push(change);
						instance.objectProperties.splice(i, 1);
					}
				}
			}
		}

		$scope.removeLinkedPropertyObject = {};
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
			while (e.target.localName != 'td') {
				e.target = e.target.parentElement;
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
            url : $scope.changesServiceUrl,
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
	
	$http.get($scope.layoutsServiceUrl)
	    .then(function(response) {
	        $scope.layouts = response.data;
			$('#layoutLoading').hide();
			$('#layoutSelect').show();
			$scope.layout = $scope.layouts[0].uri.uri;
	    });
	
	$scope.reloadTable = function(instanceTable, instance) {
		for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
			var lineLayout = $scope.screenLayout.lineLayouts[i];
			if (lineLayout.fromType.type == instanceTable.type.type) {
				var tableType = lineLayout.toType.type;
				if (!tableType || !instance || !instance.objectProperties) {
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
				
				var layoutUri = $scope.screenLayout.uri.uri;
				
				var request = {
					tableType: tableType,
					filter: filter,
					connectionInfo: $scope.connectionInfo,
					layoutUri: layoutUri
				};
				$scope.loadTableData(request, instance);
				
			} else if (lineLayout.toType.type == instanceTable.type.type) {
				var tableType = lineLayout.fromType.type;
				var property = lineLayout.property.property;
				if (!tableType || !property || !instance || !instance.uri) {
					continue;
				}
				
				var filter = {
					limit: 40,
					uriFilters: {},
					columnFilters: {}				
				};
				filter.uriFilters[property] = instance.uri.uri;
				
				var layoutUri = $scope.screenLayout.uri.uri;
				
				var request = {
					tableType: tableType,
					filter: filter,
					connectionInfo: $scope.connectionInfo,
					layoutUri: layoutUri
				};
				
				$scope.loadTableData(request, instance);
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
		
		var layoutUri = $scope.screenLayout.uri.uri;
		var tableType = sourceTable.type.type;
		
		var request = {
			tableType: tableType,
			filter: filter,
			connectionInfo: $scope.connectionInfo,
			layoutUri: layoutUri
		};
		
		$scope.loadTableData(request, null);
		
	};
	
	$scope.loadMoreData = function(sourceTable) {
		if (!sourceTable || !sourceTable.instances) {
			return;
		}
		
		var filter = {
			offset: sourceTable.instances.length,
			limit: 40,
			uriFilters: {},
			columnFilters: {}				
		};
		
		//column filters
		var tableFilters = $scope.filters[sourceTable.type.type];
		if (tableFilters) {
			for (var i = 0; i < sourceTable.columns.length; i++) {
				var property = sourceTable.columns[i];
				var propertyFilter = tableFilters[property.property];
				if (propertyFilter && propertyFilter.length < 2) {
					continue;
				}
				filter.columnFilters[property.property] = propertyFilter;
			}
		}
		
		// uri filters
		for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
			var lineLayout = $scope.screenLayout.lineLayouts[i];
			var property = lineLayout.property.property;
			
			// source table is FROM table
			if (lineLayout.fromType.type == sourceTable.type.type) {
				var otherTable = $scope.getTableByType(lineLayout.toType.type);
				if (otherTable.selectedInstance) {
					filter.uriFilters[property] = otherTable.selectedInstance.uri.uri;
				}
				
			// source table is TO table
			} else if (lineLayout.toType.type == sourceTable.type.type) {
				var otherTable = $scope.getTableByType(lineLayout.fromType.type);
				if (otherTable.selectedInstance) {
					filter.uriFilters[property] = otherTable.selectedInstance.uri.uri;
				}
			}
		}
		
		var layoutUri = $scope.screenLayout.uri.uri;
		var tableType = sourceTable.type.type;
		
		var request = {
			tableType: tableType,
			filter: filter,
			connectionInfo: $scope.connectionInfo,
			layoutUri: layoutUri
		};
		
		$scope.loadTableData(request, null);
		
	};
	
	$scope.loadingStack = [];
	
	$scope.loadTableData = function(request, instance) {
		
		$scope.loadingStack.push(request.tableType);
		
		$http.post($scope.dataServiceUrl + '/table', request)
        .success(function (data, status, headers, config) {
        	var newInstances = data;
        	var added = 0;
			for (var i = 0; i < $scope.dataModel.tables.length; i++) {
				if ($scope.dataModel.tables[i].type.type == request.tableType) {
					for (var j = 0; j < newInstances.length; j++) {
						if (!containsInstance($scope.dataModel.tables[i], newInstances[j])) {
							$scope.dataModel.tables[i].instances.push(newInstances[j]);
							$scope.originalDataModel.tables[i].instances.push(newInstances[j]);
							added++;
						}
					}
				}
			}
        	if (added == 0 && request.filter.offset > 0) {
        		alert("No more data satisfying filters.");
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

		var endpoint = $scope.connectionInfo.endpoint;
		var type = $scope.connectionInfo.type;
		var useNamedGraph = $scope.connectionInfo.useNamedGraph;
		var useAuthorization = $scope.connectionInfo.useAuthorization;
		var namedGraph = $scope.connectionInfo.namedGraph;
		var username = $scope.connectionInfo.username;
		var password = $scope.connectionInfo.password;
		var layoutUri = $scope.layout;
	
		$http.get($scope.dataServiceUrl + "?endpoint="+endpoint+"&type="+type+"&useNamedGraph="+useNamedGraph+
				"&useAuthorization="+useAuthorization+"&namedGraph="+namedGraph+"&username="+username+"&password="+password+"&layoutUri=" + layoutUri)
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
				
				$http.get($scope.layoutServiceUrl + "?uri=" + layoutUri)
				    .success(function(response) {
				        var layout = response;
				        $scope.screenLayout = layout;
				        
				        if ($scope.screenLayout.width < minWorkspaceWidth) {
							$scope.screenLayout.width = minWorkspaceWidth;
						}
						if ($scope.screenLayout.height < minWorkspaceHeight) {
							$scope.screenLayout.height = minWorkspaceHeight;
						}
				        
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
						$('#workspace').width($scope.screenLayout.width);
						$('#workspace').height($scope.screenLayout.height);
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
			$scope.reloadTable(table, instance, null);
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
	
	$scope.matchSourceTableSelectedInstance = function(sourceTable, property, instance) {
		if (!sourceTable || !sourceTable.selectedInstance || !sourceTable.selectedInstance.objectProperties) {
			return true;
		}
		var partShow = false;
		// Cycle over object properties and find one matching property from argument. Then comapre their uris.
		for (var j = 0; j < sourceTable.selectedInstance.objectProperties.length; j++) {
			var objectProperty = sourceTable.selectedInstance.objectProperties[j];
			if (objectProperty.property.property == property) {
				if (!objectProperty.objectUri || !instance.uri) {
					continue;
				}
				if (objectProperty.objectUri.uri == instance.uri.uri) {
					partShow = true;
				}
			}
		}
		return partShow;
	};
	
	$scope.matchTargetTableSelectedInstance = function(targetTable, property, instance) {
		if (!targetTable || !targetTable.selectedInstance || !targetTable.selectedInstance.objectProperties) {
			return true;
		}
		var partShow = false;
		
		// Cycle over object properties and find one matching property from argument. Then comapre their uris.
		for (var j = 0; j < instance.objectProperties.length; j++) {
			var objectProperty = instance.objectProperties[j];
			if (objectProperty.property.property == property) {
				if (!objectProperty.objectUri || !targetTable.selectedInstance.uri) {
					continue;
				}
				if (objectProperty.objectUri.uri == targetTable.selectedInstance.uri.uri) {
					partShow = true;
				}
			}
		}
		return partShow;
	};
	
	$scope.matchSelectedInstance = function(instance) {
		var show = true;
		for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
			var lineLayout = $scope.screenLayout.lineLayouts[i];
			var currentInstanceType = instance.type.type;
			var targetTableType = lineLayout.toType.type;
			var sourceTableType = lineLayout.fromType.type;
			var property = lineLayout.property.property;
			
			// examining source table
			if (targetTableType == currentInstanceType) {
				var sourceTable = $scope.getTableByType(sourceTableType);
				var partShow = $scope.matchSourceTableSelectedInstance(sourceTable, property, instance);
				show = show && partShow;
				
			// examining target table
			} else if (sourceTableType == currentInstanceType) {
				var targetTable = $scope.getTableByType(targetTableType);
				var partShow = $scope.matchTargetTableSelectedInstance(targetTable, property, instance);
				show = show && partShow;
			}
		}
		return show;
	};
	
	$scope.filterInstances = function(instance) {
		var show = true;
		if ($scope.screenLayout && $scope.screenLayout.lineLayouts) {
			var matchSelectedInstance = $scope.matchSelectedInstance(instance);
			show = show && matchSelectedInstance;
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

	$scope.getTableByType = function(type) {
		for (var i = 0; i < $scope.dataModel.tables.length; i++) {
			var table = $scope.dataModel.tables[i];
			var tableType = $scope.dataModel.tables[i].type.type;
			if (tableType == type) {
				return table;
			}
		}
		return null;
	};
	
	$scope.getInstanceByTypeAndUri = function(table, uri) {
		for (var i = 0; i < table.instances.length; i++) {
			var instance = table.instances[i];
			if (instance.uri.uri == uri) {
				return instance;
			}
		}
		return null;
	};
	
	$scope.getLiteralErrorClass = function(error) {
		var style = getCommonErrorStyle(error);
		return style;
	};
	
	$scope.getObjectErrorClass = function(error) {
		var style = getCommonErrorStyle(error);
		return style;
	};
	
	$scope.errorClicked = function(error, property) {
		if (confirm("Are you sure to resolve the error?")) {
			for (var i = 0; i < property.errors.length; i++) {
				if (property.errors[i] == error) {
					property.errors.splice(i, 1);
					var change = {
						uri: error.uri,
						property: null,
						oldValue: null,
						newValue: null
					};
					$scope.changes.push(change);
				}
			}
		}
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

function getCommonErrorStyle(error) {
	var style = {};
	var color = '#5cb85c';
	if (error.value <= 0.5) {
		color = '#d9534f';
	} else if (error.value <= 0.75) {
		color = '#f0ad4e';
	} else if (error.value <= 0.9) {
		color = '#f0ad4e';
	}
	
	if (error.severity == 'ERROR') {
		style['background'] = color;
	} else if (error.severity == 'WARNING') {
		style['-moz-border-radius'] = '10px';
		style['-webkit-border-radius'] = '10px';
        style['border-radius'] = '10px';
		style['background'] = color;
	} else if (error.severity == 'INFO') {
		style['border-left'] = '10px solid transparent';
        style['border-right'] = '10px solid transparent';
        style['border-bottom'] = '20px solid ' + color;
	}
	
	return style;
}