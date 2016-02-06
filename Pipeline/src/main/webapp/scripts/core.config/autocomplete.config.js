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
    var prodContextPath = '/a?wt=json&indent=true&group=true&group.field=type&group.limit=5&omitHeader=true&q=name_autocomplete%3A';
    var devContextPath = '/solr/autocomplete/select?wt=json&indent=true&group=true&group.field=type&group.limit=5&omitHeader=true&q=name_autocomplete%3A';

    console.log(hostname)
    if (hostname.indexOf('skroll.io') == -1) {
      autocompleteServiceProvider.setHostName('http://' + hostname + ':8983');
      autocompleteServiceProvider.setContextPath(devContextPath);
    } else {
      autocompleteServiceProvider.setHostName('http://' + location.host);
      autocompleteServiceProvider.setContextPath(prodContextPath);
    }
  }

})();