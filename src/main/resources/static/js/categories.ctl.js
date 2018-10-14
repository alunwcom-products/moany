'use strict';

app.controller('categoryCtrl', [
		'$scope', 
		'$filter', 
		'$http', 
		'$log',
		'$mdDialog',
		'categorySvc',
		function($scope, $filter, $http, $log, $mdDialog, categorySvc) {
	
	$log.info("categoryCtrl loading...");
			
	var self = this;
	
	categorySvc.getAllCategories($scope);
	$scope.c = {};
	self.budget = {};
	
	self.cSave = function() {
		$log.debug("catSave");
		$log.debug($scope.c);
		
		var result = categorySvc.putCategory($scope.c, $scope);
		$log.debug($scope.result);
		$scope.c = result;
	}
	
	self.cClear = function() {
		$log.debug("cClear");
		$scope.c = {};
		$scope.catForm.$setPristine();
	}
	
	self.cDelete = function() {
		$log.debug("cDelete");
		$log.debug($scope.c);
		
		var result = categorySvc.deleteCategoryById($scope.c.id, $scope);
		$log.debug($scope.result);
		$scope.c = {}
	}
	
	self.catSelect = function(cat) {
		$log.debug("catSelect");
		$log.debug(cat);
		
		$scope.c = cat;
		
		$log.debug($scope.c.parent);
	}
	
	self.catAddBudget = function(id, budget) {
		$log.debug("catAddBudget: " + id + ", " + budget);
		
		if ($scope.c.id != null) {
			$log.debug("update!");
			
			var result = categorySvc.addCategoryBudget(id, budget, $scope);
			
			self.budget = {};
		} else {
			$log.debug("ignore!");
		}
		
		
		//$log.debug($scope.result);
		//$scope.c = result;
	}
	
	self.catRemoveBudget = function(id) {
		$log.debug("catRemoveBudget: " + id);
		
		var result = categorySvc.removeCategoryBudget(id, $scope);
		$log.debug($scope.result);
	}
	
	
}]);
