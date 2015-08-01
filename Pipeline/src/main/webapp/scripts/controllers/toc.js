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
		.module('SkrollApp')
		.controller('TocCtrl', ['LHSModel', 'ScrollObserverService', TocCtrl]);

  /**
  * Controller for the directive
  **/
  function TocCtrl(LHSModel, ScrollObserverService) {

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
    ScrollObserverService.register(observeScroll);

    /**
    * Toggles the section when user clicks on close/open in TOC
    **/
    function toggleSection(index) {
      ctrl.classes[index].isSelected = !this.classes[index].isSelected;
    }

    /**
    * Register for clicks
    **/
    function observeScroll(paraId) {
      
      if (paraId == null)
      	return null;
      ctrl.smodel.visibleHeaders = new Array();
      var headers = LHSModel.getParagraphs(paraId);
      if (headers.length > 0) {
      	ctrl.smodel.visibleHeaders.push(paraId);	
      	console.log("observing scroll from TocCtrl:" + paraId);
      }
    }

  }


})();
