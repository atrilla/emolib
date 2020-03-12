/*
 * File    : ThreeIntervalsKE.java
 * Created : 15-Apr-2010
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

package emolib.classifier.heuristic;

import emolib.classifier.Classifier;
import emolib.classifier.FeatureBox;

/**
 * The <i>ThreeIntervalsKE</i> is a heuristic rules-based classifier
 * operating on the circumplex with sentiments.
 *
 * <p>
 * This class works exactly as the Five Intervals heuristic rules classifier,
 * but displays the sentiment corresponding to the predicted emotion.
 * </p>
 *
 * @see emolib.classifier.heuristic.FiveIntervalsKE FiveIntervalsKE
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class ThreeIntervalsKE extends Classifier {

    // A Five intervals heuristic rules classifier
    private FiveIntervalsKE fiveKE;


    /**
     * Main constructor of this neuristic rules classifier.
     */
    public ThreeIntervalsKE() {
        fiveKE = new FiveIntervalsKE();
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        String category = "";

        category = fiveKE.getCategory(inputFeatures);
        if (category.equals("surprise") || category.equals("happiness")) {
            category = "P";
        } else if (category.equals("sorrow") || category.equals("anger") || category.equals("fear")) {
            category = "N";
        }

        return category;
    }


    /**
     * Void method to train required by the Classifier class.
     */
    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#trainingProcedure()
     */
    public void trainingProcedure() {
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

}

