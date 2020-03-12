/*
 * File    : ArithmeticMean.java
 * Created : 07-Jul-2009
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2009 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.statistic.average;

import emolib.statistic.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>ArithmeticMean</i> class calculates the arithmetic mean of the
 * emotional data.
 *
 * <p>
 * The arithmetic mean has been proposed for being the statistic that contemplates
 * an even contribution of the different emotional dimensions of the sample of the population.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class ArithmeticMean extends Statistic {

    public final static String PROP_THRESHOLD = "threshold";
    private float threshold;

    // Since the processing pipeline creates a new data object (which contains a paragraph)
    // every time a result is issued, this (paragraph) object will contain the statistics of
    // the whole document so that the last paragraph also delivers the final figures.
    private float accumDocValence;
    private float accumDocActivation;
    private float accumDocControl;
    private float amountOfParagraphs;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_THRESHOLD, PropertyType.FLOAT);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        threshold = ps.getFloat(PROP_THRESHOLD, Float.parseFloat("5.75"));
    }


    /**
     * Method to initialize the ArithmeticMean.
     */
    public void initialize() {
        accumDocValence = 0;
        accumDocActivation = 0;
        accumDocControl = 0;
        amountOfParagraphs = 0;
    }


    /* (non-Javadoc)
     * @see emolib.util.proc.DataProcessor#flush()
     */
    public void flush() {
        initialize();
    }


    /**
     * Main constructor of the ArithmeticMean.
     */
    public ArithmeticMean() {
    }


    /**
     * Method to perform the arithmetic mean calculations at the paragraph and sentence level.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyStatistics(TextData inputTextDataObject) {
        float meanValence, meanActivation, meanControl;
        int numberOfEmotionalWords = 0;
        int numberOfEmotionalSentences = 0;
        int numberOfEmotionalParagraphs = 0;
        boolean modifierFlag = false;
        boolean negationFlag = false;
        float modifierValue = 0;
        // This exists due to float conversion problems.
        Integer aux;
        float accumParVal, accumParAct, accumParCon;
        ParagraphData tempParagraph;
        float accumSenVal, accumSenAct, accumSenCon;
        SentenceData tempSentence;
        float accumWordsVal, accumWordsAct, accumWordsCon;
        WordData tempWordData;

        // Useless code because only one paragraph is analysed at a time.
        accumParVal = 0;
        accumParAct = 0;
        accumParCon = 0;
        numberOfEmotionalParagraphs = 0;

        for (int numberOfParagraph = 0; numberOfParagraph < inputTextDataObject.getNumberOfParagraphs();
        numberOfParagraph++) {
            tempParagraph = inputTextDataObject.getParagraphData(numberOfParagraph);

            accumSenVal = 0;
            accumSenAct = 0;
            accumSenCon = 0;
            numberOfEmotionalSentences = 0;
            for (int numberOfSentence = 0; numberOfSentence < tempParagraph.getNumberOfSentences();
            numberOfSentence++) {
                tempSentence = tempParagraph.getSentenceData(numberOfSentence);

                accumWordsVal = 0;
                accumWordsAct = 0;
                accumWordsCon = 0;
                numberOfEmotionalWords = 0;
                negationFlag = false;
                for (int numberOfWord = 0; numberOfWord < tempSentence.getNumberOfWords(); numberOfWord++) {
                    tempWordData = tempSentence.getWordData(numberOfWord);
                    if (tempWordData.containsEmotionalDimentions()) {
                        if (modifierFlag) {
                            if (modifierValue > 0) {
                                // Positive modifier
                                if (tempWordData.getEmotionalValence() > threshold) {
                                    // Positive emotional word
                                    accumWordsVal += tempWordData.getEmotionalValence() + modifierValue;
                                } else if (tempWordData.getEmotionalValence() < threshold) {
                                    // Negative emotional word
                                    accumWordsVal += tempWordData.getEmotionalValence() - modifierValue;
                                } else {
                                    // Neutral emotional word, possibly an OOV word.
                                    accumWordsVal += tempWordData.getEmotionalValence();
                                }
                            } else {
                                // Negative modifier, accumWordsVals pivot around the threshold.
                                if (tempWordData.getEmotionalValence() > threshold) {
                                    // Positive emotional word
                                    accumWordsVal += pivotValue(tempWordData.getEmotionalValence()) - modifierValue;
                                } else if (tempWordData.getEmotionalValence() < threshold) {
                                    // Negative emotional word
                                    accumWordsVal += pivotValue(tempWordData.getEmotionalValence()) + modifierValue;
                                } else {
                                    // Neutral emotional word, possibly an OOV word.
                                    accumWordsVal += tempWordData.getEmotionalValence();
                                }
                            }
                            modifierFlag = false;
                        } else {
                            accumWordsVal += tempWordData.getEmotionalValence();
                        }
                        accumWordsAct += tempWordData.getEmotionalActivation();
                        accumWordsCon += tempWordData.getEmotionalControl();
                        numberOfEmotionalWords++;
                    } else {
                        if (tempWordData.isModifier()) {
                            modifierValue = tempWordData.getModifierValue();
                            modifierFlag = true;
                        }
                        // This works for an odd number of negation adverbs.
                        if (tempWordData.isNegationAdverb()) {
                            if (negationFlag == false) {
                                negationFlag = true;
                            } else {
                                negationFlag = false;
                            }
                        }
                    }
                }
                if (numberOfEmotionalWords > 0) {
                    // Average dimensions for the given sentence
                    aux = new Integer(numberOfEmotionalWords);
                    meanValence = accumWordsVal / aux.floatValue();
                    meanActivation = accumWordsAct / aux.floatValue();
                    meanControl = accumWordsCon / aux.floatValue();
                    if (negationFlag) {
                        // Negation adverb, mean values pivot around the threshold.
                        meanValence = pivotValue(meanValence);
                        meanActivation = pivotValue(meanActivation);
                        meanControl = pivotValue(meanControl);
                    }
                    tempSentence.setEmotionalContent();
                    tempSentence.setEmotionalValence(meanValence);
                    tempSentence.setEmotionalActivation(meanActivation);
                    tempSentence.setEmotionalControl(meanControl);
                    accumSenVal += meanValence;
                    accumSenAct += meanActivation;
                    accumSenCon += meanControl;
                    numberOfEmotionalSentences++;
                }
            }
            if (numberOfEmotionalSentences > 0) {
                // Average dimensions for the given paragraph
                aux = new Integer(numberOfEmotionalSentences);
                meanValence = accumSenVal / aux.floatValue();
                meanActivation = accumSenAct / aux.floatValue();
                meanControl = accumSenCon / aux.floatValue();
                tempParagraph.setEmotionalContent();
                tempParagraph.setEmotionalValence(meanValence);
                tempParagraph.setEmotionalActivation(meanActivation);
                tempParagraph.setEmotionalControl(meanControl);
                accumParVal += meanValence;
                accumParAct += meanActivation;
                accumParCon += meanControl;
                numberOfEmotionalParagraphs++;
            }
        }
        // Quite useless, sincerely.
        inputTextDataObject.setEmotionalContent();
        if (numberOfEmotionalParagraphs > 0) {
            // Average dimensions for the given document
            amountOfParagraphs = amountOfParagraphs + 1;
            accumDocValence += accumParVal;
            accumDocActivation += accumParAct;
            accumDocControl += accumParCon;
            meanValence = accumDocValence / amountOfParagraphs;
            meanActivation = accumDocActivation / amountOfParagraphs;
            meanControl = accumDocControl / amountOfParagraphs;
            inputTextDataObject.setEmotionalValence(meanValence);
            inputTextDataObject.setEmotionalActivation(meanActivation);
            inputTextDataObject.setEmotionalControl(meanControl);
        } else {
            // There's no emotional paragraph in the history.
            meanValence = threshold;
            meanActivation = threshold;
            meanControl = threshold;
            inputTextDataObject.setEmotionalValence(meanValence);
            inputTextDataObject.setEmotionalActivation(meanActivation);
            inputTextDataObject.setEmotionalControl(meanControl);
        }
    }


    /**
     * Function to pivot emotional values around the threshold.
     * The pivoting equation is p' = ((c-p)/(c-a))*(b-c) + c for a center value 'c', a lower bound 'a' and
     * an upper bound 'b'.
     *
     * @param inputValue The input emotional value.
     *
     * @return The pivoted value.
     */
    private float pivotValue(float inputValue) {
        // float pivotedValue = ((threshold - inputValue) / threshold) * (10 - threshold) + threshold;
        // The ANEW dimensions range from 1 to 9
        float pivotedValue = ((threshold - inputValue) / (threshold - 1)) * (9 - threshold) + threshold;
        return pivotedValue;
    }

}

