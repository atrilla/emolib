/*
 * File    : SentenceSplitterBDT.java
 * Created : 04-Dec-2008
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2008 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.splitter.bdt;

import emolib.splitter.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>SentenceSplitterBDT</i> class performs the
 * sentence segmentation process through a hand-crafted
 * Binary Decision Tree (BDT).
 *
 * <p>
 * The decision tree for sentence boundary detection has been inspired by
 * the one that appears in (Reichel and Pfitzinger, 2006). Due to the fact
 * that the tokens are independent (they don't <i>end with</i> a punctuation
 * mark) the tree has not been kept the same. Refer to the article for more
 * details.
 * </p>
 * <p>
 * The diagram below shows this decision tree implementation
 * (YES vs. NO):<br>
 * <div align=center>
 * <img src="doc-files/bdt.png">
 * <br><i>Figure 1: Decision tree for sentence boundary detection.</i>
 * </div>
 * </p>
 * <p>
 * All the input sentences are required to be delimited by either a dot, an
 * exclamation mark or a question mark.
 * </p>
 * <p>
 * --<br>
 * (Reichel and Pfitzinger, 2006) Reichel, U.D. and Pfitzinger, H.R.,
 * <i>"Text Preprocessing for Speech Synthesis"</i>,
 * In Proc. TC-Star Speech to Speech Translation Workshop, pp 207-212., 2006.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class SentenceSplitterBDT extends SentenceSplitter {

    private int paragraphNumber;
    private int sentenceNumber;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
    }


    /**
     * Method to initialize the SentenceSplitterBDT.
     */
    public void initialize() {
        paragraphNumber = 1;
        sentenceNumber = 1;
    }


    /**
     * Main constructor of the SentenceSplitterBDT.
     */
    public SentenceSplitterBDT() {
    }


    /**
     * Method to perform the sentence segmentation process.
     * It is assumed that each data input/output from the system corresponds
     * to a paragraph in the text/document.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applySentenceSplitting(TextData inputTextDataObject) {
        WordData tempWordData;
        SentenceData tempSentenceData = new SentenceData();
        ParagraphData tempParagraphData = new ParagraphData();

        for (int numberOfWords = 0; numberOfWords < inputTextDataObject.getNumberOfWords(); numberOfWords++) {
            tempWordData = inputTextDataObject.getWordData(numberOfWords);
            // This was the original design, it is kept for informational purposes.
            // Now the sentence and paragraph information is bundled in their own structures.
            tempWordData.setNumberOfParagraph(paragraphNumber);
            tempWordData.setNumberOfSentence(sentenceNumber);
            String tempWord = tempWordData.getWord();
            boolean sentenceBoundary = false;
            if (tempWord.equals("?") || tempWord.equals("!") || tempWord.equals(".")) {
                sentenceBoundary = true;
            } else if (numberOfWords == (inputTextDataObject.getNumberOfWords() - 1)) {
                sentenceBoundary = true;
                //            The new implementation misses this piece of code.
                //            } else {
                    //                if (startsWithSmallLetter(tempWord) == false) {
                        //                    sentenceBoundary = true;
                    //                }
                }

                tempSentenceData.addWordData(tempWordData);
                if (sentenceBoundary == true) {
                    tempParagraphData.addSentenceData(tempSentenceData);
                    tempSentenceData = new SentenceData();
                    sentenceNumber++;
                }
            }
            paragraphNumber++;
            sentenceNumber = 1;
            inputTextDataObject.addParagraphData(tempParagraphData);
        }


        /**
         * Determines if the word in question statrts with a small letter.
         *
         * @param wordInQuestion The word in question.
         *
         * @return True if the word in question starts with a small letter.
         */
        private boolean startsWithSmallLetter(String wordInQuestion) {
            Character tempCharacter = new Character(wordInQuestion.charAt(0));
            return tempCharacter.isLowerCase(tempCharacter.charValue());
        }

    }

