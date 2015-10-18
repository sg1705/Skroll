(function(){

  'use strict';

  /**
   * @ngdoc overview
   * @name SkConfigRoutes
   * @description
   * # SkConfigRoutes
   *
   * Configure routes
   */

  //** when newer version of material comes out
  //this is lifted from http://goo.gl/mrWZ0F
  //reloadOnSearch was because of this plunker http://plnkr.co/edit/tgin0gpQl1qwu9hVXBno?p=preview
  angular
    .module('SkrollApp')
    .config(SkConfigRoutes);

  /* @ngInject */
  function SkConfigRoutes($mdIconProvider, $routeProvider, $locationProvider) {
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
      controller: 'DocListCtrl',
      controllerAs: 'doclistCtrl'
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
      controllerAs: 'contentCtrl',
      reloadOnSearch: false
    }).
    when('/view/docId/:docId/linkId/:linkId', {
      templateUrl: 'scripts/core/app.core.tmpl.html',
      controller: 'ContentCtrl',
      controllerAs: 'contentCtrl',
      reloadOnSearch: false
    }).
    when('/error', {
      templateUrl: 'scripts/core.util/404.tmpl.html',
    }).
    otherwise({
      redirectTo: '/search'
    });
    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix("!");
  }

})();