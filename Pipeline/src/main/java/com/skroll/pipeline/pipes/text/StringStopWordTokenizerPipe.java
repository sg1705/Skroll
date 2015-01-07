package com.skroll.pipeline.pipes.text;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.tokenizer.WhitespaceNormTokenizerFactory;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sagupta on 12/15/14.
 */
public class StringStopWordTokenizerPipe extends SyncPipe<String, List<String>> {

    private static Set<String> STOP_WORDS = new HashSet<String>();

    private static String STOCK_STOP_WORDS = "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your";
    static {
        STOP_WORDS.add("of");
        STOP_WORDS.add("to");
        STOP_WORDS.add("a");
        STOP_WORDS.add("the");
        STOP_WORDS.add("and");
        STOP_WORDS.add("for");
        STOP_WORDS.add("on");
        STOP_WORDS.add("as");
        STOP_WORDS.add("that");
        STOP_WORDS.add("this");
        STOP_WORDS.add("with");
        STOP_WORDS.add("an");
        STOP_WORDS.add("in");
        STOP_WORDS.add(",");
        STOP_WORDS.add(" ");
        STOP_WORDS.add("(");
        STOP_WORDS.add(")");
        STOP_WORDS.add(".");
        STOP_WORDS.add("-");
        STOP_WORDS.add("shall");
        STOP_WORDS.add("be");

    }

    @Override
    public List<String> process(String input) {
        List<String> tokens = new ArrayList<String>();
        TokenizerFactory tokFactory = IndoEuropeanTokenizerFactory.INSTANCE;
        //tokFactory = new LowerCaseTokenizerFactory(tokFactory);
        tokFactory = new WhitespaceNormTokenizerFactory(tokFactory);
        //tokFactory = new StopTokenizerFactory(tokFactory, STOP_WORDS);
        Tokenizer tokenizer = tokFactory.tokenizer(input.toCharArray(), 0, input.length());
        String token = null;
        while ((token = tokenizer.nextToken()) != null) {
            tokens.add(token);
        }
        return tokens;
    }
}

