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

        if ((vm.searchText.indexOf('http://') === 0) || (vm.searchText.indexOf('www.') === 0)) {
          $location.path('/search/' + encodeURIComponent(vm.searchText));
        } else {
          $location.path('/search/' + vm.searchText);
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
      documentModel.isProcessing = true;
      if (((vm.searchText == null) || (vm.searchText == "undefined"))) {
        var searchText = 'goog 10-K 2012 2015';
        $location.path('/search/' + searchText);
        return;
      }
      if ((vm.searchText.indexOf("http%3A") === 0) || ((vm.searchText.indexOf('www.') === 0))) {
        httpURLInSearch(vm.searchText);
      }

      vm.searchResults = new Array();
      secSearchService.getSearchResults(vm.searchText)
        .then(function(data) {
          console.log(vm.searchText);
          if(vm.searchText.toLowerCase().indexOf("ex-") >=0) {
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

})();