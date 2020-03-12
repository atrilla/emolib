/*
 * File    : SemevalCorpusCategorizerNN.java
 * Created : 27-Feb-2009
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

package emolib.util.eval.semeval;

import emolib.classifier.machinelearning.KNearestNeighbour;
import emolib.classifier.FeatureBox;

import java.io.*;
import java.util.ArrayList;

/**
 * The <i>SemevalCorpusCategorizerNN</i> class decides the category for each
 * headline in the Semeval'07 corpus according to the Nearest Neighbour
 * algorithm.
 *
 * <p>
 * This class (with a main process) takes the six emotional dimensions
 * evaluated in the Semeval'07 task and treats them as vectors, which are
 * then summed in order to obtain a representation of the sentence in
 * question in the emotional plane (the circumplex). This representation is
 * then compared with the emotions accounted in EmoLib using the NN
 * procedure and as a result a category is decided.
 * </p>
 * <p>
 * This application requires that an original basic emotions distribution is
 * stated, so either the the Whissell reference values, the Scherer reference
 * values or the Russell reference values have to be previously decided.
 * As a result, the output file contains one line for each headline, and each
 * line contains the sentence emotional features as well as the corresponding
 * label.
 * In case a quick reference of its usage is needed, the class responds to the
 * typical help queries ("-h" or "--help") by showing the program's synopsis.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class SemevalCorpusCategorizerNN {

    private KNearestNeighbour theNN;

    private ArrayList<Float> theValences;
    private ArrayList<Float> theActivations;


    /**
     * Void constructor.
     */
    public SemevalCorpusCategorizerNN() {
    }


    /**
     * Method to initialize the confusion matrix.
     */
    public void initialize() {
        theNN = new KNearestNeighbour();
        theNN.setNumberOfEmotionalDimensions(2);
        theValences = new ArrayList<Float>();
        theActivations = new ArrayList<Float>();
    }


    /**
     * Prints the synopsis.
     */
    public void printSynopsis() {
        System.out.println("SemevalCorpusCategorizerNN usage:");
        System.out.println("\tjava -cp EmoLib-X.Y.Z.jar emolib.util.eval.semeval.SemevalCorpusCategorizerNN " +
            "LANGUAGE russell|whissell|scherer INPUT_SEMEVAL_FILE OUTPUT_CATEGORIES_FILE");
    }


    /**
     * Function to perform a change of base to compute the vector sums.
     *
     * @param theValue The value that has to be transformed.
     *
     * @return The transformed value.
     */
    private Float changeBase(float theValue) {
        float newValue = theValue - Float.parseFloat("5.0");
        Float newObjectValue = new Float(newValue);
        return newObjectValue;
    }


    /**
     * The method to compute the resulting emotional dimentions according to the base
     * reference emotions and their associated weight.
     *
     * @param theWeights The weights of the base reference emotions.
     *
     * @return The resulting emotional dimentions.
     */
    public float[] computeResultingDimentions(String[] theWeights) {
        float resultVal = 5;
        float resultAct = 5;
        for (int counter = 1; counter < theWeights.length; counter++) {
            resultVal += (Float.parseFloat(theWeights[counter]) / Float.parseFloat("100")) *
                theValences.get(counter - 1).floatValue();
            resultAct += (Float.parseFloat(theWeights[counter]) / Float.parseFloat("100")) *
                theActivations.get(counter - 1).floatValue();
        }
        // Since the corpus evaluations have been performed on a subjective basis,
        // the vector sum should be clipped in order not to excede the dimentional
        // bounds.
        if (resultVal < 0) {
            resultVal = 0;
        }
        if (resultAct < 0) {
            resultAct = 0;
        }
        if (resultVal > 10) {
            resultVal = 10;
        }
        if (resultAct > 10) {
            resultAct = 10;
        }
        float[] theDims = new float[2];
        theDims[0] = resultVal;
        theDims[1] = resultAct;
        return theDims;
    }


    /**
     * Method to train the classifier (NN).
     *
     * @param val The valence.
     * @param act The activation.
     * @param cat The category.
     */
    public void trainClassifier(float val, float act, String cat) {
        FeatureBox tempFeatures = new FeatureBox();
        tempFeatures.setNumberOfEmotionalDimensions(2);
        tempFeatures.setValence(val);
        tempFeatures.setActivation(act);
        theNN.inputTrainingExample(tempFeatures, cat);
    }


    /**
     * Methdo to add a valence with the appropriate base to compute vector operations.
     *
     * @param val The valence to add.
     */
    public void addValence(float val) {
        theValences.add(changeBase(val));
    }


    /**
     * Methdo to add an activation with the appropriate base to compute vector operations.
     *
     * @param act The activation to add.
     */
    public void addActivation(float act) {
        theActivations.add(changeBase(act));
    }


    /**
     * Function to obtain the nearest category from the classifier.
     *
     * @param val The valence.
     * @param act The activation.
     * @param uselessOne Useless parameter. Only two dimentions are needed.
     * @param uselessTwo Useless parameter. Only two dimentions are needed.
     */
    public String getCategory(float val, float act, float uselessOne, String uselessTwo) {
        FeatureBox tempFeatures = new FeatureBox();
        tempFeatures.setNumberOfEmotionalDimensions(2);
        tempFeatures.setValence(val);
        tempFeatures.setActivation(act);
        return theNN.getCategory(tempFeatures);
    }


    /**
     * The main method of the SemevalCorpusCategorizerNN application.
     *
     * @param args The input arguments. The first one corresponds to the language,
     * followed by the definitions of the
     * basic refernce emotions, followed by the input file from the Semeval corpus and finally, as
     * the fourth parameter, the desired output file containing the categories.
     */
    public static void main(String[] args) throws Exception {
        SemevalCorpusCategorizerNN categorizer = new SemevalCorpusCategorizerNN();
        categorizer.initialize();

        if (args.length == 4) {
            if (args[1].equals("whissell")) {
                // The emotions in the Semeval dataset are ordered as follows:
                // anger disgust fear joy sadness surprise
                // "Disgust" is not accounted in EmoLib.
                if (args[0].equals("english")) {
                    // Anger
                    categorizer.trainClassifier(Float.parseFloat("2.91"), Float.parseFloat("5.64"), "anger");
                    categorizer.addValence(Float.parseFloat("2.91"));
                    categorizer.addActivation(Float.parseFloat("5.64"));
                    // Disgust
                    categorizer.addValence(Float.parseFloat("3.82"));
                    categorizer.addActivation(Float.parseFloat("7.09"));
                    // Fear
                    categorizer.trainClassifier(Float.parseFloat("4.18"), Float.parseFloat("6.91"), "fear");
                    categorizer.addValence(Float.parseFloat("4.18"));
                    categorizer.addActivation(Float.parseFloat("6.91"));
                    // Happiness
                    categorizer.trainClassifier(Float.parseFloat("7.64"), Float.parseFloat("7.64"), "happiness");
                    categorizer.addValence(Float.parseFloat("7.64"));
                    categorizer.addActivation(Float.parseFloat("7.64"));
                    // Sorrow is actually sad, in the Whissell emotional dictionary.
                    categorizer.trainClassifier(Float.parseFloat("2.36"), Float.parseFloat("4.91"), "sorrow");
                    categorizer.addValence(Float.parseFloat("2.36"));
                    categorizer.addActivation(Float.parseFloat("4.91"));
                    // Surprise
                    categorizer.trainClassifier(Float.parseFloat("7.45"), Float.parseFloat("9.82"), "surprise");
                    categorizer.addValence(Float.parseFloat("7.45"));
                    categorizer.addActivation(Float.parseFloat("9.82"));
                    // Neutral
                    categorizer.trainClassifier(Float.parseFloat("5.0"), Float.parseFloat("5.0"), "neutral");
                } else if (args[0].equals("spanish")) {
                    // Anger
                    categorizer.trainClassifier(Float.parseFloat("2.91"), Float.parseFloat("5.64"), "enfado");
                    categorizer.addValence(Float.parseFloat("2.91"));
                    categorizer.addActivation(Float.parseFloat("5.64"));
                    // Disgust
                    categorizer.addValence(Float.parseFloat("3.82"));
                    categorizer.addActivation(Float.parseFloat("7.09"));
                    // Fear
                    categorizer.trainClassifier(Float.parseFloat("4.18"), Float.parseFloat("6.91"), "miedo");
                    categorizer.addValence(Float.parseFloat("4.18"));
                    categorizer.addActivation(Float.parseFloat("6.91"));
                    // Happiness
                    categorizer.trainClassifier(Float.parseFloat("7.64"), Float.parseFloat("7.64"), "alegria");
                    categorizer.addValence(Float.parseFloat("7.64"));
                    categorizer.addActivation(Float.parseFloat("7.64"));
                    // Sorrow is actually sad, in the Whissell emotional dictionary.
                    categorizer.trainClassifier(Float.parseFloat("2.36"), Float.parseFloat("4.91"), "tristeza");
                    categorizer.addValence(Float.parseFloat("2.36"));
                    categorizer.addActivation(Float.parseFloat("4.91"));
                    // Surprise
                    categorizer.trainClassifier(Float.parseFloat("7.45"), Float.parseFloat("9.82"), "sorpresa");
                    categorizer.addValence(Float.parseFloat("7.45"));
                    categorizer.addActivation(Float.parseFloat("9.82"));
                    // Neutral
                    categorizer.trainClassifier(Float.parseFloat("5.0"), Float.parseFloat("5.0"), "neutro");
                }
            } else if (args[1].equals("scherer")) {
                // The emotions in the Semeval dataset are ordered as follows:
                // anger disgust fear joy sadness surprise
                // "Disgust" is not accounted in EmoLib.
                if (args[0].equals("english")) {
                    // Anger
                    categorizer.trainClassifier(Float.parseFloat("0.65"), Float.parseFloat("8.16"), "anger");
                    categorizer.addValence(Float.parseFloat("0.65"));
                    categorizer.addActivation(Float.parseFloat("8.16"));
                    // Disgust
                    categorizer.addValence(Float.parseFloat("0.96"));
                    categorizer.addActivation(Float.parseFloat("7.51"));
                    // Fear is actually alarmed, in the Scherer emotional circumplex.
                    categorizer.trainClassifier(Float.parseFloat("3.95"), Float.parseFloat("9.44"), "fear");
                    categorizer.addValence(Float.parseFloat("3.95"));
                    categorizer.addActivation(Float.parseFloat("9.44"));
                    // Happiness
                    categorizer.trainClassifier(Float.parseFloat("9.20"), Float.parseFloat("6.02"), "happiness");
                    categorizer.addValence(Float.parseFloat("9.20"));
                    categorizer.addActivation(Float.parseFloat("6.02"));
                    // Sorrow is actually sad, in the Scherer emotional circumplex.
                    categorizer.trainClassifier(Float.parseFloat("3.00"), Float.parseFloat("0.88"), "sorrow");
                    categorizer.addValence(Float.parseFloat("3.00"));
                    categorizer.addActivation(Float.parseFloat("0.88"));
                    // Surprise is actually astonished, in the Scherer emotional circumplex.
                    categorizer.trainClassifier(Float.parseFloat("6.46"), Float.parseFloat("9.37"), "surprise");
                    categorizer.addValence(Float.parseFloat("6.46"));
                    categorizer.addActivation(Float.parseFloat("9.37"));
                    // Neutral
                    categorizer.trainClassifier(Float.parseFloat("5.0"), Float.parseFloat("5.0"), "neutral");
                } else if (args[0].equals("spanish")) {
                    // Anger
                    categorizer.trainClassifier(Float.parseFloat("0.65"), Float.parseFloat("8.16"), "enfado");
                    categorizer.addValence(Float.parseFloat("0.65"));
                    categorizer.addActivation(Float.parseFloat("8.16"));
                    // Disgust
                    categorizer.addValence(Float.parseFloat("0.96"));
                    categorizer.addActivation(Float.parseFloat("7.51"));
                    // Fear is actually alarmed, in the Scherer emotional circumplex.
                    categorizer.trainClassifier(Float.parseFloat("3.95"), Float.parseFloat("9.44"), "miedo");
                    categorizer.addValence(Float.parseFloat("3.95"));
                    categorizer.addActivation(Float.parseFloat("9.44"));
                    // Happiness
                    categorizer.trainClassifier(Float.parseFloat("9.20"), Float.parseFloat("6.02"), "alegria");
                    categorizer.addValence(Float.parseFloat("9.20"));
                    categorizer.addActivation(Float.parseFloat("6.02"));
                    // Sorrow is actually sad, in the Scherer emotional circumplex.
                    categorizer.trainClassifier(Float.parseFloat("3.00"), Float.parseFloat("0.88"), "tristeza");
                    categorizer.addValence(Float.parseFloat("3.00"));
                    categorizer.addActivation(Float.parseFloat("0.88"));
                    // Surprise is actually astonished, in the Scherer emotional circumplex.
                    categorizer.trainClassifier(Float.parseFloat("6.46"), Float.parseFloat("9.37"), "sorpresa");
                    categorizer.addValence(Float.parseFloat("6.46"));
                    categorizer.addActivation(Float.parseFloat("9.37"));
                    // Neutral
                    categorizer.trainClassifier(Float.parseFloat("5.0"), Float.parseFloat("5.0"), "neutro");
                }
            } else if (args[1].equals("russell")) {
                // The emotions in the Semeval dataset are ordered as follows:
                // anger disgust fear joy sadness surprise
                // "Disgust" is not accounted in EmoLib.
                if (args[0].equals("english")) {
                    // Anger
                    categorizer.trainClassifier(Float.parseFloat("2.34"), Float.parseFloat("6.62"), "anger");
                    categorizer.addValence(Float.parseFloat("2.34"));
                    categorizer.addActivation(Float.parseFloat("6.62"));
                    // Disgust is actually distress
                    categorizer.addValence(Float.parseFloat("1.83"));
                    categorizer.addActivation(Float.parseFloat("6.00"));
                    // Fear
                    categorizer.trainClassifier(Float.parseFloat("2.61"), Float.parseFloat("6.83"), "fear");
                    categorizer.addValence(Float.parseFloat("2.61"));
                    categorizer.addActivation(Float.parseFloat("6.83"));
                    // Happiness
                    categorizer.trainClassifier(Float.parseFloat("7.90"), Float.parseFloat("4.92"), "happiness");
                    categorizer.addValence(Float.parseFloat("7.90"));
                    categorizer.addActivation(Float.parseFloat("4.92"));
                    // Sorrow is actually sadness
                    categorizer.trainClassifier(Float.parseFloat("2.68"), Float.parseFloat("1.98"), "sorrow");
                    categorizer.addValence(Float.parseFloat("2.68"));
                    categorizer.addActivation(Float.parseFloat("1.98"));
                    // Surprise is actually astonishment
                    categorizer.trainClassifier(Float.parseFloat("5.18"), Float.parseFloat("7.92"), "surprise");
                    categorizer.addValence(Float.parseFloat("5.18"));
                    categorizer.addActivation(Float.parseFloat("7.92"));
                    // Neutral
                    categorizer.trainClassifier(Float.parseFloat("5.0"), Float.parseFloat("5.0"), "neutral");
                } else if (args[0].equals("spanish")) {
                    // Anger
                    categorizer.trainClassifier(Float.parseFloat("2.34"), Float.parseFloat("6.62"), "enfado");
                    categorizer.addValence(Float.parseFloat("2.34"));
                    categorizer.addActivation(Float.parseFloat("6.62"));
                    // Disgust is actually distress
                    categorizer.addValence(Float.parseFloat("1.83"));
                    categorizer.addActivation(Float.parseFloat("6.00"));
                    // Fear
                    categorizer.trainClassifier(Float.parseFloat("2.61"), Float.parseFloat("6.83"), "miedo");
                    categorizer.addValence(Float.parseFloat("2.61"));
                    categorizer.addActivation(Float.parseFloat("6.83"));
                    // Happiness
                    categorizer.trainClassifier(Float.parseFloat("7.90"), Float.parseFloat("4.92"), "alegria");
                    categorizer.addValence(Float.parseFloat("7.90"));
                    categorizer.addActivation(Float.parseFloat("4.92"));
                    // Sorrow is actually sadness
                    categorizer.trainClassifier(Float.parseFloat("2.68"), Float.parseFloat("1.98"), "tristeza");
                    categorizer.addValence(Float.parseFloat("2.68"));
                    categorizer.addActivation(Float.parseFloat("1.98"));
                    // Surprise is actually astonishment
                    categorizer.trainClassifier(Float.parseFloat("5.18"), Float.parseFloat("7.92"), "sorpresa");
                    categorizer.addValence(Float.parseFloat("5.18"));
                    categorizer.addActivation(Float.parseFloat("7.92"));
                    // Neutral
                    categorizer.trainClassifier(Float.parseFloat("5.0"), Float.parseFloat("5.0"), "neutro");
                }
            } else {
                System.out.println("SemevalCorpusCategorizerNN: the basic reference emotions are not correct!");
            }
            BufferedReader originalFile = new BufferedReader(new FileReader(args[2]));
            BufferedWriter predictionFile = new BufferedWriter(new FileWriter(args[3]));
            String[] evaluations;

            String lineOriginalFile = originalFile.readLine();
            float[] theResultingDimentions;
            while (lineOriginalFile != null) {
                evaluations = lineOriginalFile.split(" ");
                theResultingDimentions = categorizer.computeResultingDimentions(evaluations);
                predictionFile.write(theResultingDimentions[0] + " ");
                predictionFile.write(theResultingDimentions[1] + " ");
                predictionFile.write(categorizer.getCategory(theResultingDimentions[0],
                    theResultingDimentions[1], Float.parseFloat("1.0"), "hello"));
                predictionFile.newLine();
                lineOriginalFile = originalFile.readLine();
            }
            predictionFile.close();
        } else if (args.length == 1) {
            if (args[0].equals("-h") || args[0].equals("--help")) {
                categorizer.printSynopsis();
            } else {
                System.out.println("SemevalCorpusCategorizerNN: Please enter the correct parameters!");
                System.out.println("");
                categorizer.printSynopsis();
            }
        } else {
            System.out.println("SemevalCorpusCategorizerNN: Please enter the correct parameters!");
            System.out.println("");
            categorizer.printSynopsis();
        }
    }

}

