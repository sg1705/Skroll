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
  function LandingCtrl($location, secSearchService, $routeParams, importService, documentModel, $analytics, searchBoxModel) {

    //-- private variables
    var vm = this;
    var searchResults = [];
    var FullTextSearchCategories = ["Underwriting Agreement",
                "Plans of Reorganization, Merger or Acquisition", "Articles of Incorporation and bylaw",
                "Indenture",
                "Legal Opinion",
                "Tax Opinion",
                "Voting Agreement",
                "Material Contract",
                "Credit Agreement",
                "ex-"];
    var FullTextSearchCategoriesPostFilter = ["ex-1.",
                "ex-2.", "ex-3.",
                "ex.4.",
                "ex-5.",
                "ex-8.",
                "ex-9.",
                "ex-10.",
                "ex-10.",
                "ex-"];
    var BooleanSearchCategories  = ["Financial","Prospectus","Registration","Proxy","News"];
    var BooleanSearchCategoriesPostFilter = ["10-K 10-Q 10-D",
            "424 FWP 144 425",
            "S-1 S-4 S-8 15-15 15-12 D D/A S-3 POS",
            "DEF PX",
            "8-K"];

    //-- public variables
    vm.searchState = searchBoxModel.searchState;
    vm.searchTextInUrl = '';
    vm.searchResults = searchResults;

    //-- public methods
    vm.onEnter = onEnter;
    vm.onClickedFiling = onClickedFiling;

    loadSearchState();
    search();

    function onEnter() {
      vm.searchTextInUrl = searchBoxModel.getText();
      if (!((vm.searchTextInUrl == null) || (vm.searchTextInUrl == "undefined"))) {
        $analytics.eventTrack("main", {
          category: 'landingPage.searchText',
          label: vm.searchTextInUrl
        });
        $location.path('/search/' + encodeURIComponent(vm.searchTextInUrl));
      }
    }

    function onClickedFiling(link, docType) {
      if ((link.indexOf("http://www.sec.gov")) >= 0) {
        importService.importDocFromUrl(link, docType)
            .then(function(partial) {
              $location.search({});
              $location.path('/view/docId/' + documentModel.documentId);
            });
      } else {
        secSearchService.getIndexHtml(link)
          .then(function(data) {
            var html = $.parseHTML(data);
            var href = $(html).find('[href^="/Archives/edgar/data"]')[0];
            href = 'http://www.sec.gov/' + $(href).attr('href');;
            console.log(href);
            importService.importDocFromUrl(href, docType)
              .then(function(partial) {
                $location.search({});
                $location.path('/view/docId/' + documentModel.documentId);
            })
            // $location.path('/open').search('q', href);
          }, function(err) {
          console.log(err);
        });
      }
    }

    function httpURLInSearch(url) {
      importService.importDocFromUrl(url)
        .then(function(partial) {
          $location.search({});
          $location.path('/view/docId/' + documentModel.documentId);
        })
    }

    function search() {
      documentModel.viewState.isProcessing = true;
      if (searchBoxModel.isEmpty()) {
        documentModel.viewState.isProcessing = false;
        return;
      }
      if ((vm.searchState.searchText.indexOf("http%3A") === 0) || ((vm.searchState.searchText.indexOf('www.') === 0))) {
        httpURLInSearch(vm.searchState.searchText);
      }
      //decode search text so that search state can be saved
      vm.searchResults = new Array();
      secSearchService.getSearchResults(vm.searchTextInUrl)
        .then(function(data) {
          var IsFullTextSearch = false;
          var categoryIndex = 0;
          var searchTextFromURL = decodeURIComponent(vm.searchTextInUrl);
          for (var categoryIndex = 0; categoryIndex < FullTextSearchCategories.length; categoryIndex++) {
              if (searchTextFromURL.toLowerCase().indexOf(FullTextSearchCategories[categoryIndex].toLowerCase()) >= 0){
                  IsFullTextSearch = true;
                  break;
              }
          }
          if(IsFullTextSearch) {
            var html = $.parseHTML(data);
            var entries = $(html).find('a[class^="filing"]');
            var filingDate = $(html).find('i[class^="blue"]');
            $.each(entries, function(index) {
              var result = processFullTextResult(entries[index], filingDate[index].innerText);
              if (result.formType.toLowerCase().indexOf(FullTextSearchCategoriesPostFilter[categoryIndex]) >= 0) {
                vm.searchResults.push(result);
              }
            });
          } else {
            var rss = $.parseXML(data);
            var entries = $(rss).find("entry");
            $.each(entries, function(index) {
              var result = processXml(entries[index]);
              vm.searchResults.push(result);
            });
          }
          documentModel.viewState.isProcessing = false;
          //temporary workaround for angular bug
          $('.md-scroll-mask').css('display', 'none');
        }, function(err) {
          console.log(err);
          documentModel.viewState.isProcessing = false;
        });

    }

    function processXml(entry) {
      var filingDate = $(entry).find('updated').text();
      var title = $(entry).find('title').text();
      var href = $(entry).find('link').attr('href');
      var formType = title.split(' - ')[0];
      var category = "";
      for (var categoryIndex = 0; categoryIndex < BooleanSearchCategoriesPostFilter.length; categoryIndex++) {
         var formTypesInCategory = BooleanSearchCategoriesPostFilter[categoryIndex].toLowerCase().split(" ");
          for (var i = 0; i < formTypesInCategory.length; i++) {
              if (formType.toLowerCase().indexOf(formTypesInCategory[i]) >= 0){
                  category = BooleanSearchCategories[categoryIndex];
                  break;
              }
            }
      }
      console.log ("found category:"  + category + "for FormType: " + formType);
      var companyName = title.split(' - ')[1];
      var result = {
        'companyName': companyName,
        'formType': formType,
        'category' : category,
        'filingDate': moment(filingDate).format('YYYY MMM DD'),
        'href': href
      }
      return result;
    }

    function processFullTextResult(entry, filingDate) {
      var href = findUrls(entry.href)[0];
      var splitEntry = entry.innerText.split('for');
      var formType = splitEntry[0];
      var category = "";
      for (var categoryIndex = 0; categoryIndex < FullTextSearchCategoriesPostFilter.length; categoryIndex++) {
              if (formType.toLowerCase().indexOf(FullTextSearchCategoriesPostFilter[categoryIndex].toLowerCase()) >= 0){
                  category = FullTextSearchCategories[categoryIndex];
                  break;
              }
      }
      console.log ("found category:"  + category + "for FormType: " + formType);
      var companyName = splitEntry[1];
      var result = {
        'companyName': companyName,
        'formType': formType,
        'category' : category,
        'filingDate': filingDate,
        'href': href
      }
      return result;
    }

    function findUrls(text) {
      var source = (text || '').toString();
      var urlArray = [];
      var url;
      var matchArray;

      // Regular expression to find FTP, HTTP(S) and email URLs.
      var regexToken = /(((ftp|https?):\/\/)[\-\w@:%_\+.~#?,&\/\/=]+)|((mailto:)?[_.\w-]+@([\w][\w\-]+\.)+[a-zA-Z]{2,3})/g;

      // Iterate through any URLs in the text.
      while( (matchArray = regexToken.exec( source )) !== null )
      {
        var token = matchArray[0];
        urlArray.push( token );
      }
      return urlArray;
    }

    function loadSearchState() {
      vm.searchTextInUrl = $routeParams.searchText;
      if (vm.searchTextInUrl != undefined) {
        var searchState = JSON.parse(decodeURIComponent(vm.searchTextInUrl));
        vm.searchState.selectedChips = searchState.selectedChips;
        vm.searchState.searchText = searchState.searchText;
      } else {
        vm.searchTextInUrl = '';
      }
    }

  }
})();