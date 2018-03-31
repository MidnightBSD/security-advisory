angular.module('wwwApp').factory('SearchService', ['$resource', function ($resource) {
    'use strict';
    return $resource('/api/search?size=:size&page=:page&term=:term',
            {term: '@term', size: '@size', page: '@page'});
}]);