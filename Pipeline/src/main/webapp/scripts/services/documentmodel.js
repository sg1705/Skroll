'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.DocumentModel
 * @description
 * # DocumentModel
 * Factory in the SkrollApp.
 */

var documentModel = {
  targetHtml: '',
  isDocAvailable: false,
  isProcessing: false,
  fileName: '',
  selectedParagraphId: '',
  documentId: '',
  isPartiallyParsed: false,
  url: '',
  lunrIndex: null,
  isTocLoaded: false,

  clearToc: function() {
    this.isTocLoaded = false;
  },

  reset: function() {
    this.targetHtml = '';
    this.isDocAvailable = false;
    this.isProcessing = false;
    this.fileName = '';
    this.selectedParagraphId = '';
    this.documentId = '';
    this.isPartiallyParsed = false;
    this.url = '';
    this.lunrIndex = null;
    this.isTocLoaded = false;
  }

};


angular
  .module('SkrollApp')
  .factory('documentModel', function() {
    return documentModel;
  });