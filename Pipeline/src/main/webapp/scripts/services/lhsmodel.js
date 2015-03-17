'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.LHSModel
 * @description
 * # LHSModel
 * Factory in the SkrollApp.
 */

var LHSModel = {
	
	sections : [ {
		name: 'DEFINITIONS',
		items: [{itemId: 'p_123', text: 'hey there'}],
		isSelected: true
	},
	{
		name: 'TABLE OF CONTENTS',
		items: [{itemId: 'p_123', text: 'hey there'}],
		isSelected: false
	}]

};


angular.module('SkrollApp')
  .factory('LHSModel', function () {
    return LHSModel;
  });
