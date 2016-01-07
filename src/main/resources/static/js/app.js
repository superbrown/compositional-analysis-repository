
// U t i l i t i e s

function hasExcelWorkbookFileSuffix(fileName) {

    return (
        endsWith(fileName, ".xls") ||
        endsWith(fileName, ".xlsx") ||
        endsWith(fileName, ".xlsm")) == true;
}

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function isUnset(object) {
    return (object === undefined || object === '');
}

function createDateThatIsOneYearFromNow() {
    var expireDate = new Date();
    expireDate.setDate(expireDate.getDate() + 365);
    return expireDate;
}


// A N G U L A R   s t u f f

var drApp = angular.module('drApp',
    [
        'ngMessages',
        'ngResource',
        'ngRoute',
        'ui.date',
        'ngSanitize', // needed to allow links to be rendered that have been received from a REST service
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
            })
            .when('/',
            {
                redirectTo: '/uploadData'
            })
            .otherwise(
            {
                redirectTo: '/uploadData'
            })
    });

drApp.run(
    function ($rootScope) {

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
        $rootScope.sourceDocument = '';
        $rootScope.nameOfSubdocumentContainingDataIfApplicable = '';
        $rootScope.attachments = [];

        $rootScope.alertMessage_success = '';
        $rootScope.alertMessage_missingUserInput = '';
        $rootScope.alertMessage_failure = '';

        $rootScope.searchCriteria = [];
        $rootScope.searchResults = [];

        $rootScope.menuItemClass_uploadData = '';
        $rootScope.menuItemClass_findData = '';

        $rootScope.numberOfBlockingProcesses = 0;
    });

    drApp.controller('rootPageController',
    [
        '$scope', '$rootScope', '$http', '$log', '$filter', '$resource', '$location', '$cookies', 'drServices',
        function($scope, $rootScope, $http, $log, $filter, $resource, $location, $cookies, drServices) {

            $scope.handleUploadDataMenuItemClick = function () {
                $rootScope.menuItemClass_uploadData = '';
                $rootScope.menuItemClass_findData = '';
                $rootScope.alertMessage_success = '';
                $rootScope.alertMessage_failure = '';
                $rootScope.alertMessage_missingUserInput = '';
            }

            $scope.handleFindDataMenuItemClick = function () {
                $rootScope.menuItemClass_uploadData = '';
                $rootScope.menuItemClass_findData = '';
                $rootScope.alertMessage_success = '';
                $rootScope.alertMessage_failure = '';
                $rootScope.alertMessage_missingUserInput = '';
            }

            $rootScope.$watch('$root.dataCategory', function() {
                var cookieExpirationDate = createDateThatIsOneYearFromNow();
                $cookies.put('dataCategory', $rootScope.dataCategory, {'expires': cookieExpirationDate});
                $rootScope.searchResults = [];
                $rootScope.searchComplete = false;
                drServices.populateKnownColumnNames($scope, $http);
            });

            $rootScope.$watch('$root.submitter', function() {
                var cookieExpirationDate = createDateThatIsOneYearFromNow();
                $cookies.put('submitter', $rootScope.submitter, {'expires': cookieExpirationDate});
            });

            $rootScope.$watch('$root.sourceDocument', function() {
                drServices.populateNamesOfSheetsWithinExcelWorkbook($scope, $http);
            });

            drServices.populateKnownDataCategories($scope, $http);

            drServices.populateKnownDataTypes($scope, $http);

            var dataCategoryFromCookie = $cookies.get('dataCategory');
            if (dataCategoryFromCookie != undefined &&
                dataCategoryFromCookie != '') {
                $rootScope.dataCategory = dataCategoryFromCookie;
            }

            var submitterFromCookie = $cookies.get('submitter');
            if (submitterFromCookie != undefined &&
                submitterFromCookie != '') {
                $rootScope.submitter = submitterFromCookie;
            }
        }
    ]
);

drApp.controller('uploadController',
    ['$scope', '$rootScope', '$http', '$log', '$filter', '$resource', '$location', '$parse', 'drServices',
        function($scope, $rootScope, $http, $log, $filter, $resource, $location, $parse, drServices)  {

            $rootScope.selectedPage = "Upload Data";
            $rootScope.menuItemClass_uploadData = 'active';

            $scope.uploadData = function ()  {
                $rootScope.alertMessage_missingUserInput = '';
                drServices.uploadData($scope, $http);
            }

            $scope.handleDataFileSelection = function(event, sourceDocument) {
                $rootScope.sourceDocument = sourceDocument[0];
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
            $rootScope.menuItemClass_findData = 'active';

            $scope.handleSearchSubmission = function()
            {
                $rootScope.alertMessage_missingUserInput = '';
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

    var self = this;

    self.toSearchCriteriaAsJson = function(dataCategory, searchCriteria) {

        var criteriaPackagedForRestCall = [];

        // make sure we're limiting ourselves to the data category in question

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

            if (isUnset(criterion.columnName)) continue;
            if (isUnset(criterion.dataTypeId)) continue;
            if (isUnset(criterion.comparisonOperatorId)) continue;

            var value;

            var dataTypeId = criterion.dataTypeId;
            if (dataTypeId == 'STRING') {
                if (isUnset(criterion.value_asString)) continue;
                value = criterion.value_asString;
            }
            else if (dataTypeId == 'NUMBER') {
                if (isUnset(criterion.value_asNumber)) continue;
                value = criterion.value_asNumber;
            }
            else if (dataTypeId == 'DATE') {
                if (isUnset(criterion.value_asDate)) continue;
                value = criterion.value_asDate.toJSON();
            }
            else if (dataTypeId == 'BOOLEAN') {
                if (isUnset(criterion.value_asBoolean)) continue;
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

    self.populateKnownDataCategories = function (scope, http) {

        scope.$root.numberOfBlockingProcesses++;

        http.get('/data-repository-app/api/dataCategory/names/all')
            .success(function (result) {
                scope.$root.numberOfBlockingProcesses--;
                scope.$root.knownDataCategories = result;
            })
            .error(function (data, status) {
                scope.$root.numberOfBlockingProcesses--;
                postError(scope.$root, data);

            });
    }

    self.populateKnownColumnNames = function (scope, http) {

        if (scope.$root.dataCategory === '') return;

        scope.$root.knownColumnNames = [];

        scope.$root.numberOfBlockingProcesses++;

        http.get('/data-repository-app/api/dataCategory/searchableColumnNames?dataCategoryName=' + scope.$root.dataCategory)
            .success(function (result) {
                scope.$root.numberOfBlockingProcesses--;
                scope.$root.knownColumnNames = result;
            })
            .error(function (data, status) {
                scope.$root.numberOfBlockingProcesses--;
                postError(scope.$root, data);
            });
    }

    self.populateNamesOfSheetsWithinExcelWorkbook = function (scope, http) {

        scope.$root.knownNamesOfSheetsWithinSelectedWorkbook = [];

        var sourceDocument = scope.$root.sourceDocument;

        if (isUnset(sourceDocument)) {
            return;
        }

        if (hasExcelWorkbookFileSuffix(sourceDocument.name) == false) {
            // This is fine. They probably selected a CSV file.
            return;
        }

        var formData = new FormData();
        formData.append('sourceDocument', sourceDocument);

        scope.$root.numberOfBlockingProcesses++;

        http.post(
            '/data-repository-app/api/getNamesOfSheetsWithinExcelWorkbook',
            formData,
            {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }
        )
            .success(function (result) {
                scope.$root.numberOfBlockingProcesses--;
                scope.$root.knownNamesOfSheetsWithinSelectedWorkbook = result;
            }
        )
            .error(function (data, status) {
                scope.$root.numberOfBlockingProcesses--;
                postError(scope.$root, data);
            }
        );
    }

    self.uploadData = function (scope, http) {

        // Explanation of this approach:
        // https://uncorkedstudios.com/blog/multipartformdata-file-upload-with-angularjs
        // http://shazwazza.com/post/uploading-files-and-json-data-in-the-same-request-with-angular-js/

        var formData = new FormData();
        var $root = scope.$root;

        scope.$root.alertMessage_missingUserInput = '';

        // validation
        if (isUnset($root.dataCategory)) {
            $root.alertMessage_missingUserInput = 'Please select a Data Category.'; return; }
        if (isUnset($root.submissionDate)) {
            $root.alertMessage_missingUserInput = 'Please enter a Submission Date.'; return; }
        if (isUnset($root.submitter)) {
            $root.alertMessage_missingUserInput = 'Please enter a Submitter.'; return; }
        if (isUnset($root.sourceDocument)) {
            $root.alertMessage_missingUserInput = 'Please select a Source Document.'; return; }
        if (hasExcelWorkbookFileSuffix($root.sourceDocument.name) &&
            isUnset($root.nameOfSubdocumentContainingDataIfApplicable)) {
            $root.alertMessage_missingUserInput = 'Please select the name of the sheet within the Excel workbook that contains the data to be ingested.'; return; }

        formData.append('dataCategory', $root.dataCategory);
        formData.append('submissionDate', $root.submissionDate.toJSON());
        formData.append('submitter', $root.submitter);
        formData.append('projectName', $root.projectName);
        formData.append('chargeNumber', $root.chargeNumber);
        formData.append('comments', $root.comments);
        formData.append('sourceDocument', $root.sourceDocument);
        formData.append('nameOfSubdocumentContainingDataIfApplicable', $root.nameOfSubdocumentContainingDataIfApplicable);

        for (var i = 0; i < $root.attachments.length; i++) {
            var attachment = $root.attachments[i];
            formData.append('attachments[' + i + ']', attachment);
        }

        scope.$root.numberOfBlockingProcesses++;

        http.post(
            '/data-repository-app/api/addDataset',
            formData,
            {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }
        )
            .success(function (result) {
                scope.$root.numberOfBlockingProcesses--;
                scope.$root.alertMessage_success = "File successfully uploaded.";
                self.populateKnownColumnNames(scope, http);
            }
        )
            .error(function (data, status) {
                scope.$root.numberOfBlockingProcesses--;
                postError(scope.$root, data);
            }
        );
    }

    self.populateKnownDataTypes = function (scope, http) {

        scope.$root.numberOfBlockingProcesses++;

        http.get('/data-repository-app/api/dataTypes/all')
            .success(function (result) {
                scope.$root.numberOfBlockingProcesses--;
                scope.$root.knownDataTypes = result;
            })
            .error(function (data, status) {
                scope.$root.numberOfBlockingProcesses--;
                postError(scope.$root, data);
            });
    }

    self.populateKnownComparisonOperators = function (scope, http, criterion) {

        var dataTypeId = criterion.dataTypeId;

        if (isUnset(dataTypeId)) return;

        scope.$root.numberOfBlockingProcesses++;

        http.get('/data-repository-app/api/dataType/comparisonOperators?dataType=' + dataTypeId)
            .success(function (result) {
                scope.$root.numberOfBlockingProcesses--;
                criterion.knownComparisonOperators = result;
            })
            .error(function (data, status) {
                scope.$root.numberOfBlockingProcesses--;
                postError(scope.$root, data);
            });
    }

    self.findData = function (scope, http) {

        var searchCriteria = scope.$root.searchCriteria;
        var dataCategory = scope.$root.dataCategory;

        var searchCriteriaAsJson = self.toSearchCriteriaAsJson(dataCategory, searchCriteria);

        if (searchCriteriaAsJson.length === 1) {
            scope.$root.alertMessage_missingUserInput =
                "Can't perform search because none of the search criteria are complete.";
            return;
        }

        scope.$root.searchComplete = false;
        scope.$root.searchResults = [];

        scope.$root.searchCriteriaAsJson = searchCriteriaAsJson;

        var req = {
            method: 'POST',
            url: '/data-repository-app/api/rows/flat',
            headers: {
                'Content-Type': undefined
            },
            data: searchCriteriaAsJson
        }

        scope.$root.numberOfBlockingProcesses++;

        http(req)
            .success(function (result) {
                scope.$root.numberOfBlockingProcesses--;
                scope.$root.searchComplete = true;
                scope.$root.searchResults = result;
            })
            .error(function (data, status) {
                scope.$root.numberOfBlockingProcesses--;
                scope.$root.searchComplete = true;
                postError(scope.$root, data);
            });
    }

    function postError(root, data) {
        root.alertMessage_failure = "A failure occurred on the server.";
        console.error(data);
    }
})
