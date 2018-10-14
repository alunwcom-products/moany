'use strict';

app.config(function($mdDateLocaleProvider) {
	$mdDateLocaleProvider.formatDate = function(date) {
		return moment(date).format('YYYY-MM-DD');
	};
});

app.controller('monthviewCtrl', ['$scope', '$http', 'transactionSvc', 'accountsSvc', 'budgetingSvc', function($scope, $http, transactionSvc, accountsSvc, budgetingSvc) {
	
	var self = this;
	
	// load data
	
	console.log("loading accounts");
	accountsSvc.getAllAccounts($scope);
	console.log($scope.accounts);
	
	$scope.tr = {};
	
	self.clearFilter = function() {
		$scope.selectedAccounts = [];
		$scope.startDate = new Date();
	}
	self.clearFilter();
	
	self.refreshList = function() {
		console.log("refreshList()");
		console.log("selectedAccounts = " + $scope.selectedAccounts);
		
		$scope.startDate = moment($scope.startDate).date(1).toDate();
		var month = moment($scope.startDate).format('YYYY-MM');
		console.log("month = " + month);
		
		transactionSvc.getTransactionsByMonth($scope, $scope.selectedAccounts, month);
	}
	self.refreshList();
	
	// events
	
	self.filterReset = function() {
		self.clearFilter();
		self.refreshList();
	}
	
	self.filterUpdate = function() {
		self.refreshList();
	}
	
	self.rowClicked = function(tr) {
		console.log(tr);
		$scope.tr = tr;
	}
	
	self.reconcile = function(tr) {
		console.log("reconcile: " + tr.budgetItem.id);
		
		console.log(tr.transactionDate);
		console.log(moment(tr.transactionDate));
		console.log(moment(tr.transactionDate).add(1, 'day').toDate());
		
		tr.budgetItem.startDate = moment(tr.transactionDate).add(1, 'day').format('YYYY-MM-DD');
		
		var result = budgetingSvc.putBudgetItem(tr.budgetItem, $scope);
		
		$scope.tr = {};
		self.refreshList();
	}
	
	self.monthForward = function() {
		$scope.startDate = moment($scope.startDate).add(1, 'month').toDate();
		console.log("month forward = " + $scope.startDate);
		self.refreshList();
	}
	
	self.monthNow = function() {
		$scope.startDate = moment().date(1).toDate();
		console.log("month now = " + $scope.startDate);
		self.refreshList();
	}
	
	self.monthBackward = function() {
		$scope.startDate = moment($scope.startDate).subtract(1, 'month').toDate();
		console.log("month backward = " + $scope.startDate);
		self.refreshList();
	}
	
	self.recError = function(tr) {
		
		if (tr.accBalance == null || tr.accBalance == '') return false;
		if (tr.stmtBalance == null || tr.stmtBalance == '') return false;
		
		if (tr.account.type == 'DEBIT') {
			if (tr.stmtBalance == tr.accBalance) return false;
		} else if (tr.account.type == 'CREDIT') {
			if ((tr.stmtBalance * -1) == tr.accBalance) return false;
		} else {
			console.log("unknown/undefined account type?");
		}
		
		return true;
	}
	
}]);
