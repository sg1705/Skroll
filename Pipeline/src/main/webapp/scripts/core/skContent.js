(function() {

  'use strict';

  /**
   * @ngdoc directive
   * @name SkrollApp.directive:fileUpload
   * @description
   * # fileUpload
   */

  angular
    .module('app.core')
    .directive('skContent', skContent);

  /* @ngInject */
  function skContent(documentModel, documentService, LHSModel, selectionService, $timeout, $http, $analytics, $window) {

    var directive = {
      restricted: 'EA',
      controller: 'ViewPortCtrl',
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
          }, 0);

          documentService.importDoc(documentModel.url, false)
            .then(function(data) {
              documentModel.isPartiallyParsed = false;
              return documentService.getTerms(documentModel.documentId);
            })
            .then(function(terms) {
              LHSModel.setTerms(terms);
              console.log(terms);
              documentModel.isTocLoaded = true;
              LHSModel.setYOffsetForTerms(LHSModel.smodel.terms);
              return documentService.getIndex(documentModel.documentId);
            })
            .then(function(data) {
              console.log(data);
            });
        } else {
          documentService.loadDocument(documentModel.documentId)
            .then(function(contentHtml) {
              documentModel.targetHtml = contentHtml;
              // element.replaceWith(documentModel.targetHtml);
              insertHtmlInIframe();
              documentModel.isProcessing = false;
              $analytics.eventTrack(documentModel.documentId, {
                category: 'doc.View',
                label: selectionService.paragraphId
              });
              // if ((selectionService.serializedSelection === undefined) || (selectionService.serializedSelection == "undefined")) {
              //   showGradually();
              // } else {
              //   showEntireDoc();
              // }
              return documentService.getTerms(documentModel.documentId);
            })
            .then(function(terms) {
              LHSModel.setTerms(terms);
              console.log(terms);
              documentModel.isTocLoaded = true;
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
                LHSModel.setYOffsetForTerms(LHSModel.smodel.terms);

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

      function insertHtmlInIframe() {
        var iframe = $('#docViewIframe')[0];
        var iframeDoc = iframe.contentWindow.document;
        iframeDoc.open();
        iframeDoc.write(documentModel.targetHtml + '<style> \
          ::selection { \
              background: #F8E0F7; \
          } \
          ::-moz-selection { \
              background: #F8E0F7; \
          } \
          .selected-para-rhs { \
            background-color: #fff7e5; \
          } \
          .searched-text-rhs { \
            color: #FF6138; \
          } \
          </style>');
        iframeDoc.close();
        viewCtrl.resizeFrame();
        var elmt = angular.element(iframeDoc.body);

        elmt.bind('click', function($event) {
          viewCtrl.paraClicked($event)
        });


        elmt.bind('mouseup', function($event) {
          viewCtrl.mouseUp($event)
        });

        angular.element($window).bind('resize', function($event) {
          $timeout(function() {
            viewCtrl.resetFrameHeight();
          }, 200);
          // viewCtrl.resizeFrame();
        });



      }


    }

    function showGradually() {
      var elements = $('[id^="p_"]')
      $.each(elements, function(index) {
        if (!($(elements[index]).attr('id').indexOf("p_12") === 0)) {
          $timeout(function() {
            $(elements[index]).show();
          }, 0)
        }
      });
    };

    function showEntireDoc() {
      var elements = $('[id^="p_"]')
      $.each(elements, function(index) {
        $(elements[index]).show();
      });
    };


  }

})();