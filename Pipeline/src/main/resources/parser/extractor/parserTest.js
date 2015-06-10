var DEBUG = true;
var sourceUrl = "www.google.com";
var startTime = Date.now();
$(":root").contents().each(function(index, element) {
    processNode(index, element);
});
createLastPara();
docObject.set(PARAGRAPH_ANNOTATION, paragraphs);
docObject.set(TABLES_ANNOTATION, tables);
var totalTime = Date.now() - startTime;
console.log(JSON.stringify(docObject, null, 2));
console.log("Time taken to parse:" + totalTime+ 'ms');
