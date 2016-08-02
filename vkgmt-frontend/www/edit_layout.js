function escapeUri(uri) {
	var result = uri;
	if (uri.uri) {
		result = uri.uri;
	} else {
		result = uri;
	}
	return result.replace(/#|:|\.|\//gi, "");
}

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

var app = angular.module('vkgmt', []);

app.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
      });
  });

app.filter('colons', function () {
	return function (input) {
		if (!input) {
			return "";
		}
		return escapeUri(input);
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
    directive.templateUrl = 'lines_svg.html';
    directive.scope = {
        screenLayout: '=screenLayout',
        getLines: '&getLines',
        openModal: '&openModal'
    };
	return directive;
});

app.directive('blockForm', function () {
    var directive = {};
    directive.restrict = 'E';
    directive.transclude = true;
    directive.templateUrl = 'block_form.html';
    directive.scope = {
        currBlock: '=block',
        langs: '=langs',
        addProperty: "&addProperty",
        removeProperty: "&removeProperty"
    };
	return directive;
});

app.directive('lineForm', function () {
    var directive = {};
    directive.restrict = 'E';
    directive.transclude = true;
    directive.templateUrl = 'line_form.html';
    directive.scope = {
		screenLayout: '=screenLayout',
        lineLayout: '=lineLayout',
        saveLine: "&saveLine",
        removeLine: "&removeLine",
    };
	return directive;
});

app.controller('layoutController', function($scope, $location, $window, $http) {
	$scope.screenLayout = newScreenLayout();
	$scope.messages = [];
	$scope.layoutServiceUri = serverAddress + "/layout";
	
	var uri = $location.search()['uri'];
	if (uri) {
		uri = window.encodeURIComponent(uri);
		$http.get($scope.layoutServiceUri + "?uri=" + uri).then(
				function(response) {
					$scope.uri = uri;
					var screenLayout = response.data;
					for (var i in screenLayout.lineLayouts) {
						initLines(screenLayout.lineLayouts[i]);
						polyline(screenLayout.lineLayouts[i]);
					}
					$scope.screenLayout = screenLayout;
				});
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
	$scope.mouseDownLL = null;
	$scope.top = 0;
	$scope.left = 0;
	$scope.offsetTop = 0;
	$scope.offsetLeft = 0;
	$scope.parentWidth = 0;
	$scope.parentHeight = 0;
	$scope.langs = predefinedLangs;
	
	$scope.addNamespace = function() {
		var key = $scope.newKey;
		var value = $scope.newValue;
		$scope.screenLayout.namespaces[key] = value;
		$scope.newKey = '';
		$scope.newValue = '';
	};
	
	$scope.removeNamespace = function(key) {
		delete $scope.screenLayout.namespaces[key];
	};
		
	$scope.addLine = function() {
		if ($scope.screenLayout.blockLayouts.length < 2) {
			alert('To make a new connection there must be at least 2 tables.');
			return;
		}
		var newLine = null;
		var lineLayouts = $scope.screenLayout.lineLayouts;
		var lineLayout = $scope.screenLayout.lineLayouts[$scope.screenLayout.lineLayouts.length - 1];
		if (lineLayouts.length == 0 || (lineLayout.fromType.type && lineLayout.toType.type)) {
			newLine = newLineLayout($scope.screenLayout);
			$scope.screenLayout.lineLayouts.push(newLine);
		} else {
			newLine = lineLayout;
		}
		setTimeout(function(){
			$scope.toggleLineModal(newLine);
		}, 100);
	};
	
	$scope.saveLine = function(lineLayout) {
		var index = $scope.screenLayout.lineLayouts.indexOf(lineLayout);
		var error = false;
		for (var i in $scope.screenLayout.lineLayouts) {
			var curr = $scope.screenLayout.lineLayouts[i];
			if (i != index && curr.fromType.type == lineLayout.fromType.type && curr.toType.type == lineLayout.toType.type) {
				alert('Connection between types already exists, canceling.');
				error = true;
			}
			if (i != index && curr.fromType.type == lineLayout.toType.type && curr.toType.type == lineLayout.fromType.type) {
				alert('WARNING: connection in opposite direction already exists.');
			}
			if (lineLayout.fromType.type == lineLayout.toType.type) {
				alert('Connection can\'t be targeted to the same type, canceling.');
				error = true;
			}
		}
		
		if (!error && lineLayout && lineLayout.points.length == 0) {
			var b1 = null;
			var b2 = null;
			for (var j in $scope.screenLayout.blockLayouts) {
				var b = $scope.screenLayout.blockLayouts[j];
				if (b.forType.type == lineLayout.fromType.type) {
					b1 = b;
				} else if (b.forType.type == lineLayout.toType.type) {
					b2 = b;
				}
			}
			if (!b1 || !b2) {
				$scope.screenLayout.lineLayouts.splice(index, 1);
				alert('One of sources doesn\'t exists, canceling.');
				error = true;
			}
			
			if (!error) {
				var x = b1.left + (b1.width/2); 
				var y = b1.top + (b1.height/2);
				var point = {
					x: x,
					y: y
				};
				lineLayout.points.push(point);
				x = b2.left + (b2.width/2); 
				y = b2.top + (b2.height/2);
				point = {
					x: x,
					y: y
				};
				lineLayout.points.push(point);
				polyline(lineLayout);
			}
		}
		
		$scope.toggleLineModal(lineLayout);
	};
	
	$scope.removeLine = function(lineLayout) {
		if (!lineLayout) {
			return;
		}
		if (confirm('Are you sure to remove line?')) {
			$scope.toggleLineModal(lineLayout);
			$('.modal-backdrop').hide();
			var index = $scope.screenLayout.lineLayouts.indexOf(lineLayout);
			$scope.screenLayout.lineLayouts.splice(index, 1);
		}
	};
	
	$scope.toggleLineModal = function(lineLayout) {
		var id = "#lineForm" + escapeUri(lineLayout.uri.uri);
		$(id).modal('toggle');
	};

	$scope.addProperty = function(blockLayout) {
		var newColumn = blockLayout.toAddProperty;
		if (newColumn == null) {
			return;
		}
		
		var columnType = newColumn.columnType;
		var property = {};
		
		if (columnType == 'URI') {
			property = {
				property: 'URI'
			};
		} else if (columnType == 'LABEL') {
			property = {
				property: 'rdfs:label'
			};
		} else {
			if (newColumn.property) {
				property = newColumn.property;
			} else {
				alert('Please fill property.');
			}
		}

		var uriExists = false;
		for (var i = 0; i < blockLayout.properties.length; i++) {
			var columnLayout = blockLayout.properties[i];
			if (columnLayout.property.property == property.property) {
				uriExists = true;
				break;
			}
		}
		if (uriExists) {
			alert('Column ' + property.property + ' already exists.');
			return;
		}
		
		if (!newColumn.label) {
			newColumn.label = {
				labelSource: '',
				type: '',
				lang: ''
			};
		}
		
		var columnLayout = {
			uri: blockLayout.uri.uri + "Column" + blockLayout.properties.length,
			property: property,
			label: {
				labelSource: newColumn.label.labelSource,
				type: newColumn.label.type,
				lang: newColumn.label.lang
			},
			aggregateFunction: newColumn.aggregateFunction
		};
		blockLayout.properties.push(columnLayout);
		blockLayout.toAddProperty = {};
	};
	
	$scope.removeProperty = function(blockLayout, property) {
		var index = blockLayout.properties.indexOf(property);
		blockLayout.properties.splice(index, 1);
	};

	$scope.openModal = function(blockLayout) {
		var forType = blockLayout.forType.type;
		var id = "#blockForm" + escapeUri(forType);
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
			for (var i in $scope.screenLayout.lineLayouts) {
				var lineLayout = $scope.screenLayout.lineLayouts[i];
				if (lineLayout.fromType.type == blockLayout.forType.type || lineLayout.toType.type == blockLayout.forType.type) {
					$scope.mouseDownLL = lineLayout;
				}
			}
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

			var point = null;
			var diffPointX  = null;
			var diffPointY = null;
			if ($scope.mouseDownLL) {
				if ($scope.mouseDownLL.fromType.type == $scope.mouseDownBL.forType.type) {
					point = $scope.mouseDownLL.points[0];
				} else if ($scope.mouseDownLL.toType.type == $scope.mouseDownBL.forType.type) {
					point = $scope.mouseDownLL.points[$scope.mouseDownLL.points.length - 1];
				}
				diffPointX = point.x - $scope.mouseDownBL.left;
				diffPointY = point.y - $scope.mouseDownBL.top;
			}
			
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
			
			if ($scope.mouseDownLL) {
				point.x = $scope.mouseDownBL.left + diffPointX;
				point.y = $scope.mouseDownBL.top + diffPointY;
				polyline($scope.mouseDownLL);
			}
		}
	};
	
	$scope.onMouseUp = function(event) {
		$scope.mouseDown = false;
		$scope.mouseDownBL = null;
		$scope.mouseDownLL = null;
	};
	
	$scope.cancelErrorMessage = function(message) {
		var index = $scope.messages.indexOf(message);
		if (index > -1) {
			$scope.messages.splice(index, 1);
		}
	};
	
	$scope.saveLayout = function() {
		// remove empty line layouts
		for (var i = 0; i < $scope.screenLayout.lineLayouts; i++) {
			var lineLayout = $scope.screenLayout.lineLayouts[i];
			if (!lineLayout.fromType.type || !lineLayout.toType.type) {
				$scope.screenLayout.lineLayouts.splice(i, 1);
			}
		}
		
		$http.post($scope.layoutServiceUri + '/save', $scope.screenLayout)
        .success(function (data, status, headers, config) {
			var message = {
				caption: 'Saved',
				text: 'The layout was saved successfuly.',
				type: 'success'
	    	};
	    	$scope.messages.push(message);
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
	
	$scope.cancel = function() {
		$window.location.href = "layout.html";
	};
	
	$scope.addTable = function() {
		var blockLayout = newBlockLayout($scope.screenLayout);
		$scope.screenLayout.blockLayouts.push(blockLayout);
	};
	
	$scope.removeTable = function(blockLayout) {
		for (var i in $scope.screenLayout.lineLayouts) {
			var ll = $scope.screenLayout.lineLayouts[i];
			if (ll.fromType.type == blockLayout.forType.type || ll.toType.type == blockLayout.forType.type) {
				$scope.screenLayout.lineLayouts.splice(i, 1);
			}
		}
		
		var index = $scope.screenLayout.blockLayouts.indexOf(blockLayout);
		$scope.screenLayout.blockLayouts.splice(index, 1);
	};
	
	function newScreenLayout() {
		var screenLayout = {};
		screenLayout.uri = {uri: ''};
		screenLayout.name = '';
		screenLayout.blockLayouts = [];
		screenLayout.lineLayouts = [];
		screenLayout.namespaces = predefinedNamespaces;
		return screenLayout;
	}
	
	function newBlockLayout(screenLayout) {
		var blockLayout = {};
		
		var uriPostfix = 0;
		for (var i in screenLayout.blockLayouts) {
			var existing = screenLayout.blockLayouts[i];
			var uri = existing.uri;
			var uriIndex = uri.uri.indexOf('#new_block');
			if (uriIndex != -1) {
				uriPostfix = uri.uri.substring(uriIndex + 10);
				if (uriPostfix != '') {
					uriPostfix++;
				}
			}
		}
		
		var forTypePostfix = 0;
		for (var i in screenLayout.blockLayouts) {
			var existing = screenLayout.blockLayouts[i];
			var forType = existing.forType;
			var uriIndex = forType.name.indexOf('for_type');
			if (uriIndex != -1) {
				forTypePostfix = forType.name.substring(uriIndex + 8);
				if (forTypePostfix != '') {
					forTypePostfix++;
				}
			}
		}
		
		var uri = '#new_block' + uriPostfix;
		var forType = 'for_type' + forTypePostfix;
		
		blockLayout.uri = {uri: uri};
		blockLayout.forType = {type: forType, name: forType, prefix: ""};
		blockLayout.background = '#aaaaaa',
		blockLayout.height = 100;
		blockLayout.width = 100;
		blockLayout.left = 0;
		blockLayout.top = 0;
		blockLayout.properties = [];
		blockLayout.label = {
			labelSource: '',
			type: 'CONSTANT',
			lang: 'en'
		};
		blockLayout.fontColor = 'black';
		blockLayout.fontSize = 10;
		blockLayout.lineColor = 'black';
		blockLayout.lineType = 'SOLID';
		blockLayout.lineThickness = 1;
		return blockLayout;
	}
	
	function newLineLayout(screenLayout) {
		var newLineLayout = {};
		
		var uriPostfix = 0;
		for (var i in screenLayout.lineLayouts) {
			var lineLayout = screenLayout.lineLayouts[i];
			var uri = lineLayout.uri;
			var uriIndex = uri.uri.indexOf('#new_line');
			if (uriIndex != -1) {
				uriPostfix = uri.uri.substring(uriIndex + 9);
				if (uriPostfix != '') {
					uriPostfix++;
				}
			}
		}
		
		newLineLayout.uri = {uri: '#new_line' + uriPostfix};
		newLineLayout.fromType = {};
		newLineLayout.toType = {};
		newLineLayout.label = {
			labelSource: '',
			type: 'CONSTANT',
			lang: 'en'
		};
		newLineLayout.fontColor = 'black';
		newLineLayout.fontSize = 10;
		newLineLayout.lineColor = 'black';
		newLineLayout.lineType = 'SOLID';
		newLineLayout.lineThickness = 1;
		newLineLayout.points = [];
		return newLineLayout;
	}
});
