(function(){


  'use strict';

  /**
   * @ngdoc overview
   * @name SkHttpProviderConfig
   * @description
   * # SkHttpProviderConfig
   *
   * Configure HttpProvider
   */

  angular
    .module('SkrollApp')
    .config(['$httpProvider', SkHttpProviderConfig]);

  /* @ngInject */
  function SkHttpProviderConfig($httpProvider) {
    $httpProvider.defaults.withCredentials = true;
    $httpProvider.defaults.useXDomain = true;
    // $httpProvider.interceptors.push('httpRequestErrorInterceptor');
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
  }

})();