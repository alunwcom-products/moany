<#import "/spring.ftl" as spring/>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  
  <title>Index</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="">
  <meta name="author" content="">
  
  <link href="https://fonts.googleapis.com/css?family=Droid+Sans" rel="stylesheet">
  <link href="${rc.getContextPath()!}/css/styles.css" rel="stylesheet" media="all">
  
  <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/angular_material/1.1.0/angular-material.min.css">
  
  <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.5/angular.min.js"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.5/angular-route.min.js"></script>
  
  <!-- Angular Material requires Angular.js Libraries -->
  <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.5/angular-animate.min.js"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.5/angular-aria.min.js"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.5/angular-messages.min.js"></script>
  <!-- Angular Material Library -->
  <script src="https://ajax.googleapis.com/ajax/libs/angular_material/1.1.0/angular-material.min.js"></script>
  
  <script src="${rc.getContextPath()!}/js/moment.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-moment/1.0.1/angular-moment.js"></script>
  
  <script>
    <#if rc??>
      var ctxpath = "${rc.getContextPath()!}"
    <#else>
      var ctxpath = ""
    </#if>
    
    <#if _csrf??>
      var csrfheader = "${_csrf.headerName!''}"
      var csrftoken = "${_csrf.token!''}"
      var csrfparameter = "${_csrf.parameterName!''}"
    <#else>
      var csrfheader = "_csrf"
      var csrftoken = ""
      var csrfparameter = "_csrf"
    </#if>
    
    
  </script>
  
</head>

<body>
  <div>
  
  <a href="/">Home</a>
  <div ng-app="routeApp">
    <p>
      <a href="/#!/summary">Summary</a> | 
      <a href="/#!/accounts">Accounts</a> | 
      <a href="/#!/transactions">Transactions</a> |
      <a href="/#!/statements">Statements</a> | 
      <a href="/#!/categories">Categories</a> | 
      <a href="/#!/report1">Category Report [v1]</a> |
      <a href="/#!/report2">Category Report [v2]</a>
    </p>
    
    <div ng-view></div>
  </div>
  
  <script src="/js/app.js"></script>
  <script src="/js/services.js"></script>
  <script src="/js/categories.ctl.js"></script>
  <script src="/js/summary.ctl.js"></script>
  <script src="/js/accounts.ctl.js"></script>
  <script src="/js/transactions.ctl.js"></script>
  <script src="/js/monthview.ctl.js"></script>
  <script src="/js/budgeting.ctl.js"></script>
  <script src="/js/statements.ctl.js"></script>
  <script src="/js/report1.ctl.js"></script>
  <script src="/js/report2.ctl.js"></script>
</body>
</html>
