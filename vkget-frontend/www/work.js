var app = angular.module('vkget', []);

app.controller('dataController', function($scope, $http, $filter) {
	$scope.endpoint = 'http://dbpedia.org/sparql';
	
	$scope.sort = {
        column: 0,
        reverse: true
    };
	
	$scope.filter = {
        column: 0,
        text: ""
    };
	
	$scope.getTitleForProperty = function(layout, property) {
		if (layout === undefined) {
			return "";
		}
    	for (var j in layout.properties) {
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
    
	$scope.filter = function(scope, index) {
		$scope.filter.column = index;
		$scope.filter.text = scope.search[index];
		
		var instances = scope.table.instances;
		for (var i in $scope.dataModel.tables) {
			var table = $scope.dataModel.tables[i];
			if (scope.table == table) {
				instances = table.instances;
			}
		}
		
        scope.table.instances = $filter('filter')(instances, $scope.myFilter);
    };
	
	$scope.myFilter = function(item) {
		if ($scope.filter.text == "") {
			return true;
		} else {
			var col = $scope.filter.column;
			var text = $scope.filter.text;
			var value = item.literalProperties[col].value.toString();
			return value.toLowerCase().indexOf(text.toLowerCase()) >= 0;
		}
	};
	
	$http.get("http://localhost:8090/layouts")
    .then(function(response) {
        $scope.layouts = response.data;
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
		        for (var key in layout.blockLayouts) {
		        	var bl = layout.blockLayouts[key];
		        	layouts[bl.forType] = bl;
		        }
		        $scope.layouts = layouts;
				var c = document.getElementById('panelCanvas');
				var ctx = c.getContext('2d');
				for (var i in $scope.screenLayout.lineLayouts) {
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
						for (var i = 1; i < layout.points.length; i++) {
							var point = layout.points[i];
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