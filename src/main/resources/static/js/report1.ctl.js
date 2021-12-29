'use strict';

app.config(function($mdDateLocaleProvider) {
	$mdDateLocaleProvider.formatDate = function(date) {
		return moment(date).format('YYYY-MM-DD');
	};
});

app.controller('reportCtrl', ['$scope', '$filter', '$http', 'categorySvc', 'accountsSvc', 'transactionSvc', function($scope, $filter, $http, categorySvc, accountsSvc, transactionSvc) {
	
	var self = this;
	
	$scope.resetAccounts = function() {
		$scope.selectedAccounts = [];
		$scope.startDate = moment().subtract(6, 'month').toDate();
		$scope.endDate = new Date();
	}
	$scope.resetAccounts();

	$scope.refreshLists = function() {

		console.log("refreshLists()");

		console.log("selectedAccounts = " + $scope.selectedAccounts);
		console.log("startDate = " + $scope.startDate.toLocaleDateString());
		console.log("endDate = " + $scope.endDate.toLocaleDateString());

		// Need to reformat dates for service call
		var start = moment($scope.startDate).format('YYYY-MM-DD');
		var end = moment($scope.endDate).format('YYYY-MM-DD');

		console.log("startDate = " + start);
		console.log("endDate = " + end);

		accountsSvc.getAllAccounts($scope);

		categorySvc.getCategorySummaryByMonth($scope, $scope.selectedAccounts, start, end);
	}
	$scope.refreshLists();

	/* TODO rough WIP calc */
	self.budgetStatus = function(amount, budget) {
		console.log("amount = " + amount + ", budget = " + budget);

		if (amount == undefined || amount == null) {
			amount = 0;
		}

		if (budget == undefined || budget == null) {
			budget = 0;
		}

		if (budget == 0 && amount < 0) {
			return "color: red;";
		}

		if (budget == 0) {
			return "color: grey;";
		}

		budget = budget * -1 // invert budget
		var budgetThresholdFactor = 0.75;
		var budgetThreshold = budget * budgetThresholdFactor;
		console.log("budgetThreshold = " + budgetThreshold + " [" + budgetThresholdFactor + "]");

		if ((amount + budget) < 0) {
			console.log("red");
			return "color: red;";
		} else if ((amount + budgetThreshold) < 0) {
			console.log("orange");
			return "color: orange;";
		} else {
			console.log("green");
			return "color: green;";
		}
	}

	self.total = function(cat) {
		var total = 0;
		for (var key in cat.months) {
			console.log("key = " + key);
			total += cat.months[key];
		}

		console.log("total for '" + cat.category.fullName + "' = " + total);

		return total;
	}

	self.budget = function(cat) {
		var budget = 0;
		for (var key in cat.months) {
			budget += cat.category.monthlyBudget;
		}

		console.log("budget for '" + cat.category.fullName + "' = " + budget);

		return budget;
	}

	self.totalStatus = function(cat) {
		var amount = self.total(cat);
		var budget = self.budget(cat);
		return self.budgetStatus(amount, budget);
	}

	$scope.filterCats = function(cat) {
		console.log("cat... " + cat.category.name)
		console.log(cat)
		//console.log("cat = " + cat.name + " - > " + cat.name === 'Uncategorized')
		//console.log("cat = " + cat.name + " - > " + cat.name === 'All Categories')

		return cat.category.name !== 'Uncategorized' && cat.category.name !== 'All Categories';
		//return true;
	}

	// Show all dates/acccounts
	$scope.accReset = function() {
		console.log("accReset");
		$scope.resetAccounts();
		$scope.refreshLists();
	}

	// Show selected dates/accounts
	$scope.accUpdate = function() {
		console.log("accUpdate");
		$scope.refreshLists();
	}




}]);