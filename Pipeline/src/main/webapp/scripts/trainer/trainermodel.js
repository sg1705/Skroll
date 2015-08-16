(function(){

	'use strict';

	/**
	 * @ngdoc service
	 * @name SkrollApp.TrainerModel
	 * @description
	 * # TrainerModel
	 * Factory in the SkrollApp.
	 */

	angular
		.module('app.trainer')
		.factory('trainerModel', TrainerModel);

	/* @ngInject */
	function TrainerModel(documentService) {

		//-- private variables
		var vm = this;
		var trainerToolbar = {
			isTrainerMode 	: true,
			// isUpdateBNI			: false,
			isBenchmark 		: false,
			isTrainModel 		: false,
			lastJson 				: "",
			lastSelectedParaId: '',
			level1TypeAError: 0,
			level1TypeBError: 0,
			level1QcScore 	: 0,
			level2TypeAError: 0,
			level2TypeBError: 0,
			level2QcScore 	: 0,
			benchmarkScore 	: ''
		};

		var	toolbarInfo = {
			title: ""
		}

		var trainerPrompt = {
			text: '',
			items: []
		}

		//-- public methods
		var service = {
			trainerToolbar 	: trainerToolbar,
			toolbarInfo 		: toolbarInfo,
			trainerPrompt 	: trainerPrompt
		}

		return service;

	}


})();