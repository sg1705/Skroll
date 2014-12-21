package com.skroll.pipeline.util;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static com.google.common.io.Files.*;

/**
 * Created by sagupta on 12/12/14.
 */
public class Utils {

    private static final String DEFAULT_CHARSET = "CP1252";
    public static void prettyPrintList(List<String> strs) {
        for (int ii = 0; ii < strs.size(); ii++) {
            System.out.println(strs.get(ii));
            System.out.println("------------");
        }
    }

    public static String readStringFromFile(String fileName) throws Exception {
        return Files.toString(new File(fileName), Charset.forName(DEFAULT_CHARSET) );
    }

    public static void writeToFile(String fileName, String data)  {
        try {
            Files.write(data.getBytes(), new File(fileName));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
