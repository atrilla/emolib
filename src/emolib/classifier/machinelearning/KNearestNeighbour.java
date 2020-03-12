/*
 * File    : KNearestNeighbour.java
 * Created : 26-Feb-2009
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

import java.util.ArrayList;

import emolib.util.conf.*;
import emolib.util.proc.*;
import emolib.classifier.Classifier;
import emolib.classifier.FeatureBox;

/**
 * The <i>KNearestNeighbour</i> is an example-based classifier
 * that uses the k-Nearest Neighbour method.
 *
 * <p>
 * Computes the `k' nearest elements according to a distance measure
 * (e.g., euclidean) in the feature space (e.g., the circumplex). 
 * Decides the resulting affective
 * category according to the majority vote among these `k' elements.
 * The number of emotional dimensions of use has to be specified.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class KNearestNeighbour extends Classifier {

    /**
     * Property to determine the number of emotional dimensions the KNN deals with.
     */
    public final static String PROP_NUM_EMO_DIMS = "num_emo_dims";


    private int theK;
    private int numberOfEmotionalDimensions;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_NUM_EMO_DIMS, PropertyType.INT);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        numberOfEmotionalDimensions = ps.getInt(PROP_NUM_EMO_DIMS, Integer.parseInt("2"));
    }


    /**
     * Main constructor of this example-based classifier.
     */
    public KNearestNeighbour() {
        theK = 1;
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
     * Method to set the number of neighbors to consider (k) when evaluating this
     * classifier.
     * If this method is not invoked the k-NN takes one neighbor (k=1).
     *
     * @param number The number of neighbors.
     */
    public void setNumberOfNeighbours(int number) {
        theK = number;
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        String majorityCategory = "";

        // We cannot deal with more dimensions than we have
        if (numberOfEmotionalDimensions <= inputFeatures.getNumberOfEmotionalDimensions()) {
            ArrayList<Float> distances = new ArrayList<Float>();
            double calculatedDistance;
            Float tempFloat;
            double two = Double.parseDouble("2");

            ArrayList<FeatureBox> exampleFeatures = getListOfExampleFeatures();
            ArrayList<String> exampleCategories = getListOfExampleCategories();

            // It is supposed that if the program flow gets here, the input test features
            // will have the same dimensions as the training features
            int counter;
            // All the examples are checked
            for (counter = 0; counter < exampleFeatures.size(); counter++) {
                calculatedDistance = 0;
                // At least we have the valence
                if (numberOfEmotionalDimensions >= 1) {
                    tempFloat = new Float(inputFeatures.getValence() -
                        exampleFeatures.get(counter).getValence());
                    calculatedDistance += Math.pow(tempFloat.doubleValue(), two);
                }
                // At least we have the activation
                if (numberOfEmotionalDimensions >= 2) {
                    tempFloat = new Float(inputFeatures.getActivation() -
                        exampleFeatures.get(counter).getActivation());
                    calculatedDistance += Math.pow(tempFloat.doubleValue(), two);
                }
                // At least we have the control
                if (numberOfEmotionalDimensions >= 3) {
                    tempFloat = new Float(inputFeatures.getControl() -
                        exampleFeatures.get(counter).getControl());
                    calculatedDistance += Math.pow(tempFloat.doubleValue(), two);
                }
                calculatedDistance = Math.sqrt(calculatedDistance);
                distances.add(new Float(Double.toString(calculatedDistance)));
            }
            // Search for the smallest distances (the nearest neighbors).
            float smallestValue;
            int smallestIndex;
            int kIteration;
            ArrayList<String> nearestCategories = new ArrayList<String>(theK);
            ArrayList<String> categories = (ArrayList<String>)exampleCategories.clone();

            if (theK < distances.size()) {
                for (kIteration = 0; kIteration < theK; kIteration++) {
                    smallestValue = distances.get(0).floatValue();
                    smallestIndex = 0;
                    for (counter = 1; counter < distances.size(); counter++) {
                        if (distances.get(counter).floatValue() < smallestValue) {
                            smallestValue = distances.get(counter).floatValue();
                            smallestIndex = counter;
                        }
                    }
                    nearestCategories.add(new String(categories.get(smallestIndex)));
                    distances.remove(smallestIndex);
                    categories.remove(smallestIndex);
                }
                // Now we have an ordered list containing the 'k' smallest distances and
                // their corresponding categories
                // It's time to get the majority class
                ArrayList<String> categoryToReturn = new ArrayList<String>(theK);
                ArrayList<Integer> categoryScore = new ArrayList<Integer>(theK);
                categoryToReturn.add(nearestCategories.get(0));
                categoryScore.add(new Integer(1));
                boolean found;
                int arrayRunner;
                for (kIteration = 1; kIteration < nearestCategories.size(); kIteration++) {
                    found = false;
                    for (arrayRunner = 0; arrayRunner < categoryToReturn.size(); arrayRunner++) {
                        if (nearestCategories.get(kIteration).equals(categoryToReturn.get(arrayRunner))) {
                            categoryScore.set(arrayRunner, new Integer(categoryScore.get(arrayRunner) + 1));
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        categoryToReturn.add(nearestCategories.get(kIteration));
                        categoryScore.add(new Integer(1));
                    }
                }
                // Now the majority category has to be returned.
                majorityCategory = categoryToReturn.get(0);
                int majorityScore = categoryScore.get(0);
                for (arrayRunner = 1; arrayRunner < categoryToReturn.size(); arrayRunner++) {
                    if (categoryScore.get(arrayRunner) > majorityScore) {
                        majorityScore = categoryScore.get(arrayRunner);
                        majorityCategory = categoryToReturn.get(arrayRunner);
                    }
                }
            } else {
                System.out.println("KNearestNeighbour: `k' smaller than the number of training examples!");
                System.exit(1);
            }
        } else {
            System.out.println("KNearestNeighbour: lack of emotional dimensions!");
        }
        return majorityCategory;
    }


    /**
     * Void method to train required by the Classifier class.
     */
    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#trainingProcedure()
     */
    public void trainingProcedure() {
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

}

