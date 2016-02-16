(function() {

  'use strict';

  /**
   * @ngdoc service
   * @name myappApp.SkParaNavCtrl
   * @description
   * # SkParaNavCtrl
   * Service in the SkrollApp.
   */



  angular
    .module('app.paranav')
    .controller('SkParaNavCtrl', SkParaNavCtrl)

  /* @ngInject */
  function SkParaNavCtrl(relatedParaFactory) {

    //-- private variables
    var vm = this;

    //-- public variables


    //-- public methods
    vm.close = close;

    ////////////

    function close() {
      vm.onClose();
    }

  }

})();
