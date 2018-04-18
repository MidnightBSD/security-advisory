angular.module('wwwApp').factory('ProductService', ['$resource', function ($resource) {
    'use strict';
    return $resource('/api/product/:Id', {Id: '@Id', version: '@version', name: '@name'},
            {
                'queryByProductName': {
                    method: 'GET',
                    isArray: true,
                    url: '/api/product/name/:name'
                },
                'getByNameAndVersion': {
                    method: 'GET',
                    isArray: false,
                    url: '/api/product/name/:name/version/:version'
                }
            });
}]);