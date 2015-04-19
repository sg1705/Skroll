var DEBUG = true;
$(":root").contents().each(function(index, element) {
    processNode(index, element);
});
createLastPara();
docObject.set(PARAGRAPH_ANNOTATION, paragraphs);

console.log(JSON.stringify(docObject, null, 2));
