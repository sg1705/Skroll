(function() {
  'use strict';

  angular
    .module('app.trainer')
    .service('trainerService', TrainerService);

  /** @ngInject **/

  function TrainerService($http, $q, $log, documentService) {

    //context root of API
    var documentServiceBase = 'restServices/doc/';
    var instrumentServiceBase = 'restServices/instrument/';



    //-- service definition
    var service = {
      
      rejectClassFromPara    : rejectClassFromPara,
      approveClassForPara    : approveClassForPara,
      unObservePara          : unObservePara,
      addTermToPara          : addTermToPara,
      updateModel            : updateModel,
      setFlags               : setFlags,
      observeNone            : observeNone,
      getProbabilityDump     : getProbabilityDump

    };

    return service;

    //////////////

    /**
    * Removes all instances of paraId from smodel.terms and updates it
    **/
    function rejectClassFromPara(documentId, classId, paraId) {
      //get a filtered list
      var terms = LHSModel.filterOutClassFromPara(classId, paraId);
      return documentService.updateTerms(documentId, terms);
    }

    /**
     * Approves all instances of terms in the paraId for the given class
     */
    function approveClassForPara(documentId, classId, paraId) {
      //get a filtered list
      var terms = LHSModel.getParagraphsForClass(classId, paraId);
      return documentService.updateTerms(documentId, terms);
    }

    /**
     * Approves all instances of terms in the paraId for the given class
     */
    function unObservePara(documentId, classId, paraId) {
      //get a filtered list
      var terms = LHSModel.filterOutClassFromPara(classId, paraId);
      return documentService.unObserve(documentId, terms);
    }


    /**
     * Approves all instances of terms in the paraId for the given class
     */
    function addTermToPara(documentId, item) {
      //get a filtered list
      var terms = LHSModel.getParagraphsForClass(item.classificationId, item.paragraphId);
      if (terms == null)
        terms = [];
      terms.push(item);
      return documentService.updateTerms(documentId, terms).then(function(data) {}, function(data, status) {
        console.log(status);
      });
    }

    /**
     * Update model for the given documentId
     */
    function updateModel(documentId) {
      /** make a get request */
      $http.get(documentServiceBase + 'updateModel' + '?documentId=' + documentId)
        .success(function(data, status) {
          console.log("Updated model:");
          console.log(data);
        })
        .error(function(data, status) {
          console.log(status);
        });;
    };


    /**
     * Sets a given flag
     */
    function setFlags(flagName, flagValue) {
      /** make a get request */
      $http.get(instrumentServiceBase + 'setFlags?flagName=' + flagName + '&flagValue=' + flagValue + '&documentId=' + documentModel.documentId)
        .success(function(data, status) {
          console.log("flag set");
          console.log(data);
        })
        .error(function(data, status) {
          console.log(status);
        });;
    };

    /**
     * Observe none
     */
    function observeNone(documentId) {
      /** make a get request */
      $http.get(documentServiceBase + 'observeNone' + '?documentId=' + documentId)
        .success(function(data, status) {
          console.log("observed none");
          console.log(data);
        })
        .error(function(data, status) {
          console.log(status);
        });;
    };


    /**
     * Returns a promise to get probability dump
     */
    function getProbabilityDump(documentId) {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(instrumentServiceBase + 'getProbabilityDump' + '?documentId=' + documentId)
        .success(function(data, status) {
          deferred.resolve(data);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      /** done with get request */
      return deferred.promise;
    };
  }

})();