/*
 * File    : ARNReduced.java
 * Created : 26-Dec-2009
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

package emolib.classifier.machinelearning;

import java.util.ArrayList;
import java.lang.Math;
import java.util.HashMap;
//import java.util.List;
//import java.io.*;

import emolib.util.conf.*;
import emolib.util.proc.*;
import emolib.classifier.Classifier;
import emolib.classifier.FeatureBox;

//import org.jdom.*;
//import org.jdom.output.*;
//import org.jdom.input.*;

import Jama.Matrix;

import org.junit.Test;
import org.junit.Assert;

/**
 * The <i>ARNReduced</i> classifies according to a cosine similarity in
 * a weighted Vector Space Model with co-occurrences, which are assumed to
 * capture the style in text.
 *
 * <p>
 * The Associative Relational Network - Reduced is word co-occurrence 
 * network-based model, see the figure below, which
 * constructs a Vector Space Model (VSM) with a term selection method 
 * "on the fly" based on the observation
 * of test features (Al&iacute;as et al., 2008).
 * This term selection refinement is reported to improve the classical VSM
 * for classification. Dense vectors representing the input text and the
 * class are retrieved (no learning process is involved) and
 * evaluated by the cosine similarity measure. The basic
 * hypothesis in using the ARN-R for classification is the
 * contiguity hypothesis, where terms in the same class form
 * a contiguous region and regions of different classes do
 * not overlap.
 * </p>
 * <p>
 * <img src="doc-files/arn.png" width="300" 
 * alt="Associative Relational Network - Reduced"/>
 * </p>
 * <p>
 * The ARN-R also provides several methods, i.e., criteria,
 * 1) to weight the features in order to enhance their 
 * discriminative features,
 * and 2) to select the most relevant features in order to reduce
 * the sparsity in the VSM. These approaches intend to
 * simplify the model in order to generalise better.
 * </p>
 * <p>
 * In addition, the ARNReduced provides a classical VSM implementation
 * which enables the retrieval of sparse vectors, and therefore standardises 
 * the interface to the textual features for any vector-based classifier.
 * </p>
 * <p>
 * --<br>
 * (Al&iacute;as et al., 2008) Francesc Al&iacute;as, Xavier Sevillano, Joan Claudi Socor&oacute;
 * and Xavier Gonzalvo, "Towards high quality next-generation Text-to-Speech synthesis: a
 * Multidomain approach by automatic domain classification", IEEE Transactions on Audio, Speech
 * and Language Processing (Special issue on New Approaches to Statistical Speech and Text Processing)
 * (ISSN 1558-7916), vol. 16 (7), pp. 1340-1354, September.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class ARNReduced extends Classifier {

    /**
     * Property to indicate a pre-trained classifier.
     */
    public final static String PROP_EXTERNAL_FILE = "external_file";


    private String externalFile;

    // Classifier specific parameters
    private String termWeighingMeasure;
    private String similarityMeasure;
    private boolean assessCOF;
    private boolean assessPOS;
    private boolean assessSyns;
    private boolean assessStems;
    private boolean featSelMI;
    private boolean featSelChi2;
    private boolean featSelTF;
    private int numSelFeats;
    private ArrayList<Graph> categoryGraph;
    private Graph fullGraph;
    //
    private HashMap<String, Integer> soleCategories;
    private String[] categoryRanking;


    /**
     * Inner class representing an element of the graph.
     */
    public class GraphElement {

        private boolean isNodeFlag;
        private boolean isLinkFlag;
        private int termFrequency;
        private String term, termLeft, termRight;
        private float weighedMeasure;
        private double utilityMeasure;

        /**
         * Constructor.
         */
        public GraphElement() {
            isNodeFlag = false;
            isLinkFlag = false;
            termFrequency = 1;
            term = "";
            termLeft = "";
            termRight = "";
            weighedMeasure = 1;
            utilityMeasure = 0;
        }


        /**
         * Method to set the utility measure.
         *
         * @param um The value of the utility measure.
         */
        public void setUtilityMeasure(double um) {
            utilityMeasure = um;
        }


        /**
         * Function to retrieve the utility measure.
         *
         * @return The value of the utility measure.
         */
        public double getUtilityMeasure() {
            return utilityMeasure;
        }


        /**
         * Method to set the weighed measure.
         *
         * @param wm The value of the weighed measure.
         */
        public void setWeighedMeasure(float wm) {
            weighedMeasure = wm;
        }


        /**
         * Function to retrieve the weighed measure.
         *
         * @return The value of the weighed measure.
         */
        public float getWeighedMeasure() {
            return weighedMeasure;
        }


        /**
         * Method to set this graph element as a node.
         */
        public void setAsNode() {
            isNodeFlag = true;
        }


        /**
         * Method to set this graph element as a link.
         */
        public void setAsLink() {
            isLinkFlag = true;
        }


        /**
         * Function to see if this graph element is a node.
         *
         * @return True if it is a node.
         */
        public boolean isNode() {
            return isNodeFlag;
        }


        /**
         * Function to see if this graph element is a link.
         *
         * @return True if it is a link.
         */
        public boolean isLink() {
            return isLinkFlag;
        }


        /**
         * Method to set the term of this node.
         *
         * @param theTerm The term to set.
         */
        public void setTerm(String theTerm) {
            if (isNode()) {
                term = theTerm;
            } else {
                System.out.println("GraphElement ERROR! Trying to set a term to a link!");
            }
        }


        /**
         * Method to set the left term of this link.
         *
         * @param theTerm The term to set.
         */
        public void setLeftTerm(String theTerm) {
            if (isLink()) {
                termLeft = theTerm;
            } else {
                System.out.println("GraphElement ERROR! Trying to set a left term to a node!");
            }
        }


        /**
         * Method to set the right term of this link.
         *
         * @param theTerm The term to set.
         */
        public void setRightTerm(String theTerm) {
            if (isLink()) {
                termRight = theTerm;
            } else {
                System.out.println("GraphElement ERROR! Trying to set a right term to a node!");
            }
        }


        /**
         * Method to get the term of this node.
         *
         * @return The term.
         */
        public String getTerm() {
            String theTerm = "";

            if (isNode()) {
                theTerm = term;
            } else {
                System.out.println("GraphElement ERROR! Trying to get a term from a link!");
            }

            return theTerm;
        }


        /**
         * Method to get the left term of this link.
         *
         * @return The term.
         */
        public String getLeftTerm() {
            String theTerm = "";

            if (isLink()) {
                theTerm = termLeft;
            } else {
                System.out.println("GraphElement ERROR! Trying to get a left term from a node!");
            }

            return theTerm;
        }


        /**
         * Method to get the right term of this link.
         *
         * @return The term.
         */
        public String getRightTerm() {
            String theTerm = "";

            if (isLink()) {
                theTerm = termRight;
            } else {
                System.out.println("GraphElement ERROR! Trying to get a right term from a node!");
            }

            return theTerm;
        }


        /**
         * Method to add one count.
         */
        public void addOneCount() {
            termFrequency++;
        }


        /**
         * Method to set the number of counts.
         *
         * @param tf The term frequency to set.
         */
        public void setTermFrequency(int tf) {
            termFrequency = tf;
        }


        /**
         * Function to retrieve the number of counts.
         *
         * @return The number of counts (term frequency):
         */
        public int getTermFrequency() {
            return termFrequency;
        }


        /**
         * Function to clone this element.
         *
         * @return A clone of this element.
         */
        public GraphElement cloneElement() {
            GraphElement theClone = new GraphElement();
            if (isNode()) {
                theClone.setAsNode();
                theClone.setTerm(getTerm());
            } else {
                theClone.setAsLink();
                theClone.setLeftTerm(getLeftTerm());
                theClone.setRightTerm(getRightTerm());
            }
            theClone.setTermFrequency(getTermFrequency());

            return theClone;
        }

    }


    /**
     * Generic graph inner class.
     * As a general rule, the graph contains the minimum amount of valuable information, i.e.,
     * the term frequencies. For more enhanced IR measures the ARN should be able to manage
     * with these rates.
     */
    public class Graph {

        private ArrayList<GraphElement> elementStruct;
        private String categoryName;

        /**
         * Graph constructor.
         */
        public Graph() {
            elementStruct = new ArrayList<GraphElement>();
            categoryName = "";
        }


        /**
         * Function to retrieve the number of elements of this graph.
         *
         * @return The number of elements.
         */
        public int getNumberOfElements() {
            return elementStruct.size();
        }


        /**
         * Function to retrieve the number of nodes (ie words) of this graph.
         *
         * @return The number of nodes.
         */
        public ArrayList<String> getListOfNodes() {
            ArrayList<String> listWords = new ArrayList<String>();
            GraphElement graphElem;
            //
            for (int i = 0; i < getNumberOfElements(); i++) {
                graphElem = getElement(i);
                if (graphElem.isNode()) {
                    listWords.add(graphElem.getTerm());
                }
            }
            //
            return listWords;
        }


        /**
         * Function to retrieve a list of terms from this graph.
         *
         * @return The list of terms.
         */
        public ArrayList<String> getArrayOfTerms() {
            GraphElement tmpElem;
            String auxTerm = "";
            ArrayList<String> theTerms = new ArrayList<String>();
            for (int elemNum = 0; elemNum < getNumberOfElements(); elemNum++) {
                tmpElem = getElement(elemNum);
                if (tmpElem.isNode()) {
                    auxTerm = tmpElem.getTerm();
                } else {
                    auxTerm = tmpElem.getLeftTerm() + "_" + tmpElem.getRightTerm();
                }
                theTerms.add(auxTerm);
            }
            //
            return theTerms;
        }


        /**
         * Method to add a category name to this graph.
         *
         * @param name The name.
         */
        public void setCategoryName(String name) {
            categoryName = name;
        }


        /**
         * Function to get the category name of this graph.
         *
         * @return The category name of this graph.
         */
        public String getCategoryName() {
            return categoryName;
        }


        /**
         * Function to retrieve the total sum of term frequencies in this structure.
         *
         * @return The total sum of term frequencies.
         */
        public int getTotalSumTF() {
            int tfsum = 0;
            for (int elem = 0; elem < getNumberOfElements(); elem++) {
                tfsum += getElement(elem).getTermFrequency();
            }

            return tfsum;
        }


        /**
         * Dump the content of this graph for debugging purposes.
         */
        public void dumpContent() {
            GraphElement tmpElem;
            for (int elem = 0; elem < getNumberOfElements(); elem++) {
                tmpElem = getElement(elem);
                if (tmpElem.isNode()) {
                    System.out.print("Node: " + tmpElem.getTerm());
                    System.out.println("\ttf = " + tmpElem.getTermFrequency());
                } else {
                    System.out.print("Link: " + tmpElem.getLeftTerm() +
                        " - " + tmpElem.getRightTerm());
                    System.out.println("\ttf = " + tmpElem.getTermFrequency());
                }
            }
        }


        /**
         * Method to add a node.
         * If the node to add is already available in the graph structure, its term frequency
         * is increased a unit.
         *
         * @param term The term of the node to add.
         */
        public void addNode(String term) {
            term = term.trim();
            boolean alreadyPresent = false;
            GraphElement temp;
            for (int i = 0; i < elementStruct.size(); i++) {
                temp = elementStruct.get(i);
                if (temp.isNode()) {
                    if (temp.getTerm().equals(term)) {
                        temp.addOneCount();
                        alreadyPresent = true;
                        break;
                    }
                }
            }
            if (!alreadyPresent) {
                temp = new GraphElement();
                temp.setAsNode();
                temp.setTerm(term);
                elementStruct.add(temp);
            }
        }


        /**
         * Method to add a node with its frequential information.
         *
         * @param term The node to add.
         */
        public void addNode(GraphElement term) {
            boolean alreadyPresent = false;
            GraphElement temp;
            for (int i = 0; i < elementStruct.size(); i++) {
                temp = elementStruct.get(i);
                if (temp.isNode()) {
                    if (temp.getTerm().equals(term.getTerm())) {
                        temp.setTermFrequency(temp.getTermFrequency() +
                            term.getTermFrequency());
                        alreadyPresent = true;
                        break;
                    }
                }
            }
            if (!alreadyPresent) {
                elementStruct.add(term.cloneElement());
            }
        }


        /**
         * Function to check if the graph contains a specific node.
         *
         * @param nodeTerm The node term.
         *
         * @return True if the graph contains a specific node.
         */
        public boolean containsNode(String nodeTerm) {
            boolean contains = false;

            for (int i = 0; i < elementStruct.size(); i++) {
                if (elementStruct.get(i).isNode()) {
                    if (elementStruct.get(i).getTerm().equals(nodeTerm)) {
                        contains = true;
                    }
                }
            }

            return contains;
        }


        /**
         * Function to retrieve the term frequency of a given node.
         *
         * @param nodeTerm The node term.
         *
         * @return The TF of this node.
         */
        public int getNodeTermFrequency(String nodeTerm) {
            int nodeTF = 0;
            GraphElement tempElement;

            for (int i = 0; i < elementStruct.size(); i++) {
                tempElement = elementStruct.get(i);
                if (tempElement.isNode() && tempElement.getTerm().equals(nodeTerm)) {
                    nodeTF = tempElement.getTermFrequency();
                    break;
                }
            }

            return nodeTF;
        }


        /**
         * Method to add a link.
         * If the link to add is already available in the graph structure, 
         * its term frequency is increased a unit.
         *
         * @param lTerm The left term of the link to add.
         * @param rTerm The right term of the link to add.
         */
        public void addLink(String lTerm, String rTerm) {
            boolean alreadyPresent = false;
            GraphElement temp;
            for (int i = 0; i < elementStruct.size(); i++) {
                temp = elementStruct.get(i);
                if (temp.isLink()) {
                    if (temp.getLeftTerm().equals(lTerm) && temp.getRightTerm().equals(rTerm)) {
                        temp.addOneCount();
                        alreadyPresent = true;
                        break;
                    }
                }
            }
            if (!alreadyPresent) {
                temp = new GraphElement();
                temp.setAsLink();
                temp.setLeftTerm(lTerm);
                temp.setRightTerm(rTerm);
                elementStruct.add(temp);
            }
        }


        /**
         * Method to add a link with its frequential information.
         *
         * @param link The link to add.
         */
        public void addLink(GraphElement link) {
            boolean alreadyPresent = false;
            GraphElement temp;
            for (int i = 0; i < elementStruct.size(); i++) {
                temp = elementStruct.get(i);
                if (temp.isLink()) {
                    if (temp.getLeftTerm().equals(link.getLeftTerm()) && 
                        temp.getRightTerm().equals(link.getRightTerm())) {
                        temp.setTermFrequency(temp.getTermFrequency() +
                            link.getTermFrequency());
                        alreadyPresent = true;
                        break;
                    }
                }
            }
            if (!alreadyPresent) {
                elementStruct.add(link.cloneElement());
            }
        }



        /**
         * Function to check if the graph contains a specific link.
         *
         * @param linkLeftTerm The link left term.
         * @param linkRightTerm The link right term.
         *
         * @return True if the graph contains a specific link.
         */
        public boolean containsLink(String linkLeftTerm, String linkRightTerm) {
            boolean contains = false;

            for (int i = 0; i < elementStruct.size(); i++) {
                if (elementStruct.get(i).isLink()) {
                    if (elementStruct.get(i).getLeftTerm().equals(linkLeftTerm) &&
                    elementStruct.get(i).getRightTerm().equals(linkRightTerm)) {
                        contains = true;
                    }
                }
            }

            return contains;
        }


        /**
         * Function to retrieve the term frequency of a given link.
         *
         * @param linkLeftTerm The link left term.
         * @param linkRightTerm The link right term.
         *
         * @return The TF of this link.
         */
        public int getLinkTermFrequency(String linkLeftTerm, String linkRightTerm) {
            int linkTF = 0;
            GraphElement tempElement;

            for (int i = 0; i < elementStruct.size(); i++) {
                tempElement = elementStruct.get(i);
                if (tempElement.isLink() && tempElement.getLeftTerm().equals(linkLeftTerm) &&
                tempElement.getRightTerm().equals(linkRightTerm)) {
                    linkTF = tempElement.getTermFrequency();
                    break;
                }
            }

            return linkTF;
        }


        /**
         * Function to retrive the specified element of this graph.
         *
         * @param num Element number.
         *
         * @return The specified element.
         */
        public GraphElement getElement(int num) {
            return elementStruct.get(num);
        }


        /**
         * Function to determine if it contains an element.
         * 
         * @param elem The element under test.
         *
         * @return TRUE if it is contained.
         */
        public boolean containsElement(GraphElement elem) {
            boolean contained = false;
            if (elem.isNode()) {
                contained = containsNode(elem.getTerm());
            } else {
                    contained = containsLink(elem.getLeftTerm(),
                    elem.getRightTerm());
            }
            return contained;
        }


        /**
         * Function to retrieve the TF of the given graph element, whatsoever
         * it is (node or term).
         *
         * @param elem The input graph element.
         *
         * @return The TF of the given graph element.
         */
        public int getElementTermFrequency(GraphElement elem) {
            int etf = 0;
            if (elem.isNode()) {
                    etf = getNodeTermFrequency(elem.getTerm());
            } else {
                    etf = getLinkTermFrequency(elem.getLeftTerm(),
                    elem.getRightTerm());
            }
            return etf;
        }


        /**
         * Function to export this graph as a vector of term frequencies.
         *
         * @return The vector of TFs.
         */
        public int[] exportVectorTF() {
            int[] vec = new int[elementStruct.size()];
            for (int i = 0; i < vec.length; i++) {
                vec[i] = elementStruct.get(i).getTermFrequency();
            }

            return vec;
        }


        /**
         * Function to export this graph as a vector of weighed measures.
         *
         * @return The vector of weighed measures.
         */
        public float[] exportWeighedVector() {
            float[] vec = new float[elementStruct.size()];
            for (int i = 0; i < vec.length; i++) {
                vec[i] = elementStruct.get(i).getWeighedMeasure();
            }

            return vec;
        }


        /**
         * Function to export this graph into a weighted matrix.
         *
         * @return The weighted matrix.
         */
        public Matrix exportWeightedMatrix() {
            GraphElement gElem;
            int auxLocOne, auxLocTwo;
            ArrayList<String> listWords = getListOfNodes();
            int numWords = listWords.size();
            // Matrix of zeros
            Matrix wMatrix = new Matrix(numWords, numWords);
            for (int numE = 0; numE < elementStruct.size(); numE++) {
                gElem = getElement(numE);
                if (gElem.isNode()) {
                    auxLocOne = listWords.indexOf(gElem.getTerm());
                    wMatrix.set(auxLocOne, auxLocOne, (double)gElem.getWeighedMeasure());
                } else {
                    auxLocOne = listWords.indexOf(gElem.getLeftTerm());
                    auxLocTwo = listWords.indexOf(gElem.getRightTerm());
                    wMatrix.set(auxLocOne, auxLocTwo, (double)gElem.getWeighedMeasure());
                }
            }
            //
            return wMatrix;
        }


        /**
         * Method to add an element.
         *
         * @param elem The element to add.
         */
        private void addElement(GraphElement elem) {
            elementStruct.add(elem);
        }


        /**
         * Function to clone this graph.
         *
         * @return A clone of this graph.
         */
        public Graph cloneGraph() {
            Graph theClone = new Graph();
            theClone.setCategoryName(getCategoryName());
            for (int i = 0; i < getNumberOfElements(); i++) {
                theClone.addElement(getElement(i).cloneElement());
            }

            return theClone;
        }


        /**
         * Method to reset the term frequencies of this graph.
         */
        public void resetTermFrequencies() {
            GraphElement elem;
            for (int i = 0; i < getNumberOfElements(); i++) {
                getElement(i).setTermFrequency(0);
            }
        }


        /**
         * Prunes the graph by ordering terms wrt their utility and
         * removing the least useful.
         *
         * @param numSelected The number of useful terms desired.
         */
        public void pruneUtility(int numSelected) {
            utilitySort();
            while (getNumberOfElements() > numSelected) {
                elementStruct.remove(numSelected);
            }
        }


        /**
         * Utility sorting method.
         * The Bubble sort method is used.
         */
        private void utilitySort() {
            boolean change;
            GraphElement auxElem;
            int elemCount;
    
            change = true;
            while (change) {
                change = false;
                for (elemCount = 0; elemCount < getNumberOfElements() - 2; 
                        elemCount++) {
                    if (getElement(elemCount + 1).getUtilityMeasure() >
                            getElement(elemCount).getUtilityMeasure()) {
                        auxElem = getElement(elemCount);
                        elementStruct.set(elemCount, getElement(elemCount + 1));
                        elementStruct.set(elemCount + 1, auxElem);
                        change = true;
                    }
                }
            }
        }


        /**
         * Removes a single element.
         *
         * @param index The index of the element to remove.
         */
        public void removeElement(int index) {
            elementStruct.remove(index);
        }

    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_EXTERNAL_FILE, PropertyType.STRING);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        externalFile = ps.getString(PROP_EXTERNAL_FILE, "nullpath");
    }


    /**
     * Method to initialize the Classifier.
     */
    public void initialize() {
        if (externalFile.equals("nullpath")) {
            System.out.println("ARNReduced: no external file has been provided!");
            System.exit(1);
        } else {
            load(externalFile);
        }
    }


    /**
     * Main constructor of this classifier.
     * It assigns, by default, the term frequency as the weigthting term method,
     * the cosine distance as the similarity measure and no co-ocurrence frquencies.
     */
    public ARNReduced() {
        termWeighingMeasure = "tf";
        similarityMeasure = "cos";
        assessCOF = false;
        assessPOS = false;
        assessSyns = false;
        assessStems = false;
        featSelMI = false;
        featSelChi2 = false;
        featSelTF = false;
        numSelFeats = 0;
        categoryGraph = new ArrayList<Graph>();
        fullGraph = new Graph();
        categoryRanking = new String[]{"NEG", "NEU", "POS"};
    }


    /**
     * Function to retrieve a list of the categories to deal with.
     * This function is important to iterate over the category labels
     * because the iterator returned by the hash map is not guaranteed
     * to remain constant over time.
     *
     * @return The list of the categories to deal with.
     */
    public ArrayList<String> getCategoryList() {
        ArrayList<String> catList = new ArrayList<String>();
        for (int c = 0; c < categoryGraph.size(); c++) {
            catList.add(categoryGraph.get(c).getCategoryName());
        }
        //
        return catList;
    }


    /**
     * Function to retrieve a hash map of the categories to deal with.
     *
     * @return The hash map of the categories to deal with.
     */
    public HashMap getCategoryHash() {
        return soleCategories;
    }


    /**
     * Function to recover the category-specific graphs.
     *
     * @return The list of category graphs.
     */
    public ArrayList<Graph> getCategoryGraphs() {
        return categoryGraph;
    }


    /**
     * Function to recover the full vocabulary graph.
     *
     * @return The full graph.
     */
    public Graph getVocabularyGraph() {
        return fullGraph;
    }


    /**
     * Function to retrieve the corpus size (number of words) of the given category.
     * That is, the sum of all term frequencies (terms considered to be words alone).
     *
     * @param cat The given category.
     *
     * @return The corpus size.
     */
    public int getCorpusSize(String cat) {
        Graph theGraphOfInterest;
        int totalSize = 0;
        for (int i = 0; i < categoryGraph.size(); i++) {
            if (cat.equals(categoryGraph.get(i).getCategoryName())) {
                theGraphOfInterest = categoryGraph.get(i);
                for (int elemNum = 0; elemNum < theGraphOfInterest.getNumberOfElements(); elemNum++) {
                    if (theGraphOfInterest.getElement(elemNum).isNode()) {
                        totalSize += theGraphOfInterest.getElement(elemNum).getTermFrequency();
                    }
                }
            }
            break;
        }

        return totalSize;
    }


    /**
     * Function to retrieve the corpus size of tuples of the given category.
     * That is, the sum of all term frequencies (terms considered to be tuples).
     *
     * @param cat The given category.
     *
     * @return The corpus tuple size.
     */
    public int getCorpusTupleSize(String cat) {
        Graph theGraphOfInterest;
        int totalSize = 0;
        for (int i = 0; i < categoryGraph.size(); i++) {
            if (cat.equals(categoryGraph.get(i).getCategoryName())) {
                theGraphOfInterest = categoryGraph.get(i);
                for (int elemNum = 0; elemNum < theGraphOfInterest.getNumberOfElements(); elemNum++) {
                    if (theGraphOfInterest.getElement(elemNum).isLink()) {
                        totalSize += theGraphOfInterest.getElement(elemNum).getTermFrequency();
                    }
                }
            }
            break;
        }

        return totalSize;
    }


    /**
     * Function to retrieve the number of terms (vocabulary size, words alone)
     * which frequency is greater than the given threshold, wrt a given category.
     *
     * @param wordFreqThreshold The word frequency treshold.
     * @param cat The given category.
     *
     * @return The vocabulary size.
     */
    public int getVocabularySize(int wordFreqThreshold, String cat) {
        Graph theGraphOfInterest;
        int vocabSize = 0;
        for (int i = 0; i < categoryGraph.size(); i++) {
            if (cat.equals(categoryGraph.get(i).getCategoryName())) {
                theGraphOfInterest = categoryGraph.get(i);
                for (int elemNum = 0; elemNum < theGraphOfInterest.getNumberOfElements(); elemNum++) {
                    if (theGraphOfInterest.getElement(elemNum).isNode()) {
                        if (theGraphOfInterest.getElement(elemNum).getTermFrequency() >= wordFreqThreshold) {
                            vocabSize++;
                        }
                    }
                }
                break;
            }
        }

        return vocabSize;
    }


    /**
     * Function to retrieve the number of terms (vocabulary size, bigrams alone)
     * which frequency is greater than the given threshold, wrt a given category.
     *
     * @param bigramFreqThreshold The bigram frequency treshold.
     * @param cat The given category.
     *
     * @return The vocabulary size.
     */
    public int getBigramVocabularySize(int bigramFreqThreshold, String cat) {
        Graph theGraphOfInterest;
        int vocabSize = 0;
        for (int i = 0; i < categoryGraph.size(); i++) {
            if (cat.equals(categoryGraph.get(i).getCategoryName())) {
                theGraphOfInterest = categoryGraph.get(i);
                for (int elemNum = 0; elemNum < theGraphOfInterest.getNumberOfElements(); elemNum++) {
                    if (theGraphOfInterest.getElement(elemNum).isLink()) {
                        if (theGraphOfInterest.getElement(elemNum).getTermFrequency() >= bigramFreqThreshold) {
                            vocabSize++;
                        }
                    }
                }
                break;
            }
        }

        return vocabSize;
    }


    /**
     * Function to retrieve a sorted list (in frequency descending order) of words.
     * The sorting algorithm of use is the bubble sort.
     *
     * @param cat The given category.
     * @param wList The list if words to produce.
     * @param fList The list of frequencies to produce.
     */
    public void getOrderedList(String cat, ArrayList<String> wList, ArrayList<Integer> fList) {
        boolean listOK;
        Graph theGraphOfInterest;

        listOK = false;
        for (int i = 0; i < categoryGraph.size(); i++) {
            if (cat.equals(categoryGraph.get(i).getCategoryName())) {
                theGraphOfInterest = categoryGraph.get(i);
                for (int elemNum = 0; elemNum < theGraphOfInterest.getNumberOfElements(); elemNum++) {
                    if (theGraphOfInterest.getElement(elemNum).isNode()) {
                        wList.add(theGraphOfInterest.getElement(elemNum).getTerm());
                        fList.add(theGraphOfInterest.getElement(elemNum).getTermFrequency());
                    }
                }
                listOK = true;
                break;
            }
        }

        if (listOK) {
            bubbleSort(wList, fList);
        } else {
            System.out.println("EmoLib: ARNReduced: WARNING! No list has been created for sorting!");
            System.exit(1);
        }
    }


    /**
     * Bubble sorting method.
     *
     * @param wList The list of terms.
     * @param fList The list of counts to sort in descending order.
     */
    private void bubbleSort(ArrayList<String> wList, ArrayList<Integer> fList) {
        boolean change;
        int listCounter;
        String auxS;
        int auxI;

        change = true;
        while (change) {
            change = false;
            for (listCounter = 0; listCounter < wList.size() - 2; listCounter++) {
                if (fList.get(listCounter + 1).intValue() > fList.get(listCounter).intValue()) {
                    auxS = wList.get(listCounter);
                    auxI = fList.get(listCounter);
                    wList.set(listCounter, wList.get(listCounter + 1));
                    fList.set(listCounter, fList.get(listCounter + 1));
                    wList.set(listCounter + 1, auxS);
                    fList.set(listCounter + 1, auxI);
                    change = true;
                }
            }
        }
    }


    /**
     * Function to retrieve a sorted list (in frequency descending order) of tuples.
     * The sorting algorithm of use is the bubble sort.
     *
     * @param cat The given category.
     * @param wList The list if tuples to produce.
     * @param fList The list of frequencies to produce.
     */
    public void getOrderedTupleList(String cat, ArrayList<String> wList, ArrayList<Integer> fList) {
        boolean listOK;
        Graph theGraphOfInterest;
        String aux;

        listOK = false;
        for (int i = 0; i < categoryGraph.size(); i++) {
            if (cat.equals(categoryGraph.get(i).getCategoryName())) {
                theGraphOfInterest = categoryGraph.get(i);
                for (int elemNum = 0; elemNum < theGraphOfInterest.getNumberOfElements(); elemNum++) {
                    if (theGraphOfInterest.getElement(elemNum).isLink()) {
                        aux = theGraphOfInterest.getElement(elemNum).getLeftTerm() +
                            "_" + theGraphOfInterest.getElement(elemNum).getRightTerm();
                        wList.add(aux);
                        fList.add(theGraphOfInterest.getElement(elemNum).getTermFrequency());
                    }
                }
                listOK = true;
                break;
            }
        }

        if (listOK) {
            bubbleSort(wList, fList);
        } else {
            System.out.println("EmoLib: ARNReduced: WARNING! No list has been created for sorting!");
            System.exit(1);
        }
    }


    /**
     * Method to set the term weighting measure.
     *
     * @param twm The term weighting measure.
     */
    public void setTermWeighingMeasure(String twm) {
        termWeighingMeasure = twm;
    }


    /**
     * Method to set the similarity measure.
     *
     * @param simil The similarity measure.
     */
    public void setSimilarityMeasure(String simil) {
        similarityMeasure = simil;
    }


    /**
     * Method to set the assessment of co-ocurrence frequencies (tuples actually).
     *
     * @param flag The set flag.
     */
    public void setCOF(boolean flag){
        assessCOF = flag;
    }


    /**
     * Method to set the assessment of POS tags (grammatical analysis).
     *
     * @param flag The set flag.
     */
    public void setPOS(boolean flag) {
        assessPOS = flag;
    }


    /**
     * Method to set the assessment of synonyms.
     *
     * @param flag The set flag.
     */
    public void setSynonyms(boolean flag) {
        assessSyns = flag;
    }


    /**
     * Method to set the assessment of stemmed terms.
     *
     * @param flag The set flag.
     */
    public void setStems(boolean flag) {
        assessStems = flag;
    }


    /**
     * Method to set the Mutual Information global feature selection.
     *
     * @param flag The set flag.
     * @param numFeats The number of feats per class to select.
     */
    public void setFeatSelMI(boolean flag, int numFeats) {
        featSelMI = flag;
        numSelFeats = numFeats;
        
    }


    /**
     * Method to set the Chi square global feature selection.
     *
     * @param flag The set flag.
     * @param numFeats The number of feats per class to select.
     */
    public void setFeatSelChi2(boolean flag, int numFeats) {
        featSelChi2 = flag;
        numSelFeats = numFeats;
        
    }


    /**
     * Method to set the Term Frequency global feature selection.
     *
     * @param flag The set flag.
     * @param numFeats The number of feats per class to select.
     */
    public void setFeatSelTF(boolean flag, int numFeats) {
        featSelTF = flag;
        numSelFeats = numFeats;
        
    }


    /**
     * Function to retrieve the similarity of a given text with a given category.
     *
     * @param inputText The given text.
     * @param cat The given category.
     *
     * @return The resulting similarity.
     */
    public float getSimilarity(FeatureBox inputText, String cat) {
        float similarity = 0;
        Graph inputTextGraph;
        Graph emotionGraph;
        for (int i = 0; i < categoryGraph.size(); i++) {
            if (cat.equals(categoryGraph.get(i).getCategoryName())) {
                inputTextGraph = buildGraph(inputText);
                emotionGraph = buildEmotionGraph(inputTextGraph, i);
                applyTermWeighing(inputTextGraph, i);
                applyTermWeighing(emotionGraph, i);
                similarity = computeSimilarity(inputTextGraph, emotionGraph);
                break;
            }
        }

        return similarity;
    }


    /**
     * Method to weight the terms of corresponding to the model vector.
     * In some cases the Term Weighting method needs information
     * from the original domain model (e.g., the |T^k| in (Al&iacute;as et al., 2008) for the ITF).
     * That's the reason for using this method instead of the general applyTermWeighing.
     *
     * @param The given graph to weight.
     * @param The given catetory for supervised term weighting methods.
     */
    public void applyModelTermWeighing(Graph inputGraph, int cat) {
        GraphElement tempElem;
        int tfOthers, tfCategory;
        double rf;

        if (termWeighingMeasure.equals("itf")) {
            for (int elem = 0; elem < inputGraph.getNumberOfElements(); elem++) {
                tempElem = inputGraph.getElement(elem);
                if (tempElem.getTermFrequency() != 0) {
                    float tfalldomain = getTFAllModel(cat);
                    tempElem.setWeighedMeasure((float)Math.log((double)tfalldomain /
                        (double)tempElem.getTermFrequency()));
                } else {
                    // Since tf = 0, ITF = inf, therefore 0 is assigned.
                    tempElem.setWeighedMeasure(0);
                }
            }
        } else {
            // This function contemplates the ITF factor for its generic use, but since the program flow
            // has reached this point, there's no problem with it modifying the weights (otherwise
            // it would have computed the other part of the conditional).
            // The ITF code in this method is very similar, except for the model-based vectors.
            applyTermWeighing(inputGraph, cat);
        }
    }


    /**
     * Function to retrieve the sum of all term frequencies according to the given
     * category.
     * It's like the other corpus size getters, but regardless of the elements
     * being nodes or links (words or tuples).
     *
     * @param cat The given category.
     *
     * @return The total sum.
     */
    private float getTFAllModel(int cat) {
        Graph model = categoryGraph.get(cat);

        return (float)model.getTotalSumTF();
    }


    /**
     * Method to apply a term weighting methodology to the given graph.
     * In the case that the Term Weighting strategy of the the ARN is supervised,
     * a category is also provided. If no weighting strategy is needed, the input
     * graph will not be modified (it will just contain the default frequencies
     * of the terms within).
     *
     * @param The given graph to weight.
     * @param The given catetory for supervised term weighting methods.
     */
    public void applyTermWeighing(Graph inputGraph, int cat) {
        GraphElement tempElem;
        int tfOthers, tfCategory;
        double rf;

        if (termWeighingMeasure.equals("tf")) {
            for (int elem = 0; elem < inputGraph.getNumberOfElements(); elem++) {
                tempElem = inputGraph.getElement(elem);
                tempElem.setWeighedMeasure((float)tempElem.getTermFrequency());
            }
        } else if (termWeighingMeasure.equals("ltf")) {
            for (int elem = 0; elem < inputGraph.getNumberOfElements(); elem++) {
                tempElem = inputGraph.getElement(elem);
                tempElem.setWeighedMeasure((float)Math.log((double)1 + 
                    (double)tempElem.getTermFrequency()));
            }
        } else if (termWeighingMeasure.equals("binary")) {
            for (int elem = 0; elem < inputGraph.getNumberOfElements(); elem++) {
                tempElem = inputGraph.getElement(elem);
                if (tempElem.getTermFrequency() >= 1) {
                    tempElem.setWeighedMeasure(1);
                } else {
                    tempElem.setWeighedMeasure(0);
                }
            }
        } else if (termWeighingMeasure.equals("itf")) {
            // When tf = 0, problems need finer discussion, apart from the resulting indetermination
            double totalSumTF = (double)inputGraph.getTotalSumTF();
            for (int elem = 0; elem < inputGraph.getNumberOfElements(); elem++) {
                tempElem = inputGraph.getElement(elem);
                if (tempElem.getTermFrequency() != 0) {
                    tempElem.setWeighedMeasure((float)Math.log(totalSumTF /
                        (double)tempElem.getTermFrequency()));
                } else {
                    tempElem.setWeighedMeasure(0);
                }
            }
        // From E. Leopold and J. Kinderman, Text Categorisation with SVM, r=1 (ITF param)
        } else if (termWeighingMeasure.equals("itf_leopold")) {
            for (int elem = 0; elem < inputGraph.getNumberOfElements(); elem++) {
                tempElem = inputGraph.getElement(elem);
                tempElem.setWeighedMeasure((float)(1 - (float)1 / (float)(tempElem.getTermFrequency() + 1)));
            }
        } else if (termWeighingMeasure.equals("tfrf")) {
            for (int elem = 0; elem < inputGraph.getNumberOfElements(); elem++) {
                tempElem = inputGraph.getElement(elem);
                if (tempElem.getTermFrequency() > 0) {
                    if (tempElem.isNode()) {
                        if (categoryGraph.get(cat).containsNode(tempElem.getTerm())) {
                            tfCategory = categoryGraph.get(cat).getNodeTermFrequency(tempElem.getTerm());
                            tfOthers = calcTFOthersSum(tempElem, cat);
                            rf = Math.log((double)2 + ((double)tfCategory / (double)tfOthers)) / Math.log((double)2);
                            tempElem.setWeighedMeasure((float)tempElem.getTermFrequency() * (float)rf);
                        } else {
                            tempElem.setWeighedMeasure((float)tempElem.getTermFrequency());
                        }
                    } else if (tempElem.isLink()) {
                        if (categoryGraph.get(cat).containsLink(tempElem.getLeftTerm(), tempElem.getRightTerm())) {
                            tfCategory = categoryGraph.get(cat).getLinkTermFrequency(tempElem.getLeftTerm(),
                            tempElem.getRightTerm());
                            tfOthers = calcTFOthersSum(tempElem, cat);
                            rf = Math.log((double)2 + ((double)tfCategory / (double)tfOthers)) / Math.log((double)2);
                            tempElem.setWeighedMeasure((float)tempElem.getTermFrequency() * (float)rf);
                        } else {
                            tempElem.setWeighedMeasure((float)tempElem.getTermFrequency());
                        }
                    } else {
                        System.out.println("ARNReduced: error weighting the terms!");
                    }
                } else {
                    tempElem.setWeighedMeasure(0);
                }
            }
        } else if (termWeighingMeasure.equals("ltfrf")) {
            // This code is copied from above, except for the log(1 + tf)
            for (int elem = 0; elem < inputGraph.getNumberOfElements(); elem++) {
                tempElem = inputGraph.getElement(elem);
                if (tempElem.getTermFrequency() > 0) {
                    if (tempElem.isNode()) {
                        if (categoryGraph.get(cat).containsNode(tempElem.getTerm())) {
                            tfCategory = categoryGraph.get(cat).getNodeTermFrequency(tempElem.getTerm());
                            tfOthers = calcTFOthersSum(tempElem, cat);
                            rf = Math.log((double)2 + ((double)tfCategory / (double)tfOthers)) / Math.log((double)2);
                            tempElem.setWeighedMeasure((float)Math.log((double)1 +
                                (double)tempElem.getTermFrequency()) * (float)rf);
                        } else {
                            tempElem.setWeighedMeasure((float)Math.log((double)1 +
                                (double)tempElem.getTermFrequency()));
                        }
                    } else if (tempElem.isLink()) {
                        if (categoryGraph.get(cat).containsLink(tempElem.getLeftTerm(), tempElem.getRightTerm())) {
                            tfCategory = categoryGraph.get(cat).getLinkTermFrequency(tempElem.getLeftTerm(),
                            tempElem.getRightTerm());
                            tfOthers = calcTFOthersSum(tempElem, cat);
                            rf = Math.log((double)2 + ((double)tfCategory / (double)tfOthers)) / Math.log((double)2);
                            tempElem.setWeighedMeasure((float)Math.log((double)1 +
                                (double)tempElem.getTermFrequency()) * (float)rf);
                        } else {
                            tempElem.setWeighedMeasure((float)Math.log((double)1 +
                                (double)tempElem.getTermFrequency()));
                        }
                    } else {
                        System.out.println("ARNReduced: error weighting the terms!");
                    }
                } else {
                    tempElem.setWeighedMeasure(0);
                }
            }
        } else if (termWeighingMeasure.equals("crrf")) {
            // This code is copied from above, except for the sentiment rank
            double rankTFOthers;
            for (int elem = 0; elem < inputGraph.getNumberOfElements(); elem++) {
                tempElem = inputGraph.getElement(elem);
                if (tempElem.getTermFrequency() > 0) {
                    if (tempElem.isNode()) {
                        if (categoryGraph.get(cat).containsNode(tempElem.getTerm())) {
                            tfCategory = categoryGraph.get(cat).getNodeTermFrequency(tempElem.getTerm());
                            rankTFOthers = calcTFOthersRankSum(tempElem, cat);
                            rf = Math.log((double)2 + ((double)tfCategory / rankTFOthers)) / Math.log((double)2);
                            tempElem.setWeighedMeasure((float)Math.log((double)1 +
                                (double)tempElem.getTermFrequency()) * (float)rf);
                        } else {
                            tempElem.setWeighedMeasure((float)Math.log((double)1 +
                                (double)tempElem.getTermFrequency()));
                        }
                    } else if (tempElem.isLink()) {
                        if (categoryGraph.get(cat).containsLink(tempElem.getLeftTerm(), tempElem.getRightTerm())) {
                            tfCategory = categoryGraph.get(cat).getLinkTermFrequency(tempElem.getLeftTerm(),
                            tempElem.getRightTerm());
                            rankTFOthers = calcTFOthersRankSum(tempElem, cat);
                            rf = Math.log((double)2 + ((double)tfCategory / rankTFOthers)) / Math.log((double)2);
                            tempElem.setWeighedMeasure((float)Math.log((double)1 +
                                (double)tempElem.getTermFrequency()) * (float)rf);
                        } else {
                            tempElem.setWeighedMeasure((float)Math.log((double)1 +
                                (double)tempElem.getTermFrequency()));
                        }
                    } else {
                        System.out.println("ARNReduced: error weighting the terms!");
                    }
                } else {
                    tempElem.setWeighedMeasure(0);
                }
            }
        } else if (termWeighingMeasure.equals("ltfrfditf")) {
            // Dual ltfrf and itf. This is the one inspired by the old unsupported falias' code.
            // This only makes sense weighting the input graph, where the terms have some tf
            // within the sentence, but may be found (or not) in the given model. Instead,
            // for model vectors, all terms will be found in the model (it would be stupid
            // otherwise), and those which are missed, will always have a tf = 0, and thus
            // will not be weighted.
            for (int elem = 0; elem < inputGraph.getNumberOfElements(); elem++) {
                tempElem = inputGraph.getElement(elem);
                if (tempElem.isNode()) {
                    if (categoryGraph.get(cat).containsNode(tempElem.getTerm())) {
                        // Apply ltfrf
                        tfCategory = categoryGraph.get(cat).getNodeTermFrequency(tempElem.getTerm());
                        tfOthers = calcTFOthersSum(tempElem, cat);
                        rf = Math.log((double)2 + ((double)tfCategory / (double)tfOthers)) / Math.log((double)2);
                        tempElem.setWeighedMeasure((float)Math.log((double)1 +
                            (double)tempElem.getTermFrequency()) * (float)rf);
                    } else {
                        // Apply ITF
                        if (tempElem.getTermFrequency() == 0) {
                            tempElem.setWeighedMeasure(0);
                        } else {
                            tempElem.setWeighedMeasure((float)Math.log((double)inputGraph.getTotalSumTF() /
                            (double)tempElem.getTermFrequency()));
                        }
                    }
                } else if (tempElem.isLink()) {
                    if (categoryGraph.get(cat).containsLink(tempElem.getLeftTerm(), tempElem.getRightTerm())) {
                        tfCategory = categoryGraph.get(cat).getLinkTermFrequency(tempElem.getLeftTerm(),
                        tempElem.getRightTerm());
                        tfOthers = calcTFOthersSum(tempElem, cat);
                        rf = Math.log((double)2 + ((double)tfCategory / (double)tfOthers)) / Math.log((double)2);
                        tempElem.setWeighedMeasure((float)Math.log((double)1 +
                            (double)tempElem.getTermFrequency()) * (float)rf);
                    } else {
                        if (tempElem.getTermFrequency() == 0) {
                            tempElem.setWeighedMeasure(0);
                        } else {
                            tempElem.setWeighedMeasure((float)Math.log((double)inputGraph.getTotalSumTF() /
                            (double)tempElem.getTermFrequency()));
                        }
                    }
                } else {
                    System.out.println("ARNReduced: error weighting the terms!");
                }
            }

        } else {
            System.out.println("ARNReduced: an error accurred with the term weighting measure definition!");
        }
    }


    /**
     * Function to calculate the tf sum for all categories except for the given one.
     *
     * @param term The term to search in the affective dataset.
     * @param ex The exception category.
     *
     * @return The sum for the rest of the categories.
     */
    private int calcTFOthersSum(GraphElement term, int ex) {
        int sum = 0;
        for (int i = 0; i < categoryGraph.size(); i++) {
            if (i != ex) {
                if (term.isNode()) {
                    if (categoryGraph.get(i).containsNode(term.getTerm())) {
                        sum += categoryGraph.get(i).getNodeTermFrequency(term.getTerm());
                    }
                } else if (term.isLink()) {
                    if (categoryGraph.get(i).containsLink(term.getLeftTerm(), term.getRightTerm())) {
                        sum += categoryGraph.get(i).getLinkTermFrequency(term.getLeftTerm(), term.getRightTerm());
                    }
                } else {
                    System.out.println("ARNReduced: error weighting the terms!");
                }
            }
        }
        if (sum == 0) {
            // This is for the max(1, sum) in order to avoid a division by zero
            sum = 1;
        }

        return sum;
    }


    /**
     * Function to calculate the tf sum for all categories except for the given one
     * considering the rank within the categories.
     *
     * @param term The term to search in the affective dataset.
     * @param ex The exception category.
     *
     * @return The sum for the rest of the categories.
     */
    private double calcTFOthersRankSum(GraphElement term, int ex) {
        double totalSum = 0;
        double sum;
        for (int i = 0; i < categoryGraph.size(); i++) {
            if (i != ex) {
                sum = 0;
                if (term.isNode()) {
                    if (categoryGraph.get(i).containsNode(term.getTerm())) {
                        sum += (double)categoryGraph.get(i).
                            getNodeTermFrequency(term.getTerm());
                    }
                } else if (term.isLink()) {
                    if (categoryGraph.get(i).containsLink(term.getLeftTerm(), term.getRightTerm())) {
                        sum += (double)categoryGraph.get(i).getLinkTermFrequency(
                            term.getLeftTerm(), term.getRightTerm());
                    }
                } else {
                    System.out.println("ARNReduced: error weighting the terms!");
                    System.exit(1);
                }
                totalSum += categoryRankDifference(i, ex) * sum;
            }
        }
        if (totalSum == 0) {
            // This is for the max(1, sum) in order to avoid a division by zero
            totalSum = 1;
        }

        return totalSum;
    }


    /**
     * Calculates the relevance of the category ranks.
     *
     * @param c1 The first category.
     * @param c2 The second category.
     *
     * @return The rank-weighted TF sum.
     */
    private double categoryRankDifference(int c1, int c2) {
        String cOne = categoryGraph.get(c1).getCategoryName().trim();
        String cTwo = categoryGraph.get(c2).getCategoryName().trim();
        int index1 = 0;
        int index2 = 0;
        for (int i = 0; i < categoryRanking.length; i++) {
            if (categoryRanking[i].equals(cOne)) {
                index1 = i;
            }
            if (categoryRanking[i].equals(cTwo)) {
                index2 = i;
            }
        }
        double rankdif = (double)Math.abs(index1 - index2);
        if (rankdif == 0) {
            System.out.println("Rank error!");
            System.exit(1);
        }
        rankdif = Math.pow(3, rankdif);
        return rankdif;
    }


    /**
     * Function to build a graph from input features.
     *
     * @param inputFeatures The input features.
     *
     * @return The resulting graph.
     */
    public Graph buildGraph(FeatureBox inputFeatures) {
        String theWords = inputFeatures.getWords();
        String[] wordChunks = theWords.split(" ");
        String posTags = inputFeatures.getPOSTags();
        String[] posChunks = posTags.split(" ");
        String stems = inputFeatures.getStems();
        String[] stemChunks = stems.split(" ");
        Graph textGraph = new Graph();
        if (assessPOS) {
            if (assessStems) {
                textGraph.addNode(stemChunks[0] + "_" + posChunks[0]);
            } else {
                textGraph.addNode(wordChunks[0] + "_" + posChunks[0]);
            }
        } else {
            if (assessStems) {
                textGraph.addNode(stemChunks[0]);
            } else {
                textGraph.addNode(wordChunks[0]);
            }
        }
        for (int i = 1; i < wordChunks.length; i++) {
            if (assessPOS) {
                if (assessCOF) {
                    if (assessStems) {
                        textGraph.addLink(stemChunks[i - 1] + "_" + posChunks[i - 1],
                            stemChunks[i] + "_" + posChunks[i]);
                    } else {
                        textGraph.addLink(wordChunks[i - 1] + "_" + posChunks[i - 1],
                            wordChunks[i] + "_" + posChunks[i]);
                    }
                }
                if (assessStems) {
                    textGraph.addNode(stemChunks[i] + "_" + posChunks[i]);
                } else {
                    textGraph.addNode(wordChunks[i] + "_" + posChunks[i]);
                }
            } else {
                if (assessCOF) {
                    if (assessStems) {
                        textGraph.addLink(stemChunks[i - 1], stemChunks[i]);
                    } else {
                        textGraph.addLink(wordChunks[i - 1], wordChunks[i]);
                    }
                }
                if (assessStems) {
                    textGraph.addNode(stemChunks[i]);
                } else {
                    textGraph.addNode(wordChunks[i]);
                }
            }
        }
        // No COF with synonyms, as they are not directly observed in the text.
        if (inputFeatures.containsSynonyms()) {
            String syns = inputFeatures.getSynonyms();
            String[] synsChunks = syns.split(" ");
            String ssyns = inputFeatures.getStemmedSynonyms();
            String[] ssynsChunks = ssyns.split(" ");
            if (assessSyns) {
                for (int i = 0; i < synsChunks.length; i++) {
                    if (assessPOS) {
                        if (assessStems) {
                            textGraph.addNode(ssynsChunks[i] + "_NOMBRE");
                        } else {
                            textGraph.addNode(synsChunks[i] + "_NOMBRE");
                        }
                    } else {
                        if (assessStems) {
                            textGraph.addNode(ssynsChunks[i]);
                        } else {
                            textGraph.addNode(synsChunks[i]);
                        }
                    }
                }
            }
        }
        //
        return textGraph;
    }


    /**
     * Function to build a full graph with the term frequencies given by
     * the input terms.
     *
     * @param input The input text graph.
     *
     * @return The full graph.
     */
    public Graph buildFullGraph(Graph input) {
        Graph fullNet = fullGraph.cloneGraph();
        fullNet.resetTermFrequencies();
        GraphElement temp;
        for (int i = 0; i < fullNet.getNumberOfElements(); i++) {
            temp = fullNet.getElement(i);
            if (temp.isNode()) {
                if (input.containsNode(temp.getTerm())) {
                    temp.setTermFrequency(input.getNodeTermFrequency(temp.getTerm()));
                } else {
                    temp.setTermFrequency(0);
                }
            } else if (temp.isLink()) {
                if (input.containsLink(temp.getLeftTerm(), temp.getRightTerm())) {
                    temp.setTermFrequency(input.getLinkTermFrequency(temp.getLeftTerm(),
                        temp.getRightTerm()));
                } else {
                    temp.setTermFrequency(0);
                }
            } else {
                System.out.println("ARNReduced: ERROR One element of input graph is not " +
                    "defined neither as a node or a link!");
            }
        }
        //
        return fullNet;
    }


    /**
     * Function to build an emotion graph, a structure with the terms defined by the
     * input text and the term frequencies given by an emotion class.
     *
     * @param input The input text graph.
     * @param catNumber The category identifier.
     *
     * @return The emotion graph.
     */
    private Graph buildEmotionGraph(Graph input, int catNumber) {
        Graph emotionGraph = input.cloneGraph();
        Graph categorySpecificGraph = categoryGraph.get(catNumber);
        GraphElement temp;
        for (int i = 0; i < emotionGraph.getNumberOfElements(); i++) {
            temp = emotionGraph.getElement(i);
            if (temp.isNode()) {
                if (categorySpecificGraph.containsNode(temp.getTerm())) {
                    temp.setTermFrequency(categorySpecificGraph.getNodeTermFrequency(temp.getTerm()));
                } else {
                    temp.setTermFrequency(0);
                }
            } else if (temp.isLink()) {
                if (categorySpecificGraph.containsLink(temp.getLeftTerm(), temp.getRightTerm())) {
                    temp.setTermFrequency(categorySpecificGraph.getLinkTermFrequency(temp.getLeftTerm(),
                    temp.getRightTerm()));
                } else {
                    temp.setTermFrequency(0);
                }
            } else {
                System.out.println("ARNReduced: ERROR One element of input graph is not " +
                    "defined neither as a node or a link!");
            }
        }

        return emotionGraph;
    }


    /**
     * Function to compute the similarity between two graphs.
     * The graphs may be vectorised and used to compute a distributional similarity measure, like
     * the cosine, or directly processed to compute a network-based similarity measure, like
     * a graph distance.
     *
     * @param testGraph The test graph.
     * @param emoGraph The emotion specific graph.
     *
     * @return The similarity between the two graphs.
     */
    private float computeSimilarity(Graph testGraph, Graph emoGraph) {
        float similarity = 0;

        if (similarityMeasure.equals("cos")) {
            // Compute the similarity with the cosine distance obtained with the Law of Cosines
            float vTestNorm = computeNorm(testGraph.exportWeighedVector());
            float vEmoNorm = computeNorm(emoGraph.exportWeighedVector());
            float vDifNorm = computeNorm(testGraph.exportWeighedVector(), emoGraph.exportWeighedVector());
            similarity = (vTestNorm * vTestNorm + vEmoNorm * vEmoNorm - vDifNorm * vDifNorm) /
            (2 * vTestNorm * vEmoNorm);
        } else if (similarityMeasure.equals("dotprod")) {
            float[] vTest = testGraph.exportWeighedVector();
            float[] vEmo = emoGraph.exportWeighedVector();
            for (int i = 0; i < vTest.length; i++) {
                similarity += vTest[i] * vEmo[i];
            }
        } else if (similarityMeasure.equals("matrixdifnorm2")) {
            // The inverse of the norm of the difference matrix is taken for a similarity
            Matrix wMatText = testGraph.exportWeightedMatrix();
            Matrix wMatEmo = emoGraph.exportWeightedMatrix();
            similarity = (float)1 / (float)((wMatText.minus(wMatEmo)).norm2());
        } else if (similarityMeasure.equals("cosMatrix")) {
            // Compute the similarity with the matrix cosine distance with the Frobenius norm
            // It is the same as the cosine distance of the vectorised matrices that represents the graphs
            Matrix wMatText = testGraph.exportWeightedMatrix();
            Matrix wMatEmo = emoGraph.exportWeightedMatrix();
            similarity = (float)((wMatText.transpose().times(wMatEmo).trace()) / (wMatText.normF() * wMatEmo.normF()));
        } else {
            System.out.println("ARNReduced: en error occurred with the similarity measure!");
        }

        return similarity;
    }


    /**
     * Function to compute the norm of the input vector.
     *
     * @param inputVector The input vector.
     *
     * @return The norm.
     */
    private float computeNorm(float[] inputVector) {
        // Euclidean norm
        float theNorm = 0;
        for (int i = 0; i < inputVector.length; i++) {
            theNorm += inputVector[i] * inputVector[i];
        }

        return (float)Math.sqrt((double)theNorm);
    }


    /**
     * Function to compute the norm of the vector difference of input vectors.
     *
     * @param inputVectorOne The first input vector.
     * @param inputVectortTwo The second input vector.
     *
     * @return The norm of the difference vector.
     */
    private float computeNorm(float[] inputVectorOne, float[] inputVectortTwo) {
        // Euclidean norm
        float theNorm = 0;
        if (inputVectorOne.length != inputVectortTwo.length) {
            System.out.println("ARNReduced: vector lengths don't match! Norm cannot be computed");
        } else {
            float[] difVect = new float[inputVectortTwo.length];
            for (int i = 0; i < difVect.length; i++) {
                difVect[i] = inputVectorOne[i] - inputVectortTwo[i];
            }
            theNorm = computeNorm(difVect);
        }

        return theNorm;
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        String mostProbableCategory = categoryGraph.get(0).getCategoryName();
        float similarity = getSimilarity(inputFeatures, mostProbableCategory);
        for (int i = 1; i < categoryGraph.size(); i++) {
            if (getSimilarity(inputFeatures, categoryGraph.get(i).getCategoryName()) > similarity) {
                mostProbableCategory = categoryGraph.get(i).getCategoryName();
                similarity = getSimilarity(inputFeatures, categoryGraph.get(i).getCategoryName());
            }
        }

        return mostProbableCategory;
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#trainingProcedure()
     */
    public void trainingProcedure() {
        ArrayList<FeatureBox> exampleFeatures = getListOfExampleFeatures();
        ArrayList<String> exampleCategories = getListOfExampleCategories();
        soleCategories = new HashMap<String, Integer>();
        int categoryOrder = 0;
        int presentClassNumber;
        String presentCat;
        FeatureBox presentFeats;
        Graph temp, emoTemp;
        GraphElement tempElem;

        for (int num_examples = 0; num_examples < exampleFeatures.size(); num_examples++) {
            presentCat = exampleCategories.get(num_examples);
            presentFeats = exampleFeatures.get(num_examples);
            if (!soleCategories.containsKey(presentCat)) {
                soleCategories.put(presentCat, new Integer(categoryOrder));
                presentClassNumber = categoryOrder;
                categoryOrder++;
                temp = new Graph();
                temp.setCategoryName(presentCat);
                categoryGraph.add(temp);
            } else {
                presentClassNumber = soleCategories.get(presentCat).intValue();
            }
            temp = buildGraph(presentFeats);
            emoTemp = categoryGraph.get(presentClassNumber);
            for (int i = 0; i < temp.getNumberOfElements(); i++) {
                tempElem = temp.getElement(i);
                if (tempElem.isNode()) {
                    emoTemp.addNode(tempElem);
                    fullGraph.addNode(tempElem);
                } else {
                    emoTemp.addLink(tempElem);
                    fullGraph.addLink(tempElem);
                }
            }
        }
        // If one term selection method is set...
        if (featSelMI || featSelChi2 || featSelTF) {
            globalFeatSel();
        }
    }


    /**
     * Global feature selection procedure.
     * Based on (Manning, et al., 2008). It uses term frequencies instead
     * of document counts.
     * --<br>
     * (Manning, et al., 2008) Manning, C. D., Raghavan, P. and
     * Schutze, H., "An Introduction to Information Retrieval", 2008.
     */
    private void globalFeatSel() {
        double N00, N01, N10, N11;
        double N = (double)fullGraph.getTotalSumTF();
        for (int c = 0; c < categoryGraph.size(); c++) {
            // Compute term feature utility
            for (int t = 0; t < categoryGraph.get(c).getNumberOfElements();
                    t++) {
                N11 = (double)categoryGraph.get(c).getElement(t).
                    getTermFrequency();
                N10 = (double)calcTFOthersSum(categoryGraph.get(c).
                    getElement(t), c);
                N01 = (double)(categoryGraph.get(c).getTotalSumTF() -
                    N11);
                N00 = (double)(N - N11 - N10 - N01);
                if (featSelMI) {
                    categoryGraph.get(c).getElement(t).setUtilityMeasure(
                        (N11 / N) * Math.log((N * N11) / ((N10 + N11) * 
                        (N01 + N11))) / Math.log((double)2) +
                        (N01 / N) * Math.log((N * N01) / ((N00 + N01) * 
                        (N01 + N11))) / Math.log((double)2) +
                        (N10 / N) * Math.log((N * N10) / ((N10 + N11) * 
                        (N00 + N10))) / Math.log((double)2) +
                        (N00 / N) * Math.log((N * N00) / ((N00 + N01) * 
                        (N00 + N10))) / Math.log((double)2));
                } else if (featSelChi2) {
                    categoryGraph.get(c).getElement(t).setUtilityMeasure(
                        N * Math.pow((N11 * N00 - N10 * N01), (double)2) / 
                        ((N11 + N01) * (N11 + N10) * (N10 + N00) * (N01 + N00)));
                } else if (featSelTF) {
                    categoryGraph.get(c).getElement(t).setUtilityMeasure(N11);
                }
            }
        }
        for (int c = 0; c < categoryGraph.size(); c++) {
            // Sort list
            // Prune list keeping the first numFeats features.
            categoryGraph.get(c).pruneUtility(numSelFeats);
        }
        // Update full graph -> vocabulary
        GraphElement tmpElement;
        boolean found;
        ArrayList<Integer> elemsToRemove = new ArrayList<Integer>();
        for (int t = 0; t < fullGraph.getNumberOfElements(); t++) {
            tmpElement = fullGraph.getElement(t);
            found = false;
            for (int c = 0; c < categoryGraph.size(); c++) {
                if (categoryGraph.get(c).containsElement(tmpElement)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                elemsToRemove.add(new Integer(t));
            }
        }
        for (int rem = elemsToRemove.size() - 1; rem >= 0; rem--) {
            fullGraph.removeElement(elemsToRemove.get(rem).intValue());
        }
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
        categoryGraph = new ArrayList<Graph>();
    }


    /**
     * Functionality test.
     */
    @Test public void simpleClassification() {
        ARNReduced arnr = new ARNReduced();
        FeatureBox feat = new FeatureBox();
        feat.setWords("I hate going to the dentist .");
        arnr.inputTrainingExample(feat, "NEG");
        //
        feat = new FeatureBox();
        feat.setWords("I swim a lot .");
        arnr.inputTrainingExample(feat, "NEU");
        //
        feat = new FeatureBox();
        feat.setWords("I love reading books .");
        arnr.inputTrainingExample(feat, "POS");
        //
        arnr.train();
        //
        feat = new FeatureBox();
        feat.setWords("I like my dentist .");
        Assert.assertTrue(arnr.getCategory(feat).equals("NEG"));
        //
        feat = new FeatureBox();
        feat.setWords("You love .");
        Assert.assertTrue(arnr.getCategory(feat).equals("POS"));
    }

}

