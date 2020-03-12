/*
 * File    : FiveIntervalsKE.java
 * Created : 05-Feb-2009
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

package emolib.classifier.heuristic;

import emolib.classifier.Classifier;
import emolib.classifier.FeatureBox;

/**
 * The <i>FiveIntervalsKE</i> is a heuristic rules-based classifier
 * operating on the circumplex with emotions.
 *
 * <p>
 * There are five possible emotions (surprise, joy, sadness, anger and fear, apart
 * from the neutral feeling) according to the proposal of David Garc&iacute; in
 * his Master's Thesis, derived from (Albrecht <i>et al.</i>, 2005).
 * </p>
 * <p>
 * The KE (Knowledge Engineering) particle refers to the fact that these rules have been
 * stated by a human expert, thus this is not a trainable classifier.
 * Then, it is not saved nor loaded (but used directly) and by default it has been hard coded
 * to return the emotion label in English.
 * </p>
 * <p>
 * --<br>
 * (Albrecht  <i>et al.</i>, 2005) Albrecht, I., Scr&#246;der, M., Haber, J. and
 * Seidel, H.-P., <i>"Mixed Feelings: Expression of Non-Basic Emotions in a Muscle-Based Talking Head"</i>,
 * DFKI, Germany, 2005.
 * </p>
 *
 * @author David Garc&iacute;a
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class FiveIntervalsKE extends Classifier {

    /**
     * Main constructor of this rules classifier.
     */
    public FiveIntervalsKE() {
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        String category = "";
        float mVal, mAct, mCon;

        mVal = inputFeatures.getValence();
        mAct = inputFeatures.getActivation();
        mCon = inputFeatures.getControl();

        if ((mVal == -1) && (mAct == -1) && (mCon == -1)) {
            category = "neutral";
        } else if ((mVal >= 8.5) && (mAct >= 6.35) && (mCon >= 6.5)) {
            category = "surprise";
        } else if ((mVal >= 6.445) && (mAct >= 5.86) && (mCon >= 5)) {
            category = "happiness";
        } else if ((mVal <= 3 && mVal >= 0) && (mAct <= 4.575) && (mCon > 1.5 && mCon <= 3.75)) {
            category = "sorrow";
        } else if ((mVal <= 3.25) && (mAct >= 6.25) && (mCon > 3.5 && mCon <= 4.5)) {
            category = "anger";
        } else if ((mVal <= 3) && (mAct < 7.5) && (mCon <= 3.75)) {
            category = "fear";
        } else {
            category = "neutral";
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

