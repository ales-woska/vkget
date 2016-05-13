var app = angular.module('vkget', []);

app.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
      });
  });

app.controller('layoutController', function($scope, $http, $location) {
	$scope.messages = [];
	
	var saved = $location.search()['saved'];
	if (saved) {
		$scope.savedName = saved;
		$scope.show = '';
	} else {
		$scope.show = 'hidden';
	}
	
	$http.get("http://localhost:8090/layouts")
    .then(function(response) {
        $scope.layouts = response.data;
    });
	
	$scope.removeLayout = function(screenLayout) {
		if (confirm('Are you sure to remove layout "' + screenLayout.name + '"?')) {
			$http.post('http://localhost:8090/layout/remove', screenLayout)
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
	    			caption: 'Error!',
	    			text: data.error,
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