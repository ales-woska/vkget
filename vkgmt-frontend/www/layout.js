var app = angular.module('vkgmt', []);

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

app.controller('layoutController', function($scope, $http, $location) {
	$scope.messages = [];
	$scope.layoutsServiceUri = serverAddress + '/layouts';
	$scope.layoutServiceUri = serverAddress + '/layout';
	
	var saved = $location.search()['saved'];
	if (saved) {
		$scope.savedName = saved;
		$scope.show = '';
	} else {
		$scope.show = 'hidden';
	}
	
	$http.get($scope.layoutsServiceUri)
    .then(function(response) {
        $scope.layouts = response.data;
    });
	
	$scope.removeLayout = function(screenLayout) {
		if (confirm('Are you sure to remove layout "' + screenLayout.name + '"?')) {
			$http.post($scope.layoutServiceUri + '/remove', screenLayout)
	        .success(function (data, status, headers, config) {
	        	var message = {
	    			caption: 'Removed!',
	    			text: 'Layout ' + screenLayout.name +  ' was removed.',
	    			type: 'info'
	        	};
	        	$scope.messages.push(message);
	        	var index = $scope.layouts.indexOf(screenLayout);
	        	if (index > -1) {
	        		$scope.layouts.splice(index, 1);
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
		}
	};
	
	$scope.cancelErrorMessage = function(message) {
		var index = $scope.messages.indexOf(message);
		if (index > -1) {
			$scope.messages.splice(index, 1);
		}
	};
	
});