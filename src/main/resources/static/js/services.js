'use strict';

app.service('accountsSvc', ['$log', '$http', function ($log, $http) {
	
	console.log("loading 'accountsSvc'");
	
	var self = this;
	
	// getAllAccounts
	self.getAllAccounts = function(thescope) {
		console.log("getAllAccounts()");
		var uri = ctxpath + "/rest/accounts/v2/";
		return $http.get(uri)
		.then(
			// success
			function(response) {
				console.log("Success... [getAllAccounts]");
				console.log(response);
				thescope.accounts = response.data;
			},
			// error
			function(response) {
				console.log("Error... [getAllAccounts]");
				console.log(response);
			}
		);
	}
	
	// putAccount
	self.putAccount = function(data, thescope) {
		console.log("putAccount()");
		var uri = ctxpath + "/rest/accounts/v2/";
		var result = {};
		
		return $http.put(uri, data)
		.then(
			function(response) {
				console.log("Success... [putAccount]");
				console.log(response);
				
				return self.getAllAccounts(thescope);
			},
			function(response) {
				console.log("Error... [putAccount]");
				console.log(response);
			}
		);
	}
	
	// deleteAccount
	self.deleteAccount = function(id, thescope) {
		console.log("deleteAccount()");
		var uri = ctxpath + "/rest/accounts/v2/id/" + id;
		var result = {};
		
		return $http.delete(uri).then(
			function(response) {
				console.log("Success... [deleteAccount]");
				console.log(response);
				
				return self.getAllAccounts(thescope);
			},
			function(response) {
				console.log("Error... [deleteAccount]");
				console.log(response);
			}
		);
	}
	
	// re-balance account from a transaction
	self.rebalance = function(id) {
		console.log("acc. rebalance()");
		var uri = ctxpath + "/rest/accounts/v2/rebalance/" + id;
		return $http.get(uri).then(
			function(response) {
				console.log("Success... [rebalance]");
				console.log(response);
		},
			function(response) {
				console.log("Error... [rebalance]");
				console.log(response);
			}
		);
	}
	
	// getAccountTypes
	self.getAccountTypes = function(thescope) {
		$log.debug("getAccountTypes()");
		var uri = ctxpath + "/rest/accounts/v2/types";
		return $http.get(uri).then(
			function(response) {
				$log.debug("Success... [getAccountTypes]");
				$log.debug(response);
				thescope.accountTypes = response.data;
			},
			function(response) {
				$log.error("Error... [getAccountTypes]");
				$log.debug(response);
			}
		);
	}
	
}]);

app.service('transactionSvc', ['$log', '$http', function ($log, $http) {
	
	$log.info("transactionSvc loading");
	
	var self = this;
	
	// getTransactions
	self.getTransactions = function(thescope, selectedAccounts, startDate, endDate) {
		$log.debug("getTransactions()");
		var uri = ctxpath + "/rest/transactions/v2/?acc=" + selectedAccounts + "&startDate=" + startDate + "&endDate=" + endDate;
		return $http.get(uri).then(
			function(response) {
				console.log("Success... [getTransactions]");
				console.log(response);
				thescope.transactions = response.data;
			},
			function(response) {
				console.log("Error... [getTransactions]");
				console.log(response);
			}
		);
	}
	
	// getTransactions
	self.getTransactionsByMonth = function(thescope, selectedAccounts, month) {
		$log.debug("getTransactionsByMonth()");
		var uri = ctxpath + "/rest/transactions/v2/month/" + month + "?acc=" + selectedAccounts;
		return $http.get(uri).then(
			function(response) {
				console.log("Success... [getTransactionsByMonth]");
				console.log(response);
				
				thescope.transactions = response.data.transactions;
				thescope.startingBalance = response.data.startingBalance;
				
				console.log(thescope.startingBalance);
				console.log(thescope.transactions);
				
			},
			function(response) {
				console.log("Error... [getTransactionsByMonth]");
				console.log(response);
			}
		);
	}
	
	// putTransaction
	self.putTransaction = function(data, thescope) {
		console.log("putTransaction()");
		var uri = ctxpath + "/rest/transactions/v2/";
		
		return $http.put(uri, data)
		.then(
			function(response) {
				console.log("Success... [putTransaction]");
				console.log(response);
			},
			function(response) {
				console.log("Error... [putTransaction]");
				console.log(response);
			}
		);
	}
	
	// deleteTransaction
	self.deleteTransaction = function(id) {
		console.log("deleteTransaction()");
		var uri = ctxpath + "/rest/transactions/v2/id/" + id;
		
		return $http.delete(uri)
		.then(
			function(response) {
				console.log("Success... [deleteTransaction]");
				console.log(response);
			},
			function(response) {
				console.log("Error... [deleteTransaction]");
				console.log(response);
			}
		);
	}
	
	// getCategories TODO separate service!
	self.getCategories = function(thescope) {
		console.log("getCategories()");
		var uri = ctxpath + "/rest/categories/v2/";
		return $http.get(uri).then(
			function(response) {
				console.log("Success... [getCategories]");
				console.log(response);
				thescope.categories = response.data;
			},
			function(response) {
				console.log("Error... [getCategories]");
				console.log(response);
			}
		);
	}
	
	// getTransactionTypes
	self.getTransactionTypes = function(thescope) {
		$log.debug("getTransactionTypes()");
		var uri = ctxpath + "/rest/transactions/v2/types";
		return $http.get(uri).then(
			function(response) {
				console.log("Success... [getTransactionTypes]");
				console.log(response);
				thescope.transactionTypes = response.data;
			},
			function(response) {
				console.log("Error... [getTransactionTypes]");
				console.log(response);
			}
		);
	}
	
	self.rebalance = function() {
		console.log("rebalance()");
		var uri = ctxpath + "/rest/transactions/v2/rebalance/";
		return $http.get(uri).then(
			function(response) {
				console.log("Success... [rebalance]");
				console.log(response);
		},
			function(response) {
				console.log("Error... [rebalance]");
				console.log(response);
			}
		);
	}
	
}]);

app.service('categorySvc', ['$log', '$http', function ($log, $http) {
	
	$log.info("categorySvc loading...");
	
	var self = this;
	
	self.getAllCategories = function(thescope) {
		console.log("getAllCategories()");
		var uri = ctxpath + "/rest/categories/v2/";
		return $http.get(uri).then(
			function(response) {
				console.log("Success... [getAllCategories]");
				console.log(response);
				thescope.categories = response.data;
			},
			function(response) {
				console.log("Error... [getAllCategories]");
				console.log(response);
			}
		);
	}
	
	self.getCategorySummary = function(thescope, selectedAccounts, startMonth) {
		$log.debug("getCategorySummary()");
		var uri = ctxpath + "/rest/categories/v2/summary/?acc=" + selectedAccounts + "&month=" + startMonth;
		
		return $http.get(uri).then(
			function(response) {
				$log.debug("Success... [getCategorySummary]");
				thescope.categorySummary = response.data;
				thescope.headings = response.data[0].surplusBalance;
				$log.debug(response);
				$log.debug(response.data[0].surplusBalance);
			},
			function(response) {
				$log.debug("Error... [getCategorySummary]");
				$log.debug(response);
			}
		);
	}
	
	self.putCategory = function(data, thescope) {
		console.log("putCategory()");
		var uri = ctxpath + "/rest/categories/v2/";
		var result = {};
		
		return $http.put(uri, data)
		.then(
			function(response) {
				console.log("Success... [putCategory]");
				console.log(response);
				
				return self.getAllCategories(thescope);
			},
			function(response) {
				console.log("Error... [putCategory]");
				console.log(response);
			}
		);
	}
	
	self.deleteCategoryById = function(id, thescope) {
		console.log("deleteCategoryById()");
		var uri = ctxpath + "/rest/categories/v2/id/" + id;
		var result = {};
		
		return $http.delete(uri).then(
			function(response) {
				console.log("Success... [deleteCategoryById]");
				console.log(response);
				
				return self.getAllCategories(thescope);
			},
			function(response) {
				console.log("Error... [deleteCategoryById]");
				console.log(response);
			}
		);
	}
	
	self.addCategoryBudget = function(id, data, thescope) {
		console.log("addCategoryBudget()");
			var uri = ctxpath + "/rest/categories/v2/categoryBudget/" + id;
			var result = {};
			
			return $http.put(uri, data)
			.then(
				function(response) {
					console.log("Success... [addCategoryBudget]");
					console.log(response);
					
					return self.getAllCategories(thescope);
				},
				function(response) {
					console.log("Error... [addCategoryBudget]");
					console.log(response);
				}
			);
		}
	
	self.removeCategoryBudget = function(id, thescope) {
		console.log("removeCategoryBudget()");
		var uri = ctxpath + "/rest/categories/v2/categoryBudget/" + id;
		var result = {};
		
		return $http.delete(uri).then(
			function(response) {
				console.log("Success... [removeCategoryBudget]");
				console.log(response);
				
				return self.getAllCategories(thescope);
			},
			function(response) {
				console.log("Error... [removeCategoryBudget]");
				console.log(response);
			}
		);
	}
	
}]);

app.service('statementSvc', ['$log', '$http', function ($log, $http) {
	
	/* postStatement */
	this.postStatement = function(thescope) {
		
		console.log("postStatement()");
		
		//var uri = ctxpath + "/rest/statements/v2/upload?" + $window.csrfparameter + "=" + $window.csrftoken;
		var uri = ctxpath + "/rest/statements/v2/upload?";
		console.log("uri: " + uri);
		
		// TODO validate input (file data)
		
		
		var fd = new FormData();
		fd.append('importer', thescope.stmt.importer);
		fd.append('autoacc', thescope.stmt.autoacc);
		fd.append('nodup', thescope.stmt.nodup);
		fd.append('file', thescope.file);
		$http.post(uri, fd, {
			transformRequest: angular.identity,
			headers: {'Content-Type': undefined}
		})
		.then(
			function(response){
				console.log("Success... [postStatement]");
				console.log(response);
				
				thescope.transactions = response.data.result;
				thescope.message = response.data.message;
				
			},
			function(response){
				console.log("Error... [postStatement]");
				console.log(response);
				
				thescope.transactions = {};
				thescope.message = response.data.message;
			}
		);
		
	}
	
	this.getImporters = function(thescope) {
		$log.debug("getImporters()");
		var uri = ctxpath + "/rest/statements/v2/importers";
		
		return $http.get(uri).then(
			function(response) {
				$log.debug("Success... [getImporters]");
				thescope.importers = response.data;
				$log.debug(response);
			},
			function(response) {
				$log.debug("Error... [getImporters]");
				$log.debug(response);
			}
		);
	}
	
}]);
