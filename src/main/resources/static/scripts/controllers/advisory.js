angular.module('wwwApp').controller('AdvisoryCtrl', ['$scope', '$routeParams', '$location', '$http', 'AdvisoryService',
    function ($scope, $routeParams, $location, $http, AdvisoryService) {
        'use strict';

        $scope.advisories = AdvisoryService.queryByVendorName({vendor: $routeParams.vendor});
    }]);
