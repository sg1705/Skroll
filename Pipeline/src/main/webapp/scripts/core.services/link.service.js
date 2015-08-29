(function() {
  'use strict';

  angular
    .module('app.core.services')
    .service('linkService', LinkService);

  /** @ngInject **/
  function LinkService($log) {

    //-- private variables
    //context root of API
    var baseUrl   = 'http://k2.skroll.io:8088';


    //-- service definition
    var service = {
     
      getActiveLink     : getActiveLink,
      shortenLink       : shortenLink
    };

    return service;
    
    //////////////


    /**
    * Returns active link in the browser
    **/
    function getActiveLink(documentId, serializedSelection) {
      var activeLink = baseUrl + '/view/docId/' + documentId;
      activeLink = activeLink + '/linkId/' + serializedSelection;
      return activeLink;
    };

    /**
    * Shortens url
    **/
    function shortenLink(url) {
      gapi.client.setApiKey('AIzaSyAFAxvkl4fQRr7WfWKHQuFKIFvg0oiuZN8');
      return gapi.client.request({
        'path'     : 'urlshortener/v1/url',
        'method'   : 'POST',
        'body'     : {'longUrl'  : url}
      });
    };




  };
})();