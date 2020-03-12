/*
 * File    : HierarchicalARNReduced.java
 * Created : 20-Feb-2010
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2010 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.classifier.machinelearning;

//import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;
//import java.util.Hashtable;
//import java.util.List;
//import java.io.*;

import emolib.util.conf.*;
import emolib.util.proc.*;
import emolib.classifier.Classifier;
import emolib.classifier.FeatureBox;

//import org.jdom.*;
//import org.jdom.output.*;
//import org.jdom.input.*;

/**
 * The <i>HierarchicalARNReduced</i> is an ensemble of Reduced Associative
 * Relational Networks considering the hierarchy of affect.
 *
 * <p>
 * The HierarchicalARNReduced enhances a ARN-R classifier
 * adding hierarchical classification steps according to the human difficulty
 * to discern emotions/sentiments.
 * </p>
 * <p>
 * According to the chosen learning procedure, the structure of the hierarchy is built. The
 * available schemes are listed as follows:
 * <ul>
 *         <li>
 *                 "sentiment_hierarchical_cluster_analysis": the first classification level differentiates neutral
 *                 from affective instances (textual features). If affect is detected, then a second
 *                 level of analysis decides whether its orientation is positive or negative. Note that the
 *                 training gold labels must be consistent with the N-P-neutral set.
 *         </li>
 * </ul>
 * </p>
 *
 * @see emolib.classifier.machinelearning.ARNReduced
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class HierarchicalARNReduced extends Classifier {

    /**
     * Property to indicate a pre-trained classifier.
     */
    public final static String PROP_EXTERNAL_FILE = "external_file";


    private String externalFile;

    // Classifier specific parameters
    private String learningProcedure;
    private ARNReduced emotionVsNeutral;
    private ARNReduced negativeVsPositive;
    private String typeOfARN;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_EXTERNAL_FILE, PropertyType.STRING);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        externalFile = ps.getString(PROP_EXTERNAL_FILE, "nullpath");
    }


    /**
     * Method to initialize the Classifier.
     */
    public void initialize() {
        if (externalFile.equals("nullpath")) {
            System.out.println("HierarchicalARNReduced: no external file has been provided!");
            System.exit(1);
        } else {
            load(externalFile);
        }
    }


    /**
     * Main constructor of this classifier.
     */
    public HierarchicalARNReduced() {
        learningProcedure = "sentiment_hierarchical_cluster_analysis";
        typeOfARN = "ARNR";
        // Init the levels of the classifier.
        buildNetworks();
    }


    /**
     * Method to build the networks of this Hierarchical classifier.
     */
    private void buildNetworks() {
        emotionVsNeutral = new ARNReduced();
        negativeVsPositive = new ARNReduced();
        if (typeOfARN.equals("ARNR")) {
        } else if (typeOfARN.equals("ARNRwCOF")) {
            emotionVsNeutral.setCOF(true);
            negativeVsPositive.setCOF(true);
        } else if (typeOfARN.equals("ARNRwITF")) {
            emotionVsNeutral.setTermWeighingMeasure("itf");
            negativeVsPositive.setTermWeighingMeasure("itf");
        } else if (typeOfARN.equals("ARNRwITFwCOF")) {
            emotionVsNeutral.setTermWeighingMeasure("itf");
            negativeVsPositive.setTermWeighingMeasure("itf");
            emotionVsNeutral.setCOF(true);
            negativeVsPositive.setCOF(true);
        } else if (typeOfARN.equals("ARNRwLTFRF")) {
            emotionVsNeutral.setTermWeighingMeasure("ltfrf");
            negativeVsPositive.setTermWeighingMeasure("ltfrf");
        } else if (typeOfARN.equals("ARNRwLTFRFwCOF")) {
            emotionVsNeutral.setTermWeighingMeasure("ltfrf");
            negativeVsPositive.setTermWeighingMeasure("ltfrf");
            emotionVsNeutral.setCOF(true);
            negativeVsPositive.setCOF(true);
        }

    }


    /**
     * Method to set the learning procedure.
     *
     * @param lproc The learning procedure.
     */
    public void setLearningProcedure(String lproc) {
        learningProcedure = lproc;
    }


    /**
     * Method to set the type of ARN to use.
     *
     * @param type The type of ARN to use.
     */
    public void setTypeOfARN(String type) {
        typeOfARN = type;
        buildNetworks();
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        String mostProbableCategory = "neutral";
        // The FeatureBox contains text.
        if (inputFeatures.getText().equals("") == false) {
            // Decide the most probable category, i.e., the one with minimum risk, through the hierarchy of emotion
            if (learningProcedure.equals("sentiment_hierarchical_cluster_analysis")) {
                if (emotionVsNeutral.getCategory(inputFeatures).equals("neutral") == false) {
                    mostProbableCategory = negativeVsPositive.getCategory(inputFeatures);
                }
            }
        } else {
            System.out.println("HierarchicalARNReduced: Void input text to classify!");
            System.exit(1);
        }

        return mostProbableCategory;
    }


    /**
     * Void method to train required by the Classifier class.
     */
    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#trainingProcedure()
     */
    public void trainingProcedure() {
        ArrayList<FeatureBox> exampleFeatures = getListOfExampleFeatures();
        ArrayList<String> exampleCategories = getListOfExampleCategories();

        // Now it decides which training procedure to follow.
        if (learningProcedure.equals("sentiment_hierarchical_cluster_analysis")) {
            // Feed the classifiers of the hierarchy.
            for (int numExample = 0; numExample < exampleFeatures.size(); numExample++) {
                if (exampleCategories.get(numExample).equals("neutral")) {
                    emotionVsNeutral.inputTrainingExample(exampleFeatures.get(numExample), "neutral");
                } else {
                    emotionVsNeutral.inputTrainingExample(exampleFeatures.get(numExample), "emotion");
                    negativeVsPositive.inputTrainingExample(exampleFeatures.get(numExample),
                    exampleCategories.get(numExample));
                }
            }
            // Train the classifiers.
            emotionVsNeutral.train();
            negativeVsPositive.train();
        } else {
            System.out.println("HierarchicalARNReduced: no learning strategy specified!");
            System.exit(1);
        }
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#save(java.lang.String)
     */
    public void save(String path) {
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#load(java.lang.String)
     */
    public void load(String path) {
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#resetExamples()
     */
    @Override
    public void resetExamples() {
        super.resetExamples();
        buildNetworks();
    }

}

