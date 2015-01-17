package com.skroll.document.model;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import java.io.File;
import java.util.List;

/**
 * Created by saurabh on 1/4/15.
 */
public class JSoupNodeVisitor implements NodeVisitor {

    //StringBuffer htmlText = new StringBuffer();
    private String rollingTest;
    private String htmlText;
    private List<String> paragraphChunks;
    private List<CoreMap> paragraphs;
    private int paraId;
    private int lastParaId = 0;


    @Override
    public void head(Node node, int depth) {

        // if node is block
        // do child have blocks
        if (isNodeBlock(node)) {
            createPara(node);
        } else {

            if (!isNodeProhibhited((Element)node)) {
                this.rollingTest = this.rollingTest + getTextFromNode(node);
                this.htmlText = this.htmlText + getHtmlFromNode(node);
            }
        }



        //htmlText.append(node.outerHtml());
        System.out.println(node.nodeName()+":"+depth);
        //System.out.println(node.outerHtml());
    }

    @Override
    public void tail(Node node, int depth) {

    }

    public static void main(String[] args) throws Exception {
        String text = Utils.readStringFromFile(new File("Pipeline/src/test/resources/html-docs/random-indenture.html"));
        Document doc = Jsoup.parse(text);
        JSoupNodeVisitor nodeVisitor = new JSoupNodeVisitor();
        doc.traverse(nodeVisitor);
        Utils.writeToFile("/tmp/test.html", nodeVisitor.htmlText.toString());
//        HtmlToParagraphAnnotator annt = new HtmlToParagraphAnnotator();
//        Annotation annotation = new Annotation(text);
//        annt.annotate(annotation);
//        List<CoreMap> paragraphs = annotation.get(CoreAnnotations.ParagraphsAnnotation.class);
//        for(CoreMap para: paragraphs) {
//            System.out.println(para.get(CoreAnnotations.HTMLTextAnnotation.class));
//        }
//        System.out.println(annotation);
    }


    private void createPara() {
        this.paragraphChunks.add(this.rollingTest);
        CoreMap para = new CoreMap();
        para.set(CoreAnnotations.ParagraphIdAnnotation.class, new Integer(this.lastParaId).toString());
        para.set(CoreAnnotations.TextAnnotation.class, this.rollingTest);
        para.set(CoreAnnotations.HTMLTextAnnotation.class, this.htmlText);
        //Entity paragraph = new Entity(new Integer(this.lastParaId).toString(), this.rollingTest);
        //TODO remove this line
        //Paragraph paragraph = new Paragraph(new Integer(this.lastParaId).toString(), this.rollingTest);
        //this.paragraphs.add(paragraph);
        this.paragraphs.add(para);
        // move rolling html to html
        this.rollingTest = "";
        this.htmlText = "";

    }

    private void createPara(Node node) {
        if (node instanceof Element) {
            Element element = (Element) node;

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
            if (((Element) node).tag().getName().equals("pre")) {
                String preText = ((Element) node).text();
            }
            text = ((Element) node).text();
        }
        return text;
    }


    /**
     * Extracts html from node.
     *
     * @param node
     * @return
     */
    private String getHtmlFromNode(Node node) {
        String text = "";
        if (node instanceof TextNode) {
            text = ((TextNode) node).outerHtml();
        } else if (node instanceof Element) {
            // check for pre
            if (((Element) node).tag().getName().equals("pre")) {
                String preText = ((Element) node).html();
            }
            text = node.outerHtml();
        }
        return text;
    }


    //TODO
    // example doc - IBM Indenture from 1995
    private void processPre(Element element) {
        if (element.tag().getName().equals("pre")) {
            String preText = element.text();
        }
    }


    private boolean isNodeProhibhited(Element element) {
        if (element.tag().getName().toLowerCase().equals("document")) {
            return true;
        }

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

    private boolean isNodeBlock(Node node) {
        boolean isBlock = false;
        if (node instanceof Element) {
            isBlock = ((Element) node).isBlock();
        }
        return isBlock;
    }

}
