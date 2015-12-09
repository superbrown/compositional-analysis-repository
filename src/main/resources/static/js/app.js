
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

var drApp = angular.module('drApp',
    [
        'ngMessages',
        'ngResource',
        'ngRoute',
        'ui.grid',
        'ui.grid.resizeColumns',
        'ui.grid.autoResize'
    ]);

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

            $scope.$root.showProgressAnimation = false;
            $scope.$root.dataCategory = '';
        }
    ]
);

drApp.controller('uploadController',
    [
        '$scope', '$http', '$log', '$filter', '$resource', '$location', 'restService', 'globalValues',
        function($scope, $http, $log, $filter, $resource, $location, restService, globalValues) {

            $scope.$root.selectedPage = "Upload Data";

            $scope.$root.showProgressAnimation = false;

            $scope.knownDataCategories = '';

            $scope.submissionDate = '';
            $scope.submitter = '';
            $scope.projectName = '';
            $scope.chargeNumber = '';
            $scope.comments = '';
            $scope.columnNames = '';

            $scope.uploadData = function () {

                $scope.$root.showProgressAnimation = true;

                $http.post('/api/addDataset',
                    {
                        submissionDate: $scope.submissionDate,
                        submitter: $scope.submitter,
                        projectName: $scope.projectName,
                        chargeNumber: $scope.chargeNumber,
                        comments: $scope.comments
                    }
                )
                    .success(function (result) {
                        $scope.$root.showProgressAnimation = false;
                    }
                )
                    .error(function (data, status) {
                        $scope.$root.showProgressAnimation = false;
                        alert("A failure occurred: " + data);
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

            $scope.$root.selectedPage = "Find Data";

            $scope.$root.showProgressAnimation = false;

            $scope.knownDataCategories = '';
            $scope.knownColumnNames = '';
            $scope.knownDataTypes = '';
            $scope.knownComparisonOperators = '';

            $scope.criteria = [];

            $scope.searchResults = [];
            $scope.searchResultsGridConfig = {
                data: 'searchResults',
                enableSorting: true,
                enableColumnResizing: true,
                enableFiltering: false,
                enableGridMenu: false,
                showGridFooter: false,
                showColumnFooter: false,
                fastWatch: false,
            }

            $scope.searchComplete = false;

            $scope.$root.$watch('$root.dataCategory', function() {
                $scope.$root.showProgressAnimation = true;
                restService.getKnownColumnNames($scope, $http);
                $scope.$root.showProgressAnimation = false;
            });

            $scope.handleSearchSubmission = function() {
                $scope.$root.showProgressAnimation = true;
                restService.findData($scope, $http);
                $scope.$root.showProgressAnimation = false;
            }

            $scope.addCriterion = function() {

                var criterion = {};
                criterion.columnName = '';
                criterion.dataTypeId = '';
                criterion.knownComparisonOperators == [];
                criterion.comparisonOperatorId = '';

                criterion.value_asString = '';
                criterion.value_asNumber = '';
                criterion.value_asDate = '';
                criterion.value_asBoolean = '';

                criterion.removeMe = function() {
                    var index = $scope.criteria.indexOf(criterion);
                    $scope.criteria = $scope.criteria.splice(index, 1);
                };

                $scope.criteria.push(criterion);

                var indexOfThisNewCriterion = ($scope.criteria.length - 1);

                $scope.$watch('criteria[' + indexOfThisNewCriterion + '].dataTypeId', function() {
                    $scope.$root.showProgressAnimation = true;
                    restService.getKnownComparisonOperators($http, criterion);
                    $scope.$root.showProgressAnimation = false;
                });
            }

            // init
            $scope.addCriterion();
            restService.getKnownDataCategories($http, $scope);
            restService.getKnownDataTypes($http, $scope);
        }
    ]
);

drApp.service('globalValues', function() {

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

        if (scope.$root.dataCategory === '') return;

        http.get('/api/dataCategory/columnNames?dataCategoryName=' + scope.dataCategory)
            .success(function (result) {
                scope.knownColumnNames = result;
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

    this.getKnownComparisonOperators = function (http, criterion) {

        dataTypeId = criterion.dataTypeId;

        if (dataTypeId === '' || dataTypeId === undefined) return;

        http.get('api/dataType/comparisonOperators?dataType=' + dataTypeId)
            .success(function (result) {
                criterion.knownComparisonOperators = result;
            })
            .error(function (data, status) {
                console.log(status + ': ' + data);
            });
    }

    this.findData = function (scope, http) {

        scope.searchComplete = false;
        scope.searchResults = [];

        var criteriaPackagedForRestCall = [];

        // make sure we're limitting outselves to the data category in question
        criteriaPackagedForRestCall.push(
            {
                'name': ' Data Category',
                'comparisonOperator': 'EQUALS',
                'value': scope.$root.dataCategory
            }
        );

        for (i = 0; i < scope.criteria.length; i++) {

            var criterion = scope.criteria[i];
            var value;

            var dataTypeId = criterion.dataTypeId;
            if (dataTypeId == 'STRING') {value = criterion.value_asString;}
            else if (dataTypeId == 'NUMBER') {value = criterion.value_asNumber;}
            else if (dataTypeId == 'DATE') {value = criterion.value_asDate;}
            else if (dataTypeId == 'BOOLEAN') {value = criterion.value_asBoolean;}

            criteriaPackagedForRestCall.push(
                {
                    'name': criterion.columnName,
                    'comparisonOperator': criterion.comparisonOperatorId,
                    'value': value
                }
            );
        }

        var req = {
            method: 'POST',
            url: '/api/rows/flat',
            headers: {
                'Content-Type': undefined
            },
            data: criteriaPackagedForRestCall
        }

        http(req)
            .success(function (result) {
                scope.searchComplete = true;
                scope.searchResults = result;
            })
            .error(function (data, status) {
                scope.searchComplete = true;
                alert("A failure occurred: " + data);
            });
    }

})

