'use strict';

app.config(function($mdDateLocaleProvider) {
	$mdDateLocaleProvider.formatDate = function(date) {
		return moment(date).format('YYYY-MM-DD');
	};
});

app.controller('transactionCtrl', ['$scope', '$filter', '$http', 'transactionSvc', 'accountsSvc', function($scope, $filter, $http, transactionSvc, accountsSvc) {
	
	var self = this;
	
	console.log("get categories");
	transactionSvc.getTransactionTypes($scope);
	console.log($scope.transactionTypes);
	
	transactionSvc.getCategories($scope);
	console.log($scope.categories);
	
	$scope.resetAccounts = function() {
		$scope.selectedAccounts = [];
		$scope.startDate = moment().subtract(1, 'month').toDate();
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
		
		transactionSvc.getTransactions($scope, $scope.selectedAccounts, start, end);
		
		accountsSvc.getAllAccounts($scope);
	}
	
	$scope.refreshLists();
	
	// Show selected dates
	$scope.accReset = function() {
		console.log("accReset");
		$scope.resetAccounts();
		$scope.refreshLists();
	}
	
	// Show selected dates
	$scope.accUpdate = function() {
		console.log("accUpdate");
		$scope.refreshLists();
	}
	
	
	self.trClear = function() {
		console.log("trClear()");
		$scope.tr = {};
	}
	
	self.trSave = function() {
		console.log("trSave(" + $scope.tr.id + ")");
		
		// TODO validation
		var result = transactionSvc.putTransaction($scope.tr, $scope);
		console.log(result);
		
		$scope.tr = {};
	}
	
	self.trDelete = function(tr) {
		
		console.log("trDelete(" + tr.id + ")");
		
		transactionSvc.deleteTransaction(tr.id);
		$scope.tr = {};
	};
	
	$scope.trClick = function(tr) {
		console.log("trClick(" + tr.id + ")");
		console.log(tr);
		
		$scope.tr = tr;
	}
	
	self.trReBal = function(tr) {
		console.log("trReBal(" + tr.id + ", " + tr.accBalance + ")");
		accountsSvc.rebalance(tr.id);
		$scope.refreshLists();
	}
	
	self.trRecError = function(tr) {
		
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
