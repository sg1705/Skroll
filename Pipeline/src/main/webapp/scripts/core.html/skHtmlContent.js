(function() {

  'use strict';

  /**
   * @ngdoc directive
   * @name SkrollApp.directive:skHtmlContent
   * @description
   * # fileUpload
   */

  angular
    .module('app.core.html')
    .directive('skHtmlContent', skHtmlContent);

  /* @ngInject */
  function skHtmlContent(documentModel, documentService, LHSModel, selectionService, $timeout, $http, $analytics, $window, $q, viewportService) {

    var directive = {
      restricted: 'EA',
      controller: 'HtmlViewPortCtrl',
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
        documentModel.viewState.isProcessing = true;
        if (documentModel.isPartiallyParsed) {
          $analytics.eventTrack(documentModel.documentId, {
            category: 'doc.View',
            label: selectionService.paragraphId
          });

          $timeout(function() {
            insertHtmlInIframe();
            documentModel.viewState.isProcessing = false;
            postInsertIframe();

          }, 0);

          documentService.importDoc(documentModel.url, false)
            .then(function(data) {
              // documentModel.isPartiallyParsed = false;
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
          documentModel.viewState.isProcessing = true;
          loadDocument()
            .then(function(contentHtml) {
              // documentModel.p.content = contentHtml;

              $timeout(function() {
                insertHtmlInIframe();
                postInsertIframe();
                documentModel.viewState.isProcessing = false;
              }, 0);

              // documentModel.viewState.isProcessing = false;
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
                documentModel.viewState.isProcessing = false;
                //calculate offsets for headers
                //iterate over each term to find Y offset
                // LHSModel.setYOffsetForTerms(LHSModel.smodel.terms);
                documentModel.viewState.isProcessing = false;
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
        documentModel.viewState.isTocLoaded = true;
        LHSModel.setYOffsetForTerms(LHSModel.smodel.terms);
      }

      function loadDocument() {
        if (documentModel.p.content === '') {
          return documentService.loadDocument(documentModel.documentId)
        } else {
          var deferred = $q.defer();
          deferred.resolve(documentModel.p.content);
          return deferred.promise;
        }
      }

      function insertHtmlInIframe() {
        console.log(navigator.userAgent);
        if ((navigator.userAgent.indexOf('iPad') > -1) || (navigator.userAgent.indexOf('iPhone') > -1)){
          $('#docViewIframe').attr('scrolling', 'no');
          $('#docViewIframe').wrap('<md-content id="docViewIframeParent"></md-content>');
        }


        var iframe = $('#docViewIframe')[0];
        var iframeDoc = iframe.contentWindow.document;

        iframeDoc.open();
        iframeDoc.write(documentModel.p.content);
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


        //no mouse over for IE and Edge
        if (!(navigator.userAgent.indexOf('Trident') > -1) && !(navigator.userAgent.indexOf('Edge') > -1)) {
          elmt.bind('mousemove', function($event) {
            viewCtrl.mouseMove($event);
          });
        }


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
        viewportService.viewportOffset = iframeoffset;
        // set viewport dimensions
        $timeout(function() {
          var iframeElement = selectionService.getIframeDocument();
          var contentDiv = $('#content', $(iframeElement.body));
          var leftcurtain = $('#leftcurtain', $(iframeElement.body));
          var rightcurtain = $('#rightcurtain', $(iframeElement.body));
          viewportService.content.width = contentDiv.width();
          viewportService.content.paddingLeft = parseInt(contentDiv.css('paddingLeft'));
          viewportService.content.paddingRight = parseInt(contentDiv.css('paddingRight'));
          viewportService.leftCurtainWidth = leftcurtain.width();
          viewportService.rightCurtainWidth = rightcurtain.width();
          console.log('viewportservice=' + JSON.stringify(viewportService));
        }, 1000);

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