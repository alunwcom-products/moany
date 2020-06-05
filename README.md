# README #

[![Build Status](https://travis-ci.com/alunwcom/moany-public.svg?branch=master)](https://travis-ci.com/alunwcom/moany-public)

----

## What's this? ##

'Moany' ... me moaning about money.

* What? A money tracking webapp, using imported statements, w/ nice views on past, present and future trends.
* Why? To track personal accounts, transactions, and do away with pesky spreadsheets.
* It's a pet project allowing me to try out different things, and hopefully get a basically functioning app.

----

### ISSUES/TODO ###

NEXT RELEASE: v7.0.0

+ Added CategoryBudget
+ Updated UI to allow setting category budget. (Many loose-ends - but working.)
+ Remove references to old getMonthlyBudget
- Update to category (budget) summary.
- review category/categorybudget validation 
- review category REST service, standard response to error
- Try out database migration

BACKLOG

- FEATURE: Complete (separate) Angular UI, and remove older AngularJS UI.
- TECH DEBT: Need to clarify responsibility of different layers (esp. web/service) for validation/parsing data etc.
  Also need consistent response format (ResponseEntity?) to allow error handling
- ENHANCEMENT: Budgeting: set start date as today's date
- ENHANCEMENT: Budgeting: order budget items
- ENHANCEMENT: Budgeting: refine month view transaction ordering - getting "Run out of sort keys..." warnings.
- ENHANCEMENT: Budgeting: option to 'suspend/disable' budget item (without having to delete it and then recreate later).
- FEATURE: Summary View: New view to give monthly account summaries - with net change and net balance.
- ENHANCEMENT: Standardize summary views - use YearMonth for periods
- FEATURE: What is the behaviour when an account is deleted? Are transactions deleted? Need to include budget items too!
- FEATURE: What needs to be done to allow multiple users/accounts??
- ENHANCEMENT: Add Spring unit tests.
- FEATURE: Dockerize moany for easier deployment.
- ENHANCEMENT/BUG: Need to make the UI update when data changes.
- ENHANCEMENT/BUG: Clear tr form after transaction deleted
- ENHANCEMENT: Remove stored statements, create test statements as test resource with unit tests.
- FEATURE: Improve user management (add/edit/remove users).
- standardize toString override format
- Standard error handling, HTML pages and REST API.
  NOTE: Have dropped the wrapping of REST JSON response - Angular adds a wrapper 
  at the client-side, and the only error responses should be HTTP status codes (?).
  Can use exceptions to handle these error responses, and only provide HTTP error page(s).
- Standard API for querying transactions.
- Tidy REST API?
- Fly-out for category report, showing transactions.
- UI re-working?
- Allow mass selection/change of categories + filtering transactions.

----

| table | test |
|---|---|
| people | person 1<br>person 2 |
