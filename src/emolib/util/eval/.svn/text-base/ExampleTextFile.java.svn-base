/*
 * File    : ExampleTextFile.java
 * Created : 03-Dec-2008
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

package emolib.util.eval;

import java.io.*;
import java.net.URL;

import emolib.*;
import emolib.util.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>ExampleTextFile</i> class performs an emotional analysis on the input text file.
 *
 * <p>
 * The ExampleTextFile already contains a main method to launch the application.
 * In order to do so three parameters are required:
 * the configuration file, the language of analysis and the input
 * text file to analyse. If this requirement isn't fulfilled, EmoLib will display the
 * correspondent error, the synopsis and will shut down afterwards. If otherwise the common
 * "-h" and "--help" parameters are passed, EmoLib will display the synopsis
 * and shut down afterwards.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class ExampleTextFile {

    /**
     * Void constructor.
     */
    public ExampleTextFile() {
    }


    /**
     * Prints the synopsis.
     */
    public void printSynopsis() {
        System.out.println("ExampleTextFile usage:");
        System.out.println("\tjava " + "[-Xmx256m] -cp EmoLib-X.Y.Z.jar emolib.util.eval.ExampleTextFile " +
            "CONFIG_FILE LANGUAGE INPUT_TEXT_FILE");
    }


    public static void main(String[] args) throws Exception {
        ExampleTextFile example = new ExampleTextFile();

        if (args.length == 3) {
            // Location of the configuration XML file
            URL configFile = new File(args[0]).toURI().toURL();
            // Declaration and instantiation of the Configuration Manager
            ConfigurationManager cm = new ConfigurationManager(configFile);
            // Declaration and instantiation of the AffectiveTagger through the Configuration Manager
            AffectiveTagger tagger = null;
            String testFile = "";
            if (args[1].equals("spanish")) {
                tagger = (AffectiveTagger)cm.lookup("spanish_tagger");
            } else {
                tagger = (AffectiveTagger)cm.lookup("english_tagger");
            }
            testFile = args[2];

            BufferedReader in = new BufferedReader(new FileReader(testFile));
            System.out.println("");
            System.out.println("Welcome to the Example class using EmoLib with an input text file.");
            System.out.println("The language of use is: " + tagger.getLanguage());
            System.out.println("");
            String lineFromFile = in.readLine();
            while (lineFromFile != null) {
                // Feed the system
                // Blank lines make the system misfunction.
                if (lineFromFile.length() > 0) {
                    tagger.inputData(lineFromFile);
                }
                lineFromFile = in.readLine();
            }
            in.close();
            // Retrieve the results
            tagger.outputData();
        } else if (args.length == 1) {
            if (args[0].equals("-h") || args[0].equals("--help")) {
                example.printSynopsis();
            } else {
                System.out.println("ExampleTextFile: Please enter the correct parameters!");
                System.out.println("");
                example.printSynopsis();
            }
        } else {
            System.out.println("ExampleTextFile: Please enter the correct parameters!");
            System.out.println("");
            example.printSynopsis();
        }
    }

}

