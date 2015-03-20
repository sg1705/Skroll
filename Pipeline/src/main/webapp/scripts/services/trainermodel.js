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
		isTrainerMode: true
	},

	toolbarInfo : {
		title: ""		
	},

	trainerPrompt: {
		text: ''
	}

};


angular.module('SkrollApp')
  .factory('ToolbarModel', function () {
    return ToolbarModel;
  });
