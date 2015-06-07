'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.TrainerModel
 * @description
 * # TrainerModel
 * Factory in the SkrollApp.
 */

var ToolbarModel = {
	trainerToolbar : {
		isTrainerMode: true,
		isUpdateBNI: false,
		lastJson: "",
		lastSelectedParaId: ''
	},

	toolbarInfo : {
		title: ""		
	},

	trainerPrompt: {
		text: '',
		items: []
	}

};


angular.module('SkrollApp')
  .factory('ToolbarModel', function () {
    return ToolbarModel;
  });
