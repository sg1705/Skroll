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
  function RelatedParaFactory(selectionService) {

    //-- private variables

    //-- service definition
    var service = {
      //-- service variables
      relatedParaState: {
        inputParaId: '',
        inputParaText: '',
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
      service.relatedParaState.inputParaText = '';
      service.relatedParaState.active = false;
      service.relatedParaState.paraProto = [];
    };

    function loadResults(inputParaId, relatedParas) {
      service.relatedParaState.inputParaText = selectionService.getParagraphText(inputParaId);
      service.relatedParaState.paraProto = relatedParas;
      service.relatedParaState.inputParaId = inputParaId;
      service.relatedParaState.active = true;
    }

  };

})();