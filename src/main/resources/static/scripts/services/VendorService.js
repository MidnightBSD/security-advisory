angular.module('wwwApp').factory('VendorService', ['$resource', function ($resource) {
    'use strict';
    return $resource('/api/vendor/:Id', {Id: '@Id', name: '@name', page: '@page', size: '@size'},
            {
                'getByVendorName': {
                    method: 'GET',
                    isArray: false,
                    url: '/api/vendor/name/:name'
                },
                'getPaged': {
                    method: 'GET',
                    isArray: false,
                    url: '/api/vendor/?page=:page&size=:size&sort=name'
                }
            });
}]);