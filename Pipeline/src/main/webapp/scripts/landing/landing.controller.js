(function(){
	'use strict';

	/**
	 * @ngdoc function
	 * @name skrollApp.controller:LandingCtrl
	 * @description
	 * # LandingCtrl
	 * Controller of the LandingCtrl
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
    vm.searchText    = $routeParams.searchText;
    vm.searchResults = searchResults;

    //-- public methods
    vm.onEnter         = onEnter;
    vm.onClickedFiling = onClickedFiling;

    search();

    function onEnter() {
      if (!((vm.searchText == null)  || (vm.searchText == "undefined"))) {
        $location.path('/search/' + vm.searchText);
      }
    }

    function onClickedFiling(link) {
      secSearchService.getIndexHtml(link)
        .then(function(data) {
          var html = $.parseHTML(data);
          var href = $(html).find('[href^="/Archives/edgar/data"]')[0];
          href = 'http://www.sec.gov'+$(href).attr('href');
          console.log(href);
          $location.path('/open').search('q', href);
        }, function(err) {
          console.log(err);
        });

    }

    function search() {
      if (((vm.searchText == null)  || (vm.searchText == "undefined"))) {
        return;
      }      
      vm.searchResults = new Array();
      secSearchService.getSearchResults(vm.searchText)
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
        'filingDate'  : moment(filingDate).format('YYYY MMM DD'),
        'href'        : href
      }
      return result;
    }

  }

})();
