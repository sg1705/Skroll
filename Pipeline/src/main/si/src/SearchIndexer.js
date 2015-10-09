#!/usr/bin/env node

var lunr = require('lunr');
var fs = require('fs');
var file = __dirname + '/data/c642e2cd97a219a8ef7cc1053dca1e74';
var EventEmitter = require("events").EventEmitter;

var eventEmitter = new EventEmitter();
var argv = require('optimist').argv;

if(!argv.inputFile) {
  console.error('No input file specified to Lunr Indexer. Use flag --inputFile <filepath>');
  process.exit(9);
}

var idx = lunr(function() {
    this.ref('id');
    this.field('text', {
        boost: 10
    });
})

// indexReady is the callback that is called as
// soon as lunr is done indexing.
// This is done by emitting 'indexReady' event.
var indexReady = function(idx) {
    // leaving this commented code intact for now
    // to test actual searching until UI is ready.
    // var searchResult = idx.search("nicholas");
    // console.log(searchResult);

    var serializedIndex = JSON.stringify(idx.toJSON());

    // Spew out the index to stdout formally instead of console.log
    //console.log(serializedIndex);
    process.stdout.write(serializedIndex);
};
eventEmitter.on("indexReady", indexReady);

fs.readFile(argv.inputFile, 'utf8', function(err, data) {
    if (err) {
        console.log('Error: ' + err);
        return;
    }

    data = JSON.parse(data).map.ParagraphsAnnotation;
    data.forEach(function(paragraphAnnotation) {
        paragraphAnnotation = paragraphAnnotation.map;
        textAnnotation = paragraphAnnotation.TextAnnotation;

        //console.log(paragraphAnnotation);
        var doc = {
            id: paragraphAnnotation.IdAnnotation,
            text: textAnnotation
        };
        //console.log(doc);
        idx.add(doc);
    });

    // Done reading file, emit 'indexReady' event.
    eventEmitter.emit('indexReady', idx);
});