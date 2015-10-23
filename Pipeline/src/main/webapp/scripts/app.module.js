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

})();