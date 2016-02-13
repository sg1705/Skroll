(function() {

  'use strict';

  /**
   * @ngdoc service
   * @name SkrollApp.viewPortService
   * @description
   * # viewPortService
   * Factory that represents viewPortService in SkrollApp.
   */

  angular
    .module('app.core.services')
    .factory('viewportService', ViewPortService);

  /* @ngInject */
  function ViewPortService() {

    //-- private variables

    //contains - offsetTop and offsetLeft
    var viewportOffset;
    var contentOffsetLeft;
    var leftCurtainWidth;
    var rightCurtainWidth;
    var contentWidth;
    var paddingLeft;
    var paddingRight;

    //-- service definition
    var service = {

      //-- service variables
      viewportOffset: viewportOffset,
      contentOffsetLeft: contentOffsetLeft,
      leftCurtainWidth: leftCurtainWidth,
      content: {
        width: contentWidth,
        paddingLeft: paddingLeft,
        paddingRight: paddingRight
      },
      rightCurtainWidth: rightCurtainWidth,


      //-- service functions
      getLeftEdgeOfDocument: getLeftEdgeOfDocument,
      getRightEdgeOfDocument: getRightEdgeOfDocument,
      getContentSize: getContentSize
    }

    return service;

    //-- methods

    function getLeftEdgeOfDocument() {
      return service.viewportOffset.left + service.leftCurtainWidth;
    }

    function getRightEdgeOfDocument() {
      return service.viewportOffset.left + service.leftCurtainWidth + service.getContentSize();
    }

    function getContentSize() {
     return (service.content.paddingLeft + service.content.width + service.content.paddingRight);
    }

  };

})();