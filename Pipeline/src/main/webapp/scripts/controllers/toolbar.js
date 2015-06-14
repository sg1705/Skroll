'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ToolbarCtrl
 * @description
 * # ToolbarCtrl
 * Controller of the ToolbarCtrl
 */

var ToolbarCtrl = function(ToolbarModel, documentService, documentModel, SelectionModel, $mdSidenav) {
	this.trainerToolbar = ToolbarModel.trainerToolbar;
	this.toolbarInfo = ToolbarModel.toolbarInfo;
	this.documentService = documentService;
	this.SelectionModel = SelectionModel;
	this.documentModel = documentModel;
	this.$mdSidenav = $mdSidenav;

	var self = this;
	//load google chart
	//google.load("visualization", "1", {packages:["corechart"]});
}

ToolbarCtrl.prototype.convertToBenchmark = function() {
	console.log("need to call save benchmark");
	this.documentModel.isProcessing = true;
	this.documentService.saveAsBenchmark().then(function() {
		console.log("successfully saved as benchmark");
		documentModel.isProcessing = false;
		self.trainerToolbar.isBenchmarkMode = true;
	}, function(data, status) {
		console.log(status);
	});
	this.documentModel.isProcessing = false;
}



ToolbarCtrl.prototype.toggleTrainerMode = function() {
	ToolbarModel.trainerToolbar.isTrainerMode = !ToolbarModel.trainerToolbar.isTrainerMode;
}

ToolbarCtrl.prototype.toggleUpdateBNI = function() {
	// ToolbarModel.trainerToolbar.isUpdateBNI = !ToolbarModel.trainerToolbar.isUpdateBNI;
	// this.documentService.setFlags("ENABLE_UPDATE_BNI", ToolbarModel.trainerToolbar.isUpdateBNI);
	rangy.deserializeSelection(this.SelectionModel.serializedSelection);
	this.SelectionModel.scrollToParagraph(this.SelectionModel.serializedParagraphId);
}

ToolbarCtrl.prototype.observeNone = function() {
	console.log("observing none");
	this.documentService.observeNone();
}

ToolbarCtrl.prototype.showProbabilityDump = function() {
	$("#rightPane").html('<div id="graph_0"></div><div id="graph_1"></div>');
	this.documentService
		.getProbabilityDump()
		.then(angular.bind(this, function(data) {
			console.log(data);
			for (var kk = 0; kk < data.length; kk++) {
				//get list of probabilities
				var probs = data[kk];
				//covert data to chartData
				var convertedTable = [];
				convertedTable.push(['Para', 'Probability']);
				for (var ii = 0; ii < probs.length; ii++) {
					var tuple = [];
					tuple.push('' + ii);
					tuple.push(probs[ii])
					convertedTable.push(tuple);
				}
				var chartData = google.visualization.arrayToDataTable(convertedTable);
				var options = {
					title: 'Probability Distribution for ' + kk,
					legend: {
						position: 'center',
						alignment: 'center'
					},
					colors: ['green'],
					histogram: {
						bucketSize: 0.3
					}
				};
				console.log(kk);
				var chart = new google.visualization.Histogram(document.getElementById(
					'graph_' + kk));
				chart.draw(chartData, options);
			}
			this.$mdSidenav('right').toggle();
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

google.load("visualization", "1", {
	packages: ["corechart"]
});