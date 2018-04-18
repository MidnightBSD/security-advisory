angular.module('wwwApp').controller('MainCtrl', ['$scope', '$http', '$location', 'VendorService',
    function ($scope, $http, $location, VendorService) {
        'use strict';

        $scope.vendors = [];


        $scope.load = function () {
            var vendor = VendorService.getPaged({page: 0, size: 100}, function () {
                for (var x = 0; x < vendor.content.length; x++) {
                    $scope.vendors.push(vendor.content[x]);
                }

                for (var i = 1; i < vendor.totalPages; i++) {
                      VendorService.getPaged({page: i, size: 100}, function (result) {
                          for (var z = 0; z < result.content.length; z++) {
                              $scope.vendors.push(result.content[z]);
                          }
                    });
                }
            });
        };

        $scope.search = function () {
            $location.path('/search').search('keyword', $scope.term);
        };

        $scope.load();
    }]);
