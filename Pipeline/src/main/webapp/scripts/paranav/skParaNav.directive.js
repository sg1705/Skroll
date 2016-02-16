(function(){

  'use strict';

  /**
   * @ngdoc directive
   * @name app.directive:skParaNav
   * @description
   * # skParaNav
   */
  angular
    .module('app.paranav')
      .directive('skParaNav', SkParaNav);

    function SkParaNav() {

      return {
        template: getTemplate,
        restrict: 'E',
        scope: {},
        controller: 'SkParaNavCtrl',
        controllerAs: 'ctrl',
        bindToController: {
          items: '=',
          onClose: '&'
        }
      };


      /**
      * Returns template to render the widget based on attributes
      **/
      function getTemplate(element, attrs) {
        //takes a span tag
        var headerNavHtml = getHtmlFromTemplate(element, attrs, 'sk-nav-header');
        var navHeader = '';
        if (headerNavHtml != '') {
          navHeader =     '<div layout="row" class="sk-nav-header"> \
                            <div class="md-primary sk-nav-header-template">' +
                              headerNavHtml +
                            '</div> \
                            <div class="sk-nav-back-icon"> \
                              <md-icon ng-click="ctrl.close()" md-svg-icon="images/icons/ic_close_24px.svg"></md-icon> \
                            </div> \
                          </div>';
        }




        var footerNavHtml = getHtmlFromTemplate(element, attrs, 'sk-nav-footer');
        var paraTemplate = getParaTemplate(element, attrs);

        element.empty();
        var template = '<div layout="column" class="sk-para-nav">' +
                          navHeader +
                          paraTemplate +
                          footerNavHtml +
                        '</div>'
        return template;
      }

      //-- helper function

      function getParaTemplate(element, attrs) {
        var paraHeader = getHtmlFromTemplate(element, attrs, 'sk-header');
        var paraFooter = getHtmlFromTemplate(element, attrs, 'sk-footer');
        var paraBody = getHtmlFromTemplate(element, attrs, 'sk-para-body');

        var paraTemplate = '<div layout="column" ng-repeat="item in ctrl.items">' +
                              '<div layout="column" class="sk-para-item">' +
                                '<div>' + paraHeader + '</div>' +
                                '<div>' + paraBody + '</div>' +
                                '<div>' + paraFooter + '</div>' +
                              '</div> \
                            </div>';

        return paraTemplate;
      }


      function getHtmlFromTemplate(element, attrs, tag) {
        var templateTag = element.find(tag).detach();
        var html = templateTag.length ? templateTag.html() : '';
        return html;
      }

    };



})();
