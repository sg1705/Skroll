'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.DocumentModel
 * @description
 * # DocumentModel
 * Factory in the SkrollApp.
 */

var documentModel = {
  targetHtml: "",
  isDocAvailable: false,
  isProcessing: false,
  fileName: "",
  selectedParagraphId: "",

  classes: [
  	{
  		id: 0,
  		name: 'Definition'
  	},
  	{
  		id: 1,
  		name: 'Table of Contents'
  	}
  ]
};


angular.module('SkrollApp')
  .factory('documentModel', function () {
    return documentModel;
  });

