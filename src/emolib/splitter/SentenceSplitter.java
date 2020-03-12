/*
 * File    : SentenceSplitter.java
 * Created : 04-Dec-2008
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

package emolib.splitter;

import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>SentenceSplitter</i> abstract class defines the general structure to perform the
 * sentence segmentation process.
 *
 * <p>
 * The definition of the sentence boundaries is necessary in order to have a structured
 * document. Then, statistics of the emotion conveyed by the different 
 * sentences that build a paragraph can be easily retrieved.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public abstract class SentenceSplitter extends TextDataProcessor {

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
                applySentenceSplitting(((TextData)input));
            }
        }
        return input;
    }


    /**
     * Method to initialize the SentenceSplitter.
     */
    public void initialize() {
    }


    /**
     * Main constructor of the SentenceSplitter.
     */
    public SentenceSplitter() {
    }


    /**
     * Method to perform the sentence splitting process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public abstract void applySentenceSplitting(TextData inputTextDataObject);

}

