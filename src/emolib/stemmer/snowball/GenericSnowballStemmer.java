/*
 * File    : GenericSnowballStemmer.java
 * Created : 20-Nov-2008
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

package emolib.stemmer.snowball;

import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;

import emolib.stemmer.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>GenericSnowballStemmer</i> class performs the
 * stemming process using the Snowball library.
 *
 * <p>
 * This class accepts two parameters through the Configuration Manager:
 * the "language", which determines the algorithm of the stemming process
 * thus stating the language of use,
 * and the "iterations", which determines the number of stemming
 * iterations that must be performed on the incoming word
 * removing suffices one at a time,
 * starting at the end of the word and working towards the beginning.
 * </p>
 * <p>
 * Only the words that may have an affective content are stemmed.
 * This assumption responds to the indexing goal that stemming
 * pursues in Information Retrieval (IR).
 * Read more about this in the article
 * <a href="http://snowball.tartarus.org/texts/introduction.html">Snowball: A language for stemming algorithms</a>.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class GenericSnowballStemmer extends Stemmer {

    /**
     * The name of the property indicating the language of
     * this Stemmer.
     */
    public final static String PROP_LANGUAGE = "language";
    public final static String PROP_ITERATIONS = "iterations";


    private String stemmerLanguage;
    private SnowballStemmer stemmer;
    private int stemmingIterations;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_LANGUAGE, PropertyType.STRING);
        registry.register(PROP_ITERATIONS, PropertyType.INT);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        stemmerLanguage = ps.getString(PROP_LANGUAGE, "spanish");
        stemmingIterations = ps.getInt(PROP_ITERATIONS, 1);
    }


    /**
     * Method to initialize the GenericSnowballStemmer.
     */
    public void initialize() {
        try {
            Class stemClass = Class.forName("emolib.stemmer.snowball.ext." + stemmerLanguage + "Stemmer");
            stemmer = (SnowballStemmer)stemClass.newInstance();
        } catch (Throwable e) {
            System.out.println("ERROR loading the GenericSnowballStemmer!");
            e.printStackTrace();
        }
    }


    /**
     * Main constructor of the GenericSnowballStemmer.
     */
    public GenericSnowballStemmer() {
    }


    /**
     * Method to perform the stemming process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyStemming(TextData inputTextDataObject) {
        WordData tempWordData;
        int counter;
        String decapitalize;
        for (int numberOfWords = 0; numberOfWords < inputTextDataObject.getNumberOfWords(); numberOfWords++) {
            tempWordData = inputTextDataObject.getWordData(numberOfWords);
            if (tempWordData.hasEmotionalContent()) {
                decapitalize = tempWordData.getWord();
                stemmer.setCurrent(decapitalize.toLowerCase());
                for (counter = 0; counter < stemmingIterations; counter++) {
                    stemmer.stem();
                }
                tempWordData.setWordStem(stemmer.getCurrent());
                if (tempWordData.containsSynonyms()) {
                    ArrayList synonymsList = tempWordData.getSense();
                    ArrayList senseStems = new ArrayList(synonymsList.size());
                    for (int synonymCounter = 0; synonymCounter < synonymsList.size(); synonymCounter++) {
                        decapitalize = (String)synonymsList.get(synonymCounter);
                        stemmer.setCurrent(decapitalize.toLowerCase());
                        for (counter = 0; counter < stemmingIterations; counter++) {
                            stemmer.stem();
                        }
                        senseStems.add(stemmer.getCurrent());
                    }
                    tempWordData.setStemmedWordSense(senseStems);
                }
            } else {
                // If the word in question does not contain affective content, it is
                // maintained as is in lowercase.
                tempWordData.setWordStem(tempWordData.getWord().toLowerCase());
            }
            inputTextDataObject.setWordData(numberOfWords, tempWordData);
        }
    }

}

