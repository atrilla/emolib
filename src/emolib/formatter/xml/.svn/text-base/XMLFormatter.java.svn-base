/*
 * File    : XMLFormatter.java
 * Created : 15-Dec-2008
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2008 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.formatter.xml;

import emolib.formatter.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

import org.jdom.*;
import org.jdom.output.*;

import java.io.*;

/**
 * The <i>XMLFormatter</i> class performs the
 * formatting process of the output data into a XML file.
 *
 * <p>
 * The structure of the resulting XML file responds to the sentence and paragraph levels of
 * analysis. Thus, the resulting hierarchy is: document, paragraph, sentence.
 * </p>
 * <p>
 * The XMLFormatter accepts 2 configuration parameters:
 * <ul>
 *     <li>
 *         The path of the resulting XML results file.
 *     </li>
 *     <li>
 *         The threshold to discern between a positive valence or a negative one.
 *     </li>
 * </ul>
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class XMLFormatter extends AffectiveFormatter {

    public final static String PROP_XMLRESULTS_PATH = "xmlresultspath";
    public final static String PROP_THRESHOLD = "threshold";


    private String xmlResultsPath;
    private float threshold;

    private Element root;
    private int numOfParagraph;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_XMLRESULTS_PATH, PropertyType.STRING);
        registry.register(PROP_THRESHOLD, PropertyType.FLOAT);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        xmlResultsPath = ps.getString(PROP_XMLRESULTS_PATH, "nullpath");
        threshold = ps.getFloat(PROP_THRESHOLD, Float.parseFloat("5.75"));
    }


    /**
     * Method to initialize the XMLFormatter.
     */
    public void initialize() {
        root = new Element("document");
        numOfParagraph = 0;
    }


    /* (non-Javadoc)
     * @see emolib.util.proc.DataProcessor#flush()
     */
    public void flush() {
        initialize();
    }


    /**
     * Main constructor of the XMLFormatter.
     */
    public XMLFormatter() {
    }


    /**
     * Method to perform the formatting process at the paragraph and sentence level.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyFormatting(TextData inputTextDataObject) {
        Element paragraph;
        Element sentence;
        ParagraphData tempParagraph;
        SentenceData tempSentence;
        WordData tempWord;
        String sentenceText;

        // This is quite stupid since it only inputs one paragraph.
        for (int numberOfParagraph = 0; numberOfParagraph < inputTextDataObject.getNumberOfParagraphs();
        numberOfParagraph++) {
            // This makes it possible to keep record of the totality of input paragraphs.
            numOfParagraph++;
            tempParagraph = inputTextDataObject.getParagraphData(numberOfParagraph);
            paragraph = new Element("paragraph");
            paragraph.setAttribute("num", Integer.toString(numOfParagraph));
            for (int numberOfSentence = 0; numberOfSentence < tempParagraph.getNumberOfSentences();
            numberOfSentence++) {
                tempSentence = tempParagraph.getSentenceData(numberOfSentence);
                sentence = new Element("sentence");
                sentence.setAttribute("num", Integer.toString(numberOfSentence + 1));
                sentenceText = new String();
                for (int numberOfWord = 0; numberOfWord < tempSentence.getNumberOfWords(); numberOfWord++) {
                    tempWord = tempSentence.getWordData(numberOfWord);
                    sentenceText = sentenceText.concat(" " + tempWord.getWord());
                }
                // Undo the slack trick.
                sentence.setText(sentenceText.replace("|", "/"));
                if (tempSentence.containsEmotionalDimentions()) {
                    sentence.setAttribute("val", Float.toString(tempSentence.getEmotionalValence()));
                    sentence.setAttribute("act", Float.toString(tempSentence.getEmotionalActivation()));
                    sentence.setAttribute("con", Float.toString(tempSentence.getEmotionalControl()));
                    sentence.setAttribute("cat", tempSentence.getEmotionalCategory());
                } else {
                    sentence.setAttribute("val", Float.toString(threshold));
                    sentence.setAttribute("act", Float.toString(threshold));
                    sentence.setAttribute("con", Float.toString(threshold));
                    sentence.setAttribute("cat", new String("neutral"));
                }
                paragraph.addContent(sentence);
            }
            if (tempParagraph.containsEmotionalDimentions()) {
                paragraph.setAttribute("val", Float.toString(tempParagraph.getEmotionalValence()));
                paragraph.setAttribute("act", Float.toString(tempParagraph.getEmotionalActivation()));
                paragraph.setAttribute("con", Float.toString(tempParagraph.getEmotionalControl()));
                paragraph.setAttribute("cat", tempParagraph.getEmotionalCategory());
            } else {
                paragraph.setAttribute("val", Float.toString(threshold));
                paragraph.setAttribute("act", Float.toString(threshold));
                paragraph.setAttribute("con", Float.toString(threshold));
                paragraph.setAttribute("cat", new String("neutral"));
            }
            root.addContent(paragraph);
        }
        if (inputTextDataObject.containsEmotionalDimentions()) {
            root.setAttribute("val", Float.toString(inputTextDataObject.getEmotionalValence()));
            root.setAttribute("act", Float.toString(inputTextDataObject.getEmotionalActivation()));
            root.setAttribute("con", Float.toString(inputTextDataObject.getEmotionalControl()));
            root.setAttribute("cat", inputTextDataObject.getEmotionalCategory());
        } else {
            root.setAttribute("val", Float.toString(threshold));
            root.setAttribute("act", Float.toString(threshold));
            root.setAttribute("con", Float.toString(threshold));
            root.setAttribute("cat", new String("neutral"));
        }
    }


    /**
     * Method to produce the results obtained from the text processing pipeline.
     * The XMLFormatter will produce an XML representation of these results.
     * According to the configuration given by the class properties defined in the
     * configuration file, this XML results file will be saved in the local
     * filesystem if the path is defined.
     */
    public void outputData() {
        try {
            if (xmlResultsPath.equals("nullpath") == false) {
                Format format = Format.getPrettyFormat();
                format.setIndent("    ");
                format.setEncoding("UTF-8");
                format.setTextMode (Format.TextMode.PRESERVE);
                XMLOutputter outputter = new XMLOutputter(format);
                File xmlResultsFile = new File(xmlResultsPath, "emolib.results.xml");
                OutputStream out = new FileOutputStream(xmlResultsFile);
                outputter.output(new Document(root), out);
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("EmoLib: problem writing the XML results file!");
            e.printStackTrace();
        }
    }


    /**
     * Method to produce the results obtained from the text processing pipeline.
     * The XMLFormatter will produce an XML representation of these results.
     * According to the configuration given by the class properties defined in the
     * configuration file, this XML results file will be saved to the present
     * file.
     *
     * @param file File The determined file to output the results. This is normally a temp file.
     */
    public void outputData(File file) {
        try {
            Format format = Format.getPrettyFormat();
            format.setIndent("    ");
            format.setEncoding("UTF-8");
            format.setTextMode (Format.TextMode.PRESERVE);
            XMLOutputter outputter = new XMLOutputter(format);
            OutputStream out = new FileOutputStream(file);
            outputter.output(new Document(root), out);
            out.flush();
        } catch (Exception e) {
            System.out.println("EmoLib: problem writing the XML results file!");
            e.printStackTrace();
        }
    }

}

