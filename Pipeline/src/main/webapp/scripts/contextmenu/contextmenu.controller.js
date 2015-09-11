(function() {

	'use strict';

	/**
	 * @ngdoc service
	 * @name myappApp.ContextMenuCtrl
	 * @description
	 * # ContextMenuCtrl
	 * ContextMenuCtrl in the SkrollApp.
	 */


	angular
		.module('app.contextmenu')
		.controller('ContextMenuCtrl' , ContextMenuCtrl);

	/* @ngInject */
  function ContextMenuCtrl($mdToast, $mdDialog, $mdUtil, linkService, selectionService, documentModel) {

  	//-- private variables
  	var vm = this;

  	//-- public methods
  	vm.closeContextMenu = closeContextMenu;
  	vm.copyLink 				= copyLink;
    vm.openTwitter      = openTwitter;
    vm.openEmailDialog  = openEmailDialog;
    vm.selectedText     = selectionService.selectedText;
    vm.copySelection    = copySelection;
  	////////

    

    /**
    * Copy email dialog
    **/
    function openEmailDialog() {
      closeContextMenu();
      var activeLink = linkService.getActiveLink(documentModel.documentId, selectionService.serializedSelection);      
      linkService.shortenLink(activeLink)
        .then(function(response) {
          console.log(response.result.id);
          selectionService.shortLink = response.result.id;
          var mailBody = createEmailText();
          window.open('mailto:?body=' + mailBody,'MsgWindow',
                               'toolbar=no,location=no, status=no,menubar=no,scrollbars=yes,resizable=yes,top=300, left=300,width=550,height=420');      
        }, function(reason) {
          $log.error(reason);
        });   
    }

    /**
    * Copy selection
    **/
    function copySelection() {
      closeContextMenu();
      $mdToast.show({
        template    : '<md-toast>Selection copied to clipboard</md-toast>',
        hideDelay   : 2500,
        theme       : 'default-dark',
        parent      : angular.element(":root")
      });
    }


    /**
    * Copy link to clipboard
    **/
  	function copyLink(link) {
      var activeLink = linkService.getActiveLink(documentModel.documentId, selectionService.serializedSelection);
      var shortenedUrl = '';
      linkService.shortenLink(activeLink)
        .then(function(response) {
          shortenedUrl = response.result.id;
        }, function(reason) {
          $log.error(reason);
        }).then(function(){
          return $mdToast.hide();
        }).then(function(){
          showAlert(shortenedUrl);
        });
  	}


    /**
    * Show dialog
    **/
    function showAlert(shortenedUrl) {
      var alert = $mdDialog.alert({
        title: 'Copy URL for Selected Text',
        content: shortenedUrl,
        ok: 'Close'
      });
      $mdDialog
        .show( alert )
        .finally(function() {
          alert = undefined;
        });
    }


    /**
    * Close context menu
    **/
  	function closeContextMenu() {
  		$mdUtil.nextTick($mdToast.hide);
  	}

    function createTweetText() {
      var selectedText = selectionService.selectedText;      
      var shortLink = selectionService.shortLink;
      var lengthOfTweetText = 140 - (shortLink.length + 6);
      var tweet;
      if (selectedText.length > lengthOfTweetText) {
        tweet = selectedText.substring(0, lengthOfTweetText) + " - " + shortLink;
      } else {
        tweet = selectedText + " - " + shortLink;
      }
      return tweet;
    }

    function createEmailText() {
      var selectedText = selectionService.selectedText;      
      var shortLink = selectionService.shortLink;
      var emailText = shortLink + ' -  ' + selectedText;
      return emailText;
    }



    /**
    * Open twitter tweet intent when link is clicked.
    **/
    function openTwitter() {
      closeContextMenu();
      var activeLink = linkService.getActiveLink(documentModel.documentId, selectionService.serializedSelection);      
      linkService.shortenLink(activeLink)
        .then(function(response) {
          console.log(response.result.id);
          selectionService.shortLink = response.result.id;
          var tweetText = createTweetText();
          window.open('https://twitter.com/intent/tweet?text=' + tweetText,'MsgWindow',
                               'toolbar=no,location=no, status=no,menubar=no,scrollbars=yes,resizable=yes,top=300, left=300,width=550,height=420');      

        }, function(reason) {
          $log.error(reason);
        });

    }

  }

})();