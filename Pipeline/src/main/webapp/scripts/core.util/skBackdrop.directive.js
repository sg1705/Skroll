(function() {

  'use strict';

  /**
   * @ngdoc directive
   * @name SkrollApp.directive:skBackdrop
   * @description
   * # do something when enter is pressed
   */
  angular
    .module('app.core.util')
    .directive('skBackdrop', SkBackdrop);

  /* @ngInject */
  function SkBackdrop(documentModel) {

    var directive = {
      restricted: 'E',
      template: '<md-backdrop ng-show="backdropCtrl.documentModel.isProcessing"  class="md-opaque md-bottom-sheet-backdrop md-default-theme"> \
                   <md-progress-circular style="z-index: 80; position:absolute;top: 50%;left: 50%;" md-mode="indeterminate"></md-progress-circular> \
                  </md-backdrop>',
      controller: SkBackdropCtrl,
      controllerAs: 'backdropCtrl'
    }

    return directive;

    //////////

    function SkBackdropCtrl(documentModel) {
      //-- private variables
      var vm = this;

      //-- public methods
      vm.documentModel = documentModel;
    }


  }

})();