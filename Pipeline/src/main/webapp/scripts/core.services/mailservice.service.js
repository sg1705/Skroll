(function() {
  'use strict';

  angular
    .module('app.core.services')
    .service('mailService', MailService);

  /* @ngInject */
  function MailService($http, $q, $log) {

    //-- private variables
    //context root of API
    var mailServiceBase = 'restServices/mail/';

    //-- service definition
    var service = {

      sendFeedback: sendFeedback
    };

    return service;

    //////////////


    /**
     * Returns a promise to send feedback email
     **/
    function sendFeedback(address, feedbackMessage, documentId, url) {
      $log.debug("Sending feedback");
      var feedback = { address: address, feedbackMessage: feedbackMessage, documentId: documentId, url: url, browser: navigator.userAgent };
      var deferred = $q.defer();
      $http.post(mailServiceBase + 'sendFeedback', feedback)
        .success(function() {
          deferred.resolve();
        })
        .error(function(msg, status) {
          deferred.reject(msg);
        })
      return deferred.promise;
    };



  };
})();