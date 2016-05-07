var app = angular.module('vkget', []);

app.config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
}
]);

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

app.controller('dataController', function($scope, $http, $filter, $window) {
	$scope.endpoint = 'http://dbpedia.org/sparql';
	$scope.endpointType = 'other';
	$scope.changes = [];
	$scope.updateScript = '';
	$scope.selectedTd = {};
	$scope.addRowObject = {};
	$scope.removeLinkedPropertyObject = {};
	$scope.addLinkedPropertyObject = {};
	
	$scope.editCell = function() {
		var instance = $scope.selectedTd.instance;
		var property = $scope.selectedTd.property;
		var oldValue = $scope.oldCellValue;
		var newValue = $scope.editCellValue;
		$scope.commonAction(instance.objectURI, property.propertyURI, oldValue, newValue);
		property.value = newValue;
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
		var property = $scope.selectedTd.property;
		for (var i = 0; i < instance.literalProperties.length; i++) {
			var currProperty = instance.literalProperties[i].propertyURI;
			if (currProperty == property.propertyURI) {
				$scope.oldCellValue = property.value;
				$scope.editCellValue = property.value;
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
			var currProperty = instance.literalProperties[i].propertyURI;
			if (currProperty == property.propertyURI) {
				oldValue = property.value;
				property.value = null;
			}
		}
		$scope.commonAction(instance.objectURI, property.propertyURI, oldValue, '');
	};
	
	$scope.addRow = function() {
		var table = $scope.selectedTd.table;
		var addRowObject = $scope.addRowObject;
		var addRowUri = addRowObject['uri'];
		var newInstance = {
				objectURI: addRowUri,
				type: table.typeUri,
				literalProperties: [],
				objectProperties: []
			};
		for (var key in addRowObject) {
			if (key == 'uri') {
				continue;
			}
			var change = {
				'objectUri': addRowUri,
				'property': key,
				'oldValue': null,
				'newValue': addRowObject[key]
			};
			$scope.changes.push(change);
			newInstance.literalProperties.push({
				propertyURI: key,
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
				'objectUri': instance.objectURI,
				'property': instance.literalProperties[i].propertyURI,
				'oldValue': instance.literalProperties[i].value,
				'newValue': null
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
		var property = addLinkedPropertyObject['property'];
		var value = addLinkedPropertyObject['value'];
		var change = {
			'objectUri': instance.objectURI,
			'property': property,
			'oldValue': null,
			'newValue': value
		};
		instance.objectProperties.push({
			property: property,
			subjectUri: instance.objectURI,
			objectUri: value
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
					'objectUri': instance.objectURI,
					'property': property,
					'oldValue': value,
					'newValue': null
				};
				$scope.changes.push(change);
				
				$scope.removeLinkedPropertyObject = {};
				for (var i = 0; i < instance.objectProperties.length; i++) {
					var objectProperty = instance.objectProperties[i];
					if (objectProperty.property == property) {
						instance.objectProperties.splice(i, 1);
					}
				}
			}
		}
		$('#removeLinkedPropertyModal').modal('hide');
		$scope.hideContextMenu();
	};
	
	$scope.commonAction = function(objectUri, property, oldValue, newValue) {
		var change = {
			'objectUri': objectUri,
			'property': property,
			'oldValue': oldValue,
			'newValue': newValue
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
	
	$scope.commitChange = function() {
		if (confirm("Are you sure to commit all changes?")) {
			$scope.originalDataModel = JSON.parse(JSON.stringify(dataModel));
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
	
	$scope.selectInstance = function(table, instance) {
		if (table.selectedInstance == instance) {
			delete table.selectedInstance;
		} else {
			table.selectedInstance = instance;
		}
	};
	
	$scope.filters = {};
	
	$scope.filterInstances = function(instance) {
		if ($scope.screenLayout && $scope.screenLayout.lineLayouts) {
			for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
				var lineLayout = $scope.screenLayout.lineLayouts[i];
				if (lineLayout.toType != instance.type) {
					continue;
				} else {
					var sourceTable = getTableByType($scope.dataModel, lineLayout.fromType);
					if (sourceTable && sourceTable.selectedInstance) {
						if (!sourceTable.selectedInstance.objectProperties || sourceTable.selectedInstance.objectProperties.length == 0) {
							return false;
						}
						for (var j = 0; j < sourceTable.selectedInstance.objectProperties.length; j++) {
							var objectProperty = sourceTable.selectedInstance.objectProperties[j];
							if (objectProperty.property == lineLayout.property) {
								if (objectProperty.objectUri == instance.objectURI) {
									return true;
								}
							}
						}
						return false;
					}
				}
			}
		}
		
		var tableFilters = $scope.filters[instance.type];
		if (!tableFilters) {
			return true;
		}
		
		for (var i = 0; i < instance.literalProperties.length; i++) {
			var property = instance.literalProperties[i];
			var propertyFilter = tableFilters[property.propertyURI];
			if (!propertyFilter) {
				continue;
			}
			var value = property.value.toString();
			if (value.toLowerCase().indexOf(propertyFilter.toLowerCase()) == -1) {
				return false;
			}
		}
		return true;
	};
	
	function getTableByType(dataModel, type) {
		for (var i = 0; i < dataModel.tables.length; i++) {
			if (dataModel.tables[i].typeUri == type) {
				return dataModel.tables[i];
			}
		}
		return null;
	}
	
	$scope.sort = {
        column: 0,
        reverse: true
    };
	
	$scope.getTitleForProperty = function(layout, property) {
		if (layout === undefined) {
			return "";
		}
    	for (var j = 0; j < layout.properties.length; j++) {
    		var rl = layout.properties[j];
    		if (rl.property == property) {
    			return rl.title;
    		}
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
	
	$http.get("http://localhost:8090/layouts")
    .then(function(response) {
        $scope.layouts = response.data;
		$('#layoutLoading').hide();
		$('#layoutSelect').show();
		$scope.layout = $scope.layouts[0].uri;
    });
	
	$scope.run = function() {
		$('#dataSourceModal').modal('hide');
		$('#loading').show();
		
		var endpoint = $scope.endpoint;
		var type = $scope.endpointType;
		var layoutUri = $scope.layout;
	
		$http.get("http://localhost:8090/data?endpoint=" + endpoint + "&type=" + type + "&layoutUri=" + layoutUri)
	    .then(function(response) {
	    	var dataModel = response.data;
			for (var i = 0; i < dataModel.tables.length; i++) {
				var table = dataModel.tables[i];
				var scope = {};
				scope['table'] = table;
				$scope.order(scope, 0);
			}
			$scope.dataModel = dataModel;
			$scope.originalDataModel = JSON.parse(JSON.stringify(dataModel));

			
			$http.get("http://localhost:8090/layout?uri=" + layoutUri)
		    .then(function(response) {
		        var layout = response.data;
		        $scope.screenLayout = layout;
		        var layouts = {};
		        for (var key = 0; key < layout.blockLayouts.length; key++) {
		        	var bl = layout.blockLayouts[key];
		        	layouts[bl.forType] = bl;
		        }
				for (var i = 0; i < layout.lineLayouts.length; i++) {
					polyline(layout.lineLayouts[i]);
				}
		        $scope.layouts = layouts;
				
				$('#loading').hide();
				$('#workspace').show();
		    });
			
	    });
	};
	
	$scope.getObjectPropertyTitle = function(property) {
		var title = property;
		for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
			if ($scope.screenLayout.lineLayouts[i].property == property) {
				title = $scope.screenLayout.lineLayouts[i].title;
			};
		}
		return title;
	};
	
	$scope.getObjectPropertyValue = function(uri) {
		var value = "";
		for (var i = 0; i < $scope.dataModel.tables.length; i++) {
			var table = $scope.dataModel.tables[i];
			for (var j = 0; j < table.instances.length; j++) {
				var instance = table.instances[j];
				if (instance.objectURI == uri) {
					for (var k = 0; k < instance.literalProperties.length; k++) {
						var property = instance.literalProperties[k];
						if (property.propertyURI == 'rdfs:label') {
							value = property.value;
						}
					}
				}
			}
		}
		return value;
	};
	
	$scope.getInstancesForType = function(type) {
		for (var i = 0; i < $scope.dataModel.tables.length; i++) {
			if ($scope.dataModel.tables[i].typeUri == type) {
				return $scope.dataModel.tables.instances;
			}
		}
	};
	
	$scope.getLinkedPropertyInstances = function(type, property) {
		for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
			var lineLayout = $scope.screenLayout.lineLayouts[i];
			if (lineLayout.fromType == type && lineLayout.property == property) {
				var toType = lineLayout.toType;
				for (var j = 0; j < $scope.dataModel.tables.length; j++) {
					var table = $scope.dataModel.tables[j];
					if (table.typeUri == toType) {
						return table.instances;
					}
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