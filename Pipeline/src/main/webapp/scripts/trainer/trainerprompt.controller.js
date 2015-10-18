(function(){

  angular
  	.module('app.trainer')
  	.controller('TrainerPromptCtrl', TrainerPromptCtrl);

  /* @ngInject */
	function TrainerPromptCtrl($scope, $mdBottomSheet, documentService, trainerModel) {
    $scope.prompt = trainerModel.trainerPrompt.text;
    $scope.items = trainerModel.trainerPrompt.items;

    $scope.documentService = documentService;

    $scope.itemClicked = function($index) {
      $mdBottomSheet.hide($index);
    }
  }

})();

