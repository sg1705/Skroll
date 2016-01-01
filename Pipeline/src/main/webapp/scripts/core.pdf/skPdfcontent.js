(function() {

  'use strict';

  /**
   * @ngdoc directive
   * @name SkrollApp.directive:skPdfContent
   * @description
   * # fileUpload
   */

  angular
    .module('app.core.pdf')
    .directive('skPdfContent', skPdfContent);

  /* @ngInject */
  function skPdfContent(documentModel, documentService, LHSModel, selectionService, $timeout, $http, $analytics, $window, $q, viewportService) {

    var directive = {
      restricted: 'A',
      controller: 'PdfViewPortCtrl',
      scope: false,
      link: link
    }

    return directive;

    //////

    /**
     * Two paths when a document is loaded.
     * Complete the partially loaded doc
     * Or load the entire document
     **/
    function link(scope, element, attrs, viewCtrl) {
      if (documentModel.documentId != null) {
        documentModel.isProcessing = true;
        if (documentModel.isPartiallyParsed) {
          console.log('partially parsed');
          $analytics.eventTrack(documentModel.documentId, {
            category: 'doc.View',
            label: selectionService.paragraphId
          });

          $timeout(function() {
            insertHtmlInIframe();
            documentModel.isProcessing = false;
            postInsertIframe();
          }, 0);

          documentService.importDoc(documentModel.url, false)
            .then(function(data) {
              documentModel.isPartiallyParsed = false;
              return documentService.getTerms(documentModel.documentId);
            })
            .then(function(terms) {
              setTerms(terms);
              return documentService.getIndex(documentModel.documentId);
            }, function(data, status) {
                      console.log(status);
                         })
            .then(function(data) {
              documentModel.lunrIndex = lunr.Index.load(data);
            });
        } else {
          loadDocument()
            .then(function(contentHtml) {
              documentModel.targetHtml = contentHtml;

              $timeout(function() {
                insertHtmlInIframe();
                documentModel.isProcessing = false;
                postInsertIframe();
              }, 0);

              documentModel.isProcessing = false;
              $analytics.eventTrack(documentModel.documentId, {
                category: 'doc.View',
                label: selectionService.paragraphId
              });
              return documentService.getTerms(documentModel.documentId);
            })
            .then(function(terms) {
              setTerms(terms);
              $timeout(timeout, 0); //@see function timeout()

              function timeout() {
                console.log(selectionService.serializedSelection);
                if ((selectionService.serializedSelection === undefined) || (selectionService.serializedSelection == "undefined")) {

                } else {
                  selectionService.scrollToSelection(selectionService.serializedSelection);
                }
                documentModel.isProcessing = false;
                //calculate offsets for headers
                //iterate over each term to find Y offset
                // LHSModel.setYOffsetForTerms(LHSModel.smodel.terms);
                documentModel.isProcessing = false;
              }
              return documentService.getIndex(documentModel.documentId);
            }, function(data, status) {
              console.log(status);
            })
            .then(function(data) {
              documentModel.lunrIndex = lunr.Index.load(data);
            });
        }
      }


      function setTerms(terms) {
        LHSModel.setTerms(terms);
        console.log(terms);
        documentModel.isTocLoaded = true;
        LHSModel.setYOffsetForTerms(LHSModel.smodel.terms);
      }

      function loadDocument() {
        if (documentModel.targetHtml === '') {
          return documentService.loadDocument(documentModel.documentId)
        } else {
          var deferred = $q.defer();
          deferred.resolve(documentModel.targetHtml);
          return deferred.promise;
        }
      }

      function insertHtmlInIframe() {
        if ((navigator.userAgent.indexOf('iPad') > -1) || (navigator.userAgent.indexOf('iPhone') > -1)){
          $('#docViewIframe').attr('scrolling', 'no');
          $('#docViewIframe').wrap('<md-content id="docViewIframeParent"></md-content>');
        }


        var iframe = $('#docViewIframe')[0];
        var iframeDoc = iframe.contentWindow.document;

        iframeDoc.open();
        iframeDoc.write(documentModel.targetHtml);
        iframeDoc.close();
        // if (navigator.userAgent.indexOf('Chrome') > -1) {
        viewCtrl.resizeFrame();
        // }

        var elmt = angular.element(iframeDoc.body);


        if ((navigator.userAgent.indexOf('iPad') > -1) || (navigator.userAgent.indexOf('iPhone') > -1) || (navigator.userAgent.indexOf('Android') > -1)) {
          // var hammertime = new Hammer(iframeDoc.getElementById('content'));
          // hammertime.on('tap', function(ev) {
          //     selectionService.selectParagraph(selectionService.inferParagraphId(ev));
          //     console.log('user tapped');
          // });
        } else {
          elmt.bind('click', function($event) {
            viewCtrl.paraClicked($event)
          });
        }


        elmt.bind('mouseup', function($event) {
          viewCtrl.mouseUp($event)
        });

        elmt.bind('mousedown', function($event) {
          viewCtrl.mouseDown($event)
        });



        angular.element($window).bind('resize', function($event) {
          $timeout(function() {
            viewCtrl.resetFrameHeight();
          }, 200);
        });

        if (navigator.userAgent.indexOf('Firefox') > -1) {
          firefoxAnchorLinks(iframe);
        }
      }

      /*
      * Method to find out the bounding rect of the iframe
      */
      function postInsertIframe() {
        var iframeoffset = $('#docViewIframe').offset();
        iframeoffset.width = $('#docViewIframe').width();
        viewportService.viewportOffset = iframeoffset;
        console.log(viewCtrl.iframeOffset);
      }

      function firefoxAnchorLinks(iframe) {
        var windowElement = iframe.contentWindow;
        var bodyElement = $(iframe.contentWindow.document.body);
        $("a", $(bodyElement)).each(function (){
          var link = $(this);
          var href = link.attr("href");
          if(href && href[0] == "#")
          {
            var name = href.substring(1);
            $(this).click(function() {
              var nameElement = $("[name='"+name+"']", bodyElement);
              var idElement = $("#"+name, bodyElement);
              var element = null;
              if(nameElement.length > 0) {
                element = nameElement;
              } else if(idElement.length > 0) {
                element = idElement;
              }

              if(element) {
                var offset = element.offset();
                windowElement.scrollTo(offset.left, offset.top);
              }

              return false;
            });
          }

});
      }

    }

  }

})();