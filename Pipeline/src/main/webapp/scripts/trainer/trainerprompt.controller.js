(function(){

  angular
  	.module('app.trainer')
  	.controller('TrainerPromptCtrl', TrainerPromptCtrl);

	function TrainerPromptCtrl($scope,ToolbarModel, $mdBottomSheet, documentService) {
    $scope.prompt = ToolbarModel.trainerPrompt.text;
    $scope.items = ToolbarModel.trainerPrompt.items;
    $scope.documentService = documentService;

    $scope.itemClicked = function($index) {
      $mdBottomSheet.hide($index);
    }
  }

})();

