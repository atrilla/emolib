/*
 * File    : POSTagger.java
 * Created : 17-Nov-2008
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

package emolib.pos;

import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>POSTagger</i> abstract class defines the general structure to perform the
 * Part-Of-Speech (POS) tagging process, which determines the correct function of a word
 * in a sentence.
 * <p>
 * Nouns, adjectives and verbs share the same regular expression, thus their function cannot
 * be inferred with a simple tokeniser. These classes of word need to be disambiguated
 * according to the context where they appear.
 * </p>
 * <p>
 * In order to overcome this problem, two approaches can be presented:
 * <ul>
 *     <li>
 *         <b>Rule-based approaches</b>: The classical approach to resolving ambiguities
 *         by constructing a set of rules based on contextual clues. Many rules and
 *         exceptions are required, and this labour is hard and tedious to accomplish and maintain.
 *     </li>
 *     <li>
 *         <b>Statistical approaches</b>: The construction of these rules is based on the
 *         estimation of the conditional probabilities of appearance of a word according
 *         to the context observed in previously tagged textual corpora.
 *     </li>
 * </ul>
 * </p>
 * <p>
 * Any well coded POS tagger should set the incoming words as nouns, verbs or adjectives according
 * to the results obtained. 
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public abstract class POSTagger extends TextDataProcessor {

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
                applyPOSTagging(((TextData)input));
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
    public POSTagger() {
    }


    /**
     * Method to perform the POS tagging process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public abstract void applyPOSTagging(TextData inputTextDataObject);

}

