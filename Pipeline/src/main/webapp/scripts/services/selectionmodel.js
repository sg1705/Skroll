'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.SelectionModel
 * @description
 * # SelectionModel
 * Factory in the SkrollApp.
 */

var SelectionModel = {
	paragraphId: "",
	selectedText: "",
	mouseDownParaId: ""
};


angular.module('SkrollApp')
  .factory('SelectionModel', function () {
    return SelectionModel;
  });
