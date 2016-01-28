(function(){


  'use strict';

  /**
   * @ngdoc overview
   * @name SkMdThemeConfig
   * @description
   * # SkMdThemeConfig
   *
   * Configure angular material themses
   */

  angular
    .module('SkrollApp')
    .config(SkMdThemeConfig);

  /* @ngInject */
  function SkMdThemeConfig($mdThemingProvider) {
    $mdThemingProvider
      .theme('default-dark')
      .primaryPalette('blue', {
        'hue-1' : '400'
      })
      .dark();

    $mdThemingProvider
      .theme('greyTheme')
      .primaryPalette('grey', {
      });


    // $mdThemingProvider.theme('default')
    //  .primaryPalette('blue', {

    //  });
  };

})();