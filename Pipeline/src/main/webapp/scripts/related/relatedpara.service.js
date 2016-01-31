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
  function RelatedParaService(textSelectionObserverService, documentService, relatedParaFactory, selectionService) {

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
        var data = _.map(data, function(d){
          d.paraText = selectionService.getParagraphText(d.paraId).substring(0,100);
          return d;
        });
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
