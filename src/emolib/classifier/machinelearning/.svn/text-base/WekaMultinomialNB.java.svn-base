/*
 * File    : WekaMultinomialNB.java
 * Created : 11-Aug-2010
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

import weka.core.Instances;
import weka.core.Instance;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.FastVector;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import org.junit.Test;
import org.junit.Assert;

/**
 * The <i>WekaMultinomialNB</i> class is a Multinomial Naive Bayes (MNB)
 * classifier.
 *
 * <p>
 * The core implementation of this general-purpose MNB is based on
 * <a href="http://www.cs.waikato.ac.nz/ml/weka/">Weka</a>'s
 * implementation of the NaiveBayesMultinomial class, which
 * works with continuous features.
 * The same term weighting schemes as the ones used in the ARN-R are considered.
 * </p>
 *
 * @see emolib.classifier.machinelearning.MultinomialNB
 * @see emolib.classifier.machinelearning.ARNReduced
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class WekaMultinomialNB extends Classifier {

    // WekaMultinomialNB core
    private NaiveBayesMultinomial theMNB;
    private Instances dataset;

    // Term Weighting scheme core
    private ARNReduced theARNR;
    private String theTWMeasure;
    private boolean theBigramFreq;
    private boolean interceptFeat;
    private boolean posTags;
    private boolean stemming;
    private boolean synonyms;
    private boolean emotionDims;
    private boolean negation;
    private boolean mutualInformation;
    private boolean chiSquare;
    private boolean termFreq;
    private int numFeatSel;
    //
    private HashMap<String, Integer> theCategoryHash;


    /**
     * Main constructor of this Multinomial Naive Bayes classifier.
     */
    public WekaMultinomialNB() {
        theMNB = null;
        theARNR = null;
        theCategoryHash = null;
        theTWMeasure = "binary";
        theBigramFreq = false;
        interceptFeat = true;
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
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        Iterator categoryLabels = theARNR.getCategoryList().iterator();
        String catLab = (String)categoryLabels.next();
        int catNum = theCategoryHash.get(catLab);
        //
        Graph tempGraph = theARNR.buildFullGraph(
            theARNR.buildGraph(inputFeatures));
        theARNR.applyTermWeighing(tempGraph, catNum);
        float[] tmpVector = tempGraph.exportWeighedVector();
        float[] weightedVector = createWeightedVector(tmpVector, inputFeatures);
        Instance tmpInstance = new Instance(weightedVector.length);
        tmpInstance.setDataset(dataset);
        for (int termNum = 0; termNum < weightedVector.length; termNum++) {
            tmpInstance.setValue(termNum, weightedVector[termNum]);
        }
        try {
            double categoryLabel = theMNB.classifyInstance(tmpInstance);
            while ((double)catNum != categoryLabel) {
                catLab = (String)categoryLabels.next();
                catNum = theCategoryHash.get(catLab);
            }
        } catch (Exception e) {
            System.out.println("SVM: Prediction error!");
            e.printStackTrace();
        }
        //
        return catLab;
    }


    /**
     * Function to add the attributes to the system.
     *
     * @param atts The attributes vector.
     * @param terms The terms vector.
     */
    private void addAttributes(FastVector atts, ArrayList<String> terms) {
        if (interceptFeat) {
            atts.addElement(new Attribute("intercept"));
        }
        // Terms are always considered.
        for (int t = 0; t < terms.size(); t++) {
            atts.addElement(new Attribute(terms.get(t)));
        }
        if (emotionDims) {
            atts.addElement(new Attribute("valence"));
            atts.addElement(new Attribute("activation"));
            atts.addElement(new Attribute("control"));
        }
        if (negation) {
            atts.addElement(new Attribute("negation"));
        }
    }


    /**
     * Function to create the weighted vector apt for this classifier.
     * Same implementation as Logistic, but dealing with array of floats
     * instead of doubles.
     *
     * @param lexvect The TW vector of lexical features.
     * @param fbox All the feats.
     *
     * @return The weighted vector.
     */
    private float[] createWeightedVector(float[] lexvect, FeatureBox fbox) {
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
        float[] weightedVector = new float[totalen];
        if (interceptFeat) {
            weightedVector[0] = (float)1;
            offset++;
        }
        for (int i = 0; i < lexvect.length; i++) {
            weightedVector[i + offset] = lexvect[i];
        }
        offset += lexvect.length;
        if (emotionDims) {
            weightedVector[offset] = fbox.getValence();
            weightedVector[offset + 1] = fbox.getActivation();
            weightedVector[offset + 2] = fbox.getControl();
            offset += 3;
        }
        if (negation) {
            if (fbox.getNegation()) {
                weightedVector[offset] = (float)1;
            } else {
                weightedVector[offset] = (float)0;
            }
        }
        return weightedVector;
    }


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
            theARNR.inputTrainingExample((FeatureBox)exFeat.next(),
                (String)exCat.next());
        }
        theARNR.train();
        theCategoryHash = theARNR.getCategoryHash();
        // Category attribute creation
        FastVector catNominalValues = new FastVector(theCategoryHash.size());
        Iterator categoryIter = theARNR.getCategoryList().iterator();
        while (categoryIter.hasNext()) {
            catNominalValues.addElement((String)categoryIter.next());
        }
        Attribute categoryAttribute = new Attribute("CATEGORY_LABEL",
            catNominalValues);
        // Attributes creation (the terms)
        Graph tempGraph = theARNR.buildFullGraph(theARNR.buildGraph(
            new FeatureBox("foo")));
        ArrayList<String> terms = tempGraph.getArrayOfTerms();
        FastVector attributes = new FastVector();
        addAttributes(attributes, terms);
        attributes.addElement(categoryAttribute);
        dataset = new Instances("dataset", attributes, exampleFeatures.size());
        dataset.setClassIndex(dataset.numAttributes() - 1);
        // Feeding the dataset with the instances
        Instance tmpInstance;
        float[] tmpVector, weightedVector;
        for (int instNum = 0; instNum < exampleFeatures.size(); instNum++) {
            tempGraph = theARNR.buildFullGraph(theARNR.buildGraph(
                exampleFeatures.get(instNum)));
            theARNR.applyTermWeighing(tempGraph,
                theCategoryHash.get(exampleCategories.get(instNum)).intValue());
            tmpVector = tempGraph.exportWeighedVector();
            weightedVector = createWeightedVector(tmpVector,
                exampleFeatures.get(instNum));
            tmpInstance = new Instance(dataset.numAttributes());
            tmpInstance.setDataset(dataset);
            for (int termNum = 0; termNum < weightedVector.length; termNum++) {
                tmpInstance.setValue(termNum, weightedVector[termNum]);
            }
            tmpInstance.setValue(weightedVector.length,
                exampleCategories.get(instNum));
            dataset.add(tmpInstance);
        }
        // Time to train the Multinomial NB.
        // Pokemon programming style
        try {
            theMNB = new NaiveBayesMultinomial();
            theMNB.buildClassifier(dataset);
        } catch (Exception e) {
            System.out.println("WekaMultinomialNB: Training error!");
            e.printStackTrace();
        }
    }


    /**
     * Method to set the feature weights.
     * It is required that the private reference to the ARN-R is initialised.
     */
    private void setFeatureWeights() {
        theARNR.setTermWeighingMeasure(theTWMeasure);
        theARNR.setCOF(theBigramFreq);
        theARNR.setPOS(posTags);
        theARNR.setStems(stemming);
        theARNR.setSynonyms(synonyms);
        theARNR.setFeatSelMI(mutualInformation, numFeatSel);
        theARNR.setFeatSelChi2(chiSquare, numFeatSel);
        theARNR.setFeatSelTF(termFreq, numFeatSel);
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
        theMNB = null;
        theARNR = null;
        theCategoryHash = null;
    }


    /**
     * Functionality test.
     */
    @Test public void simpleClassification() {
        WekaMultinomialNB mnb = new WekaMultinomialNB();
        FeatureBox feat = new FeatureBox();
        feat.setWords("I hate going to the dentist .");
        mnb.inputTrainingExample(feat, "NEG");
        //
        feat = new FeatureBox();
        feat.setWords("I swim a lot .");
        mnb.inputTrainingExample(feat, "NEU");
        //
        feat = new FeatureBox();
        feat.setWords("I love reading books .");
        mnb.inputTrainingExample(feat, "POS");
        //
        mnb.train();
        //
        feat = new FeatureBox();
        feat.setWords("I like my dentist .");
        Assert.assertTrue(mnb.getCategory(feat).equals("NEG"));
        //
        feat = new FeatureBox();
        feat.setWords("You love .");
        Assert.assertTrue(mnb.getCategory(feat).equals("POS"));
    }

}

