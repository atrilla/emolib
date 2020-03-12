/*
 * File    : EmotionalKeywordSpotter.java
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

package emolib.eks;

import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>EmotionalKeywordSpotter</i> abstract class defines the general structure to perform the
 * Emotional Keyword Spotting (EKS) process, which detects the words with an affective
 * content and assigns them the pertinent emotional dimensions.
 *
 * <p>
 * This module should work along with an affective dictionary, containing the emotional
 * dimensions for each of the words in the database. Once the EmotionalKeywordSpotter
 * detects the presence of an affective word, it tags it accordingly.
 * Normally these dictionaries index stems of words in order to be as much extensive
 * as possible. In order to surpass the conflation effect introduced by the
 * stemming process, a POS disambiguation is used.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public abstract class EmotionalKeywordSpotter extends TextDataProcessor {

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
                applyEKS(((TextData)input));
            }
        }
        return input;
    }


    /**
     * Method to initialize the Stemmer.
     */
    public void initialize() {
    }


    /**
     * Main constructor of the Stemmer.
     */
    public EmotionalKeywordSpotter() {
    }


    /**
     * Method to perform the stemming process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public abstract void applyEKS(TextData inputTextDataObject);

}

