'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.DocumentModel
 * @description
 * # DocumentModel
 * Factory in the SkrollApp.
 */

var documentModel = {
  isDocAvailable: false,
  isProcessing: false,
  isTocLoaded: false,

  documentId: '',
  docTypeId: 0,
  url: '',
  isPartiallyParsed: false,
  targetHtml: '',
  lunrIndex: null,

  documentProto: {
    id: null,
    typeId: null,
    format: null,
    url: null,
    isPartiallyParsed: false,
  },

  contentProto: {
    content: ''
  },

  indexProto: {
    index: ''
  },

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
    this.docTypeId = 0;
    this.docTypeName = '';
  }

};


angular
  .module('SkrollApp')
  .factory('documentModel', function() {
    return documentModel;
  });