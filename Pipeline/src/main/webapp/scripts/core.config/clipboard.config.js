(function(){


  'use strict';

  /**
   * @ngdoc overview
   * @name SkClipboardConfig
   * @description
   * # SkClipboardConfig
   *
   * Configure Zero Clipboard
   */

  angular
    .module('SkrollApp')
    .config(['uiZeroclipConfigProvider', SkClipboardConfig]);

  /* @ngInject */
  function SkClipboardConfig(uiZeroclipConfigProvider) {
    // config ZeroClipboard
    uiZeroclipConfigProvider.setZcConf({
      swfPath: 'other/ZeroClipboard.swf'
    });
  }

})();