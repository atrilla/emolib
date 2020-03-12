/*
 * File    : CreateTextClassData.java
 * Created : 03-Dec-2009
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


package emolib.util.eval;

import java.io.*;

/**
 * The <i>CreateTextClassData</i> class performs the creation of a dataset for the text classifier.
 *
 * <p>
 * This class works out of the box for N-P-neutral classes if the folders (neg, pos and neutral)
 * have already been created in the work folder.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class CreateTextClassData {

    int numNeg, numPos, numNeu;


    /**
     * Void constructor.
     */
    public CreateTextClassData() {
        numNeu = 1;
        numNeg = 1;
        numPos = 1;
    }


    /**
     * Prints the synopsis.
     */
    public void printSynopsis() {
        System.out.println("CreateTextClassData usage:");
        System.out.println("\tjava " + "[-Xmx256m] CreateTextClassData INPUT_TEXT_FILE GROUND_TRUTH");
    }


    /**
     * The procedure that processes.
     *
     * @param textFile The input text file.
     * @param groundTruth The ground truth.
     */
    public void createDataset(String textFile, String groundTruth) throws Exception {
        BufferedReader inText = new BufferedReader(new FileReader(textFile));
        BufferedReader inGT = new BufferedReader(new FileReader(groundTruth));
        System.out.println("");
        System.out.println("Dataset builder for the text classifier");
        System.out.println("");
        String lineFromText = inText.readLine();
        String lineFromGT = inGT.readLine();
        while (lineFromText != null) {
            if (lineFromGT.trim().equals("N")) {
                writeNewFile("neg/" + numNeg + ".txt", lineFromText);
                numNeg++;
            } else if (lineFromGT.trim().equals("P")) {
                writeNewFile("pos/" + numPos + ".txt", lineFromText);
                numPos++;
            } else {
                writeNewFile("neutral/" + numNeu + ".txt", lineFromText);
                numNeu++;
            }
            lineFromText = inText.readLine();
            lineFromGT = inGT.readLine();
        }
    }


    private void writeNewFile(String name, String content) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(name));
            out.write(content);
            out.close();
        } catch (IOException e) {
            System.out.println("There's been a problem!");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        CreateTextClassData creator = new CreateTextClassData();

        if (args.length == 2) {
            creator.createDataset(args[0], args[1]);
        } else {
            System.out.println("CreateTextClassData: Please enter the correct parameters!");
            System.out.println("");
            creator.printSynopsis();
        }
    }

}

