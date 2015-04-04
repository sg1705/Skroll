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
	//load google chart
	//google.load("visualization", "1", {packages:["corechart"]});
}

ToolbarCtrl.prototype.toggleTrainerMode = function() {
	ToolbarModel.trainerToolbar.isTrainerMode = !ToolbarModel.trainerToolbar.isTrainerMode;
}

ToolbarCtrl.prototype.toggleUpdateBNI = function() {
	ToolbarModel.trainerToolbar.isUpdateBNI = !ToolbarModel.trainerToolbar.isUpdateBNI;
	this.documentService.setFlags("ENABLE_UPDATE_BNI", ToolbarModel.trainerToolbar.isUpdateBNI);
}

ToolbarCtrl.prototype.observeNone = function() {
	console.log("observing none");
	this.documentService.observeNone();
}

ToolbarCtrl.prototype.showProbabilityDump = function() {
  this.documentService
    .getProbabilityDump()
    .then(angular.bind(this, function(data) {
    	//covert data to chartData
    	var convertedTable = [ ];
    	convertedTable.push(['Para', 'Probability']);
    	for(var ii = 0; ii < data.length; ii++) {
    		var tuple = [];
    		tuple.push(''+ii);
    		tuple.push(data[ii])
    		convertedTable.push(tuple);
    	}
    	console.log(convertedTable);
      $("#rightPane").html('<pre>' + JSON.stringify(data, null, 2) + '</pre>');
      this.$mdSidenav('right').toggle();
      var chartData = google.visualization.arrayToDataTable(convertedTable);
      var options = {
	    title: 'Probability Distribution for Definitions',
	    legend: { position: 'center', alignment : 'center' },
	    colors: ['green'],
	    histogram: { bucketSize: 0.3 }
	  };
	  var chart = new google.visualization.Histogram(document.getElementById('rightPane'));
	  chart.draw(chartData, options);
    }), function(data, status) {
      console.log(status);
    });
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

google.load("visualization", "1", {packages:["corechart"]});