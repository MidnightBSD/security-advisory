angular.module('wwwApp').controller('AdvisoryCtrl', ['$scope', '$routeParams', '$location', '$http', 'AdvisoryService',
    function ($scope, $routeParams, $location, $http, AdvisoryService) {
        'use strict';

        $scope.max = 5;
        $scope.size = 50;
        $scope.ratings = {};

        $scope.page = $location.search().page;
        if (typeof $scope.page === 'undefined')
            $scope.page = 0;


        $scope.advisories = AdvisoryService.queryByVendor({ Name: $routeParams.vendor});

        $scope.load = function() {
            $scope.advisories = AdvisoryService.queryByVendor({
                vendor: $routeParams.vendor,
                page: $scope.page,
                size: $scope.size
            }, function(pkgs) {
          //      $scope.totalElements = $scope.advisories.totalElements;
            });
        };

        $scope.load();
    }]);
