(function() {
  'use strict';

  angular
    .module('app.core', []);


	angular
		.module('app.core')
		.run(function(featureFlags, $http) {
			var flags = [ 
				{
					"key" : "trainer",
					"active" : true,
					"name" : "flag for trainer",
					"description" : "no description"
				},
				{
					"key" : "trainer.probability",
					"active" : false,
					"name" : "flag for probabilities in TOC",
					"description" : "no description"
				},
				{
					"key" : "googl.shortlink",
					"active" : false,
					"name" : "flag to create shortlinsk",
					"description" : "no description"
				},
				{
					"key" : "server.rendering",
					"active" : true,
					"name" : "flag to turn on server rendering",
					"description" : "no description"
				}

			];
	  		featureFlags.set(flags);
			}
		);



    
})();