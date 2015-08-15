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
	}

};


angular.module('SkrollApp')
	.factory('ToolbarModel', ['documentService', function(documentService) {
		return ToolbarModel;
	}]);