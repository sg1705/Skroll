package com.skroll.pipeline.pipes.document;

import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.SyncPipe;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Takes a string and converts into a doc
 *
 *  //TODO Fix bugs when the document has </pre> tag
 *
 * Created by sagupta on 12/14/14.
 */
public class ParseHtmlToDocumentPipe extends SyncPipe<HtmlDocument, HtmlDocument> {

    private String rollingTest;
    private List<String> paragraphChunks;
    private List<Paragraph> paragraphs;
    private int paraId;
    private int lastParaId = 0;


    public ParseHtmlToDocumentPipe() {
        this.rollingTest = "";
        paragraphChunks = new ArrayList<String>();
        paragraphs = new ArrayList<Paragraph>();
        this.paraId = 1234;
    }

    @Override
    public HtmlDocument process(HtmlDocument htmlDoc) {
        Document doc = Jsoup.parse(htmlDoc.getSourceHtml());
        processNodes(doc);
        htmlDoc.setAnnotatedHtml(doc.outerHtml());
        htmlDoc.setParagraphs(this.paragraphs);
        //TODO find out the Charset
        return this.target.process(htmlDoc);
    }


    private void processNodes(Document doc) {
        Iterator<Node> childNodes = doc.childNodes().iterator();
        while (childNodes.hasNext()) {
            Node childNode = childNodes.next();
            processNode(childNode);
        }
        createPara();
    }

    /**
     * Process each node
     *
     * @param node
     */

    private void processNode(Node node) {
        if (isNodeBlock(node)) {
            createPara(node);
        }

        Iterator<Node> childNodes = node.childNodes().iterator();
        if (childNodes.hasNext()) {
            // process each node
            while (childNodes.hasNext()) {
                Node childNode = childNodes.next();
                processNode(childNode);
            }
        } else {
            this.rollingTest = this.rollingTest + getTextFromNode(node);
        }
    }


    /**
     * Converts rollingText into a para
     *
     * Takes the html of all the nodes in the rollingNodes; wraps a span and adds to a String
     *
     */
    private void createPara() {
        this.paragraphChunks.add(this.rollingTest);
        Paragraph paragraph = new Paragraph(new Integer(this.lastParaId).toString(), this.rollingTest);
        this.paragraphs.add(paragraph);
        // move rolling html to html
        this.rollingTest = "";

    }

    private void createPara(Node node) {
        if (node instanceof Element) {
            Element element = (Element)node;

            if (!isNodeProhibhited(element)) {

                element.prepend("<a name=\"" + this.paraId + "\"/>");
                paraId++;
                lastParaId = paraId;
                this.createPara();


                //check for pre
            }
        }
    }

    /**
     * Extracts text from node.
     *
     * @param node
     * @return
     */
    private String getTextFromNode(Node node) {
        String text = "";
        if (node instanceof TextNode) {
            text = ((TextNode) node).text();
        } else if (node instanceof Element) {
            // check for pre
            if (((Element)node).tag().getName().equals("pre")) {
                String preText = ((Element) node).text();
            }
            text = ((Element) node).text();
        }
        return text;
    }

    //TODO
    // example doc - IBM Indenture from 1995
    private void processPre(Element element) {
        if (element.tag().getName().equals("pre")) {
            String preText = element.text();
            // split this into paragraphs and increment
        }

    }


    /**
     * Finds out if a given node is a block element.
     *
     * @param node
     * @return
     */
    private boolean isNodeBlock(Node node) {
        boolean isBlock = false;
        if (node instanceof Element) {
            isBlock = ((Element) node).isBlock();
        }
        return isBlock;
    }


    private boolean isNodeProhibhited(Element element) {
        if (element.tag().getName().toLowerCase().equals("html")) {
            return true;
        }

        if (element.tag().getName().toLowerCase().equals("head")) {
            return true;
        }

        if (element.tag().getName().toLowerCase().equals("script")) {
            return true;
        }

        if (element.tag().getName().toLowerCase().equals("meta")) {
            return true;
        }
        if (element.tag().getName().toLowerCase().equals("link")) {
            return true;
        }

        if (element.tag().getName().toLowerCase().equals("title")) {
            return true;
        }

        return false;
    }

}
