/*
 * File    : Classifier.java
 * Created : 18-Feb-2009
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

package emolib.classifier;

import emolib.util.conf.*;
import emolib.util.proc.*;

import java.util.ArrayList;

/**
 * The <i>Classifier</i> abstract class defines the
 * basic methods that any EmoLib classifier should implement in order to 
 * provide an affective label.
 *
 * <p>
 * The Classifier establishes the frontier (interface) between the
 * feature-wise world and the knowledge-wise world.
 * The knowledge may be incorporated in the classifier by means of
 * explicitly hard-coding it into the definition of the classifier, i.e.,
 * the expert-based heuristic approach, or by means of automatically learning 
 * it from training data, i.e., the data-driven Machine Learning approach.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public abstract class Classifier extends TextDataProcessor {

    private ArrayList<FeatureBox> listOfExampleFeatures;
    private ArrayList<String> listOfExampleCategories;


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
                applyClassification(((TextData)input));
            }
        }
        return input;
    }


    /**
     * Method to initialize the Classifier.
     */
    public void initialize() {
    }


    /**
     * Main constructor of the Classifier.
     */
    public Classifier() {
        listOfExampleFeatures = new ArrayList<FeatureBox>();
        listOfExampleCategories = new ArrayList<String>();
    }


    /**
     * Method to perform the classification process.
     * This is the method that will be called externally (by the processing
     * chain) and will wrap all the work.
     * Since the target application of the whole system is emotional
     * speech synthesis, the depth of the analysis is required
     * at document, paragraph and sentence levels.
     * The rest of the abstract methods are
     * required to perform some basic classification functionalities.
     * Beware that the textual features are not yet considered!!!
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyClassification(TextData inputTextDataObject) {
        ParagraphData tempParagraph;
        SentenceData tempSentence;
        WordData tempWordData;
        FeatureBox tempFeatureBox;

        for (int numberOfParagraph = 0; numberOfParagraph < inputTextDataObject.getNumberOfParagraphs();
            numberOfParagraph++) {
            tempParagraph = inputTextDataObject.getParagraphData(numberOfParagraph);
            for (int numberOfSentence = 0; numberOfSentence < tempParagraph.getNumberOfSentences();
            numberOfSentence++) {
                tempSentence = tempParagraph.getSentenceData(numberOfSentence);
                if (tempSentence.hasEmotionalContent()) {
                    tempFeatureBox = new FeatureBox();
                    // It is supposed that the emotional entity in question has three
                    // emotional dimensions. If someday the emotional features are different
                    // from the original implementation, this code should be fixed.
                    if (tempSentence.containsEmotionalDimentions()) {
                        tempFeatureBox.setNumberOfEmotionalDimensions(3);
                        tempFeatureBox.setValence(tempSentence.getEmotionalValence());
                        tempFeatureBox.setActivation(tempSentence.getEmotionalActivation());
                        tempFeatureBox.setControl(tempSentence.getEmotionalControl());
                        tempSentence.setEmotionalCategory(getCategory(tempFeatureBox));
                    }
                }
            }
            if (tempParagraph.hasEmotionalContent()) {
                tempFeatureBox = new FeatureBox();
                if (tempParagraph.containsEmotionalDimentions()) {
                    tempFeatureBox.setNumberOfEmotionalDimensions(3);
                    tempFeatureBox.setValence(tempParagraph.getEmotionalValence());
                    tempFeatureBox.setActivation(tempParagraph.getEmotionalActivation());
                    tempFeatureBox.setControl(tempParagraph.getEmotionalControl());
                    tempParagraph.setEmotionalCategory(getCategory(tempFeatureBox));
                }
            }
        }
        if (inputTextDataObject.hasEmotionalContent()) {
            tempFeatureBox = new FeatureBox();
            if (inputTextDataObject.containsEmotionalDimentions()) {
                tempFeatureBox.setNumberOfEmotionalDimensions(3);
                tempFeatureBox.setValence(inputTextDataObject.getEmotionalValence());
                tempFeatureBox.setActivation(inputTextDataObject.getEmotionalActivation());
                tempFeatureBox.setControl(inputTextDataObject.getEmotionalControl());
                inputTextDataObject.setEmotionalCategory(getCategory(tempFeatureBox));
            }
        }
    }


    /**
     * The function that decides the most appropriate emotional category.
     * This is required for any classifier.
     * The classifier in question has to previously
     * run any training algorithm in order to provide the required prediction.
     *
     * @param inputFeatures The input emotional features.
     *
     * @return The most appropriate emotional category.
     */
    public abstract String getCategory(FeatureBox inputFeatures);


    /**
     * Mehtod to input training data into the classifier.
     * The training examples are fed to the classifier
     * one by one. It is a matter of the classifier in question to treat them
     * appropriately.
     *
     * @param features The input emotional features.
     *
     * @param cat The category of the input example.
     */
    public void inputTrainingExample(FeatureBox features, String cat) {
        listOfExampleFeatures.add(features);
        listOfExampleCategories.add(new String(cat));
    }


    /**
     * Method to train the classifier.
     * Only trainable classifiers will implement the body of this method. The classifiers
     * that rely on an expert knowledge base will implement a void method.
     */
    public void train() {
        if (!listOfExampleFeatures.isEmpty()) {
            trainingProcedure();
        } else {
            System.out.println("Classifier: trying to train with no input examples!");
        }
    }


    /**
     * Generic training procedure.
     * It trains the classifier in question with the input training examples.
     */
    public abstract void trainingProcedure();


    /**
     * Retrieves the list of training example features.
     *
     * @return The list of training example features.
     */
    public ArrayList<FeatureBox> getListOfExampleFeatures() {
        return listOfExampleFeatures;
    }


    /**
     * Retrieves the list of training example categories.
     *
     * @return The list of training example categories.
     */
    public ArrayList<String> getListOfExampleCategories() {
        return listOfExampleCategories;
    }


    /**
     * Method to reset the classifier and flush the training examples.
     * This method only makes sense if the classifier in question is trainable and
     * already has some training examples.
     */
    public void resetExamples() {
        listOfExampleFeatures = new ArrayList<FeatureBox>();
        listOfExampleCategories = new ArrayList<String>();
    }


    /**
     * Generic method to save the fully fledged classifier into a given file path.
     * It is recommended to use a plain text file (such as XML)
     * to save the classifier's configuration since it's readable directly.
     *
     * @param path The file path to save the classifier.
     */
    public abstract void save(String path);


    /**
     * Generic function to load a previously saved classifier.
     * This function should be consistent with the design followed in the
     * saving procedure.
     *
     * @param path The path of the file which contains the previously saved
     * classifier.
     */
    public abstract void load(String path);

}

