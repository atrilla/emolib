/*
 * File    : FeatureBox.java
 * Created : 09-Jul-2009
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

package emolib.classifier;

import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>FeatureBox</i> class defines the data container that must be used
 * to interact with the classifiers.
 *
 * <p>
 * This container just keeps the features that are relevant for
 * emotional prediction, along with the textual elements used to
 * produce such features. Since the many classifiers deal with different
 * kinds of features and the training corpora used to extract the knowledge
 * provide complementary data, this class is useful to deal with these
 * situations and enables the classifiers to behave in a standardised
 * manner.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class FeatureBox {

    private boolean hasEmotionalDimensions;
    private int numberOfEmotionalDimensions;
    private float valence;
    private float activation;
    private float control;
    private String text;
    private String theWords;
    private String thePOSTags;
    private String theStems;
    private String theSynonyms;
    private String theStemmedSynonyms;
    private boolean theNegation;
    private boolean hasSynonyms;


    /**
     * Main constructor.
     */
    public FeatureBox() {
        hasEmotionalDimensions = false;
        numberOfEmotionalDimensions = -1;
        text = "";
        theWords = "";
        thePOSTags = "";
        theStems = "";
        theSynonyms = "";
        theStemmedSynonyms = "";
        theNegation = false;
        hasSynonyms = false;
    }


    /**
     * Constructor with text.
     */
    public FeatureBox(String givenText) {
        hasEmotionalDimensions = false;
        numberOfEmotionalDimensions = -1;
        text = givenText;
        theWords = "";
        thePOSTags = "";
        theStems = "";
        theSynonyms = "";
        theStemmedSynonyms = "";
        theNegation = false;
        hasSynonyms = false;
    }


    /**
     * Method to input text.
     * This text should separate words and punctuation marks with spaces.
     *
     * @param input The text to input.
     */
    public void setText(String input) {
        text = input;
    }


    /**
     * Function to retrieve the text.
     *
     * @return The text of this feature box.
     */
    public String getText() {
        return text;
    }


    /**
     * Method to input words.
     * This text should separate words and punctuation marks with spaces.
     * This method is in concordance with the POS tags, stems, etc., but not
     * with the text.
     *
     * @param words The words to input.
     */
    public void setWords(String words) {
        theWords = words;
    }


    /**
     * Function to retrieve the words.
     *
     * @return The words of this feature box.
     */
    public String getWords() {
        return theWords;
    }


    /**
     * Method to input the POS tags.
     *
     * @param tags The POS tags to input.
     */
    public void setPOSTags(String tags) {
        thePOSTags = tags;
    }


    /**
     * Function to retrieve the POS tags.
     *
     * @return The POS tags of this feature box.
     */
    public String getPOSTags() {
        return thePOSTags;
    }


    /**
     * Method to input the stems of the words.
     *
     * @param stems The stems to input.
     */
    public void setStems(String stems) {
        theStems = stems;
    }


    /**
     * Function to retrieve the stems of the words.
     *
     * @return The stems of this feature box.
     */
    public String getStems() {
        return theStems;
    }


    /**
     * Method to input the synonyms of the words (nouns, actually).
     *
     * @param syns The synonyms to input.
     */
    public void setSynonyms(String syns) {
        theSynonyms = syns;
        hasSynonyms = true;
    }


    /**
     * Function to check if this FeatureBox contains any synonyms.
     *
     * @return True if it contains any synonyms.
     */
    public boolean containsSynonyms() {
        return hasSynonyms;
    }


    /**
     * Function to retrieve the synonyms of the words.
     *
     * @return The synonyms of this feature box.
     */
    public String getSynonyms() {
        return theSynonyms;
    }


    /**
     * Method to input the stemmed synonyms of the words (nouns, actually).
     *
     * @param ssyns The stemmed synonyms to input.
     */
    public void setStemmedSynonyms(String ssyns) {
        theStemmedSynonyms = ssyns;
    }


    /**
     * Function to retrieve the stemmed synonyms of the words.
     *
     * @return The stemmed synonyms of this feature box.
     */
    public String getStemmedSynonyms() {
        return theStemmedSynonyms;
    }


    /**
     * Method to input the negation feature of the words above..
     *
     * @param neg The negation to input.
     */
    public void setNegation(boolean neg) {
        theNegation = neg;
    }


    /**
     * Function to retrieve the negation.
     *
     * @return The negation aspect of this feature box.
     */
    public boolean getNegation() {
        return theNegation;
    }


    /**
     * Method to set if this FeatureBox contains emotional dimensions.
     *
     * @return True if this FeatureBox contains emotional dimensions.
     */
    public boolean containsEmotionalDimensions() {
        return hasEmotionalDimensions;
    }


    /**
     * Method to set the number of emotional dimensions.
     * This method should be called before introducing any dimension into this container.
     * Otherwise the system will complain by issuing a warning message.
     *
     * @param numberOfDimensions The number of dimensions.
     */
    public void setNumberOfEmotionalDimensions(int numberOfDimensions) {
        numberOfEmotionalDimensions = numberOfDimensions;
    }


    /**
     * Function to retrieve the number of emotional dimensions that this
     * FeatureBox object contains.
     *
     * @return The number of emotional dimensions.
     */
    public int getNumberOfEmotionalDimensions() {
        return numberOfEmotionalDimensions;
    }


    /**
     * Method to introduce the valence emotional dimension into this container.
     * The valence stands the first emotional dimension.
     *
     * @param valence The valence value.
     */
    public void setValence(float valence) {
        if (numberOfEmotionalDimensions != Integer.parseInt("-1")) {
            this.valence = valence;
            hasEmotionalDimensions = true;
        } else {
            System.out.println("FeatureBox: the emotional dimensions have not yet been set!");
        }
    }


    /**
     * Function to retrieve the valence value of this FeatureBox.
     *
     * @return The valence value.
     */
    public float getValence() {
        return valence;
    }


    /**
     * Method to introduce the activation emotional dimension into this container.
     * The activation stands the second emotional dimension.
     *
     * @param activation The activation value.
     */
    public void setActivation(float activation) {
        if (numberOfEmotionalDimensions != Integer.parseInt("-1")) {
            this.activation = activation;
        } else {
            System.out.println("FeatureBox: the emotional dimensions have not yet been set!");
        }
    }


    /**
     * Function to retrieve the activation value of this FeatureBox.
     *
     * @return The activation value.
     */
    public float getActivation() {
        return activation;
    }


    /**
     * Method to introduce the control emotional dimension into this container.
     * The control stands the third emotional dimension.
     *
     * @param control The control value.
     */
    public void setControl(float control) {
        if (numberOfEmotionalDimensions != Integer.parseInt("-1")) {
            this.control = control;
        } else {
            System.out.println("FeatureBox: the emotional dimensions have not yet been set!");
        }
    }


    /**
     * Function to retrieve the control value of this FeatureBox.
     *
     * @return The control value.
     */
    public float getControl() {
        return control;
    }

}

