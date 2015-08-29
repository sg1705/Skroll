package com.skroll.util;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

public class UniqueIdGeneratorTest {

    @Test
    public void testGenerateId() throws Exception {
        String id1 = UniqueIdGenerator.generateId("123456");
        String id2 = UniqueIdGenerator.generateId("123456");
        assert(id1.equals(id2));
    }

    @Test
    public void testGenerateIdNotEqual() throws Exception {
        String id1 = UniqueIdGenerator.generateId("123456 ");
        String id2 = UniqueIdGenerator.generateId("123456");
        assert(!id1.equals(id2));
    }

   @Test
    public void testGenerateIds() throws Exception {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File("src/test/resources/document/"));
        for (File f : iterable) {
            if (f.isFile()) {
                String fileName = f.getPath();
                String content = Files.toString(new File(fileName), Charset.defaultCharset());
                System.out.println("unique doc id:" + UniqueIdGenerator.generateId(content));
            }
        }
    }
}
