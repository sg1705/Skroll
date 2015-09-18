'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.DocumentModel
 * @description
 * # DocumentModel
 * Factory in the SkrollApp.
 */

var documentModel = {
  targetHtml					: "",
  isDocAvailable			: false,
  isProcessing				: false,
  fileName						: "",
  selectedParagraphId	: "",
  documentId					: "",
  isPartiallyParsed   : false,
  url                 : ""
};


angular
	.module('app.core')
  .factory('documentModel', function () {
    return documentModel;
  });

