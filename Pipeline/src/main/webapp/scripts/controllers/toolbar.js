'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ToolbarCtrl
 * @description
 * # ToolbarCtrl
 * Controller of the ToolbarCtrl
 */

var ToolbarCtrl = function (ToolbarModel, documentService) {
	this.trainerToolbar = ToolbarModel.trainerToolbar;
	this.toolbarInfo = ToolbarModel.toolbarInfo;
	this.documentService = documentService;
}

ToolbarCtrl.prototype.toggleTrainerMode = function() {
	ToolbarModel.trainerToolbar.isTrainerMode = !ToolbarModel.trainerToolbar.isTrainerMode;
}

ToolbarCtrl.prototype.updateModelByTrainer = function() {
	this.documentService.updateModel();
}


angular.module('SkrollApp')
	.controller('ToolbarCtrl', ToolbarCtrl);
