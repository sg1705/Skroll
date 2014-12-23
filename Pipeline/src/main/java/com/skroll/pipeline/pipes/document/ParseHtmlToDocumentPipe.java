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



    public List<String> removeBlankRows(List<String> list) {
        List<String> newList = new ArrayList<String>();
        for(int ii = 0; ii < list.size(); ii++) {
            String str = list.get(ii);
            if (!StringUtil.isBlank(str.replace("\u00a0", ""))) {
                newList.add(str);
            }
        }
        return newList;

    }


    public void printPara(List<String> para) {
        for (int ii = 0; ii < para.size(); ii++) {
            System.out.println(para.get(ii));
            System.out.println("------------");
        }
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
        Paragraph paragraph = new Paragraph(""+lastParaId, this.rollingTest);
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
                this.createPara();
                lastParaId = paraId;
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
            text = ((Element) node).text();
        }
        return text;
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

        if (element.tag().getName().toLowerCase().equals("body")) {
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
