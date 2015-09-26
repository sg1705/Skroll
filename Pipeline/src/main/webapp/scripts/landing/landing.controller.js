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
		.controller('LandingCtrl', LandingCtrl);

  /* @ngInject*/
  function LandingCtrl($location, secSearchService, $routeParams) {

    //-- private variables
    var searchResults = [];
    var vm            = this;

    //-- public variables
    vm.url = $routeParams.searchText;
    vm.searchResults = searchResults;

    //-- public methods
    vm.onPasteUrl      = onPasteUrl;
    vm.onEnter         = onEnter;
    vm.onClickedFiling = onClickedFiling;

    search();

    /**
    * Handles when user pastes a url
    **/
    function onPasteUrl($event) {
      vm.url = $event.originalEvent.clipboardData.getData('text');
      console.log('something pasted' + vm.url);
      //http://localhost:8088/open?q=http://www.sec.gov/Archives/edgar/data/820027/000082002715000024/amp.htm
      // $location.search('q', vm.url);
      // $location.path('/open');
    }

    function onEnter() {
      console.log('enter pressed');
      //search();
      $location.path('/search/' + vm.url);
    }

    function onClickedFiling(link) {
      secSearchService.getIndexHtml(link)
        .then(function(data) {
          var html = $.parseHTML(data);
          var href = $(html).find('[href^="/Archives/edgar/data"]')[0];
          href = 'http://www.sec.gov'+$(href).attr('href');
          console.log(href);
          $location.path('/open').search('q', href);
          // http://localhost:8088/open?q=http:%2F%2Fwww.sec.gov%2FArchives%2Fedgar%2Fdata%2F1467373%2F000146737314000467%2Facn831201410k.htm
        }, function(err) {
          console.log(err);
        });

    }

    function search() {
      vm.searchResults = new Array();
      secSearchService.getSearchResults(vm.url)
        .then(function(data) {
          var rss = $.parseXML(data);
          var entries = $(rss).find("entry");
          console.log(entries.length);
          $.each(entries, function(index){
            var result = processXml(entries[index]);
            vm.searchResults.push(result);
          });
        }, function(err) {
          console.log(err);
        });

    }

    function processXml(entry) {
      var filingDate = $(entry).find('updated').text();
      var title = $(entry).find('title').text();
      var href = $(entry).find('link').attr('href');
      var formType = title.split(' - ')[0];
      var companyName = title.split(' - ')[1];
      var result = {
        'companyName' : companyName,
        'formType'    : formType,
        'filingDate'  : filingDate,
        'href'        : href
      }
      return result;
    }

  }

})();
