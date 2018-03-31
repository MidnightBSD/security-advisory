angular.module('wwwApp',
        [
            'ui.bootstrap.buttons',
            'ui.bootstrap.pagination',
            'ui.bootstrap.rating',
            'ngRoute',
            'ngResource',
            'ngCookies',
            'ngRoute',
            'ngSanitize'
        ])
        .config(function ($routeProvider, $locationProvider) {
            'use strict';

            $routeProvider
                    .when('/', {
                        templateUrl: 'views/main.html',
                        controller: 'MainCtrl'
                    })
                    .when('/advisory/:id', {
                        templateUrl: 'views/advisory.html',
                        controller: 'AdvisoryCtrl'
                    })
                    .when('/os/:os/arch/:arch', {
                        templateUrl: 'views/os.html',
                        controller: 'OsCtrl'
                    })
                    .when('/privacy', {
                        templateUrl: 'views/privacy.html',
                        controller: 'PrivacyCtrl'
                    })
                    .when('/search', {
                        templateUrl: 'views/search.html',
                        controller: 'SearchCtrl'
                    })
                    .otherwise({
                        redirectTo: '/'
                    });

            $locationProvider.hashPrefix('!');
        });