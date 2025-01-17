'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.DocumentModel
 * @description
 * # DocumentModel
 * Factory in the SkrollApp.
 */

var documentModel = {

  viewState: {
    isDocAvailable: false,
    isProcessing: false,
    isTocLoaded: false,
    fabMenu: {
      currentParaId: '',
      currentParaZone: null
    }
  },

  documentId: '',
  docTypeId: 0,
  format: 0,
  url: '',
  isPartiallyParsed: false,

  p: {
    content: '',
  },

  lunrIndex: null,

  clearToc: function() {
    this.viewState.isTocLoaded = false;
  },

  reset: function() {

    this.p.content = '';

    this.documentId = '';
    this.isPartiallyParsed = false;
    this.url = '';
    this.lunrIndex = null;
    this.docTypeId = 0;

    this.resetViewState();
  },

  resetViewState: function() {
    this.viewState.isDocAvailable = false;
    this.viewState.isProcessing = false;
    this.viewState.isTocLoaded = false;

    this.fabMenu = {
      currentParaId: '',
      currentParaZone: null
    };
  },

  loadDocument: function(id, typeId, format, url, partiallyParsed, content) {
    this.documentId = id;
    this.docTypeId = typeId;
    this.format = format;
    this.url = url;
    if (partiallyParsed == 'true') {
      this.isPartiallyParsed = true;
    } else {
      this.isPartiallyParsed = false;
    }
    this.p.content = content;
  }

};

angular
  .module('SkrollApp')
  .factory('documentModel', function() {
    return documentModel;
  });