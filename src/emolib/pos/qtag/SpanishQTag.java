/*
 * File    : SpanishQTag.java
 * Created : 2007
 * By      : dgarcia
 *
 * Modified: 17-Nov-2008
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2007 David Garcia &
 * 2008 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.pos.qtag;

import java.io.*;

import emolib.pos.*;
import qtag.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>SpanishQTag</i> class performs the
 * Part-Of-Speech (POS) tagging process in Spanish using the QTag library.
 *
 * <p>
 * In order to obtain a Spanish version of QTag, the guidelines posted on
 * the blog "Pythonner Zone!"
 * <a href="http://pythonner.blogspot.com/2004/05/building-spanish-part-of-speech-tagger.html"
 * "">Building a Spanish Part-of-Speech Tagger for Java in 5 Easy Steps...</a>.
 * Basically, the steps have been the following:
 * <ol>
 *     <li>
 *         Obtaining of <a href="http://www.english.bham.ac.uk/staff/omason/software/qtag.html">QTag</a>,
 *         a probabilistic POS tagger developed by Oliver Mason in Java at the University of
 *         Birmingham (UK).
 *     </li>
 *     <li>
 *         Obtaining of the Spanish corpus of the CoNLL 2002 Shared Task with POS tags, available at 
 *         <a href="http://www.lsi.upc.es/~nlp/tools/nerc/nerc.html"
 *         "">Resources on Named Entity Recognition and Classification (NERC)</a>.
 *     </li>
 *     <li>
 *         Modification of the corpus: removal of the second tag at the end of each line,
 *         removal of the empty lines and removal of the lines with long strings of
 *         "=" signs.
 *     </li>
 *     <li>
 *         Training of QTag through: <tt>java qtag.ResourceCreator esp.train.txt qtag-spanish</tt>
 *     </li>
 *     <li>
 *         Usage of the new POS tagger.
 *     </li>
 * </ol>
 * </p>
 * <p>
 * The necessary files to generate the tagger in Spanish using QTag are available in the
 * <tt>dat/dataset/conll02task</tt> folder,
 * </p>
 * <p>
 * This POS tagger makes mistakes. QTag is a probabilistic POS tagger, so it may be inaccurate. The
 * training Spanish corpus also has incoherences. But if used for what it is meant to be (the disambiguation
 * of the function of nouns, verbs and adjectives in a sentence) this tool does its job successfully.
 * </p>
 *
 * @author David Garc&iacute;a
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class SpanishQTag extends POSTagger {

    /**
     * The name of the property indicating the location of
     * the lexicon and matrix Spanish files.
     */
    public final static String PROP_RESOURCES_PATH = "resources_path";


    private Tagger tagger;
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
                System.out.println("The resources path required by SpanishQTag has not been defined in " +
                    "the configuration file!");
                System.exit(1);
            } else {
                tagger = new Tagger(resourcesPath + "qtag-spanish");
            }
        } catch (IOException e) {
            System.out.println("ERROR loading the QTag tagger!");
            e.printStackTrace();
        }
    }


    /**
     * Main constructor of the SpanishQTag.
     */
    public SpanishQTag() {
    }


    /**
     * Method to perform the POS tagging process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyPOSTagging(TextData inputTextDataObject) {
        String[] posTags = tagger.tag(inputTextDataObject.getWordsAsArrayOfStrings());
        WordData tempWordData;
        for (int numberOfWords = 0; numberOfWords < posTags.length; numberOfWords++) {
            tempWordData = inputTextDataObject.getWordData(numberOfWords);
            tempWordData.setWordPOS(posTags[numberOfWords]);
            if (posTags[numberOfWords].startsWith("V")) {
                tempWordData.setAsVerb();
            } else if ((posTags[numberOfWords].equals("NC")) ||
                    (posTags[numberOfWords].equals("NP"))) {
                tempWordData.setAsNoun();
            } else if (posTags[numberOfWords].equals("AQ")) {
                tempWordData.setAsAdjective();
            }
            inputTextDataObject.setWordData(numberOfWords, tempWordData);
        }
    }

}

