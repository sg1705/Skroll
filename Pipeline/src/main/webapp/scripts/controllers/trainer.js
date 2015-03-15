'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:TrainerCtrl
 * @description
 * # TrainerCtrl
 * Controller of the TrainerCtrl
 */

var TrainerCtrl = function (TrainerModel) {
	this.trainerToolbar = TrainerModel.trainerToolbar;
}

TrainerCtrl.prototype.toggleTrainerMode = function() {
	TrainerModel.trainerToolbar.isTrainerMode = !TrainerModel.trainerToolbar.isTrainerMode;
}

angular.module('SkrollApp')
	.controller('TrainerCtrl', TrainerCtrl);
