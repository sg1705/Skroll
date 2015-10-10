(function() {

  'use strict';

  /**
   * @ngdoc service
   * @name SkrollApp.selectionService
   * @description
   * # selectionService
   * Factory that represents selectionService in SkrollApp.
   */

  angular
    .module('app.core.services')
    .factory('selectionService', SelectionService);


  /** @ngInject **/
  function SelectionService($ngSilentLocation) {

    //-- private variables
    var paragraphId = '',
      selectedText = '',
      mouseDownParaId = '',
      serializedSelection = '',
      serializedParagraphId = '',
      shortLink = '';

    //-- service definition
    var service = {

      //-- service variables
      paragraphId: paragraphId,
      selectedText: selectedText,
      mouseDownParaId: mouseDownParaId,
      serializedSelection: serializedSelection,
      serializedParagraphId: serializedParagraphId,
      shortLink: shortLink,

      //-- service functions
      scrollToParagraph: scrollToParagraph,
      scrollToSelection: scrollToSelection,
      saveSelection: saveSelection,
      clearSelection: clearSelection,
      removeHighlightParagraph: removeHighlightParagraph

    }

    return service;

    function scrollToParagraph(paragraphId) {
      var para = $("#" + paragraphId);
      $("#" + SelectionModel.paragraphId).css("background-color", "");
      if (para != null) {
        var contentDiv = $("#skrollport");
        $("#skrollport").animate({
          scrollTop: ($("#skrollport").scrollTop() - 200 + $(
            para).offset().top)
        }, "slow");
        $(para).css("background-color", "yellow");
        SelectionModel.paragraphId = paragraphId;
      }
    }

    function scrollToSelection(selectionId) {
      var savedSelection = JSON.parse(selectionId);
      var rangySelection = savedSelection.rangy;
      var paraId = savedSelection.paraId;
      rangy.getSelection().restoreCharacterRanges($("#" + paraId).get(0), rangySelection);
      var selection = rangy.getSelection();
      var dom = selection.anchorNode;
      var para = $(dom);
      //assuming that node is a text node
      if (para != null) {
        var contentDiv = $("#skrollport");
        var paraOffset = 0;
        if ($(para).get(0).nodeType == 3) {
          //use parent
          paraOffset = $(para).parent().offset().top;
        } else {
          paraOffset = $(para).offset().top;
        }
        $("#skrollport").animate({
          scrollTop: ($("#skrollport").scrollTop() - 200 + paraOffset)
        }, "slow");
      }
    }

    function saveSelection(paraId, selectedText) {
      this.paragraphId = paraId;
      this.selectedText = selectedText;
      this.serializedParagraphId = paraId;
      this.shortLink = '';
      var selection = {};
      selection.rangy = rangy.getSelection().saveCharacterRanges($("#" + paraId).get(0));
      selection.paraId = paraId;
      this.serializedSelection = JSON.stringify(selection);
      this.serializedSelection = encodeURIComponent(encodeURIComponent(this.serializedSelection));
      $ngSilentLocation.silent('/view/docId/' + documentModel.documentId + '/linkId/' + this.serializedSelection, false);
    }

    function clearSelection() {
      //clear highlight
      if (this.paragraphId != '') {
        this.removeHighlightParagraph(this.paragraphId);
      }
      this.paragraphId = '';
      this.selectedText = '';
      this.serializedSelection = "";
      this.serializedParagraphId = "";
      this.shortLink = '';

    }

    function removeHighlightParagraph(paraId) {
      $("#" + paraId).css("background-color", "");
    }

  };

})();