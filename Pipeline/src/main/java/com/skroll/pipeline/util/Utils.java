package com.skroll.pipeline.util;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by sagupta on 12/12/14.
 */
public class Utils {

    /**
     * Reads from a given pipeline name
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String readStringFromFile(String fileName) throws Exception {
        return Files.toString(new File(fileName), Constants.DEFAULT_CHARSET);
    }

    /**
     * Reads a text file from a given File object
     * @param file
     * @return string
     * @throws Exception
     */

    public static String readStringFromFile(File file) throws Exception {
        return Files.toString(file, Constants.DEFAULT_CHARSET );
    }


    public static void writeToFile(String fileName, String data)  {
        try {
            Files.write(data.getBytes(), new File(fileName));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
