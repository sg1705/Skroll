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
		terms: [],
		levelTerms: []
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
		isSelected: false,
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
  },

  filterOutClassFromPara: function(classId, paraId) {
  	return [{
  		classificationId: classId,
  		paragraphId: paraId,
  		term: ''
  	}]
  },

  getParagraphsForClass: function(classId, paraId) {
		var paras = _.filter(LHSModel.smodel.terms, function(obj){
			if ((obj.paraId == paragraphId) && (obj.classificationId == classId))
				return true;
		});
		return paras;
  },

  getTermsForClass: function(classId) {
		var paras = _.filter(LHSModel.smodel.terms, function(obj){
			if ((obj.classificationId == classId))
				return true;
		});
		return paras;
  },

  /**
  * Creates temporary levels in the terms to pain TOC
  **/
  createLevels: function() {
    //for classId ==2 , check if a term starts with Item,
    // if so, then it is level1, otherwise level 2
    var terms = LHSModel.getTermsForClass(2);
    var levelTerms = [ ];
    for(var ii = 0; ii < terms.length; ii++) {
    	var level = new Object();
    	var term = terms[ii].term.toLowerCase();
      if (s.startsWith(term, 'item')) {
      	//level 1
      	level['level'] = 1;
      } else {
      	level['level'] = 2;
      }
     	level['paragraphId'] = terms[ii].paragraphId;
    	level['term'] = terms[ii].term;
    	levelTerms.push(level);
    }
    LHSModel.smodel.levelTerms = levelTerms;
    console.log(LHSModel.smodel.levelTerms);
  }

};

angular.module('SkrollApp')
	.factory('LHSModel', function() {
		return LHSModel;
	});