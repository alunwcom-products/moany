'use strict';

app.controller('accountsCtrl', ['$scope', '$filter', '$http', 'accountsSvc', function($scope, $filter, $http, accountsSvc) {
	
	console.log("loading 'accountsCtrl'");
	accountsSvc.getAllAccounts($scope);
	
	accountsSvc.getAccountTypes($scope);
	console.log($scope.accountTypes);
	
	
	// Clear edit form
	this.accClear = function() {
		console.log("accClear");
		$scope.acc = {};
		$scope.accForm.$setPristine();
	}
	
	// Select account function
	this.accClick = function(cid) {
		console.log("accClick: " + cid);
		
		var acc = $filter('filter')($scope.accounts, function (d) {return d.id === cid;})[0];
		// If you want to see the result, just check the log
		console.log(acc);
		
		$scope.acc = acc;
		$scope.accForm.$setPristine();
	}
	
	// Submit form function (save)
	this.accSubmit = function(acc) {
		console.log("Save account: " + acc.id);
		
		// TODO better validation
		if (Object.keys(acc).length != 0) {
			var result = accountsSvc.putAccount(acc, $scope);
			console.log(result);
		}
		
		$scope.acc = {};
	}
	
	// Delete form function
	this.accDelete = function(acc) {
		console.log("Delete account: " + acc.id);
		
		var result = accountsSvc.deleteAccount(acc.id, $scope);
		
		$scope.acc = {};
	}
	
}]);
