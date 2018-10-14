'use strict';

app.config(function($mdDateLocaleProvider) {
	$mdDateLocaleProvider.formatDate = function(date) {
		return moment(date).format('YYYY-MM-DD');
	};
});

app.controller('budgetingCtrl', ['$scope', '$http', 'budgetingSvc', 'accountsSvc', 'categorySvc', function($scope, $http, budgetingSvc, accountsSvc, categorySvc) {
	
	var self = this;
	
	self.periodTypes = ["WEEKS","MONTHS"];
	
	// load data
	
	console.log("loading accounts");
	accountsSvc.getAllAccounts($scope);
	console.log($scope.accounts);
	
	console.log("loading categories");
	categorySvc.getAllCategories($scope);
	console.log($scope.categories);
	
	self.refreshList = function() {
		console.log("refreshList()");
		budgetingSvc.getBudgetItems($scope);
	}
	self.refreshList();
	
	// events
	
	// Submit form
	self.save = function() {
		// TODO validation?
		
		// TODO submission
		var result = budgetingSvc.putBudgetItem($scope.item, $scope);
		console.log(result);
		$scope.item = {};
	}
	
	// Clear form
	self.clear = function() {
		$scope.item = {};
	}
	
	// Delete selected
	self.deleteItem = function(item) {
		console.log("deleteItem(" + item.id + ")");
		budgetingSvc.deleteBudgetItem(item.id, $scope);
		$scope.item = {};
	};
	
	// Item selected
	self.itemClicked = function(item) {
		console.log("itemClicked(" + item.id + ")");
		$scope.item = item;
	}
}]);
