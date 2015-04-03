'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ToolbarCtrl
 * @description
 * # ToolbarCtrl
 * Controller of the ToolbarCtrl
 */

var ToolbarCtrl = function (ToolbarModel, documentService, SelectionModel,$mdSidenav) {
	this.trainerToolbar = ToolbarModel.trainerToolbar;
	this.toolbarInfo = ToolbarModel.toolbarInfo;
	this.documentService = documentService;
	this.SelectionModel = SelectionModel;
	this.$mdSidenav = $mdSidenav;
}

ToolbarCtrl.prototype.toggleTrainerMode = function() {
	ToolbarModel.trainerToolbar.isTrainerMode = !ToolbarModel.trainerToolbar.isTrainerMode;
}

ToolbarCtrl.prototype.toggleUpdateBNI = function() {
	ToolbarModel.trainerToolbar.isUpdateBNI = !ToolbarModel.trainerToolbar.isUpdateBNI;
	this.documentService.setFlags("ENABLE_UPDATE_BNI", ToolbarModel.trainerToolbar.isUpdateBNI);
}



ToolbarCtrl.prototype.updateModelByTrainer = function() {
	this.documentService.updateModel();
}

ToolbarCtrl.prototype.showAnnotations = function() {
  var paraId = this.SelectionModel.paragraphId;
  this.documentService
    .getParagraphJson(paraId)
    .then(angular.bind(this, function(result) {
      var oldJson = this.trainerToolbar.lastJson;
      var newJson = JSON.stringify(result, null, 2);
      var ds;
      if ((oldJson != '') && (paraId == this.trainerToolbar.lastSelectedParaId)) {
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
        this.trainerToolbar.lastJson = newJson;
        this.trainerToolbar.lastSelectedParaId = paraId;
        ds = newJson;
      }
      $("#rightPane").html(ds);
      this.$mdSidenav('right').toggle();
    }), function(data, status) {
      console.log(status);
    });
}

angular.module('SkrollApp')
	.controller('ToolbarCtrl', ToolbarCtrl);
