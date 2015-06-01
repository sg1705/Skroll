var page = require('webpage').create();
var system = require('system');
var fs = require('fs');

var args = system.args;

var fileName = args[1];

/* process url argument */
var globalSourceUrl = args[2];
var globalSourceUrlFile = (new Date()).getTime();
if (globalSourceUrl == null) {
    globalSourceUrl = '';
}
fs.write('/tmp/' + globalSourceUrlFile + '.js', "var sourceUrl = '" + globalSourceUrl + "'", "w");
/* -- end process url argument */

var htmlText = fs.read(fileName);
page.settings.resourceTimeout = 200;
page.settings.loadImages = false;

page.onConsoleMessage = function (msg) {
    console.log(msg);
};

//page.settings.userAgent = 'SpecialAgent';
page.content = htmlText;
page.injectJs('/tmp/' + globalSourceUrlFile + '.js', function() {
    console.log('included...');
});

page.injectJs('./jquery.min.js', function() {
    console.log('included...');
});
page.injectJs('./jQueryParser.js', function() {
    console.log('parser included...');
});
page.injectJs('./jQueryTableParser.js', function() {
    console.log('parser included...');
});


var parsedJson = page.evaluate(function(globalSourceUrl) {

    $(":root").contents().each(function(index, element) {
        processNode(index, element);
    });

    // done
    //move chunks to paragraphs
    createLastPara();
    docObject.set(PARAGRAPH_ANNOTATION, paragraphs);
    docObject.set(TABLES_ANNOTATION, tables);
    return ( ";---------------SKROLLJSON---------------------;"
             + JSON.stringify(docObject, null, 2)
             + ";---------------SKROLL---------------------;"
             + $(":root").html() );
});
console.log(parsedJson);
//write the file
fs.write('/tmp/parsedJson.json', parsedJson);
phantom.exit(1);
