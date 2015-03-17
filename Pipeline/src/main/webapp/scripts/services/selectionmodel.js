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
	selectedText: ""
};


angular.module('SkrollApp')
  .factory('SelectionModel', function () {
    return SelectionModel;
  });
