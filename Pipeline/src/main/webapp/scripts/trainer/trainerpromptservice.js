(function() {
  'use strict';

  angular
    .module('app.trainer')
    .factory('trainerPromptService', TrainerPromptService);

  /* @ngInject */
  function TrainerPromptService(LHSModel, trainerService, documentModel, documentService, $mdBottomSheet, trainerModel) {

    //-- private variables
    var vm = this;

    //-- private methods
    vm.updateDocument     = updateDocument;
    vm.showYesNoAllDialog = showYesNoAllDialog;
    vm.showYesNoDialog    = showYesNoDialog;
    vm.trainerPrompt      = trainerModel.trainerPrompt;
    //-- service definition
    var service = {
      handleTrainerTextSelection  : handleTrainerTextSelection,
      handleTrainerParaSelection  : handleTrainerParaSelection
    };

    return service;

    //////////////

    /**
    * Handles text selection when user is in training mode
    *
    **/
    function handleTrainerTextSelection(data) {
      var paraId = data.paraId;
      var selectedText = data.selectedText;
      console.log(selectedText);
      trainerModel.trainerToolbar.lastJson = '';
      var text = "Is %s a %s";
      var prompt = '';
      //find a matching term
      var matchedItem = _.find(LHSModel.smodel.terms, function(obj){
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
        var self = this;
        vm.showYesNoDialog(prompt, items)
          .then(function(clicked) {
                  matchedItem.classificationId = LHSModel.getClassFromIndex(clicked);
                  documentModel.isProcessing = true;
                  trainerService.addTermToPara(documentModel.documentId, matchedItem)
                  .then(function(contentHtml){
                          vm.updateDocument(contentHtml);  
                    });
          });

      } else {
        // yes / no question if matching found
        var className = LHSModel.getClassFromId(matchedItem.classificationId).name;
        prompt = s.sprintf(text, selectedText, className);
        vm.showYesNoAllDialog(prompt, matchedItem);
      }
    }

    /**
    * Handles a paragraph selection when user is in training mode
    *
    **/
    function handleTrainerParaSelection(paraId) {
      trainerModel.trainerToolbar.lastJson = '';
      var text = "Is this paragraph a %s";
      var prompt = '';
      //find if any term matches
      var matchedItem = _.find(LHSModel.smodel.terms, function(obj){
        return ((obj.paragraphId == paraId) && (obj.term));
      });

      if (matchedItem != null) {
        //yes-no question because there is a term match
        var className = LHSModel.getClassFromId(matchedItem.classificationId).name;
        prompt = s.sprintf(text, className);
        vm.showYesNoAllDialog(prompt, matchedItem);
      }

    }

    /**
    * Updates the viewport with fresh content html
    */
    function updateDocument(contentHtml) {
      //$("#content").html(contentHtml);
      var self = this;
      documentService.getTerms(documentModel.documentId).then(function(terms){
        LHSModel.setTerms(terms);
        console.log("Terms return by API");
        console.log(terms);
        documentModel.isProcessing = false;
        //fetch score
        trainerService.updateBenchmark();
        trainerService.fetchProbabilities(documentModel.documentId, terms);
      }, function(data, status){
        console.log(status);
      });

    }

    /**
    * Shows a "Yes, No, Yes to all" dialog
    */
    function showYesNoAllDialog(prompt, matchedItem) {
      var className = LHSModel.getClassFromId(matchedItem.classificationId).name;
      //create a set of questions. In this case, yes or no
      var items = ['Yes', 'No', 'Unobserve ' + className];
      vm.showYesNoDialog(prompt, items).then(angular.bind(this, function(clicked) {
        documentModel.isProcessing = true;
        if (clicked == 1) {
          trainerService.rejectClassFromPara(documentModel.documentId, matchedItem.classificationId, matchedItem.paragraphId).
          then(angular.bind(this, function(contentHtml){
            vm.updateDocument(contentHtml);  
          }));
        }
        //answer is yes
        if (clicked == 0) {
          trainerService.approveClassForPara(documentModel.documentId, matchedItem.classificationId, matchedItem.paragraphId).
          then(angular.bind(this, function(contentHtml){
            vm.updateDocument(contentHtml);  
          }));
        }
        //if answer is unobserve
        if (clicked == 2) {
          trainerService.unObservePara(documentModel.documentId, matchedItem.classificationId, matchedItem.paragraphId).
          then(angular.bind(this, function(contentHtml){
            vm.updateDocument(contentHtml);  
          }));
        }

      }));
    }

    /**
    * Shows a question bottom sheet
    *
    **/
    function showYesNoDialog(text, items) {
      vm.trainerPrompt.text = text;
      vm.trainerPrompt.items = items;

      //there are two types of bottom sheet
      //true or false ; or item selection
      return $mdBottomSheet.show({
        templateUrl: 'scripts/trainer/viewport-bottom-sheet.tmpl.html',
        controller: 'TrainerPromptCtrl',
        parent: angular.element(":root")

      });
    }
  }

})();