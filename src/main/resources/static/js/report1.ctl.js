'use strict';

app.config(function($mdDateLocaleProvider) {
	$mdDateLocaleProvider.formatDate = function(date) {
		return moment(date).format('YYYY-MM-DD');
	};
});

app.controller('reportCtrl1', ['$scope', '$filter', '$http', 'categorySvc', 'accountsSvc', 'transactionSvc', function($scope, $filter, $http, categorySvc, accountsSvc, transactionSvc) {
	
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

		categorySvc.getCategorySummary($scope, $scope.selectedAccounts, start);
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
    console.log("total(" + cat + ")");
		var total = 0;
		for (var entry in cat.actualAmount) {
			console.log("entry = " + entry);
			console.log("value = " + cat.actualAmount[entry]);
			total += cat.actualAmount[entry];
		}
		console.log("total for '" + cat.fullName + "' = " + total);
		return total;
	}

	self.budget = function(cat) {
    console.log("budget(" + cat + ")");
		var budget = 0;
		for (var entry in cat.plannedAmount) {
			console.log("entry = " + entry);
			console.log("value = " + cat.plannedAmount[entry]);
			budget += cat.plannedAmount[entry];
		}
		console.log("budget for '" + cat.fullName + "' = " + budget);
		return budget;
	}

	self.totalStatus = function(cat) {
		var amount = self.total(cat);
		var budget = self.budget(cat);
		return self.budgetStatus(amount, budget);
	}

	$scope.filterCats = function(cat) {
		console.log("cat... " + cat.name)
		console.log(cat)
		//console.log("cat = " + cat.name + " - > " + cat.name === 'Uncategorized')
		//console.log("cat = " + cat.name + " - > " + cat.name === 'All Categories')

		return cat.name !== 'Uncategorized' && cat.name !== 'All Categories';
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