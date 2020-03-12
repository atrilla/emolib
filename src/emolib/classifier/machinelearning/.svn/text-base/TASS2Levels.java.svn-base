/*
 * File    : TASS2Levels.java
 * Created : 1-Jul-2012
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
 * The <i>TASS2Levels</i> class is a Hierarchical classifier for
 * the SEPLN TASS competition.
 *
 * Level 1: C-NEU vs. NEU
 * Level 2: C-NEU, N+,N,NONE,P,P+
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class TASS2Levels extends Classifier {

    private MultinomialNB MNBPolarity, MNBSentiment;

    /**
     * Main constructor of this exponential regression classifier.
     */
    public TASS2Levels() {
        MNBPolarity = new MultinomialNB();
        MNBSentiment = new MultinomialNB();
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.
     * FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        String theCat = MNBPolarity.getCategory(inputFeatures);
        if (!theCat.equals("NEU")) {
            // equals NO-NEU
            theCat = MNBSentiment.getCategory(inputFeatures);
        }
        return theCat;
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
        Iterator exFeat = exampleFeatures.iterator();
        Iterator exCat = exampleCategories.iterator();
        String presentCat;
        FeatureBox presentFeat;
        while (exFeat.hasNext() && exCat.hasNext()) {
            presentFeat = (FeatureBox)exFeat.next();
            presentCat = (String)exCat.next();
            if (presentCat.equals("NEU")) {
                MNBPolarity.inputTrainingExample(presentFeat, presentCat);
            } else {
                MNBPolarity.inputTrainingExample(presentFeat, "NO-NEU");
                MNBSentiment.inputTrainingExample(presentFeat, presentCat);
            }
        }
        MNBPolarity.train();
        MNBSentiment.train();
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
        MNBPolarity = new MultinomialNB();
        MNBSentiment = new MultinomialNB();
    }

}

