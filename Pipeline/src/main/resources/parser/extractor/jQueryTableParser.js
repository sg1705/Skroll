/**
TABLES_ANNOTATION: [ // List<CoreMap>
    {
       ROWS_ANNOTATION: [ // List<CoreMap>
          {
             ParagraphsAnnotation: [ //List<CoreMap>
                {

                },
                {

                }
             ]
          },
          {

          }

       ]
    },

    {


    }

]




**/


var TABLES_ANNOTATION = "TablesAnnotation";
var ROWS_ANNOTATION = "RowsAnnotation";
var COLUMN_ANNOTATION = "ColsAnnotation";
var PARAGRAPH_ID_ANNOTATION = "ParagraphIdAnnotation";
var tableId = 5000;
var tables = [ ];
//this function receives a table element
function processTableAnnotation(tableElement) {
    //process all rows
    var rows = [];
    $(tableElement).find('tr').each(function(rowIndex,r) {
        var columnAnnotation = new Object();
        var cells = [];
        $(this).find('th, td').each(function(cellIndex, c) {
            var cell = new Object();
            var textNodes = getTextNodesIn(c, false);
            for(var ii = 0; ii < textNodes.length; ii++) {
                processTextNode(ii,textNodes[ii]);
            }
            setParaAnnotations(cell);
            cells.push(cell);
            columnAnnotation[COLUMN_ANNOTATION] = cells;
            resetPara();
        });
        rows.push(columnAnnotation);
    });
    var newTable = new Object();
    newTable[ID_ANNOTATION] = "t_" + tableId;
    newTable[PARAGRAPH_ID_ANNOTATION] = 'p_'+paragraphId;
    newTable[ROWS_ANNOTATION] = rows;
    tableId++;
    tables.push(newTable);
}

//function getAllTextNodesIn(element) {
//    return $(element).find('*').contents().filter(function () { return this.nodeType === 3; });
//}

function getTextNodesIn(node, includeWhitespaceNodes) {
    var textNodes = [], nonWhitespaceMatcher = /\S/;

    function getTextNodes(node) {
        if (node.nodeType == 3) {
            if (includeWhitespaceNodes || nonWhitespaceMatcher.test(node.nodeValue)) {
                textNodes.push(node);
            }
        } else {
            for (var i = 0, len = node.childNodes.length; i < len; ++i) {
                getTextNodes(node.childNodes[i]);
            }
        }
    }

    getTextNodes(node);
    return textNodes;
}