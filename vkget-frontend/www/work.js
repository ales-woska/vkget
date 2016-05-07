var app = angular.module('vkget', []);

app.config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
}
]);

app.controller('dataController', function($scope, $http, $filter, $window) {
	$scope.endpoint = 'http://dbpedia.org/sparql';
	$scope.endpointType = 'other';
	$scope.changes = [{
		'objectUri': 'aaa',
		'property': 'bbb',
		'oldValue': 'ccc',
		'newValue': 'ddd'},{
		'objectUri': '111',
		'property': '222',
		'oldValue': '333',
		'newValue': '444'
		}];
	$scope.updateScript = '';
	
	$scope.discardChanges = function() {
		if (confirm("Are you sure to discard all changes?")) {
			$scope.dataModel = $scope.originalDataModel;
			$scope.changes = [];
		}
	};
	
	$scope.confirmChanges = function() {
		var changes = $scope.changes;
		var dataModel = $scope.dataModel;
		
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
			$scope.originalDataModel = $scope.dataModel;
			$scope.changes = [];
			$('#changesModal').modal('hide');
		}
	};
	
	$scope.closeChangeModal = function() {
		$('#changesModal').modal('hide');
	};
	
	$scope.download = function() {
		var blob = new Blob([$scope.changes], { type: "application/json;charset=utf-8;" });			
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
			$scope.originalDataModel = jQuery.extend({}, dataModel);

			
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

$(function() {

	var $contextMenu = $("#contextMenu");
	var $td = null;
	
	$("body").on("contextmenu", "table td", function(e) {
		if ($td) {
			$td.css({
				'background-color': ""
			});
		}
		$td = $(e.target);
		$td.css({
			'background-color': "red"
		});
		$contextMenu.css({
			display : "block",
			left : e.pageX,
			top : e.pageY
		});
		return false;
	});

	$contextMenu.on("click", "a", function(e) {
		hide();
	});
	
	$contextMenu.on("click", "#closeContextMenu", function(e) {
		hide();
	});
	
	function hide() {
		if ($td) {
			$td.css({
				'background-color': ""
			});
		}
		$contextMenu.hide();
		
	}

});