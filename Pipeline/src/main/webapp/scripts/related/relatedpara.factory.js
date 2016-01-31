(function() {

  'use strict';

  /**
   * @ngdoc service
   * @name SkrollApp.relatedParaFactory
   * @description
   * # RelatedParaFactory
   * Factory that represents relatedParaFactory in SkrollApp.
   */

  angular
    .module('app.related')
    .factory('relatedParaFactory', RelatedParaFactory);


  /* @ngInject */
  function RelatedParaFactory() {

    //-- private variables

    //-- service definition
    var service = {
      //-- service variables
      relatedParaState: {
        inputParaId: '',
        active: false,
        paraProto: []
      },
      clear: clear,
      loadResults: loadResults
    }

    return service;


    //////////////

    /**
     * Clear search
     **/
    function clear() {
      service.relatedParaState.inputParaId = '';
      service.relatedParaState.active = false;
      service.relatedParaState.paraProto = [];
    };

    function loadResults(inputParaId, relatedParas) {
      service.relatedParaState.paraProto = relatedParas;
      service.relatedParaState.inputParaId = inputParaId;
      service.relatedParaState.active = true;
    }

  };

})();