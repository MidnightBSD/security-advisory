angular.module('wwwApp').factory('AdvisoryService', ['$resource', function ($resource) {
    'use strict';
    return $resource('/api/advisory/:Id', {Id: '@Id', cve: '@cve', vendor: '@vendor', product: '@product'},
            {
                'getByCve': {
                    method: 'GET',
                    isArray: false,
                    url: '/api/advisory/cveId/:cve'
                },
                'queryByVendorName': {
                    method: 'GET',
                    isArray: true,
                    url: '/api/advisory/vendor/:vendor'
                },
                'queryByProductName': {
                    method: 'GET',
                    isArray: true,
                    url: '/api/advisory/product/:product'
                },
                'queryByVendorAndProduct': {
                    method: 'GET',
                    isArray: true,
                    url: '/api/advisory/vendor/:vendor/product/:product'
                }
            });
}]);