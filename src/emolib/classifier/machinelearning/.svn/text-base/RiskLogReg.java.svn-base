/*
 * File    : RiskLogReg.java
 * Created : 3-Jun-2011
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

/**
 * The <i>RiskLogReg</i> is a Logistic Regression classifier
 * where the objective function is weighted with a
 * risk wrt the sentiment (NEG-NEU-POS labels).
 *
 * <p>
 * The risk function of use penalises critical errors, i.e., taking
 * one extreme sentiment for the other extreme (POS for NEG and
 * viceversa), over non-extreme errors involving the NEU.
 * The same term weighting schemes as the ones used in the ARN-R are 
 * considered.
 * </p>
 *
 * @see emolib.classifier.machinelearning.Logistic
 * @see emolib.classifier.machinelearning.ARNReduced
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class RiskLogReg extends Classifier {

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
    public RiskLogReg() {
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
        double prob = calcProbability(catNum, wVector);
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
            if (newProb > prob) {
                prob = newProb;
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
            0.0001, // learning rate
            0.0001, // min improve
            10); // max epochs
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
        double partition, exp, update, newErr, riskProb, preup;
        double error = calcError(feats, cats);
        System.out.println("Err ini: " + error);
        for (int epoch = 0; epoch < maxepochs; epoch++) {
            for (int ex = 0; ex < cats.length; ex++) {
                partition = calcPartition(feats.get(ex));
                riskProb = calcRisk(cats[ex], feats.get(ex));
                for (int cat = 0; cat < (theBetas.size() - 1); cat++) {
                    exp = calcExp(cat, feats.get(ex));
                    for (int dim = 0; dim < feats.get(0).size(); dim++) {
                        update = 0;
                        for (int catprime = 0; catprime <
                                theBetas.size(); catprime++) {
                            preup = riskFunction(cats[ex], catprime) *
                                exp * feats.get(ex).get(dim) *
                                (1 / Math.pow(partition, 2));
                            if (cat == catprime) {
                                preup *= (partition - exp);
                            } else {
                                preup *= (double)(-1) * exp;
                            }
                            update += preup;
                        }
                        update *= (double)(1) / riskProb;
                        theBetas.get(cat).set(dim,
                            new Double(theBetas.get(cat).get(dim).
                            doubleValue() - lrate * update));
                    }
                }
            }
            newErr = calcError(feats, cats);
            System.out.println("Err " + epoch + " : " + newErr);
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
     * Compute the risk.
     *
     * @param cat The category.
     * @param feats The features.
     *
     * @return The risk probability.
     */
    private double calcRisk(int cat, ArrayList<Double> feats) {
        double rp = 0;
        for (int c = 0; c < theBetas.size(); c++) {
            rp += riskFunction(cat, c) * calcProbability(c, feats);
        }
        return rp;
    }


    /**
     * Determines the risk function wrt the ordinality in the valence
     * of sentiments.
     *
     * @param cat One category.
     * @param ocat The other category.
     *
     * @return The risk function.
     */
    private double riskFunction(int cat, int ocat) {
        double scoreC1 = 0;
        double scoreC2 = 0;
        // Get the first score.
        if (categoryHash.get("NEG") == cat) {
            scoreC1 = 1;
        } else if (categoryHash.get("NEU") == cat) {
            scoreC1 = 2;
        } else {
            scoreC1 = 3;
        }
        // Get the second score.
        if (categoryHash.get("NEG") == ocat) {
            scoreC2 = 1;
        } else if (categoryHash.get("NEU") == ocat) {
            scoreC2 = 2;
        } else {
            scoreC2 = 3;
        }
        return Math.abs(scoreC1 - scoreC2) / 2;
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
            error += Math.log(calcRisk(cats[ex], feats.get(ex)));
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

}

