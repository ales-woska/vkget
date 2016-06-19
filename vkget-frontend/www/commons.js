vkgetCommons = angular.module('vkgetCommons', []);

vkgetCommons.factory('vkgetCommons', function($rootScope) {
	  var vkgetCommons = {};

	  vkgetCommons.showError = function (data, status, header, config) {
	    	var message = {
				caption: data.error,
				text: data.message,
				type: 'danger'
	    	};
	    	$scope.messages.push(message);
		};

	  return vkgetCommons;
	
});