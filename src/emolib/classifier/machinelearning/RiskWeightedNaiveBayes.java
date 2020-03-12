/*
 * File    : RiskWeightedNaiveBayes.java
 * Created : 04-Nov-2009
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

package emolib.classifier.machinelearning;

import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;
//import java.util.Hashtable;
//import java.util.List;
//import java.io.*;

import emolib.util.conf.*;
import emolib.util.proc.*;
import emolib.classifier.Classifier;
import emolib.classifier.FeatureBox;
import emolib.classifier.machinelearning.density.ClassConditionalDensity;
import emolib.classifier.machinelearning.density.Gaussian;

//import org.jdom.*;
//import org.jdom.output.*;
//import org.jdom.input.*;

/**
 * The <i>RiskWeightedNaiveBayes</i> is a Naive Bayes classifier
 * that accounts for the hierarchy of emotion.
 *
 * <p>
 * It enhances a simple Naive Bayes classifier by
 * weighting the risk of a wrong decision with the distance to the
 * centroid of the predicted sentiment. This classifier is inspired
 * in the Minimum-Error-Rate Classification (Duda, et al., 2001).
 * </p>
 * <p>
 * --<br/>
 * (Duda, et al., 2001) Duda, R.O., Hart, P.E. and Stork, D.G., 
 * "Pattern Classification", New York: John Wiley &amp; Sons, 2001, 
 * ISBN: 0-471-05669-3 
 * </p>
 *
 * @see emolib.classifier.machinelearning.NaiveBayes
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class RiskWeightedNaiveBayes extends Classifier {

    /**
     * Property to determine the number of emotional dimensions the NaiveBayes deals with.
     */
    public final static String PROP_NUM_EMO_DIMS = "num_emo_dims";

    /**
     * Property to indicate a pre-trained classifier.
     */
    public final static String PROP_EXTERNAL_FILE = "external_file";

    /**
     * Property to indicate a pre-trained Naive Bayes classifier.
     */
    public final static String PROP_NB_EXTERNAL_FILE = "nb_external_file";


    private int numberOfEmotionalDimensions;
    private String externalFile;
    private String nbExternalFile;
    private float momentum;
    private float threshold;
    private String learningProcedure;

    // Classifier specific parameters
    private NaiveBayes theNB;
    private ArrayList<ArrayList<Float>> lossCosts;
    private ArrayList<String> emotionCategories;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_NUM_EMO_DIMS, PropertyType.INT);
        registry.register(PROP_EXTERNAL_FILE, PropertyType.STRING);
        registry.register(PROP_NB_EXTERNAL_FILE, PropertyType.STRING);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        numberOfEmotionalDimensions = ps.getInt(PROP_NUM_EMO_DIMS, Integer.parseInt("3"));
        externalFile = ps.getString(PROP_EXTERNAL_FILE, "nullpath");
        nbExternalFile = ps.getString(PROP_NB_EXTERNAL_FILE, "nullpath");
    }


    /**
     * Method to initialize the Classifier.
     */
    public void initialize() {
        if (externalFile.equals("nullpath")) {
            System.out.println("RiskWeightedNaiveBayes: no external file has been provided!");
            System.exit(1);
        } else {
            if (nbExternalFile.equals("nullpath")) {
                System.out.println("RiskWeightedNaiveBayes: no external file has been provided for the Naive Bayes!");
                System.exit(1);
            } else {
                theNB.load(nbExternalFile);
                load(externalFile);
            }
        }
    }


    /**
     * Main constructor of this classifier.
     */
    public RiskWeightedNaiveBayes() {
        theNB = new NaiveBayes();
        lossCosts = new ArrayList<ArrayList<Float>>();
        emotionCategories = new ArrayList<String>();
        numberOfEmotionalDimensions = 3;
        momentum = (new Float("0.1")).floatValue();
        threshold = (new Float("0.000001")).floatValue();
        learningProcedure = "normalized_euclidean_emotion_distance";
    }


    /**
     * Method to set the number of emotional dimensions.
     *
     * @param numDims The number of emotional dimensions.
     */
    public void setNumberOfEmotionalDimensions(int numDims) {
        numberOfEmotionalDimensions = numDims;
        theNB.setNumberOfEmotionalDimensions(numDims);
    }


    /**
     * Method to set the momentum.
     *
     * @param mom The momentum.
     */
    public void setMomentum(float mom) {
        momentum = mom;
    }


    /**
     * Method to set the threshold.
     *
     * @param phi The threshold.
     */
    public void setThreshold(float phi) {
        threshold = phi;
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
     * Function to retrieve the risk associated to deciding on a class 'c'.
     *
     * @param feat The feature vector.
     * @param losses The loss cost vector related to class 'c', i.e., $\lambda^c$.
     *
     * @return The associated risk.
     */
    public float getRisk(FeatureBox feat, ArrayList<Float> losses) {
        float aux = 0;
        for (int emoClass = 0; emoClass < emotionCategories.size(); emoClass++) {
            aux += losses.get(emoClass).floatValue() *
                theNB.getPosteriorProbability(emotionCategories.get(emoClass), feat);
        }

        return aux;
    }



    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        String mostProbableCategory = "neutral";
        ArrayList<Float> risks = new ArrayList<Float>();
        if (inputFeatures.getNumberOfEmotionalDimensions() >= numberOfEmotionalDimensions) {
            for (int emoClassRisk = 0; emoClassRisk < emotionCategories.size(); emoClassRisk++) {
                risks.add(getRisk(inputFeatures, lossCosts.get(emoClassRisk)));
            }
            // Decide the most probable category, i.e., the one with minimum risk
            mostProbableCategory = emotionCategories.get(0);
            float probability = risks.get(0).floatValue();
            for (int emoClass = 1; emoClass < risks.size(); emoClass++) {
                if (risks.get(emoClass).floatValue() < probability) {
                    probability = risks.get(emoClass).floatValue();
                    mostProbableCategory = emotionCategories.get(emoClass);
                }
            }
        } else {
            System.out.println("RiskWeightedNaiveBayes: The number of input emotion dimensions is smaller than " +
                "the number of dimensions needed by this classifier!");
            System.exit(1);
        }

        return mostProbableCategory;
    }


    /**
     * Method to scramble the examples in case that some items are more representative than
     * others.
     *
     * @param feats Example features.
     * @param cats Example categories.
     */
    private void scrambleExamples(ArrayList<FeatureBox> feats, ArrayList<String> cats) {
        Random rand = new Random();
        // Limit of examples
        int n = feats.size();
        int randNumOne;
        int randNumTwo;
        FeatureBox tempFeat;
        String tempCat;
        // Swap exemplars
        for (int num = 0; num < rand.nextInt(n); num++) {
            randNumOne = rand.nextInt(n);
            tempFeat = feats.get(randNumOne);
            tempCat = cats.get(randNumOne);
            randNumTwo = rand.nextInt(n);
            feats.set(randNumOne, feats.get(randNumTwo));
            cats.set(randNumOne, cats.get(randNumTwo));
            feats.set(randNumTwo, tempFeat);
            cats.set(randNumTwo, tempCat);
        }
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

        // Training of the NB
        for (int num_examples = 0; num_examples < exampleFeatures.size(); num_examples++) {
            theNB.inputTrainingExample(exampleFeatures.get(num_examples), exampleCategories.get(num_examples));
        }
        theNB.train();
        emotionCategories = (ArrayList<String>)theNB.getEmotionCategories().clone();

        // Now it decides which training procedure to follow.
        if (learningProcedure.equals("three_sentiment_heuristic")) {
            // All errors count the same.
            ArrayList<Float> tempLoss;
            for (int numClass = 0; numClass < emotionCategories.size(); numClass++) {
                tempLoss = new ArrayList<Float>();
                if (emotionCategories.get(numClass).equals("neutral")) {
                    for (int cnum = 0; cnum < emotionCategories.size(); cnum++) {
                        if (cnum == numClass) {
                            tempLoss.add(new Float("0"));
                        } else {
                            tempLoss.add(new Float("0.5"));
                        }
                    }
                } else {
                    for (int cnum = 0; cnum < emotionCategories.size(); cnum++) {
                        if (cnum == numClass) {
                            tempLoss.add(new Float("0"));
                        } else if (emotionCategories.get(cnum).equals("neutral")) {
                            tempLoss.add(new Float("0.5"));
                        } else {
                            tempLoss.add(new Float("1"));
                        }
                    }
                }
                lossCosts.add(tempLoss);
            }
        } else if (learningProcedure.equals("three_sentiment_gradient_descent")) {
            String presentCat;
            FeatureBox presentFeats;
            ArrayList<ArrayList<ArrayList<Float>>> repetitions = new ArrayList<ArrayList<ArrayList<Float>>>();

            // Arbitrarily, 10 loss cost vector repetitions are averaged.
            int timeStep;
            ArrayList<ArrayList<Float>> bigTempLoss;
            ArrayList<Float> tempLoss = new ArrayList<Float>();
            ArrayList<Float> memory;
            ArrayList<Float> auxMemo;
            float targetRisk, variation = 0;
            boolean firstTime;

            for (int rep = 0; rep < 10; rep++) {
                scrambleExamples(exampleFeatures, exampleCategories);
                // Temporal lambda (all classes), one possible solution
                bigTempLoss = new ArrayList<ArrayList<Float>>();
                for (int numClass = 0; numClass < emotionCategories.size(); numClass++) {
                    timeStep = 0;
                    // Temporal lambda^c. This is the one to got hrough the gradient descent.
                    tempLoss = new ArrayList<Float>();
                    memory = new ArrayList<Float>();
                    // Initialise to random numbers.
                    Random rand = new Random();
                    for (int cnum = 0; cnum < emotionCategories.size(); cnum++) {
                        tempLoss.add(new Float(rand.nextFloat()));
                        memory.add(new Float(tempLoss.get(cnum).floatValue()));
                    }
                    firstTime = true;
                    // Single sample gradient descent.
                    // tempLoss evolves, auxMemo is a sample of tempLoss and memory is a z^{-1} register.
                    variation = 1;
                    while (variation > threshold) {
                        timeStep++;
                        for (int num_examples = 0; num_examples < exampleFeatures.size(); num_examples++) {
                            auxMemo = (ArrayList<Float>)tempLoss.clone();
                            presentCat = exampleCategories.get(num_examples);
                            presentFeats = exampleFeatures.get(num_examples);
                            // This is adapted to 3 class sentiment classification.
                            if (presentCat.equals(emotionCategories.get(numClass))) {
                                // Target risk = 0
                                targetRisk = 0;
                            } else if (presentCat.equals("neutral")) {
                                // Target risk = 0.5
                                targetRisk = (new Float("0.5")).floatValue();
                            } else {
                                // Target risk = 1
                                targetRisk = 1;
                            }
                            for (int k = 0; k < emotionCategories.size(); k++) {
                                variation = -(1 - momentum) * (1 / timeStep) * 2 * (getRisk(presentFeats, auxMemo) -
                                    targetRisk) *
                                    theNB.getPosteriorProbability(emotionCategories.get(k), presentFeats) + momentum *
                                    (auxMemo.get(k).floatValue() - memory.get(k).floatValue());
                                tempLoss.set(k, new Float(auxMemo.get(k).floatValue() + variation));
                            }
                            if (firstTime) {
                                firstTime = false;
                            } else {
                                memory = (ArrayList<Float>)auxMemo.clone();
                            }
                        }
                    }
                    bigTempLoss.add(tempLoss);
                }
                repetitions.add(bigTempLoss);
            }
            // Init the lossCosts matrix
            for (int i = 0; i < emotionCategories.size(); i++) {
                tempLoss = new ArrayList<Float>();
                for (int j = 0; j < emotionCategories.size(); j++) {
                    tempLoss.add(new Float("0"));
                }
                lossCosts.add(tempLoss);
            }
            for (int rep = 0; rep < repetitions.size(); rep++) {
                // Retrieve one possible solution
                bigTempLoss = repetitions.get(rep);
                for (int theClass = 0; theClass < bigTempLoss.size(); theClass++) {
                    tempLoss = bigTempLoss.get(theClass);
                    for (int k = 0; k < tempLoss.size(); k++) {
                        lossCosts.get(theClass).set(k, new Float(lossCosts.get(theClass).get(k).floatValue() +
                            tempLoss.get(k).floatValue()));
                    }
                }
            }
            // Average the possible solutions
            for (int theClass = 0; theClass < lossCosts.size(); theClass++) {
                for (int k = 0; k < tempLoss.size(); k++) {
                    lossCosts.get(theClass).set(k, new Float(lossCosts.get(theClass).get(k).floatValue() /
                        (float)repetitions.size()));
                }
            }
        } else if (learningProcedure.equals("normalized_euclidean_emotion_distance")) {
            if (theNB.getTypeOfLikelihoodDistribution().equals("gaussian")) {
                // Initialise distance matrix
                ArrayList<ArrayList<Float>> distanceMatrix = new ArrayList<ArrayList<Float>>();
                ArrayList<Float> rowDistanceMatrix;
                for (int i = 0; i < emotionCategories.size(); i++) {
                    rowDistanceMatrix = new ArrayList<Float>();
                    for (int j = 0; j < emotionCategories.size(); j++) {
                        rowDistanceMatrix.add(new Float("0"));
                    }
                    distanceMatrix.add(rowDistanceMatrix);
                }
                // Retrieve the Gaussian for each emotion category
                ArrayList<ClassConditionalDensity> likelihoodValences = null;
                ArrayList<ClassConditionalDensity> likelihoodActivations = null;
                ArrayList<ClassConditionalDensity> likelihoodControls = null;
                // At least we have the valence
                if (numberOfEmotionalDimensions >= 1) {
                    likelihoodValences = theNB.getLikelihoodValences();
                }
                // At least we have the activation
                if (numberOfEmotionalDimensions >= 2) {
                    likelihoodActivations = theNB.getLikelihoodActivations();
                }
                // At least we have the control
                if (numberOfEmotionalDimensions >= 3) {
                    likelihoodControls = theNB.getLikelihoodControls();
                }
                // Calculate the euclidean distances among the classes.
                double maxLength = 0;
                Gaussian firstGaussian, secondGaussian;
                double calculatedDistance;
                Float tempFloat;
                double two = Double.parseDouble("2");
                int rowClass = 0;
                int colClass = 0;
                while (rowClass < emotionCategories.size()) {
                    while (colClass < emotionCategories.size()) {
                        // The distance from class 'c' to itself is zero, leave the default 0.
                        if (rowClass != colClass) {
                            calculatedDistance = 0;
                            // At least we have the valence
                            if (numberOfEmotionalDimensions >= 1) {
                                firstGaussian = (Gaussian)likelihoodValences.get(rowClass);
                                secondGaussian = (Gaussian)likelihoodValences.get(colClass);
                                tempFloat = new Float(Math.abs(firstGaussian.getMean() - secondGaussian.getMean()));
                                calculatedDistance += Math.pow(tempFloat.doubleValue(), two);
                            }
                            // At least we have the activation
                            if (numberOfEmotionalDimensions >= 2) {
                                firstGaussian = (Gaussian)likelihoodActivations.get(rowClass);
                                secondGaussian = (Gaussian)likelihoodActivations.get(colClass);
                                tempFloat = new Float(Math.abs(firstGaussian.getMean() - secondGaussian.getMean()));
                                calculatedDistance += Math.pow(tempFloat.doubleValue(), two);
                            }
                            // At least we have the control
                            if (numberOfEmotionalDimensions >= 3) {
                                firstGaussian = (Gaussian)likelihoodControls.get(rowClass);
                                secondGaussian = (Gaussian)likelihoodControls.get(colClass);
                                tempFloat = new Float(Math.abs(firstGaussian.getMean() - secondGaussian.getMean()));
                                calculatedDistance += Math.pow(tempFloat.doubleValue(), two);
                            }
                            calculatedDistance = Math.sqrt(calculatedDistance);
                            // The max distance is kept for normalizations afterwards.
                            if (calculatedDistance > maxLength) {
                                maxLength = calculatedDistance;
                            }
                            distanceMatrix.get(rowClass).set(colClass, new Float(Double.toString(calculatedDistance)));
                            distanceMatrix.get(colClass).set(rowClass, new Float(Double.toString(calculatedDistance)));
                        }
                        colClass++;
                    }
                    rowClass++;
                    colClass = rowClass;
                }
                // Normalise the distance matrix (division by maxLength)
                tempFloat = new Float(maxLength);
                float maxl = tempFloat.floatValue();
                for (int i = 0; i < emotionCategories.size(); i++) {
                    for (int j = 0; j < emotionCategories.size(); j++) {
                        distanceMatrix.get(i).set(j, new Float(distanceMatrix.get(i).get(j).floatValue() / maxl));
                    }
                }
                // Assign the matrix of interest.
                lossCosts = distanceMatrix;
            } else {
                System.out.println("RiskWeightedNaiveBayes: the NB does not work with Gaussians! No distances found!");
                System.exit(1);
            }
        } else {
            System.out.println("RiskWeightedNaiveBayes: no learning strategy specified!");
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
        theNB.resetExamples();
        emotionCategories = new ArrayList<String>();
        lossCosts = new ArrayList<ArrayList<Float>>();
    }

}

