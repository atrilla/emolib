/*
 * File    : Gaussian.java
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

/**
 * The <i>Gaussian</i> class models a given dataset with a univariate normal
 * distribution.
 *
 * <p>
 * This density is defined by two variables: the mean and the standard
 * deviation/variance of the
 * population. For data mining purposes, these parameters may be estimated with
 * the Maximum Likelihood method. Its parameter MLE are the sample mean and the
 * sample variance.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class Gaussian implements ClassConditionalDensity {


    /**
     * Parameters of the density.
     */
    private float mean;
    private float std;


    /**
     * Main constructor of this density.
     * Constructs a standarized normal distribution.
     */
    public Gaussian() {
        mean = 0;
        std = 1;
    }


    /**
     * Constructor with parameters.
     * Constructs a normal distribution with the given parameters: the
     * mean and the std.
     *
     * @param mean The mean.
     * @param std The standard deviation.
     */
    public Gaussian(float mean, float std) {
        this.mean = mean;
        this.std = std;
    }


    /**
     * Function to retrieve the mean of this distribution.
     *
     * @return The mean.
     */
    public float getMean() {
        return mean;
    }


    /* (non-Javadoc)
     * @see emolib.classifier.machinelearning.density.ClassConditionalDensity#getProbability(java.lang.float)
     */
    public float getLikelihood(float feature) {
        Float theMean = new Float(mean);
        Float theStd = new Float(std);
        Float theFeature = new Float(feature);

        double dMean = theMean.doubleValue();
        double dStd = theStd.doubleValue();
        double dFeature = theFeature.doubleValue();
        Float theProb = new Float((1 / (Math.sqrt(2 * Math.PI) * dStd)) * Math.exp((-0.5) *
            Math.pow((dFeature - dMean) / dStd, 2)));

        return theProb.floatValue();
    }

}

