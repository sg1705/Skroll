(function() {

  angular
    .module('app.toc')
    .filter('tocTerms', function() {
      return function(terms) {
        var newTerms = [];
        var prevTerm = null;
        for (var ii = 0; ii < terms.length; ii++) {
          var term = terms[ii];
          if ((term.classificationId >= 2) && (term.classificationId <= 3)) {
            //if (term.term != prevTerm) {
            newTerms.push(term);
            //}
          }
          prevTerm = term.term;
        }
        return newTerms;
      };
    });

})();