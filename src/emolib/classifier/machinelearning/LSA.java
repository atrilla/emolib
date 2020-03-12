/*
 * File    : LSA.java
 * Created : 01-Dec-2010
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

package emolib.classifier.machinelearning;

import emolib.classifier.Classifier;
import emolib.classifier.FeatureBox;

import emolib.classifier.machinelearning.ARNReduced.Graph;

import com.aliasi.matrix.SvdMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.lang.Math;

import org.junit.Test;
import org.junit.Assert;

/**
 * The <i>LSA</i> class is a Latent Semantic Analysis classifier.
 *
 * <p>
 * It learns the latent semantic space by computing the Singular
 * Value Decomposition (SVD) of the term-class matrix (i.e.,
 * constructing a low-rank approximation with its principal eigenvectors). 
 * The cosine similarity between the class vectors
 * (right singular vectors) and the query text vectors (obtained by
 * adding the observed term vectors, i.e., the left singular vectors)
 * is used to make decisions in the reduced latent space.
 * </p>
 * <p>
 * The core implementation of this LSA classifier is based on
 * <a href="http://alias-i.com/lingpipe/index.html">LingPipe</a>.
 * The same term weighting schemes as the ones used in the ARN-R are considered.
 * </p>
 *
 * @see emolib.classifier.machinelearning.ARNReduced
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class LSA extends Classifier {

    // LSA core: the SVD matrix
    private double[] theScales;
    private double[][] theTermVectors;
    private double[][] theDocVectors;
    //
    private ArrayList<String> theTerms;
    private ArrayList<String> theCategoryLabels;
    private ArrayList<ArrayList<Double>> theCategoryVectors;

    // Term Weighting scheme core
    private ARNReduced theARNR;
    private String theTWMeasure;
    private boolean theBigramFreq;
    private boolean posTags;
    private boolean stemming;
    private boolean synonyms;


    /**
     * Main constructor of this LSA classifier.
     * The LSA object is actually initialised when the SVD matrix is constructed.
     */
    public LSA() {
        theARNR = null;
        theTWMeasure = "binary";
        theBigramFreq = false;
        theTerms = null;
        theScales = null;
        theTermVectors = null;
        theDocVectors = null;
        theCategoryVectors = null;
        theCategoryLabels = null;
        posTags = false;
        stemming = false;
        synonyms = false;
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        String mostProbableCategory = theCategoryLabels.get(0);
        double similarity = getSimilarity(inputFeatures, mostProbableCategory);
        for (int i = 1; i < theCategoryLabels.size(); i++) {
            if (getSimilarity(inputFeatures, theCategoryLabels.get(i)) > similarity) {
                mostProbableCategory = theCategoryLabels.get(i);
                similarity = getSimilarity(inputFeatures, theCategoryLabels.get(i));
            }
        }
        //
        return mostProbableCategory;
    }


    /**
     * Function to retrieve the similarity of a given text with a given category.
     *
     * @param inputText The given text.
     * @param cat The given category.
     *
     * @return The resulting similarity.
     */
    private double getSimilarity(FeatureBox inputText, String cat) {
        double similarity = 0;
        double[] inputVector;
        ArrayList<String> inputTerms = theARNR.buildGraph(inputText).getArrayOfTerms();
        for (int i = 0; i < theCategoryLabels.size(); i++) {
            if (cat.equals(theCategoryLabels.get(i))) {
                inputVector = new double[theScales.length];
                Arrays.fill(inputVector, 0.0);
                for (int term = 0; term < inputTerms.size(); term++) {
                    addTermVector(inputTerms.get(term), inputVector);
                }
                // here
                ArrayList<Double> categoryVector = theCategoryVectors.get(i);
                double[] catVector = new double[categoryVector.size()];
                for (int j = 0; j < categoryVector.size(); j++) {
                    catVector[j] = categoryVector.get(j).doubleValue();
                }
                similarity = cosine(inputVector, catVector);
                break;
            }
        }
        //
        return similarity;
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
        double[] weightedVector = new double[totalen];
        for (int i = 0; i < lexvect.length; i++) {
            weightedVector[i + offset] = (double)lexvect[i];
        }
        offset += lexvect.length;
        return weightedVector;
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
     * Function to compute the cosine between two given vectors as a
     * similarity measure.
     *
     * @param xs The first vector.
     * @param ys The second vector.
     *
     * @return The cosine between the two vectors.
     */
    private double cosine(double[] xs, double[] ys) {
        double product = 0.0;
        double xsLengthSquared = 0.0;
        double ysLengthSquared = 0.0;
        for (int k = 0; k < xs.length; ++k) {
            double sqrtScale = Math.sqrt(theScales[k]);
            double scaledXs = sqrtScale * xs[k];
            double scaledYs = sqrtScale * ys[k];
            xsLengthSquared += scaledXs * scaledXs;
            ysLengthSquared += scaledYs * scaledYs;
            product += scaledXs * scaledYs;
        }
        return product / Math.sqrt(xsLengthSquared * ysLengthSquared);
    }


    /**
     * Training method based on the SVD decomposition.
     */
    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#trainingProcedure()
     */
    public void trainingProcedure() {
        ArrayList<FeatureBox> exampleFeatures = getListOfExampleFeatures();
        ArrayList<String> exampleCategories = getListOfExampleCategories();
        theARNR = new ARNReduced();
        setFeatureWeights();
        Iterator exFeat = exampleFeatures.iterator();
        Iterator exCat = exampleCategories.iterator();
        while (exFeat.hasNext() && exCat.hasNext()) {
            theARNR.inputTrainingExample((FeatureBox)exFeat.next(), (String)exCat.next());
        }
        theARNR.train();
        HashMap<String, Integer> categoryHash = theARNR.getCategoryHash();
        theCategoryLabels = theARNR.getCategoryList();
        // Terms
        Graph tempGraph = theARNR.buildFullGraph(theARNR.buildGraph(new FeatureBox("foo")));
        theTerms = tempGraph.getArrayOfTerms();
        // Time to build the term-document matrix, indexed as matrix[row][col]
        // i.e., matrix[term][doc]
        double[][] termDocMatrix = new double[theTerms.size()][exampleFeatures.size()];
        float[] tmpVector;
        double[] weightedVector;
        for (int docNum = 0; docNum < exampleFeatures.size(); docNum++) {
            tempGraph = theARNR.buildFullGraph(theARNR.buildGraph(exampleFeatures.get(docNum)));
            theARNR.applyTermWeighing(tempGraph, categoryHash.get(exampleCategories.get(docNum)).intValue());
            tmpVector = tempGraph.exportWeighedVector();
            weightedVector = createWeightedVector(tmpVector, exampleFeatures.get(docNum));
            for (int termNum = 0; termNum < weightedVector.length; termNum++) {
                termDocMatrix[termNum][docNum] = weightedVector[termNum];
            }
        }
        // SVD decomposition parameters
        int maxFactors = 2;
        double featureInit = 0.01;
        double initialLearningRate = 0.005;
        int annealingRate = 1000;
        double regularization = 0.00;
        double minImprovement = 0.0000;
        int minEpochs = 10;
        int maxEpochs = 50000;
        //
        SvdMatrix matrix = SvdMatrix.svd(termDocMatrix,
            maxFactors,
            featureInit,
            initialLearningRate,
            annealingRate,
            regularization,
            null,
            minImprovement,
            minEpochs,
            maxEpochs);
        // Final training results
        theScales = matrix.singularValues();
        theTermVectors = matrix.leftSingularVectors();
        theDocVectors = matrix.rightSingularVectors();
        // Extraction of the category vectors
        ArrayList<Graph> listOfCatGraphs = theARNR.getCategoryGraphs();
        ArrayList<String> categoryTerms;
        double[] categoryVector;
        ArrayList<Double> catV;
        theCategoryVectors = new ArrayList<ArrayList<Double>>();
        for (int catLabNum = 0; catLabNum < theCategoryLabels.size(); catLabNum++) {
            tempGraph = listOfCatGraphs.get(catLabNum);
            categoryTerms = tempGraph.getArrayOfTerms();
            categoryVector = new double[theScales.length];
            Arrays.fill(categoryVector, 0.0);
            for (int term = 0; term < categoryTerms.size(); term++) {
                addTermVector(categoryTerms.get(term), categoryVector);
            }
            catV = new ArrayList<Double>();
            for (int i = 0; i < categoryVector.length; i++) {
                catV.add(new Double(categoryVector[i]));
            }
            theCategoryVectors.add(catV);
        }
    }


    /**
     * Method to iterate over the terms and add their term vectors to the
     * category vector.
     *
     * @param term The term in question.
     * @param categoryVector The category vector to be computed.
     */
    private void addTermVector(String term, double[] categoryVector) {
        if (theTerms.contains(term)) {
            int indexTerm = theTerms.indexOf(term);
            for (int dim = 0; dim < theScales.length; dim++) {
                categoryVector[dim] += theTermVectors[indexTerm][dim];
            }
        }
    }


    /**
     * Method to set the feature weights of the LSA term-doc matrix.
     * It is required that the private reference to the ARN-R is initialised.
     */
    private void setFeatureWeights() {
        theARNR.setTermWeighingMeasure(theTWMeasure);
        theARNR.setCOF(theBigramFreq);
        theARNR.setPOS(posTags);
        theARNR.setStems(stemming);
        theARNR.setSynonyms(synonyms);
    }


    /**
     * Method to set the TW method.
     *
     * @param twm The TW method.
     */
    public void setTermWeighingMeasure(String twm) {
        theTWMeasure = twm;
    }


    /**
     * Method to consider bigram frequencies.
     *
     * @param cof The COF flag.
     */
    public void setCOF(boolean cof) {
        theBigramFreq = cof;
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
        theARNR = null;
        theTerms = null;
        theScales = null;
        theTermVectors = null;
        theDocVectors = null;
        theCategoryVectors = null;
        theCategoryLabels = null;
    }


    /**
     * Functionality test.
     */
    @Test public void simpleClassification() {
        LSA lsa = new LSA();
        FeatureBox feat = new FeatureBox();
        feat.setWords("I hate going to the dentist .");
        lsa.inputTrainingExample(feat, "NEG");
        //
        feat = new FeatureBox();
        feat.setWords("I swim a lot .");
        lsa.inputTrainingExample(feat, "NEU");
        //
        feat = new FeatureBox();
        feat.setWords("I love reading books .");
        lsa.inputTrainingExample(feat, "POS");
        //
        lsa.train();
        //
        feat = new FeatureBox();
        feat.setWords("I like my dentist .");
        Assert.assertTrue(lsa.getCategory(feat).equals("NEG"));
        //
        feat = new FeatureBox();
        feat.setWords("You love .");
        Assert.assertTrue(lsa.getCategory(feat).equals("POS"));
    }

}

