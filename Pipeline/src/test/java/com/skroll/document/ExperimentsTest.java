package com.skroll.document;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExperimentsTest extends TestCase {

    public void testSetupDirectories() throws Exception {
        String fileName = "src/test/resources/document/experiment-jsoup-node-extraction.html";

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

    public void testGson() {
        Gson gson = new Gson();

        Doc doc = new Doc("<div>123</div");
        String jsonString = gson.toJson(doc);
        Type docType = new TypeToken<Doc>() {}.getType();
        Doc newDoc = gson.fromJson(jsonString, docType);
        assert (newDoc.paragraph.size() == 2);

    }

    private class Doc {
        String html;
        String processedHtml;
        List<String> paragraph;
        List<String> paragraphIds;

        public Doc(String html) {
            this.html = html;
            paragraph = Lists.newArrayList("aaa", "bbb");
        }

    }


    public void testRegex() {
        String in = "The quick brown fox \"jumped over\" the \"lazy\" dog";
        Pattern p = Pattern.compile( "\"([^\"]*)\"" );
        Matcher m = p.matcher( in );
        while( m.find()) {
            System.err.println( m.group( 1 ));
        }
    }
}