
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
        'ui.grid.autoResize',
        'ui.grid.moveColumns',
        'ui.date',
        'ngSanitize',
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
        '$scope', '$http', '$log', '$filter', '$resource', '$location', 'restService',
        function($scope, $http, $log, $filter, $resource, $location, restService) {

            $scope.$root.showProgressAnimation = false;
            $scope.$root.dataCategory = '';

            $scope.$root.showProgressAnimation = false;

            $scope.$root.knownDataCategories = '';
            $scope.$root.knownColumnNames = '';
            $scope.$root.knownDataTypes = '';
            $scope.$root.knownComparisonOperators = '';

            $scope.$root.knownDataCategories = '';

            $scope.$root.submissionDate = '';
            $scope.$root.submitter = '';
            $scope.$root.projectName = '';
            $scope.$root.chargeNumber = '';
            $scope.$root.comments = '';
            $scope.$root.columnNames = '';
            $scope.$root.nameOfSheetContainingData = '';

            $scope.$root.searchCriteria = [];

            $scope.$root.searchResults = [];
            $scope.$root.searchResultsGridConfig = {
                data: '$root.searchResults',
                enableSorting: true,
                enableColumnResizing: true,
                enableFiltering: false,
                enableGridMenu: false,
                showGridFooter: false,
                showColumnFooter: false,
                fastWatch: false,
            }

            $scope.$root.menuItmeClass_uploadData = '';
            $scope.$root.menuItmeClass_findData = '';

            $scope.navigate_uploadData = function () {
                $scope.$root.menuItmeClass_uploadData = 'active';
                $scope.$root.menuItmeClass_findData = '';
            }

            $scope.navigate_findData = function () {
                $scope.$root.menuItmeClass_uploadData = '';
                $scope.$root.menuItmeClass_findData = 'active';
            }
        }
    ]
);

drApp.controller('uploadController',
    [
        '$scope', '$http', '$log', '$filter', '$resource', '$location', 'restService',
        function($scope, $http, $log, $filter, $resource, $location, restService) {

            $scope.$root.selectedPage = "Upload Data";

            $scope.$root.showProgressAnimation = false;

            $scope.uploadData = function () {

                $scope.$root.showProgressAnimation = true;

                $http.post('/api/addDataset',
                    {
                        submissionDate: $scope.$root.submissionDate,
                        submitter: $scope.$root.submitter,
                        projectName: $scope.$root.projectName,
                        chargeNumber: $scope.$root.chargeNumber,
                        comments: $scope.$root.comments,
                        nameOfSheetContainingData: $scope.$root.nameOfSheetContainingData,
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
            restService.populateKnownDataCategories($http, $scope);
        }
    ]
);

drApp.controller('findDataController',
    [
        '$scope', '$http', '$log', '$filter', '$resource', '$location', 'restService',
        function($scope, $http, $log, $filter, $resource, $location, restService) {

            $scope.$root.selectedPage = "Find Data";

            $scope.$root.$watch('$root.dataCategory', function() {
                $scope.$root.showProgressAnimation = true;
                restService.populateKnownColumnNames($scope, $http);
                $scope.$root.showProgressAnimation = false;
            });

            $scope.handleSearchSubmission = function() {
                $scope.$root.showProgressAnimation = true;
                restService.findData($scope, $http);
                $scope.$root.showProgressAnimation = false;
            }

            $scope.createANewCriterion = function() {

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
                    var index = $scope.$root.searchCriteria.indexOf(this);
                    $scope.$root.searchCriteria.splice(index, 1);
                };

                $scope.$root.searchCriteria.push(criterion);

                var indexOfThisNewCriterion = ($scope.$root.searchCriteria.length - 1);

                $scope.$watch('$root.searchCriteria[' + indexOfThisNewCriterion + '].dataTypeId', function() {
                    $scope.$root.showProgressAnimation = true;
                    restService.poplulateKnownComparisonOperators($http, criterion);
                    $scope.$root.showProgressAnimation = false;
                });
            }

            // init
            if ($scope.$root.searchCriteria.length === 0) {
                $scope.$root.searchComplete = false;
                $scope.createANewCriterion();
            }
            restService.populateKnownDataCategories($http, $scope);
            restService.populateKnownDataTypes($http, $scope);
        }
    ]
);

drApp.service('restService', function() {

    this.populateKnownDataCategories = function (http, scope) {

        http.get('/api/dataCategory/names/all')
            .success(function (result) {
                scope.knownDataCategories = result;
            })
            .error(function (data, status) {
                console.log(status + ': ' + data);
            });
    }

    this.populateKnownColumnNames = function (scope, http) {

        if (scope.$root.dataCategory === '') return;

        http.get('/api/dataCategory/columnNames?dataCategoryName=' + scope.dataCategory)
            .success(function (result) {
                scope.knownColumnNames = result;
            })
            .error(function (data, status) {
                console.log(status + ': ' + data);
            });
    }

    this.populateKnownDataTypes = function (http, scope) {

        http.get('api/dataTypes/all')
            .success(function (result) {
                scope.knownDataTypes = result;
            })
            .error(function (data, status) {
                console.log(status + ': ' + data);
            });
    }

    this.poplulateKnownComparisonOperators = function (http, criterion) {

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

        scope.$root.searchComplete = false;
        scope.$root.searchResults = [];

        var criteriaPackagedForRestCall = [];

        // make sure we're limitting outselves to the data category in question
        criteriaPackagedForRestCall.push(
            {
                'name': ' Data Category',
                'dataType': 'STRING',
                'comparisonOperator': 'EQUALS',
                'value': scope.$root.dataCategory
            }
        );

        for (i = 0; i < scope.$root.searchCriteria.length; i++) {

            var criterion = scope.$root.searchCriteria[i];
            var value;

            var dataTypeId = criterion.dataTypeId;
            if (dataTypeId == 'STRING') {value = criterion.value_asString;}
            else if (dataTypeId == 'NUMBER') {value = criterion.value_asNumber;}
            else if (dataTypeId == 'DATE') {value = criterion.value_asDate;}
            else if (dataTypeId == 'BOOLEAN') { value = criterion.value_asBoolean;}

            criteriaPackagedForRestCall.push(
                {
                    'name': criterion.columnName,
                    'dataType': criterion.dataTypeId,
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

                scope.$root.searchComplete = true;
                scope.$root.searchResults = result;
            })
            .error(function (data, status) {
                scope.$root.searchComplete = true;
                alert("A failure occurred: " + data);
            });
    }

})

