var DEBUG = true;
$(":root").contents().each(function(index, element) {
    processNode(index, element);
});
createLastPara();
docObject.set(PARAGRAPH_ANNOTATION, paragraphs);
docObject.set(TABLES_ANNOTATION, tables);

console.log(JSON.stringify(docObject, null, 2));
