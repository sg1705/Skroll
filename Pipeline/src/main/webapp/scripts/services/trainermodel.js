'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.TrainerModel
 * @description
 * # TrainerModel
 * Factory in the SkrollApp.
 */

var ToolbarModel = {
	trainerToolbar: {
		isTrainerMode: true,
		isUpdateBNI: false,
		isBenchmark: false,
		isTrainModel: false,
		lastJson: "",
		lastSelectedParaId: '',
		level1TypeAError: 0,
		level1TypeBError: 0,
		level1QcScore: 0,
		level2TypeAError: 0,
		level2TypeBError: 0,
		level2QcScore: 0,
		benchmarkScore: ''
	},

	toolbarInfo: {
		title: ""
	},

	trainerPrompt: {
		text: '',
		items: []
	},

	enableBenchmarkMode: function() {
		this.trainerToolbar.isTrainerMode = false;
		this.benchmarkToolbar.isBenchMarkMode = true;
	},

	updateBenchmark: function(documentService) {
	    console.log("fetching score");
	    var self = this;
	    documentService.getBenchmarkScore().then(function(benchmarkScore){
	      self.trainerToolbar.benchmarkScore = benchmarkScore;
	      console.log(benchmarkScore);
	      self.trainerToolbar.level1TypeAError = benchmarkScore.qc.stats[1].type1Error;
	      self.trainerToolbar.level1TypeBError = benchmarkScore.qc.stats[1].type2Error;
	      self.trainerToolbar.level1QcScore = benchmarkScore.qc.stats[1].qcScore;
	      self.trainerToolbar.level2TypeAError = benchmarkScore.qc.stats[2].type1Error;
	      self.trainerToolbar.level2TypeBError = benchmarkScore.qc.stats[2].type2Error;
	      self.trainerToolbar.level2QcScore = benchmarkScore.qc.stats[2].qcScore;

	      if (benchmarkScore.isFileBenchmarked && !benchmarkScore.isFileTrained) {
	      	self.trainerToolbar.isBenchmark = true;
	      	self.trainerToolbar.isTrainModel = false;
	      } else if (benchmarkScore.isFileTrained) {
	      	self.trainerToolbar.isBenchmark = false;
	      	self.trainerToolbar.isTrainModel = true;
	      } else if (!benchmarkScore.isFileBenchmarked && !benchmarkScore.isFileTrained) {
	      	self.trainerToolbar.isBenchmark = true;
	      	self.trainerToolbar.isTrainModel = true;
	      }
	    });                
	}

};


angular.module('SkrollApp')
	.factory('ToolbarModel', ['documentService', function(documentService) {
		return ToolbarModel;
	}]);