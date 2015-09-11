package com.skroll.document.factory;

import com.google.common.cache.CacheLoader;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.CoreAnnotations.TokenAnnotation;
import com.skroll.document.annotation.DocTypeAnnotationHelper;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SingleParaDocumentFactoryImpl is used to convert the entire document into a single para. It is used for DocType Classification.
 * By converting the entire document into one paragraph, we can use the existing model code that works on paragraph level.
 * Created by saurabhagarwal on 8/30/2015.
 */
public class SingleParaDocumentFactoryImpl extends FileSystemDocumentFactoryImpl {

    public static final Logger logger = LoggerFactory.getLogger(SingleParaDocumentFactoryImpl.class);
    protected CorpusFSDocumentFactoryImpl corpusDocumentFactory;

    @Inject
    public SingleParaDocumentFactoryImpl(CorpusFSDocumentFactoryImpl corpusDocumentFactory) {
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
    protected CacheService<Document> getDocumentCache() {
        return null;
    }

    @Override
    protected ConcurrentHashSet<String> getSaveLaterDocumentId() {
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