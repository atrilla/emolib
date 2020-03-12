/*
 * File    : WordAssoc.java
 * Created : 21-Feb-2012
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2012 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.wordassoc;

import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>WordAssoc</i> provides words associated to the ones observed in a given text.
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public abstract class WordAssoc extends TextDataProcessor {

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
                applyWordAssociation(((TextData)input));
            }
        }
        return input;
    }


    /**
     * Method to initialize the POSTagger.
     */
    public void initialize() {
    }


    /**
     * Main constructor of the POSTagger.
     */
    public WordAssoc() {
    }


    /**
     * Method to perform the POS tagging process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public abstract void applyWordAssociation(TextData inputTextDataObject);

}

