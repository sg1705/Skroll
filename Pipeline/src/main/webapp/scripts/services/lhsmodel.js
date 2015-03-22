'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.LHSModel
 * @description
 * # LHSModel
 * Factory in the SkrollApp.
 */

var LHSModel = {

	smodel: {
		terms: []
	},

	classes: [{
		id: 0,
		name: 'None',
		isSelected: false,
		isVisible: false
	},{
		id: 1,
		name: 'Definition',
		isSelected: true,
		isVisible: true
	}, {
		id: 2,
		name: 'Table of Contents',
		isSelected: true,
		isVisible: true
	}],

	removePara: function(paraId) {
		this.smodel.terms = _.reject(this.smodel.terms, function(obj) {
	    if (obj.paragraphId == paraId)
	          return true;
    });  
	},

	removeTerm: function(paraId, term) {
    LHSModel.smodel.terms = _.reject(LHSModel.smodel.terms, function(obj) {
      if ((obj.paragraphId == paraId) && (term == obj.term ))
          return true;
    });
	},

	getParagraphs: function(paraId) {
		var paras = _.filter(LHSModel.smodel.terms, function(obj){
			if (obj.paragraphId == paraId)
				return true;
		});
	},

  getClassNames: function() {
    var items = [];
    for (var ii = 0; ii < this.classes.length; ii++) {
      items.push(this.classes[ii].name);
    }
    return items;
  },

  getClassFromId: function(classId) {
    return _.find(this.classes, function(obj){
        if (obj.id == classId)
          return true;
    });
  }

};

angular.module('SkrollApp')
	.factory('LHSModel', function() {
		return LHSModel;
	});