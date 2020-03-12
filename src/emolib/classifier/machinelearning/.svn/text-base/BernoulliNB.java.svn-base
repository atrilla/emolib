/*
 * File    : BernoulliNB.java
 * Created : 24-Jul-2011
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
 * The <i>BernoulliNB</i> class is a Bernoulli Naive Bayes classifier.
 *
 * <p>
 * The BernoulliNB follows the implementation described in (Manning, et al.,
 * 2008). The model essentially assumes conditional independence among the 
 * presence of terms in a given text.
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
public class BernoulliNB extends Classifier {

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
    private ArrayList<Graph> categoryGraph;
    private Graph vocabularyGraph;


    /**
     * Main constructor of this exponential regression classifier.
     */
    public BernoulliNB() {
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
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.
     * FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        // APPLY BERNOULLINB (C,V,prior,condprob,d)
        int maxCat = 0;
        double maxScore = 0;
        boolean first = true;
        // 1) Vd ← EXTRACT TERMS FROM DOC (V,d)
        Graph Vd = arnR.buildGraph(inputFeatures);
        // 2) for each c ∈ C
        double score = 0;
        for (int c = 0; c < categoryGraph.size(); c++) {
            // 3) do score(c) ← log prior (c)
            score = Math.log(thePriors.get(c).doubleValue());
            // 4) for each t ∈ V
            for (int t = 0; t < vocabularyGraph.getNumberOfElements(); t++) {
                // 5) do if t ∈ Vd
                if (Vd.containsElement(vocabularyGraph.getElement(t))) {
                    // 6) then score(c) += log condprob(t)(c)
                    score += Math.log(theProbs.get(c).get(t).doubleValue());
                // 7) else score(c) += log(1 − condprob(t)(c))
                } else {
                    score += Math.log((double)1 - 
                        theProbs.get(c).get(t).doubleValue());
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
        // TRAIN BERNOULLINB (C,D)
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
        double Nct = 0;
        ArrayList<Double> pVocab = null;
        // 3) for each c in C
        for (int c = 0; c < categoryGraph.size(); c++) {
            // 4) do Nc ← COUNT DOCS IN CLASS (D,c)
            Nc = (double)categoryGraph.get(c).getTotalSumTF();
            // 5) prior(c) ← Nc /N
            thePriors.add(new Double((double)Nc / (double)N));
            // 6) for each t in V 
            pVocab = new ArrayList<Double>();
            for (int t = 0; t < vocabularyGraph.getNumberOfElements(); t++) {
                    // 7) do Nct ← COUNT DOCS IN CLASS CONTAINING TERM (D,c,t) 
                Nct = (double)categoryGraph.get(c).
                    getElementTermFrequency(vocabularyGraph.getElement(t));
                // 8) condprob(t)(c) ← ( Nct + 1)/( Nc + 2)
                pVocab.add(new Double((double)(Nct + 1) / (double)(Nc + 2)));
            }
            theProbs.add(pVocab);
        }
        // 9) return V, prior, condprob
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

