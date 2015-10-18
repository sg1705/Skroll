(function() {
  'use strict';

  angular
    .module('SkrollApp')
    .run(SkFeatureFlagsConfig);

  /* @ngInject */
  function SkFeatureFlagsConfig(featureFlags, $http) {
    var flags = [{
      "key": "trainer",
      "active": false,
      "name": "flag for trainer",
      "description": "no description"
    }, {
      "key": "trainer.probability",
      "active": false,
      "name": "flag for probabilities in TOC",
      "description": "no description"
    }, {
      "key": "googl.shortlink",
      "active": false,
      "name": "flag to create shortlinsk",
      "description": "no description"
    }, {
      "key": "search.old",
      "active": false,
      "name": "Use old autocomplete based search",
      "description": "no description"
    }];
    featureFlags.set(flags);
  }

})();