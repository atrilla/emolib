/*
 * File    : NaiveBayes.java
 * Created : 03-Oct-2009
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

import emolib.classifier.machinelearning.density.Gaussian;
import emolib.classifier.machinelearning.density.ClassConditionalDensity;

import java.util.ArrayList;
import java.util.Hashtable;
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
 * The <i>NaiveBayes</i> is a Bayesian classifier operating in
 * the circumplex.
 *
 * <p>
 * The NaiveBayes assumes that the features are conditionally independent,
 * and models their distribution according to a given density form, e.g.,
 * the Gaussian/normal.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class NaiveBayes extends Classifier {

    /**
     * Property to determine the number of emotional dimensions the NaiveBayes deals with.
     */
    public final static String PROP_NUM_EMO_DIMS = "num_emo_dims";

    /**
     * Property to indicate a pre-trained classifier.
     */
    public final static String PROP_EXTERNAL_FILE = "external_file";

    /**
     * Property to account for prior probabilities.
     */
    public final static String PROP_PRIORS = "account_for_priors";


    private int numberOfEmotionalDimensions;

    private String externalFile;

    private boolean accountPriors;

    // Classifier specific parameters
    private ArrayList<ClassConditionalDensity> likelihoodValences;
    private ArrayList<ClassConditionalDensity> likelihoodActivations;
    private ArrayList<ClassConditionalDensity> likelihoodControls;
    private ArrayList<Float> priors;
    private ArrayList<String> emotionCategories;

    private String classConditionalDensityType;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_NUM_EMO_DIMS, PropertyType.INT);
        registry.register(PROP_EXTERNAL_FILE, PropertyType.STRING);
        registry.register(PROP_PRIORS, PropertyType.BOOLEAN);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        numberOfEmotionalDimensions = ps.getInt(PROP_NUM_EMO_DIMS, Integer.parseInt("3"));
        externalFile = ps.getString(PROP_EXTERNAL_FILE, "nullpath");
        accountPriors = ps.getBoolean(PROP_PRIORS, false);
    }


    /**
     * Method to initialize the Classifier.
     */
    public void initialize() {
        if (externalFile.equals("nullpath")) {
            System.out.println("NaiveBayes: no external file has been provided!");
            System.exit(1);
        } else {
            load(externalFile);
        }
    }


    /**
     * Main constructor of this classifier.
     */
    public NaiveBayes() {
        likelihoodValences = new ArrayList<ClassConditionalDensity>();
        likelihoodActivations = new ArrayList<ClassConditionalDensity>();
        likelihoodControls = new ArrayList<ClassConditionalDensity>();
        priors = new ArrayList<Float>();
        emotionCategories = new ArrayList<String>();
        numberOfEmotionalDimensions = 3;
        accountPriors = false;
        classConditionalDensityType = "gaussian";
    }


    /**
     * Method to set the number of emotional dimensions.
     *
     * @param numDims The number of emotional dimensions.
     */
    public void setNumberOfEmotionalDimensions(int numDims) {
        numberOfEmotionalDimensions = numDims;
    }


    /**
     * Method to set the NB to account for prior probabilities.
     *
     * @param flag The boolean flag.
     */
    public void setAccountForPriors(boolean flag) {
        accountPriors = flag;
    }


    /**
     * Method to set the type of class-conditional densities.
     *
     * By default this NB class assumes normal distributions.
     *
     * @param type The distribution.
     */
    public void setTypeOfLikelihoodDistribution(String type) {
        classConditionalDensityType = type;
    }


    /**
     * Method to get the type of class-conditional densities.
     *
     * By default this NB class assumes normal distributions.
     *
     * @return The type of distribution.
     *
     */
    public String getTypeOfLikelihoodDistribution() {
        return classConditionalDensityType;
    }


    /**
     * Function to retrieve the likelihood distributions for valence from this NB.
     *
     * @return The array of likelihood distributions for valence.
     */
    public ArrayList<ClassConditionalDensity> getLikelihoodValences() {
        return likelihoodValences;
    }


    /**
     * Function to retrieve the likelihood distributions for activation from this NB.
     *
     * @return The array of likelihood distributions for activation.
     */
    public ArrayList<ClassConditionalDensity> getLikelihoodActivations() {
        return likelihoodActivations;
    }


    /**
     * Function to retrieve the likelihood distributions for control from this NB.
     *
     * @return The array of likelihood distributions for control.
     */
    public ArrayList<ClassConditionalDensity> getLikelihoodControls() {
        return likelihoodControls;
    }


    /**
     * Open function to retrieve the posterior probabilities of having a single class given
     * a bunch of features.
     *
     * This function implements the Bayes Theorem.
     *
     * @param theClass The class.
     * @param theFeatures The features.
     *
     * @return The associated posterior probability P(c|x).
     */
    public float getPosteriorProbability(String theClass, FeatureBox theFeatures) {
        float posteriorProb = 0;
        Float aux;
        float allLikelihoods, value;
        for (int emoClass = 0; emoClass < emotionCategories.size(); emoClass++) {
            if (theClass.equals(emotionCategories.get(emoClass))) {
                if (accountPriors) {
                    aux = new Float(Math.log(priors.get(emoClass).doubleValue()));
                } else {
                    aux = new Float("0");
                }
                // This function outputs an actual probability function.
                // At least we have the valence
                if (numberOfEmotionalDimensions >= 1) {
                    value = likelihoodValences.get(emoClass).getLikelihood(theFeatures.getValence());
                    aux = new Float(aux.floatValue() + (float)Math.log((double)value));
                }
                // At least we have the activation
                if (numberOfEmotionalDimensions >= 2) {
                    value = likelihoodActivations.get(emoClass).getLikelihood(theFeatures.getActivation());
                    aux = new Float(aux.floatValue() + (float)Math.log((double)value));
                }
                // At least we have the control
                if (numberOfEmotionalDimensions >= 3) {
                    value = likelihoodControls.get(emoClass).getLikelihood(theFeatures.getControl());
                    aux = new Float(aux.floatValue() + (float)Math.log((double)value));
                }
                // Actual posterior probability. Baye's Theorem with logs unfoling.
                allLikelihoods = 0;
                float prev;
                for (int i = 0; i < emotionCategories.size(); i++) {
                    prev = 1;
                    if (numberOfEmotionalDimensions >= 1) {
                        prev *= likelihoodValences.get(i).getLikelihood(theFeatures.getValence());
                    }
                    if (numberOfEmotionalDimensions >= 2) {
                        prev *= likelihoodActivations.get(i).getLikelihood(theFeatures.getActivation());
                    }
                    if (numberOfEmotionalDimensions >= 3) {
                        prev *= likelihoodControls.get(i).getLikelihood(theFeatures.getControl());
                    }
                    allLikelihoods += prev;
                }
                posteriorProb = aux.floatValue() - (float)Math.log(allLikelihoods);
                break;
            }
        }

        aux = new Float(Math.exp(posteriorProb));
        return aux.floatValue();
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        String mostProbableCategory = "neutral";
        ArrayList<Float> posteriors = new ArrayList<Float>();
        if (inputFeatures.getNumberOfEmotionalDimensions() >= numberOfEmotionalDimensions) {
            for (int emoClass = 0; emoClass < emotionCategories.size(); emoClass++) {
                posteriors.add(new Float(getPosteriorProbability(emotionCategories.get(emoClass),
                inputFeatures)));
            }
            // Decide the most probable category
            mostProbableCategory = emotionCategories.get(0);
            float probability = posteriors.get(0).floatValue();
            for (int emoClass = 1; emoClass < posteriors.size(); emoClass++) {
                if (posteriors.get(emoClass).floatValue() > probability) {
                    probability = posteriors.get(emoClass).floatValue();
                    mostProbableCategory = emotionCategories.get(emoClass);
                }
            }
        } else {
            System.out.println("NaiveBayes: The number of input emotion dimensions is smaller than " +
                "the number of dimensions needed by this NB classifier!");
        }
        //
        return mostProbableCategory;
    }


    /**
     * Function to retrieve the emotion categories.
     *
     * This is used for integration purposes. Normally NB is a basic classifier, prone to
     * be enhanced by dome method.
     */
    public ArrayList<String> getEmotionCategories() {
        return emotionCategories;
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
        Hashtable<String, Integer> soleCategories = new Hashtable<String, Integer>();
        int categoryOrder = 0;
        int presentClassNumber;
        String presentCat;
        FeatureBox presentFeats;
        ArrayList<Integer> numberOfExamples = new ArrayList<Integer>();
        Float tempFloat;
        ArrayList<Float> meanValence = new ArrayList<Float>();
        ArrayList<Float> meanActivation = new ArrayList<Float>();
        ArrayList<Float> meanControl = new ArrayList<Float>();
        ArrayList<Float> stdValence = new ArrayList<Float>();
        ArrayList<Float> stdActivation = new ArrayList<Float>();
        ArrayList<Float> stdControl = new ArrayList<Float>();

        for (int num_examples = 0; num_examples < exampleFeatures.size(); num_examples++) {
            presentCat = exampleCategories.get(num_examples);
            presentFeats = exampleFeatures.get(num_examples);
            if (!soleCategories.containsKey(presentCat)) {
                soleCategories.put(presentCat, new Integer(categoryOrder));
                emotionCategories.add(new String(presentCat));
                presentClassNumber = categoryOrder;
                categoryOrder++;
                numberOfExamples.add(new Integer(1));
            } else {
                presentClassNumber = soleCategories.get(presentCat).intValue();
                numberOfExamples.set(presentClassNumber,
                new Integer(numberOfExamples.get(presentClassNumber).intValue() + 1));
            }
            // Add up the emotion dimensions
            // At least we have the valence
            if (numberOfEmotionalDimensions >= 1) {
                if (meanValence.size() > presentClassNumber) {
                    meanValence.set(presentClassNumber,
                    new Float(meanValence.get(presentClassNumber).floatValue() +
                        presentFeats.getValence()));
                } else {
                    meanValence.add(new Float(presentFeats.getValence()));
                }
            }
            // At least we have the activation
            if (numberOfEmotionalDimensions >= 2) {
                if (meanActivation.size() > presentClassNumber) {
                    meanActivation.set(presentClassNumber,
                    new Float(meanActivation.get(presentClassNumber).floatValue() +
                        presentFeats.getActivation()));
                } else {
                    meanActivation.add(new Float(presentFeats.getActivation()));
                }
            }
            // At least we have the control
            if (numberOfEmotionalDimensions >= 3) {
                if (meanControl.size() > presentClassNumber) {
                    meanControl.set(presentClassNumber,
                    new Float(meanControl.get(presentClassNumber).floatValue() +
                        presentFeats.getControl()));
                } else {
                    meanControl.add(new Float(presentFeats.getControl()));
                }
            }
        }

        // Calculate the means
        for (int numClass = 0; numClass < emotionCategories.size(); numClass++) {
            if (numberOfEmotionalDimensions >= 1) {
                meanValence.set(numClass, new Float(meanValence.get(numClass).floatValue() /
                numberOfExamples.get(numClass).floatValue()));
            }
            if (numberOfEmotionalDimensions >= 2) {
                meanActivation.set(numClass, new Float(meanActivation.get(numClass).floatValue() /
                numberOfExamples.get(numClass).floatValue()));
            }
            if (numberOfEmotionalDimensions >= 3) {
                meanControl.set(numClass, new Float(meanControl.get(numClass).floatValue() /
                numberOfExamples.get(numClass).floatValue()));
            }
        }

        // Calculate the prior probabilities.
        // Laplace smoothing (mu=3) as is indicated in Weka. This prevents classes with low generality from being
        // veted by the rest of the class a priori distributions.
        for (int numClass = 0; numClass < numberOfExamples.size(); numClass++) {
            // No smoothing
            priors.add(new Float(numberOfExamples.get(numClass).floatValue() / (float)exampleFeatures.size()));
            // Laplace smoothing with 1/3 of the total training examples
            // priors.add(new Float((numberOfExamples.get(numClass).floatValue() +
                // (float)exampleFeatures.size()/(float)9) / ((float)exampleFeatures.size() +
                // (float)exampleFeatures.size()/(float)3)));
        }

        // Calculate the stds
        for (int num_examples = 0; num_examples < exampleFeatures.size(); num_examples++) {
            presentCat = exampleCategories.get(num_examples);
            presentFeats = exampleFeatures.get(num_examples);
            presentClassNumber = soleCategories.get(presentCat).intValue();
            // At least we have the valence
            if (numberOfEmotionalDimensions >= 1) {
                tempFloat = new Float(presentFeats.getValence() -
                    meanValence.get(presentClassNumber).floatValue());
                if (stdValence.size() > presentClassNumber) {
                    stdValence.set(presentClassNumber,
                    new Float(stdValence.get(presentClassNumber).doubleValue() +
                        Math.pow(tempFloat.doubleValue(), 2)));
                } else {
                    stdValence.add(new Float(Math.pow(tempFloat.doubleValue(), 2)));
                }
            }
            // At least we have the activation
            if (numberOfEmotionalDimensions >= 2) {
                tempFloat = new Float(presentFeats.getActivation() -
                    meanActivation.get(presentClassNumber).floatValue());
                if (stdActivation.size() > presentClassNumber) {
                    stdActivation.set(presentClassNumber,
                        new Float(stdActivation.get(presentClassNumber).doubleValue() +
                        Math.pow(tempFloat.doubleValue(), 2)));
                } else {
                    stdActivation.add(new Float(Math.pow(tempFloat.doubleValue(), 2)));
                }
            }
            // At least we have the control
            if (numberOfEmotionalDimensions >= 3) {
                tempFloat = new Float(presentFeats.getControl() -
                    meanControl.get(presentClassNumber).floatValue());
                if (stdControl.size() > presentClassNumber) {
                    stdControl.set(presentClassNumber,
                    new Float(stdControl.get(presentClassNumber).doubleValue() +
                        Math.pow(tempFloat.doubleValue(), 2)));
                } else {
                    stdControl.add(new Float(Math.pow(tempFloat.doubleValue(), 2)));
                }
            }
        }
        for (int numClass = 0; numClass < emotionCategories.size(); numClass++) {
            if (numberOfEmotionalDimensions >= 1) {
                stdValence.set(numClass, new Float(stdValence.get(numClass).floatValue() /
                (numberOfExamples.get(numClass).floatValue() - 1)));
                stdValence.set(numClass, new Float(Math.sqrt(stdValence.get(numClass).doubleValue())));
            }
            if (numberOfEmotionalDimensions >= 2) {
                stdActivation.set(numClass, new Float(stdActivation.get(numClass).floatValue() /
                (numberOfExamples.get(numClass).floatValue() - 1)));
                stdActivation.set(numClass, new Float(Math.sqrt(stdActivation.get(numClass).doubleValue())));
            }
            if (numberOfEmotionalDimensions >= 3) {
                stdControl.set(numClass, new Float(stdControl.get(numClass).floatValue() /
                (numberOfExamples.get(numClass).floatValue() - 1)));
                stdControl.set(numClass, new Float(Math.sqrt(stdControl.get(numClass).doubleValue())));
            }
        }

        // Build the likelihood distributions
        for (int numClass = 0; numClass < emotionCategories.size(); numClass++) {
            if (classConditionalDensityType.equals("gaussian")) {
                if (numberOfEmotionalDimensions >= 1) {
                    likelihoodValences.add(new Gaussian(meanValence.get(numClass).floatValue(),
                    stdValence.get(numClass).floatValue()));
                }
                if (numberOfEmotionalDimensions >= 2) {
                    likelihoodActivations.add(new Gaussian(meanActivation.get(numClass).floatValue(),
                    stdActivation.get(numClass).floatValue()));
                }
                if (numberOfEmotionalDimensions >= 3) {
                    likelihoodControls.add(new Gaussian(meanControl.get(numClass).floatValue(),
                    stdControl.get(numClass).floatValue()));
                }
            }
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
        likelihoodValences = new ArrayList<ClassConditionalDensity>();
        likelihoodActivations = new ArrayList<ClassConditionalDensity>();
        likelihoodControls = new ArrayList<ClassConditionalDensity>();
        priors = new ArrayList<Float>();
        emotionCategories = new ArrayList<String>();
    }

}

