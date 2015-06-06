package com.skroll.document;

import com.google.common.io.Files;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Created by saurabh on 4/16/15.
 */
public class DocumentFactory {

    public static final Logger logger = LoggerFactory.getLogger(DocumentFactory.class);
    private static HashMap<String, Document> documents = new HashMap();
    private static Configuration configuration = new Configuration();
    private static String PRE_EVALUATED_FOLDER = configuration.get("preEvaluatedFolder", "/tmp/");


    public Document get(String documentId) {
        if (documents.containsKey(documentId)) {
            return documents.get(documentId);
        } else {
            //document does not exist in map
            //let's fetch it from the filesystem
            try {
                logger.debug("Fetching [{}] from filesystem", documentId);
                String jsonString = Files.toString(new File(PRE_EVALUATED_FOLDER + documentId), Charset.defaultCharset());
                Document doc = JsonDeserializer.fromJson(jsonString);
                return doc;
            } catch (Exception e) {
                logger.error("Document [{}] cannot be parsed", documentId, e );
                return null;
            }
        }
    }

    public void putDocument(String documentId, Document document) {
        documents.put(documentId, document);
    }

}
