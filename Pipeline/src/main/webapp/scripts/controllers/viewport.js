'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ViewPortCtrl
 * @description
 * # ViewPortCtrl
 * Controller of the ViewPortCtrl
 */



var ViewPortCtrl = function(selectionService, $mdBottomSheet,
  ToolbarModel, LHSModel, $log, $routeParams, scrollObserverService, documentService, trainerService,
  clickObserverService, textSelectionObserverService) {
  this.selectionService = selectionService;
  this.documentService = documentService;
  this.trainerService = trainerService;
  this.$mdBottomSheet = $mdBottomSheet;
  this.ToolbarModel = ToolbarModel;
  this.LHSModel = LHSModel;
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

  // if (ToolbarModel.trainerToolbar.isTrainerMode) {
  //   //this.handleTrainerTextSelection(paraId, selection);
  // }
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
  this.loadParagraphJson(paraId);

  this.scrollObserverService.notify(paraId);
  this.clickObserverService.notify(paraId);

  // if (ToolbarModel.trainerToolbar.isTrainerMode) {
  //   //this.handleTrainerParaSelection(paraId);
  // }

}

ViewPortCtrl.prototype.loadParagraphJson = function(paraId) {
  this.selectionService.paragraphId = paraId;
  this.documentService
    .getParagraphJson(this.documentModel.documentId, paraId)
    .then(angular.bind(this, function(result) {
      var oldJson = this.ToolbarModel.trainerToolbar.lastJson;
      var newJson = JSON.stringify(result, null, 2);
      var ds;
      if ((oldJson != '') && (paraId == this.ToolbarModel.trainerToolbar.lastSelectedParaId)) {
        var dmp = new diff_match_patch();
        var a = dmp.diff_linesToChars_(oldJson, newJson);
        var lineText1 = a['chars1'];
        var lineText2 = a['chars2'];
        var lineArray = a['lineArray'];
        var d = dmp.diff_main(lineText1, lineText2, false);
        dmp.diff_charsToLines_(d, lineArray);
        var ms_end = (new Date()).getTime();
        dmp.diff_cleanupSemantic(d);
        ds = dmp.diff_prettyHtml(d);
      } else {
        ds = newJson;
      }
      this.ToolbarModel.trainerToolbar.lastJson = newJson;
      this.ToolbarModel.trainerToolbar.lastSelectedParaId = paraId;
      $("#rightPane").html(ds);
    }), function(data, status) {
      console.log(status);
    });
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
  .controller('ViewPortCtrl', [ 'selectionService', '$mdBottomSheet', 'ToolbarModel', 
                                'LHSModel', '$log', '$routeParams', 
                                'scrollObserverService', 'documentService', 'trainerService',
                                'clickObserverService', 'textSelectionObserverService',
                                ViewPortCtrl ]);
