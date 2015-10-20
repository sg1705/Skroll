(function() {

  /**
   * @ngdoc function
   * @name skrollApp.controller:EnableTrainerToolbarCtrl
   * @description
   * # EnableTrainerToolbarCtrl
   * Controller of the EnableTrainerToolbarCtrl
   */



  angular
    .module('app.trainer')
    .controller('EnableTrainerToolbarCtrl', EnableTrainerToolbarCtrl);

  /* @ngInject */
  function EnableTrainerToolbarCtrl(featureFlags, $location, trainerPromptService) {

    //turn on trainer
    enableTrainerFlag();
    //register
    trainerPromptService.registerTextSelectionObserver();
    trainerPromptService.registerClickObserver();

    $location.path("/list");

    function enableTrainerFlag() {
      var flagName = 'trainer';
      var flags = featureFlags.get();
      var flag = _.find(flags, function(item) {
        if (item.key == flagName) {
          return true;
        }
      });
      featureFlags.enable(flag);
    }

  }

})();