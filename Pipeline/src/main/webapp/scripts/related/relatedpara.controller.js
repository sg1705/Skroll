(function() {

  'use strict';

  /**
   * @ngdoc service
   * @name myappApp.RelatedParaController
   * @description
   * # RelatedParaController
   * Service in the SkrollApp.
   */



  angular
    .module('app.related')
    .controller('RelatedParaCtrl', RelatedParaController)

  /* @ngInject */
  function RelatedParaController(relatedParaFactory) {

    //-- private variables
    var vm = this;

    //-- public variables
    vm.relatedParaFactory = relatedParaFactory;

    //-- public methods
    vm.clear = clear;
    vm.close = close;

    ////////////

    function clear() {
      relatedParaFactory.clear();
    }

    function close() {
      vm.relatedParaFactory.relatedParaState.active = false;
    }

  }

})();
