/*
 * File    : DictionaryANLW.java
 * Created : 27-Nov-2008
 * By      : atrilla
 *
 * Emolib - Emotional Library
 *
 * Copyright (c) 2007 David Garcia &
 * 2008 Alexandre Trilla &
 * 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
 *
 * This file is part of Emolib.
 *
 * You should have received a copy of the rights granted with this
 * distribution of EmoLib. See COPYING.
 */

package emolib.eks.anlw;

import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;

/**
 * The <i>DictionaryANLW</i> class provides a dictionary to deal with
 * the emotional dimensions contained in the ANLW database.
 *
 * @author David Garc&iacute;a
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class DictionaryANLW {

    ArrayList theDataBase;
    ArrayList theKeys;


    /**
     * The main constructor of the DictionaryANLW object.
     *
     * @param path The path to find the ANLW database.
     */
    public DictionaryANLW(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String dictionaryLine = reader.readLine();
            // The first line of the ANLW.dat file corresponds to the size of the database.
            // It is an ArrayList instead of a Hashtable because multiple keys must be
            // allowed.
            theDataBase = new ArrayList(Integer.parseInt(dictionaryLine));
            theKeys = new ArrayList(Integer.parseInt(dictionaryLine));
            String[] fields;
            DimentionsANLW tempDimentions;
            dictionaryLine = reader.readLine();
            while (dictionaryLine != null) {
                fields = dictionaryLine.split("\t");
                tempDimentions = new DimentionsANLW(fields[0], Float.parseFloat(fields[1]),
                    Float.parseFloat(fields[2]), Float.parseFloat(fields[3]), fields[4]);
                theDataBase.add(tempDimentions);
                theKeys.add(fields[0]);
                dictionaryLine = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR loading the EmotionalKeywordSpotterANLW!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR loading the EmotionalKeywordSpotterANLW!");
            e.printStackTrace();
        }
    }


    /**
     * Function to determine if the input key has a match in the database.
     * This function should be called before any other trying to obtain information
     * from the emotional content of the key.
     *
     * @param emotionalKey The emotional key in question.
     *
     * @return True if the key has a match in the database.
     */
    public boolean containsEmotionalKey(String emotionalKey) {
        return theKeys.contains(emotionalKey);
    }


    /**
     * Checks if the the key has been produced by a noun.
     * This function assumes that "containsEmotionalKey" has already returned true.
     *
     * @param emotionalKey The emotional key in question.
     */
    public boolean isProducedByNoun(String emotionalKey) {
        int startIndex, endIndex;
        startIndex = theKeys.indexOf(emotionalKey);
        endIndex = theKeys.lastIndexOf(emotionalKey);
        boolean matchOk = false;
        DimentionsANLW tempDimentions;
        String tempClass;
        int possibleNounIndex;
        int minusOne = new Integer("-1");
        for (int counter = startIndex; counter <= endIndex; counter++) {
            tempDimentions = (DimentionsANLW)theDataBase.get(counter);
            tempClass = tempDimentions.getCategory();
            possibleNounIndex = tempClass.lastIndexOf("noun");
            if (possibleNounIndex != minusOne) {
                matchOk = true;
            }
        }
        return matchOk;
    }


    /**
     * Retrieves the emotional dimentions for this noun-based stem.
     * This function assumes that "isProducedByNoun" has already returned true.
     *
     * @param emotionalKey The emotional key in question.
     */
    public float[] getDimentionsProducedByNoun(String emotionalKey) {
        int startIndex, endIndex;
        startIndex = theKeys.indexOf(emotionalKey);
        endIndex = theKeys.lastIndexOf(emotionalKey);
        DimentionsANLW tempDimentions;
        String tempClass;
        int possibleNounIndex;
        int minusOne = new Integer("-1");
        float[] emoDimentions = new float[3];
        for (int counter = startIndex; counter <= endIndex; counter++) {
            tempDimentions = (DimentionsANLW)theDataBase.get(counter);
            tempClass = tempDimentions.getCategory();
            possibleNounIndex = tempClass.lastIndexOf("noun");
            if (possibleNounIndex != minusOne) {
                emoDimentions[0] = tempDimentions.getValence();
                emoDimentions[1] = tempDimentions.getActivation();
                emoDimentions[2] = tempDimentions.getControl();
                break;
            }
        }
        return emoDimentions;
    }


    /**
     * Retrieves the first emotional dimentions for this stem.
     * This function assumes that conflation has occurred, and thus a "best effort"
     * result is obtained.
     *
     * @param emotionalKey The emotional key in question.
     */
    public float[] getFirstDimentions(String emotionalKey) {
        DimentionsANLW tempDimentions;
        float[] emoDimentions = new float[3];
        tempDimentions = (DimentionsANLW)theDataBase.get(theKeys.indexOf(emotionalKey));
        emoDimentions[0] = tempDimentions.getValence();
        emoDimentions[1] = tempDimentions.getActivation();
        emoDimentions[2] = tempDimentions.getControl();
        return emoDimentions;
    }


    /**
     * Checks if the the key has been produced by a verb.
     * This function assumes that "containsEmotionalKey" has already returned true.
     *
     * @param emotionalKey The emotional key in question.
     */
    public boolean isProducedByVerb(String emotionalKey) {
        int startIndex, endIndex;
        startIndex = theKeys.indexOf(emotionalKey);
        endIndex = theKeys.lastIndexOf(emotionalKey);
        boolean matchOk = false;
        DimentionsANLW tempDimentions;
        String tempClass;
        int possibleNounIndex;
        int minusOne = new Integer("-1");
        for (int counter = startIndex; counter <= endIndex; counter++) {
            tempDimentions = (DimentionsANLW)theDataBase.get(counter);
            tempClass = tempDimentions.getCategory();
            possibleNounIndex = tempClass.lastIndexOf("verb");
            if (possibleNounIndex != minusOne) {
                matchOk = true;
            }
        }
        return matchOk;
    }


    /**
     * Retrieves the emotional dimentions for this verb-based stem.
     * This function assumes that "isProducedByVerb" has already returned true.
     *
     * @param emotionalKey The emotional key in question.
     */
    public float[] getDimentionsProducedByVerb(String emotionalKey) {
        int startIndex, endIndex;
        startIndex = theKeys.indexOf(emotionalKey);
        endIndex = theKeys.lastIndexOf(emotionalKey);
        DimentionsANLW tempDimentions;
        String tempClass;
        int possibleNounIndex;
        int minusOne = new Integer("-1");
        float[] emoDimentions = new float[3];
        for (int counter = startIndex; counter <= endIndex; counter++) {
            tempDimentions = (DimentionsANLW)theDataBase.get(counter);
            tempClass = tempDimentions.getCategory();
            possibleNounIndex = tempClass.lastIndexOf("verb");
            if (possibleNounIndex != minusOne) {
                emoDimentions[0] = tempDimentions.getValence();
                emoDimentions[1] = tempDimentions.getActivation();
                emoDimentions[2] = tempDimentions.getControl();
                break;
            }
        }
        return emoDimentions;
    }


    /**
     * Checks if the the key has been produced by an adjective.
     * This function assumes that "containsEmotionalKey" has already returned true.
     *
     * @param emotionalKey The emotional key in question.
     */
    public boolean isProducedByAdjective(String emotionalKey) {
        int startIndex, endIndex;
        startIndex = theKeys.indexOf(emotionalKey);
        endIndex = theKeys.lastIndexOf(emotionalKey);
        boolean matchOk = false;
        DimentionsANLW tempDimentions;
        String tempClass;
        int possibleNounIndex;
        int minusOne = new Integer("-1");
        for (int counter = startIndex; counter <= endIndex; counter++) {
            tempDimentions = (DimentionsANLW)theDataBase.get(counter);
            tempClass = tempDimentions.getCategory();
            possibleNounIndex = tempClass.lastIndexOf("adjective");
            if (possibleNounIndex != minusOne) {
                matchOk = true;
            }
        }
        return matchOk;
    }


    /**
     * Retrieves the emotional dimentions for this adjective-based stem.
     * This function assumes that "isProducedByAdjective" has already returned true.
     *
     * @param emotionalKey The emotional key in question.
     */
    public float[] getDimentionsProducedByAdjective(String emotionalKey) {
        int startIndex, endIndex;
        startIndex = theKeys.indexOf(emotionalKey);
        endIndex = theKeys.lastIndexOf(emotionalKey);
        DimentionsANLW tempDimentions;
        String tempClass;
        int possibleNounIndex;
        int minusOne = new Integer("-1");
        float[] emoDimentions = new float[3];
        for (int counter = startIndex; counter <= endIndex; counter++) {
            tempDimentions = (DimentionsANLW)theDataBase.get(counter);
            tempClass = tempDimentions.getCategory();
            possibleNounIndex = tempClass.lastIndexOf("adjective");
            if (possibleNounIndex != minusOne) {
                emoDimentions[0] = tempDimentions.getValence();
                emoDimentions[1] = tempDimentions.getActivation();
                emoDimentions[2] = tempDimentions.getControl();
                break;
            }
        }
        return emoDimentions;
    }

}

