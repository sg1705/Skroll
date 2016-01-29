(function() {

  'use strict';

  /**
   * @ngdoc service
   * @name myappApp.RelatedParaService
   * @description
   * # contextMenuService
   * Service in the SkrollApp.
   */



  angular
    .module('app.related')
    .service('relatedParaService', RelatedParaService)

  /* @ngInject */
  function RelatedParaService(textSelectionObserverService, documentService, relatedParaFactory) {

    //-- private variables
    var service = this;
    var isOpen = false;

    //-- public variables
    service.onSelection = onSelection;

    ////////////

    /*
     * Invoke on text selection
     */
    function onSelection(e) {
      console.log('on selection called in related para service');
      documentService.getRelatedPara(documentModel.documentId, e.paraId).
      then(function(data) {
        console.log(data);
        relatedParaFactory.loadResults(e.paraId, data);
      })
    }

  }

  angular
    .module('app.related')
    .run(function(textSelectionObserverService, relatedParaService) {
      textSelectionObserverService.register(relatedParaService.onSelection);
    });



})();
