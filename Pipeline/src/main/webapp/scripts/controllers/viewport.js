'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ViewPortCtrl
 * @description
 * # ViewPortCtrl
 * Controller of the ViewPortCtrl
 */

var ViewPortCtrl = function(SelectionModel, documentService, $mdBottomSheet,
  ToolbarModel, LHSModel, documentModel) {
  this.SelectionModel = SelectionModel;
  this.documentService = documentService;
  this.$mdBottomSheet = $mdBottomSheet;
  this.ToolbarModel = ToolbarModel;
  this.LHSModel = LHSModel;
  this.documentModel = documentModel;
}

ViewPortCtrl.prototype.mouseUp = function($event) {
  //should mouse click handle it
  //find out if this is a selection
  var selection = window.getSelection().toString();
  if (selection == '')
    return;

  //clear selection
  this.clearSelection();
  var paraId = this.inferParagraphId($event);

  //save selection
  this.saveSelection(paraId, selection);

  if (ToolbarModel.trainerToolbar.isTrainerMode) {
    this.handleTrainerTextSelection(paraId, selection);
  }
}


ViewPortCtrl.prototype.paraClicked = function($event) {
  //find out if this is a selection
  var selection = window.getSelection().toString();
  //check to see if mouseup should handle it
  if (selection != '')
    return;
  //clear highlight
  this.clearSelection();
  var paraId = this.inferParagraphId($event);
  if (paraId == null)
    return;

  //store in SelectionModel
  this.SelectionModel.paragraphId = paraId;
  //highlight paragraph
  this.highlightParagraph(paraId);


  this.documentService
    .getParagraphJson(paraId)
    .then(function(data) {
      $("#rightPane").html(JSON.stringify(data, null, 2));
    }, function(data, status) {
      console.log(status);
    });

  if (ToolbarModel.trainerToolbar.isTrainerMode) {
    this.handleTrainerParaSelection(paraId);
  }

}

ViewPortCtrl.prototype.highlightParagraph = function(paraId) {
  $("#" + paraId).css("background-color", "yellow");
}

ViewPortCtrl.prototype.removeHighlightParagraph = function(paraId) {
  $("#" + paraId).css("background-color", "");
}

ViewPortCtrl.prototype.inferParagraphId = function($event) {

  var parents = $($event.target).parents("div[id^='p_']");
  for (var ii = 0; ii < parents.length; ii++) {
    console.log($(parents[ii]).attr('id'));
  }

  if (parents.length > 1) {
    return $(parents[0]).attr('id');
  } else {
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
    return ((obj.paragraphId == paraId) && (obj.term));
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
      var contentHtml = this.documentService.addTermToPara(matchedItem);
      this.updateDocument(contentHtml);
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

  if (matchedItem == null) {
    //class question when no match is found
    prompt = 'Please choose the class for this paragraph';
    //fetch classes
    var items = LHSModel.getClassNames();
    this.showYesNoDialog(prompt, items).then(function(clickedItem) {
      //resolve answer to a class
      var resolvedClass = LHSModel.getClassFromId(clickedItem);
      console.log(clickedItem);
    });

  } else {
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
  $("#content").html(contentHtml);
  this.documentService.getTerms().then(function(terms){
    LHSModel.smodel.terms = terms;
    console.log(terms);
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
    if (clicked == 1) {
      var contentHtml = this.documentService.rejectClassFromPara(matchedItem.classificationId, matchedItem.paragraphId);
      this.updateDocument(contentHtml);
    }
    //answer is yes
    if (clicked == 0) {
      var contentHtml = this.documentService.approveClassForPara(matchedItem.classificationId, matchedItem.paragraphId);
      this.updateDocument(contentHtml);
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
    controller: 'TrainerPromptCtrl'
  })
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

ViewPortCtrl.prototype.saveSelection = function(paraId, selectedText) {
  this.SelectionModel.paragraphId = paraId;
  this.SelectionModel.selectedText = selectedText;
}

ViewPortCtrl.prototype.clearSelection = function() {
  //clear highlight
  if (this.SelectionModel.paragraphId != '') {
    this.removeHighlightParagraph(this.SelectionModel.paragraphId);
  }
  this.SelectionModel.paragraphId = '';
  this.SelectionModel.selectedText = '';
}

angular.module('SkrollApp')
  .controller('ViewPortCtrl', ['SelectionModel', 'documentService',
    '$mdBottomSheet', 'ToolbarModel', 'LHSModel', ViewPortCtrl
  ]);