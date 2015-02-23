(function() {

    var documentModel = {
            targetHtml: "",
            isDocAvailable: false,
            isProcessing: false,
            fileName: "",
    };

    var skrollApp = angular.module('SkrollApp', ['ngMaterial','ngSanitize', 'ngTouch' ]);

    skrollApp.factory('documentModel', function () {
        return documentModel;
    });



    skrollApp.directive('fileUpload', ['documentModel', function(documentModel) {
        return {
            restricted: 'A',
            link: function(scope, element, attrs) {
             $(element).fileupload({
                    dataType: 'text',
                    add: function (e, data) {
                        scope.$apply(function() {
                            scope.isProcessing = true;
                            documentModel.isProcessing = true;
                            documentModel.fileName = data.files[0].name;
                            scope.fileName = data.files[0].name;
                        })
                        data.submit();
                    },
                    done: function (e, data) {
                        console.log("Done setting");
                        $("#content").html(data.result);
                        scope.$apply(function() {
                            scope.targetHtml = data.result;
                            scope.isDocAvailable = true;
                            scope.isProcessing = false;
                            documentModel.isDocAvailable = true;
                            documentModel.targetHtml = data.result;
                            documentModel.isProcessing = false;
                            console.log("Done assigning");

                        });
                    },
                    fail: function (e, data) {
                        console.log("failed");
                        console.log(e);
                    }
                });
            }
        }

    }]);


    skrollApp.directive('scrollToParagraph', ['documentModel', function(documentModel) {
        return {
            restricted: 'A',
            link: function(scope, element, attrs) {
                var paragraphId = attrs.scrollToParagraph;
                var para =
                $(element).click(function() {
                    var para = $("#"+paragraphId);
                    if (para != null) {
                        $("#content").animate({scrollTop: ($("#content").scrollTop() - 200 + $(para).offset().top)}, "slow");
                        $(para).parent().css("background-color","yellow");
                        scope.toggleSidenav('left');
                    }
                });
            }

        }

    }]);


    skrollApp.controller('ContentController', ['documentModel', '$scope', '$mdSidenav', "$http",
                    function(documentModel, $scope, $mdSidenav, $http){
        $scope.targetHtml = documentModel.targetHtml;
        $scope.isDocAvailable = documentModel.isDocAvailable;
        $scope.fileName = documentModel.fileName;
        $scope.isProcessing = documentModel.isProcessing;
        $scope.definitions = [ ];
        $scope.isEdit = false;

        //toggle side navigation
        $scope.toggleSidenav = function(menuId) {
            //check to see if we need to get json
            if ($scope.definitions.length == 0) {
                //get json
                //TODO add a line for failure
                $http.get('restServices/jsonAPI/getDefinition').success(function(data) {
                    $scope.definitions = [ ];
                    for(var ii = 0; ii < data.length; ii++) {
                        var def = {};
                        def.paragraphId = data[ii].paragraphId;
                        def.definition = data[ii].definedTerm;
                        $scope.definitions.push(def);

                    }
                }).error(function(data, status) {
                    console.log(status);
                });
            }
            $mdSidenav(menuId).toggle();
        };

        //click on edit button
        $scope.toggleEdit = function() {
            $scope.isEdit = !$scope.isEdit;
        };

        $scope.contentClicked = function($event) {
            var foundId = false;
            var paraId;
            //find the paragraph element
            //children
            var ids = $($event.target).find("a");
            for(var ii = 0; ii < ids.length; ii++) {
                if ($(ids[ii]).attr("name") != null) {
                    foundId = true;
                    $(ids[ii]).parent().css("background-color","yellow");
                    paraId = $(ids[ii]).attr("name");
                    $("#rightPanel").html()
                }
            }

            //now try siblings
            //TODO need to refactor this properly

            if (!foundId) {
                ids = $($event.target).prevAll("a");
            }

            for(var ii = 0; ii < ids.length; ii++) {
                if ($(ids[ii]).attr("name") != null) {
                    foundId = true;
                    $(ids[ii]).parent().css("background-color","yellow");
                    paraId = $(ids[ii]).attr("name");
                }
            }

            if (foundId) {
                $http.get('restServices/jsonAPI/getParagraphJson?paragraphId=' + paraId).success(function(data) {
                    $("#rightPane").html(JSON.stringify(data, null, 2));
                }).error(function(data, status) {
                    console.log(status);
                });
            }

        };

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

    //** when newer version of material comes out
    //this is lifted from http://goo.gl/mrWZ0F
    skrollApp.config(function($mdIconProvider) {
        $mdIconProvider
          .iconSet('viewer', 'img/icons/sets/viewer-24.svg', 24);
    });

})();
