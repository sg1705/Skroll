(function(){


  'use strict';

  /**
   * @ngdoc overview
   * @name SkAutocompleteConfig
   * @description
   * # SkAutocompleteConfig
   *
   * Configure Autocomplete provider
   */

  angular
    .module('SkrollApp')
    .config(['autocompleteServiceProvider', SkAutocompleteConfig]);

  /* @ngInject */
  function SkAutocompleteConfig(autocompleteServiceProvider) {
    // config ZeroClipboard
    autocompleteServiceProvider.setHostName('http://localhost:8983');
  }

})();