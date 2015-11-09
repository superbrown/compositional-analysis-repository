
// needed for the date picker widget
$(function() {
    $( "#submissionDate" ).datepicker();
});



// A N G U L A R   s t u f f

var angular_module = angular.module('angular_module', ['ngMessages', 'ngResource']);

angular_module.controller('angular_uploadController', [
    '$scope', '$log', '$filter', '$resource',
    function($scope, $log, $filter, $resource) {

        $scope.dataCategory = '';
        $scope.dataCategory_submissionDate = '';
        $scope.dataCategory_submitter = '';
        $scope.dataCategory_projectName = '';
        $scope.dataCategory_chargeNumber = '';
        $scope.dataCategory_comments = '';

        $scope.dataCategories = [
            { name: "Algea"},
            { name: "ATP3"},
            { name: "Biomas"},
        ];
    }]);


