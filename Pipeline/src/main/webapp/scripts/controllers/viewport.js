'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ViewPortCtrl
 * @description
 * # ViewPortCtrl
 * Controller of the ViewPortCtrl
 */

var ViewPortCtrl = function(SelectionModel, documentService, $mdBottomSheet,
  ToolbarModel, LHSModel, $log, $routeParams) {
  this.SelectionModel = SelectionModel;
  this.documentService = documentService;
  this.$mdBottomSheet = $mdBottomSheet;
  this.ToolbarModel = ToolbarModel;
  this.LHSModel = LHSModel;
  this.documentModel = documentModel;
  this.documentModel.documentId = $routeParams.docId;
  this.SelectionModel.serializedSelection = decodeURIComponent(decodeURIComponent($routeParams.linkId));

  
}

ViewPortCtrl.prototype.mouseDown = function($event) {
  var selection = window.getSelection().toString();
  var paraId = this.inferParagraphId($event);
  this.SelectionModel.mouseDownParaId = paraId;
}

ViewPortCtrl.prototype.mouseUp = function($event) {
  console.log("mouseup clicked");
  //should mouse click handle it
  //find out if this is a selection
  if (rangy.getSelection().toString() != '') {
    //rangy.getSelection().expand("word", { trim: true });  
  }
  
  var selection = window.getSelection().toString();
  if (selection == '')
    return;

  //clear selection
  this.SelectionModel.clearSelection();
  var paraId = this.inferParagraphId($event);
  if (paraId == null)
    return;
  //save selection
  this.SelectionModel.saveSelection(paraId, selection);

  if (ToolbarModel.trainerToolbar.isTrainerMode) {
    this.handleTrainerTextSelection(paraId, selection);
  }
}


ViewPortCtrl.prototype.paraClicked = function($event) {
  console.log("Paragraph clicked");
  //find out if this is a selection
  var selection = window.getSelection().toString();
  //check to see if mouseup should handle it
  if (selection != '')
    return;
  //clear highlight
  this.SelectionModel.clearSelection();
  var paraId = this.inferParagraphId($event);
  if (paraId == null)
    return;

  //store in SelectionModel
  this.SelectionModel.paragraphId = paraId;
  //highlight paragraph
  this.highlightParagraph(paraId);
  this.loadParagraphJson(paraId);

  if (ToolbarModel.trainerToolbar.isTrainerMode) {
    this.handleTrainerParaSelection(paraId);
  }

}

ViewPortCtrl.prototype.loadParagraphJson = function(paraId) {
  this.SelectionModel.paragraphId = paraId;
  this.documentService
    .getParagraphJson(paraId)
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

    if ((this.SelectionModel.mouseDownParaId != null) && (this.SelectionModel.mouseDownParaId != '')) {
      return this.SelectionModel.mouseDownParaId;
    }

    if ((children.length == 0) && (parents.length ==1)) {
      return $(parents[0]).attr('id');
    }

    return null;
  }

}

/**
* Handles text selection when user is in training mode
*
**/
ViewPortCtrl.prototype.handleTrainerTextSelection = function(paraId,
  selectedText) {
  console.log(selectedText);
  var text = "Is %s a %s";
  var prompt = '';
  //find a matching term
  var matchedItem = _.find(this.LHSModel.smodel.terms, function(obj){
    return ((obj.paragraphId == paraId) && (obj.term == selectedText));
  });
  // class question if no matching term found
  if (matchedItem == null) {
    prompt = 'Please choose the class for this [' + selectedText + ']';
    //show set of questions for classes
    var items = LHSModel.getClassNames();
    //create a new matched item
    matchedItem = { paragraphId: paraId, term: selectedText, classificationId: ''};
    console.log(matchedItem);
    this.showYesNoDialog(prompt, items).then(angular.bind(this, function(clicked) {
      matchedItem.classificationId = clicked;
      documentModel.isProcessing = true;
      this.documentService.addTermToPara(matchedItem).
      then(angular.bind(this, function(contentHtml){
        this.updateDocument(contentHtml);  
      }));
    }));

  } else {
    // yes / no question if matching found
    var className = LHSModel.getClassFromId(matchedItem.classificationId).name;
    prompt = s.sprintf(text, selectedText, className);
    this.showYesNoAllDialog(prompt, matchedItem);
  }
}

/**
* Handles a paragraph selection when user is in training mode
*
**/
ViewPortCtrl.prototype.handleTrainerParaSelection = function(paraId) {
  var text = "Is this paragraph a %s";
  var prompt = '';
  //find if any term matches
  var matchedItem = _.find(this.LHSModel.smodel.terms, function(obj){
    return ((obj.paragraphId == paraId) && (obj.term));
  });

  if (matchedItem != null) {
    //yes-no question because there is a term match
    var className = LHSModel.getClassFromId(matchedItem.classificationId).name;
    prompt = s.sprintf(text, className);
    this.showYesNoAllDialog(prompt, matchedItem);
  }

}

/**
* Updates the viewport with fresh content html
*/
ViewPortCtrl.prototype.updateDocument = function(contentHtml) {
  //$("#content").html(contentHtml);
  var self = this;
  this.documentService.getTerms().then(function(terms){
    LHSModel.setTerms(terms);
    console.log("Terms return by API");
    console.log(terms);
    documentModel.isProcessing = false;
    //fetch score
    self.ToolbarModel.updateBenchmark(self.documentService);
  }, function(data, status){
    console.log(status);
  });

}

/**
* Shows a "Yes, No, Yes to all" dialog
*/
ViewPortCtrl.prototype.showYesNoAllDialog = function(prompt, matchedItem) {
  var className = LHSModel.getClassFromId(matchedItem.classificationId).name;
  //create a set of questions. In this case, yes or no
  var items = ['Yes', 'No', 'Yes to all ' + className];
  this.showYesNoDialog(prompt, items).then(angular.bind(this, function(clicked) {
    documentModel.isProcessing = true;
    if (clicked == 1) {
      this.documentService.rejectClassFromPara(matchedItem.classificationId, matchedItem.paragraphId).
      then(angular.bind(this, function(contentHtml){
        this.updateDocument(contentHtml);  
      }));
    }
    //answer is yes
    if (clicked == 0) {
      this.documentService.approveClassForPara(matchedItem.classificationId, matchedItem.paragraphId).
      then(angular.bind(this, function(contentHtml){
        this.updateDocument(contentHtml);  
      }));
    }
  }));
}

/**
* Shows a question bottom sheet
*
**/
ViewPortCtrl.prototype.showYesNoDialog = function(text, items) {
  this.ToolbarModel.trainerPrompt.text = text;
  this.ToolbarModel.trainerPrompt.items = items;
  //there are two types of bottom sheet
  //true or false ; or item selection
  return this.$mdBottomSheet.show({
    templateUrl: 'partials/viewport-bottom-sheet.tmpl.html',
    controller: 'TrainerPromptCtrl',
    parent: angular.element(":root")

  });
}

angular.module('SkrollApp').controller('TrainerPromptCtrl',function($scope,
  ToolbarModel, $mdBottomSheet, documentService) {
  $scope.prompt = ToolbarModel.trainerPrompt.text;
  $scope.items = ToolbarModel.trainerPrompt.items;
  $scope.documentService = documentService;

  $scope.itemClicked = function($index) {
    $mdBottomSheet.hide($index);
  }
});

// ViewPortCtrl.prototype.saveSelection = function(paraId, selectedText) {
//   this.SelectionModel.paragraphId = paraId;
//   this.SelectionModel.selectedText = selectedText;
//   this.SelectionModel.serializedSelection = rangy.serializeSelection(rangy.getSelection($("#content").get(0)), false, $("#content").get(0));
//   console.log(this.SelectionModel.serializedSelection);
//   console.log(encodeURIComponent(encodeURIComponent(this.SelectionModel.serializedSelection)));
//   this.SelectionModel.serializedParagraphId = paraId;
//   this.LHSModel.addBookmark(7, paraId, selectedText.substring(0,10), rangy.serializeSelection(window, $("#content")));
// }

// ViewPortCtrl.prototype.clearSelection = function() {
//   //clear highlight
//   if (this.SelectionModel.paragraphId != '') {
//     this.removeHighlightParagraph(this.SelectionModel.paragraphId);
//   }
//   this.SelectionModel.paragraphId = '';
//   this.SelectionModel.selectedText = '';
// }

angular.module('SkrollApp')
  .controller('ViewPortCtrl', ['SelectionModel', 'documentService',
    '$mdBottomSheet', 'ToolbarModel', 'LHSModel', '$log', '$routeParams', ViewPortCtrl
  ]);
