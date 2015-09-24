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
      // $.get( "http://www.sec.gov/cgi-bin/srch-edgar?text=google&first=2015&last=2015", function(data) {
      //   console.log(data);
      // })

      secSearchService.getSearchResults('google 10-k')
        .then(function(data) {
          console.log(data);
          var rss = $.parseXML(data);
          var entries = $(rss).find("entry");
          console.log(entries.length);
          $.each(entries, function(index){
            console.log($(entries[index]).find('title').text());
            console.log($(entries[index]).find('link').attr('href'));
          });
        }, function(err) {
          console.log(err);
        });
    }


  }


})();
