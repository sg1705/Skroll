'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ViewPortCtrl
 * @description
 * # ViewPortCtrl
 * Controller of the ViewPortCtrl
 */



var ViewPortCtrl = function(selectionService, $log, $routeParams, 
        scrollObserverService, clickObserverService, textSelectionObserverService) {
  this.selectionService = selectionService;
  // this.documentService = documentService;
  // this.trainerService = trainerService;
  // this.$mdBottomSheet = $mdBottomSheet;
  // this.LHSModel = LHSModel;
  // this.ToolbarModel = ToolbarModel;
  this.documentModel = documentModel;
  this.documentModel.documentId = $routeParams.docId;
  this.selectionService.serializedSelection = decodeURIComponent(decodeURIComponent($routeParams.linkId));
  this.scrollObserverService = scrollObserverService;
  this.clickObserverService = clickObserverService;
  this.textSelectionObserverService = textSelectionObserverService;
}

ViewPortCtrl.prototype.mouseDown = function($event) {
  var selection = window.getSelection().toString();
  var paraId = this.inferParagraphId($event);
  this.selectionService.mouseDownParaId = paraId;
}

ViewPortCtrl.prototype.mouseUp = function($event) {
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
  this.selectionService.clearSelection();
  var paraId = this.inferParagraphId($event);
  if (paraId == null)
    return;
  //save selection
  this.selectionService.saveSelection(paraId, selection);
  this.textSelectionObserverService.notify({'paraId' : paraId, 'selectedText' : selection});

}


ViewPortCtrl.prototype.paraClicked = function($event) {
  console.log("Paragraph clicked");
  //find out if this is a selection
  var selection = window.getSelection().toString();
  //check to see if mouseup should handle it
  if (selection != '')
    return;
  //clear highlight
  this.selectionService.clearSelection();
  var paraId = this.inferParagraphId($event);
  if (paraId == null)
    return;

  //store in selectionService
  this.selectionService.paragraphId = paraId;
  //highlight paragraph
  this.highlightParagraph(paraId);

  this.scrollObserverService.notify(paraId);
  this.clickObserverService.notify(paraId);

}


ViewPortCtrl.prototype.highlightParagraph = function(paraId) {
  $("#" + paraId).css("background-color", "yellow");
}

// ViewPortCtrl.prototype.removeHighlightParagraph = function(paraId) {
//   $("#" + paraId).css("background-color", "");
// }

ViewPortCtrl.prototype.inferParagraphId = function($event) {

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



angular
  .module('SkrollApp')
  .controller('ViewPortCtrl', [ 'selectionService', '$log', '$routeParams', 
                                'scrollObserverService', 'clickObserverService', 'textSelectionObserverService',
                                ViewPortCtrl ]);
