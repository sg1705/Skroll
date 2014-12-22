package com.skroll.pipeline.pipes;

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
 * Created by sagupta on 12/14/14.
 */
public class AssignParagraphIdsToHTMLDocumentPipe extends SyncPipe<List<String>, List<String>> {

    private String rollingTest;
    private List<String> paragraphChunks;
    private int paraId;


    @Override
    public List<String> process(List<String> input) {
        // extract the first string
        String htmlText = input.get(0);
        List<String> para = split(htmlText);
        // do something
        return this.target.process(para);
    }



    public AssignParagraphIdsToHTMLDocumentPipe() {
        this.rollingTest = "";
        paragraphChunks = new ArrayList<String>();
        this.paraId = 1234;
    }


    public List<String> split(String html) {
        Document doc = Jsoup.parse(html);
        processNodes(doc);
        System.out.println(doc.outerHtml());
        //TODO find out the Charset
        return this.paragraphChunks;
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
        // move rolling html to html

        this.rollingTest = "";

    }

    private void createPara(Node node) {
        if (node instanceof Element) {
            Element element = (Element)node;
            element.prepend("<a name=\""+this.paraId+"\"/>");
            paraId++;
            this.createPara();
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



}
