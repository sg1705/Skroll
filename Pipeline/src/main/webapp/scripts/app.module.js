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
    'app.core.html',
    'app.core.pdf',
  	'app.core.services',
  	'app.core.util',
    'app.paranav',
  	'app.contextmenu',
  	'app.landing',
  	'app.search',
  	'app.trainer',
  	'app.toc',
  	'app.upload',
    'pdfjsViewer',
    'app.searchbox',
    'app.related',
    'app.link'
  ]);

})();