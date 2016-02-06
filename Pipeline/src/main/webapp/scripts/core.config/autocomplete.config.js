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
    .config(['autocompleteServiceProvider', '$locationProvider', SkAutocompleteConfig]);

  /* @ngInject */
  function SkAutocompleteConfig(autocompleteServiceProvider, $locationProvider) {

    var hostname = location.hostname;
    console.log(hostname)
    if (hostname.indexOf('skroll.io') == -1) {
      autocompleteServiceProvider.setHostName('http://' + hostname + ':8983');
    } else {
      autocompleteServiceProvider.setHostName('http://' + location.host);
    }
  }

})();