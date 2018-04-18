angular.module('wwwApp').controller('MainCtrl', ['$scope', '$http', '$location', 'AdvisoryService', 'VendorService',
    function ($scope, $http, $location, AdvisoryService, VendorService) {
    'use strict';

    $scope.advisories = AdvisoryService.query();

    $scope.vendors = VendorService.getPaged({page: 0});

    $scope.search = function() {
        $location.path('/search').search('keyword', $scope.term);
    }
}]);
