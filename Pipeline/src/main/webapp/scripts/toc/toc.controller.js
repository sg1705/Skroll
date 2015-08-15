(function(){
	'use strict';

	/**
	 * @ngdoc function
	 * @name skrollApp.controller:TocCtrl
	 * @description
	 * # TocCtrl
	 * Controller of the TocCtrl
	 */

	angular
		.module('app.toc')
		.controller('TocCtrl', ['LHSModel', 'scrollObserverService', TocCtrl]);

  /**
  * Controller for the directive
  **/
  function TocCtrl(LHSModel, scrollObserverService) {

    //-- private variables
    var ctrl      = this,
    		LHSModel	= LHSModel;

    //-- public variables
    ctrl.classes            = LHSModel.classes;
    ctrl.smodel             = LHSModel.smodel;
    ctrl.sections           = LHSModel.sections;

    //-- public methods
    ctrl.toggleSection      = toggleSection;

    //-- register for notification
    scrollObserverService.register(observeScroll);

    /**
    * Toggles the section when user clicks on close/open in TOC
    **/
    function toggleSection(index) {
      ctrl.classes[index].isSelected = !this.classes[index].isSelected;
    }

    /**
    * Callback to handle a scroll event
    **/
    function observeScroll(paraId) {
      
      if (paraId == null)
      	return null;
      ctrl.smodel.visibleHeaders = new Array();
      var headerItems = LHSModel.getParaFromClassIdRange(2,4);
      if (headerItems.length > 0) {
        //convert clicked paragraphId to integer
        var levelsPara = [];
        var paraStr = paraId.split("_")[1];
        var paraIdInt = parseInt(paraStr);
        var nearestHeader;
        
        //convert header paragraphIds to itegrations
        for (var ii = 0; ii < headerItems.length; ii++) {
          var str = headerItems[ii].paragraphId.split("_")[1];
          var paraIdd = parseInt(str, 10);
          levelsPara.push(paraIdd);
        }

        //iterate over each header
        for (var ii = 0; ii < headerItems.length; ii++) {
          //if the current header is the same as clicked paragraph
          if (headerItems[ii].paragraphId == paraId) {
            nearestHeader = paraId;
            break;
          }
          //if the current header has passed the paragraph
          if (levelsPara[ii] >= paraIdInt) {
            break;
          }
          nearestHeader = headerItems[ii].paragraphId;
        }
      	ctrl.smodel.visibleHeaders.push(nearestHeader);	
      }
    }

  }


})();
