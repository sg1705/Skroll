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

    //-- public variables
    vm.searchState = searchBoxModel.searchState;
    vm.searchState.searchText = $routeParams.searchText;
    vm.searchResults = searchResults;
    vm.companyPanelResults = companyPanelResults;

    //-- public methods
    vm.onEnter = onEnter;
    vm.onClickedFiling = onClickedFiling;

    search();

    function onEnter() {
      if (!((vm.searchState.searchText == null) || (vm.searchState.searchText == "undefined"))) {
      $analytics.eventTrack("main", {
              category: 'landingPage.searchText',
              label: vm.searchState.searchText
            });

        if ((vm.searchState.searchText.indexOf('http://') === 0) || (vm.searchState.searchText.indexOf('www.') === 0)) {
          $location.path('/search/' + encodeURIComponent(vm.searchState.searchText));
        } else {
          $location.path('/search/' + vm.searchState.searchText);
        }
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
      if (((vm.searchState.searchText == null) || (vm.searchState.searchText == "undefined"))) {
        var searchText = 'goog 10-K 2012 2015';
        $location.path('/search/' + searchText);
        return;
      }
      if ((vm.searchState.searchText.indexOf("http%3A") === 0) || ((vm.searchState.searchText.indexOf('www.') === 0))) {
        httpURLInSearch(vm.searchState.searchText);
      }

      vm.searchResults = new Array();
      secSearchService.getSearchResults(vm.searchState.searchText)
        .then(function(data) {
          console.log(vm.searchState.searchText);
          if(vm.searchState.searchText.toLowerCase().indexOf("ex-") >=0) {
            var html = $.parseHTML(data);
            var entries = $(html).find('a[class^="filing"]');
            var filingDate = $(html).find('i[class^="blue"]');
            console.log(filingDate);
            $.each(entries, function(index) {
              console.log("text:[" + index + "]" + entries[index].innerText);
              var result = processFullTextResult(entries[index], filingDate[index].innerText);
              vm.searchResults.push(result);
            });
          } else {
            var rss = $.parseXML(data);
            var entries = $(rss).find("entry");
            console.log(entries.length);
            $.each(entries, function(index) {
              var result = processXml(entries[index]);
              vm.searchResults.push(result);
            });
          }
          documentModel.viewState.isProcessing = false;
          vm.companyPanelResults = convertSearchResultsIntoCompanyPanel(vm.searchResults);
          console.log(vm.companyPanelResults);
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
  }

  /**
  * Filter out each company name
  * Iterate over each company
  * Filter out each category in sequence
  * Append results
  **/
  function convertSearchResultsIntoCompanyPanel(originalResults) {
    var CATEGORIES = ['Financial', 'News', 'Ownership'];
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
        category = 'NONE'
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