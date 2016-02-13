(function() {
  'use strict';

  angular
    .module('app.link')
    .service('linkService', LinkService);

  /* @ngInject */
  function LinkService($log, $q, featureFlags, viewportService, documentModel, $animateCss) {

    //-- private variables
    //context root of API
    var baseUrl = 'http://skroll.io';


    //-- service definition
    var service = {

      getActiveLink: getActiveLink,
      shortenLink: shortenLink,
      onMouseEnter: onMouseEnter
    };

    return service;

    //////////////

    /**
    * Callback when mouse enters a new paragraph
    **/
    function onMouseEnter(event) {
      var element = angular.element('#skFabMenu');
      if (event.command == 'display') {
        var top = event.clientY + viewportService.viewportOffset.top;
        var left = getRightPositionOfFab();
        //hide
        hide(element)
        .then(move(element, top, left))
        .then(display(element));

      } else if (event.command == 'hide') {
        hide(element);
      }
    }

    function getLeftPositionOfFab() {
      //if leftcurtain is great than 0, then between the edge
      //otherwise on the document
      var fabWidth = 52;
      var leftEdge = viewportService.getLeftEdgeOfDocument();
      var horizontalLocation = 0;
      if (leftEdge > 0) {
        horizontalLocation = leftEdge - fabWidth/2;
      } else {
        horizontalLocation = leftEdge ;
      }
      return horizontalLocation;
    }


    function getRightPositionOfFab() {
      //if leftcurtain is great than 0, then between the edge
      //otherwise on the document
      var fabWidth = 52;
      var rightEdge = viewportService.getRightEdgeOfDocument();
      var horizontalLocation = 0;

      if (rightEdge > viewportService.getContentSize()) {
        horizontalLocation = rightEdge - fabWidth/2;
      } else {
        horizontalLocation = rightEdge - fabWidth ;
      }
      return horizontalLocation;
    }



    function hide(fabElement) {
      return $animateCss(fabElement, {
        duration: 0,
        from: {
          transitionDelay: 0,
          opacity: 0
        },
        to: {
          opacity: 0
        },
      }).start();
    }

    function move(fabElement, top, left) {
      //move
      return $animateCss(fabElement, {
        duration: 0,
        from: {
          // transitionDelay: '2s'
        },
        to: {
          top: top + 'px',
          left: left + 'px',

        },
      }).start();
    }


    function display(fabElement) {
      //show
      return $animateCss(fabElement, {
        // duration: 1,
        from: {
          transitionDelay: '2s',
        },
        to: {
          opacity: 0.5
        },
        easing: 'cubic-bezier(.14,1.09,.58,.9)',
        // duration: 2.3,

      }).start();
    }

    /**
     * Returns active link in the browser
     **/
    function getActiveLink(documentId, serializedSelection) {
      var activeLink = baseUrl + '/view/docId/' + documentId;
      if (serializedSelection != null) {
        activeLink = activeLink + '/linkId/' + serializedSelection;
      }
      return activeLink;
    };

    /**
     * Shortens url
     **/
    function shortenLink(url) {

      if (featureFlags.isOn('googl.shortlink')) {
        gapi.client.setApiKey('AIzaSyAFAxvkl4fQRr7WfWKHQuFKIFvg0oiuZN8');
        return gapi.client.request({
          'path': 'urlshortener/v1/url',
          'method': 'POST',
          'body': {
            'longUrl': url
          }
        });

      } else {
        var response = {
          result: {
            id: 'https://goo.gl/53se3'
          }
        };
        var deferred = $q.defer();
        deferred.resolve(response);
        return deferred.promise;
      }
    };
  };

  angular
    .module('app.link')
    .run(function(mouseEnterObserverService, linkService) {
      mouseEnterObserverService.register(linkService.onMouseEnter);
    });


})();