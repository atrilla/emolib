/*
 * File    : EmotionalKeywordSpotterANLW.java
 * Created : 25-Nov-2008
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

import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;

import emolib.eks.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>EmotionalKeywordSpotterANLW</i> class performs the
 * Emotional Keyword Spotting (EKS) process using the Affective
 * Norms Language Words (ANLW) dictionary.
 *
 * <p>
 * This class accepts a configuration parameter that indicates the
 * absolute path of the location of an ANLW dictionary in the file system
 * of the user's computer. Since these dictionaries have the same structure,
 * the term Language has been introduced to wrap English and Spanish.
 * </p>
 * <p>
 * ANLW contains 1034 stemmed words evaluated for the three emotional dimensions:
 * valence, activation and control. These evaluations range from 0 to 10
 * in this work. Also, the words are grammatically labelled in order to
 * disambiguate the cases where conflation occurs.
 * In this case, the weight configuration parameter determines the amount
 * of contribution that, despite the conflation, is allowed in the
 * affect computation.
 * </p>
 * <p>
 * The dictionary is first loaded into the system to then be used to map the
 * incoming stemmed words to the word stems that contains, thus extracting
 * the emotional dimensions and enabling the rest of the system to compute the
 * general affect being expressed.
 * If the stemmed words matched correspond to synonyms of the original word,
 * then a weighting parameter is allowed through the configuration file.
 * </p>
 * <p>
 * If for the word in question, despite having emotional content, no appropriate
 * emotional dimensions are found, then a default neutral value is assigned
 * to each of the dimensions. This value corresponds to a parameter available through
 * the configuration file.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class EmotionalKeywordSpotterANLW extends EmotionalKeywordSpotter {

    /**
     * The name of the property indicating the location of the
     * ANLW dictionary.
     */
    public final static String PROP_DICTIONARY = "dictionary";
    public final static String PROP_CONFLATION_WEIGHT = "conflation_weight";
    public final static String PROP_SYNONYMS_WEIGHT = "synonyms_weight";
    public final static String PROP_WORD_NOT_FOUND = "word_not_found";


    private String dictionaryLocation;
    private float conflationWeight;
    private float synonymsWeight;
    private float wordNotFound;
    private DictionaryANLW theDictionary;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_DICTIONARY, PropertyType.STRING);
        registry.register(PROP_CONFLATION_WEIGHT, PropertyType.FLOAT);
        registry.register(PROP_SYNONYMS_WEIGHT, PropertyType.FLOAT);
        registry.register(PROP_WORD_NOT_FOUND, PropertyType.FLOAT);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        dictionaryLocation = ps.getString(PROP_DICTIONARY, "nullpath");
        conflationWeight = ps.getFloat(PROP_CONFLATION_WEIGHT, Float.parseFloat("1.0"));
        synonymsWeight = ps.getFloat(PROP_SYNONYMS_WEIGHT, Float.parseFloat("1.0"));
        wordNotFound = ps.getFloat(PROP_WORD_NOT_FOUND, Float.parseFloat("5.75"));
    }


    /**
     * Method to initialize the EmotionalKeywordSpotterANLW.
     */
    public void initialize() {
        if (dictionaryLocation.equals("nullpath")) {
            System.out.println("The ANLW dictionary absolute path required by EmotionalKeywordSpotterANLW " +
                "has not been defined in the configuration file!");
            System.exit(1);
        } else {
            theDictionary = new DictionaryANLW(dictionaryLocation);
        }
    }


    /**
     * Main constructor of the EmotionalKeywordSpotterANLW.
     */
    public EmotionalKeywordSpotterANLW() {
    }


    /**
     * Method to perform the EKS process.
     * If the word stem of the affective dictionary is produced by the same POS as the
     * word in question, the emotional dimentions are direct, otherwise they are
     * weighed due to the conflation effect.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyEKS(TextData inputTextDataObject) {
        WordData tempWordData;
        ArrayList tempList;
        int synonymCounter;
        boolean emotionalDimentionsFound = false;
        float[] dimentionResults = new float[3];
        for (int numberOfWords = 0; numberOfWords < inputTextDataObject.getNumberOfWords(); numberOfWords++) {
            tempWordData = inputTextDataObject.getWordData(numberOfWords);
            if (tempWordData.hasEmotionalContent()) {
                if (theDictionary.containsEmotionalKey(tempWordData.getWordStem())) {
                    if (tempWordData.isNoun()) {
                        if (theDictionary.isProducedByNoun(tempWordData.getWordStem())) {
                            dimentionResults = theDictionary.getDimentionsProducedByNoun(tempWordData.getWordStem());
                        } else {
                            dimentionResults = getDefaultDimentions(tempWordData.getWordStem());
                        }
                        emotionalDimentionsFound = true;
                    } else if (tempWordData.isAdjective()) {
                        if (theDictionary.isProducedByAdjective(tempWordData.getWordStem())) {
                            dimentionResults = theDictionary.
                                getDimentionsProducedByAdjective(tempWordData.getWordStem());
                        } else {
                            dimentionResults = getDefaultDimentions(tempWordData.getWordStem());
                        }
                        emotionalDimentionsFound = true;
                    } else if (tempWordData.isVerb() || tempWordData.isAdverb()) {
                        if (theDictionary.isProducedByAdjective(tempWordData.getWordStem())) {
                            dimentionResults = theDictionary.getDimentionsProducedByVerb(tempWordData.getWordStem());
                        } else {
                            dimentionResults = getDefaultDimentions(tempWordData.getWordStem());
                        }
                        emotionalDimentionsFound = true;
                    }
                } else if (tempWordData.containsStemmedSynonyms()) {
                    tempList = tempWordData.getStemmedWordSense();
                    synonymCounter = 0;
                    // Takes the first synonym with affective content.
                    while ((emotionalDimentionsFound == false) && (synonymCounter < tempList.size())) {
                        if (theDictionary.containsEmotionalKey((String)tempList.get(synonymCounter))) {
                            if (tempWordData.isNoun()) {
                                if (theDictionary.isProducedByNoun((String)tempList.get(synonymCounter))) {
                                    dimentionResults = theDictionary.
                                        getDimentionsProducedByNoun((String)tempList.get(synonymCounter));
                                } else {
                                    dimentionResults = getDefaultSynonymDimentions((String)tempList.
                                        get(synonymCounter));
                                }
                                emotionalDimentionsFound = true;
                            } else if (tempWordData.isAdjective()) {
                                if (theDictionary.isProducedByAdjective((String)tempList.get(synonymCounter))) {
                                    dimentionResults = theDictionary.
                                        getDimentionsProducedByAdjective((String)tempList.get(synonymCounter));
                                } else {
                                    dimentionResults = getDefaultSynonymDimentions((String)tempList.
                                        get(synonymCounter));
                                }
                                emotionalDimentionsFound = true;
                            } else if (tempWordData.isVerb() || tempWordData.isAdverb()) {
                                if (theDictionary.isProducedByVerb((String)tempList.get(synonymCounter))) {
                                    dimentionResults = theDictionary.
                                        getDimentionsProducedByVerb((String)tempList.get(synonymCounter));
                                } else {
                                    dimentionResults = getDefaultSynonymDimentions((String)tempList.
                                        get(synonymCounter));
                                }
                                emotionalDimentionsFound = true;
                            } else {
                                // If the POS tagger fails, do this best effort approach.
                                if (theDictionary.isProducedByNoun((String)tempList.get(synonymCounter))) {
                                    dimentionResults = theDictionary.
                                        getDimentionsProducedByNoun((String)tempList.get(synonymCounter));
                                } else if (theDictionary.isProducedByAdjective((String)tempList.get(synonymCounter))) {
                                    dimentionResults = theDictionary.
                                        getDimentionsProducedByAdjective((String)tempList.get(synonymCounter));
                                } else {
                                    dimentionResults = theDictionary.
                                        getDimentionsProducedByVerb((String)tempList.get(synonymCounter));
                                }
                                emotionalDimentionsFound = true;
                            }
                        }
                        synonymCounter++;
                    }
                } else {
                    /*
                    // No dimentions are found
                    // Leaving the word in question without a score discards it when summing up emotional words
                    dimentionResults[0] = wordNotFound;
                    dimentionResults[1] = wordNotFound;
                    dimentionResults[2] = wordNotFound;
                     */
                }
                if (emotionalDimentionsFound) {
                    tempWordData.setEmotionalValence(dimentionResults[0]);
                    tempWordData.setEmotionalActivation(dimentionResults[1]);
                    tempWordData.setEmotionalControl(dimentionResults[2]);
                    inputTextDataObject.setWordData(numberOfWords, tempWordData);
                }
            }
            emotionalDimentionsFound = false;
        }
    }


    /**
     * Sets the default dimentions, the first ones, despite the conflation.
     * This function biases the statistical results.
     *
     * @param wordStem The stem which emotional dimentions are to be retrieved.
     */
    private float[] getDefaultDimentions(String wordStem) {
        float[] dimentionResults = new float[3];
        dimentionResults = theDictionary.getFirstDimentions(wordStem);
        dimentionResults[0] = dimentionResults[0] * conflationWeight;
        dimentionResults[1] = dimentionResults[1] * conflationWeight;
        dimentionResults[2] = dimentionResults[2] * conflationWeight;
        return dimentionResults;
    }


    /**
     * Sets the default synonym dimentions, the first ones, despite the conflation.
     * This function biases the statistical results.
     *
     * @param wordStem The stem which emotional dimentions are to be retrieved.
     */
    private float[] getDefaultSynonymDimentions(String wordStem) {
        float[] dimentionResults = new float[3];
        dimentionResults = theDictionary.getFirstDimentions(wordStem);
        dimentionResults[0] = dimentionResults[0] * conflationWeight * synonymsWeight;
        dimentionResults[1] = dimentionResults[1] * conflationWeight * synonymsWeight;
        dimentionResults[2] = dimentionResults[2] * conflationWeight * synonymsWeight;
        return dimentionResults;
    }

}

