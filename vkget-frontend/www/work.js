var app = angular.module('vkget', []);

app.controller('dataController', function($scope, $http, $filter) {
	$scope.endpoint = 'http://dbpedia.org/sparql';
	$scope.endpointType = 'other';
	
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
						for (var j = 0; j < sourceTable.selectedInstance.objectProperties.length; j++) {
							var objectProperty = sourceTable.selectedInstance.objectProperties[j];
							if (objectProperty.property == lineLayout.property) {
								if (objectProperty.objectUri != instance.objectURI) {
									return false;
								}
							}
						}
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

			
			$http.get("http://localhost:8090/layout?uri=" + layoutUri)
		    .then(function(response) {
		        var layout = response.data;
		        $scope.screenLayout = layout;
		        var layouts = {};
		        for (var key = 0; key < layout.blockLayouts.length; key++) {
		        	var bl = layout.blockLayouts[key];
		        	layouts[bl.forType] = bl;
		        }
		        $scope.layouts = layouts;
				var c = document.getElementById('panelCanvas');
				var ctx = c.getContext('2d');
				for (var i = 0; i < $scope.screenLayout.lineLayouts.length; i++) {
					layout = $scope.screenLayout.lineLayouts[i];
					ctx.beginPath();
					if (layout.lineType == 'dashed') {
						ctx.setLineDash([5,5]);
					} else if (layout.lineType == 'dotted') {
						ctx.setLineDash([1,5]);
					}
					ctx.lineWidth = layout.lineThickness;
					ctx.strokeStyle = layout.lineColor;
					if (layout.points.length > 1) {
						var first = layout.points[0];
						ctx.moveTo(first.x, first.y);
						for (var j = 1; j < layout.points.length; j++) {
							var point = layout.points[j];
							ctx.lineTo(point.x, point.y);
						}
					}
					ctx.stroke();
				}
				
				$('#loading').hide();
				$('#workspace').show();
		    });
			
	    });
	};
});