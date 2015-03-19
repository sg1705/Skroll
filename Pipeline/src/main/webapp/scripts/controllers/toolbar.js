'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ToolbarCtrl
 * @description
 * # ToolbarCtrl
 * Controller of the ToolbarCtrl
 */

var ToolbarCtrl = function (ToolbarModel) {
	this.trainerToolbar = ToolbarModel.trainerToolbar;
	this.toolbarInfo = ToolbarModel.toolbarInfo;
}

ToolbarCtrl.prototype.toggleTrainerMode = function() {
	ToolbarModel.trainerToolbar.isTrainerMode = !ToolbarModel.trainerToolbar.isTrainerMode;
}

angular.module('SkrollApp')
	.controller('ToolbarCtrl', ToolbarCtrl);
