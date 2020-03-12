/*
 * File    : ParagraphData.java
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
 * The <i>ParagraphData</i> class is a  Data construct
 * that holds information at paragraph-level.
 * This class implements the appropriate interfaces required by the
 * Configuration Manager.
 * <p>
 * The information that a ParagraphData object should be able to contain:
 * <ul>
 *     <li>Sentences: a reference to the sentences in the text (SentenceData objects)</li>
 *     <li>Emotional content: is a potential affect container</li>
 *     <li>Emotional valence: the valence of the emotion.
 *     <li>Emotional activation: the activation of the emotion.
 *     <li>Emotional control: the control of the emotion.
 * </ul>
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class ParagraphData implements Data, Cloneable {

    private ArrayList<SentenceData> sentenceDataItems;
    private boolean emotionalContent;
    private float emotionalValence;
    private float emotionalActivation;
    private float emotionalControl;
    private String emotionalCategory;

    private boolean containsEmotionalDimentions;


    /**
     * Constructs an empty ParagraphData object.
     */
    public ParagraphData() {
        sentenceDataItems = new ArrayList<SentenceData>();
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
     * @param sentenceDataToBeAdded The SentenceData to be added.
     */
    public void addSentenceData(SentenceData sentenceDataToBeAdded) {
        sentenceDataItems.add(sentenceDataToBeAdded);
    }


    /**
     * Gets the SentenceData object from the paragraph struct.
     *
     * @param whichOne Which SentenceData must be retrieved.
     *
     * @return The SentenceData of the paragraph.
     */
    public SentenceData getSentenceData(int whichOne) {
        return sentenceDataItems.get(whichOne);
    }


    /**
     * Retrieves the number of sentences.
     *
     * @return The number of sentences.
     */
    public int getNumberOfSentences() {
        return sentenceDataItems.size();
    }


    /**
     * Marks the paragraph as emotional container.
     */
    public void setEmotionalContent() {
        emotionalContent = true;
    }


    /**
     * Retrieves the emotional content of the paragraph.
     *
     * @return The emotional content of the paragraph.
     */
    public boolean hasEmotionalContent() {
        return emotionalContent;
    }


    /**
     * Sets the valence of the emotion of this paragraph.
     *
     * @param inputValence The valence of the emotion of this paragraph.
     */
    public void setEmotionalValence(float inputValence) {
        emotionalValence = inputValence;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the valence of the emotion of this paragraph.
     *
     * @return The valence of the emotion of this paragraph.
     */
    public float getEmotionalValence() {
        return emotionalValence;
    }


    /**
     * Sets the activation of the emotion of this paragraph.
     *
     * @param inputActivation The activation of the emotion of this paragraph.
     */
    public void setEmotionalActivation(float inputActivation) {
        emotionalActivation = inputActivation;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the activation of the emotion of this paragraph.
     *
     * @return The activation of the emotion of this paragraph.
     */
    public float getEmotionalActivation() {
        return emotionalActivation;
    }


    /**
     * Sets the control of the emotion of this paragraph.
     *
     * @param inputControl The control of the emotion of this paragraph.
     */
    public void setEmotionalControl(float inputControl) {
        emotionalControl = inputControl;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the control of the emotion of this paragraph.
     *
     * @return The control of the emotion of this paragraph.
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
     * Sets the category of the emotion of this paragraph.
     *
     * @param inputCategory The category of the emotion of this paragraph.
     */
    public void setEmotionalCategory(String inputCategory) {
        emotionalCategory = inputCategory;
    }


    /**
     * Retrieves the category of the emotion of this paragraph.
     *
     * @return The category of the emotion of this paragraph.
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

