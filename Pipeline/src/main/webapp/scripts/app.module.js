(function(){


'use strict';

/**
 * @ngdoc overview
 * @name SkrollApp
 * @description
 * # SkrollApp
 *
 * Main module of the application.
 */

angular.module('SkrollApp', [
	'ngAnimate',
	'ngMaterial',
	'ngSanitize',
	'ngCookies',
	'ngTouch',
	'ngRoute',
	'ngSilent',
	'feature-flags',
	'zeroclipboard',
	'angulartics',
	'angulartics.google.analytics',
	'app.core',
	'app.core.services',
	'app.core.util',
	'app.contextmenu',
	'app.landing',
	'app.search',
	'app.trainer',
	'app.toc',
	'app.upload'
]);

//** when newer version of material comes out
//this is lifted from http://goo.gl/mrWZ0F
//reloadOnSearch was because of this plunker http://plnkr.co/edit/tgin0gpQl1qwu9hVXBno?p=preview
angular
	.module('SkrollApp')
	.config(function($mdIconProvider, $routeProvider, $locationProvider) {
		$mdIconProvider
			.iconSet('viewer', 'img/icons/sets/viewer-24.svg', 24);

		$routeProvider.
		when('/search', {
			templateUrl: 'scripts/landing/landing.tmpl.html',
			controller: 'LandingCtrl',
			controllerAs: 'ctrl'
		}).
		when('/search/:searchText', {
			templateUrl: 'scripts/landing/landing.tmpl.html',
			controller: 'LandingCtrl',
			controllerAs: 'ctrl'
		}).
		when('/list', {
			templateUrl: 'scripts/landing/doclist.tmpl.html',
			controller: 'ContentCtrl',
			controllerAs: 'ctrl'
		}).
		when('/open', {
			templateUrl: 'scripts/core/app.core.tmpl.html',
			controller: 'ImportCtrl'
		}).
		when('/trainer', {
			templateUrl: 'scripts/core/app.core.tmpl.html',
			controller: 'EnableTrainerToolbarCtrl'
		}).
		when('/view/docId/:docId', {
			templateUrl: 'scripts/core/app.core.tmpl.html',
			controller: 'ContentCtrl',
			controllerAs: 'ctrl',
			reloadOnSearch: false
		}).
		when('/view/docId/:docId/linkId/:linkId', {
			templateUrl: 'scripts/core/app.core.tmpl.html',
			controller: 'ContentCtrl',
			controllerAs: 'ctrl',
			reloadOnSearch: false
		}).
		otherwise({
			redirectTo: '/search'
		});
	    $locationProvider.html5Mode(true);
	    $locationProvider.hashPrefix("!");

	});

angular.module('SkrollApp')
.config(function($mdThemingProvider) {
  $mdThemingProvider.theme('default-dark')
  	.primaryPalette('blue', {
  		'hue-1' : '400'
  	})
    .dark();

  // $mdThemingProvider.theme('default')
  // 	.primaryPalette('blue', {

  // 	});

})
.config(['uiZeroclipConfigProvider', function(uiZeroclipConfigProvider) {
  // config ZeroClipboard
  uiZeroclipConfigProvider.setZcConf({
    swfPath: '../bower_components/zeroclipboard/dist/ZeroClipboard.swf'
  });

}]);


angular.module('SkrollApp')
.config(['$httpProvider', function($httpProvider) {
	$httpProvider.defaults.withCredentials = true;
	$httpProvider.defaults.useXDomain = true;
	delete $httpProvider.defaults.headers.common['X-Requested-With'];
}]);

})();