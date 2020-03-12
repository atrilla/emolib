/*
 * File    : ConfusionMatrixCalculator.java
 * Created : 15-Feb-2009
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

package emolib.util.eval;

import java.io.*;
import java.util.ArrayList;

/**
 * The <i>ConfusionMatrixCalculator</i> class calculates the confusion matrix
 * associated to two input datasets.
 *
 * <p>
 * The two input datasets are entered as two command line parameters that
 * name the two files that contain the datasets.
 * The first one corresponds to the original categorised dataset while the
 * second one corresponds to the prediction dataset. One third parameter is
 * provided indicating the different labels (categories) that are to be
 * found in the datasets. These categories have to be hyphen-separated.
 * It is preferred to be done like this because in
 * case that in the datasets one of the expected categories didn't appear,
 * this would be rather confusing for the expert interpreting
 * the results. Note that at least two categories must be provided.
 * </p>
 * <p>
 * The ConfusionMatrixCalculator class contains a main method to execute the
 * class directly with the appropriate command line parameters.
 * In case a quick reference of its usage is needed, the class responds to the
 * typical help queries ("-h" or "--help") by showing the program's synopsis.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class ConfusionMatrixCalculator {

    ArrayList<ArrayList<Float>> theMatrix;
    ArrayList<String> theCategories;
    int totalEvaluations;


    /**
     * Void constructor.
     */
    public ConfusionMatrixCalculator() {
    }


    /**
     * Method to initialize the confusion matrix.
     * This method requires a string with the different categories separated by a hyphen.
     *
     * @param inputCategories The categories.
     */
    public void initialize(String inputCategories) {
        int catCounter, catDimentions;

        String[] categories = inputCategories.split("-");
        theCategories = new ArrayList<String>(categories.length);
        for (catCounter = 0; catCounter < categories.length; catCounter++) {
            theCategories.add(catCounter, categories[catCounter].trim());
        }

        int numberOfCategories = categories.length;
        theMatrix = new ArrayList<ArrayList<Float>>(numberOfCategories);
        for (catCounter = 0; catCounter < numberOfCategories; catCounter++) {
            theMatrix.add(catCounter, new ArrayList<Float>(numberOfCategories));
            for (catDimentions = 0; catDimentions < numberOfCategories; catDimentions++) {
                theMatrix.get(catCounter).add(catDimentions, new Float("0.0"));
            }
        }

        totalEvaluations = 0;
    }


    /**
     * Method to evaluate the datasets and construct the confusion matrix.
     *
     * @param categoryOriginalFile A category from the first file.
     * @param categoryPredictionFile A category from the second file.
     */
    public void evaluate(String categoryOriginalFile, String categoryPredictionFile) {
        float tempFloat;
        int catOriginalFileCounter, catPredictionFileCounter;

        for (catOriginalFileCounter = 0; catOriginalFileCounter < theCategories.size(); catOriginalFileCounter++) {
            if (categoryOriginalFile.equals(theCategories.get(catOriginalFileCounter))) {
                for (catPredictionFileCounter = 0; catPredictionFileCounter < theCategories.size();
                catPredictionFileCounter++) {
                    if (categoryPredictionFile.equals(theCategories.get(catPredictionFileCounter))) {
                        tempFloat = theMatrix.get(catOriginalFileCounter).get(catPredictionFileCounter).floatValue() +
                            Float.parseFloat("1.0");
                        theMatrix.get(catOriginalFileCounter).set(catPredictionFileCounter, new Float(tempFloat));
                        break;
                    }
                }
                break;
            }
        }
        totalEvaluations++;
    }


    /**
     * Method to print the resulting confusion matrix.
     */
    public void printConfusionMatrix() {
        if (totalEvaluations == 0) {
            System.out.println("ConfusionMatrixCalculator: since there is no input data, the confusion " +
                "matrix can't be produced!");
            System.exit(1);
        } else {
            System.out.println("");
            System.out.println("Resulting confusion matrix:");
            System.out.println("");
            int elementsInArray, catCounter;
            float numericValue;
            float totalValues;
            String value;
            for (catCounter = 0; catCounter < theCategories.size(); catCounter++) {
                if (theCategories.get(catCounter).length() > 7) {
                    System.out.print("\t" + theCategories.get(catCounter).substring(0, 7));
                } else {
                    System.out.print("\t" + theCategories.get(catCounter));
                }
            }
            System.out.println("");
            for (catCounter = 0; catCounter < theCategories.size(); catCounter++) {
                if (theCategories.get(catCounter).length() > 7) {
                    System.out.print(theCategories.get(catCounter).substring(0, 7));
                } else {
                    System.out.print(theCategories.get(catCounter));
                }
                totalValues = 0;
                // Calculates the total values for a category in order to compute the percentages.
                for (elementsInArray = 0; elementsInArray < theCategories.size(); elementsInArray++) {
                    numericValue = theMatrix.get(catCounter).get(elementsInArray).floatValue();
                    totalValues += numericValue;
                }
                for (elementsInArray = 0; elementsInArray < theCategories.size(); elementsInArray++) {
                    numericValue = theMatrix.get(catCounter).get(elementsInArray).floatValue();
                    numericValue = (numericValue / totalValues) * Float.parseFloat("100");
                    value = Float.toString(numericValue);
                    if (value.length() > 5) {
                        value = value.substring(0, 5);
                    }
                    System.out.print("\t" + value);
                }
                System.out.println("");
            }
            System.out.println("");
        }
    }


    /**
     * Prints the synopsis.
     */
    public void printSynopsis() {
        System.out.println("ConfusionMatrixCalculator usage:");
        System.out.println("\tjava " + "[-Xmx256m] -cp EmoLib-X.Y.Z.jar emolib.util.eval.ConfusionMatrixCalculator " +
            "INPUT_DATASET_ORIGINAL INPUT_DATASET_PREDICTION CATEGORY_1-CATEGORY_2[-CATEGORY_N]");
    }


    /**
     * The main method of the ConfusionMatrixCalculator application.
     *
     * @param args The input arguments. The first one corresponds to the original categorized
     * file, the second one corresponds to the prediction file and the third one corresponds
     * to the categories the system is allowed to deal with.
     */
    public static void main(String[] args) throws Exception {
        ConfusionMatrixCalculator calculator = new ConfusionMatrixCalculator();

        if (args.length == 3) {
            BufferedReader originalFile = new BufferedReader(new FileReader(args[0]));
            BufferedReader predictionFile = new BufferedReader(new FileReader(args[1]));

            calculator.initialize(args[2]);

            String lineOriginalFile = originalFile.readLine();
            String linePredictionFile = predictionFile.readLine();
            while ((lineOriginalFile != null) && (linePredictionFile != null)) {
                calculator.evaluate(lineOriginalFile.trim(), linePredictionFile.trim());
                lineOriginalFile = originalFile.readLine();
                linePredictionFile = predictionFile.readLine();
            }
            calculator.printConfusionMatrix();
        } else if (args.length == 1) {
            if (args[0].equals("-h") || args[0].equals("--help")) {
                calculator.printSynopsis();
            } else {
                System.out.println("ConfusionMatrixCalculator: Please enter the correct parameters!");
                System.out.println("");
                calculator.printSynopsis();
            }
        } else {
            System.out.println("ConfusionMatrixCalculator: Please enter the correct parameters!");
            System.out.println("");
            calculator.printSynopsis();
        }
    }

}

