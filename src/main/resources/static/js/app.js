var app = angular.module('routeApp', [
  'ngRoute','ngMaterial','ngMessages'
]);

app.config(function ($routeProvider) {
  $routeProvider
    .when('/summary', {
      templateUrl: '/templates/summary.tpl.html',
      controller: 'summaryCtrl',
      controllerAs: 'ctrl'
    })
    .when('/accounts', {
      templateUrl: '/templates/accounts.tpl.html',
      controller: 'accountsCtrl',
      controllerAs: 'ctrl'
    })
    .when('/transactions', {
      templateUrl: '/templates/transactions.tpl.html',
      controller: 'transactionCtrl',
      controllerAs: 'ctrl'
    })
    .when('/budgeting', {
      templateUrl: '/templates/budgeting.tpl.html',
      controller: 'budgetingCtrl',
      controllerAs: 'ctrl'
    })
    .when('/monthview', {
      templateUrl: '/templates/monthview.tpl.html',
      controller: 'monthviewCtrl',
      controllerAs: 'ctrl'
    })
    .when('/statements', {
      templateUrl: '/templates/statements.tpl.html',
      controller: 'statementCtrl',
      controllerAs: 'ctrl'
    })
    .when('/categories', {
      templateUrl: '/templates/categories.tpl.html',
      controller: 'categoryCtrl',
      controllerAs: 'ctrl'
    })
    .when('/report1', {
      templateUrl: '/templates/report1.tpl.html',
      controller: 'reportCtrl1',
      controllerAs: 'ctrl'
    })
    .when('/report2', {
      templateUrl: '/templates/report2.tpl.html',
      controller: 'reportCtrl2',
      controllerAs: 'ctrl'
    })
    .otherwise({
      redirectTo: '/summary'
    });
});
