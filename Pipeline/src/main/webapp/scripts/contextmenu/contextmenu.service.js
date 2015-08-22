(function() {

	'use strict';

	/**
	 * @ngdoc service
	 * @name myappApp.ContextMenuService
	 * @description
	 * # contextMenuService
	 * Service in the SkrollApp.
	 */



	angular
		.module('app.contextmenu')
		.service('contextMenuService', SearchService)
		.controller('ContentMenuCtrl' , ContentMenuCtrl);

	/* @ngInject */
  function ContentMenuCtrl($mdToast) {

  	//-- private variables
  	var vm = this;

  	//-- public methods
  	vm.closeContextMenu = closeContextMenu;
  	vm.copyLink 				= copyLink;

  	////////

  	function copyLink(link) {
  		$mdToast.hide();
  	}


  	function closeContextMenu() {
  		$mdToast.hide();
  	}
  }



	/* @ngInject */
	function SearchService(textSelectionObserverService, $mdToast, $mdUtil, $animate, featureFlags) {

		//-- private variables
		var service = this;
		var isOpen = false;

		//-- public variables
		service.onSelection = onSelection

		////////////

		function onSelection(paragraphId) {

			if (featureFlags.isOn('trainer')) {
				return;
			}
			console.log('context menu invokved');
			var s = window.getSelection();
			var oRange = s.getRangeAt(0); //get the text range
			var oRect = oRange.getBoundingClientRect();			
			console.log(oRect);

	    //$mdToast.show($mdToast.simple().content('Hello!'));
	    $mdToast.show({
	      templateUrl : 'scripts/contextmenu/contextmenu.tmpl.html',
	      hideDelay 	: 0,
	      onShow 			: onShow,
	      theme 			: 'default-dark',
	      controller 	: 'ContentMenuCtrl',
	      controllerAs: 'ctrl'
	    });

	    //override onShow method
			function onShow(scope, element, options) {
				isOpen = true;
				console.log('calling on show');
	      //activeToastContent = options.content;

	      element = $mdUtil.extractElementByName(element, 'md-toast');

	      options.onSwipe = function(ev, gesture) {
	        //Add swipeleft/swiperight class to element so it can animate correctly
	        element.addClass('md-' + ev.type.replace('$md.',''));
	        $mdUtil.nextTick($mdToast.cancel);
	      };

	      options.openClass = toastOpenClass(options.position);


	      // 'top left' -> 'md-top md-left'
	      options.parent.addClass(options.openClass);
	      element.on(SWIPE_EVENTS, options.onSwipe);
	      element.addClass(options.position.split(' ').map(function(pos) {
	        return 'md-' + pos;
	      }).join(' '));

				$(element).css({position: 'fixed', top: oRect.top - 100, left: (oRect.left + (oRect.right - oRect.left)/2), opacity: 0});
				$(options.parent).append($(element));
				$(element).animate({opacity: 1});
	      // return $animate.enter(element, options.parent)
	      // 		.then(function() {
	      // 			//$(element).css({position: 'fixed', top: '700px'});
	      // 			$(element).animate({position: 'fixed', top: '160px', opatcity: 1})});
	      	//.then($animateCss(element, {from: {height: '0px'}, to: {height: '160px'}, duration: 1}));
  	  }

  	  var SWIPE_EVENTS = '$md.swipeleft $md.swiperight';

			function toastOpenClass(position) {
      	return 'md-toast-open-' +
        	(position.indexOf('top') > -1 ? 'top' : 'bottom');
    	}



		}

	}

	angular
		.module('app.contextmenu')
		.run(function(textSelectionObserverService, contextMenuService){
			textSelectionObserverService.register(contextMenuService.onSelection);
		})



})();