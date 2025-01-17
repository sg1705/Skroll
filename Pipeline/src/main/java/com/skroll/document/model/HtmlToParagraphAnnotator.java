package com.skroll.document.model;

import com.skroll.document.Annotator;
import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TypesafeMap;
import com.skroll.parser.extractor.file.html.ParseHtmlToDocumentPipe;
import com.skroll.pipeline.util.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.File;
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
public class HtmlToParagraphAnnotator implements Annotator {

    private String rollingTest;
    private String htmlText;
    private List<String> paragraphChunks;
    private List<CoreMap> paragraphs;
    private int paraId;
    private int lastParaId = 0;


    public HtmlToParagraphAnnotator() {
        this.rollingTest = "";
        this.htmlText = "";
        paragraphChunks = new ArrayList<String>();
        paragraphs = new ArrayList<CoreMap>();
        this.paraId = 1234;
    }

    public void annotate(Annotation annotation) {
        String text = annotation.get(CoreAnnotations.TextAnnotation.class);
        org.jsoup.nodes.Document doc = Jsoup.parse(text);
        processNodes(doc);
        //document.setTarget(doc.outerHtml());
        //htmlDoc.setAnnotatedHtml(doc.outerHtml());
        annotation.set(CoreAnnotations.ParagraphsAnnotation.class, this.paragraphs);
        //document.setParagraphs(this.paragraphs);
        //TODO find out the Charset
        //return this.target.process(document);

    }


    private void processNodes(org.jsoup.nodes.Document doc) {
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
            this.htmlText = this.htmlText + getHtmlFromNode(node);
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
        CoreMap para = new CoreMap();
        para.set(CoreAnnotations.ParagraphIdAnnotation.class,new Integer(this.lastParaId).toString() );
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


    /**
     * Extracts html from node.
     *
     * @param node
     * @return
     */
    private String getHtmlFromNode(Node node) {
        String text = "";
        String parentHtml = "";
        if (node instanceof TextNode) {

            if (isTextNodeMarkedUp(node.parent())) {
                text = node.parent().outerHtml();
            } else {
                text = ((TextNode) node).outerHtml();
            }
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

    private boolean isTextNodeMarkedUp(Node node) {
        if (node.nodeName().equals("b"))
            return true;


        if (node.nodeName().equals("i"))
            return true;

        if (node.nodeName().equals("u"))
            return true;

        return false;
    }




    public static void main(String[] args) throws Exception {
        String text = Utils.readStringFromFile(new File("Pipeline/src/test/resources/html-docs/random-indenture.html"));
        HtmlToParagraphAnnotator annt = new HtmlToParagraphAnnotator();
        Annotation annotation = new Annotation(text);
        annt.annotate(annotation);
        List<CoreMap> paragraphs = annotation.get(CoreAnnotations.ParagraphsAnnotation.class);
        for(CoreMap para: paragraphs) {
            System.out.println(para.get(CoreAnnotations.HTMLTextAnnotation.class));
        }
        System.out.println(annotation);

        ParseHtmlToDocumentPipe pipe = new ParseHtmlToDocumentPipe();
    }

    @Override
    public List<Class<? extends TypesafeMap.Key>> requirements() {
        return null;
    }
}
