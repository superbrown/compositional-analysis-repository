
// needed for the date picker widgets
$(function() {
    $( "#submissionDate" ).datepicker();
});

$(function() {
    $( "#value_asDate" ).datepicker();
});


function hasExcelWorkbookFileSuffix(str, fileName) {
    return (endsWith(fileName, ".xls") || endsWith(fileName, ".xlsx") || endsWith(fileName, ".xlsm")) == true;
}

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

// A N G U L A R   s t u f f

var drApp = angular.module('drApp', ['ngMessages', 'ngResource', 'ngRoute']);

drApp.config(function ($routeProvider) {

    $routeProvider
        .when('/uploadData',
        {
            templateUrl: 'pages/uploadData.html',
            controller: 'uploadController'
        })
        .when('/findData',
        {
            templateUrl: 'pages/findData.html',
            controller: 'findDataController'
        });
});

drApp.controller('menuController',
    [
        '$scope', '$http', '$log', '$filter', '$resource', '$location', 'restService', 'globalValues',
        function($scope, $http, $log, $filter, $resource, $location, restService, globalValues) {

            $scope.dataCategory = globalValues.dataCategory;
        }
    ]
);

drApp.controller('uploadController',
    [
        '$scope', '$http', '$log', '$filter', '$resource', '$location', 'restService', 'globalValues',
        function($scope, $http, $log, $filter, $resource, $location, restService, globalValues) {

            $scope.knownDataCategories = '';

            $scope.dataCategory = globalValues.dataCategory;
            $scope.submissionDate = '';
            $scope.submitter = '';
            $scope.projectName = '';
            $scope.chargeNumber = '';
            $scope.comments = '';
            $scope.columnNames = '';

            $scope.$watch('dataCategory', function() {
                globalValues.dataCategory = $scope.dataCategory;
                restService.getKnownColumnNames($scope, $http);
            });

            $scope.uploadData = function () {

                $http.post('/api/addDataset',
                    {
                        dataCategory: $scope.dataCategory,
                        submissionDate: $scope.submissionDate,
                        submitter: $scope.submitter,
                        projectName: $scope.projectName,
                        chargeNumber: $scope.chargeNumber,
                        comments: $scope.comments
                    }
                )
                    .success(
                    function (result) {
                        alert("Data has been fully ingested.");
                    }
                )
                    .error(
                    function (data, status) {
                        console.log(data);
                        alert("A failure occurred why attempting to ingest the data.");
                    }
                );
            }

            // init
            restService.getKnownDataCategories($http, $scope);
        }
    ]
);

drApp.controller('findDataController',
    [
        '$scope', '$http', '$log', '$filter', '$resource', '$location', 'restService', 'globalValues',
        function($scope, $http, $log, $filter, $resource, $location, restService, globalValues) {

            $scope.knownDataCategories = '';
            $scope.knownDataCategoryColumnNames = '';
            $scope.knownDataTypes = '';
            $scope.knownComparisonOperators = '';

            $scope.dataCategory = globalValues.dataCategory;

            $scope.columnName = '';
            $scope.dataTypeId = '';
            $scope.comparisonOperatorId = '';

            $scope.value_asString = '';
            $scope.value_asNumber = '';
            $scope.value_asDate = '';
            $scope.value_asBoolean = '';

            $scope.searchResults = '';

            $scope.$watch('dataCategory', function() {
                globalValues.dataCategory = $scope.dataCategory;
                restService.getKnownColumnNames($scope, $http);
            });

            $scope.$watch('dataTypeId', function() {
                restService.getKnownComparisonOperators($scope, $http);
            });

            $scope.handleSearchSubmission = function() {
                restService.findData($scope, $http);
            }

            // init
            restService.getKnownDataCategories($http, $scope);
            restService.getKnownDataTypes($http, $scope);
        }
    ]
);

drApp.service('globalValues', function() {

    this.dataCategory = '';
});

drApp.service('restService', function() {

    this.getKnownDataCategories = function (http, scope) {

        http.get('/api/dataCategory/names/all')
            .success(function (result) {
                scope.knownDataCategories = result;
            })
            .error(function (data, status) {
                console.log(status + ': ' + data);
            });
    }

    this.getKnownColumnNames = function (scope, http) {

        if (scope.dataCategory === '') return;

        http.get('/api/dataCategory/columnNames?dataCategoryName=' + scope.dataCategory)
            .success(function (result) {
                scope.knownDataCategoryColumnNames = result;
            })
            .error(function (data, status) {
                console.log(status + ': ' + data);
            });
    }

    this.getKnownDataTypes = function (http, scope) {

        http.get('api/dataTypes/all')
            .success(function (result) {
                scope.knownDataTypes = result;
            })
            .error(function (data, status) {
                console.log(status + ': ' + data);
            });
    }

    this.getKnownComparisonOperators = function (scope, http) {

        var dataTypeId = scope.dataTypeId;
        if (dataTypeId === '') return;

        http.get('api/dataType/comparisonOperators?dataType=' + dataTypeId)
            .success(function (result) {
                scope.knownComparisonOperators = result;
            })
            .error(function (data, status) {
                console.log(status + ': ' + data);
            });
    }

    this.findData = function ($scope, $http) {
        var req = {
            method: 'POST',
            url: '/api/rows',
            headers: {
                'Content-Type': undefined
            },
            data: [
                {
                    'name': $scope.columnName,
                    'comparisonOperator': $scope.comparisonOperatorId,
                    'value': $scope.value_asString
                }
            ]
        }

        $http(req)
            .success(function (result) {

                $scope.searchResults = result;
            })
            .error(function (data, status) {

            });
    }

})

