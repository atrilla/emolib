/*
 * File    : AffectiveTagger.java
 * Created : 11-Nov-2008
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

package emolib;

import java.util.List;
import java.util.Iterator;
import java.lang.reflect.*;
import java.io.File;

import emolib.util.*;
import emolib.util.proc.*;
import emolib.util.conf.*;
import emolib.formatter.AffectiveFormatter;

/**
 * The <i>AffectiveTagger</i> is a wrapper class for the chain of Text Data Processors
 * that perform the emotional tagging of the incoming text.
 * It provides methods for manipulating and navigating the processors.
 * <p>
 * The AffectiveTagger is modelled as a pipeline of Text Data Processors,
 * each of which performs a specific Natural Language Processing (NLP) task.
 * </p>
 * <p>
 * This class is based on the Sphinx-4 FrontEnd class with the appropriate
 * Text Data objects. It follows the Sphinx-4 implementation in order to friendly
 * interact with the {@link emolib.util.conf.ConfigurationManager}, a very
 * useful tool to deal with the configuration issues. More information about the
 * interfaces that must be implemented, the Pull Model, the configuration and the
 * usage of this class is to be found in the documentation of the Sphinx-4
 * <a href="http://cmusphinx.sourceforge.net/sphinx4/javadoc/edu/cmu/sphinx/frontend/FrontEnd.html">FrontEnd</a>.
 * </p>
 * <p>
 * The AffectiveTagger implemented for EmoLib proposes that the Data objects that
 * carry the actual information should be of the
 * {@link emolib.util.proc.TextData} class, i.e., the main data structure,
 * and the processors chained in the pipeline should
 * extend the {@link emolib.util.proc.TextDataProcessor} abstract class.
 * These two classes implement the interfaces required by the Configuration Manager.
 * </p>
 * <p>
 * The arrangement of the selected Text Data Processors is given by the external
 * configuration file in XML format, treated by the Configuration Manager.
 * </p>
 * <p>
 * The tagger provides the tools to input data into the
 * processing pipeline as well as to retrieve the results from
 * the pipeline through the "inputData" and "outputData" methods.
 * These methods MUST be implemented by the first and the last modules of the pipeline,
 * namely the INPUTTER and OUTPUTTER.
 * Otherwise, the application will report the problem and crash.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class AffectiveTagger extends TextDataProcessor {

    /**
     * The name of the property list of all the components of
     * the AffectiveTagger pipe line.
     */
    public final static String PROP_PIPELINE = "pipeline";

    /**
     * The name of the property that defines the language of the system.
     * This property should be coherent with the rest of the modules
     * of the AffectiveTagger. Please check the XML configuration file.
     */
    public final static String PROP_LANGUAGE = "language";

    /**
     * The name of the property that defines the root path of EmoLib.
     */
    public final static String PROP_EMOLIB_PATH = "emolib_path";

    // ----------------------------
        // Configuration data
    // -----------------------------
        private List textProcessorList;

    private DataProcessor first;
    private DataProcessor last;

    private String lang;

    private String path;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_PIPELINE, PropertyType.COMPONENT_LIST);
        registry.register(PROP_LANGUAGE, PropertyType.STRING);
        registry.register(PROP_EMOLIB_PATH, PropertyType.STRING);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        textProcessorList = ps.getComponentList(PROP_PIPELINE, DataProcessor.class);

        lang = ps.getString(PROP_LANGUAGE, "English");

        path = ps.getString(PROP_EMOLIB_PATH, "NONE");

        for (Iterator i = textProcessorList.iterator(); i.hasNext();) {
            DataProcessor dp = (DataProcessor)i.next();
            dp.setPredecessor(last);
            if (first == null) {
                first = dp;
            }
            last = dp;
        }
        initialize();
    }


    /* (non-Javadoc)
     * @see emolib.util.proc.DataProcessor#initialize()
     */
    public void initialize() {
        super.initialize();
        for (Iterator i = textProcessorList.iterator(); i.hasNext();) {
            TextDataProcessor dp = (TextDataProcessor)i.next();
            dp.initialize();
        }
    }


    /* (non-Javadoc)
     * @see emolib.util.proc.DataProcessor#flush()
     */
    public void flush() {
        for (Iterator i = textProcessorList.iterator(); i.hasNext();) {
            TextDataProcessor dp = (TextDataProcessor)i.next();
            dp.flush();
        }
    }

    /**
     * Sets the source of Data for this AffectiveTagger.
     * It basically sets the predecessor of the first DataProcessor
     * of this AffectiveTagger.
     *
     * @param dataSource The source of Data.
     */
    private void setDataSource(DataProcessor dataSource) {
        first.setPredecessor(dataSource);
    }

    /**
     * Returns the processed Data output, basically calls
     * <code>getData()</code> on the last processor.
     *
     * @return A Data object that has been processed by this AffectiveTagger.
     *
     * @throws DataProcessingException If a Data Processor error occurs.
     */
    public Data getData() throws DataProcessingException {
        Data data = last.getData();

        return data;
    }


    /**
     * Sets the source of Data for this AffectiveTagger.
     * It basically calls <code>setDataSource(dataSource)</code>.
     *
     * @param dataSource The source of Data.
     */
    public void setPredecessor(DataProcessor dataSource) {
        setDataSource(dataSource);
    }


    /**
     * Returns a description of this AffectiveTagger in the format:
     * <tagger name> {<TextDataProcessor1>, <TextDataProcessor2> ...
     * <TextDataProcessorN>}
     *
     * @return A description of this AffectiveTagger.
     */
    public String toString() {
        String description = "";
        DataProcessor current = last;
        while (current != null) {
            description = (current.getName() + description);
            current = current.getPredecessor();
            if (current != null) {
                description = (", " + description);
            }
        }
        return (getName() + " {" + description + "}");
    }


    /**
     * Function that returns the language of the AffectiveTagger.
     * This language should be coherent with the rest of the modules
     * of the AffectiveTagger. Please check the XML configuration file.
     *
     * @return The language of the system.
     */
    public String getLanguage() {
        return lang;
    }


    /**
     * Function that returns the root path of EmoLib.
     *
     * @return The root path.
     */
    public String getEmoLibPath() {
        return path;
    }


    /**
     * Method to  initialize the INPUTTER.
     * This method mas been separated from the "inputData" in order to avoid
     * confusing errors in case there exists any.
     *
     * @param inData The text to be inputted.
     */
    private void initInputter(String inData) {
        try {
            Class inputDataClass = first.getClass();
            Method inputMethod = inputDataClass.getMethod("inputData", new Class[]{java.lang.String.class});
            inputMethod.invoke(first, inData);
        } catch (Exception e) {
            System.out.println("EmoLib: the first module of the pipeline is not an INPUTTER!");
            e.printStackTrace();
        }
    }


    /**
     * Function to input and process data.
     * The data to be inputted must be a String. The developer should ensure that the first
     * data processor defined in the pipeline is able to execute the "inputData" method.
     *
     * @param inData The text to be inputted.
     *
     * @return The processed data.
     */
    public Data inputData(String inData) {
        Data theData = null;
        try {
            initInputter(inData);
            theData = getData();
        } catch (Exception e) {
            System.out.println("EmoLib: there's been a problem inputting the data!");
            e.printStackTrace();
        }
        return theData;
    }


    /**
     * Function to process textual data.
     * This function is just a rename of inputData.
     *
     * @param text The text to process.
     *
     * @return The processed data.
     */
    public TextData processText(String text) {
        return (TextData)inputData(text);
    }


    /**
     * Method to issue the Tagger to output the results.
     * It brings the OUTPUTTER into action.
     */
    public void outputData() {
        try {
            Class outputDataClass = last.getClass();
            Method outputMethod = outputDataClass.getMethod("outputData", new Class[]{});
            outputMethod.invoke(last, new Object[]{});
        } catch (Exception e) {
            System.out.println("EmoLib: the last module of the pipeline is not an OUTPUTTER!");
            e.printStackTrace();
        }
    }


    /**
     * Method to issue the Tagger to output the results into a determined file.
     * It brings the OUTPUTTER into action.
     */
    public void outputData(File file) {
        try {
            Class outputDataClass = last.getClass();
            Method outputMethod = outputDataClass.getMethod("outputData", new Class[]{File.class});
            outputMethod.invoke(last, new Object[]{file});
        } catch (Exception e) {
            System.out.println("EmoLib: the last module of the pipeline is not an OUTPUTTER!");
            e.printStackTrace();
        }
    }

}

