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
		typeBError: 0
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
	.factory('ToolbarModel', function() {
		return ToolbarModel;
	});