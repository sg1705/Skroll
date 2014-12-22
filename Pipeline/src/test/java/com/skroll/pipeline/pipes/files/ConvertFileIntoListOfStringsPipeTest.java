package com.skroll.pipeline.pipes.files;

import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

public class ConvertFileIntoListOfStringsPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        Pipeline<String, List<String>> pipeline =
                new Pipeline.Builder<String, List<String>>()
                .add(Pipes.FILE_INTO_LIST_OF_STRING)
                .build();

        List<String> list = pipeline.process("src/test/resources/file-into-list-of-string-test.txt");
        assert ( list.size() == 307);
    }
}