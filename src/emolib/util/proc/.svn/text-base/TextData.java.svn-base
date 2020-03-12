/*
 * File    : TextData.java
 * Created : 11-Nov-2008
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
 * The <i>TextData</i> class is a  Data object
 * that holds information at WordData-level.
 * This is the actual construct that should transit through the
 * text processing chain described by the AffectiveTagger.
 * <p>
 * A TextData object holds the information of a variable amount of
 * WordData objects, thus it could be stated that it holds the
 * information of a sentence, a paragraph... a text in general.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class TextData implements Data, Cloneable {

    // The text data may be retrieved differently.
    // The whole bunch of words.
    private ArrayList<WordData> textWords;
    // The beautiful paragraph-sentence-words structure.
    private ArrayList<ParagraphData> paragraphDataItems;

    private boolean emotionalContent;
    private float emotionalValence;
    private float emotionalActivation;
    private float emotionalControl;
    private String emotionalCategory;

    private boolean containsEmotionalDimentions;


    /**
     * Constructs an empty TextData object with a default
     * initial capacity for 10 WordData objects.
     */
    public TextData() {
        textWords = new ArrayList<WordData>();
        paragraphDataItems = new ArrayList<ParagraphData>();
        containsEmotionalDimentions = false;
        emotionalContent = false;
        emotionalValence = -1;
        emotionalActivation = -1;
        emotionalControl = -1;
    }


    /**
     * Constructs an empty TextData object with an
     * initial capacity for the specified number of WordData objects.
     *
     * @param initialCapacity Initial capacity for the construct.
     */
    public TextData(int initialCapacity) {
        textWords = new ArrayList<WordData>(initialCapacity);
        paragraphDataItems = new ArrayList<ParagraphData>();
        containsEmotionalDimentions = false;
        emotionalContent = false;
        emotionalValence = -1;
        emotionalActivation = -1;
        emotionalControl = -1;
    }


    /**
     * Puts a WordData into the TextData object at the end
     * of the structure.
     *
     * @param inputWordData The WordData to be inserted.
     */
    public void putWordData(WordData inputWordData) {
        boolean op = textWords.add(inputWordData);
        if (!op) {
            System.out.println("ERROR adding a word!\n");
            System.exit(1);
        }
    }

    /**
     * Retrieves the textWords from the TextData object.
     *
     * @return The textWords of the TextData object.
     */
    public ArrayList getText() {
        return textWords;
    }


    /**
     * Retrieves the size of the structure (the number of words).
     *
     * @return The size of the structure.
     */
    public int getNumberOfWords() {
        return textWords.size();
    }


    /**
     * Retrieves the number of paragraphs.
     *
     * @return The number of paragraphs.
     */
    public int getNumberOfParagraphs() {
        return paragraphDataItems.size();
    }


    /**
     * Retrieves all the words as an array of strings.
     *
     * @return An array of strings containing all the words.
     */
    public String[] getWordsAsArrayOfStrings() {
        String[] arrayOfStringsToReturn = new String[textWords.size()];
        for (int counter = 0; counter < textWords.size(); counter++) {
            arrayOfStringsToReturn[counter] = textWords.get(counter).getWord();
        }
        return arrayOfStringsToReturn;
    }


    /**
     * Retrieves the specified WordData object.
     *
     * @param index The index of the WordData object to be retrieved.
     *
     * @return The WordData object in question.
     */
    public WordData getWordData(int index) {
        return textWords.get(index);
    }


    /**
     * Replaces the WordData object in the specified index with the
     * new given WordData object.
     *
     * @param index The index of the WordData to be replaced.
     * @param newWordDataObject The new WordData object to be inserted.
     */
    public void setWordData(int index, WordData newWordDataObject) {
        textWords.set(index, newWordDataObject);
    }


    /**
     * Adds a ParagraphData object into the text struct.
     *
     * @param paragraphDataToBeAdded The ParagraphData to be added.
     */
    public void addParagraphData(ParagraphData paragraphDataToBeAdded) {
        paragraphDataItems.add(paragraphDataToBeAdded);
    }


    /**
     * Gets the ParagraphData object from the text struct.
     *
     * @param whichOne Which ParagraphData must be retrieved.
     *
     * @return The ParagraphData of the text.
     */
    public ParagraphData getParagraphData(int whichOne) {
        return paragraphDataItems.get(whichOne);
    }


    /**
     * Marks the document as emotional container.
     */
    public void setEmotionalContent() {
        emotionalContent = true;
    }


    /**
     * Retrieves the emotional content of the document.
     *
     * @return The emotional content of the document.
     */
    public boolean hasEmotionalContent() {
        return emotionalContent;
    }


    /**
     * Sets the valence of the emotion of this document.
     *
     * @param inputValence The valence of the emotion of this document.
     */
    public void setEmotionalValence(float inputValence) {
        emotionalValence = inputValence;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the valence of the emotion of this document.
     *
     * @return The valence of the emotion of this document.
     */
    public float getEmotionalValence() {
        return emotionalValence;
    }


    /**
     * Sets the activation of the emotion of this document.
     *
     * @param inputActivation The activation of the emotion of this document.
     */
    public void setEmotionalActivation(float inputActivation) {
        emotionalActivation = inputActivation;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the activation of the emotion of this document.
     *
     * @return The activation of the emotion of this document.
     */
    public float getEmotionalActivation() {
        return emotionalActivation;
    }


    /**
     * Sets the control of the emotion of this document.
     *
     * @param inputControl The control of the emotion of this document.
     */
    public void setEmotionalControl(float inputControl) {
        emotionalControl = inputControl;
        containsEmotionalDimentions = true;
    }


    /**
     * Retrieves the control of the emotion of this document.
     *
     * @return The control of the emotion of this document.
     */
    public float getEmotionalControl() {
        return emotionalControl;
    }


    /**
     * Determines if this TextData object has emotional dimentions.
     *
     * @return True if this TextData object has emotional dimentions.
     */
    public boolean containsEmotionalDimentions() {
        return containsEmotionalDimentions;
    }


    /**
     * Sets the category of the emotion of this document.
     *
     * @param inputCategory The category of the emotion of this document.
     */
    public void setEmotionalCategory(String inputCategory) {
        emotionalCategory = inputCategory;
    }


    /**
     * Retrieves the category of the emotion of this document.
     *
     * @return The category of the emotion of this document.
     */
    public String getEmotionalCategory() {
        return emotionalCategory;
    }


    /**
     * Returns a clone of this Data object.
     *
     * @return a clone of this data object
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

