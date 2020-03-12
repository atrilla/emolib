/*
 * File    : Statistic.java
 * Created : 07-Jul-2009
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2009 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.statistic;

import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>Statistic</i> abstract class defines the general class to perform the
 * statistical calculations.
 * <p>
 * At this point, the emotional words should already have their correspondent emotional
 * dimensions. The purpose of this class is to compute a determined statistic
 * and use it as an estimator of a population parameter which is not directly
 * observable. These parameters should be available for all the levels of the
 * data structure (sentence and paragraph levels).
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public abstract class Statistic extends TextDataProcessor {

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
                applyStatistics(((TextData)input));
            }
        }
        return input;
    }


    /**
     * Method to initialize the Statistic.
     */
    public void initialize() {
    }


    /**
     * Main constructor of the Statistic processor.
     */
    public Statistic() {
    }


    /**
     * Method to perform the statistical process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public abstract void applyStatistics(TextData inputTextDataObject);

}

