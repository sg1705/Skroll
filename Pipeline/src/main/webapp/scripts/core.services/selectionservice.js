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

  /* @ngInject */
  function SelectionService($ngSilentLocation) {

    //-- private variables
    var paragraphId = '',
      selectedText = '',
      mouseDownParaId = '',
      serializedSelection = '',
      serializedParagraphId = '',
      shortLink = '',
      searchSelectionRange = null,
      iframeElement = null,
      vm = this;

    //-- service definition
    var service = {
      //-- service variables
      paragraphId: paragraphId,
      selectedText: selectedText,
      mouseDownParaId: mouseDownParaId,
      serializedSelection: serializedSelection,
      serializedParagraphId: serializedParagraphId,
      shortLink: shortLink,
      searchSelectionRange: searchSelectionRange,

      //-- service functions
      scrollToParagraph: scrollToParagraph,
      scrollToSelection: scrollToSelection,
      saveSelection: saveSelection,
      clearSelection: clearSelection,
      removeHighlightParagraph: removeHighlightParagraph,
      getJQParaElement: getJQParaElement,
      getRangySelection: getRangySelection,
      getWindowSelection: getWindowSelection
    }

    return service;

    function getJQParaElement(paraId) {
      if (vm.iframeElement == null) {
        vm.iframeElement = $(document.getElementById("docViewIframe").contentWindow.document.body);
      }
      return $('#' + paraId, vm.iframeElement);
    }

    function getIframeElement() {
      if (vm.iframeElement == null) {
        vm.iframeElement = $(document.getElementById("docViewIframe").contentWindow.document.body);
      }
      return vm.iframeElement;
    }

    function getRangySelection() {
      return rangy.getSelection(document.getElementById("docViewIframe"));
    }

    function getWindowSelection() {
     return document.getElementById("docViewIframe").contentWindow.getSelection();
    }




    function scrollToParagraph(paragraphId, selectedText) {
      var me = this;
      // var para = $('#' + paragraphId);
      var para = getJQParaElement(paragraphId);
      // $('#' + SelectionModel.paragraphId).css('background-color', '');
      if (SelectionModel.paragraphId !== '' && SelectionModel.paragraphId !== undefined) {
        getJQParaElement(SelectionModel.paragraphId).css('background-color', '');
      }

      if (para != null) {
        var contentDiv = $('#skrollport');
        $('#skrollport').stop(true, true).animate({
          scrollTop: ($('#skrollport').scrollTop() - 200 + $(para).offset().top)
        }, 'slow');
        $('.selected-para-rhs').toggleClass('selected-para-rhs', false);
        //$('.searched-text-rhs').toggleClass('searched-text-rhs', false);
        para.toggleClass('selected-para-rhs', true);

        var searchResultApplier = rangy.createClassApplier('searched-text-rhs');

        // Remove existing highlights
        var prevRange = service.searchSelectionRange;
        var range = rangy.createRange();
        var searchScopeRange = rangy.createRange();
        searchScopeRange.selectNodeContents(para[0]);

        var options = {
          caseSensitive: false,
          wholeWordsOnly: false,
          withinRange: searchScopeRange
        };

        //prevRange.selectNodeContents('searched-text-rhs');
        if( prevRange !== null) {
          searchResultApplier.undoToRange(prevRange);
        }
        if (typeof selectedText !== 'undefined') {
          var escapedSearcedText = selectedText.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, '\\$&');
          var searchTerm = new RegExp('\\b(' + escapedSearcedText + ')\\b', 'ig');

          while (range.findText(searchTerm, options)) {
            searchResultApplier.applyToRange(range);
            range.collapse(false);
          }
        }
        service.searchSelectionRange = searchScopeRange;
        SelectionModel.paragraphId = paragraphId;
      }
    }

    function scrollToSelection(selectionId) {
      var savedSelection = JSON.parse(selectionId);
      var rangySelection = savedSelection.rangy;
      var paraId = savedSelection.paraId;
      // rangy.getSelection().restoreCharacterRanges($('#' + paraId).get(0), rangySelection);
      rangy.getSelection(document.getElementById("docViewIframe")).restoreCharacterRanges($('#' + paraId, getIframeElement()).get(0), rangySelection);
      var selection = rangy.getSelection(document.getElementById("docViewIframe"));
      // var selection = rangy.getSelection();
      var dom = selection.anchorNode;
      var para = $(dom);
      //assuming that node is a text node
      if (para != null) {
        var contentDiv = $('#skrollport');
        var paraOffset = 0;
        if ($(para).get(0).nodeType == 3) {
          //use parent
          paraOffset = $(para).parent().offset().top;
        } else {
          paraOffset = $(para).offset().top;
        }
        $('#skrollport').animate({
          scrollTop: ($('#skrollport').scrollTop() - 200 + paraOffset)
        }, 'slow');
      }
    }

    function saveSelection(paraId, selectedText) {
      this.paragraphId = paraId;
      this.selectedText = selectedText;
      this.serializedParagraphId = paraId;
      this.shortLink = '';
      var selection = {};
      // selection.rangy = rangy.getSelection().saveCharacterRanges($('#' + paraId).get(0));
      var iframe = $(document.getElementById("docViewIframe").contentWindow.document.body);
      selection.rangy = rangy.getSelection(document.getElementById("docViewIframe")).saveCharacterRanges($('#' + paraId, getIframeElement()).get(0));
      // selection.rangy = rangy.getSelection(getIframeElement()[0]).saveCharacterRanges(getJQParaElement()[0]);
      // selection.rangy = rangy.getSelection().saveCharacterRanges($('#' + paraId, getIframeElement()).get(0));

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
      this.serializedSelection = '';
      this.serializedParagraphId = '';
      this.shortLink = '';

    }

    function removeHighlightParagraph(paraId) {
      $('#' + paraId).css('background-color', '');
    }

  };

})();