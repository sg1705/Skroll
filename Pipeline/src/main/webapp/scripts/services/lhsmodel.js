'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.LHSModel
 * @description
 * # LHSModel
 * Factory in the SkrollApp.
 */

var LHSModel = {

	smodel: {
		terms: []
	},

	classes: [{
		id: 1,
		name: 'Definition',
		isSelected: true

	}, {
		id: 2,
		name: 'Table of Contents',
		isSelected: true
	}]

};

angular.module('SkrollApp')
  .factory('LHSModel', function () {
    return LHSModel;
  });
