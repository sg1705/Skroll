var page = require('webpage').create();
var system = require('system');
var fs = require('fs');

var args = system.args;

var testFlags = args[1];
var fetchHtml = args[2];
var fileName = args[3];
var globalSourceUrl = args[4];
var fetchUrl = args[5];
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


var parsedJson = '';

if (fetchHtml == 'true') {

    page.open(fetchUrl, function(status) {
        preparePage(page);
        injectJs(page);
        parsedJson = page.evaluate(evaluateHtml);
        end();
    });

} else  {
    var htmlText = fs.read(fileName);
    preparePage(page);
    page.content = htmlText;
    injectJs(page);
    parsedJson = page.evaluate(evaluateHtml);
    end();
}


function end() {
    console.log(parsedJson);
    //write the file
    //fs.write('/tmp/parsedJson.json', parsedJson);
    phantom.exit(1);

}


function preparePage(page) {
    //page.settings.userAgent = 'SpecialAgent';
    page.settings.resourceTimeout = 200;
    page.settings.loadImages = false;

    page.onConsoleMessage = function (msg) {
        console.log(msg);
    };
}

function injectJs(page) {
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
}

function evaluateHtml(globalSourceUrl) {
    //measure
    var startTime = Date.now();
    var sourceHtml = $(":root").html();
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
    var delimiter = ';-skroll.io-;';
    return ( delimiter
             + JSON.stringify(docObject, null, 2)
             + delimiter
             + $(":root").html()
             + delimiter
             + totalTime
             + delimiter
             + sourceHtml);


}