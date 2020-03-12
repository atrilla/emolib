/*
 * File    : TextDataProcessor.java
 * Created : 10-Nov-2008
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

package emolib.util.proc;

import emolib.util.conf.PropertyException;
import emolib.util.conf.PropertySheet;
import emolib.util.conf.Registry;

/**
 * The <i>TextDataProcessor</i> is an
 * abstract base text processing class parent of all the
 * primary classes of the text processing tasks.
 * This class implements the different interfaces required by
 * the Configuration Manager.
 * <p>
 * All the Data Processors (generally speaking)
 * that build the text processing chain
 * defined by the AffectiveTagger should extend this abstract class.
 * </p>
 */
public abstract class TextDataProcessor implements DataProcessor {

    private String name;
    private DataProcessor predecessor;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry)
    throws PropertyException {
        setName(name);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
    }

    /*
     * (non-Javadoc)
     * @see DataProcessor#getData()
     */
    public abstract Data getData() throws DataProcessingException;

    /*
     * (non-Javadoc)
     * @see DataProcessor#initialize()
     */
    public void initialize() {
    }

    /**
     * Method to flush the previous partial results of this TextDataProcessor.
     */
    public void flush() {
    }

    /*
     * (non-Javadoc)
     * @see DataProcessor#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this TextDataProcessor.
     *
     * @param name The name.
     */
    private void setName(String name) {
        this.name = name;
    }


    /*
     * (non-Javadoc)
     * @see DataProcessor#getPredecessor()
     */
    public DataProcessor getPredecessor() {
        return predecessor;
    }

    /*
     * (non-Javadoc)
     * @see DataProcessor#setPredecessor(DataProcessor predecessor)
     */
    public void setPredecessor(DataProcessor predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * Returns the name of this TextDataProcessor.
     *
     * @return The name of this TextDataProcessor.
     */
    public String toString() {
        return name;
    }

}

