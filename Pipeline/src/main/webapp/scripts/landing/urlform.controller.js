(function(){
	'use strict';

	/**
	 * @ngdoc function
	 * @name skrollApp.controller:UrlFormCtrl
	 * @description
	 * # UrlFormCtrl
	 * Controller of the UrlFormCtrl
	 */

	angular
		.module('app.landing')
		.controller('UrlFormCtrl', UrlFormCtrl);

  /* @ngInject*/
  function UrlFormCtrl($location, secSearchService) {

    //-- private variables
    var vm = this;

    //-- public variables
    vm.url = '';

    //-- public methods
    vm.onPasteUrl = onPasteUrl;
    vm.onEnter    = onEnter;

    /**
    * Handles when user pastes a url
    **/
    function onPasteUrl($event) {
      vm.url = $event.originalEvent.clipboardData.getData('text');
      console.log('something pasted' + vm.url);
      //http://localhost:8088/open?q=http://www.sec.gov/Archives/edgar/data/820027/000082002715000024/amp.htm
      $location.search('q', vm.url);
      $location.path('/open');
    }

    function onEnter() {
      console.log('enter pressed');
      secSearchService.getSearchResults('google')
        .then(function(data) {
          console.log(data);
        }, function(err) {
          console.log(err);
        });
    }


  }


})();
