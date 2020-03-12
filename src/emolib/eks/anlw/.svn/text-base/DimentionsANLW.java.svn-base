/*
 * File    : DimentionsANLW.java
 * Created : 27-Nov-2008
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

package emolib.eks.anlw;

/**
 * The <i>DimentionsANLW</i> class is a construct that holds the
 * emotional dimensions of a word stem found in the ANLW database.
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class DimentionsANLW {

    private String stemKey;
    private float valence;
    private float activation;
    private float control;
    private String category;


    /**
     * Constructs an empty DimentionsANLW object.
     */
    public DimentionsANLW() {
        stemKey = "";
        valence = -1;
        activation = -1;
        control = -1;
        category = "";
    }


    /**
     * Constructs and fills a DimentionsANLW object.
     *
     * @param inputValence The valence of the word stem.
     * @param inputActivation The activation of the word stem.
     * @param inputControl The control of the word stem.
     * @param inputCategory The class of word.
     */
    public DimentionsANLW (String inputStemKey, float inputValence, float inputActivation,
    float inputControl, String inputCategory) {
        stemKey = inputStemKey;
        valence = inputValence;
        activation = inputActivation;
        control = inputControl;
        category = inputCategory;
    }


    /**
     * Sets the stem of the object.
     *
     * @param inputStemKey The stem to be set.
     */
    public void setStemKey(String inputStemKey) {
        stemKey = inputStemKey;
    }


    /**
     * Gets the stem of the object.
     *
     * @return The stem of the object.
     */
    public String getStemKey() {
        return stemKey;
    }


    /**
     * Sets the valence of the object.
     *
     * @param inputValence The valence to be set.
     */
    public void setValence(float inputValence) {
        valence = inputValence;
    }


    /**
     * Gets the valence of the object.
     *
     * @return The valence of the object.
     */
    public float getValence() {
        return valence;
    }


    /**
     * Sets the activation of the object.
     *
     * @param inputActivation The activation to be set.
     */
    public void setActivation(float inputActivation) {
        activation = inputActivation;
    }


    /**
     * Gets the activation of the object.
     *
     * @return The activation of the object.
     */
    public float getActivation() {
        return activation;
    }


    /**
     * Sets the control of the object.
     *
     * @param inputControl The control to be set.
     */
    public void setControl(float inputControl) {
        control = inputControl;
    }


    /**
     * Gets the control of the object.
     *
     * @return The control of the object.
     */
    public float getControl() {
        return control;
    }


    /**
     * Sets the word-class of the object.
     *
     * @param wordClassToBeSet The word-class to be set.
     */
    public void setCategory(String wordClassToBeSet) {
        category = wordClassToBeSet;
    }


    /**
     * Gets the word-class of the object.
     *
     * @return The word-class of the object.
     */
    public String getCategory() {
        return category;
    }

}

