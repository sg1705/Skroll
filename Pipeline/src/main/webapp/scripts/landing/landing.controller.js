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
    var companyPanelResults = [];
    var FullTextSearchCategories = ["Underwriting%20Agreement",
                "Reorganization", "Articles%20of%20Incorporation%20and%20bylaw",
                "Indenture",
                "Legal%20Opinion",
                "Tax%20Opinion",
                "Voting%20Agreement",
                "Material%20Contract",
                "Credit%20Agreement",
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


    //-- public variables
    vm.searchState = searchBoxModel.searchState;
    vm.searchBoxModel = searchBoxModel;
    vm.searchTextInUrl = '';
    vm.searchResults = searchResults;
    vm.companyPanelResults = companyPanelResults;

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
          for (var categoryIndex = 0; categoryIndex < FullTextSearchCategories.length; categoryIndex++) {
              if (vm.searchTextInUrl.toLowerCase().indexOf(FullTextSearchCategories[categoryIndex].toLowerCase()) >= 0){
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
          vm.companyPanelResults = convertSearchResultsIntoCompanyPanel(vm.searchResults);
          console.log(vm.companyPanelResults);
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
      var companyName = title.split(' - ')[1];
      var result = {
        'companyName': companyName,
        'formType': formType,
        'filingDate': moment(filingDate).format('YYYY MMM DD'),
        'href': href
      }
      return result;
    }

    function processFullTextResult(entry, filingDate) {
      var href = findUrls(entry.href)[0];
      var splitEntry = entry.innerText.split('for');
      var formType = splitEntry[0];
      var companyName = splitEntry[1];
      var result = {
        'companyName': companyName,
        'formType': formType,
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

  /**
  * Filter out each company name
  * Iterate over each company
  * Filter out each category in sequence
  * Append results
  **/
  function convertSearchResultsIntoCompanyPanel(originalResults) {
    var CATEGORIES = ['Financial', 'News', 'Ownership', 'Other', 'Indenture'];
    var FORMS = {
      '10-K' : 'Financial',
      '10-Q' : 'Financial',
      '8-K'  : 'News',
      '4'  : 'Ownership'
    }
    //create category for each search result
    var results = _.map(originalResults, function(r) {
      var category = FORMS[r.formType];
      if (category == null) {
        category = 'Other'
      }
      r.category = category;
      return r;
    });
    //filter out unique companies
    var uniqCompanies = _.uniq(_.map(originalResults, function(c){ return c.companyName; }));
    //convert into company array
    var searchResults = _.map(uniqCompanies, function (c) {
      var categories = [ ];
      //filter each category
      _.each(CATEGORIES, function(category) {
        var filteredCategoryResults = _.filter(results, function(result) {
          if ((result.companyName == c) && (result.category == category)) {
            return result;
          }
        });
        if (filteredCategoryResults.length < 1) {
          return;
        }

        var filteredCategoryResults = _.first(filteredCategoryResults, 6);
        var filings = _.map(filteredCategoryResults, function(f){
            var fl = {};
            fl.formType = f.formType;
            fl.filingDate = f.filingDate;
            fl.href = f.href;
            return fl;
          });
        //now that we have filtered categories for category, let's create category proto
        var categoryProto = {
          categoryName: category,
          filings: filings
        }
        categories.push(categoryProto);
      });
      if (categories.length > 0) {
        return {
          companyName: c,
          categories: categories
        }
      } else {
        return null;
      }
    });
    return _.filter(searchResults, function(r) { if (r != null) return r });
  }

})();