/**
1. Why am I moving to Javascript based parsing.

* PhantomJs is integrated with WebKit
* It allows me to use jQuery
* It can decipher computed styles (though not the best)
* Easy enough to do tricks to HTML that will be useful in the viewer.

2. How will this parser be architected.

From within Java (or maybe JS); there will be a mechansim to call PhantonJS.
PhantomJs will return a JSON which will be de-serialzed into a Java Object (or JS)

3. What is the serialization format.

Rules first
- Each document is broken into chunks that has display:block
- Each chunk is a potential paragraph
- Each chunk is further broken into sub chunks.
- Sub chunks are part of a paragraph that have similar formatting characterstics.
    These are eventually text nodes in a given element. Any element can have more
    one text node.
- A sub chunk can have any number of formatting characters which will be converted
  into annotation when the document is de-serialized in Java


{
    chunkId: 84dfd345343,
    annotations: [{ .... }] ,
    text: "some text",
    chunks:
    [{
        chunkId: k34854nd3ww,
        annotations: [{"Bold", "FirstBold", "FontHeight 8"}],
        text: "some text",
        chunks: [
        {


        }
        ]
    }]
}


4. What is the jQuery algorithm to parse HTML

   Similar to what we did with Jsoup early on. Here are the rules.

   a. Start with Root
   b. Traverse each child
   c. If hit a text node, then collect text, collect annotations
       create chunk object and add it to a stack
   d. Keep traversing until we hit a block element
      We move the stack into a bigger chunk (called paragraph)
      Add it to a list of paragraphs
   d. Continue traversing


**/


var docObject = new CoreMap(1000, "");
var paragraphs = new Array();
var chunkStack = new Array();
var count = 0;
var paragraphId = 1234;
var chunkId = 5000;
var pageBreak = false;
var DEBUG = false;


// core annotations
var ID_ANNOTATION = "IdAnnotation";
var TEXT_ANNOTATION = "TextAnnotation";
var PARAGRAPH_ANNOTATION = "ParagraphsAnnotation";
var HTMLTEXT_ANNOTATION = "HTMLTextAnnotation";
var IS_BOLD_ANNOTATION = "IsBoldAnnotation";
var IS_ITALIC_ANNOTATION = "IsItalicAnnotation";
var IS_UNDERLINE_ANNOTATION = "IsUnderlineAnnotation";
var PARAGRAPH_FRAGMENT = "ParagraphFragmentAnnotation"
var IS_PAGE_BREAK_ANNOTATION = "IsPageBreakAnnotation";

 function CoreMap(chunkId, text) {

    this.CoreMap = new Object();

    this.set = function(annotation, object) {
        this.CoreMap[annotation] = object;
    }

    this.get = function(annotation) {
        return this.CoreMap[annotation];
    }


    this.set(ID_ANNOTATION, chunkId);
    this.set(TEXT_ANNOTATION, text);

}


function processNode(index, element) {

    //ignore nodeName is "script"
    if ($(element).is("script")) {
        processScriptTag(index, element);
        return;
    }

    // does the element have page break
    if (isPageBreak(element)) {
        processPageBreak(index, element);
    }

    //check if a #text node
    if (element.nodeType == 3) {
        processTextNode(index, element);
    }

    // check to see if the node is a block type
    if (isNodeBlock(element)) {
        // create a paragraph
        createPara(element);
    }

    $(element).contents().each(function(index, element) {
        processNode(index, element);
    });
}

function createPara(element) {
    var newParagraph = new Object();
    newParagraph[ID_ANNOTATION] = paragraphId;
    newParagraph[TEXT_ANNOTATION] = "";
    newParagraph[PARAGRAPH_FRAGMENT] = chunkStack;
    //insert page break annotation
    if (pageBreak) {
      newParagraph[IS_PAGE_BREAK_ANNOTATION] = true;
    }
    paragraphs.push(newParagraph);
    insertMarker(paragraphId, element);
    chunkStack = new Array();
    paragraphId++;
    pageBreak = false;
}


/**
*  Processes an element if it is a row
**/
function processTableRow(index, element) {
    //process each child
    console.log("Table row:"+$(element).children().length);
}


/**
*  Processes script tag
**/
function processScriptTag(index, element) {
    // remove the script element
    $(element).remove();
}


/**
*  Processes a page break
**/
function processPageBreak(index, element) {
    pageBreak = true;
    if (DEBUG) {
     console.log("--- page -- break:" + paragraphId);
    }
}


/**
  Process any given node as a text node and chunks it based on formatting
**/
function processTextNode(index, element) {
    //create a chunk and add it to stack
    var chunkText = $(element).text();
    var newChunk = new Object();
    newChunk[ID_ANNOTATION] = chunkId;
    newChunk[TEXT_ANNOTATION] = chunkText;
    if (isBold(element.parentNode)) {
        newChunk[IS_BOLD_ANNOTATION] = true;
    }
    if (isItalic(element.parentNode)) {
        newChunk[IS_ITALIC_ANNOTATION] = true;
    }
    if (isUnderLine(element.parentNode)) {
        newChunk[IS_UNDERLINE_ANNOTATION] = true;
    }
    chunkStack.push(newChunk);
    chunkId++;

    if (DEBUG) {
        printNodes(index, element, $(element).css("display"));
    }
}


function insertMarker(paragraphId, element) {
    $(element).prepend("<a id=\""+(paragraphId+1)+"\" name=\"" + (paragraphId+1) + "\"/>");
}

function printNodes(index, element, block) {

    if (element.nodeType == 3) {
        count++;
        if (isUnderLine(element.parentNode)) {
            console.log(count+":"+element.parentNode.nodeName + ":underline");
        }

        if (isBold(element.parentNode)) {
            console.log(count+":"+element.parentNode.nodeName + ":bold");
        }

        if (isItalic(element.parentNode)) {
            console.log(count+":"+element.parentNode.nodeName + ":italic");
        }
    }
}

function isNodeBlock(element) {
    var displayStyle = $(element).css("display")
    if ((displayStyle == "block") || (displayStyle == "table")) {
        return true;
    }
    return false;
}

function isUnderLine(element) {
    if ($(element).css("text-decoration") == "underline")
        return true;

    return false;
}

function isBold(element) {
    if ($(element).css("font-weight") == "bold")
        return true;

    return false;
}

function isItalic(element) {
    if ($(element).css("font-style") == "italic")
        return true;

    return false;
}

function isPageBreak(element) {
    if ($(element).css("page-break-after") == "always")
        return true;

    if ($(element).css("page-break-before") == "always")
            return true;

    return false;
}


// table methods
function treeDepth(element) {
    var $children = $( element ).children();
    var depth = 0;

    while ( $children.length > 0 ) {
        $children = $children.children();
        depth += 1;
    }

    return depth;
};

function isTableInTable(table) {
    var elements = $(table).find("table").length;
    if (elements > 0)
      return true;
    return false;
}
