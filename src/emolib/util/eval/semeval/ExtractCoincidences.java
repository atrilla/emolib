/*
 * File    : ExtractCoincidences.java
 * Created : 02-Mar-2009
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

import java.io.*;

/**
 * The <i>ExtractCoincidences</i> class checks the coincidences between the
 * two input emotion tag categorisations and creates one file with these
 * matches, another file with the predictions produced by EmoLib and a file
 * containing the original sentences for further study (relation with its
 * dimensions).
 *
 * <p>
 * In case a quick reference of its usage is needed, the class responds to the
 * typical help queries ("-h" or "--help") by showing the program's synopsis.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class ExtractCoincidences {

    /**
     * Void constructor.
     */
    public ExtractCoincidences() {
    }


    /**
     * Prints the synopsis.
     */
    public void printSynopsis() {
        System.out.println("ExtractCoincidences usage:");
        System.out.println("\tjava -cp EmoLib-X.Y.Z.jar emolib.util.eval.semeval.ExtractCoincidences " +
            "FILE_CATEGORIES_1 FILE_CATEGORIES_2 PREDICTIONS_FILE SENTENCES_FILE OUTPUT_FOLDER");
    }


    /**
     * The main method of the ExtractCoincidences application.
     *
     * @param args The input arguments. The first one corresponds to the first input categories file,
     * followed by the second input categories file,
     * followed by the predictions file produced by EmoLib  and finally, as
     * the fourth parameter, the desired output folder.
     */
    public static void main(String[] args) throws Exception {
        ExtractCoincidences theExtractor = new ExtractCoincidences();
        if (args.length == 5) {
            BufferedReader categoriesFileOne = new BufferedReader(new FileReader(args[0]));
            BufferedReader categoriesFileTwo = new BufferedReader(new FileReader(args[1]));
            BufferedReader predictionFile = new BufferedReader(new FileReader(args[2]));
            BufferedReader sentencesFile = new BufferedReader(new FileReader(args[3]));
            BufferedWriter outputCategoriesFile = new BufferedWriter(new FileWriter(args[4] +
                System.getProperty("file.separator") + "semeval_categories.txt"));
            BufferedWriter outputPredictionsFile = new BufferedWriter(new FileWriter(args[4] +
                System.getProperty("file.separator") + "emolib_predictions.txt"));
            BufferedWriter outputSentencesFile = new BufferedWriter(new FileWriter(args[4] +
                System.getProperty("file.separator") + "semeval_sentences.txt"));

            String lineCategoriesFileOne = categoriesFileOne.readLine();
            String lineCategoriesFileTwo = categoriesFileTwo.readLine();
            String linePredictionFile = predictionFile.readLine();
            String lineSentencesFile = sentencesFile.readLine();
            while ((lineCategoriesFileOne != null) && (lineCategoriesFileTwo != null) &&
            (linePredictionFile != null) && (lineSentencesFile != null)) {
                if (lineCategoriesFileOne.trim().equals(lineCategoriesFileTwo.trim())) {
                    outputCategoriesFile.write(lineCategoriesFileOne.trim());
                    outputCategoriesFile.newLine();
                    outputPredictionsFile.write(linePredictionFile.trim());
                    outputPredictionsFile.newLine();
                    outputSentencesFile.write(lineSentencesFile.trim());
                    outputSentencesFile.newLine();
                }
                lineCategoriesFileOne = categoriesFileOne.readLine();
                lineCategoriesFileTwo = categoriesFileTwo.readLine();
                linePredictionFile = predictionFile.readLine();
                lineSentencesFile = sentencesFile.readLine();
            }
            outputCategoriesFile.close();
            outputPredictionsFile.close();
            outputSentencesFile.close();
        } else if (args.length == 1) {
            if (args[0].equals("-h") || args[0].equals("--help")) {
                theExtractor.printSynopsis();
            } else {
                System.out.println("ExtractCoincidences: Please enter the correct parameters!");
                System.out.println("");
                theExtractor.printSynopsis();
            }
        } else {
            System.out.println("ExtractCoincidences: Please enter the correct parameters!");
            System.out.println("");
            theExtractor.printSynopsis();
        }
    }

}

