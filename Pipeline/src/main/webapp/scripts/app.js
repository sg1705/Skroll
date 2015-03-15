'use strict';

/**
 * @ngdoc overview
 * @name SkrollApp
 * @description
 * # SkrollApp
 *
 * Main module of the application.
 */

angular.module('SkrollApp', ['ngMaterial','ngSanitize', 'ngTouch' ]);

//** when newer version of material comes out
//this is lifted from http://goo.gl/mrWZ0F
angular.module('SkrollApp')
  .config(function($mdIconProvider) {
    $mdIconProvider
    .iconSet('viewer', 'img/icons/sets/viewer-24.svg', 24);
  });
