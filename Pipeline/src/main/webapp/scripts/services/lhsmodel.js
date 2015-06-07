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
		levelTerms: [],
    hover: false
	},

	classes: [{
		id: 0,
		name: 'None',
		isSelected: false,
		isVisible: false,
    isActive: false
	},

  {
    id: 1,
    name: 'Definition',
    isSelected: false,
    isVisible: true,
    isActive: true
  },


  {
		id: 2,
		name: 'Table of Contents',
		isSelected: true,
		isVisible: true,
    isActive: true
	},
  {
    id: 3,
    name: 'TOC Level 2',
    isSelected: false,
    isVisible: false,
    isActive: false
  },
  {
    id: 4,
    name: 'TOC Level 3',
    isSelected: false,
    isVisible: false,
    isActive: false
  },
  {
    id: 5,
    name: 'TOC Level 4',
    isSelected: false,
    isVisible: false,
    isActive: false
  },
  {
    id: 6,
    name: 'TOC Level 5',
    isSelected: false,
    isVisible: false,
    isActive: false
  },

  {
    id: 7,
    name: 'Bookmarks',
    isSelected: false,
    isVisible: false,
    isActive: false
  },

  ],

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


  getParaFromClassIdRange: function(startClassId, endClassId) {
    var terms = [];
    var prevTerm = null;
    for(var ii = 0; ii < LHSModel.smodel.terms.length; ii++) {
      var term = LHSModel.smodel.terms[ii];
      if ((term.classificationId >= startClassId) && (term.classificationId <= endClassId)) {
        if (term.term != prevTerm) {
          terms.push(term);
        }
      }
      prevTerm = term.term;
    }
    return terms;
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
			if ((obj.paragraphId == paraId) && (obj.classificationId == classId))
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

  addBookmark: function(classId, paraId, termText, serializedSelection) {
    var term = new Object();
    term['classificationId'] = classId;
    term['paragraphId'] = paraId;
    term['term'] = termText;
    term['serializedSelection'] = serializedSelection;
    this.smodel.terms.push(term);
  },

  setActiveClasses: function(terms) {
    //anything that is not visible cannot be active
    for(var ii = 0; ii < this.classes.length; ii++) {
      if (this.classes[ii].isVisible) {
        //get terms for classId
        var classId = this.classes[ii].id;
        var paras = _.filter(terms, function(obj){
          if ((obj.classificationId == classId))
            return true;
          });

        if (paras.length > 0) {
          this.classes[ii].isActive = true;
          console.log("active is true"+this.classes[ii].id);
        } else {
          this.classes[ii].isActive = false;
          console.log("active is false"+this.classes[ii].id);
        }

      } else {
        this.classes[ii].isActive = false;
      }
    }
  },

  setTerms: function (terms) {
    this.smodel.terms = terms;
    this.setActiveClasses(terms);
  }

};

angular.module('SkrollApp')
	.factory('LHSModel', function() {
		return LHSModel;
	});