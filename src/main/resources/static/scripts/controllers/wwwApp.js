angular.module('wwwApp').controller('wwwAppCtrl',
        ['$scope', '$routeParams', '$location', '$window',
            function ($scope, $routeParams, $location, $window) {
                'use strict';

                $scope.$on('$destroy', function finalize() {
                    $scope.Login = null;
                });
                $scope.$on('$viewContentLoaded', function () {
                    $window.ga('send', 'pageview');
                });
            }]);