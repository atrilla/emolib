/*
 * File    : OpenThesWSD.java
 * Created : 24-Dec-2008
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

package emolib.wsd.openthes;

import emolib.wsd.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

import java.io.*;
import java.util.*;

/**
 * The <i>OpenThesWSD</i> class performs the
 * Word Sense Disambiguation (WSD) process using the OpenThesaurus thesaurus.
 *
 * <p>
 * In order to perform this process, the word-sense disambiguator checks for a word if the
 * rest the words in the sentence with a potential emotion (nouns, verbs and adjectives) are in the
 * same synonym set in the thesaurus according to one specific meaning for the word in
 * question. According to the count results for all the different meanings of the words,
 * one sense is accepted, and the related words in
 * the synonym set are taken for further analysis.
 * </p>
 * <p>
 * The OpenThesWSD requires a configuration parameter that indicates the location of the
 * OpenThesaurus in the user's filesystem. If the aimed language is Spanish, this file
 * can be obtained at the <a href="http://openthes-es.berlios.de/">OpenThesaurus-es - Tesauro en espa&ntilde;ol</a>
 * website.
 * </p>
 * <p>
 * There is an optional configuration parameter to analyse the text, thus disambiguating
 * the meanings at sentence or paragraph level. By default, the analysis is performed at
 * the latter level.
 * </p>
 * <p>
 * Two additional configuration parameters enable or disable the computations of
 * verbs and adjectives (nouns are always accounted). This responds to some linguistic
 * particularities, e.g., hypernymy/hyponymy applies to nouns while troponmy
 * applies to verbs.
 * </p>

 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class OpenThesWSD extends WordSenseDisambiguator {

    /**
     * The name of the property indicating the path of
     * the thesaurus.
     */
    public final static String PROP_THESAURUS_PATH = "thesauruspath";

    /**
     * The name of the property indicating the scope of
     * the analysis.
     */
    public final static String PROP_SENTENCE_LEVEL = "sentence_level";
    public final static String PROP_ANALYZE_VERBS = "analyze_verbs";
    public final static String PROP_ANALYZE_ADJECTIVES = "analyze_adjectives";


    private String thesaurusPath;
    private boolean sentenceLevel;
    private boolean analyzeVerbs;
    private boolean analyzeAdjectives;

    private ArrayList thesaurusEntries;
    private Hashtable thesaurusWords;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_THESAURUS_PATH, PropertyType.STRING);
        registry.register(PROP_SENTENCE_LEVEL, PropertyType.BOOLEAN);
        registry.register(PROP_ANALYZE_VERBS, PropertyType.BOOLEAN);
        registry.register(PROP_ANALYZE_ADJECTIVES, PropertyType.BOOLEAN);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        thesaurusPath = ps.getString(PROP_THESAURUS_PATH, "nullpath");
        sentenceLevel = ps.getBoolean(PROP_SENTENCE_LEVEL, false);
        analyzeVerbs = ps.getBoolean(PROP_ANALYZE_VERBS, false);
        analyzeAdjectives = ps.getBoolean(PROP_ANALYZE_ADJECTIVES, false);
    }


    /**
     * Method to initialize the OpenThesWSD.
     */
    public void initialize() {
        if (thesaurusPath.equals("nullpath")) {
            System.out.println("EmoLib: the word-sense disambiguator has no thesaurus defined in the filesystem! " +
                "Please check the configuration file.");
            System.exit(1);
        } else {
            createIndex();
        }
    }


    /**
     * Main constructor of the OpenThesWSD.
     */
    public OpenThesWSD() {
    }


    /**
     * Creates the index for the thesaurus.
     */
    private void createIndex() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(thesaurusPath));
            String[] synonyms;
            int index = 0;
            int count;
            ArrayList refEntries;

            thesaurusWords = new Hashtable();
            thesaurusEntries = new ArrayList();
            String thesaurusLine = reader.readLine();
            while (thesaurusLine != null) {
                if (thesaurusLine.startsWith("#")) {
                } else {
                    synonyms = thesaurusLine.split(";");
                    for (count = 0; count < synonyms.length; count++) {
                        if (synonyms[count].indexOf("(") >= 0) {
                            synonyms[count] = synonyms[count].substring(0, synonyms[count].indexOf("(")).trim();
                        }
                        if (thesaurusWords.containsKey(synonyms[count]) == false) {
                            refEntries = new ArrayList();
                            refEntries.add(index);
                            thesaurusWords.put(synonyms[count], refEntries);
                        } else {
                            refEntries = (ArrayList)thesaurusWords.get(synonyms[count]);
                            refEntries.add(index);
                            thesaurusWords.put(synonyms[count], refEntries);
                        }
                    }
                    thesaurusEntries.add(index, buildList(synonyms));
                    index++;
                }
                thesaurusLine = reader.readLine();
            }
        } catch (Exception e) {
            System.out.println("EmoLib: There's been a problem creating the index file of the thesaurus!");
            e.printStackTrace();
        }
    }


    /**
     * Function to provide list from an array of words.
     *
     * @param theWords The array of words.
     *
     * @return The list object.
     */
    private ArrayList buildList(String[] theWords) {
        ArrayList theList = new ArrayList(theWords.length);
        for (int counter = 0; counter < theWords.length; counter++) {
            theList.add(theWords[counter]);
        }
        return theList;
    }


    /**
     * Method to perform the word-sense disambiguation process.
     * The meaning is taken at sentence-level.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyWSD(TextData inputTextDataObject) {
        if (sentenceLevel) {
            wsdSentence(inputTextDataObject);
        } else {
            wsdParagraph(inputTextDataObject);
        }
    }


    /**
     * Method to perform the WSD process at sentence-level.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    private void wsdSentence(TextData inputTextDataObject) {
        WordData tempWordData;
        ArrayList synsets, otherWordSynsets, wordsInTheSentence;
        int[] senseHits;
        ArrayList sentencesEmotionalContent = extractSentencesEmotionalContent(inputTextDataObject);
        int bestAcceptionIndex, bestHits, wordInTheSentenceCounter, senseCount, otherWordCount;
        int indexWordData = 0;
        Integer tempInteger;
        boolean nextWord;
        for (int sentenceCounter = 0; sentenceCounter < sentencesEmotionalContent.size(); sentenceCounter++) {
            wordsInTheSentence = (ArrayList)sentencesEmotionalContent.get(sentenceCounter);
            for (wordInTheSentenceCounter = 0; wordInTheSentenceCounter < wordsInTheSentence.size();
            wordInTheSentenceCounter++) {
                if (thesaurusWords.containsKey((String)wordsInTheSentence.get(wordInTheSentenceCounter))) {
                    synsets = (ArrayList)thesaurusWords.get((String)wordsInTheSentence.get(wordInTheSentenceCounter));
                    senseHits = new int[synsets.size()];
                    for (senseCount = 0; senseCount < synsets.size(); senseCount++) {
                        senseHits[senseCount] = 0;
                        for (otherWordCount = 0; otherWordCount < wordsInTheSentence.size(); otherWordCount++) {
                            if (wordsInTheSentence.get(otherWordCount).equals(wordsInTheSentence.
                            get(wordInTheSentenceCounter)) == false) {
                                if (thesaurusWords.containsKey((String)wordsInTheSentence.get(otherWordCount))) {
                                    otherWordSynsets =
                                        (ArrayList)thesaurusWords.get((String)wordsInTheSentence.get(otherWordCount));
                                    if (otherWordSynsets.contains(synsets.get(senseCount))) {
                                        senseHits[senseCount]++;
                                    }
                                }
                            }
                        }
                    }
                    // Determination of the best sense.
                    bestAcceptionIndex = 0;
                    bestHits = senseHits[bestAcceptionIndex];
                    for (int bestCounter = 1; bestCounter < senseHits.length; bestCounter++) {
                        if (senseHits[bestCounter] > bestHits) {
                            bestAcceptionIndex = bestCounter;
                            bestHits = senseHits[bestAcceptionIndex];
                        }
                    }
                    // Sets the correct synonyms to the appropriate word in the text.
                    nextWord = false;
                    while (nextWord == false) {
                        if (indexWordData < inputTextDataObject.getNumberOfWords()) {
                            tempWordData = inputTextDataObject.getWordData(indexWordData);
                            if (tempWordData.getWord().equals(wordsInTheSentence.get(wordInTheSentenceCounter))) {
                                if (tempWordData.isNoun() || (tempWordData.isVerb() && analyzeVerbs) ||
                                (tempWordData.isAdjective() && analyzeAdjectives)) {
                                    tempInteger = (Integer)synsets.get(bestAcceptionIndex);
                                    tempWordData.setSense((ArrayList)thesaurusEntries.get(tempInteger.intValue()));
                                    inputTextDataObject.setWordData(indexWordData, tempWordData);
                                }
                                nextWord = true;
                            }
                            indexWordData++;
                        } else {
                            nextWord = true;
                        }
                    }
                } else {
                    // What happens if the word has no entry in the thesaurus?
                }
            }
        }
    }


    /**
     * Method to perform the WSD process at paragraph-level (the default configuration).
     *
     * @param inputTextDataObject The TextData object to process.
     */
    private void wsdParagraph(TextData inputTextDataObject) {
        WordData tempWordData, otherWordData;
        ArrayList synsets, otherWordSynsets, wordsInTheSentence;
        int[] senseHits;
        int bestAcceptionIndex, bestHits, wordInTheSentenceCounter, senseCount, otherWordCount;
        Integer tempInteger;
        for (int numberOfWords = 0; numberOfWords < inputTextDataObject.getNumberOfWords(); numberOfWords++) {
            tempWordData = inputTextDataObject.getWordData(numberOfWords);
            if (tempWordData.isNoun() || (tempWordData.isVerb() && analyzeVerbs) ||
            (tempWordData.isAdjective() && analyzeAdjectives)) {
                if (thesaurusWords.containsKey(tempWordData.getWord())) {
                    synsets = (ArrayList)thesaurusWords.get(tempWordData.getWord());
                    senseHits = new int[synsets.size()];
                    for (senseCount = 0; senseCount < synsets.size(); senseCount++) {
                        senseHits[senseCount] = 0;
                        for (otherWordCount = 0; otherWordCount < inputTextDataObject.getNumberOfWords();
                        otherWordCount++) {
                            if (otherWordCount != numberOfWords) {
                                otherWordData = inputTextDataObject.getWordData(otherWordCount);
                                if (thesaurusWords.containsKey(otherWordData.getWord())) {
                                    otherWordSynsets = (ArrayList)thesaurusWords.get(otherWordData.getWord());
                                    if (otherWordSynsets.contains(synsets.get(senseCount))) {
                                        senseHits[senseCount]++;
                                    }
                                }
                            }
                        }
                    }
                    // Determination of the best sense.
                    bestAcceptionIndex = 0;
                    bestHits = senseHits[bestAcceptionIndex];
                    for (int bestCounter = 1; bestCounter < senseHits.length; bestCounter++) {
                        if (senseHits[bestCounter] > bestHits) {
                            bestAcceptionIndex = bestCounter;
                            bestHits = senseHits[bestAcceptionIndex];
                        }
                    }
                    // Sets the correct synonyms to the appropriate word in the text.
                    tempInteger = (Integer)synsets.get(bestAcceptionIndex);
                    tempWordData.setSense((ArrayList)thesaurusEntries.get(tempInteger.intValue()));
                    inputTextDataObject.setWordData(numberOfWords, tempWordData);
                } else {
                    // What happens if the word has no entry in the thesaurus?
                }
            }
        }
    }


    /**
     * Function to extract the words with emotional content from
     * the sentences in order to perform the WSD at sentence-level.
     *
     * @param inputTextData The text to analyze.
     *
     * @return The different sentences that build the body of the text.
     */
    private ArrayList extractSentencesEmotionalContent(TextData inputTextData) {
        WordData tempWordData;
        ArrayList theSentences = new ArrayList();
        ArrayList tempWords = new ArrayList();
        int sentenceNumber = 1;
        for (int numberOfWords = 0; numberOfWords < inputTextData.getNumberOfWords(); numberOfWords++) {
            tempWordData = inputTextData.getWordData(numberOfWords);
            if (tempWordData.hasEmotionalContent()) {
                if (tempWordData.getNumberOfSentence() != sentenceNumber) {
                    theSentences.add(tempWords);
                    tempWords = new ArrayList();
                    tempWords.add(tempWordData.getWord());
                    sentenceNumber = tempWordData.getNumberOfSentence();
                } else {
                    tempWords.add(tempWordData.getWord());
                }
            }
        }
        theSentences.add(tempWords);
        return theSentences;
    }

}

