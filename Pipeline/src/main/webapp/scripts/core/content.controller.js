(function() {

  'use strict';

  /**
   * @ngdoc function
   * @name SkrollApp.controller:ContentCtrl
   * @description
   * # ContentCtrl
   * Controller of the SkrollApp
   */
  angular
    .module('app.core')
    .controller('ContentCtrl', ContentCtrl);

  /* @ngInject */
  function ContentCtrl(documentModel, documentService, $mdSidenav, $location, searchService, $scope) {

    //-- private variables
    var vm = this;

    //-- public variables
    vm.isDocAvailable = documentModel.isDocAvailable;
    vm.documentModel = documentModel;
    vm.userDocumentIds = [];
    vm.smodel = LHSModel.smodel;

    //-- public methods
    vm.getDocumentIds = getDocumentIds;
    vm.loadDocument = loadDocument;
    vm.toggleSidenav = toggleSidenav;
    vm.overrideControlF = overrideControlF;


    // $scope.targetHtml = documentModel.targetHtml;
    // $scope.definitions = [];
    // $scope.isEdit = false;
    // $scope.similarPara = [];

    //$scope.documentModel = documentModel;
    // $scope.documentService = documentService;
    // $scope.searchService = searchService;
    // $scope.$location = $location;


    function getDocumentIds() {
      documentService.getDocumentIds().then(function(documentIds) {
        vm.userDocumentIds = documentIds;
      });
    }

    function loadDocument(documentId) {
      vm.documentModel.isProcessing = true;
      $location.path('/view/docId/' + documentId);
    }

    //toggle side navigation
    function toggleSidenav(menuId) {
      //check to see if we need to get json
      $mdSidenav(menuId).toggle();
    };

    function overrideControlF($event) {
      if ((event.keyCode == 114) || (event.ctrlKey && event.keyCode == 70)) {
        // Block CTRL + F event
        event.preventDefault();
        searchService.focusOnSearchBox();
      }
    };

    vm.getDocumentIds();
  }

})();

// 'use strict';

// /**
//  * @ngdoc function
//  * @name SkrollApp.controller:ContentCtrl
//  * @description
//  * # ContentCtrl
//  * Controller of the SkrollApp
//  */
// angular.module('SkrollApp')
//   .controller('ContentCtrl', ['documentModel', 'documentService', '$scope', '$mdSidenav', '$http', '$mdMedia', '$location', 'searchService',
//     function(documentModel, documentService, $scope, $mdSidenav, $http, $mdMedia, $location, searchService) {
//       $scope.targetHtml = documentModel.targetHtml;
//       $scope.isDocAvailable = documentModel.isDocAvailable;
//       $scope.fileName = documentModel.fileName;
//       $scope.definitions = [];
//       $scope.isEdit = false;
//       $scope.similarPara = [];
//       $scope.userDocumentIds = [];
//       $scope.documentModel = documentModel;
//       $scope.documentService = documentService;
//       $scope.searchService = searchService;
//       $scope.$location = $location;
//       $scope.smodel = LHSModel.smodel;

//       $scope.getDocumentIds = function() {
//         documentService.getDocumentIds().then(function(documentIds) {
//           $scope.userDocumentIds = documentIds;
//         });
//       }

//       $scope.loadDocument = function(documentId) {
//         $scope.documentModel.isProcessing = true;
//         $location.path('/view/docId/' + documentId);
//       }

//       //toggle side navigation
//       $scope.toggleSidenav = function(menuId) {
//         //check to see if we need to get json
//         $mdSidenav(menuId).toggle();
//       };

//       //toggle side navigation
//       $scope.showSimilar = function() {
//         var selectedParagraphId = $scope.selectedParagraphId;
//         documentService.getSimilarPara(selectedParagraphId).then(function(paragraphs) {
//           $scope.definitions = paragraphs;

//         }, function(errMsg) {
//           console.log(errMsg);
//         });
//         $mdSidenav('left').toggle();
//       };

//       //click on edit button
//       $scope.toggleEdit = function() {
//         $scope.isEdit = !$scope.isEdit;
//       };

//       $scope.overrideControlF = function($event) {
//         if ((event.keyCode == 114) || (event.ctrlKey && event.keyCode == 70)) {
//           // Block CTRL + F event
//           event.preventDefault();
//           searchService.focusOnSearchBox();
//         }
//       };

//       // /*
//       // * Someday I should remove this method. Not being used
//       // */
//       // $scope.contentClicked = function($event) {
//       //     var foundId = false;
//       //     var paraId;
//       //     //find the paragraph element
//       //     //children
//       //     var ids = $($event.target).find("a");
//       //     for(var ii = 0; ii < ids.length; ii++) {
//       //         if ($(ids[ii]).attr("name") != null) {
//       //             foundId = true;
//       //             $(ids[ii]).parent().css("background-color","yellow");
//       //             paraId = $(ids[ii]).attr("name");
//       //             $("#rightPanel").html()
//       //         }
//       //     }

//       //     //now try siblings
//       //     //TODO need to refactor this properly

//       //     if (!foundId) {
//       //         ids = $($event.target).prevAll("a");
//       //     }

//       //     for(var ii = 0; ii < ids.length; ii++) {
//       //         if ($(ids[ii]).attr("name") != null) {
//       //             foundId = true;
//       //             $(ids[ii]).parent().css("background-color","yellow");
//       //             paraId = $(ids[ii]).attr("name");
//       //         }
//       //     }
//       // };

//       //### hack for iPhone
//       //this code is a hack to get it working on iPhone
//       // angular.element(document).ready(function() {
//       //     if (navigator.platform.indexOf("iPhone") != -1) {
//       //         $scope.isProcessing = true;
//       //         $http.get('test/AMC-Networks-CA.html')
//       //         .success(function(data, status, headers, config) {
//       //             var boundary = (new Date()).getTime();
//       //             var bodyParts = new Array();
//       //             bodyParts.push(
//       //                   '--' + boundary,
//       //                   'Content-Disposition: form-data; name="files[]"; filename="random"',
//       //                   'Content-Type: text/html',
//       //                   '',
//       //                   data);
//       //             bodyParts.push('--' + boundary + '--');
//       //             var bodyString = bodyParts.join('\r\n');
//       //             $http.post('restServices/jsonAPI/upload', bodyString, {
//       //                 headers: {
//       //                     'Content-Type': 'multipart/form-data; boundary=' + boundary
//       //                 }
//       //             }).success(function(data) {
//       //                     $scope.targetHtml = data;
//       //                     $scope.isDocAvailable = true;
//       //                     $scope.isProcessing = false;
//       //                     $("#content").html(data);
//       //             });

//       //             // this callback will be called asynchronously
//       //             // when the response is available
//       //         })
//       //     }
//       // });
//       $scope.getDocumentIds();


//     }
//   ]);