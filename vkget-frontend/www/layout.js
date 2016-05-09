var app = angular.module('vkget', []);
		
app.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
      });
  });

app.controller('layoutController', function($scope, $http, $location) {
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
});