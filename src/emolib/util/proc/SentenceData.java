/*
 * File    : SentenceData.java
 * Created : 02-Jul-2009
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

package emolib.util.proc;

import java.util.ArrayList;

/**
 * The <i>SentenceData</i> class is a  Data construct
 * that holds information at sentence-level.
 * This class implements the appropriate interfaces required by the
 * Configuration Manager.
 * <p>
 * The information that a SentenceData object should be able to contain:
 * <ul>
 *     <li>Words: a reference to the words in the sentences (WordData objects)</li>
 *     <li>Emotional content: is a potential affect container</li>
 *     <li>Emotional valence: the valence of the emotion.
 *     <li>Emotional activation: the activation of the emotion.
 *     <li>Emotional control: the control of the emotion.
 * </ul>
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class SentenceData implements Data, Cloneable {

    private ArrayList<WordData> wordDataItems;
    private boolean emotionalContent;
    private float emotionalValence;
    private float emotionalActivation;
    private float emotionalControl;
    private String emotionalCategory;

    private boolean containsEmotionalDimentions;


    /**
     * Constructs an empty SentenceData object.
     */
    public SentenceData() {
        wordDataItems = new ArrayList<WordData>();
        emotionalContent = false;
        emotionalValence = -1;
        emotionalActivation = -1;
        emotionalControl = -1;
        emotionalCategory = "";

        containsEmotionalDimentions = false;
    }


    /**
     * Adds a WordData object into the sentence struct.
     *
     * @param wordDataToBeAdded The WordData to be added.
     */
    public void addWordData(WordData wordDataToBeAdded) {
        wordDataItems.add(wordDataToBeAdded);
    }


    /**
     * Gets the WordData object from the sentence struct.
     *
     * @param whichOne Which WordData must be retrieved.
     *
     * @return The WordData of the sentence.
     */
    public WordData getWordData(int whichOne) {
        return wordDataItems.get(whichOne);
    }


    /**
     * Retrieves the number of words.
     *
     * @return The number of words.
     */
    public int getNumberOfWords() {
        return wordDataItems.size();
    }


    /**
     * Marks the sentence as emotional container.
     */
    public void setEmotionalContent() {
        emotionalContent = true;
    }


    /**
     * Retrieves the emotional content of the sentence.
     *
     * @return The emotional content of the sentence.
     */
    public boolean hasEmotionalContent() {
        return emotionalContent;
    }


    /**
     * Sets the valence of the emotion of this sentence.
     *
     * @param inputValence The valence of the emotion of this sentence.
     */
    public void setEmotionalValence(float inputValence) {
        emotionalValence = inputValence;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the valence of the emotion of this sentence.
     *
     * @return The valence of the emotion of this sentence.
     */
    public float getEmotionalValence() {
        return emotionalValence;
    }


    /**
     * Sets the activation of the emotion of this sentence.
     *
     * @param inputActivation The activation of the emotion of this sentence.
     */
    public void setEmotionalActivation(float inputActivation) {
        emotionalActivation = inputActivation;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the activation of the emotion of this sentence.
     *
     * @return The activation of the emotion of this sentence.
     */
    public float getEmotionalActivation() {
        return emotionalActivation;
    }


    /**
     * Sets the control of the emotion of this sentence.
     *
     * @param inputControl The control of the emotion of this sentence.
     */
    public void setEmotionalControl(float inputControl) {
        emotionalControl = inputControl;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the control of the emotion of this sentence.
     *
     * @return The control of the emotion of this sentence.
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
     * Sets the category of the emotion of this sentence.
     *
     * @param inputCategory The category of the emotion of this sentence.
     */
    public void setEmotionalCategory(String inputCategory) {
        emotionalCategory = inputCategory;
    }


    /**
     * Retrieves the category of the emotion of this sentence.
     *
     * @return The category of the emotion of this sentence.
     */
    public String getEmotionalCategory() {
        return emotionalCategory;
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

