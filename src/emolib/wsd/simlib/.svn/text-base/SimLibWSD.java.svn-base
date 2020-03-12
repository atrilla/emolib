/*
 * File    : SimLibWSD.java
 * Created : 30-Jan-2009
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2007 David Garcia &
 * 2009 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.wsd.simlib;

import emolib.wsd.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

import org.apache.lucene.search.Hits;

import java.io.*;
import java.util.ArrayList;

/**
 * The <i>SimLibWSD</i> class performs the
 * Word Sense Disambiguation (WSD) process using the WordNet Similarity library.
 *
 * <p>
 * In order to perform this process, the word-sense disambiguator is based on the
 * system proposed by (Seco et al., 2004).
 * </p>
 * <p>
 * The SimLibWSD class requires a configuration parameter that indicates the location
 * of the necessary files that represent the indexed WordNet structure.
 * SimLibWSD depends on the (adapted) code developed by Nuno Seco for the WordNet
 * Similarity library.
 * </p>
 * <p>
 * --<br>
 * (Seco et al., 2004) Seco, N., Veale, T., Hayes, J. (2004) "An Intrinsic Information
 * Content Metric for Semantic Similarity in WordNet". In Proceedings of the
 * European Conference of Artificial Intelligence.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class SimLibWSD extends WordSenseDisambiguator {

    /**
     * The name of the property indicating the path of the WordNet index files
     * needed by the WordNet Similarity library.
     */
    public final static String PROP_WNINDEX_PATH = "wn_index_path";
    public final static String PROP_USE_NOUNS = "use_nouns";
    public final static String PROP_USE_VERBS = "use_verbs";
    public final static String PROP_USE_ADJECTIVES = "use_adjectives";

    private String wnIndexPath;
    private boolean use_n;
    private boolean use_v;
    private boolean use_a;

    private SimilarityAssessor assessorNoun; 
    private SimilarityAssessor assessorVerb;
    private SimilarityAssessor assessorAdjective;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_WNINDEX_PATH, PropertyType.STRING);
        registry.register(PROP_USE_NOUNS, PropertyType.BOOLEAN);
        registry.register(PROP_USE_VERBS, PropertyType.BOOLEAN);
        registry.register(PROP_USE_ADJECTIVES, PropertyType.BOOLEAN);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        wnIndexPath = ps.getString(PROP_WNINDEX_PATH, "nullpath");
        use_n = ps.getBoolean(PROP_USE_NOUNS, true);
        use_v = ps.getBoolean(PROP_USE_VERBS, true);
        use_a = ps.getBoolean(PROP_USE_ADJECTIVES, true);
    }


    /**
     * Method to initialize the SimLibWSD.
     */
    public void initialize() {
        if (wnIndexPath.equals("nullpath")) {
            System.out.println("EmoLib: the word-sense disambiguator has no WordNet index path defined in " +
                "the filesystem! Please check the configuration file.");
            System.exit(1);
        } else {
            assessorNoun = new SimilarityAssessor(wnIndexPath + "/noun");
            assessorVerb = new SimilarityAssessor(wnIndexPath + "/verb");
            assessorAdjective = new SimilarityAssessor(wnIndexPath + "/adj");
        }
    }


    /**
     * Main constructor of the SimLibWSD.
     */
    public SimLibWSD() {
    }


    /**
     * Method to perform the word-sense disambiguation process.
     * The meaning is taken at paragraph-level (all text obtained from a single acquisition)
     * for all the nouns in the text.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyWSD(TextData inputTextDataObject) {
        WordData tempWordData, otherWordData;
        ArrayList<Double> similarity;
        int index, bestIndex, senseCounter, resultingSenseCounter;
        int otherWordsCounter, otherWordsSenseCounter;
        double currentSimilarity, bestValue, currentValue;
        Hits resultingSense;
        ArrayList resultingSynonyms, tempSynonyms;
        SimilarityAssessor workingAssessor = null;
        boolean actual_n = false, actual_v = false, actual_a = false;

        for (int numberOfWords = 0; numberOfWords < 
                inputTextDataObject.getNumberOfWords(); numberOfWords++) {
            tempWordData = inputTextDataObject.getWordData(numberOfWords);
            if (tempWordData.hasEmotionalContent() &&
                    ((tempWordData.isNoun() && use_n) || 
                    (tempWordData.isVerb() && use_v) || 
                    (tempWordData.isAdjective() && use_a))) {
                if (tempWordData.isNoun() && use_n) {
                    workingAssessor = assessorNoun;
                    actual_n = true;
                    actual_v = false;
                    actual_a = false;
                } else if (tempWordData.isVerb() && use_v) {
                    workingAssessor = assessorVerb;
                    actual_n = false;
                    actual_v = true;
                    actual_a = false;
                } else if (tempWordData.isAdjective() && use_a) {
                    workingAssessor = assessorAdjective;
                    actual_n = false;
                    actual_v = false;
                    actual_a = true;
                }   
                similarity = new ArrayList<Double>(workingAssessor.
                    getHits(tempWordData.getWord().toLowerCase() + ".*").length());
                for (senseCounter = 0; senseCounter < workingAssessor.
                        getHits(tempWordData.getWord().toLowerCase() + ".*").length();
                        senseCounter++) {
                    currentSimilarity = 0;
                    for (otherWordsCounter = 0; otherWordsCounter < 
                            inputTextDataObject.getNumberOfWords();
                            otherWordsCounter++) {
                        otherWordData = inputTextDataObject.
                            getWordData(otherWordsCounter);
                        bestValue = 0;
                        currentValue = 0;
                        if ((!tempWordData.getWord().
                                equals(otherWordData.getWord())) && 
                                otherWordData.hasEmotionalContent() && 
                                ((otherWordData.isNoun() && use_n && actual_n) || 
                                (otherWordData.isAdjective() && use_a && actual_a) ||
                                (otherWordData.isVerb() && use_v && actual_v))) {
                            for (otherWordsSenseCounter = 0; otherWordsSenseCounter < 
                                    workingAssessor.getHits(otherWordData.getWord().
                                    toLowerCase() + ".*").length(); 
                                    otherWordsSenseCounter++) {
                                try {
                                    currentValue = workingAssessor.
                                        getSenseSimilarity(tempWordData.getWord(),
                                        senseCounter + 1, otherWordData.getWord(), 
                                        otherWordsSenseCounter + 1);
                                } catch (WordNotFoundException e) {
                                    currentValue = 0;
                                }
                                bestValue = currentValue > bestValue ? currentValue : 
                                    bestValue;
                            }
                        }
                        currentSimilarity += bestValue;
                    }
                    // currentSimilarity contains the similarity between 
                    // the tempWordData for one of
                    // its senses and the rest of the nouns in the text.
                    similarity.add(new Double(currentSimilarity));
                }
                // similarity contains all the similarity values corresponding to
                // each sense of tempWordData.
                if (!similarity.isEmpty()) {
                    bestValue = similarity.get(0).doubleValue();
                    bestIndex = 0;
                    for (index = 1; index < similarity.size(); index++) {
                        currentValue = similarity.get(index).doubleValue();
                        if (currentValue > bestValue) {
                            bestValue = currentValue;
                            bestIndex = index;
                        }
                    }
                    // The WordNet index adds one to the array index.
                    bestIndex++;
                    resultingSense = (Hits)workingAssessor.
                        getHits(tempWordData.getWord().toLowerCase() + "." + bestIndex);
                    resultingSynonyms = new ArrayList();
                    for (resultingSenseCounter = 0; resultingSenseCounter < resultingSense.length();
                    resultingSenseCounter++) {
                        try {
                            tempSynonyms = buildSynonyms(resultingSense.doc(resultingSenseCounter).
                                getField(workingAssessor.getWordsField()).stringValue(), tempWordData.getWord());
                            resultingSynonyms.addAll(tempSynonyms);
                        } catch (Exception e) {
                            System.out.println("EmoLib: a problem with SimLib has occurred!");
                            e.printStackTrace();
                        }
                    }
                    tempWordData.setSense(resultingSynonyms);
                }
            }
        }
    }


    /**
     * Function to preprocess the synonyms returned by WordNet through the Similarity Library.
     *
     * @param synSet The incoming synonym set.
     *
     * @return The preprocessed synonyms.
     */
    private ArrayList buildSynonyms(String synSet, String originalWord) {
        ArrayList resultingSynSet;
        int counter;
        String synCandidate;

        String[] chunks = synSet.split(" ");
        resultingSynSet = new ArrayList(chunks.length);
        for (counter = 0; counter < chunks.length; counter++) {
            synCandidate = chunks[counter].substring(0, chunks[counter].indexOf("."));
            if (synCandidate.equals(originalWord) || (synCandidate.indexOf("_") > 0)) {
            } else {
                resultingSynSet.add(synCandidate);
            }
        }
        return resultingSynSet;
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

