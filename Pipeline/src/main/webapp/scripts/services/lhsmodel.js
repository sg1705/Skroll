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
    visibleHeaders: []
  },

  /*
    isSelected: Whether this is default selected or not
    isVisible: Whether to show it in the viewer
    isActive:
    isTrainerLHS: Whether this will appear in trainerLHS, by default false

  */
  classes: [{
      id: 0,
      name: 'None',
      isSelected: false,
      isVisible: false,
      isActive: false,
      isTrainerLHS: false
    },

    {
      id: 1,
      name: 'Definition',
      isSelected: false,
      isVisible: true,
      isActive: true,
      isTrainerLHS: false
    },


    {
      id: 2,
      name: 'Table of Contents',
      isSelected: true,
      isVisible: true,
      isActive: true,
      isTrainerLHS: false

    }, {
      id: 3,
      name: 'TOC Level 2',
      isSelected: false,
      isVisible: false,
      isActive: false,
      isTrainerLHS: false
    },

    {
      id: 4,
      name: 'User TOC',
      isSelected: false,
      isVisible: true,
      isActive: false,
      isTrainerLHS: true
    },


    {
      id: 5,
      name: 'Bookmarks',
      isSelected: false,
      isVisible: true,
      isActive: false,
      isTrainerLHS: false
    }

  ],

  removePara: function(paraId) {
    this.smodel.terms = _.reject(this.smodel.terms, function(obj) {
      if (obj.paragraphId == paraId)
        return true;
    });
  },

  removeTerm: function(paraId, term) {
    LHSModel.smodel.terms = _.reject(LHSModel.smodel.terms, function(obj) {
      if ((obj.paragraphId == paraId) && (term == obj.term))
        return true;
    });
  },

  getParagraphs: function(paraId) {
    var paras = _.filter(LHSModel.smodel.terms, function(obj) {
      if (obj.paragraphId == paraId)
        return true;
    });
    return paras;
  },

  getClassNames: function() {
    var items = [];
    for (var ii = 0; ii < this.classes.length; ii++) {
      items.push(this.classes[ii].name);
    }
    return items;
  },

  getClassFromId: function(classId) {
    return _.find(this.classes, function(obj) {
      if (obj.id == classId)
        return true;
    });
  },

  getClassFromIndex: function(index) {
    return this.classes[index].id;
  },


  getParaFromClassIdRange: function(startClassId, endClassId) {
    var terms = [];
    var prevTerm = null;
    for (var ii = 0; ii < LHSModel.smodel.terms.length; ii++) {
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
    var paras = _.filter(LHSModel.smodel.terms, function(obj) {
      if ((obj.paragraphId == paraId) && (obj.classificationId == classId))
        return true;
    });
    return paras;
  },

  filterInvalidClass: function(terms) {
    var paras = _.filter(terms, function(obj) {
      //loop over each class
      var filteredClass = _.where(LHSModel.classes, {
        'id': obj.classificationId
      })
      if (filteredClass.length > 0) {
        //valid
        return true;
      }
      return false;
    });
    return paras;
  },



  getTermsForClass: function(classId) {
    var paras = _.filter(LHSModel.smodel.terms, function(obj) {
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
    for (var ii = 0; ii < this.classes.length; ii++) {
      if (this.classes[ii].isVisible) {
        //get terms for classId
        var classId = this.classes[ii].id;
        var paras = _.filter(terms, function(obj) {
          if ((obj.classificationId == classId))
            return true;
        });

        if (paras.length > 0) {
          this.classes[ii].isActive = true;
          console.log("active is true" + this.classes[ii].id);
        } else {
          this.classes[ii].isActive = false;
          console.log("active is false" + this.classes[ii].id);
        }

      } else {
        this.classes[ii].isActive = false;
      }
    }
  },

  setTerms: function(terms) {
    this.smodel.terms = this.filterInvalidClass(terms);
    this.setActiveClasses(terms);
  },

  setYOffsetForTerms: function(terms) {
    this.smodel.terms = _.map(terms, function(term) {
      term.offsetY = $("#" + term.paragraphId).scrollTop();
      return term;
    });
  }

};

angular.module('SkrollApp')
  .factory('LHSModel', function() {
    return LHSModel;
  });