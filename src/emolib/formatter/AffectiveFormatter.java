/*
 * File    : AffectiveFormatter.java
 * Created : 11-Dec-2008
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

package emolib.formatter;

import emolib.util.conf.*;
import emolib.util.proc.*;

import java.io.File;

/**
 * The <i>AffectiveFormatter</i> abstract class defines the general structure to perform the
 * formatting of the results.
 * <p>
 * This module should format the results obtained from the processing chain and present them
 * accordingly.
 * For this purpose, it must always be plugged at the end of the processing pipeline thus being
 * the last text data processor of the chain.
 * </p>
 * <p>
 * Due to the position where this module is located and the function it has been designed for,
 * it is commonly referred to as the OUTPUTTER of the pipeline, therefore it implements the
 * "outputData" method.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public abstract class AffectiveFormatter extends TextDataProcessor {

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
                applyFormatting(((TextData)input));
            }
        }
        return input;
    }


    /**
     * Method to initialize the AffectiveFormatter.
     */
    public void initialize() {
    }


    /**
     * Main constructor of the AffectiveFormatter.
     */
    public AffectiveFormatter() {
    }


    /**
     * Method to perform the formatting process.
     * This method should take record of all the results that flow through it.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public abstract void applyFormatting(TextData inputTextDataObject);


    /**
     * Method to produce the results obtained from the text processing pipeline
     * into a file.
     * This method labels the AffectiveFormatter module as an OUTPUTTER.
     * This implementation is the original one. The method that follows should
     * substitute it for wrapping design.
     */
    public abstract void outputData();


    /**
     * Method to produce the results obtained from the text processing pipeline
     * into a determined file.
     * This method labels the AffectiveFormatter module as an OUTPUTTER.
     *
     * @param file File The determined file to output the results. This is normally a temp file.
     */
    public abstract void outputData(File file);

}

