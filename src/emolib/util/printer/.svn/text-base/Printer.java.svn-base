/*
 * File    : Printer.java
 * Created : 09-Jan-2009
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

package emolib.util.printer;

import java.util.ArrayList;

import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>Printer</i> class provides a device to output the content of the data
 * flowing in the processing pipeline.
 *
 * <p>
 * The Printer can be configured through the XML configuration file to output
 * various types of information. Its inclusion is not restricted to any
 * part of the processing chain. The idea is that the Printer enables the
 * check-out procedure to be carried out easily and securely thus being a
 * trustful monitor for the application.
 * </p>
 * <p>
 * The different configuration parameters are listed as follows:
 * <ul>
 *     <li>
 *         <b>echo_emotional_words</b>: prints the emotional words that the pipeline contains.
 *     </li>
 *     <li>
 *         <b>echo_modifiers</b>: prints the modifiers and their values.
 *     </li>
 *     <li>
 *         <b>echo_nouns</b>: prints the nouns in the text.
 *     </li>
 *     <li>
 *         <b>echo_number_of_words</b>: prints the number of words pertaining to a paragraph
 *         that are introduced into the system.
 *     </li>
 *     <li>
 *         <b>echo_stems</b>: prints the stem of the emotional words in the text.
 *     </li>
 *     <li>
 *         <b>echo_stemmed_synonyms</b>: prints the stemmed synonyms of the emotional
 *         words in the text.
 *     </li>
 *     <li>
 *         <b>echo_synonyms</b>: prints the synonyms of the words with emotional content.
 *     </li>
 *     <li>
 *         <b>echo_word_class</b>: prints the class of word of each word in the text.
 *     </li>
 *     <li>
 *         <b>echo_word_pos</b>: prints the POS tag of each word in the text.
 *     </li>
 * </ul>
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class Printer extends TextDataProcessor {

    public final static String PROP_SYNONYMS = "echo_synonyms";
    public final static String PROP_NUMBER_WORDS = "echo_number_of_words";
    public final static String PROP_MODIFIERS = "echo_modifiers";
    public final static String PROP_EMOTIONAL_WORDS = "echo_emotional_words";
    public final static String PROP_STEMS = "echo_stems";
    public final static String PROP_STEMMED_SYNONYMS = "echo_stemmed_synonyms";
    public final static String PROP_NOUNS = "echo_nouns";
    public final static String PROP_WORD_CLASS = "echo_word_class";
    public final static String PROP_WORD_POS = "echo_word_pos";


    private boolean echoSynonyms;
    private boolean echoNumberWords;
    private boolean echoModifiers;
    private boolean echoEmotionalWords;
    private boolean echoStems;
    private boolean echoStemmedSynonyms;
    private boolean echoNouns;
    private boolean echoWordClass;
    private boolean echoWordPOS;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_SYNONYMS, PropertyType.BOOLEAN);
        registry.register(PROP_NUMBER_WORDS, PropertyType.BOOLEAN);
        registry.register(PROP_MODIFIERS, PropertyType.BOOLEAN);
        registry.register(PROP_EMOTIONAL_WORDS, PropertyType.BOOLEAN);
        registry.register(PROP_STEMS, PropertyType.BOOLEAN);
        registry.register(PROP_STEMMED_SYNONYMS, PropertyType.BOOLEAN);
        registry.register(PROP_NOUNS, PropertyType.BOOLEAN);
        registry.register(PROP_WORD_CLASS, PropertyType.BOOLEAN);
        registry.register(PROP_WORD_POS, PropertyType.BOOLEAN);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        echoSynonyms = ps.getBoolean(PROP_SYNONYMS, false);
        echoNumberWords = ps.getBoolean(PROP_NUMBER_WORDS, false);
        echoModifiers = ps.getBoolean(PROP_MODIFIERS, false);
        echoEmotionalWords = ps.getBoolean(PROP_EMOTIONAL_WORDS, false);
        echoStems = ps.getBoolean(PROP_STEMS, false);
        echoStemmedSynonyms = ps.getBoolean(PROP_STEMMED_SYNONYMS, false);
        echoNouns = ps.getBoolean(PROP_NOUNS, false);
        echoWordClass = ps.getBoolean(PROP_WORD_CLASS, false);
        echoWordPOS = ps.getBoolean(PROP_WORD_POS, false);
    }


    /**
     * Obtains the TextData from the previous module, processes it and makes it
     * available to the rest of the text processing chain.
     *
     * @return The next available Data object, returns null if no Data object
     *         is available.
     *
     * @throws DataProcessingException
     *                 If there is a processing error.
     */
    public Data getData() throws DataProcessingException {
        Data input = getPredecessor().getData();
        if (input != null) {
            if (input instanceof TextData) {
                applyPrinting(((TextData)input));
            }
        }
        return input;
    }


    /**
     * Method to initialize the Printer.
     */
    public void initialize() {
    }

    /** * Main constructor of the Printer.
     */
    public Printer() {
    }


    /**
     * Method to perform the printing process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyPrinting(TextData inputTextDataObject) {
        WordData tempWordData;
        // Auxiliary variables.
        ArrayList theSynonyms;
        int counter;

        if (echoNumberWords) {
            System.out.println("Number of words of this paragraph: " + inputTextDataObject.getNumberOfWords());
        }

        for (int numberOfWords = 0; numberOfWords < inputTextDataObject.getNumberOfWords(); numberOfWords++) {
            tempWordData = inputTextDataObject.getWordData(numberOfWords);
            if (echoEmotionalWords) {
                if (tempWordData.hasEmotionalContent()) {
                    System.out.println("Emotional word: " + tempWordData.getWord());
                    System.out.println("\tValence: " + tempWordData.getEmotionalValence());
                    System.out.println("\tActivation: " + tempWordData.getEmotionalActivation());
                    System.out.println("\tControl: " + tempWordData.getEmotionalControl());
                }
            }
            if (echoModifiers) {
                if (tempWordData.isModifier()) {
                    System.out.println("Modifier word: " + tempWordData.getWord());
                    System.out.println("\tValue: " + tempWordData.getModifierValue());
                }
            }
            if (echoSynonyms) {
                theSynonyms = new ArrayList();
                if (tempWordData.containsSynonyms()) {
                    System.out.println("Word with synonyms: " + tempWordData.getWord());
                    theSynonyms = tempWordData.getSense();
                    for (counter = 0; counter < theSynonyms.size(); counter++) {
                        System.out.println("\t" + (String)theSynonyms.get(counter));
                    }
                }
            }
            if (echoStems) {
                if (tempWordData.hasEmotionalContent()) {
                    System.out.println("Word with stem: " + tempWordData.getWord());
                    System.out.println("\tStem: " + tempWordData.getWordStem());
                }
            }
            if (echoStemmedSynonyms) {
                if (tempWordData.containsStemmedSynonyms()) {
                    System.out.println("Word with stemmed synonyms: " + tempWordData.getWord());
                    ArrayList stemmedSynonymsList = tempWordData.getStemmedWordSense();
                    for (counter = 0; counter < stemmedSynonymsList.size(); counter++) {
                        System.out.println("\tStemmed synonym: " + (String)stemmedSynonymsList.get(counter));
                    }
                }
            }
            if (echoNouns) {
                if (tempWordData.isNoun()) {
                    System.out.println("Noun: " + tempWordData.getWord());
                }
            }
            if (echoWordClass) {
                System.out.println("Word: " + tempWordData.getWord());
                System.out.println("\tClass: " + tempWordData.getWordClass());
            }
            if (echoWordPOS) {
                if (tempWordData.containsWordPOS()) {
                    System.out.println("Word with POS tag: " + tempWordData.getWord());
                    System.out.println("\tPOS: " + tempWordData.getWordPOS());
                }
            }
        }
    }

}

