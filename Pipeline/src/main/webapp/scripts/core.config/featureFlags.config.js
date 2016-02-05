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
      "key": "trainer.benchmark",
      "active": false,
      "name": "flag for fetching benchmark score",
      "description": "no description"
    }, {
      "key": "googl.shortlink",
      "active": true,
      "name": "flag to create shortlinsk",
      "description": "no description"
    }, {
      "key": "search.old",
      "active": false,
      "name": "Use old autocomplete based search",
      "description": "no description"
    }, {
      "key": "searchbox.new",
      "active": true,
      "name": "Show new searchbox on landing page",
      "description": "no description"
    }, {
      "key": "searchbox.old",
      "active": false,
      "name": "Show old searchbox on landing page",
      "description": "no description"
    }, {
      "key": "solr.autocomplete",
      "active": true,
      "name": "Is autocomplete for Solr available",
      "description": "no description"
    }, {
      "key": "related.lhs",
      "active": true,
      "name": "Show related para in LHS",
      "description": "no description"
    }];
    featureFlags.set(flags);
  }

})();