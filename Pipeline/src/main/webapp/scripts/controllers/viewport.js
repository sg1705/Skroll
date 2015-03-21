'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ViewPortCtrl
 * @description
 * # ViewPortCtrl
 * Controller of the ViewPortCtrl
 */

var ViewPortCtrl = function(SelectionModel, documentService, $mdBottomSheet,
  ToolbarModel, LHSModel) {
  this.SelectionModel = SelectionModel;
  this.documentService = documentService;
  this.$mdBottomSheet = $mdBottomSheet;
  this.ToolbarModel = ToolbarModel;
  this.LHSModel = LHSModel;
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

ViewPortCtrl.prototype.handleTrainerTextSelection = function(paraId,
  selectedText) {
  //find definitions for the given paraId
  //definitions is section[0]
  //find if the selection is a definition
  console.log(selectedText);
  var sections = this.LHSModel.sections;
  var text = "Is %s a %s";
  var prompt = '';
  var match = false;
  for (var ii = 0; ii < sections[0].items.length; ii++) {
    if (sections[0].items[ii].itemId == paraId) {
      //check for defintions
      if (sections[0].items[ii].text == selectedText) {
        //found an item
        var prompt = s.sprintf(text, selectedText, sections[0].name);
        match = true;
      }
    }
  }

  if (!match)
    return;

  this.showYesNoDialog(prompt).then(function(clicked) {
    if (clicked == 1) {
      LHSModel.sections[0].items = _.reject(LHSModel.sections[0].items, function(obj) {
        if ((obj.itemId == paraId) && (selectedText = obj.text ))
            return true;
      });
    }

  })
}

ViewPortCtrl.prototype.handleTrainerParaSelection = function(paraId) {
  //find definitions for the given paraId
  //definitions is section[0]
  //find if the selection is a definition
  var sections = this.LHSModel.sections;
  var text = "Is this paragraph a %s";
  var match = false;
  var prompt = '';
  for (var jj = 0; jj < sections.length; jj++) {
    for (var ii = 0; ii < sections[jj].items.length; ii++) {
      if (sections[jj].items[ii].itemId == paraId) {
        //check for defintions
        match = true;
        prompt = s.sprintf(text, sections[jj].name);
        console.log(prompt);
        break;
      }
    }
  }

  if (!match) {
    return;
  }

  this.showYesNoDialog(prompt).then(function(clicked) {
    if (clicked == 1) {
      LHSModel.sections[0].items = _.reject(LHSModel.sections[0].items, function(obj) {
        if (obj.itemId == paraId)
            return true;
      });
      
    }
    console.log(clicked);
  })
}

ViewPortCtrl.prototype.showYesNoDialog = function(text) {
  //create a set of questions. In this case, yes or no
  var items = ['Yes', 'No'];
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
  ToolbarModel, $mdBottomSheet) {
  $scope.prompt = ToolbarModel.trainerPrompt.text;
  $scope.items = ToolbarModel.trainerPrompt.items;

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