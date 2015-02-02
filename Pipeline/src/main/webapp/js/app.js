(function() {

    var documentModel = {
            targetHtml: "",
            isDocAvailable: false,
            isProcessing: false,
            fileName: ""
    };

    var skrollApp = angular.module('SkrollApp', ['ngMaterial','ngSanitize' ]);

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


    skrollApp.controller('ContentController', ['documentModel', '$scope', '$mdSidenav', "$http",
                    function(documentModel, $scope, $mdSidenav, $http){
        $scope.targetHtml = documentModel.targetHtml;
        $scope.isDocAvailable = documentModel.isDocAvailable;
        $scope.fileName = documentModel.fileName;
        $scope.isProcessing = documentModel.isProcessing;
        $scope.definitions = [ ];
        $scope.toggleSidenav = function(menuId) {
            //get json
            $http.get('test.json').success(function(data) {
                $scope.definitions = [ ];
                for(var ii = 0; ii < data.length; ii++) {
                    //get all definitions
                    for(var jj =0; jj < data[ii].definedTerms.length; jj++) {
                        if (data[ii].definedTerms[jj] != '') {
                            var def = {};
                            def.paragraphId = data[ii].paragraphId;
                            def.definition = data[ii].definedTerms[jj];
                            $scope.definitions.push(def);
                        }
                    }
                }
            })

            $mdSidenav(menuId).toggle();
        };


    }]);

})();
