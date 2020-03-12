/*
 * File    : EnglishStanford.java
 * Created : 2007
 * By      : dgarcia
 *
 * Modified: 23-Jan-2009
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

package emolib.pos.stanford;

import java.io.*;
import java.util.Vector;

import edu.stanford.nlp.tagger.maxent.*;

import emolib.pos.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>EnglishStanford</i> class performs the
 * Part-Of-Speech (POS) tagging process in English using the Stanford POS tagger.
 *
 * <p>
 * The Stanford POS tagger is a high performance POS tagger that makes use of several enriched features
 * as well as a bidirectional structure (dependency network) to compute the predictions.
 * </p>
 * <p>
 * The necessary files (models) to use the Stanford POS tagger in English
 * are available in the <tt>dat</tt> folder,
 * under the <tt>stanford-postagger/english</tt> folder name.
 * The configuration parameter <i>resources_path</i> must lead to the desired model file.
 * </p>
 * <p>
 * This POS tagger makes mistakes. Stanford POS tagger is a
 * probabilistic POS tagger, so it may be inaccurate although the correctness performance
 * is slightly better than 97% using the enriched bidirectional architecture.
 * </p>
 *
 * @author David Garc&iacute;a
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class EnglishStanford extends POSTagger {

    /**
     * The name of the property indicating the location of
     * the English model.
     */
    public final static String PROP_RESOURCES_PATH = "resources_path";


    private MaxentTagger tagger;
    private String resourcesPath;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_RESOURCES_PATH, PropertyType.STRING);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        resourcesPath = ps.getString(PROP_RESOURCES_PATH, "nullpath");
    }


    /**
     * Method to initialize the SpanishQTag.
     */
    public void initialize() {
        try {
            if (resourcesPath.equals("nullpath")) {
                System.out.println("The resources path required by EnglishStanford has not been defined in " +
                    "the configuration file!");
                System.exit(1);
            } else {
                tagger = new MaxentTagger(resourcesPath);
            }
        } catch (Exception e) {
            System.out.println("ERROR loading the EnglishStanford tagger!");
            e.printStackTrace();
        }
    }


    /**
     * Main constructor of the SpanishQTag.
     */
    public EnglishStanford() {
    }


    /**
     * Method to perform the POS tagging process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyPOSTagging(TextData inputTextDataObject) {
        int i;

        try {
            String[] words = inputTextDataObject.getWordsAsArrayOfStrings();
            String theWords = "";
            for (i = 0; i < words.length; i++) {
                theWords += words[i] + " ";
            }
            theWords = theWords.trim();
            String taggedWords = tagger.tagString(theWords);
            String[] posTags = taggedWords.split(" ");
            Vector tag = new Vector();
            int index = 0;
            for (i = 0; i < posTags.length; i++) {
                tag.add(posTags[i].substring(posTags[i].indexOf("/") + 1));
            }
            //
            WordData tempWordData;
            for (int numberOfWords = 0; numberOfWords < posTags.length; numberOfWords++) {
                tempWordData = inputTextDataObject.getWordData(numberOfWords);
                tempWordData.setWordPOS((String)tag.get(numberOfWords));
                if (correspondsToVerb((String)tag.get(numberOfWords))) {
                    tempWordData.setAsVerb();
                } else if (correspondsToNoun((String)tag.get(numberOfWords))) {
                    tempWordData.setAsNoun();
                } else if (correspondsToAdjective((String)tag.get(numberOfWords))) {
                    tempWordData.setAsAdjective();
                } else if (correspondsToAdverb((String)tag.get(numberOfWords))) {
                    tempWordData.setAsAdverb();
                }
                inputTextDataObject.setWordData(numberOfWords, tempWordData);
            }
        } catch (Exception e) {
            System.out.println("EmoLib: a problem with the EnglishStanford POS tagging process!");
            e.printStackTrace();
        }
    }


    /**
     * Function to determine if the input tag corresponds to an adverb.
     *
     * @param theTag The input tag.
     *
     * @return True if the input tag corresponds to an adverb.
     */
    private boolean correspondsToAdverb(String theTag) {
        boolean result = false;
        if (theTag.equals("RB") || theTag.equals("RBR") ||
                theTag.equals("RBS") || theTag.equals("WRB")) {
            result = true;
        }
        return result;
    }


    /**
     * Function to determine if the input tag corresponds to a noun.
     *
     * @param theTag The input tag.
     *
     * @return True if the input tag corresponds to a noun.
     */
    private boolean correspondsToNoun(String theTag) {
        boolean result = false;
        if (theTag.equals("NN") || theTag.equals("NNP") || theTag.equals("NNPS") || theTag.equals("NNS") ||
        theTag.equals("FW") || theTag.equals("CD")) {
            result = true;
        }
        return result;
    }


    /**
     * Function to determine if the input tag corresponds to a verb.
     *
     * @param theTag The input tag.
     *
     * @return True if the input tag corresponds to a verb.
     */
    private boolean correspondsToVerb(String theTag) {
        boolean result = false;
        if (theTag.equals("VB") || theTag.equals("VBD") || theTag.equals("VBG") || theTag.equals("VBN") ||
        theTag.equals("VBP") || theTag.equals("VBZ") || theTag.equals("MD")) {
            result = true;
        }
        return result;
    }


    /**
     * Function to determine if the input tag corresponds to an adjective.
     *
     * @param theTag The input tag.
     *
     * @return True if the input tag corresponds to an adjective.
     */
    private boolean correspondsToAdjective(String theTag) {
        boolean result = false;
        if (theTag.equals("JJ") || theTag.equals("JJR") || theTag.equals("JJS")) {
            result = true;
        }
        return result;
    }

}

