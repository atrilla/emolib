/*
 * File    : CorpusVocabularyStatistics.java
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

package emolib.util.eval;

import emolib.classifier.machinelearning.ARNReduced;
import emolib.classifier.FeatureBox;

import java.io.*;
import java.util.ArrayList;
import java.lang.Math;


/**
 * The <i>CorpusVocabularyStatistics</i> class performs a vocabulary analysis
 * on the input text file.
 *
 * <p>
 * The CorpusVocabularyStatistics outputs the total vocabulary size (size of training corpus),
 * the vocabulary size (number of words with a frequency over 20, 15, 10, 5 and 3) and the
 * amount of observed bigrams wrt the number of possible events.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class CorpusVocabularyStatistics {

    /**
     * Void constructor.
     */
    public CorpusVocabularyStatistics() {
    }


    /**
     * Prints the synopsis.
     */
    public void printSynopsis() {
        System.out.println("CorpusVocabularyStatistics usage:");
        System.out.println("\tjava " + "[-Xmx256m] -cp EmoLib-X.Y.Z.jar emolib.util.eval.CorpusVocabularyStatistics " +
            "INPUT_TEXT_FILE");
    }


    /**
     * Function to extract the features from the given text.
     *
     * @param text The given text.
     *
     * @return The extracted features.
     */
    public FeatureBox getFeatures(String text) {
        FeatureBox fbox = new FeatureBox();
        fbox.setWords(text.replaceAll("  ", " ").trim());
        //
        return fbox;
    }



    public static void main(String[] args) throws Exception {
        CorpusVocabularyStatistics example = new CorpusVocabularyStatistics();
        ARNReduced arn = new ARNReduced();
        arn.setCOF(true);

        if (args.length == 1) {
            BufferedReader in = new BufferedReader(new FileReader(args[0]));
            System.out.println("");
            System.out.println("CorpusVocabularyStatistics");
            System.out.println("");
            FeatureBox tempFeatures;
            String lineFromFile = in.readLine();
            while (lineFromFile != null) {
                // Feed the system
                // Blank lines make the system misfunction.
                if (lineFromFile.length() > 0) {
                    arn.inputTrainingExample(example.getFeatures(lineFromFile), "corpus");
                }
                lineFromFile = in.readLine();
            }
            // Train the ARN-R in order to build the graph.
            arn.train();
            // Get the corpus size (for class "corpus")
            int corpusSize = arn.getCorpusSize("corpus");
            int totalVocabSize = arn.getVocabularySize(1, "corpus");
            int vocabSize20 = arn.getVocabularySize(20, "corpus");
            int vocabSize15 = arn.getVocabularySize(15, "corpus");
            int vocabSize10 = arn.getVocabularySize(10, "corpus");
            int vocabSize5 = arn.getVocabularySize(5, "corpus");
            int vocabSize4 = arn.getVocabularySize(4, "corpus");
            int vocabSize3 = arn.getVocabularySize(3, "corpus");
            int vocabSize2 = arn.getVocabularySize(2, "corpus");
            // Print the results for bigrams
            System.out.println("The size of the corpus is: " + corpusSize);

            System.out.println("The total vocabulary size (unigrams) of the corpus is: " + totalVocabSize);

            System.out.println("Vocabulary size for 20: " + vocabSize20);
            System.out.println("\tObserved bigrams on the corpus: " + ((float)corpusSize /
                (float)(vocabSize20 * vocabSize20)) * (float)100 + "%");
            System.out.println("\tTotal vocabulary coverage: " + ((float)vocabSize20 /
                (float)totalVocabSize) * (float)100 + "%");

            System.out.println("Vocabulary size for 15: " + vocabSize15);
            System.out.println("\tObserved bigrams on the corpus: " + ((float)corpusSize /
                (float)(vocabSize15 * vocabSize15)) * (float)100 + "%");
            System.out.println("\tTotal vocabulary coverage: " + ((float)vocabSize15 /
                (float)totalVocabSize) * (float)100 + "%");

            System.out.println("Vocabulary size for 10: " + vocabSize10);
            System.out.println("\tObserved bigrams on the corpus: " + ((float)corpusSize /
                (float)(vocabSize10 * vocabSize10)) * (float)100 + "%");
            System.out.println("\tTotal vocabulary coverage: " + ((float)vocabSize10 /
                (float)totalVocabSize) * (float)100 + "%");

            System.out.println("Vocabulary size for 5: " + vocabSize5);
            System.out.println("\tObserved bigrams on the corpus: " + ((float)corpusSize /
                (float)(vocabSize5 * vocabSize5)) * (float)100 + "%");
            System.out.println("\tTotal vocabulary coverage: " + ((float)vocabSize5 /
                (float)totalVocabSize) * (float)100 + "%");

            System.out.println("Vocabulary size for 4: " + vocabSize4);
            System.out.println("\tObserved bigrams on the corpus: " + ((float)corpusSize /
                (float)(vocabSize4 * vocabSize4)) * (float)100 + "%");
            System.out.println("\tTotal vocabulary coverage: " + ((float)vocabSize4 /
                (float)totalVocabSize) * (float)100 + "%");

            System.out.println("Vocabulary size for 3: " + vocabSize3);
            System.out.println("\tObserved bigrams on the corpus: " + ((float)corpusSize /
                (float)(vocabSize3 * vocabSize3)) * (float)100 + "%");
            System.out.println("\tTotal vocabulary coverage: " + ((float)vocabSize3 /
                (float)totalVocabSize) * (float)100 + "%");

            System.out.println("Vocabulary size for 2: " + vocabSize2);
            System.out.println("\tObserved bigrams on the corpus: " + ((float)corpusSize /
                (float)(vocabSize2 * vocabSize2)) * (float)100 + "%");
            System.out.println("\tTotal vocabulary coverage: " + ((float)vocabSize2 /
                (float)totalVocabSize) * (float)100 + "%");

            System.out.println("");
            System.out.println("The ten most frequent WORDS, along with their respective frequencies:");
            ArrayList<String> wList = new ArrayList<String>();
            ArrayList<Integer> fList = new ArrayList<Integer>();
            arn.getOrderedList("corpus", wList, fList);
            if (wList.size() >= 10) {
                for (int i = 0; i < 10; i++) {
                    System.out.println(i + "\t" + wList.get(i) + "\t" + fList.get(i).intValue());
                }
            }
            System.out.println();
            // Perplexity calculation for words
            double entropy = 0;
            double wProb = 0;
            for (int i = 0; i < wList.size(); i++) {
                wProb = fList.get(i).doubleValue() / (double)corpusSize;
                entropy -= wProb * Math.log(wProb) / Math.log((double)2);
            }
            System.out.println("Perplexity: " + Math.pow((double)2, entropy));
            // Now with tuples
            System.out.println("");
            int corpusTupleSize = arn.getCorpusTupleSize("corpus");
            int totalBigramVocabSize = arn.getBigramVocabularySize(1, "corpus");
            System.out.println("The bigram size of the corpus is: " + corpusTupleSize);
            System.out.println("The total vocabulary size (bigrams) of the corpus is: " + totalBigramVocabSize);
            System.out.println("");
            System.out.println("Bigram vocabulary size for 5: " + 
                arn.getBigramVocabularySize(5, "corpus"));
            System.out.println("");
            System.out.println("The ten most frequent TUPLES, along with their respective frequencies:");
            wList = new ArrayList<String>();
            fList = new ArrayList<Integer>();
            arn.getOrderedTupleList("corpus", wList, fList);
            if (wList.size() >= 10) {
                for (int i = 0; i < 10; i++) {
                    System.out.println(i + "\t" + wList.get(i) + "\t" + fList.get(i).intValue());
                }
            }
            System.out.println();
            // Perplexity calculation for tuples
            entropy = 0;
            wProb = 0;
            for (int i = 0; i < wList.size(); i++) {
                wProb = fList.get(i).doubleValue() / (double)corpusTupleSize;
                entropy -= wProb * Math.log(wProb) / Math.log((double)2);
            }
            System.out.println("Perplexity: " + Math.pow((double)2, entropy));
        } else if (args.length == 1) {
            if (args[0].equals("-h") || args[0].equals("--help")) {
                example.printSynopsis();
            } else {
                System.out.println("CorpusVocabularyStatistics: Please enter the correct parameters!");
                System.out.println("");
                example.printSynopsis();
            }
        } else {
            System.out.println("CorpusVocabularyStatistics: Please enter the correct parameters!");
            System.out.println("");
            example.printSynopsis();
        }
    }

}

