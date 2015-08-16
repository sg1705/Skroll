(function(){

  angular
  	.module('app.toc')
    .controller('LHSLevelsCtrl', LHSLevelsCtrl);

  /* @ngInject */
  function LHSLevelsCtrl(LHSModel) {

  	//-- private variables
  	var vm = this;

  	//-- public variables
    vm.sections = LHSModel.sections;
    vm.smodel = LHSModel.smodel;
    vm.classes = LHSModel.classes;

    //-- public methods
    vm.toggleSection = toggleSection;

    function toggleSection(index) {
    	vm.classes[index].isSelected = !vm.classes[index].isSelected;	
    }
  }
	
})();