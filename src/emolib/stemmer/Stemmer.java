/*
 * File    : Stemmer.java
 * Created : 19-Nov-2008
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

package emolib.stemmer;

import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>Stemmer</i> abstract class defines the general structure to perform the
 * stemming process, which eliminates the inflexion of words.
 * <p>
 * The idea of stemming is to improve the Information Retrieval (IR) performance
 * generally by bringing under one heading variant forms of a word which share
 * a common meaning.
 * </p>
 * <p>
 * Stemming is feasible to the Indo-European languages because there exist common
 * patterns of word structures, and for languages that are more highly inflected
 * than English (and most of them are), greater improvements will be observed when
 * stemming is applied. Thus, for a Romance language like Spanish the results
 * are expected to be reasonably good.
 * </p>
 * <p>
 * Assuming that words are written left to right, the stem, or root of a word is on
 * the left, and zero or more suffixes may be added on the right. If the root is
 * modified by this process it will normally be at its right hand end. Also, prefixes
 * may be added on the left usually altering its meaning radically, so they are best
 * left in place. But suffixes can, in certain circumstances, be removed. In fact,
 * suffix stripping is a practical aid in IR after all.
 * </p>
 * <p>
 * Here, stem and root are used interchangeably, but there exists a finer
 * distinction between them, regarding the stem as the residue of the stemming
 * process and the root as the inner word from which the stem word derives. Anyway,
 * such a proficient degree is not intended in this Stemmer class description.
 * </p>
 * <p>
 * There is a lot more to be said about the stemming process: the stemming errors, the
 * use of dictionaries, stop words, irregularities, etc. Refer to Dr. Porter's article
 * <a href="http://snowball.tartarus.org/texts/introduction.html">Snowball: A language for stemming algorithms</a>
 * in order to get an extensive description of the stemming process.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public abstract class Stemmer extends TextDataProcessor {

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
                applyStemming(((TextData)input));
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
    public Stemmer() {
    }


    /**
     * Method to perform the stemming process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public abstract void applyStemming(TextData inputTextDataObject);

}

