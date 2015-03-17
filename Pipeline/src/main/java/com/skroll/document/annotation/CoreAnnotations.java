package com.skroll.document.annotation;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.pipeline.util.EraserUtils;

import java.util.List;
import java.util.Set;

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

    public static class ParagraphFragmentAnnotation implements CoreAnnotation<List<CoreMap>> {
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
     * This is a temporary annotation used during training
     */
    public static class WordSetForTrainingAnnotation implements CoreAnnotation<Set<String>> {
        public Class<Set<String>> getType() {
            return EraserUtils.<Class<Set<String>>> uncheckedCast(Set.class);
        }
    }


    // annotation for paragraphs start with quotes
    public static class StartsWithQuote implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
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
     * annotation to indicate that a paragraph has a user observation
     */
    public static class IsUserObservationAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    /**
     * annotation to indicate that a paragraph is trained by trainer
     */
    public static class IsTrainerFeedbackAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }
    /**
     * annotation for keeping the user training weight for a paragraph
     */
    public static class TrainingWeightAnnotation implements CoreAnnotation<List<Float>> {
        public Class<List<Float>> getType() {
            return EraserUtils.<Class<List<Float>>> uncheckedCast(List.class);
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

    public static class DefinedTermListAnnotation implements CoreAnnotation<List<List<Token>>> {
        public Class<List<List<Token>>> getType() {
            return EraserUtils.<Class<List<List<Token>>>> uncheckedCast(List.class);
        }
    }

    /**
     * Annotations for CoreLabel
     */
    public static class IsBoldAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    public static class InQuotesAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    public static class IsItalicAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    public static class IsUnderlineAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    /**
     * Annotation to indicate if it is all uppercase
     */
    public static class IsUpperCaseAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }


    /**
     * Annotation to indicate that this is center aligned
     */
    public static class IsCenterAlignedAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    /**
     * Annotation to indicate that this is a page break paragraph
     */
    public static class IsPageBreakAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }


    /**
     * Annotation for training. It specifies the index of a word token in the paragraph.
     */
    public static class Index implements CoreAnnotation<Integer> {
        public Class<Integer> getType() {
            return Integer.class;
        }
    }

    /**
     * Annotation for font size
     */
    public static class FontSizeAnnotation implements CoreAnnotation<String> {
        public Class<String> getType() {
            return String.class;
        }
    }

    /**
     * Annotation for training. It specifies the index of a word token in the paragraph.
     */
    public static class TokenStartsWithUpperCaseCount implements CoreAnnotation<Integer> {
        public Class<Integer> getType() {
            return Integer.class;
        }
    }

    /**
     * Annotation to indicate that this paragraph has an anchor tag
     */
    public static class IsAnchorAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    // TOC Annotation
    public static class TOCListAnnotation implements CoreAnnotation<List<Token>> {
        public Class<List<Token>> getType() {
            return EraserUtils.<Class<List<Token>>> uncheckedCast(List.class);
        }
    }
    public static class IsTOCAnnotation implements CoreAnnotation<Boolean> {
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }
}
