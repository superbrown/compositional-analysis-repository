
// needed for the date picker widget
$(function() {
    $( "#submissionDate" ).datepicker();
});

function hasExcelWorkbookFileSuffix(str, fileName) {
    return (endsWith(fileName, ".xls") || endsWith(fileName, ".xlsx") || endsWith(fileName, ".xlsm")) == true;
}

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

// A N G U L A R   s t u f f

var angular_module = angular.module('angular_module', ['ngMessages', 'ngResource', 'ngRoute']);

angular_module.config(function ($routeProvider) {

    $routeProvider
        .when('/uploadData',
        {
            templateUrl: 'pages/uploadData.html',
            controller: 'angular_uploadController'
        })
        .when('/findData',
        {
            templateUrl: 'pages/findData.html',
            controller: 'angular_findDataController'
        });
});

angular_module.controller('angular_uploadController',
    [
        '$scope', '$http', '$log', '$filter', '$resource', '$location',
        function($scope, $http, $log, $filter, $resource, $location) {

            $scope.dataCategory = '';
            $scope.dataCategoryNames = '';
            $scope.dataCategory_submissionDate = '';
            $scope.dataCategory_submitter = '';
            $scope.dataCategory_projectName = '';
            $scope.dataCategory_chargeNumber = '';
            $scope.dataCategory_comments = '';
            $scope.columnNames = '';

            $scope.uploadData = function() {

                $http.post('/api/addDataset',
                    {
                        dataCategory: $scope.dataCategory,
                        dataCategoryNames: $scope.dataCategoryNames,
                        dataCategory_submissionDate: $scope.dataCategory_submissionDate,
                        dataCategory_submitter: $scope.dataCategory_submitter,
                        dataCategory_projectName: $scope.dataCategory_projectName,
                        dataCategory_chargeNumber: $scope.dataCategory_chargeNumber,
                        dataCategory_comments: $scope.dataCategory_comments
                    }
                )
                    .success(
                    function (result) {
                        alert("Data has been fully ingested.");
                    }
                )
                    .error(
                    function(data, status) {
                        console.log(data);
                        alert("A failure occurred why attempting to ingest the data.");
                    }
                );
            }

            $scope.handleDataCategorySelection = function() {

                $http.get('/api/dataCategory/columnNames',
                    {
                        dataCategoryName: $scope.dataCategory
                    })
                    .success(function (result) {

                        $scope.columnNames = result;
                    })
                    .error(function(data, status) {

                    });
            }

            $http.get('/api/dataCategory/names/all')
                .success(function (result) {

                    $scope.dataCategoryNames = result;
                })
                .error(function(data, status) {

                });

            var req = {
                method: 'POST',
                url: '/api/rows',
                headers: {
                    'Content-Type': undefined
                },
                data:
                    [
                        {name: 'Some Column Name', value: 4, comparisonOperator: 'EQUALS'},
                        {name: 'Float Values Column Name', value: 4.55, comparisonOperator: 'EQUALS'},
                        {name: 'Additional new Column Name 2', value: 'b4', comparisonOperator: 'EQUALS'},
                        {name: '_submitter', value: 'Submitter 2', comparisonOperator: 'EQUALS'}
                    ]
            }

            $http(req)
                .success(function (result) {

                    $scope.dataCategories = result;
                })
                .error(function(data, status) {

                });


        }
    ]
);

angular_module.controller('angular_findDataController',
    [
        '$scope', '$http', '$log', '$filter', '$resource', '$location',
        function($scope, $http, $log, $filter, $resource, $location) {

            $scope.dataCategory = '';
            $scope.dataCategoryNames = '';
            $scope.dataCategory_submissionDate = '';
            $scope.dataCategory_submitter = '';
            $scope.dataCategory_projectName = '';
            $scope.dataCategory_chargeNumber = '';
            $scope.dataCategory_comments = '';
            $scope.columnNames = '';

            $scope.uploadData = function() {

                $http.post('/api/addDataset',
                    {
                        dataCategory: $scope.dataCategory,
                        dataCategoryNames: $scope.dataCategoryNames,
                        dataCategory_submissionDate: $scope.dataCategory_submissionDate,
                        dataCategory_submitter: $scope.dataCategory_submitter,
                        dataCategory_projectName: $scope.dataCategory_projectName,
                        dataCategory_chargeNumber: $scope.dataCategory_chargeNumber,
                        dataCategory_comments: $scope.dataCategory_comments
                    }
                )
                    .success(
                    function (result) {
                        alert("Data has been fully ingested.");
                    }
                )
                    .error(
                    function(data, status) {
                        console.log(data);
                        alert("A failure occurred why attempting to ingest the data.");
                    }
                );
            }

            $scope.handleDataCategorySelection = function() {

                $http.get('/api/dataCategory/columnNames',
                    {
                        dataCategoryName: $scope.dataCategory
                    })
                    .success(function (result) {

                        $scope.columnNames = result;
                    })
                    .error(function(data, status) {

                    });
            }

            $http.get('/api/dataCategory/names/all')
                .success(function (result) {

                    $scope.dataCategoryNames = result;
                })
                .error(function(data, status) {

                });

            var req = {
                method: 'POST',
                url: '/api/rows',
                headers: {
                    'Content-Type': undefined
                },
                data:
                    [
                        {name: 'Some Column Name', value: 4, comparisonOperator: 'EQUALS'},
                        {name: 'Float Values Column Name', value: 4.55, comparisonOperator: 'EQUALS'},
                        {name: 'Additional new Column Name 2', value: 'b4', comparisonOperator: 'EQUALS'},
                        {name: '_submitter', value: 'Submitter 2', comparisonOperator: 'EQUALS'}
                    ]
            }

            $http(req)
                .success(function (result) {

                    $scope.dataCategories = result;
                })
                .error(function(data, status) {

                });


        }
    ]
);


