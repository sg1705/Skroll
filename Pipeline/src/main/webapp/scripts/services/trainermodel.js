'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.TrainerModel
 * @description
 * # TrainerModel
 * Factory in the SkrollApp.
 */

var TrainerModel = {
	trainerToolbar : {
		isTrainerMode: false
	}
};


angular.module('SkrollApp')
  .factory('TrainerModel', function () {
    return TrainerModel;
  });
