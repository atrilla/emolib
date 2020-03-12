/*
 * File    : NearestCentroid.java
 * Created : 17-Jun-2009
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
import java.util.Hashtable;
import java.util.List;
import java.io.*;

import emolib.util.conf.*;
import emolib.util.proc.*;
import emolib.classifier.Classifier;
import emolib.classifier.FeatureBox;

import org.jdom.*;
import org.jdom.output.*;
import org.jdom.input.*;

/**
 * The <i>NearestCentroid</i> is a Rocchio classifier operating in
 * the circumplex.
 *
 * <p>
 * Once this classifier is fed with a sensible amount of examples for each class,
 * the arithmetic mean of each emotional dimension is computed in order to
 * provide the centroid corresponding to the class.
 * The core of this classifier is based on a 1-NN where the examples are the centroids
 * of the classes.
 * </p>
 * <p>
 * The NearestCentroid class includes a main method to train the classifier for
 * a future use. Its training dataset is a plain text file where each row represents
 * a training instance. The first numbers indicate the emotional dimensions while
 * the last one represents the affective category.
 * </p>
 * <p>
 * For more information about this classifier, please refer to (Trilla and Al&iacute;as, 2009).
 * </p>
 * <p>
 * --<br>
 * (Trilla and Al&iacute;as, 2009) Trilla, A. and Al&iacute;as, F., "Sentiment classification in English
 * from sentence-level annotations of emotions regarding models of affect", In Proceedings of the
 * 10th Annual Conference of the International Speech Communication Association (Interspeech 2009)
 * (ISSN: 1990-9772), pp. 516-519, 2009, September, Brighton, UK.
 * </p>
 *
 * @see emolib.classifier.machinelearning.KNearestNeighbour
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class NearestCentroid extends Classifier {

    /**
     * Property to determine the number of emotional dimensions the NearestCentroid deals with.
     */
    public final static String PROP_NUM_EMO_DIMS = "num_emo_dims";

    /**
     * Property to indicate a pre-trained classifier.
     */
    public final static String PROP_EXTERNAL_FILE = "external_file";


    private int numberOfEmotionalDimensions;

    private String externalFile;

    private ArrayList<Float> centroidValences;
    private ArrayList<Float> centroidActivations;
    private ArrayList<Float> centroidControls;
    private ArrayList<String> centroidCategories;
    private KNearestNeighbour theNN;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_NUM_EMO_DIMS, PropertyType.INT);
        registry.register(PROP_EXTERNAL_FILE, PropertyType.STRING);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        numberOfEmotionalDimensions = ps.getInt(PROP_NUM_EMO_DIMS, Integer.parseInt("2"));
        externalFile = ps.getString(PROP_EXTERNAL_FILE, "nullpath");
    }


    /**
     * Method to initialize the Classifier.
     */
    public void initialize() {
        if (externalFile.equals("nullpath")) {
            System.out.println("NearestCentroid: no external file has been provided!");
            System.exit(1);
        } else {
            load(externalFile);
        }
    }


    /**
     * Main constructor of this classifier.
     */
    public NearestCentroid() {
        centroidValences = new ArrayList<Float>();
        centroidActivations = new ArrayList<Float>();
        centroidControls = new ArrayList<Float>();
        centroidCategories = new ArrayList<String>();
        theNN = new KNearestNeighbour();
    }


    /**
     * Method to set the number of emotional dimensions.
     *
     * @param numDims The number of emotional dimensions.
     */
    public void setNumberOfEmotionalDimensions(int numDims) {
        numberOfEmotionalDimensions = numDims;
        theNN.setNumberOfEmotionalDimensions(numDims);
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#getCategory(emolib.classifier.FeatureBox)
     */
    public String getCategory(FeatureBox inputFeatures) {
        return theNN.getCategory(inputFeatures);
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#trainingProcedure()
     */
    public void trainingProcedure() {
        ArrayList<FeatureBox> exampleFeatures = getListOfExampleFeatures();
        ArrayList<String> exampleCategories = getListOfExampleCategories();
        Hashtable<String, Integer> soleCategories = new Hashtable<String, Integer>();
        int categoryOrder = 0;
        int presentClassNumber;
        String presentCat;
        FeatureBox presentFeats;
        ArrayList<Integer> numberOfExamples = new ArrayList<Integer>();
        Float tempFloat;

        for (int num_examples = 0; num_examples < exampleFeatures.size(); num_examples++) {
            presentCat = exampleCategories.get(num_examples);
            presentFeats = exampleFeatures.get(num_examples);
            if (!soleCategories.containsKey(presentCat)) {
                soleCategories.put(presentCat, new Integer(categoryOrder));
                centroidCategories.add(new String(presentCat));
                presentClassNumber = categoryOrder;
                categoryOrder++;
                numberOfExamples.add(new Integer(1));
            } else {
                presentClassNumber = soleCategories.get(presentCat).intValue();
                numberOfExamples.set(presentClassNumber,
                    new Integer(numberOfExamples.get(presentClassNumber).intValue() + 1));
            }
            // At least we have the valence
            if (numberOfEmotionalDimensions >= 1) {
                if (centroidValences.size() > presentClassNumber) {
                    centroidValences.set(presentClassNumber,
                        new Float(centroidValences.get(presentClassNumber).floatValue() + presentFeats.getValence()));
                } else {
                    centroidValences.add(new Float(presentFeats.getValence()));
                }
            }
            // At least we have the activation
            if (numberOfEmotionalDimensions >= 2) {
                if (centroidActivations.size() > presentClassNumber) {
                    centroidActivations.set(presentClassNumber,
                        new Float(centroidActivations.get(presentClassNumber).floatValue() +
                        presentFeats.getActivation()));
                } else {
                    centroidActivations.add(new Float(presentFeats.getActivation()));
                }
            }
            // At least we have the control
            if (numberOfEmotionalDimensions >= 3) {
                if (centroidControls.size() > presentClassNumber) {
                    centroidControls.set(presentClassNumber,
                        new Float(centroidControls.get(presentClassNumber).floatValue() + presentFeats.getControl()));
                } else {
                    centroidControls.add(new Float(presentFeats.getControl()));
                }
            }
        }

        FeatureBox tempFeatures;
        for (int numClass = 0; numClass < centroidCategories.size(); numClass++) {
            tempFeatures = new FeatureBox();
            tempFeatures.setNumberOfEmotionalDimensions(numberOfEmotionalDimensions);
            if (numberOfEmotionalDimensions >= 1) {
                centroidValences.set(numClass, new Float(centroidValences.get(numClass).floatValue() /
                    numberOfExamples.get(numClass).floatValue()));
                tempFeatures.setValence(centroidValences.get(numClass).floatValue());
            }
            if (numberOfEmotionalDimensions >= 2) {
                centroidActivations.set(numClass, new Float(centroidActivations.get(numClass).floatValue() /
                    numberOfExamples.get(numClass).floatValue()));
                tempFeatures.setActivation(centroidActivations.get(numClass).floatValue());
            }
            if (numberOfEmotionalDimensions >= 3) {
                centroidControls.set(numClass, new Float(centroidControls.get(numClass).floatValue() /
                    numberOfExamples.get(numClass).floatValue()));
                tempFeatures.setControl(centroidControls.get(numClass).floatValue());
            }
            theNN.inputTrainingExample(tempFeatures, centroidCategories.get(numClass));
        }
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#save(java.lang.String)
     */
    public void save(String path) {
        Element root = new Element("classifier");

        // Information about this classifier.
        Element info = new Element("information");

        Element name = new Element("name");
        name.setText("Nearest Centroid");
        info.addContent(name);

        Element desc = new Element("description");
        desc.setText("This classifier works in a continuous domain of 'n' emotional dimensions (typically " +
            "ranging from 1 to 3). A set of n-dimensional centroids is defined, one for each category. " +
            "Given a test instance the classifier returns the category corresponding to the nearest centroid.");
        info.addContent(desc);

        Element numDimensions = new Element("num_dimensions");
        numDimensions.setText(Integer.toString(numberOfEmotionalDimensions));
        info.addContent(numDimensions);

        Element numCategories = new Element("num_categories");
        numCategories.setText(Integer.toString(centroidCategories.size()));
        info.addContent(numCategories);

        root.addContent(info);

        // The set of centroids is defined.
        Element params = new Element("parameters");
        Element centroid;
        for (int ncats = 0; ncats < centroidCategories.size(); ncats++) {
            centroid = new Element("centroid");
            centroid.setAttribute("val", Float.toString(centroidValences.get(ncats)));
            if (numberOfEmotionalDimensions >= 2) {
                centroid.setAttribute("act", Float.toString(centroidActivations.get(ncats)));
            }
            if (numberOfEmotionalDimensions >= 3) {
                centroid.setAttribute("con", Float.toString(centroidControls.get(ncats)));
            }
            centroid.setAttribute("cat", centroidCategories.get(ncats));
            params.addContent(centroid);
        }
        root.addContent(params);

        outputData (root, path);
    }


    /**
     * Method to produce the an XML representation of this classifier.
     * This XML file will be saved in the local filesystem to the specified path.
     *
     * @param root The root of the XML structure.
     * @param path The complete path of the output XML file.
     */
    private void outputData(Element root, String path) {
        try {
            Format format = Format.getPrettyFormat();
            format.setIndent("    ");
            format.setEncoding("UTF-8");
            format.setTextMode (Format.TextMode.PRESERVE);
            XMLOutputter outputter = new XMLOutputter(format);
            File xmlResultsFile = new File(path);
            OutputStream out = new FileOutputStream(xmlResultsFile);
            outputter.output(new Document(root), out);
            out.flush();
        } catch (Exception e) {
            System.out.println("NearestCentroid: problem writing the XML file!");
            e.printStackTrace();
        }
    }


    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#load(java.lang.String)
     */
    public void load(String path) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(path));
            Element root = doc.detachRootElement();

            // Get the number of dimensions.
            List listInfo = root.getChildren("information");
            Element info = (Element)listInfo.get(0);
            List listNumDims = info.getChildren("num_dimensions");
            Element ndims = (Element)listNumDims.get(0);
            setNumberOfEmotionalDimensions((Integer.valueOf(ndims.getText())).intValue());

            // Get the centroids.
            FeatureBox tempFeatures;
            List listParams = root.getChildren("parameters");
            Element params = (Element)listParams.get(0);
            List listCentroids = params.getChildren("centroid");
            Element centroid;
            for (int ncentroid = 0; ncentroid < listCentroids.size(); ncentroid++) {
                centroid = (Element)listCentroids.get(ncentroid);
                tempFeatures = new FeatureBox();
                tempFeatures.setNumberOfEmotionalDimensions(numberOfEmotionalDimensions);
                if (numberOfEmotionalDimensions >= 1) {
                    tempFeatures.setValence((Float.valueOf(centroid.getAttributeValue("val"))).floatValue());
                }
                if (numberOfEmotionalDimensions >= 2) {
                    tempFeatures.setActivation((Float.valueOf(centroid.getAttributeValue("act"))).floatValue());
                }
                if (numberOfEmotionalDimensions >= 3) {
                    tempFeatures.setControl((Float.valueOf(centroid.getAttributeValue("con"))).floatValue());
                }
                theNN.inputTrainingExample(tempFeatures, centroid.getAttributeValue("cat"));
            }
        } catch (JDOMException e) {
            System.out.println("NearestCentroid: a problem occurred while loading the classifier XML!");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("NearestCentroid: a problem occurred while loading the classifier XML!");
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see emolib.classifier.Classifier#resetExamples()
     */
    @Override
    public void resetExamples() {
        super.resetExamples();
        centroidValences = new ArrayList<Float>();
        centroidActivations = new ArrayList<Float>();
        centroidControls = new ArrayList<Float>();
        centroidCategories = new ArrayList<String>();
        theNN = new KNearestNeighbour();
        theNN.setNumberOfEmotionalDimensions(numberOfEmotionalDimensions);
    }


    /**
     * Main method to train the NearestCentroid classifier.
     */
    public static void main(String[] args) throws Exception {
        NearestCentroid nc = new NearestCentroid();

        if ((args.length == 3) && ((Integer.valueOf(args[0]).intValue() == 1) ||
        (Integer.valueOf(args[0]).intValue() == 2) || ((Integer.valueOf(args[0]).intValue() == 3)))) {
            //
            int numberDims = Integer.valueOf(args[0]).intValue();
            nc.setNumberOfEmotionalDimensions(numberDims);

            String[] instanceParameters;
            FeatureBox tempFeatures;
            BufferedReader trainingFile = new BufferedReader(new FileReader(args[1]));
            String lineTrainingFile = trainingFile.readLine();
            while (lineTrainingFile != null) {
                tempFeatures = new FeatureBox();
                instanceParameters = lineTrainingFile.split(" ");
                if (numberDims == 1) {
                    tempFeatures.setNumberOfEmotionalDimensions(1);
                    tempFeatures.setValence((Float.valueOf(instanceParameters[0])).floatValue());
                }
                if (numberDims == 2) {
                    tempFeatures.setNumberOfEmotionalDimensions(2);
                    tempFeatures.setValence((Float.valueOf(instanceParameters[0])).floatValue());
                    tempFeatures.setActivation((Float.valueOf(instanceParameters[1])).floatValue());
                }
                if (numberDims == 3) {
                    tempFeatures.setNumberOfEmotionalDimensions(3);
                    tempFeatures.setValence((Float.valueOf(instanceParameters[0])).floatValue());
                    tempFeatures.setActivation((Float.valueOf(instanceParameters[1])).floatValue());
                    tempFeatures.setControl((Float.valueOf(instanceParameters[2])).floatValue());
                }
                nc.inputTrainingExample(tempFeatures, instanceParameters[instanceParameters.length - 1]);
                lineTrainingFile = trainingFile.readLine();
            }
            nc.train();
            nc.save(args[2]);
        } else if (args.length == 1) {
            nc = new NearestCentroid();
            if (args[0].equals("-h") || args[0].equals("--help")) {
                nc.printSynopsis();
            } else {
                System.out.println("NearestCentroid: Please enter the correct parameters!");
                System.out.println("");
                System.out.println("NearestCentroid: a problem accurred with the training parameters!");
                nc.printSynopsis();
            }
        } else {
            nc = new NearestCentroid();
            System.out.println("NearestCentroid: Please enter the correct parameters!");
            System.out.println("");
            nc.printSynopsis();
        }
    }


    /**
     * Prints the synopsis.
     */
    public void printSynopsis() {
        System.out.println("NearestCentroid trainer usage:");
        System.out.println("\tjava " + "[-Xmx256m] -cp EmoLib-X.Y.Z.jar emolib.classifier.machinelearning." +
            "NearestCentroid NUMBER_OF_DIMENTIONS INPUT_TRAINING_DATASET OUTPUT_XML_FILE");
    }

}

