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
		lastJson: "",
		lastSelectedParaId: '',
		typeAError: 0,
		typeBError: 0,
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
	      self.trainerToolbar.typeAError = benchmarkScore.qc.stats[1].type1Error;
	      self.trainerToolbar.typeBError = benchmarkScore.qc.stats[1].type2Error;
	    });                
	}

};


angular.module('SkrollApp')
	.factory('ToolbarModel', ['documentService', function(documentService) {
		return ToolbarModel;
	}]);