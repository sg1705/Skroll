(function() {
  /**
   * @ngdoc directive
   * @name SkrollApp.directive:skObserveScroll
   * @description Observes for scrolling event
   * # skObserveScroll
   */
  angular
    .module('app.core.services')
    .directive('skObserveScroll', skObserverScroll);

  /* @ngInject */
  function skObserverScroll($document, $interval, $window, LHSModel, scrollObserverService) {

    var directive = {
      restricted: 'A',
      link: link
    }

    return directive;

    ////////////

    function Scroll(event) {
      this.x = $window.pageXOffset !== undefined ? $window.pageXOffset : ($document[0].documentElement || $document[0].body.parentNode || $document[0].body).scrollLeft,
      this.y = $window.pageYOffset !== undefined ? $window.pageYOffset : ($document[0].documentElement || $document[0].body.parentNode || $document[0].body).scrollTop,
      this.type = event.type || event.name,
      this.timestamp = event.timeStamp || Date.now(),
      this.timelapse = this.prev ? this.timestamp - this.prev.timestamp : 0,
      this.distanceX = this.x - (this.prev ? this.prev.x : 0),
      this.distanceY = this.y - (this.prev ? this.prev.y : 0),
      this.velocityX = this.distanceX / (this.timestamp - (this.prev ? this.prev.timestamp : 0)),
      this.velocityY = this.distanceY / (this.timestamp - (this.prev ? this.prev.timestamp : 0)),
      this.directionX = this.distanceX < 0 ? 'left' : this.distanceX > 0 ? 'right' : false,
      this.directionY = this.distanceY < 0 ? 'up' : this.distanceY > 0 ? 'down' : false,
      this.scrollHeight = ($document[0].body.scrollHeight || $document[0].documentElement.scrollHeight) - $window.innerHeight,
      this.event = event || false;
    };



    function link(scope, element, attrs) {
      var _this = this;
      var scrollThrottle = 100;
      var didScroll = false;
      var timer = false;
      element.bind('scroll', function(event) {
        var currentY = $(element).offset().top + 200;
        if (!didScroll) {
          timer = $interval(function() {
            if (didScroll) {
              didScroll = false;
              $interval.cancel(timer);
              var data = new Scroll(event);
            }
          }, scrollThrottle);
        };
        //figure out paraid
        var headerPara = '';
        var terms = LHSModel.smodel.terms;
        //calculate offsets for headers
        //iterate over each term to find Y offset
        var offsets = _.map(terms, function(term) {
          return $("#" + term.paragraphId).offset().top;
        });

        for (var ii = 0; ii < terms.length; ii++) {
          if (offsets[ii] == currentY) {
            //found it
            headerPara = terms[ii].paragraphId;
            break;
          }

          if (offsets[ii] > currentY) {
            break;
          }
          headerPara = terms[ii].paragraphId;
        }
        LHSModel.smodel.visibleHeaders = [headerPara];
        scrollObserverService.notify(headerPara);
        didScroll = true;
      });
    }
  }

})();

// 'use strict';

// /**
//  * @ngdoc directive
//  * @name SkrollApp.directive:skObserveScroll
//  * @description Observes for scrolling event
//  * # skObserveScroll
//  */
// angular.module('SkrollApp')
//   .directive('skObserveScroll', [ '$document', '$interval', '$window', 'LHSModel', 'scrollObserverService',
//     function($document, $interval, $window, LHSModel, scrollObserverService) {

//       function Scroll(event) {
//         this.x = $window.pageXOffset !== undefined ? $window.pageXOffset : ($document[0].documentElement || $document[0].body.parentNode || $document[0].body).scrollLeft,
//         this.y = $window.pageYOffset !== undefined ? $window.pageYOffset : ($document[0].documentElement || $document[0].body.parentNode || $document[0].body).scrollTop,
//         this.type = event.type || event.name,
//         this.timestamp = event.timeStamp || Date.now(),
//         this.timelapse = this.prev ? this.timestamp - this.prev.timestamp : 0,
//         this.distanceX = this.x - (this.prev ? this.prev.x : 0),
//         this.distanceY = this.y - (this.prev ? this.prev.y : 0),
//         this.velocityX = this.distanceX / (this.timestamp - (this.prev ? this.prev.timestamp : 0)),
//         this.velocityY = this.distanceY / (this.timestamp - (this.prev ? this.prev.timestamp : 0)),
//         this.directionX = this.distanceX < 0 ? 'left' : this.distanceX > 0 ? 'right' : false,
//         this.directionY = this.distanceY < 0 ? 'up' : this.distanceY > 0 ? 'down' : false,
//         this.scrollHeight = ($document[0].body.scrollHeight || $document[0].documentElement.scrollHeight) - $window.innerHeight,
//         this.event = event || false;
//       };

//       return {
//         restricted: 'A',
//         link: function(scope, element, attrs) {
//           var _this = this;
//           var scrollThrottle = 3;
//           var didScroll = false;
//           var timer = false;
//           element.bind('scroll', function(event) {
//             var currentY = $(element).offset().top + 200;
//             if (!didScroll) {
//               timer = $interval(function() {
//                 if (didScroll) {
//                   didScroll = false;
//                   $interval.cancel(timer);
//                   var data = new Scroll(event);
//                 }
//               }, scrollThrottle);
//             };
//             //figure out paraid
//             var headerPara = '';
//             var terms = LHSModel.smodel.terms;
//             //calculate offsets for headers
//             //iterate over each term to find Y offset
//             var offsets = _.map(terms, function(term) {
//               return  $("#"+term.paragraphId).offset().top;
//             });

//             for(var ii = 0; ii < terms.length; ii++) {
//               if (offsets[ii] == currentY) {
//                 //found it
//                 headerPara = terms[ii].paragraphId;
//                 break;
//               }

//               if (offsets[ii] > currentY) {
//                 break;
//               }
//               headerPara = terms[ii].paragraphId;
//             }
//             LHSModel.smodel.visibleHeaders = [headerPara];
//             console.log(LHSModel.smodel.visibleHeaders);
//             scrollObserverService.notify(headerPara);
//             didScroll = true;
//           });
//         }
//       }
//     }
//   ]);