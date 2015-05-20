'use strict';

/**
 * @ngdoc overview
 * @name SkrollApp
 * @description
 * # SkrollApp
 *
 * Main module of the application.
 */

angular.module('SkrollApp', ['ngMaterial', 'ngSanitize', 'ngTouch', 'ngRoute']);

//** when newer version of material comes out
//this is lifted from http://goo.gl/mrWZ0F
//reloadOnSearch was because of this plunker http://plnkr.co/edit/tgin0gpQl1qwu9hVXBno?p=preview
angular.module('SkrollApp')
	.config(function($mdIconProvider, $routeProvider, $locationProvider) {
		$mdIconProvider
			.iconSet('viewer', 'img/icons/sets/viewer-24.svg', 24);

		$routeProvider.
		when('/list', {
			templateUrl: 'partials/doclist.tmpl.html',
			controller: 'ContentCtrl'
		}).
		when('/open', {
			templateUrl: 'partials/viewport.tmpl.html',
			controller: 'ImportCtrl'
		}).
		when('/view/docId/:docId', {
			templateUrl: 'partials/viewport.tmpl.html',
			controller: 'ContentCtrl',
			reloadOnSearch: false
		}).
		otherwise({
			redirectTo: '/list'
		});
	    $locationProvider.html5Mode(true);
	    $locationProvider.hashPrefix("!");

	});