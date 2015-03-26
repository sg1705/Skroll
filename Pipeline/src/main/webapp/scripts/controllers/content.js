'use strict';

/**
 * @ngdoc function
 * @name SkrollApp.controller:ContentCtrl
 * @description
 * # ContentCtrl
 * Controller of the SkrollApp
 */
angular.module('SkrollApp')
.controller('ContentCtrl', ['documentModel','documentService', '$scope', '$mdSidenav', '$http', '$mdMedia',
  function(documentModel, documentService, $scope, $mdSidenav, $http) {
            $scope.targetHtml = documentModel.targetHtml;
            $scope.isDocAvailable = documentModel.isDocAvailable;
            $scope.fileName = documentModel.fileName;
            $scope.isProcessing = documentModel.isProcessing;
            $scope.definitions = [ ];
            $scope.isEdit = false;
            $scope.similarPara = [ ];

            //toggle side navigation
            $scope.toggleSidenav = function(menuId) {
                //check to see if we need to get json
                $mdSidenav(menuId).toggle();
              };

            //toggle side navigation
            $scope.showSimilar = function() {
                var selectedParagraphId = $scope.selectedParagraphId;
                documentService.getSimilarPara(selectedParagraphId).then(function(paragraphs){
                    $scope.definitions = paragraphs;

                }, function(errMsg){
                    console.log(errMsg);
                });
                $mdSidenav('left').toggle();
            };

            //click on edit button
            $scope.toggleEdit = function() {
                $scope.isEdit = !$scope.isEdit;
            };

            // /*
            // * Someday I should remove this method. Not being used
            // */
            // $scope.contentClicked = function($event) {
            //     var foundId = false;
            //     var paraId;
            //     //find the paragraph element
            //     //children
            //     var ids = $($event.target).find("a");
            //     for(var ii = 0; ii < ids.length; ii++) {
            //         if ($(ids[ii]).attr("name") != null) {
            //             foundId = true;
            //             $(ids[ii]).parent().css("background-color","yellow");
            //             paraId = $(ids[ii]).attr("name");
            //             $("#rightPanel").html()
            //         }
            //     }

            //     //now try siblings
            //     //TODO need to refactor this properly

            //     if (!foundId) {
            //         ids = $($event.target).prevAll("a");
            //     }

            //     for(var ii = 0; ii < ids.length; ii++) {
            //         if ($(ids[ii]).attr("name") != null) {
            //             foundId = true;
            //             $(ids[ii]).parent().css("background-color","yellow");
            //             paraId = $(ids[ii]).attr("name");
            //         }
            //     }

            //     if (foundId) {
            //         documentService
            //         .getParagraphJson(paraId)
            //         .then(function(data) {
            //             $("#rightPane").html(JSON.stringify(data, null, 2));
            //             $scope.selectedParagraphId = paraId;
            //             documentModel.selectedParagraphId = paraId;
            //         },function(data, status) {
            //             console.log(status);
            //         });
            //     }
            // };

            //### hack for iPhone
            //this code is a hack to get it working on iPhone
            angular.element(document).ready(function() {
                if (navigator.platform.indexOf("iPhone") != -1) {
                    $scope.isProcessing = true;
                    $http.get('test/AMC-Networks-CA.html')
                    .success(function(data, status, headers, config) {

                        //create a multiple part request
                        // I got help from here http://goo.gl/Z8WYlQ
                        var boundary = (new Date()).getTime();
                        var bodyParts = new Array();
                        bodyParts.push(
                              '--' + boundary,
                              'Content-Disposition: form-data; name="files[]"; filename="random"',
                              'Content-Type: text/html',
                              '',
                              data);
                        bodyParts.push('--' + boundary + '--');
                        var bodyString = bodyParts.join('\r\n');

                        //post
                        $http.post('restServices/jsonAPI/upload', bodyString, {
                            headers: {
                                'Content-Type': 'multipart/form-data; boundary=' + boundary
                            }
                        }).success(function(data) {
                                $scope.targetHtml = data;
                                $scope.isDocAvailable = true;
                                $scope.isProcessing = false;
                                $("#content").html(data);
                        });

                        // this callback will be called asynchronously
                        // when the response is available
                    })
                }
            });

        }]);