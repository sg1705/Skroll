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

Changelog

5/13/2015
* Refactored processTextNode by splitting into two. processTextNode() and createChunk()
* Created method called getTableText . This method iterates over each row and cell, and
  joins the each text in the cell with a space.

5/13/2015
* Created a method called processFont
* Commented this method in two places processNode() and createChunk()

**/


var docObject = new CoreMap(1000, "");
var paragraphs = new Array();
var chunkStack = new Array();
var count = 0;
var paragraphId = 1234;
var chunkId = 5000;
var pageBreak = false;
var DEBUG = false;
var isAnchor = false;
var isFirstChunkOfPara = true;
var isBlockInTable = false;
var isHref = false;

var processingFlags = {
    table: false,
    fonts: true,
    pageBreak: false
}

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
var IS_CENTER_ALIGNED_ANNOTATION = "IsCenterAlignedAnnotation";
var FONTSIZE_ANNOTATION = "FontSizeAnnotation";
var IS_ANCHOR_ANNOTATION = "IsAnchorAnnotation";
var IS_TABLE_ANNOTATION = "IsInTableAnnotation";
var IS_HREF_ANNOTATION = "IsHrefAnnotation";

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

    //ignore these tags
    if ($(element).is("meta") || $(element).is("link") || $(element).is("comment") ||
        $(element).is("style") || $(element).is("iframe")) {
        return;
    }

    //is element an anchor
    if (isAnchorElement(element)) {
        isAnchor = true;
    }

    // does the element have page break
    if (processingFlags.pageBreak) {
        if (isPageBreak(element)) {
            processPageBreak(index, element);
        }
    }

    if ($(element).is('img')) {
        var srcurl = $(element).attr('src');
        if (srcurl.indexOf('/') == -1 ) {
            //append the sourceurl
            if (sourceUrl != null) {
                var newUrl = sourceUrl + '/' + srcurl;
                $(element).attr('src', newUrl);
            }
        }
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

    if (processingFlags.fonts) {
        processFont(element);
    }


    //check to see if the new element is in table
    if (isElementInTable(element)) {
        if (processingFlags.table) {
            processTableAnnotation(element);
            //create new para
        }
        processTableText(element);
        return;
    }


    $(element).contents().each(function(index, element) {
        processNode(index, element);
    });
}

function createPara(element) {
    var newParagraph = new Object();
    newParagraph[ID_ANNOTATION] = 'p_'+paragraphId;
    newParagraph[TEXT_ANNOTATION] = "";
    if (isBlockInTable) {
      newParagraph[IS_TABLE_ANNOTATION] = true;
    }

    setParaAnnotations(newParagraph);
    paragraphs.push(newParagraph);
    insertMarker(paragraphId, element);
    paragraphId++;
    resetPara();

    if (isElementInTable(element)) {
        isBlockInTable = true;
    } else {
        isBlockInTable = false;
    }

}

function createLastPara() {
    createPara();
}


function setParaAnnotations(newParagraph) {
    newParagraph[PARAGRAPH_FRAGMENT] = chunkStack;
    //insert page break annotation
    if (pageBreak) {
      newParagraph[IS_PAGE_BREAK_ANNOTATION] = true;
    }
    if (isAnchor) {
      newParagraph[IS_ANCHOR_ANNOTATION] = true;
    }

    if (isHref) {
        newParagraph[IS_HREF_ANNOTATION] = true;
    }

}

function processFont(element) {
    if ($(element).attr('style') != null) {
        if (element.style.fontFamily != null) {
            $(element).css("font-family","freight-text-pro, Georgia, Cambria, 'Times New Roman', Times, serif");
            //$(element).css("font-family","");
            $(element).css("line-height","25px");
            $(element).css("font-size", "18px");
            //$(element).css("font-family","'Roboto', sans-serif");
            //$(element).css("font-family","TiemposTextWeb-Regular, Georgia, serif");
            //font-family: freight-text-pro, Georgia, Cambria, 'Times New Roman', Times, serif;
            //line-height:33px;
            //font-size: 26px
        }
    }
}

function resetPara() {
    chunkStack = new Array();
    pageBreak = false;
    isAnchor = false;
    isFirstChunkOfPara = true;
    isHref = false;
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
    var chunkText = $(element).text();
    createChunk(chunkText, element);
}

function processTableText(tableElement) {
    var textNodesInTable = getTextNodesFromTable(tableElement);
    // if nodes are less than 10
    if (textNodesInTable.length <= 10) {
        $.each(textNodesInTable, function(index, node) {
              processTextNode(index, node);
        });
    } else {
        var chunkText = getTextFromTable(tableElement);
        createChunk(chunkText, tableElement);
    }

}

function createChunk(chunkText, element) {
    var newChunk = new Object();
    //check to see if need to create a chunk
    var isChunkRequired = false;
    if (isFirstChunkOfPara) {
        isChunkRequired = true;
        isFirstChunkOfPara = false;
    }
    if (isBold(element.parentNode)) {
        newChunk[IS_BOLD_ANNOTATION] = true;
        isChunkRequired = true;
        isFirstChunkOfPara = true;
    }
    if (isItalic(element.parentNode)) {
        newChunk[IS_ITALIC_ANNOTATION] = true;
        isChunkRequired = true;
        isFirstChunkOfPara = true;
    }
    if (isUnderLine(element.parentNode)) {
        newChunk[IS_UNDERLINE_ANNOTATION] = true;
        isChunkRequired = true;
        isFirstChunkOfPara = true;
    }
    if (isCenterAligned(element.parentNode)) {
        newChunk[IS_CENTER_ALIGNED_ANNOTATION] = true;
        isChunkRequired = true;
        isFirstChunkOfPara = true;
    }
    //processFont(element);
    if (isChunkRequired) {
        //create a chunk and add it to stack
        newChunk[ID_ANNOTATION] = chunkId;
        newChunk[TEXT_ANNOTATION] = chunkText;
        newChunk[FONTSIZE_ANNOTATION] = $(element.parentNode).css("font-size");

        chunkStack.push(newChunk);
        chunkId++;
    } else {
        //append to old chunk
        var oldChunk = chunkStack[chunkStack.length - 1];
        oldChunk[TEXT_ANNOTATION] = oldChunk[TEXT_ANNOTATION] + chunkText;
        chunkStack[chunkStack.length - 1] = oldChunk;
    }

    if (DEBUG) {
        if (element.jquery)
            printNodes(index, element, $(element).css("display"));
    }
}

function insertMarker(paragraphId, element) {
    //original marker
    //$(element).prepend("<a id=\""+(paragraphId+1)+"\" name=\"" + (paragraphId+1) + "\"/>");
    //tried with web components.. need to figure out background
    //$(element ).wrap( "<skroll-id id=\""+(paragraphId+1)+"\"></skroll-id>");

    //$(element ).wrap( "<div id=\"p_"+(paragraphId+1)+"\"></div>");
    //$(element ).wrap( "<a name=\"p_"+(paragraphId+1)+"\"></a>");
    //fastest
    //$(element ).attr('id','p_' + (paragraphId +1) );
    //$('"p_' + (paragraphId +1) + '"').insertBefore($(element));
    var displayStyle = '';
    if (paragraphId > 1300) {
        displayStyle = 'style="display:none"';
    }
    $('<!--sk <div id="p_' + (paragraphId +1) + '" ' + displayStyle +' > sk-->').insertBefore($(element));

//    $('<!--sk <div id="p_' + (paragraphId +1) + '"> sk-->').insertBefore($(element));
    $('<!--sk </div> sk-->').insertAfter($(element));
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

    if (element.nodeType != 1)
        return false;

    var displayStyle = $(element).css("display")
    if ((displayStyle == "block") || (displayStyle == "table")) {
        return true;
    }
    return false;
}

function isElementInTable(element) {
    if ($(element).is('table'))
        return true;
    if ($(element).parents('table').length > 0) {
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

function isAnchorElement(element) {
    if ($(element).is("a")) {
        //get name
        var anchorName = $(element).attr('name');
        var anchorId = $(element).attr('id');
        //hack - if anchorId == anchorName, this is probably ours
        if (anchorName != null) {
            if (anchorName != '') {
                if (!(anchorName == anchorId))
                    return true;
            }
        }
        //check for href
        var href = $(element).attr('href');
        if (href != null) {
            //check to see if an external link or anchor
            isHref = true;
            var lastIndexOfHash = href.lastIndexOf("#");
            var lastIndexOfSlash = href.lastIndexOf("/");
            if (lastIndexOfHash > lastIndexOfSlash) {
                //has anchor, so replace href
                $(element).attr('href', href.substr(lastIndexOfHash));
            }
        }

    }
    return false;
}



function isCenterAligned(element) {
    var textCenter = $(element).css("text-align");
    if (textCenter == null) {
        return false;
    }
    if (textCenter.indexOf("center") > -1) {
        return true;
    } else {
        return false;
    }

}


function isPageBreak(element) {

    if (element.nodeType != 1)
        return false;

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
