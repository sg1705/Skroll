(function() {
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

  /* @ngInject */
  function LandingCtrl($location, secSearchService, $routeParams, importService, documentModel, $analytics) {

    //-- private variables
    var searchResults = [];
    var vm = this;

    //-- public variables
    vm.searchText = $routeParams.searchText;
    vm.searchResults = searchResults;

    //-- public methods
    vm.onEnter = onEnter;
    vm.onClickedFiling = onClickedFiling;

    search();

    function onEnter() {
      if (!((vm.searchText == null) || (vm.searchText == "undefined"))) {
      $analytics.eventTrack("main", {
              category: 'landingPage.searchText',
              label: vm.searchText
            });
        $location.path('/search/' + vm.searchText);
      }
    }

    function onClickedFiling(link) {
      secSearchService.getIndexHtml(link)
        .then(function(data) {
          var html = $.parseHTML(data);
          var href = $(html).find('[href^="/Archives/edgar/data"]')[0];
          href = 'http://www.sec.gov' + $(href).attr('href');
          console.log(href);
          importService.importDocFromUrl(href)
            .then(function(partial) {
              $location.search({});
              $location.path('/view/docId/' + documentModel.documentId);
            })
          // $location.path('/open').search('q', href);
        }, function(err) {
          console.log(err);
        });

    }

    function search() {
      documentModel.isProcessing = true;
      if (((vm.searchText == null) || (vm.searchText == "undefined"))) {
        var searchText = 'Google 10K 2012 2015';
        $location.path('/search/' + searchText);
        return;
      }
      vm.searchResults = new Array();
      secSearchService.getSearchResults(vm.searchText)
        .then(function(data) {
          var rss = $.parseXML(data);
          var entries = $(rss).find("entry");
          console.log(entries.length);
          $.each(entries, function(index) {
            var result = processXml(entries[index]);
            vm.searchResults.push(result);
          });
          documentModel.isProcessing = false;
        }, function(err) {
          console.log(err);
          documentModel.isProcessing = false;
        });

    }

    function processXml(entry) {
      var filingDate = $(entry).find('updated').text();
      var title = $(entry).find('title').text();
      var href = $(entry).find('link').attr('href');
      var formType = title.split(' - ')[0];
      var companyName = title.split(' - ')[1];
      var result = {
        'companyName': companyName,
        'formType': formType,
        'filingDate': moment(filingDate).format('YYYY MMM DD'),
        'href': href
      }
      return result;
    }

  }

})();