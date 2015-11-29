(function() {
  /**
   * @ngdoc function
   * @name skrollApp.controller:ViewPortCtrl
   * @description
   * # ViewPortCtrl
   * Controller of the ViewPortCtrl
   */


  angular
    .module('app.core')
    .controller('ViewPortCtrl', ViewPortCtrl);

  /* @ngInject */
  function ViewPortCtrl(selectionService, $log, $routeParams,
    scrollObserverService, clickObserverService,
    textSelectionObserverService,
    mouseEnterObserverService, mouseLeaveObserverService, $timeout) {

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

    //-- initialization
    documentModel.documentId = $routeParams.docId;
    selectionService.serializedSelection = decodeURIComponent(decodeURIComponent($routeParams.linkId));


    /////////////

    function mouseMove($event) {
      var paraId = vm.inferParagraphId($event);

    }

    function mouseDown($event) {
      var paraId = vm.inferParagraphId($event);
      selectionService.mouseDownParaId = paraId;
    }

    function mouseUp($event) {
      console.log("mouseup clicked");
      //should mouse click handle it
      //find out if this is a selection
      if (selectionService.getRangySelection().toString() != '') {
        //rangy.getSelection().expand("word", { trim: true });
      }

      var selection = selectionService.getWindowSelection().toString();
      if ((selection == '') || (selection == undefined))
        return;

      //clear selection
      selectionService.clearSelection();
      var paraId = vm.inferParagraphId($event);
      if (paraId == null)
        return;
      textSelectionObserverService.notify({
        'paraId': paraId,
        'selectedText': selection
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