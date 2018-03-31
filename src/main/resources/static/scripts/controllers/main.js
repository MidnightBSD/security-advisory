angular.module('wwwApp').controller('MainCtrl', ['$scope', '$http', '$location', 'AdvisoryService',
    function ($scope, $http, $location, OperatingSystemService) {
    'use strict';

    $scope.advisories = AdvisoryService.query();

    $scope.search = function() {
        $location.path('/search').search('keyword', $scope.term);
    }
}]);
