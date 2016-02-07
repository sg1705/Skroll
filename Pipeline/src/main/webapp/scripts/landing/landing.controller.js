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
    var companyPanelResults = [];
    var totalResults = 0;
    var categories = new Categories();
    var fullTextSearchCategories = categories.getFullTextSearchCategories();
    var booleanSearchCategories = categories.getBooleanSearchCategories();
    var refinerCategories = categories.getRefinerCategories();

    //-- public variables
    vm.searchState = searchBoxModel.searchState;
    vm.searchBoxModel = searchBoxModel;
    vm.searchTextInUrl = '';
    vm.progressBar = false;
    vm.companyPanelResults = companyPanelResults;
    vm.totalResults = totalResults;
    vm.refinerCategories = refinerCategories;
    vm.refinerYears = ['2012', '2013', '2014', '2015'];

    //-- public methods
    vm.onEnter = onEnter;
    vm.onClickedFiling = onClickedFiling;
    vm.onClickRefineChip = onClickRefineChip;
    vm.onClickRefineYear = onClickRefineYear;
    vm.onClickLogo = onClickLogo;

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

    function onClickLogo() {
      vm.searchBoxModel.clear();
      $location.path('/search/');
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

    function onClickRefineChip(chipType, chipName) {
      var newChip = { id: '', field1: chipName, type: chipType, field2: '' };
      searchBoxModel.updateChip(newChip);
      vm.onEnter();
    }

    function onClickRefineYear(year) {
      vm.searchState.searchText = year;
      vm.onEnter();
    }

    function httpURLInSearch(url) {
      importService.importDocFromUrl(url)
        .then(function(partial) {
          $location.search({});
          $location.path('/view/docId/' + documentModel.documentId);
        })
    }

    function search() {
      vm.progressBar = true;
      if (searchBoxModel.isEmpty()) {

        vm.progressBar = false;
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
          for (var categoryIndex = 0; categoryIndex < fullTextSearchCategories.length; categoryIndex++) {
              if (searchTextFromURL.toLowerCase().indexOf(fullTextSearchCategories[categoryIndex].categoryName.toLowerCase()) >= 0){
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
              if (result.formType.toLowerCase().indexOf(fullTextSearchCategories[categoryIndex].postFilter) >= 0) {
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
          vm.progressBar = false;
          vm.companyPanelResults = convertSearchResultsIntoCompanyPanel(vm.searchResults);
          //temporary workaround for angular bug
          $('.md-scroll-mask').css('display', 'none');
        }, function(err) {
          console.log(err);
          vm.progressBar = false;
        });

    }

    function processXml(entry) {
      var filingDate = $(entry).find('updated').text();
      var title = $(entry).find('title').text();
      var href = $(entry).find('link').attr('href');
      var formType = title.split(' - ')[0];
      var category = "";
      for (var categoryIndex = 0; categoryIndex < booleanSearchCategories.length; categoryIndex++) {
         var formTypesInCategory = booleanSearchCategories[categoryIndex].postFilter.toLowerCase().split(" ");
          for (var i = 0; i < formTypesInCategory.length; i++) {
              if (formType.toLowerCase().indexOf(formTypesInCategory[i]) >= 0){
                  category = booleanSearchCategories[categoryIndex].categoryName;
                  break;
              }
            }
      }
      var companyName = title.split(' - ')[1];
      var result = {
        'companyName': companyName,
        'formType': formType,
        'category' : category,
        'filingDate': moment(filingDate).format('MMM DD, YYYY'),
        'href': href
      }
      return result;
    }

    function processFullTextResult(entry, filingDate) {
      var href = findUrls(entry.href)[0];
      var splitEntry = entry.innerText.split('for');
      var formType = splitEntry[0];
      var category = "";
      for (var categoryIndex = 0; categoryIndex < fullTextSearchCategories.length; categoryIndex++) {
              if (formType.toLowerCase().indexOf(fullTextSearchCategories[categoryIndex].postFilter.toLowerCase()) >= 0){
                  category = fullTextSearchCategories[categoryIndex].categoryName;
                  break;
              }
      }
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

    /**
    * Filter out each company name
    * Iterate over each company
    * Filter out each category in sequence
    * Append results
    **/
    function convertSearchResultsIntoCompanyPanel(originalResults) {
      vm.totalResults = originalResults.length;
      //filter out unique companies
      var uniqCompanies = _.uniq(_.map(originalResults, function(c){ return c.companyName; }));
      //convert into company array
      var searchResults = _.map(uniqCompanies, function (c) {
        var resultCategories = [ ];
        //filter each category
        _.each(categories.categories, function(category) {
          var filteredCategoryResults = _.filter(originalResults, function(result) {
            if ((result.companyName == c) && (result.category == category.categoryName)) {
              return result;
            }
          });
          if (filteredCategoryResults.length < 1) {
            return;
          }
          // var filteredCategoryResults = _.first(filteredCategoryResults, 60);
          var filings = _.map(filteredCategoryResults, function(f){
              var fl = {};
              fl.formType = f.formType;
              fl.filingDate = f.filingDate;
              fl.href = f.href;
              return fl;
            });
          //now that we have filtered categories for category, let's create category proto
          var categoryProto = {
            'categoryName': category.categoryName,
            'filings': filings
          }
          resultCategories.push(categoryProto);
        });
        if (resultCategories.length > 0) {
          return {
            'companyName': c,
            'categories': resultCategories
          }
        } else {
          return null;
        }
      });
      convertSearchResultsIntoCompanyPanelForOneCard(searchResults);
      return _.filter(searchResults, function(r) { if (r != null) return r });
    }


    function convertSearchResultsIntoCompanyPanelForOneCard(results) {
      var results = _.map(results, function(result){
        if (result.categories.length == 1) {
          console.log('found only 1 category');
          //organize in years
          //get uniq years
          var uniqYears = _.uniq(result.categories[0].filings, function(f) {
            f.filingYear = moment(f.filingDate).format('YYYY');
            return moment(f.filingDate).format('YYYY');
          });

          var newCategories = [ ];
          _.each(uniqYears, function(u) {
            var filteredForYear = _.filter(result.categories[0].filings, function(f) {
              if (u.filingYear === moment(f.filingDate).format('YYYY')) {
                return f;
              }
            })
            newCategories.push( {
              'categoryName' : u.filingYear,
              'filings' : filteredForYear,
            })
          });
          result.categories = newCategories;
          result.isSingleCategory = true;
          console.log(result);
        } else {
          result.isSingleCategory = false;
        }
        return result;
      });
    }


    function Categories() {

      this.categories = [{
        'categoryName'  : 'Financials',
        'postFilter'    : '10-K 10-Q 10-D',
        'categoryType'  : 'BooleanSearch',
        'inRefiner'     : true
      },  {
        'categoryName'  : 'Proxies',
        'postFilter'    : 'DEF PX',
        'categoryType'  : 'BooleanSearch',
        'inRefiner'     : true
      }, {
        'categoryName'  : 'News',
        'postFilter'    : '8-K',
        'categoryType'  : 'BooleanSearch',
        'inRefiner'     : true
      }, {
        'categoryName'  : 'Prospectuses',
        'postFilter'    : 'S-1 S-4 S-8 15-15 15-12 D D/A S-3 POS 424 FWP 144 425',
        'categoryType'  : 'BooleanSearch',
        'inRefiner'     : true
      }, {
        'categoryName'  : 'Underwriting Agreements',
        'postFilter'    : 'ex-1.',
        'categoryType'  : 'FullTextSearch',
        'inRefiner'     : true
      }, {
        'categoryName'  : 'Plans of Reorganization, Merger or Acquisitions',
        'postFilter'    : 'ex-2.',
        'categoryType'  : 'FullTextSearch',
        'inRefiner'     : false
      }, {
        'categoryName'  : 'Articles of Incorporation and Bylaws',
        'postFilter'    : 'ex-3.',
        'categoryType'  : 'FullTextSearch',
        'inRefiner'     : false
      }, {
        'categoryName'  : 'Indentures',
        'postFilter'    : 'ex-4.',
        'categoryType'  : 'FullTextSearch',
        'inRefiner'     : true
      }, {
        'categoryName'  : 'Legal Opinions',
        'postFilter'    : 'ex-5.',
        'categoryType'  : 'FullTextSearch',
        'inRefiner'     : false
      }, {
        'categoryName'  : 'Tax Opinions',
        'postFilter'    : 'ex-8.',
        'categoryType'  : 'FullTextSearch',
        'inRefiner'     : false
      }, {
        'categoryName'  : 'Voting Agreements',
        'postFilter'    : 'ex-9.',
        'categoryType'  : 'FullTextSearch',
        'inRefiner'     : false
      }, {
        'categoryName'  : 'Material Contracts',
        'postFilter'    : 'ex-10.',
        'categoryType'  : 'FullTextSearch',
        'inRefiner'     : false
      }, {
        'categoryName'  : 'Credit Agreements',
        'postFilter'    : 'ex-10',
        'categoryType'  : 'FullTextSearch',
        'inRefiner'     : true
      }];


      this.getFullTextSearchCategories = function() {
        return _.filter(this.categories, function(c) {
          if (c.categoryType == 'FullTextSearch')
            return c;
        })
      }


      this.getBooleanSearchCategories = function() {
        return _.filter(this.categories, function(c) {
          if (c.categoryType == 'BooleanSearch')
            return c;
        })
      }

      this.getRefinerCategories = function() {
        return _.filter(this.categories, function(c) {
          if (c.inRefiner)
            return c;
        })

      }

    }



  }

})();
