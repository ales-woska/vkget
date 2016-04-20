var app = angular.module('vkget', []);

app.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
      });
  });

app.directive('blockForm', function ($scope) {
    var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = 'blockForm.html';
    directive.scope = {
    	index: '=id',
        currBlock: '=block'
    };
    return directive;
});

app.directive('jointDiagram', function () {
    var directive = {
        link: link,
        restrict: 'E',
        scope: {
            height: '=',
            width: '=',
            gridSize: '=',
            graph: '=',
        }
    };

    return directive;

    function link(scope, element, attrs) {
        var diagram = newDiagram(scope.height, scope.width, scope.gridSize, scope.graph, element[0]);

        diagram.on('cell:pointerdblclick', function(element) {
        	var i = element.id.split("_")[1];
        	var blockFormId = "#blockForm" + (i - 1);
			$(blockFormId).modal('toggle');
		});

        diagram.on('blank:pointerclick', function (evt, x, y) {
        });

        diagram.on('link:options', function (evt, cellView, x, y) {
        });
    }

    function newDiagram(height, width, gridSize, graph, targetElement) {
        var paper = new joint.dia.Paper({
            el: targetElement,
            width: width,
            height: height,
            gridSize: gridSize,
            model: graph,
        });
        return paper;
    }

});

app.controller('layoutController', function($scope, $location, $window, $http) {
	$scope.graph = new joint.dia.Graph;
	
	var uri = $location.search()['uri'];
	$http.get("http://localhost:8090/layout?uri=" + uri).then(
			function(response) {
				$scope.uri = uri;
				var screenLayout = response.data;
				$scope.screenLayout = screenLayout;
				$scope.currBlock = screenLayout.blockLayouts[0];
				$scope.loadLayout();
			});
	
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
		var rect = new joint.shapes.basic.Rect({
			position : {
				x : 0,
				y : 0
			},
			size : {
				width : 100,
				height : 100
			},
			attrs : {
				rect : {
					fill : 'silver'
				},
				text : {
					text : 'new_table',
					fill : 'black'
				}
			}
		});
		$scope.graph.addCell(rect);
	};

	$scope.loadLayout = function() {
		$scope.graph.clear();
		if (!$scope.screenLayout) {
			return;
		}
		for (var i in $scope.screenLayout.blockLayouts) {
			var bl = $scope.screenLayout.blockLayouts[i];
			var rect = new joint.shapes.basic.Rect({
				id : bl.forType,
				position : {
					x : bl.left,
					y : bl.top
				},
				size : {
					width : bl.width,
					height : bl.height
				},
				attrs : {
					rect : {
						fill : bl.background
					},
					text : {
						text : bl.forType,
						fill : 'black'
					}
				}
			});
			$scope.graph.addCell(rect);
		}

		for (var i in $scope.screenLayout.lineLayouts) {
			var ll = $scope.screenLayout.lineLayouts[i];
			var link = new joint.dia.Link({
				source : {
					id : ll.fromType
				},
				target : {
					id : ll.toType
				}
			});
			link.on('change:vertices', function (element) {
				$('#lineModal').modal('toggle');
			});
			$scope.graph.addCell(link);
		}
	}
});