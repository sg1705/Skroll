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
		isTrainerMode: false
	},

	toolbarInfo : {
		title: ""		
	}

};


angular.module('SkrollApp')
  .factory('ToolbarModel', function () {
    return ToolbarModel;
  });
