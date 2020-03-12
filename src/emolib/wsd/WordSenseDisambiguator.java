/*
 * File    : WordSenseDisambiguator.java
 * Created : 14-Nov-2008
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

package emolib.wsd;

import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>WordSenseDisambiguator</i> abstract class defines the general structure to perform the
 * Word Sense Disambiguation process, which determines the correct sense of polysemous words in context.
 * <p>
 * A rich variety of techniques have been researched, 
 * from dictionary-based methods that use the knowledge 
 * encoded in lexical resources, to supervised machine 
 * learning methods in which a classifier is trained for each distinct 
 * word on a corpus of manually sense-annotated examples,
 * to completely unsupervised methods that cluster occurrences of words, 
 * thereby inducing word senses. Among these, supervised learning 
 * approaches have been the most successful algorithms to date.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public abstract class WordSenseDisambiguator extends TextDataProcessor {

    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
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
                applyWSD(((TextData)input));
            }
        }
        return input;
    }


    /**
     * Method to initialize the WordSenseDisambiguator.
     */
    public void initialize() {
    }


    /**
     * Main constructor of the WordSenseDisambiguator.
     */
    public WordSenseDisambiguator() {
    }


    /**
     * Method to perform the word sense disambiguation process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public abstract void applyWSD(TextData inputTextDataObject);

}

