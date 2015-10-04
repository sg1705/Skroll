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
		.service('contextMenuService', ContextMenuService)

	/* @ngInject */
	function ContextMenuService(textSelectionObserverService, $document, $mdToast, $mdUtil, $mdMedia, $animate, featureFlags, $analytics, documentModel) {

		//-- private variables
		var service = this;
		var isOpen = false;

		//-- public variables
		service.onSelection = onSelection;

		////////////

		/*
		* Invoke on text selection
		*/
		function onSelection(paragraphId) {

			if (featureFlags.isOn('trainer')) {
				return;
			}
			console.log('context menu invokved');
			var s = window.getSelection();
			var oRange = s.getRangeAt(0); //get the text range
			var oRect = oRange.getBoundingClientRect();			
 			$analytics.eventTrack(documentModel.documentId, { category: 'cm.contextMenuOpen', label: paragraphId });
	    $mdToast.show({
	      templateUrl : 'scripts/contextmenu/contextmenu.tmpl.html',
	      hideDelay 	: 6000,
	      onShow 			: onShow,
	      onRemove 		: onRemove,
	      theme 			: 'default-dark',
	      controller 	: 'ContextMenuCtrl',
	      controllerAs: 'ctrl',
	      hasBackdrop : true
	    });


	    //-- private
  	  var SWIPE_EVENTS = '$md.swipeleft $md.swiperight';
	    /*
	    * Override onShow of $mdToast
	    */
			function onShow(scope, element, options) {
				isOpen = true;
	      element = $mdUtil.extractElementByName(element, 'md-toast');

	      options.onSwipe = function(ev, gesture) {
	        //Add swipeleft/swiperight class to element so it can animate correctly
	        element.addClass('md-' + ev.type.replace('$md.',''));
	        $mdUtil.nextTick($mdToast.cancel);
	      };

	      /**  start below is the only part added to standard onShow method **/
	      var positionStyle = calculatePosition(oRect, options);
				$(element).css(positionStyle);
				options.hideBackdrop = showBackdrop(scope, element, options);
				/** end below is the only part added to standard onShow method **/

	      options.openClass = toastOpenClass(options.position);
	      // 'top left' -> 'md-top md-left'
	      options.parent.addClass(options.openClass);
	      element.on(SWIPE_EVENTS, options.onSwipe);
	      element.addClass(options.position.split(' ').map(function(pos) {
	        return 'md-' + pos;
	      }).join(' '));

	    //   $document.one('click.toast', clickHandler);

	    //   function clickHandler(e) {
					// scope.$apply(function(){
					// 	onRemove(scope, element, options);
					// });
					// return false;
					
	    //   }

				return $animate.enter(element, options.parent)
	      	.then(function(response){
		      		options.cleanupInteraction = activateInteraction(options);
		      		return response;
		      });
  	  }

			function toastOpenClass(position) {
      	return 'md-toast-open-' +
        	(position.indexOf('top') > -1 ? 'top' : 'bottom');
    	}

    	//function added for onShow
    	function calculatePosition(oRect, options) {
    		//how many pixels above or below
    		var CONTEXT_MENU_WIDTH = 200;
    		var CONTEXT_MENU_HEIGHT = 40;
    		//we can use $mdMedia
    		var SCREEN_WIDTH = 800;
    		var VERTICAL_OFFSET = 50;
    		var HORIZONTAL_OFFSET = CONTEXT_MENU_WIDTH/2; //200 is the min width context bar

    		var position = {
    			position: 'fixed',
    			opacity : 1
    		}

    		if (oRect.top < 90) {
    			position.top = oRect.bottom + VERTICAL_OFFSET - CONTEXT_MENU_HEIGHT;
    			options.position = 'top left';
    		} else {
    			position.top = oRect.top - VERTICAL_OFFSET;
    		}

    		//clear off the edges
    		var midpoint = oRect.left + (oRect.right - oRect.left)/2;
    		if (midpoint < HORIZONTAL_OFFSET) {
    			position.left = 0;
    		} else if (midpoint > SCREEN_WIDTH) {
    			position.left = SCREEN_WIDTH - CONTEXT_MENU_WIDTH;
    		} else {
    			position.left = midpoint - HORIZONTAL_OFFSET;
    		}
    		return position;

    	}

			function onRemove(scope, element, options) {
	      element.off(SWIPE_EVENTS, options.onSwipe);
	      options.parent.removeClass(options.openClass);
	      options.cleanupInteraction();
	      options.hideBackdrop();
	      return $animate.leave(element);
	    }


	    /**
	     * Show modal backdrop element...
	     * @returns {function(): void} A function that removes this backdrop
	     */
	    function showBackdrop(scope, element, options) {

	      // If we are not within a dialog...
	      if (options.disableParentScroll && !$mdUtil.getClosest(options.target, 'MD-DIALOG')) {
	        // !! DO this before creating the backdrop; since disableScrollAround()
	        //    configures the scroll offset; which is used by mdBackDrop postLink()
	        options.restoreScroll = $mdUtil.disableScrollAround(options.element, options.parent);
	      } else {
	        options.disableParentScroll = false;
	      }

	      if (options.hasBackdrop) {
	        options.backdrop = $mdUtil.createBackdrop(scope, "md-menu-backdrop md-click-catcher");
	        $animate.enter(options.backdrop, options.parent);
	      }

	      /**
	       * Hide and destroys the backdrop created by showBackdrop()
	       */
	      return function hideBackdrop() {
	        if (options.backdrop) {
	          // Override duration to immediately remove invisible backdrop
	          $animate.leave(options.backdrop, {duration:0});
	        }
	        if (options.disableParentScroll) {
	          options.restoreScroll();
	        }
	      }
	    }

      /**
       * Activate interaction on the menu. Wire up keyboard listerns for
       * clicks, keypresses, backdrop closing, etc.
       */
      function activateInteraction(opts) {

        // close on backdrop click
        if (opts.backdrop) opts.backdrop.on('click', onBackdropClick);

        return function cleanupInteraction() {
          if (opts.backdrop) opts.backdrop.off('click', onBackdropClick);
        };
	    }

      function onBackdropClick(e) {
        // e.preventDefault();
        // e.stopPropagation();
        //scope.$apply(function() {
          $mdToast.hide();
        //});
      }
         
		}

	}

	angular
		.module('app.contextmenu')
		.run(function(textSelectionObserverService, contextMenuService){
			textSelectionObserverService.register(contextMenuService.onSelection);
		});

})();