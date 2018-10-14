'use strict';

app.directive('stmtModel', function ($parse) {
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
			var model = $parse(attrs.stmtModel);
			var modelSetter = model.assign;
			
			element.bind('change', function(){
				scope.$apply(function(){
					modelSetter(scope, element[0].files[0]);
				});
			});
		}
	};	
	/*
	return {
		scope: {
			file: '='
		},
		link: function (scope, element, attributes) {
			element.bind('change', function (event) {
				var file = event.target.files[0];
				scope.file = file ? file : undefined;
				scope.$apply();
			});
		}
	};
	*/
});

app.controller('statementCtrl', ['$scope', '$window', '$filter', '$http', 'statementSvc', function($scope, $window, $filter, $http, statementSvc) {
	
	statementSvc.getImporters($scope);
	
	$scope.postAction = "/upload?" + $window.csrfparameter + "=" + $window.csrftoken
	console.log("postAction: " + $scope.postAction);
	
	$scope.stmt = {
		"autoacc": true,
		"nodup": true
	}
	
	// Submit form function
	var stmtSubmit = function() {
		console.log("stmtSubmit");
		
		console.log($scope.importer)
		
		console.log($scope.file);
		//if ($scope.file == undefined) {
		//	console.log("Shouldn't submit - no file!");
		//} else {
		statementSvc.postStatement($scope);
		//}
	}
	$scope.stmtSubmit = stmtSubmit;
	
}]);
