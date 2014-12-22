package com.skroll.pipeline;

import com.google.common.collect.Lists;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ExperimentsTest extends TestCase {

    public void testSetupDirectories() throws Exception {
        String fileName = "src/test/resources/experiment-jsoup-node-extraction.html";

        try {
            String htmlText = Utils.readStringFromFile(fileName);
            List<String> input = new ArrayList<String>();
            input.add(htmlText);

            //create a pipeline
            Pipeline<List<String>, List<String>> documentChunkingPipeline =
                    new Pipeline.Builder<List<String>, List<String>>()
                            .add(Pipes.PARAGRAPH_CHUNKER)
                            .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                            //.add(Pipes.LINE_REMOVE_NBSP_FILTER)
                            .build();

            List<String> paragraphs = documentChunkingPipeline.process(input);

            // use jsoup to extract node
            Document doc = Jsoup.parse(htmlText);
            int ii = 0;
/*
            for (String para : paragraphs) {
                para = para.trim();
                Elements elements = doc.select("*:contains(" + para + ")");
                System.out.println("Para:[" + ii + "] ..Size:[" + elements.size() + "]" );
                if (elements.size() == 0) {
                    System.out.println("-----");
                    System.out.println(para);
                    System.out.println("-----");
                }
                ii++;
            }
*/

            Elements elements = doc.select("*:contains(" + paragraphs.get(505) + ")");
            System.out.println("Para:[" + ii + "] ..Size:[" + elements.size() + "]" );



        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWrapHtml() {
        String html = "<div>abc</div><div>xyz</div>";

        Document doc = Jsoup.parse(html);
        List<Node> childNodes = doc.childNodes();
        childNodes.get(0).wrap("<span id=\"adfde3\"></span>");
        System.out.println(childNodes.get(0).toString());

    }

}