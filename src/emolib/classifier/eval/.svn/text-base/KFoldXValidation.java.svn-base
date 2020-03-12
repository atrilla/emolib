/*
 * File    : KFoldXValidation.java
 * Created : 01-Jan-2011
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2011 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.classifier.eval;

import java.io.*;
import java.util.ArrayList;
import java.lang.Math;
import java.net.URL;

import emolib.classifier.Classifier;
import emolib.classifier.FeatureBox;
import emolib.AffectiveTagger;
import emolib.util.proc.TextData;
import emolib.util.proc.WordData;
import emolib.util.conf.ConfigurationManager;
import emolib.classifier.heuristic.*;
import emolib.classifier.machinelearning.*;


/**
 * The <i>KFoldXValidation</i> class performs the k-fold cross-validation
 * method on the stratified input data file.
 *
 * <p>
 * The k-fold cross-validation method divides the input file into `k' parts,
 * trains the specified classifier with `k-1' parts and tests it with the
 * remaining part. The effectiveness is scored using a macroaveraging method
 * (precision and recall calculations) and
 * the results obtained over all iterations are averaged with the arithmetic mean.
 * </p>
 * <p>
 * It is emphasised that the input dataset needs to be stratified, i.e., each fold
 * must maintain the category balance of the dataset.
 * </p>
 * <p>
 * This class uses the whole textual affect processing pipeline of EmoLib
 * defined in an external config file,
 * taking advantage from the partial contributions of each module.
 * The <i>KFoldXValidation</i> class seeks the <i>kfoldcv</i> component in the
 * XML config file, so please beware of its existence and correct definition.
 * </p>
 * <p>
 * The the <i>KFoldXValidation</i> is launched with one fold (test data) and a
 * fixed dataset (training data), it performs the train-test process.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class KFoldXValidation {

    private ArrayList<String> inputCategories;
    private ArrayList<String> inputFixedCategories;
    private ArrayList<FeatureBox> inputText;
    private ArrayList<FeatureBox> inputFixedText;

    private boolean fixedDataset;

    private ArrayList<String> basicCategories;

    private int nFolds;

    private ArrayList<Float> globalPrecision;
    private ArrayList<Float> globalRecall;
    private ArrayList<Float> globalFone;

    private AffectiveTagger textProcPipe;


    /**
     * Void constructor.
     */
    public KFoldXValidation() {
        nFolds = 10;
        fixedDataset = false;
        inputText = new ArrayList<FeatureBox>();
        inputCategories = new ArrayList<String>();
        globalPrecision = new ArrayList<Float>(nFolds);
        globalRecall = new ArrayList<Float>(nFolds);
        globalFone = new ArrayList<Float>(nFolds);
        for (int init = 0; init < nFolds; init++) {
            globalPrecision.add(new Float("0"));
            globalRecall.add(new Float("0"));
            globalFone.add(new Float("0"));
        }
        textProcPipe = null;
    }


    /**
     * Method to set the number of folds.
     *
     * @param The number of folds.
     */
    public void setNumberOfFolds(int nf) {
        nFolds = nf;
    }


    /**
     * Method to set the text processing pipeline.
     *
     * @param Reference to the pipeline;
     */
    public void setTextProcessingPipeline(AffectiveTagger pipe) {
        textProcPipe = pipe;
    }


    /**
     * Method to set only if a fixed dataset is given for training.
     */
    public void setFixedDataset() {
        fixedDataset = true;
        inputFixedText = new ArrayList<FeatureBox>();
        inputFixedCategories = new ArrayList<String>();
    }


    /**
     * Method to set the basic categories of the system.
     * This method requires a string with the different categories separated by a hyphen.
     *
     * @param inputCategories The categories.
     */
    public void setBasicCategories(String inputCategories) {
        int catCounter;

        String[] categories = inputCategories.split("-");
        basicCategories = new ArrayList<String>(categories.length);
        for (catCounter = 0; catCounter < categories.length; catCounter++) {
            basicCategories.add(catCounter, categories[catCounter].trim());
        }
    }


    /**
     * Mehtod to include a new input corpus instance into the system.
     * The category of this training instance must be space separated at the end
     * of the text.
     *
     * @param inputInstance The input instance.
     */
    public void inputInstance(String inputInstance) {
        String[] instanceParameters = inputInstance.split(" ");
        String temp = instanceParameters[0];
        for (int i = 1; i < instanceParameters.length - 1; i++) {
            temp += " " + instanceParameters[i];
        }
        inputText.add(getFeatures(temp));
        inputCategories.add(new String(instanceParameters[instanceParameters.length - 1]));
    }


    /**
     * Method to include a new fixed instance.
     * A method similar to inputInstance. With this method, for each fold in the
     * cross-validation procedure, these fixed instanced will always be present to
     * train the classifier.
     *
     * @param inputFixedInstance The input fixed instance.
     */
    public void inputFixedInstance(String inputFixedInstance) {
        String[] instanceParameters = inputFixedInstance.split(" ");
        String temp = instanceParameters[0];
        for (int i = 1; i < instanceParameters.length - 1; i++) {
            temp += " " + instanceParameters[i];
        }
        inputFixedText.add(getFeatures(temp));
        inputFixedCategories.add(new String(instanceParameters[instanceParameters.length - 1]));
    }


    /**
     * Function to extract the features from the given text.
     *
     * @param text The given text.
     *
     * @return The extracted features.
     */
    private FeatureBox getFeatures(String text) {
        TextData data = textProcPipe.processText(text);
        WordData wdata = null;
        FeatureBox fbox = new FeatureBox();
        // Emotional dimension feats
        fbox.setNumberOfEmotionalDimensions(3);
        fbox.setValence(data.getEmotionalValence());
        fbox.setActivation(data.getEmotionalActivation());
        fbox.setControl(data.getEmotionalControl());
        // Lexical feats
        String words = "";
        String postags = "";
        String stems = "";
        String synonyms = "";
        String stemmedsynonyms = "";
        boolean negation = false;
        for (int word = 0; word < data.getNumberOfWords(); word++) {
            wdata = data.getWordData(word);
            words += " " + wdata.getWord();
            // It is not considered if other POS such as determiners, possessives, etc. could be
            // labelled as nouns, adjectives..., only the words that do not pertain to such POS tags,
            // i.e. OTRO.
            if (wdata.hasEmotionalContent()) {
                if (wdata.isNoun()) {
                    postags += " NOMBRE";
                } else if (wdata.isAdjective()) {
                    postags += " ADJETIVO";
                } else if (wdata.isVerb()) {
                    postags += " VERBO";
                } else if (wdata.isAdverb()) {
                    postags += " ADVERBIO";
                } else {
                    postags += " " + wdata.getWordClass();
                }
            } else {
                postags += " " + wdata.getWordClass();
            }
            stems += " " + wdata.getWordStem();
            if (wdata.containsSynonyms()) {
                ArrayList syns = wdata.getSense();
                for (int syn = 0; syn < syns.size(); syn++) {
                    synonyms += " " + (String)syns.get(syn);
                }
            }
            if (wdata.containsStemmedSynonyms()) {
                ArrayList ssyns = wdata.getStemmedWordSense();
                for (int ssyn = 0; ssyn < ssyns.size(); ssyn++) {
                    stemmedsynonyms += " " + (String)ssyns.get(ssyn);
                }
            }
            if (wdata.isNegationAdverb()) {
                if (negation) {
                    negation = false;
                } else {
                    negation = true;
                }
            }
        }
        fbox.setWords(words.replaceAll("  ", " ").trim());
        fbox.setPOSTags(postags.replaceAll("  ", " ").trim());
        fbox.setStems(stems.replaceAll("  ", " ").trim());
        if (!synonyms.equals("")) {
            fbox.setSynonyms(synonyms.replaceAll("  ", " ").trim());
            fbox.setStemmedSynonyms(stemmedsynonyms.replaceAll("  ", " ").trim());
        }
        fbox.setNegation(negation);
        //
        return fbox;
    }


    /**
     * Method to evaluate the dataset and output the result of the k-fold cross-validation
     * process.
     * The performance metrics are evaluated with macroaveraging, thus the balance
     * of the dataset does not bias the results, and the partial results in each fold
     * are finally averaged with the arithmetic mean.
     *
     * @param theClassifier The classifier.
     */
    public void evaluate(Classifier theClassifier) {
        // Define the k+1 fold boundaries.
        ArrayList<Integer> foldBounds = new ArrayList<Integer>(nFolds + 1);
        foldBounds.add(new Integer(0));
        int increment = inputCategories.size() / nFolds;
        for (int incrCounter = 0; incrCounter < (nFolds - 1); incrCounter++) {
            foldBounds.add(new Integer(increment + incrCounter * increment));
        }
        foldBounds.add(new Integer(inputCategories.size()));
        // Begin the k-fold iterations.
        ArrayList<FeatureBox> trainTexts;
        ArrayList<String> trainCategories;
        ArrayList<FeatureBox> testTexts;
        ArrayList<String> testCategories;
        int opInterval, categoryCounter;
        for (int kCounter = 0; kCounter < nFolds; kCounter++) {
            System.out.println("KFoldXValidation: fold number: " + kCounter);
            trainTexts = new ArrayList<FeatureBox>();
            trainCategories = new ArrayList<String>();
            testTexts = new ArrayList<FeatureBox>();
            testCategories = new ArrayList<String>();
            // Training and testing datasets creation.
            createSubDataset(foldBounds.get(kCounter).intValue(),
                foldBounds.get(kCounter + 1).intValue() - 1, inputText, testTexts);
            createSubDataset(foldBounds.get(kCounter).intValue(),
                foldBounds.get(kCounter + 1).intValue() - 1, inputCategories, testCategories);
            for (opInterval = 0; opInterval < kCounter; opInterval++) {
                createSubDataset(foldBounds.get(opInterval).intValue(),
                    foldBounds.get(opInterval + 1).intValue() - 1, inputText, trainTexts);
                createSubDataset(foldBounds.get(opInterval).intValue(),
                    foldBounds.get(opInterval + 1).intValue() - 1, inputCategories, trainCategories);
            }
            for (opInterval = (kCounter + 1); opInterval < nFolds; opInterval++) {
                createSubDataset(foldBounds.get(opInterval).intValue(),
                    foldBounds.get(opInterval + 1).intValue() - 1, inputText, trainTexts);
                createSubDataset(foldBounds.get(opInterval).intValue(),
                    foldBounds.get(opInterval + 1).intValue() - 1, inputCategories, trainCategories);
            }
            // Time to train the classifier.
            theClassifier.resetExamples();
            for (opInterval = 0; opInterval < trainTexts.size(); opInterval++) {
                theClassifier.inputTrainingExample(trainTexts.get(opInterval), trainCategories.get(opInterval));
            }
            if (fixedDataset) {
                for (int numfix = 0; numfix < inputFixedCategories.size(); numfix++) {
                    theClassifier.inputTrainingExample(inputFixedText.get(numfix), inputFixedCategories.get(numfix));
                }
            }
            theClassifier.train();
            // Time to test the classifier.
            ArrayList<String> classificationResult = new ArrayList<String>();
            String presentCategory = "";
            float localPrecision, localRecall;
            float truePositives = 0;
            float classifierScore = 0;
            float expertScore = 0;
            int numTestInstances = 0;
            //
            // The results are obtained here
            numTestInstances = testTexts.size();
            String aaau;
            System.out.println("Results: ---------------------------------_");
            for (opInterval = 0; opInterval < testTexts.size(); opInterval++) {
                aaau = theClassifier.getCategory(testTexts.get(opInterval));
                System.out.println(aaau);
                classificationResult.add(aaau);
            }

            // The macroaveraged effectiveness rates are computed here
            // This process could be more effectively implemented :)
            for (categoryCounter = 0; categoryCounter < basicCategories.size(); categoryCounter++) {
                presentCategory = basicCategories.get(categoryCounter);
                truePositives = 0;
                classifierScore = 0;
                expertScore = 0;
                for (opInterval = 0; opInterval < numTestInstances; opInterval++) {
                    if (classificationResult.get(opInterval).equals(testCategories.get(opInterval)) &&
                    classificationResult.get(opInterval).equals(presentCategory)) {
                        truePositives = truePositives + 1;
                    }
                    if (classificationResult.get(opInterval).equals(presentCategory)) {
                        classifierScore = classifierScore + 1;
                    }
                    if (testCategories.get(opInterval).equals(presentCategory)) {
                        expertScore = expertScore + 1;
                    }
                }
                localPrecision = truePositives / classifierScore;
                localRecall = truePositives / expertScore;
                // Update the global effectiveness estimates.
                float nBasicCategories = basicCategories.size();
                globalPrecision.set(kCounter, new Float(globalPrecision.get(kCounter).floatValue() +
                    localPrecision / nBasicCategories));
                globalRecall.set(kCounter, new Float(globalRecall.get(kCounter).floatValue() + localRecall /
                    nBasicCategories));
            }
            globalFone.set(kCounter, new Float(2 *
                globalPrecision.get(kCounter).floatValue() *
                globalRecall.get(kCounter).floatValue() /
                (globalPrecision.get(kCounter).floatValue() +
                globalRecall.get(kCounter).floatValue())));
            System.out.println("\tF_1 = " + globalFone.get(kCounter).floatValue());
        }
        // Show the results.
        // Overall results: mean +/- std
        System.out.println("");
        float meanPrecision = 0;
        int correctFolds = 0;
        for (int i = 0; i < globalPrecision.size(); i++) {
            System.out.println("Precision " + i + " : " + globalPrecision.get(i).floatValue());
            if (!globalPrecision.get(i).isNaN()) {
                meanPrecision = meanPrecision + globalPrecision.get(i).floatValue();
                correctFolds++;
            }
        }
        System.out.println("Overall precision: " + meanPrecision / correctFolds + " +/- " +
            calcStandardDeviation(globalPrecision, meanPrecision / correctFolds));
        System.out.println("");
        float meanRecall = 0;
        correctFolds = 0;
        for (int i = 0; i < globalRecall.size(); i++) {
            System.out.println("Recall " + i + " : " + globalRecall.get(i).floatValue());
            if (!globalRecall.get(i).isNaN()) {
                meanRecall = meanRecall + globalRecall.get(i).floatValue();
                correctFolds++;
            }
        }
        System.out.println("Overall recall: " + meanRecall / correctFolds + " +/- " +
            calcStandardDeviation(globalRecall, meanRecall / correctFolds));
        System.out.println("");
        float meanFone = 0;
        correctFolds = 0;
        for (int i = 0; i < globalFone.size(); i++) {
            System.out.println("F1 " + i + " : " + globalFone.get(i).floatValue());
            if (!globalFone.get(i).isNaN()) {
                meanFone = meanFone + globalFone.get(i).floatValue();
                correctFolds++;
            }
        }
        System.out.println("Overall F1: " + meanFone / correctFolds + " +/- " +
            calcStandardDeviation(globalFone, meanFone / correctFolds));
    }


    /**
     * Function to calculate the standard deviation of an input set.
     *
     * @param input_set The input set.
     * @param the_mean The mean of the input set.
     *
     * @return The standard deviation of the input set.
     */
    private float calcStandardDeviation(ArrayList<Float> input_set, float the_mean) {
        double the_sum = 0;
        int correctFolds = 0;
        Float mean = new Float(the_mean);

        for (int i = 0; i < input_set.size(); i++) {
            if (!input_set.get(i).isNaN()) {
                the_sum += Math.pow(input_set.get(i).doubleValue() - mean.doubleValue(), 2);
                correctFolds++;
            }
        }
        Float the_result = new Float(Math.sqrt(the_sum / (correctFolds - 1)));
        return the_result.floatValue();
    }


    /**
     * Method to create a subset from a bigger superset.
     * It is used for building the training and testing datasets.
     *
     * @param lowerBoundIndex The lower bound index.
     * @param upperBoundIndex The upper bound index.
     * @param superset The superset.
     * @param subset The subset.
     */
    private void createSubDataset(int lowerBoundIndex, int upperBoundIndex, ArrayList superset, ArrayList subset) {
        for (int counter = lowerBoundIndex; counter <= upperBoundIndex; counter++) {
            subset.add(superset.get(counter));
        }
    }


    /**
     * Prints the synopsis.
     */
    public void printSynopsis() {
        System.out.println("KFoldXValidation usage:");
        System.out.println("\tjava " + "[-Xmx256m] -cp EmoLib-X.Y.Z.jar emolib.classifier.eval.KFoldXValidation");
        System.out.println("\t\t-nf NUMBER_OF_FOLDS");
        System.out.println("\t\t-id INPUT_DATASET");
        System.out.println("\t\t[-fd FIXED_DATASET]");
        System.out.println("\t\t-bc CATEGORY_1-CATEGORY_2[-CATEGORY_N]");
        System.out.println("\t\t-cf CONFIGURATION_FILE");
        System.out.println("\t\t-c CLASSIFIER");
        System.out.println("");
        System.out.println("The available classifiers are:");
        System.out.println("\t{5,3}IKE: Five Intervals or Three Intervals (in English), i.e. the sentiments");
        System.out.println("\tNC: Nearest Centroid");
        System.out.println("\t{,3,5,7}NN: k-Nearest Neighbour, for k=1,3,5,7");
        System.out.println("\tNB: Naive Bayes (with Gaussian likelihoods and without priors)");
        System.out.println("\tNBP: Naive Bayes (with Gaussian likelihoods and with priors)");
        System.out.println("\tRWNB: Risk Weighted Naive Bayes");
        System.out.println("\tRWNBgd: Risk Weighted Naive Bayes with Gradient Descent learning rule");
        System.out.println("\tLSA: Latent Semantic Analysis (word frequencies)");
        System.out.println("\t\twCOF: with Collocation Frequencies");
        System.out.println("\t\twITF: with Inverse Term Frequencies");
        System.out.println("\t\twLTFRF: with log Term Frequency - Relevance Factor");
        System.out.println("\t\twPOS: with with POS tags");
        System.out.println("\t\twSYN: with synonyms");
        System.out.println("\t\twSTEM: with stems (instead of the whole words)");
        //
        System.out.println("\tWSVM: Weka Support Vector Machine (binary attributes," +
            " linear kernel, intercept)");
        System.out.println("\t\twTF: with Term Frequency attributes");
        System.out.println("\t\twCOF: with Collocation Frequencies");
        System.out.println("\t\twITF: with Inverse Term Frequencies");
        System.out.println("\t\twTFRF: with Term Frequency - Relevance Factor");
        System.out.println("\t\twLTFRF: with log Term Frequency - Relevance Factor");
        System.out.println("\t\twPOS: with with POS tags");
        System.out.println("\t\twSYN: with synonyms");
        System.out.println("\t\twSTEM: with stems (instead of the whole words)");
        System.out.println("\t\twEMO: with emotion dimensions (Valence-Activation-Control)");
        System.out.println("\t\twNEG: consideration of negations in the text");
        System.out.println("\t\twRBF: consideration of a Radial Basis Function kernel");
        System.out.println("\t\twK2: consideration of a polynomial quadratic kernel");
        System.out.println("\t\twNPK: consideration of a normalised polynomial linear kernel");
        System.out.println("\t\twNPK2: consideration of a normalised polynomial quadratic kernel");
        System.out.println("\t\twLOT: consideration of lower order terms in the kernel");
        System.out.println("\t\twNOINT: without intercept feature");
        System.out.println("\t\twMI-k: with Mutual Information " +
            "(k selected terms)");
        System.out.println("\t\twCHI2-k: with Chi-square " +
            "(k selected terms)");
        System.out.println("\t\twFSTF-k: with Term Frequency feat. sel. " +
            "(k selected terms)");
        //
        System.out.println("\tARNR: Associative Relational Network - Reduced (only words are considered + " +
            "freq weights + cosine distributional similarity measure)");
        System.out.println("\t\twBIN: with binary-weighted features");
        System.out.println("\t\twCOF: with Collocation Frequencies");
        System.out.println("\t\twPOS: with POS tags");
        System.out.println("\t\twITF: with Inverse Term Frequencies");
        System.out.println("\t\twLTFRF: with log Term Frequency - Relevance Factor");
        System.out.println("\t\twMDN2: with Norm2 (Euclidean) Matrix Difference as distributional similarity measure");
        System.out.println("\t\twMCD: with matrix cosine distance as distributional similarity measure");
        System.out.println("\t\twSYN: with synonyms");
        System.out.println("\t\twSTEM: with stems (instead of the whole words)");
        System.out.println("\t\twDOT: with the dot product as similarity measure");
        //
        System.out.println("\tBNB: Bernoulli Naive Bayes");
        System.out.println("\tMNB: Multinomial Naive Bayes");
        System.out.println("\t\twCOF: with Collocation Frequencies");
        System.out.println("\t\twPOS: with with POS tags");
        System.out.println("\t\twSYN: with synonyms");
        System.out.println("\t\twSTEM: with stems (instead of the whole words)");
        System.out.println("\t\twMI-k: with Mutual Information " +
            "(k selected terms)");
        System.out.println("\t\twCHI2-k: with Chi-square " +
            "(k selected terms)");
        System.out.println("\t\twFSTF-k: with Term Frequency feat. sel. " +
            "(k selected terms)");
        System.out.println("\tWMNB: Weka Multinomial Naive Bayes");
        System.out.println("\t\twMI-k: with Mutual Information " +
            "(k selected terms)");
        System.out.println("\t\twCHI2-k: with Chi-square " +
            "(k selected terms)");
        System.out.println("\t\twFSTF-k: with Term Frequency feat. sel. " +
            "(k selected terms)");
        System.out.println("\t\twCOF: with Collocation Frequencies");
        System.out.println("\t\twITF: with Inverse Term Frequencies");
        System.out.println("\t\twLTFRF: with log Term Frequency - Relevance Factor");
        System.out.println("\t\twPOS: with with POS tags");
        System.out.println("\t\twSYN: with synonyms");
        System.out.println("\t\twSTEM: with stems (instead of the whole words)");
        System.out.println("\t\twEMO: with emotion dimensions (Valence-Activation-Control)");
        System.out.println("\t\twNEG: consideration of negations in the text");
        System.out.println("\tOLR: Ordinal Logistic Regression " +
            "(default binary features, noninformative prior, intercept)");
        System.out.println("\tRLR: Risk-weighted Logistic Regression (default binary features, " +
            "noninformative prior, intercept, for sentiments)");
        System.out.println("\tLOGR: Logistic Regression (default binary features, " +
            "noninformative prior, intercept, LingPipe implementation)");
        System.out.println("\t\twCOF: with Collocation Frequencies");
        System.out.println("\t\twITF: with Inverse Term Frequencies");
        System.out.println("\t\twTFRF: with Term Frequency - Relevance Factor");
        System.out.println("\t\twLTFRF: with log Term Frequency - Relevance Factor");
        System.out.println("\t\twCRRF: with Category Rank - Relevance Factor");
        System.out.println("\t\twPOS: with with POS tags");
        System.out.println("\t\twSYN: with synonyms");
        System.out.println("\t\twSTEM: with stems (instead of the whole words)");
        System.out.println("\t\twEMO: with emotion dimensions (Valence-Activation-Control)");
        System.out.println("\t\twNEG: consideration of negations in the text");
        System.out.println("\t\twGAUSSPRIOR: with Gaussian regression prior");
        System.out.println("\t\twNOINT: without intercept feature");
        System.out.println("\t\twMI-k: with Mutual Information " +
            "(k selected terms)");
        System.out.println("\t\twCHI2-k: with Chi-square " +
            "(k selected terms)");
        System.out.println("\t\twFSTF-k: with Term Frequency feat. sel. " +
            "(k selected terms)");
        //
        System.out.println("\tSHCAwARNR: Sentiment Hierarchical Cluster Analysis with Associative " +
            "Relational Network - Reduced");
        System.out.println("\tSHCAwARNRwCOF: Sentiment Hierarchical Cluster Analysis with Associative " +
            "Relational Network - Reduced with Co-Ocurrence Frequencies");
        System.out.println("\tSHCAwARNRwITF: Sentiment Hierarchical Cluster Analysis with Associative " +
            "Relational Network - Reduced with Inverse Term Frequencies");
        System.out.println("\tSHCAwARNRwITFwCOF: Sentiment Hierarchical Cluster Analysis with Associative " +
            "Relational Network - Reduced with Inverse Term Frequencies and Co-Ocurrence Frequencies");
        System.out.println("\tSHCAwARNRwLTFRF: Sentiment Hierarchical Cluster Analysis with Associative " +
            "Relational Network - Reduced with Relevance factor");
        System.out.println("\tSHCAwARNRwLTFRFwCOF: Sentiment Hierarchical Cluster Analysis with Associative " +
            "Relational Network - Reduced with Relevance factor and Co-Ocurrence Frequencies");
        System.out.println("");
        System.out.println("\tTASS2L: SEPLN TASS 2 Levels");
        System.out.println("");
    }


    /**
     * Function to get the index of a query in an array of strings.
     *
     * @param query The query.
     * @param theArray The array of strings.
     *
     * @return The index of the query in the array.
     */
    public int indexOf(String query, String[] theArray) {
        int ind = -1;
        for (int i = 0; i < theArray.length; i++) {
            if (theArray[i].equals(query)) {
                ind = i;
                break;
            }
        }
        return ind;
    }


    /**
     * The main method of the KFoldXValidation application.
     *
     * @param args The input arguments.
     */
    public static void main(String[] args) throws Exception {
        KFoldXValidation validator;
        //
        if ((args.length > 1) && ((args.length % 2) == 0)) {
            String[] params = (String[])args.clone();
            validator = new KFoldXValidation();
            // Setting parameters
            validator.setNumberOfFolds(Integer.parseInt(params[validator.indexOf("-nf", params) + 1]));
            validator.setBasicCategories(params[validator.indexOf("-bc", params) + 1]);
            // Presentation
            System.out.println("");
            System.out.println(params[validator.indexOf("-nf", params) + 1] + "-fold Cross Validation " +
                "procedure");
            System.out.println("***");
            System.out.print("Loading resources for EmoLib... ");
            long start = System.currentTimeMillis();
            // Time to construct the classifier
            URL configFile = new File(params[validator.indexOf("-cf", params) + 1]).toURI().toURL();
            ConfigurationManager confMgr = new ConfigurationManager(configFile);
            AffectiveTagger textProcPipeline = (AffectiveTagger)confMgr.lookup("kfoldcv");
            validator.setTextProcessingPipeline(textProcPipeline);
            System.out.println("OK");
            System.out.print("Loading corpus examples... ");
            BufferedReader originalFile = new BufferedReader(new FileReader(params[validator.indexOf("-id",
                params) + 1]));
            String lineOriginalFile = originalFile.readLine();
            while (lineOriginalFile != null) {
                validator.inputInstance(lineOriginalFile);
                lineOriginalFile = originalFile.readLine();
            }
            originalFile.close();
            // Check if a fixed dataset is given for evaluation
            if (validator.indexOf("-fd", params) != -1) {
                validator.setFixedDataset();
                BufferedReader fixedDB = new BufferedReader(new FileReader(params[validator.indexOf("-fd",
                    params) + 1]));
                String lineFixedDB = fixedDB.readLine();
                while (lineFixedDB != null) {
                    validator.inputFixedInstance(lineFixedDB);
                    lineFixedDB = fixedDB.readLine();
                }
            }
            System.out.println("OK");
            System.out.println("Starting evaluation...");
            System.out.println("***");
            // Classifier selection and evaluation
            String[] classif = params[validator.indexOf("-c", params) + 1].split("w");
            if (classif[0].equals("5IKE")) {
                FiveIntervalsKE five_intervals_ke = new FiveIntervalsKE();
                validator.evaluate(five_intervals_ke);
            } else if (classif[0].equals("3IKE")) {
                ThreeIntervalsKE three_intervals_ke = new ThreeIntervalsKE();
                validator.evaluate(three_intervals_ke);
            } else if (classif[0].equals("7NN")) {
                KNearestNeighbour knn = new KNearestNeighbour();
                knn.setNumberOfNeighbours(7);
                knn.setNumberOfEmotionalDimensions(Integer.valueOf(args[1]).intValue());
                validator.evaluate(knn);
            } else if (classif[0].equals("3NN")) {
                KNearestNeighbour knn = new KNearestNeighbour();
                knn.setNumberOfNeighbours(3);
                knn.setNumberOfEmotionalDimensions(Integer.valueOf(args[1]).intValue());
                validator.evaluate(knn);
            } else if (classif[0].equals("5NN")) {
                KNearestNeighbour knn = new KNearestNeighbour();
                knn.setNumberOfNeighbours(5);
                knn.setNumberOfEmotionalDimensions(Integer.valueOf(args[1]).intValue());
                validator.evaluate(knn);
            } else if (classif[0].equals("NN")) {
                KNearestNeighbour knn = new KNearestNeighbour();
                knn.setNumberOfNeighbours(1);
                knn.setNumberOfEmotionalDimensions(Integer.valueOf(args[1]).intValue());
                validator.evaluate(knn);
            } else if (classif[0].equals("NC")) {
                NearestCentroid nC = new NearestCentroid();
                nC.setNumberOfEmotionalDimensions(2);
                validator.evaluate(nC);
            } else if (classif[0].equals("NB")) {
                NaiveBayes nB = new NaiveBayes();
                nB.setNumberOfEmotionalDimensions(Integer.valueOf(args[1]).intValue());
                validator.evaluate(nB);
            } else if (classif[0].equals("NBP")) {
                NaiveBayes nB = new NaiveBayes();
                nB.setNumberOfEmotionalDimensions(Integer.valueOf(args[1]).intValue());
                nB.setAccountForPriors(true);
                validator.evaluate(nB);
            } else if (classif[0].equals("RWNB")) {
                RiskWeightedNaiveBayes rWNB = new RiskWeightedNaiveBayes();
                rWNB.setNumberOfEmotionalDimensions(Integer.valueOf(args[1]).intValue());
                validator.evaluate(rWNB);
            } else if (classif[0].equals("RWNBgd")) {
                RiskWeightedNaiveBayes rWNB = new RiskWeightedNaiveBayes();
                rWNB.setNumberOfEmotionalDimensions(Integer.valueOf(args[1]).intValue());
                rWNB.setLearningProcedure("three_sentiment_gradient_descent");
                validator.evaluate(rWNB);
            } else if (classif[0].equals("LSA")) {
                LSA lsa = new LSA();
                for (int opts = 1; opts < classif.length; opts++) {
                    if (classif[opts].equals("COF")) {
                        lsa.setCOF(true);
                    } else if (classif[opts].equals("ITF")) {
                        lsa.setTermWeighingMeasure("itf");
                    } else if (classif[opts].equals("LTFRF")) {
                        lsa.setTermWeighingMeasure("ltfrf");
                    } else if (classif[opts].equals("POS")) {
                        lsa.setPOS(true);
                    } else if (classif[opts].equals("STEM")) {
                        lsa.setStemming(true);
                    } else if (classif[opts].equals("SYN")) {
                        lsa.setSynonyms(true);
                    } else {
                        System.out.println("KFoldXValidation: error in the specification of LSA " +
                            "parameters!");
                        validator.printSynopsis();
                        System.exit(1);
                    }
                }
                validator.evaluate(lsa);
            } else if (classif[0].equals("WSVM")) {
                SupportVectorMachine svm = new SupportVectorMachine();
                for (int opts = 1; opts < classif.length; opts++) {
                    if (classif[opts].startsWith("MI-")) {
                        String[] miChunk = classif[opts].split("-");
                        svm.setMI(true, Integer.parseInt(miChunk[1]));
                    } else if (classif[opts].startsWith("CHI2-")) {
                        String[] chi2Chunk = classif[opts].split("-");
                        svm.setChi2(true, Integer.parseInt(chi2Chunk[1]));
                    } else if (classif[opts].startsWith("FSTF-")) {
                        String[] tfChunk = classif[opts].split("-");
                        svm.setTF(true, Integer.parseInt(tfChunk[1]));
                    } else if (classif[opts].equals("TF")) {
                        svm.setTermWeighingMeasure("tf");
                    } else if (classif[opts].equals("COF")) {
                        svm.setCOF(true);
                    } else if (classif[opts].equals("ITF")) {
                        svm.setTermWeighingMeasure("itf");
                    } else if (classif[opts].equals("TFRF")) {
                        svm.setTermWeighingMeasure("tfrf");
                    } else if (classif[opts].equals("LTFRF")) {
                        svm.setTermWeighingMeasure("ltfrf");
                    } else if (classif[opts].equals("POS")) {
                        svm.setPOS(true);
                    } else if (classif[opts].equals("STEM")) {
                        svm.setStemming(true);
                    } else if (classif[opts].equals("SYN")) {
                        svm.setSynonyms(true);
                    } else if (classif[opts].equals("EMO")) {
                        svm.setEmotionDims(true);
                    } else if (classif[opts].equals("NEG")) {
                        svm.setNegation(true);
                    } else if (classif[opts].equals("RBF")) {
                        svm.setRBF(true);
                    } else if (classif[opts].equals("K2")) {
                        svm.setExponent((double)2);
                    } else if (classif[opts].equals("NPK")) {
                        svm.setNormalisedPolyK(true);
                    } else if (classif[opts].equals("NPK2")) {
                        svm.setNormalisedPolyK(true);
                        svm.setExponent((double)2);
                    } else if (classif[opts].equals("LOT")) {
                        svm.setLowerOrderTerms(true);
                    } else if (classif[opts].equals("NOINT")) {
                        svm.setIntercept(false);
                    } else {
                        System.out.println("KFoldXValidation: error in the specification of WSVM parameters!");
                        validator.printSynopsis();
                        System.exit(1);
                    }
                }
                validator.evaluate(svm);
            } else if (classif[0].equals("ARNR")) {
                ARNReduced arnR = new ARNReduced();
                for (int opts = 1; opts < classif.length; opts++) {
                    if (classif[opts].equals("COF")) {
                        arnR.setCOF(true);
                    } else if (classif[opts].equals("POS")) {
                        arnR.setPOS(true);
                    } else if (classif[opts].equals("SYN")) {
                        arnR.setSynonyms(true);
                    } else if (classif[opts].equals("STEM")) {
                        arnR.setStems(true);
                    } else if (classif[opts].equals("BIN")) {
                        arnR.setTermWeighingMeasure("binary");
                    } else if (classif[opts].equals("ITF")) {
                        arnR.setTermWeighingMeasure("itf");
                    } else if (classif[opts].equals("TFRF")) {
                        arnR.setTermWeighingMeasure("tfrf");
                    } else if (classif[opts].equals("LTFRF")) {
                        arnR.setTermWeighingMeasure("ltfrf");
                    } else if (classif[opts].equals("MDN2")) {
                        arnR.setSimilarityMeasure("matrixdifnorm2");
                    } else if (classif[opts].equals("MCD")) {
                        arnR.setSimilarityMeasure("cosMatrix");
                    } else if (classif[opts].equals("DOT")) {
                        arnR.setSimilarityMeasure("dotprod");
                    } else {
                        System.out.println("KFoldXValidation: error in the specification of ARNR parameters!");
                        validator.printSynopsis();
                        System.exit(1);
                    }
                }
                validator.evaluate(arnR);
            } else if (classif[0].equals("TASS2L")) {
                TASS2Levels tass = new TASS2Levels();
                validator.evaluate(tass);
            } else if (classif[0].equals("WMNB")) {
                WekaMultinomialNB wMNB = new WekaMultinomialNB();
                for (int opts = 1; opts < classif.length; opts++) {
                    if (classif[opts].startsWith("MI-")) {
                        String[] miChunk = classif[opts].split("-");
                        wMNB.setMI(true, Integer.parseInt(miChunk[1]));
                    } else if (classif[opts].startsWith("CHI2-")) {
                        String[] chi2Chunk = classif[opts].split("-");
                        wMNB.setChi2(true, Integer.parseInt(chi2Chunk[1]));
                    } else if (classif[opts].startsWith("FSTF-")) {
                        String[] tfChunk = classif[opts].split("-");
                        wMNB.setTF(true, Integer.parseInt(tfChunk[1]));
                    } else if (classif[opts].equals("ITF")) {
                        wMNB.setTermWeighingMeasure("itf");
                    } else if (classif[opts].equals("LTFRF")) {
                        wMNB.setTermWeighingMeasure("ltfrf");
                    } else if (classif[opts].equals("COF")) {
                        wMNB.setCOF(true);
                    } else if (classif[opts].equals("POS")) {
                        wMNB.setPOS(true);
                    } else if (classif[opts].equals("STEM")) {
                        wMNB.setStemming(true);
                    } else if (classif[opts].equals("SYN")) {
                        wMNB.setSynonyms(true);
                    } else if (classif[opts].equals("EMO")) {
                        wMNB.setEmotionDims(true);
                    } else if (classif[opts].equals("NEG")) {
                        wMNB.setNegation(true);
                    } else {
                        System.out.println("KFoldXValidation: error in the " +
                            "specification of WMNB parameters!");
                        validator.printSynopsis();
                        System.exit(1);
                    }
                }
                validator.evaluate(wMNB);
            } else if (classif[0].equals("MNB")) {
                MultinomialNB mNB = new MultinomialNB();
                for (int opts = 1; opts < classif.length; opts++) {
                    if (classif[opts].startsWith("MI-")) {
                        String[] miChunk = classif[opts].split("-");
                        mNB.setMI(true, Integer.parseInt(miChunk[1]));
                    } else if (classif[opts].startsWith("CHI2-")) {
                        String[] chi2Chunk = classif[opts].split("-");
                        mNB.setChi2(true, Integer.parseInt(chi2Chunk[1]));
                    } else if (classif[opts].startsWith("FSTF-")) {
                        String[] tfChunk = classif[opts].split("-");
                        mNB.setTF(true, Integer.parseInt(tfChunk[1]));
                    } else if (classif[opts].equals("COF")) {
                        mNB.setCOF(true);
                    } else if (classif[opts].equals("POS")) {
                        mNB.setPOS(true);
                    } else if (classif[opts].equals("STEM")) {
                        mNB.setStemming(true);
                    } else if (classif[opts].equals("SYN")) {
                        mNB.setSynonyms(true);
                    } else {
                        System.out.println("KFoldXValidation: error in the " +
                            "specification of MNB parameters!");
                        validator.printSynopsis();
                        System.exit(1);
                    }
                }
                validator.evaluate(mNB);
            } else if (classif[0].equals("BNB")) {
                BernoulliNB bNB = new BernoulliNB();
                validator.evaluate(bNB);
            } else if (classif[0].equals("OLR")) {
                OrdinalLogReg oLR = new OrdinalLogReg();
                validator.evaluate(oLR);
            } else if (classif[0].equals("RLR")) {
                RiskLogReg rlogR = new RiskLogReg();
                validator.evaluate(rlogR);
            } else if (classif[0].equals("LOGR")) {
                Logistic logR = new Logistic();
                for (int opts = 1; opts < classif.length; opts++) {
                    if (classif[opts].equals("COF")) {
                        logR.setCOF(true);
                    } else if (classif[opts].equals("ITF")) {
                        logR.setTermWeighingMeasure("itf");
                    } else if (classif[opts].equals("TFRF")) {
                        logR.setTermWeighingMeasure("tfrf");
                    } else if (classif[opts].equals("LTFRF")) {
                        logR.setTermWeighingMeasure("ltfrf");
                    } else if (classif[opts].equals("CRRF")) {
                        logR.setTermWeighingMeasure("crrf");
                    } else if (classif[opts].equals("POS")) {
                        logR.setPOS(true);
                    } else if (classif[opts].equals("STEM")) {
                        logR.setStemming(true);
                    } else if (classif[opts].equals("SYN")) {
                        logR.setSynonyms(true);
                    } else if (classif[opts].equals("EMO")) {
                        logR.setEmotionDims(true);
                    } else if (classif[opts].equals("NEG")) {
                        logR.setNegation(true);
                    } else if (classif[opts].equals("NOINT")) {
                        logR.setIntercept(false);
                    } else {
                        System.out.println("KFoldXValidation: error in the specification of Logistic Regression " +
                            "parameters!");
                        validator.printSynopsis();
                        System.exit(1);
                    }
                }
                validator.evaluate(logR);
            } else if (classif[0].equals("SHCAwARNRwCOF")) {
                HierarchicalARNReduced hARNR = new HierarchicalARNReduced();
                hARNR.setTypeOfARN("ARNRwCOF");
                validator.evaluate(hARNR);
            } else if (classif[0].equals("SHCAwARNR")) {
                HierarchicalARNReduced hARNR = new HierarchicalARNReduced();
                hARNR.setTypeOfARN("ARNR");
                validator.evaluate(hARNR);
            } else if (classif[0].equals("SHCAwARNRwITF")) {
                HierarchicalARNReduced hARNR = new HierarchicalARNReduced();
                hARNR.setTypeOfARN("ARNRwITF");
                validator.evaluate(hARNR);
            } else if (classif[0].equals("SHCAwARNRwITFwCOF")) {
                HierarchicalARNReduced hARNR = new HierarchicalARNReduced();
                hARNR.setTypeOfARN("ARNRwITFwCOF");
                validator.evaluate(hARNR);
            } else if (classif[0].equals("SHCAwARNRwLTFRF")) {
                HierarchicalARNReduced hARNR = new HierarchicalARNReduced();
                hARNR.setTypeOfARN("ARNRwLTFRF");
                validator.evaluate(hARNR);
            } else if (classif[0].equals("SHCAwARNRwLTFRFwCOF")) {
                HierarchicalARNReduced hARNR = new HierarchicalARNReduced();
                hARNR.setTypeOfARN("ARNRwLTFRFwCOF");
                validator.evaluate(hARNR);
            } else {
                System.out.println("KFoldXValidation: Please enter a valid classifier!");
                System.out.println("");
                System.exit(1);
            }
            System.out.println("");
            long elapsedTime = (System.currentTimeMillis() - start) / 1000;
            int days = (int)(elapsedTime / (3600 * 24));
            int rest = (int)(elapsedTime % (3600 * 24));
            int hours = (int)(rest / 3600);
            rest = rest % 3600;
            int minutes = (int)(rest / 60);
            rest = rest % 60;
            int seconds = rest;
            System.out.println("Elapsed time: " + days + "d. " + hours + 
                "h. " + minutes + "min. " + seconds + "sec.");
        } else if (args.length == 1) {
            validator = new KFoldXValidation();
            if (args[0].equals("-h") || args[0].equals("--help")) {
                validator.printSynopsis();
            } else {
                System.out.println("KFoldXValidation: Please enter the correct parameters!");
                System.out.println("");
                validator.printSynopsis();
            }
        } else {
            validator = new KFoldXValidation();
            System.out.println("KFoldXValidation: Please enter the correct parameters!");
            System.out.println("");
            validator.printSynopsis();
        }
    }

}

