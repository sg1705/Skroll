(function(){
	'use strict';

	/**
	 * @ngdoc function
	 * @name skrollApp.controller:TrainerToolbarCtrl
	 * @description
	 * # TrainerToolbarCtrl
	 * Controller of the TrainerToolbarCtrl
	 */

	angular
		.module('app.trainer')
		.controller('TrainerToolbarCtrl', TrainerToolbarCtrl);

	/* @ngInject */
	function TrainerToolbarCtrl(
								documentService, 
								documentModel, 
								selectionService,
								$mdSidenav,
								trainerService,
								trainerModel,
								trainerPromptService,
								clickObserverService,
								textSelectionObserverService) {

		//-- private variables
		var vm = this;
		
		//-- public variables
		vm.trainerToolbar = trainerModel.trainerToolbar;

		//-- public methods
		vm.convertToBenchmark 	= convertToBenchmark;
		vm.toggleTrainerMode 		= toggleTrainerMode;
		vm.toggleUpdateBNI 			= toggleUpdateBNI;
		vm.observeNone					= observeNone;
		vm.showProbabilityDump	= showProbabilityDump;
		vm.updateModelByTrainer = updateModelByTrainer;
		vm.showAnnotations 			= showAnnotations;

		//-- register for click events
		clickObserverService.register(trainerPromptService.handleTrainerParaSelection);
		textSelectionObserverService.register(trainerPromptService.handleTrainerTextSelection);

		////////////////

		function convertToBenchmark() {
			documentModel.isProcessing = true;
			var self = this;
			documentService.saveAsBenchmark(documentModel.documentId).then(function() {
				documentModel.isProcessing = false;
				self.trainerToolbar.isBenchmark = true;
			}, function(data, status) {
				console.log(status);
			});
			trainerService.updateBenchmark();
			documentModel.isProcessing = false;
		}


		function toggleTrainerMode() {
			vm.trainerToolbar.isTrainerMode = !vm.trainerToolbar.isTrainerMode;
		}


		function toggleUpdateBNI() {
			rangy.deserializeSelection(selectionService.serializedSelection);
			selectionService.scrollToParagraph(selectionService.serializedParagraphId);
		}


		function observeNone() {
			console.log("observing none");
			trainerService.observeNone(documentModel.documentId);
		}


		function showProbabilityDump() {
			$("#rightPane").html('<div id="graph_0"></div><div id="graph_1"></div>');
			trainerService
				.getProbabilityDump(documentModel.documentId)
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
					$mdSidenav('left').toggle();
				}), function(data, status) {
					console.log(status);
				});
		}


		function updateModelByTrainer() {
			trainerService.updateModel(documentModel.documentId);
			trainerService.updateBenchmark();
		}


		function showAnnotations() {
			var paraId = selectionService.paragraphId;
			documentService
				.getParagraphJson(documentModel.documentId, paraId)
				.then(angular.bind(this, function(result) {
					var oldJson = vm.trainerToolbar.lastJson;
					var newJson = JSON.stringify(result, null, 2);
					var ds;
					if ((oldJson != '') && (paraId == vm.trainerToolbar.lastSelectedParaId)) {
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
						vm.trainerToolbar.lastJson = newJson;
						vm.trainerToolbar.lastSelectedParaId = paraId;
						ds = newJson;
					}
					$("#rightPane").html(ds);
					$mdSidenav('left').toggle();
				}), function(data, status) {
					console.log(status);
				});
		}

	}

	google.load("visualization", "1", {
		packages: ["corechart"]
	});	
})();

