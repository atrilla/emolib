/*
 * File    : ClassConditionalDensity.java
 * Created : 30-Oct-2009
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

package emolib.classifier.machinelearning.density;

import emolib.classifier.FeatureBox;

/**
 * The <i>ClassConditionalDensity</i> interface defines the function to retrieve
 * the probabilities conditioned on a class given an observed feature.
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public interface ClassConditionalDensity {

    /**
     * The likelihood probabitity given a feature vector.
     *
     * @param feature The feature.
     *
     * @return prob The associated probability.
     */
    public float getLikelihood(float feature);

}

