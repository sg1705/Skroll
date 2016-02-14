(function() {
  /**
   * @ngdoc function
   * @name skrollApp.controller:ViewPortCtrl
   * @description
   * # ViewPortCtrl
   * Controller of the ViewPortCtrl
   */


  angular
    .module('app.core.html')
    .controller('HtmlViewPortCtrl', HtmlViewPortCtrl);

  /* @ngInject */
  function HtmlViewPortCtrl(selectionService, $log, $routeParams,
    scrollObserverService, clickObserverService,
    textSelectionObserverService,
    mouseEnterObserverService, mouseLeaveObserverService, $timeout, viewportService, documentService, documentModel, featureFlags) {

    //-- private variables
    var vm = this;

    //-- public methods
    vm.mouseDown = mouseDown;
    vm.mouseUp = mouseUp;
    vm.mouseMove = mouseMove;
    vm.paraClicked = paraClicked;
    vm.inferParagraphId = inferParagraphId;
    vm.resizeFrame = resizeFrame;
    vm.resetFrameHeight = resetFrameHeight;
    vm.fabMenu = documentModel.viewState.fabMenu;

    //-- initialization
    // documentModel.documentId = $routeParams.docId;
    selectionService.serializedSelection = decodeURIComponent(decodeURIComponent($routeParams.linkId));


    /////////////

    function mouseMove($event) {
      if (!featureFlags.isOn('fab.link'))
        return;
      //clientY is what we are looking for in event
      //logic
      var paraId = vm.inferParagraphId($event);
      var mouseEnterEventPayLoad = {};
      //paragraph is null when a) when page loads, or b) cursor goes outside a paragraph
      //hoverbox is null when a) when page loads or b) cursor goes out of Y bound of paragraph in curtains
      if (paraId == null) {
        if (vm.fabMenu.currentParaZone == null) {
          return;
        } else {
          if (isClickInsideParaBox($event.clientY, vm.fabMenu.currentParaZone.top, vm.fabMenu.currentParaZone.bottom)) {
            //do nothing
            return;
          } else {
            mouseEnterEventPayLoad.command = 'hide';
            vm.fabMenu.currentParaZone = null;
            vm.fabMenu.currentParaId = null;
          }
        }
      } else {
        // user is in the same para zone, keep displaying
        if (vm.fabMenu.currentParaId == paraId) {
          return;
        } else {
          // user has changed paragraph zone, display fab at a new location
          vm.fabMenu.currentParaZone = selectionService.getJQParaElement(paraId)[0].getBoundingClientRect();
          vm.fabMenu.currentParaId = paraId;
          mouseEnterEventPayLoad.command = 'display';
          mouseEnterEventPayLoad.clientY = vm.fabMenu.currentParaZone.top;
        }
      }

      mouseEnterObserverService.notify(mouseEnterEventPayLoad);

      function isClickInsideParaBox(clickY, paraTop, paraBottom) {
        if ((clickY >= paraTop) && (clickY <= paraBottom)) {
          return true;
        } else {
          return false;
        }
      }


    }

    function mouseDown($event) {
      var paraId = vm.inferParagraphId($event);
      selectionService.clearSelection();
      selectionService.mouseDownParaId = paraId;
      selectionService.mouseDownX = $event.clientX;
      selectionService.mouseDownY = $event.clientY;
    }

    function mouseUp($event) {
      //should mouse click handle it
      //find out if this is a selection
      if (selectionService.getRangySelection().toString() != '') {
        //rangy.getSelection().expand("word", { trim: true });
      }

      var selection = selectionService.getWindowSelection().toString();
      if ((selection == '') || (selection == undefined))
        return;

      var paraId = vm.inferParagraphId($event);
      selectionService.mouseUpX = $event.clientX;
      selectionService.mouseUpY = $event.clientY;

      var selectionBoundingBox = selectionService.getSelectionBoundingBox();
      if (paraId == null)
        return;

      textSelectionObserverService.notify({
        'paraId': paraId,
        'selectedText': selection,
        'selectionBoundingBox': selectionBoundingBox
      });
      selectionService.saveSelection(paraId, selection);
    }


    function paraClicked($event) {
      console.log("Paragraph clicked");
      //find out if this is a selection
      var selection = selectionService.getWindowSelection().toString();
      //check to see if mouseup should handle it
      if (selection != '' || (selection == undefined))
        return;
      //clear highlight
      selectionService.clearSelection();
      var paraId = vm.inferParagraphId($event);
      if (paraId == null)
        return;

      //store in selectionService
      selectionService.paragraphId = paraId;
      scrollObserverService.notify(paraId);
      clickObserverService.notify(paraId);

    }


    function inferParagraphId($event) {
      var parents = $($event.target).parents("div[id^='p_']");
      var children = $($event.target).children("div[id^='p_']");
      if (parents.length > 1) {
        return $(parents[0]).attr('id');
      } else {

        if ((children.length == 0) && (parents.length == 1)) {
          return $(parents[0]).attr('id');
        }
        return null;
      }

    }

    function resizeFrame() {

      var iframeDiv = document.getElementById("docViewIframe");
      var toolbarDiv = document.getElementById("toolbar");
      var iframeBody = document.getElementById("docViewIframe").contentWindow.document.body;

      //for iPhone, load the width first and then set the height
      if (navigator.userAgent.indexOf('iPhone') > -1) {
        $timeout(function() {
          iframeDiv.width = toolbarDiv.offsetWidth + "px";
          $timeout(function() {
            iframeDiv.height = iframeBody.offsetHeight + "px";
          }, 0);

        }, 0);
        return;
      }

      iframeDiv.height = iframeBody.offsetHeight + "px";
      iframeDiv.width = toolbarDiv.offsetWidth + "px";
      console.log(toolbarDiv.offsetWidth);
    }

    function resetFrameHeight() {
      var iframeDiv = document.getElementById("docViewIframe");
      iframeDiv.height = 0;
      resizeFrame();
    }

  }

})();