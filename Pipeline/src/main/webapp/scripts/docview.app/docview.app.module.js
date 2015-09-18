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

angular.module('SkrollDocView', [
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
	.module('SkrollDocView')
	.config(function($mdIconProvider, $routeProvider, $locationProvider) {
		$mdIconProvider
			.iconSet('viewer', 'img/icons/sets/viewer-24.svg', 24);

	    $locationProvider.html5Mode(true);
	    $locationProvider.hashPrefix("!");

	});

angular.module('SkrollDocView')
.config(function($mdThemingProvider) {
  $mdThemingProvider.theme('default-dark')
  	.primaryPalette('blue')
    .dark();
})
.config(['uiZeroclipConfigProvider', function(uiZeroclipConfigProvider) {
  // config ZeroClipboard
  uiZeroclipConfigProvider.setZcConf({
    swfPath: '../bower_components/zeroclipboard/dist/ZeroClipboard.swf'
  });

}]);


angular.module('SkrollDocView')
.config(['$httpProvider', function($httpProvider) {
	$httpProvider.defaults.withCredentials = true;
}]);

})();