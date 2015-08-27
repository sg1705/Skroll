var page = require('webpage').create();
var system = require('system');
var fs = require('fs');

var args = system.args;

var testFlags = args[1];
var fetchHtml = args[2];
var fileName = args[3];
var globalSourceUrl = [4];

/* process url argument */
var globalSourceUrlFile = (new Date()).getTime();
if (globalSourceUrl == null) {
    globalSourceUrl = '';
}

if ((testFlags == null) || (testFlags == 'false')) {
    testFlags = false;
} else if (testFlags == 'true') {
    testFlags = true;
}
fs.write('/tmp/' + globalSourceUrlFile + '.js', "var sourceUrl = '" + globalSourceUrl + "'; var testFlags = " + testFlags + ";", "w");
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

    //measure
    var startTime = Date.now();

    if (testFlags) {
        processingFlags.table = true;
        processingFlags.fonts = true;
        processingFlags.pageBreak = true;
    }

    $(":root").css('display', 'none');
    $(":root").contents().each(function(index, element) {
        processNode(index, element);
    });
    $(":root").css('display', '');

    // done
    //move chunks to paragraphs
    createLastPara();
    docObject.set(PARAGRAPH_ANNOTATION, paragraphs);
    docObject.set(TABLES_ANNOTATION, tables);
    var totalTime = Date.now() - startTime;
    return ( ";---------------SKROLLJSON---------------------;"
             + JSON.stringify(docObject, null, 2)
             + ";---------------SKROLL---------------------;"
             + $(":root").html()
             + ";---------------SKROLLTIME---------------------;"
             + totalTime);
});
console.log(parsedJson);
//write the file
fs.write('/tmp/parsedJson.json', parsedJson);
phantom.exit(1);
