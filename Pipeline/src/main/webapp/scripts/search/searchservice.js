(function() {

	'use strict';

	/**
	 * @ngdoc service
	 * @name myappApp.SearchService
	 * @description
	 * # searchService
	 * Service in the SkrollApp.
	 */



	angular
		.module('app.search')
		.service('searchService', SearchService);

	function SearchService($http, $q, $log, LHSModel) {

		//-- private variables
		var service = this;

		//-- public variables


		//-- public methods
		service.focusOnSearchBox = focusOnSearchBox;

		////////////

		function focusOnSearchBox() {
			var inputElement = $("#searchBox").find("input");
			if (inputElement.length > 0) {
				$(inputElement[0]).focus();
				LHSModel.smodel.hover = true;
			}
		}

	}

})();