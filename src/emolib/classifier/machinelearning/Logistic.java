/*
 * File    : Logistic.java
 * Created : 24-May-2011
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

package emolib.classifier.machinelearning;

import emolib.classifier.Classifier;
import emolib.classifier.FeatureBox;

import emolib.classifier.machinelearning.ARNReduced.Graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.lang.Math;

import org.junit.Test;
import org.junit.Assert;

/**
 * The <i>Logistic</i> class is logistic regression classifier.
 *
 * <p>
 * The same IR weighting schemes as the ones used in the ARN-R are 
 * considered.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class Logistic extends Classifier {

    // Core
    private ArrayList<ArrayList<Double>> theBetas;

    // Term Weighting scheme core
    private ARNReduced arnR;
    private String TWMeasure;
    private boolean bigramFreq;
    private boolean interceptFeat;
    private boolean posTags;
    private boolean stemming;
    private boolean synonyms;
    private boolean emotionDims;
    private boolean negation;
    //
    private HashMap<String, Integer> categoryHash;


    /**
     * Main constructor of this logistic regression classifier.
     */
    public Logistic() {
        theBetas = null;
        arnR = null;
        categoryHash = null;
        TWMeasure = "binary";
        bigramFreq = false;
        interceptFeat = true;
        posTags = false;
        stemming = false;
        synonyms = false;
        emotionDims = false;
        negation = false;
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.
     * FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        Iterator categoryLabels = arnR.getCategoryList().iterator();
        String catLab = (String)categoryLabels.next();
        int catNum = categoryHash.get(catLab);
        Graph tempGraph = arnR.buildFullGraph(arnR.buildGraph(inputFeatures));
        arnR.applyTermWeighing(tempGraph, catNum);
        float[] tmpVector = tempGraph.exportWeighedVector();
        double[] weightedVector = createWeightedVector(tmpVector,
            inputFeatures);
        ArrayList<Double> wVector = new ArrayList<Double>();
        for (int i = 0; i < weightedVector.length; i++) {
            wVector.add(new Double(weightedVector[i]));
        }
        double probability = calcProbability(catNum, wVector);
        String newCatLab;
        int newCatNum;
        double newProb;
        while (categoryLabels.hasNext()) {
            newCatLab = (String)categoryLabels.next();
            newCatNum = categoryHash.get(newCatLab);
            tempGraph = arnR.buildFullGraph(arnR.buildGraph(inputFeatures));
            arnR.applyTermWeighing(tempGraph, newCatNum);
            tmpVector = tempGraph.exportWeighedVector();
            weightedVector = createWeightedVector(tmpVector, inputFeatures);
            wVector = new ArrayList<Double>();
            for (int i = 0; i < weightedVector.length; i++) {
                wVector.add(new Double(weightedVector[i]));
            }
            newProb = calcProbability(newCatNum, wVector);
            if (newProb > probability) {
                probability = newProb;
                catLab = newCatLab;
            }
        }
        //
        return catLab;
    }


    /**
     * Function to create the weighted vector apt for this classifier.
     *
     * @param lexvect The TW vector of lexical features.
     * @param fbox All the feats.
     *
     * @return The weighted vector.
     */
    private double[] createWeightedVector(float[] lexvect, FeatureBox fbox) {
        int totalen = lexvect.length;
        int offset = 0;
        if (interceptFeat) {
            totalen++;
        }
        if (emotionDims) {
            totalen += 3;
        }
        if (negation) {
            totalen++;
        }
        double[] weightedVector = new double[totalen];
        if (interceptFeat) {
            weightedVector[0] = (double)1;
            offset++;
        }
        for (int i = 0; i < lexvect.length; i++) {
            weightedVector[i + offset] = (double)lexvect[i];
        }
        offset += lexvect.length;
        if (emotionDims) {
            weightedVector[offset] = (double)fbox.getValence();
            weightedVector[offset + 1] = (double)fbox.getActivation();
            weightedVector[offset + 2] = (double)fbox.getControl();
            offset += 3;
        }
        if (negation) {
            if (fbox.getNegation()) {
                weightedVector[offset] = (double)1;
            } else {
                weightedVector[offset] = (double)0;
            }
        }
        return weightedVector;
    }


    /**
     * Training method based on the Stochastic Gradient Descent.
     */
    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#trainingProcedure()
     */
    public void trainingProcedure() {
        ArrayList<FeatureBox> exampleFeatures = getListOfExampleFeatures();
        ArrayList<String> exampleCategories = getListOfExampleCategories();
        arnR = new ARNReduced();
        setFeatureWeights();
        Iterator exFeat = exampleFeatures.iterator();
        Iterator exCat = exampleCategories.iterator();
        while (exFeat.hasNext() && exCat.hasNext()) {
            arnR.inputTrainingExample((FeatureBox)exFeat.next(),
                (String)exCat.next());
        }
        arnR.train();
        categoryHash = arnR.getCategoryHash();
        // Time to train the logistic regression
        int[] goldStandardCats = new int[exampleCategories.size()];
        ArrayList<ArrayList<Double>> goldStandardFeats =
            new ArrayList<ArrayList<Double>>();
        //
        Graph tempGraph;
        double[] weightedVector;
        float[] tmpVector;
        for (int exNum = 0; exNum < exampleFeatures.size(); exNum++) {
            tempGraph = arnR.buildFullGraph(arnR.buildGraph(exampleFeatures.
                get(exNum)));
            arnR.applyTermWeighing(tempGraph,
                categoryHash.get(exampleCategories.get(exNum)).intValue());
            tmpVector = tempGraph.exportWeighedVector();
            weightedVector = createWeightedVector(tmpVector,
                exampleFeatures.get(exNum));
            goldStandardFeats.add(new ArrayList<Double>());
            for (int f = 0; f < weightedVector.length; f++) {
                goldStandardFeats.get(exNum).add(new Double(weightedVector[f]));
            }
            goldStandardCats[exNum] = categoryHash.
                get(exampleCategories.get(exNum)).intValue();
        }
        estimateLogistic(goldStandardFeats, goldStandardCats,
            0.001, // learning rate
            0.001, // min improve
            10000); // max epochs
    }


    /**
     * Method to estimate the parameters of the distribution.
     *
     * @param feats The features.
     * @param cats The categories.
     * @param lrate The learning rate (constant).
     * @param impr The minimum improvement.
     * @param maxepochs The maximum number of epochs.
     */
    private void estimateLogistic(
        ArrayList<ArrayList<Double>> feats,
        int[] cats,
        double lrate,
        double impr,
        int maxepochs) {
        // Initialisation
        theBetas = new ArrayList<ArrayList<Double>>();
        for (int c = 0; c < categoryHash.size(); c++) {
            theBetas.add(new ArrayList<Double>());
            for (int d = 0; d < feats.get(0).size(); d++) {
                theBetas.get(c).add(new Double("0"));
            }
        }
        double partition, exp, update, newErr;
        double error = calcError(feats, cats);
        for (int epoch = 0; epoch < maxepochs; epoch++) {
            for (int ex = 0; ex < cats.length; ex++) {
                partition = calcPartition(feats.get(ex));
                for (int cat = 0; cat < (theBetas.size() - 1); cat++) {
                    exp = calcExp(cat, feats.get(ex));
                    for (int dim = 0; dim < feats.get(0).size(); dim++) {
                        if (cat == cats[ex]) {
                            update = 1;
                        } else {
                            update = 0;
                        }
                        update -= exp / partition;
                        // The update corresponds to the derivative of
                        // Err_l(beta,D)
                        update *= (double)(-1) * feats.get(ex).get(dim).
                            doubleValue();
                        theBetas.get(cat).set(dim,
                            new Double(theBetas.get(cat).get(dim).
                            doubleValue() - lrate * update));
                    }
                }
            }
            newErr = calcError(feats, cats);
            if (relDiff(newErr, error) < impr) {
                break;
            } else {
                error = newErr;
            }
        }
    }


    /**
     * Calculates the exponential.
     * Namely, exp(- lambda_c · x).
     *
     * @param cat The category.
     * @param feats The feature vector.
     *
     * @return The exponential.
     */
    private double calcExp(int cat, ArrayList<Double> feats) {
        double e = 0;
        for (int i = 0; i < feats.size(); i++) {
            e += theBetas.get(cat).get(i).doubleValue() *
               feats.get(i).doubleValue();
        }
        return Math.exp(e);
    }


    /**
     * Calculate the partition function.
     * Namely, sum_forall_c (1 - exp(c,x)).
     *
     * @param feats The feature vector.
     *
     * @return The partition function.
     */
    private double calcPartition(ArrayList<Double> feats) {
        double p = 0;
        for (int c = 0; c < theBetas.size(); c++) {
            p += calcExp(c, feats);
        }
        return p;
    }


    /**
     * Compute the probability.
     *
     * @param cat The category.
     * @param feats The features.
     *
     * @return The probability.
     */
    private double calcProbability(int cat, ArrayList<Double> feats) {
        return calcExp(cat, feats) / calcPartition(feats);
    }


    /**
     * Computes the error.
     * It is defined as the negative of the corpus lod likelihood.
     *
     * @param feats The features of the examples.
     * @param cats The categories of the examples.
     *
     * @return The error.
     */
    private double calcError(ArrayList<ArrayList<Double>> feats, int[] cats) {
        double error = 0;
        for (int ex = 0; ex < cats.length; ex++) {
            error -= Math.log(calcProbability(cats[ex], feats.get(ex)));
        }
        return error;
    }


    /**
     * Compute the relative difference.
     *
     * @param val1 First value.
     * @param val2 Second value.
     *
     * @return The relative difference.
     */
    private double relDiff(double val1, double val2) {
        return Math.abs(val1 - val2) / (Math.abs(val1) + Math.abs(val2));
    }


    /**
     * Method to set the feature weights of the logistic regression.
     * It is required that the private reference to the ARN-R is 
     * initialised.
     */
    private void setFeatureWeights() {
        arnR.setTermWeighingMeasure(TWMeasure);
        arnR.setCOF(bigramFreq);
        arnR.setPOS(posTags);
        arnR.setStems(stemming);
        arnR.setSynonyms(synonyms);
    }


    /**
     * Method to set the TW method.
     *
     * @param twm The TW method.
     */
    public void setTermWeighingMeasure(String twm) {
        TWMeasure = twm;
    }


    /**
     * Method to consider bigram frequencies.
     *
     * @param cof The COF flag.
     */
    public void setCOF(boolean cof) {
        bigramFreq = cof;
    }


    /**
     * Method to consider POS tags.
     *
     * @param pos The POS flag.
     */
    public void setPOS(boolean pos) {
        posTags = pos;
    }


    /**
     * Method to consider stems.
     *
     * @param stems The stemming flag.
     */
    public void setStemming(boolean stems) {
        stemming = stems;
    }


    /**
     * Method to consider the intercept feature.
     *
     * @param intercept The intercept flag.
     */
    public void setIntercept(boolean intercept) {
        interceptFeat = intercept;
    }


    /**
     * Method to consider synonyms.
     *
     * @param syns The synonyms flag.
     */
    public void setSynonyms(boolean syns) {
        synonyms = syns;
    }


    /**
     * Method to consider emotion dimensions.
     *
     * @param emodims The emotion dimensions flag.
     */
    public void setEmotionDims(boolean emodims) {
        emotionDims = emodims;
    }


    /**
     * Method to consider negations.
     *
     * @param neg The negation flag.
     */
    public void setNegation(boolean neg) {
        negation = neg;
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
        theBetas = null;
        arnR = null;
        categoryHash = null;
    }


    /**
     * Functionality test.
     */
    @Test public void simpleClassification() {
        Logistic mlr = new Logistic();
        FeatureBox feat = new FeatureBox();
        feat.setWords("I hate going to the dentist .");
        mlr.inputTrainingExample(feat, "NEG");
        //
        feat = new FeatureBox();
        feat.setWords("I swim a lot .");
        mlr.inputTrainingExample(feat, "NEU");
        //
        feat = new FeatureBox();
        feat.setWords("I love reading books .");
        mlr.inputTrainingExample(feat, "POS");
        //
        mlr.train();
        //
        feat = new FeatureBox();
        feat.setWords("I like my dentist .");
        Assert.assertTrue(mlr.getCategory(feat).equals("NEG"));
        //
        feat = new FeatureBox();
        feat.setWords("You love .");
        Assert.assertTrue(mlr.getCategory(feat).equals("POS"));
    }

}

