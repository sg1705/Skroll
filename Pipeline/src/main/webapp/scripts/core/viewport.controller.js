(function(){
  /**
   * @ngdoc function
   * @name skrollApp.controller:ViewPortCtrl
   * @description
   * # ViewPortCtrl
   * Controller of the ViewPortCtrl
   */


  angular
    .module('app.core')
    .controller('ViewPortCtrl', ViewPortCtrl);

  /* @ngInject */
  function ViewPortCtrl(selectionService, $log, $routeParams, 
                        scrollObserverService, clickObserverService, 
                        textSelectionObserverService) {

    //-- private variables
    var vm = this;


    //-- public methods
    vm.mouseDown    = mouseDown;
    vm.mouseUp      = mouseUp;
    vm.paraClicked  = paraClicked;
    vm.inferParagraphId   = inferParagraphId;
    vm.highlightParagraph = highlightParagraph;

    //-- initialization
    documentModel.documentId = $routeParams.docId;
    selectionService.serializedSelection = decodeURIComponent(decodeURIComponent($routeParams.linkId));


    /////////////


    function mouseDown($event) {
      var selection = window.getSelection().toString();
      var paraId = vm.inferParagraphId($event);
      selectionService.mouseDownParaId = paraId;
    }

    function mouseUp($event) {
      console.log("mouseup clicked");
      //should mouse click handle it
      //find out if this is a selection
      if (rangy.getSelection().toString() != '') {
        //rangy.getSelection().expand("word", { trim: true });  
      }
      
      var selection = window.getSelection().toString();
      if ((selection == '') || (selection == undefined))
        return;

      //clear selection
      selectionService.clearSelection();
      var paraId = vm.inferParagraphId($event);
      if (paraId == null)
        return;
      //save selection
      selectionService.saveSelection(paraId, selection);
      textSelectionObserverService.notify({'paraId' : paraId, 'selectedText' : selection});
    }


    function paraClicked($event) {
      console.log("Paragraph clicked");
      //find out if this is a selection
      var selection = window.getSelection().toString();
      //check to see if mouseup should handle it
      if (selection != '' || (selection == undefined))
        return;
      //clear highlight
      selectionService.clearSelection();
      var paraId = vm.inferParagraphId($event);
      if (paraId == null)
        return;

      //store in selectionService
      selectionService.paragraphId = paraId;
      //highlight paragraph
      vm.highlightParagraph(paraId);

      scrollObserverService.notify(paraId);
      clickObserverService.notify(paraId);

    }


    function highlightParagraph(paraId) {
      $("#" + paraId).css("background-color", "yellow");
    }

    // ViewPortCtrl.prototype.removeHighlightParagraph = function(paraId) {
    //   $("#" + paraId).css("background-color", "");
    // }

    function inferParagraphId($event) {
      var parents = $($event.target).parents("div[id^='p_']");
      for (var ii = 0; ii < parents.length; ii++) {
        console.log($(parents[ii]).attr('id'));
      }

      var children = $($event.target).children("div[id^='p_']");
      //console.log('children:' + children.length);
      if (parents.length > 1) {
        return $(parents[0]).attr('id');
      } else {

        if ((children.length == 0) && (parents.length ==1)) {
          return $(parents[0]).attr('id');
        }
        return null;
      }

    }
  }

})();