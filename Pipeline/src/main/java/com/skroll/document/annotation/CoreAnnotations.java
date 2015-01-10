package com.skroll.document.annotation;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.pipeline.util.EraserUtils;

import java.util.List;

/**
 * Created by saurabh on 1/3/15.
 */
public class CoreAnnotations {

    /**
     * Static methods only
     */
    private CoreAnnotations() {

    }

    /**
     * The CoreMap key identifying the annotation's text.
     *
     * Note that this key is intended to be used with many different kinds of
     * annotations - documents, sentences and tokens all have their own text.
     */
    public static class TextAnnotation implements CoreAnnotation<String> {
        public Class<String> getType() {
            return String.class;
        }
    }


    /**
     * The CoreMap key identifying HTMLText which is the source of the document
     */
    public static class HTMLTextAnnotation implements CoreAnnotation<String> {
        public Class<String> getType() {
            return String.class;
        }
    }

    /**
     * CoreMap key identifying process html . For example, in a document when
     * definition has been identified, the document needs to be processed so that
     * definitions can be linked across the document.
     */
    public static class ProcessedHTMLTextAnnotation implements CoreAnnotation<String> {
        public Class<String> getType() {
            return String.class;
        }
    }


    /**
     * CoreMap key identifying the document id
     */
    public static class IdAnnotation implements CoreAnnotation<String> {
        public Class<String> getType() {
            return String.class;
        }
    }


    /**
     * CoreMap key identifying the paragraphs
     */
    public static class ParagraphsAnnotation implements CoreAnnotation<List<CoreMap>> {
        public Class<List<CoreMap>> getType() {
            return EraserUtils.<Class<List<CoreMap>>> uncheckedCast(List.class);
        }
    }


    public static class TokenAnnotation implements CoreAnnotation<List<Token>> {
        public Class<List<Token>> getType() {
            return EraserUtils.<Class<List<Token>>> uncheckedCast(List.class);
        }
    }



    /**
     * CoreMap key identifying the document id
     */
    public static class ParagraphIdAnnotation implements CoreAnnotation<String> {
        public Class<String> getType() {
            return String.class;
        }
    }

    public static class IsDefinitionAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }



    /**
     * CoreMap key identifying the definitions in paragraph. Each CoreMap in the list is
     * a definition. A definition can be accessed using TextAnnotation.
     *
     * <pre>
     *     List<CoreMap> map = Document.getDocument()
     *                          .get(CoreAnnotations.ParagraphsAnnotation.class);
     *
     *     CoreMap def = map.get(0).get(CoreAnnotations.DefinitionAnnotation.class);
     *     String defintionTerm = def.get(CoreAnnotations.TextAnnotation.class);
     *
     *     List<CoreLabel> label = map.get(0).get(CoreAnnotations.TokenAnnotation.class);
     *     label.get(0).isBold();
     *     boolean isBold = label.get(0).get(CoreAnnotations.BoldAnnotation.class);
     *     label.get(0).is
     *
     * </pre>
     */
    public static class DefinedTermsAnnotation implements CoreAnnotation<List<Token>> {
        public Class<List<Token>> getType() {
            return EraserUtils.<Class<List<Token>>> uncheckedCast(List.class);
        }
    }


    /**
     * Annotations for CoreLabel
     */
    public static class BoldAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    public static class InQuotesAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    public static class ItalicAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    public static class UnderlineAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }



}
