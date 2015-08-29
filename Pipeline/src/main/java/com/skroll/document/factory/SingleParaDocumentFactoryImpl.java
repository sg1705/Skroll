package com.skroll.document.factory;

import com.google.common.cache.CacheLoader;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.CoreAnnotations.TokenAnnotation;
import com.skroll.document.annotation.DocTypeAnnotationHelper;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by saurabh on 4/16/15.
 */
public class SingleParaDocumentFactoryImpl implements DocumentFactory {

    public static final Logger logger = LoggerFactory.getLogger(SingleParaDocumentFactoryImpl.class);
    protected Configuration configuration;
    protected String folder;
    protected int cacheSize;
    protected DocumentFactory corpusDocumentFactory;

    @Inject
    public SingleParaDocumentFactoryImpl(DocumentFactory corpusDocumentFactory) {
        this.corpusDocumentFactory = corpusDocumentFactory;
    }

    @Override
    public Document get(String documentId) throws Exception {
        Document doc = null;
        try {
            doc = corpusDocumentFactory.get(documentId);
        } catch (CacheLoader.InvalidCacheLoadException e){
            logger.error("doc is not Found in cache: {}", e.getMessage());
            return null;
        }
        return getEntireDocCollaspedAsSingleParagraph(doc);
    }

    @Override
    public void putDocument(Document document) throws Exception {

    }

    @Override
    public void saveDocument(Document document) throws Exception {

    }

    @Override
    public List<String> getDocumentIds() throws Exception {
        return null;
    }

    @Override
    public boolean isDocumentExist(String documentId) throws Exception {
        return false;
    }

    public Document getEntireDocCollaspedAsSingleParagraph(Document entireDocument) throws Exception{

        Document singleParaDocument = new Document();
        CoreMap singleParagraph = new CoreMap("collapsedPara", "collapsedPara");

        List<Token> allTokens = entireDocument.getParagraphs()
                .stream()
                .flatMap( paragraph -> paragraph.get(CoreAnnotations.TokenAnnotation.class).stream())
                .collect(Collectors.toList());
        singleParagraph.set(TokenAnnotation.class, allTokens);
        List<CoreMap> paraList = new ArrayList<>();
        paraList.add(singleParagraph);
        int docType = DocTypeAnnotationHelper.getDocType(entireDocument);
        float currentWeight = DocTypeAnnotationHelper.getDocTypeTrainingWeight(entireDocument);
        CategoryAnnotationHelper.annotateCategoryWeight(singleParagraph, docType,currentWeight);
        boolean isUserObserved = entireDocument.containsKey(CoreAnnotations.IsUserObservationAnnotation.class);
        singleParagraph.set(CoreAnnotations.IsUserObservationAnnotation.class, isUserObserved);
        singleParaDocument.set(CoreAnnotations.ParagraphsAnnotation.class, paraList);
        return singleParaDocument;
    }
}