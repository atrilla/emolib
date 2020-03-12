/*
 * File    : MultinomialNB.java
 * Created : 25-Jul-2011
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
import emolib.classifier.machinelearning.ARNReduced.GraphElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.lang.Math;

/**
 * The <i>MultinomialNB</i> class is a Multinomial Naive 
 * Bayes (MNB) classifier.
 *
 * <p>
 * It is a probabilistic generative approach 
 * that builds a language model assuming
 * conditional independence among the features. In reality, this
 * conditional independence assumption does not hold for text
 * data, but even though the probability estimates of this
 * oversimplified model are of low quality because of this, its
 * classification decisions (based on Bayes’ decision rule) are
 * surprisingly good. The MNB combines efficiency (its has
 * an optimal time performance) with good accuracy.
 * </p>
 * <p>
 * The MultinomialNB follows the implementation described in (Manning, et al.,
 * 2008).
 * The same term weighting schemes as the ones used in the ARN-R are 
 * considered.
 * </p>
 * <p>
 * --<br>
 * (Manning, et al., 2008) Manning, C. D., Raghavan, P. and
 * Schutze, H., "An Introduction to Information Retrieval", 2008.
 * </p>
 *
 * @see emolib.classifier.machinelearning.ARNReduced
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class MultinomialNB extends Classifier {

    // Core
    private ArrayList<ArrayList<Double>> theProbs;
    private ArrayList<Double> thePriors;

    // Term Weighting scheme core
    private ARNReduced arnR;
    private boolean bigramFreq;
    private boolean posTags;
    private boolean stemming;
    private boolean synonyms;
    private boolean emotionDims;
    private boolean negation;
    //
    private boolean mutualInformation;
    private boolean chiSquare;
    private boolean termFreq;
    private int numFeatSel;
    //
    private ArrayList<Graph> categoryGraph;
    private Graph vocabularyGraph;


    /**
     * Main constructor of this exponential regression classifier.
     */
    public MultinomialNB() {
        theProbs = null;
        thePriors = null;
        arnR = null;
        categoryGraph = null;
        bigramFreq = false;
        posTags = false;
        stemming = false;
        synonyms = false;
        emotionDims = false;
        negation = false;
        mutualInformation = false;
        chiSquare = false;
        termFreq = false;
        numFeatSel = 0;
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.
     * FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        // APPLY MULTINOMIALNB (C,V,prior,condprob,d)
        int maxCat = 0;
        double maxScore = 0;
        boolean first = true;
        // 1) W ← EXTRACT TOKENS FROM DOC (V,d)
        Graph W = arnR.buildGraph(inputFeatures);
        // 2) for each c ∈ C
        double score = 0;
        for (int c = 0; c < categoryGraph.size(); c++) {
            // 3) do score(c) ← log prior (c)
            score = Math.log(thePriors.get(c).doubleValue());
            // 4) for each t ∈ W 
            for (int t = 0; t < vocabularyGraph.getNumberOfElements(); t++) {
                // 5) do score(c) += log condprob(t)(c)
                if (W.containsElement(vocabularyGraph.getElement(t))) {
                    score += Math.log(theProbs.get(c).get(t).doubleValue()) *
                        (double)W.getElementTermFrequency(
                        vocabularyGraph.getElement(t));
                }
            }
            // 8) return arg maxc∈C score(c)
            if (first) {
                maxCat = 0; // again
                maxScore = score;
                first = false;
            } else {
                if (score > maxScore) {
                    maxScore = score;
                    maxCat = c;
                }
            }
        }
        return categoryGraph.get(maxCat).getCategoryName();
    }


    /**
     * Training method based on the algorithm in (Manning, et al.,
     * 2008).
     * Nevertheless, doc counts are approximated by the sum of term freqs.
     *
     * (non-Javadoc)
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
        // From (Manning, et al., 2008)
        // TRAIN MULTINOMIALNB (C,D)
        arnR.train();
        categoryGraph = arnR.getCategoryGraphs();
        theProbs = new ArrayList<ArrayList<Double>>();
        thePriors = new ArrayList<Double>();
        // 1) V ← EXTRACT VOCABULARY (D)
        vocabularyGraph = arnR.getVocabularyGraph();
        // 2) N ← COUNT DOCS (D)
        double N = 0;
        for (int c = 0; c < categoryGraph.size(); c++) {
            N += (double)categoryGraph.get(c).getTotalSumTF();
        }
        double Nc = 0;
        double Tct = 0;
        ArrayList<Double> pVocab = null;
        // 3) for each c in C
        for (int c = 0; c < categoryGraph.size(); c++) {
            // 4) do Nc ← COUNT DOCS IN CLASS (D,c)
            Nc = (double)categoryGraph.get(c).getTotalSumTF();
            // 5) prior(c) ← Nc /N
            thePriors.add(new Double((double)Nc / (double)N));
            // 6) textc ← CONCATENATE TEXT OF ALL DOCS IN CLASS(D,c)
            // Done
            // 7) for each t in V 
            pVocab = new ArrayList<Double>();
            for (int t = 0; t < vocabularyGraph.getNumberOfElements(); t++) {
                // 8) do Tct ← COUNT TOKENS OF TERM (textc,t)
                Tct = (double)categoryGraph.get(c).
                    getElementTermFrequency(vocabularyGraph.getElement(t));
                // 9 and 10) for each t ∈ V, condprob(t)(c)
                pVocab.add(new Double((double)(Tct + 1) / (double)(Nc + 
                    vocabularyGraph.getNumberOfElements())));
            }
            theProbs.add(pVocab);
        }
        // 11) return V, prior, condprob
    }


    /**
     * Method to set the feature weights of the regression.
     * It is required that the private reference to the ARN-R is 
     * initialised.
     */
    private void setFeatureWeights() {
        arnR.setCOF(bigramFreq);
        arnR.setPOS(posTags);
        arnR.setStems(stemming);
        arnR.setSynonyms(synonyms);
        arnR.setFeatSelMI(mutualInformation, numFeatSel);
        arnR.setFeatSelChi2(chiSquare, numFeatSel);
        arnR.setFeatSelTF(termFreq, numFeatSel);
    }


    /**
     * Set the Mutual Information feature selection.
     *
     * @param mi The Mutual Information flag.
     * @param numF The number of relevant features desired.
     */
    public void setMI(boolean mi, int numF) {
        mutualInformation = mi;
        numFeatSel = numF;
    }


    /**
     * Set the Chi square feature selection.
     *
     * @param chi The Chi2 flag.
     * @param numF The number of relevant features desired.
     */
    public void setChi2(boolean chi, int numF) {
        chiSquare = chi;
        numFeatSel = numF;
    }


    /**
     * Set the Term Frequency feature selection.
     *
     * @param tf The Term Frequency flag.
     * @param numF The number of relevant features desired.
     */
    public void setTF(boolean tf, int numF) {
        termFreq = tf;
        numFeatSel = numF;
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
        theProbs = null;
        thePriors = null;
        arnR = null;
    }

}

