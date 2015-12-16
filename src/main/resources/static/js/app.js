
// needed for the date picker widgets
$(function() {
    $( "#submissionDate" ).datepicker();
});

$(function() {
    $( "#value_asDate" ).datepicker();
});


function hasExcelWorkbookFileSuffix(fileName) {

    return (
        endsWith(fileName, ".xls") ||
        endsWith(fileName, ".xlsx") ||
        endsWith(fileName, ".xlsm")) == true;
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
        'ngCookies',
    ]);

drApp.config(
    function ($routeProvider) {

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

drApp.run(
    function ($rootScope) {

        $rootScope.showProgressAnimation = false;

        $rootScope.knownDataCategories = [];
        $rootScope.knownColumnNames = [];
        $rootScope.knownDataTypes = [];
        $rootScope.knownComparisonOperators = [];
        $rootScope.knownNamesOfSheetsWithinSelectedWorkbook = [];

        $rootScope.dataCategory = '';
        $rootScope.submissionDate = '';
        $rootScope.submitter = '';
        $rootScope.projectName = '';
        $rootScope.chargeNumber = '';
        $rootScope.comments = '';
        $rootScope.dataFile = '';
        $rootScope.nameOfSheetContainingData = '';
        $rootScope.attachments = '';

        $rootScope.searchCriteria = [];
        $rootScope.searchResults = [];

        $rootScope.menuItemClass_uploadData = '';
        $rootScope.menuItemClass_findData = '';
    });

drApp.controller('rootPageController',
    [
        '$scope', '$rootScope', '$http', '$log', '$filter', '$resource', '$location', '$cookies', 'drServices',
        function($scope, $rootScope, $http, $log, $filter, $resource, $location, $cookies, drServices) {

            $scope.navigate_uploadData = function () {
                $rootScope.menuItemClass_uploadData = 'active';
                $rootScope.menuItemClass_findData = '';
            }

            $scope.navigate_findData = function () {
                $rootScope.menuItemClass_uploadData = '';
                $rootScope.menuItemClass_findData = 'active';
            }

            $rootScope.$watch('$root.dataCategory', function() {
                var expireDate = new Date();
                expireDate.setDate(expireDate.getDate() + 365);
                $cookies.put('dataCategory', $rootScope.dataCategory, {'expires': expireDate});
                drServices.populateKnownColumnNames($scope, $http);
            });

            $rootScope.$watch('$root.dataFile', function() {
                drServices.populateNamesOfSheetsWithinExcelWorkbook($scope, $http);
            });

            drServices.populateKnownDataCategories($scope, $http);

            drServices.populateKnownDataTypes($scope, $http);

            var dataCategoryFromCookie = $cookies.get('dataCategory');
            if (dataCategoryFromCookie != undefined &&
                dataCategoryFromCookie != '') {
                $rootScope.dataCategory = dataCategoryFromCookie;
            }
        }
    ]
);

drApp.controller('uploadController',
    ['$scope', '$rootScope', '$http', '$log', '$filter', '$resource', '$location', '$parse', 'drServices',
        function($scope, $rootScope, $http, $log, $filter, $resource, $location, $parse, drServices)  {

            $rootScope.selectedPage = "Upload Data";

            $scope.uploadData = function ()  {
                drServices.uploadData($scope, $http);
            }

            $scope.handleDataFileSelection = function(event, dataFile) {
                $rootScope.dataFile = dataFile[0];
                drServices.populateNamesOfSheetsWithinExcelWorkbook($scope, $http);
            }

            $scope.handleAttachmentFilesSelection = function($event, attachments) {
                $rootScope.attachments = attachments;
            }
        }
    ]
);

// This came from:
// http://stackoverflow.com/questions/17922557/angularjs-how-to-check-for-changes-in-file-input-fields#answer-26591042
drApp.directive('fileChange', ['$parse', function($parse) {

    return {
        require: 'ngModel',
        restrict: 'A',
        link: function ($scope, element, attrs, ngModel) {

            // Get the function provided in the file-change attribute.
            // Note the attribute has become an angular expression,
            // which is what we are parsing. The provided handler is
            // wrapped up in an outer function (attrHandler) - we'll
            // call the provided event handler inside the handler()
            // function below.
            var attrHandler = $parse(attrs['fileChange']);

            // This is a wrapper handler which will be attached to the
            // HTML change event.
            var handler = function (e) {

                $scope.$apply(function () {

                    // Execute the provided handler in the directive's scope.
                    // The files variable will be available for consumption
                    // by the event handler.
                    attrHandler($scope, { $event: e, files: e.target.files });
                });
            };

            // Attach the handler to the HTML change event
            element[0].addEventListener('change', handler, false);
        }
    };
}]);

drApp.controller('findDataController',
    [
        '$scope', '$rootScope', '$http', '$log', '$filter', '$resource', '$location', 'drServices',
        function($scope, $rootScope, $http, $log, $filter, $resource, $location, drServices)
        {
            $rootScope.selectedPage = "Find Data";

            $scope.handleSearchSubmission = function()
            {
                drServices.findData($scope, $http);
            }

            $scope.createANewCriterion = function()
            {
                var newCriterion = {};
                newCriterion.columnName = '';
                newCriterion.dataTypeId = '';
                newCriterion.knownComparisonOperators = [];
                newCriterion.comparisonOperatorId = '';

                newCriterion.value_asString = '';
                newCriterion.value_asNumber = '';
                newCriterion.value_asDate = '';
                newCriterion.value_asBoolean = '';

                newCriterion.removeMe = function()
                {
                    var index = $rootScope.searchCriteria.indexOf(this);
                    $rootScope.searchCriteria.splice(index, 1);
                };

                var searchCriteria = $rootScope.searchCriteria;

                searchCriteria.push(newCriterion);

                var indexOfThisNewCriterion = (searchCriteria.length - 1);

                $rootScope.$watch('$root.searchCriteria[' + indexOfThisNewCriterion + '].dataTypeId', function()
                {
                    drServices.populateKnownComparisonOperators($scope, $http, newCriterion);
                });
            }

            if ($rootScope.searchCriteria.length === 0) {
                $rootScope.searchComplete = false;
                $scope.createANewCriterion();
            }
        }
    ]
);

drApp.service('drServices', function() {

    this.toSearchCriteriaAsJson = function(dataCategory, searchCriteria) {

        var criteriaPackagedForRestCall = [];

        // make sure we're limiting outselves to the data category in question

        criteriaPackagedForRestCall.push(
            {
                'name': ' Data Category',
                'dataType': 'STRING',
                'comparisonOperator': 'EQUALS',
                'value': dataCategory
            }
        );

        for (i = 0; i < searchCriteria.length; i++) {

            var criterion = searchCriteria[i];
            var value;

            var dataTypeId = criterion.dataTypeId;
            if (dataTypeId == 'STRING') {
                value = criterion.value_asString;
            }
            else if (dataTypeId == 'NUMBER') {
                value = criterion.value_asNumber;
            }
            else if (dataTypeId == 'DATE') {
                value = criterion.value_asDate.toJSON();
            }
            else if (dataTypeId == 'BOOLEAN') {
                value = criterion.value_asBoolean;
            }

            criteriaPackagedForRestCall.push(
                {
                    'name': criterion.columnName,
                    'dataType': criterion.dataTypeId,
                    'comparisonOperator': criterion.comparisonOperatorId,
                    'value': value
                }
            );
        }
        return criteriaPackagedForRestCall;
    }

    this.populateKnownDataCategories = function (scope, http) {

//        scope.$root.showProgressAnimation = true;

        http.get('/api/dataCategory/names/all')
            .success(function (result) {
//                scope.$root.showProgressAnimation = false;
                scope.$root.knownDataCategories = result;
            })
            .error(function (data, status) {
//                scope.$root.showProgressAnimation = false;
                console.log(status + ': ' + data);
            });
    }

    this.populateKnownColumnNames = function (scope, http) {

        if (scope.$root.dataCategory === '') return;

//        scope.$root.showProgressAnimation = true;

        http.get('/api/dataCategory/columnNames?dataCategoryName=' + scope.dataCategory)
            .success(function (result) {
//                scope.$root.showProgressAnimation = false;
                scope.$root.knownColumnNames = result;
            })
            .error(function (data, status) {
//                scope.$root.showProgressAnimation = false;
                console.log(status + ': ' + data);
            });
    }

    this.populateNamesOfSheetsWithinExcelWorkbook = function (scope, http) {

        scope.$root.knownNamesOfSheetsWithinSelectedWorkbook = [];

        var dataFile = scope.$root.dataFile;

        if (dataFile === '') {
            return;
        }

        if (hasExcelWorkbookFileSuffix(dataFile.name) == false) {
            // This is fine. They probably selected a CSV file.
            return;
        }

        var formData = new FormData();
        formData.append('dataFile', dataFile);

//        scope.$root.showProgressAnimation = true;

        http.post(
            '/api/getNamesOfSheetsWithinExcelWorkbook',
            formData,
            {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }
        )
            .success(function (result) {
//                scope.$root.showProgressAnimation = false;
                scope.$root.knownNamesOfSheetsWithinSelectedWorkbook = result;
            }
        )
            .error(function (data, status) {
//                scope.$root.showProgressAnimation = false;
                alert("A failure occurred (status: " + status + " data: " + data);
            }
        );
    }

    this.uploadData = function (scope, http) {

        var formData = new FormData();
        var $root = scope.$root;

        formData.append('dataCategory', $root.dataCategory);
        formData.append('submissionDate', $root.submissionDate.toJSON());
        formData.append('submitter', $root.submitter);
        formData.append('projectName', $root.projectName);
        formData.append('chargeNumber', $root.chargeNumber);
        formData.append('comments', $root.comments);
        formData.append('nameOfSheetContainingData', $root.nameOfSheetContainingData);
        formData.append('dataFile', $root.dataFile);
        formData.append('attachments', $root.attachments);

//        scope.$root.showProgressAnimation = true;

        http.post(
            '/api/addDataset',
            formData,
            {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }
        )
            .success(function (result) {
//                scope.$root.showProgressAnimation = false;
                alert("File successfully uploaded.")
            }
        )
            .error(function (data, status) {
//                scope.$root.showProgressAnimation = false;
                alert("A failure occurred (status: " + status + " data: " + data);
            }
        );
    }

    this.populateKnownDataTypes = function (scope, http) {

//        scope.$root.showProgressAnimation = true;

        http.get('api/dataTypes/all')
            .success(function (result) {
//                scope.$root.showProgressAnimation = false;
                scope.$root.knownDataTypes = result;
            })
            .error(function (data, status) {
//                scope.$root.showProgressAnimation = false;
                alert("A failure occurred (status: " + status + " data: " + data);
            });
    }

    this.populateKnownComparisonOperators = function (scope, http, criterion) {

        var dataTypeId = criterion.dataTypeId;

        if (dataTypeId === '' || dataTypeId === undefined) return;

//        scope.$root.showProgressAnimation = true;

        http.get('api/dataType/comparisonOperators?dataType=' + dataTypeId)
            .success(function (result) {
//                scope.$root.showProgressAnimation = false;
                criterion.knownComparisonOperators = result;
            })
            .error(function (data, status) {
//                scope.$root.showProgressAnimation = false;
                alert("A failure occurred (status: " + status + " data: " + data);
            });
    }

    this.findData = function (scope, http) {

        scope.$root.searchComplete = false;
        scope.$root.searchResults = [];

        var searchCriteria = scope.$root.searchCriteria;
        var dataCategory = scope.$root.dataCategory;

        var searchCriteriaAsJson = this.toSearchCriteriaAsJson(dataCategory, searchCriteria);

        scope.$root.searchCriteriaAsJson = searchCriteriaAsJson;

        var req = {
            method: 'POST',
            url: '/api/rows/flat',
            headers: {
                'Content-Type': undefined
            },
            data: searchCriteriaAsJson
        }

//        scope.$root.showProgressAnimation = true;

        http(req)
            .success(function (result) {
//                scope.$root.showProgressAnimation = false;
                scope.$root.searchComplete = true;
                scope.$root.searchResults = result;
            })
            .error(function (data, status) {
//                scope.$root.showProgressAnimation = false;
                scope.$root.searchComplete = true;
                alert("A failure occurred (status: " + status + " data: " + data);
            });
    }
})

