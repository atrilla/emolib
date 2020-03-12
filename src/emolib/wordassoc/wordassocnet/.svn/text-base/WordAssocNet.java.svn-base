/*
 * File    : WordAssocNet.java
 * Created : 23-Feb-2012
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2012 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.wordassoc.wordassocnet;

import emolib.wordassoc.*;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import emolib.util.conf.*;
import emolib.util.proc.*;

import java.util.ArrayList;

/**
 * The <i>WordAssocNet</i> class associates words using the Word Associations
 * Network.
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class WordAssocNet extends WordAssoc {

    // http://wordassociations.net/

    /**
     * Maximum number of associated terms allowed to retrieve.
     */
    public final static String PROP_MAX_ASSOC = "max_assoc";


    private int maxAssociations;


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#register(java.lang.String, emolib.util.conf.Registry)
     */
    public void register(String name, Registry registry) throws PropertyException {
        super.register(name, registry);
        registry.register(PROP_MAX_ASSOC, PropertyType.INT);
    }


    /* (non-Javadoc)
     * @see emolib.util.conf.Configurable#newProperties(emolib.util.conf.PropertySheet)
     */
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        maxAssociations = ps.getInt(PROP_MAX_ASSOC, 10);
    }


    /**
     * Method to initialize the WordAssocNet.
     */
    public void initialize() {
    }


    /**
     * Main constructor of the WordAssocNet.
     */
    public WordAssocNet() {
    }


    /**
     * Method to perform the word association process.
     *
     * @param inputTextDataObject The TextData object to process.
     */
    public void applyWordAssociation(TextData inputTextDataObject) {
        WordData tempWordData;
        for (int numberOfWords = 0; numberOfWords < inputTextDataObject.getNumberOfWords(); numberOfWords++) {
            tempWordData = inputTextDataObject.getWordData(numberOfWords);
            if (tempWordData.hasEmotionalContent()) {
                try {
                    Document doc = Jsoup.connect("http://wordassociations.net/search?hl=en&q=" +
                        tempWordData.getWord() + "&button=Search").get();
                    String wText = doc.outerHtml();
                    String[] wChunk = wText.split("w=");    
                    // first one is html overhead
                    String[] assocWord;
                    ArrayList bunchOfAssocWords = new ArrayList();
                    for (int i = 1; i < (1 + maxAssociations); i++) {
                        if (i < wChunk.length) {
                            assocWord = wChunk[i].split("\"");
                            bunchOfAssocWords.add(assocWord[0]);
                        }
                    }
                    tempWordData.setSense(bunchOfAssocWords);
                } catch (Exception e) {
                    System.out.println("WordAssocNet: access error!");
                    e.printStackTrace();
                }
            }
        }

    }
}

