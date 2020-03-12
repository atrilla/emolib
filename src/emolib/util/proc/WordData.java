/*
 * File    : WordData.java
 * Created : 10-Nov-2008
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

package emolib.util.proc;

import java.util.ArrayList;

/**
 * The <i>WordData</i> class is a  Data construct
 * that holds information at word-level.
 * This class implements the appropriate interfaces required by the
 * Configuration Manager.
 * <p>
 * A WordData is aimed at containing the maximum amount of information at
 * word-level. Since words are the minimum unit that represents a
 * certain amount of information at semantic level, this construct has
 * been produced in order to group the maximum amount of useful
 * and distinct information for this minimum unit.
 * </p>
 * <p>
 * The information that a WordData object should be able to contain:
 * <ul>
 *     <li>Word: the word in question</li>
 *     <li>Class: the lexical category</li>
 *     <li>Part-of-speech: the function in the sentence</li>
 *     <li>Paragraph/sentence: numeration in the document</li>
 *     <li>Sense: the meaning</li>
 *     <li>Stem: the stem of the word</li>
 *     <li>Emotional content: is a potential affect container</li>
 *     <li>Emotional valence: the valence of the emotion.
 *     <li>Emotional activation: the activation of the emotion.
 *     <li>Emotional control: the control of the emotion.
 * </ul>
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class WordData implements Data, Cloneable {

    private String word;
    private String wordClass;
    private String wordPOS;
    private int paragraphCount;
    private int sentenceCount;
    private ArrayList wordSense;
    private ArrayList wordSenseStem;
    private boolean hasSynonyms;
    private String wordStem;
    private boolean emotionalContent;
    private float emotionalValence;
    private float emotionalActivation;
    private float emotionalControl;
    private float modifierValue;
    private String emotionalCategory;
    private boolean isNoun;
    private boolean isVerb;
    private boolean isAdjective;
    private boolean isAdverb;
    private boolean isModifier;
    private boolean isNegationAdverb;

    //Boolean flags for data storage.
    private boolean containsSynonymsFlag;
    private boolean containsSynonymsStemFlag;
    private boolean containsPOSFlag;
    private boolean containsEmotionalDimentions;


    /**
     * Constructs an empty WordData object.
     */
    public WordData() {
        word = "";
        wordClass = "";
        wordPOS = "";
        wordSense = null;
        wordSenseStem = null;
        wordStem = "";
        emotionalContent = false;
        emotionalValence = -1;
        emotionalActivation = -1;
        emotionalControl = -1;
        modifierValue = 0;
        emotionalCategory = "";
        isNoun = false;
        isVerb = false;
        isAdjective = false;
        isAdverb = false;
        isModifier = false;
        isNegationAdverb = false;

        containsSynonymsFlag = false;
        containsSynonymsStemFlag = false;
        containsPOSFlag = false;
        containsEmotionalDimentions = false;
    }


    /**
     * Constructs and fills a WordData object.
     *
     * @param word Input word for the constructor.
     * @param wordClass Input wordClass for the constructor.
     */
    public WordData(String word, String wordClass) {
        this.word = word;
        this.wordClass = wordClass;
    }


    /**
     * Sets the word of the object.
     *
     * @param wordToBeSet The word to be set.
     */
    public void setWord(String wordToBeSet) {
        word = wordToBeSet;
    }


    /**
     * Gets the word of the object.
     *
     * @return The word of the object.
     */
    public String getWord() {
        return word;
    }


    /**
     * Sets the word-class of the object.
     *
     * @param wordClassToBeSet The word-class to be set.
     */
    public void setWordClass(String wordClassToBeSet) {
        wordClass = wordClassToBeSet;
    }


    /**
     * Gets the word-class of the object.
     *
     * @return The word-class of the object.
     */
    public String getWordClass() {
        return wordClass;
    }


    /**
     * Sets the Part-Of-Speech of the word.
     *
     * @param thePOS The POS of the word.
     */
    public void setWordPOS(String thePOS) {
        wordPOS = thePOS;
        containsPOSFlag = true;
    }


    /**
     * Retrieves the Part-Of-Speech of the word.
     *
     * @return The POS of the word.
     */
    public String getWordPOS() {
        return wordPOS;
    }


    /**
     * Function to check if the word has a POS tag.
     *
     * @return True if this WordData object contains a POS tag.
     */
    public boolean containsWordPOS() {
        return containsPOSFlag;
    }


    /**
     * Sets the stem of the word.
     *
     * @param theStem The stem of the word.
     */
    public void setWordStem(String theStem) {
        wordStem = theStem;
    }


    /**
     * Retrieves the stem of the word.
     *
     * @return The stem of the word.
     */
    public String getWordStem() {
        return wordStem;
    }


    /**
     * Marks the word as emotional container.
     */
    public void setEmotionalContent() {
        emotionalContent = true;
    }


    /**
     * Retrieves the emotional content of the word.
     *
     * @return The emotional content of the word.
     */
    public boolean hasEmotionalContent() {
        return emotionalContent;
    }


    /**
     * Sets the valence of the emotion of this word.
     *
     * @param inputValence The valence of the emotion of this word.
     */
    public void setEmotionalValence(float inputValence) {
        emotionalValence = inputValence;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the valence of the emotion of this word.
     *
     * @return The valence of the emotion of this word.
     */
    public float getEmotionalValence() {
        return emotionalValence;
    }


    /**
     * Sets the activation of the emotion of this word.
     *
     * @param inputActivation The activation of the emotion of this word.
     */
    public void setEmotionalActivation(float inputActivation) {
        emotionalActivation = inputActivation;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the activation of the emotion of this word.
     *
     * @return The activation of the emotion of this word.
     */
    public float getEmotionalActivation() {
        return emotionalActivation;
    }


    /**
     * Sets the control of the emotion of this word.
     *
     * @param inputControl The control of the emotion of this word.
     */
    public void setEmotionalControl(float inputControl) {
        emotionalControl = inputControl;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the control of the emotion of this word.
     *
     * @return The control of the emotion of this word.
     */
    public float getEmotionalControl() {
        return emotionalControl;
    }


    /**
     * Determines if this WordData object has emotional dimentions.
     *
     * @return True if this WordData object has emotional dimentions.
     */
    public boolean containsEmotionalDimentions() {
        return containsEmotionalDimentions;
    }


    /**
     * Sets the category of the emotion of this word.
     *
     * @param inputCategory The category of the emotion of this word.
     */
    public void setEmotionalCategory(String inputCategory) {
        emotionalCategory = inputCategory;
    }


    /**
     * Retrieves the category of the emotion of this word.
     *
     * @return The category of the emotion of this word.
     */
    public String getEmotionalCategory() {
        return emotionalCategory;
    }


    /**
     * Sets this words as a noun.
     */
    public void setAsNoun() {
        isNoun = true;
    }


    /**
     * Sets this words as an adverb.
     */
    public void setAsAdverb() {
        isAdverb = true;
    }


    /**
     * Sets this words as an adjective.
     */
    public void setAsAdjective() {
        isAdjective = true;
    }


    /**
     * Sets this words as a verb.
     */
    public void setAsVerb() {
        isVerb = true;
    }


    /**
     * Function to check if this word is a noun.
     *
     * @return True if it is a noun.
     */
    public boolean isNoun() {
        return isNoun;
    }


    /**
     * Function to check if this word is an adverb.
     *
     * @return True if it is an adverb.
     */
    public boolean isAdverb() {
        return isAdverb;
    }


    /**
     * Function to check if this word is an adjective.
     *
     * @return True if it is an adjective.
     */
    public boolean isAdjective() {
        return isAdjective;
    }


    /**
     * Function to check if this word is a verb.
     *
     * @return True if it is a verb.
     */
    public boolean isVerb() {
        return isVerb;
    }


    /**
     * Sets the number of paragraph.
     *
     * @param numberOfParagraph The number of paragraph.
     */
    public void setNumberOfParagraph(int numberOfParagraph) {
        paragraphCount = numberOfParagraph;
    }


    /**
     * Retrieves the number of paragraph.
     *
     * @return The number of paragraph.
     */
    public int getNumberOfParagraph() {
        return paragraphCount;
    }


    /**
     * Sets the number of sentence.
     *
     * @param numberOfSentence The number of sentence.
     */
    public void setNumberOfSentence(int numberOfSentence) {
        sentenceCount = numberOfSentence;
    }


    /**
     * Retrieves the number of sentence.
     *
     * @return The number of sentence.
     */
    public int getNumberOfSentence() {
        return sentenceCount;
    }


    /**
     * Method to set the sense of the word.
     *
     * @param synonyms The synonyms of the word according to its sense.
     */
    public void setSense(ArrayList synonyms) {
        wordSense = synonyms;
        containsSynonymsFlag = true;
    }


    /**
     * Function to retrieve the sense of the word.
     *
     * @return The synonyms of the word according to its sense.
     */
    public ArrayList getSense() {
        return wordSense;
    }


    /**
     * Function to determine if this WordData object contains synonyms.
     *
     * @return True if this WordData object contains synonyms.
     */
    public boolean containsSynonyms() {
        return containsSynonymsFlag;
    }


    /**
     * Method to set the stememd sense of the word.
     *
     * @param stemmedSynonyms The stemmed synonyms of the word according to its sense.
     */
    public void setStemmedWordSense(ArrayList stemmedSynonyms) {
        wordSenseStem = stemmedSynonyms;
        containsSynonymsStemFlag = true;
    }


    /**
     * Function to retrieve the sense of the word.
     *
     * @return The stemmed synonyms of the word according to its sense.
     */
    public ArrayList getStemmedWordSense() {
        return wordSenseStem;
    }


    /**
     * Function to determine if this WordData object contains stemmed synonyms.
     *
     * @return True if this WordData object contains stemmed synonyms.
     */
    public boolean containsStemmedSynonyms() {
        return containsSynonymsStemFlag;
    }



    /**
     * Method to set this WordData object as a modifier.
     */
    public void setAsModifier() {
        isModifier = true;
    }


    /**
     * Function to check if this word is a modifier.
     *
     * @return True if it is a modifier.
     */
    public boolean isModifier() {
        return isModifier;
    }


    /**
     * Method to set this WordData object as a negation adverb.
     */
    public void setAsNegationAdverb() {
        isNegationAdverb = true;
    }


    /**
     * Function to check if this word is a negation adverb.
     *
     * @return True if it is a negation adverb.
     */
    public boolean isNegationAdverb() {
        return isNegationAdverb;
    }


    /**
     * Method to set the modifier value.
     *
     * @param modifierValue The modifier value.
     */
    public void setModifierValue(float modifierValue) {
        this.modifierValue = modifierValue;
    }


    /**
     * Function to retrieve the modifier value.
     *
     * @return The modifier value.
     */
    public float getModifierValue() {
        return modifierValue;
    }


    /**
     * Returns a clone of this Data object.
     *
     * @return A clone of this Data object.
     */
    public Object clone() {
        try {
            Data data = (Data) super.clone();
            return data;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
    }

}

