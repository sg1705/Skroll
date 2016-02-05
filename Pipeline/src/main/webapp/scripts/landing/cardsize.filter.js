(function() {

  angular
    .module('app.landing')
    .filter('card', function() {
      return function(items, isSingleCategory) {
        if (isSingleCategory) {
          return items;
        }
        return _.first(items, 6);
      };
    });


})();