var page = require('webpage').create();
var system = require('system');
var fs = require('fs');

var args = system.args;

var fileName = args[1];
//htmlText = htmlText.substring(1, htmlText.length-1);
var htmlText = fs.read(fileName);

page.settings.resourceTimeout = 200;
page.settings.loadImages = false;

page.onConsoleMessage = function (msg) {
    console.log(msg);
};

//page.settings.userAgent = 'SpecialAgent';
page.content = htmlText;
page.injectJs('./jquery.min.js', function() {
    console.log('included...');
});
page.injectJs('./jQueryParser.js', function() {
    console.log('parser included...');
});



var parsedJson = page.evaluate(function() {

    $(":root").contents().each(function(index, element) {
        processNode(index, element);
    });


    // done
    //move chunks to paragraphs
    createLastPara();
    docObject.set(PARAGRAPH_ANNOTATION, paragraphs);

    return ( ";---------------SKROLLJSON---------------------;"
             + JSON.stringify(docObject, null, 2)
             + ";---------------SKROLL---------------------;"
             + $(":root").html() );
});
console.log(parsedJson);
//write the file
fs.write('/tmp/parsedJson.json', parsedJson);
phantom.exit(1);
