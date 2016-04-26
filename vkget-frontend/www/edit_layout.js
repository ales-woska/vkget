var app = angular.module('vkget', []);

app.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
      });
  });

app.filter('colons', function () {
	return function (input) {
		return input.replace(/:/g, '');
	};
});

app.filter('subtract', function ($filter) {
	return function (input, criteria) {
		if (!criteria) {
			return input;
		}
		var output = input.filter(function(x) { return criteria.indexOf(x) < 0; });
		return output;
	};
});

app.directive('linesSvg', function () {
    var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = 'linesSvg.html';
    directive.scope = {
        screenLayout: '=screenLayout',
        getLines: '&getLines'
    };
	return directive;
});

app.directive('blockForm', function () {
    var directive = {};
    directive.restrict = 'E';
    directive.transclude = true;
    directive.templateUrl = 'blockForm.html';
    directive.scope = {
        currBlock: '=block',
        addTitleType: "&addTitleType",
        removeTitleType: "&removeTitleType",
        addProperty: "&addProperty",
        removeProperty: "&removeProperty"
    };
	return directive;
});

app.directive('lineForm', function () {
    var directive = {};
    directive.restrict = 'E';
    directive.transclude = true;
    directive.templateUrl = 'lineForm.html';
    directive.scope = {
		screenLayout: '=screenLayout',
        lineLayout: '=lineLayout',
        addLine: "&addLine",
    };
	return directive;
});

app.controller('layoutController', function($scope, $location, $window, $http) {
	var uri = $location.search()['uri'];
	$http.get("http://localhost:8090/layout?uri=" + uri).then(
			function(response) {
				$scope.uri = uri;
				var screenLayout = response.data;
				for (var i in screenLayout.lineLayouts) {
					initLines(screenLayout.lineLayouts[i]);
					polyLine(screenLayout.lineLayouts[i]);
				}
				$scope.screenLayout = screenLayout;
			});
	
	function polyLine(lineLayout) {
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
	}
	
	function initLines(lineLayout) {
		if (!lineLayout) {
			return;
		}
		var lines = [];
		for (var i = 0; i < lineLayout.points.length - 1; i += 1) {
			var point1 = lineLayout.points[i];
			var point2 = lineLayout.points[i + 1];
			var newLine = [point1.x, point1.y, point2.x, point2.y];
			lines.push(newLine);
		}
		lineLayout.lines = lines;
	}
	
	$scope.mouseDown = false;
	$scope.mouseDownBL = null;
	$scope.top = 0;
	$scope.left = 0;
	$scope.offsetTop = 0;
	$scope.offsetLeft = 0;
	$scope.parentWidth = 0;
	$scope.parentHeight = 0;
		
	$scope.addLine = function() {
		var newLine = $scope.lineLayout;
		var newObject = jQuery.extend(true, {}, newLine);
		$scope.lineLayout = {};
		$scope.screenLayout.lineLayouts.push(newObject);
	};
	
	$scope.openLineForm = function() {
		$('#lineModal').modal('toggle');
	};

	$scope.addProperty = function(blockLayout) {
		var property = blockLayout.toAddProperty;
		if (property == null) {
			return;
		}
		var rowLayout = {
			property: property.property,
			title: property.title,
			titleTypes: [property.titleType],
			aggregateFunctions: [property.aggregateFunction]
		};
		blockLayout.properties.push(rowLayout);
	};
	
	$scope.removeProperty = function(blockLayout, property) {
		var index = blockLayout.properties.indexOf(property);
		blockLayout.properties.splice(index, 1);
	};

	$scope.addTitleType = function(blockLayout) {
		var titleType = blockLayout.toAddTitle;
		if (titleType == null) {
			return;
		}
		if (!blockLayout.titleTypes) {
			blockLayout.titleTypes = [];
		}
		blockLayout.titleTypes.push(titleType);
	};
	
	$scope.removeTitleType = function(blockLayout, titleType) {
		var index = blockLayout.titleTypes.indexOf(titleType);
		blockLayout.titleTypes.splice(index, 1);
	};

	$scope.openModal = function(blockLayout) {
		var id = "#blockForm" + blockLayout.forType.replace(":", "");
		$(id).modal('toggle');
	};
	
	$scope.onMouseDown = function(event, blockLayout) {
		if ($scope.mouseDown == false) {
			$scope.mouseDown = true;
			$scope.left = event.screenX - blockLayout.left - event.offsetX;
			$scope.top = event.screenY - blockLayout.top - event.offsetY;
			$scope.offsetLeft = event.offsetX;
			$scope.offsetTop = event.offsetY;
			$scope.mouseDownEl = event.toElement;
			$scope.mouseDownBL = blockLayout;
			$scope.parentWidth = event.toElement.parentElement.offsetWidth;
			$scope.parentHeight = event.toElement.parentElement.offsetHeight;
		}
	};
	
	$scope.onMouseMove = function(event) {
		if (event && $scope.mouseDown == true) {
			var left = $scope.left;
			var top = $scope.top;
			var newLeft = (event.screenX - $scope.offsetLeft) - left;
			var newTop = (event.screenY - $scope.offsetTop) - top;
			if (newLeft < 0) {
				$scope.mouseDownBL.left = 0;
			} else if ((newLeft + $scope.mouseDownBL.width) > $scope.parentWidth) {
				$scope.mouseDownBL.left = $scope.parentWidth - $scope.mouseDownBL.width;
			} else {
				$scope.mouseDownBL.left = newLeft;
			}
			if (newTop < 0) {
				$scope.mouseDownBL.top = 0;
			} else if ((newTop + $scope.mouseDownBL.height) > $scope.parentHeight) {
				$scope.mouseDownBL.top = $scope.parentHeight - $scope.mouseDownBL.height;
			} else {
				$scope.mouseDownBL.top = newTop;
			}
		}
	};
	
	$scope.onMouseUp = function(event) {
		$scope.mouseDown = false;
		$scope.mouseDownBL = null;
	};
	
	$scope.saveLayout = function() {
		$http.post('http://localhost:8090/layout/save', $scope.screenLayout)
        .success(function (data, status, headers, config) {
			$window.location.href = "layout.html?saved=" + $scope.screenLayout.name;
        })
        .error(function (data, status, header, config) {
        	$('.alert').show();
        });
	};
	
	$scope.cancel = function() {
		$window.location.href = "layout.html";
	};
	
	$scope.addTable = function() {
		var blockLayout = newBlockLayout();
		$scope.screenLayout.blockLayouts.push(blockLayout);
	};
	
	$scope.removeTable = function(blockLayout) {
		var index = $scope.screenLayout.blockLayouts.indexOf(blockLayout);
		$scope.screenLayout.blockLayouts.splice(index, 1);
	};
	
	function newBlockLayout() {
		var blockLayout = {};
		blockLayout.forType = 'for_type';
		blockLayout.background = '#aaaaaa',
		blockLayout.height = 100;
		blockLayout.width = 100;
		blockLayout.left = 0;
		blockLayout.top = 0;
		blockLayout.properties = [];
		blockLayout.title = '';
		blockLayout.titleTypesFromString = 'CONSTANT';
		blockLayout.fontColor = 'black';
		blockLayout.fontSize = 10;
		blockLayout.lineColor = 'black';
		blockLayout.lineType = 'SOLID';
		blockLayout.lineThickness = 1;
		blockLayout.uri = 'asfasfasfasf';
		return blockLayout;
	}
});
