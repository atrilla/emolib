/*
 * File    : IndentationFormatter.java
 * Created : 18-Oct-2010
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

package emolib.util;

import java.io.*;

/**
 * The <i>IndentationFormatter</i> class performs a check-and-correct process of the
 * indentation of the source files compiled in the input file.
 *
 * <p>
 * The motivation behind this class is the standardisation of the code style.
 * It checks (and corrects if a mistake is found) that the indentations follow
 * a reasonable structure, i.e., after <i>x</i> spaces, <i>x + 4</i> follow,
 * if a curly brace ends the previous line, and so on.
 * </p>
 * <p>
 * After the process, each modified file will have a "file.indent" version to
 * compare the effect of the indentation sets.
 * </p>
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class IndentationFormatter {

    private int theIndent;
    private boolean longLineIndent;


    /**
     * Void constructor.
     */
    public IndentationFormatter() {
        theIndent = 0;
        longLineIndent = false;
    }


    /**
     * Method to reset the indetation level counter.
     */
    public void resetIndent() {
        theIndent = 0;
        longLineIndent = false;
    }


    /**
     * Prints the synopsis.
     */
    public void printSynopsis() {
        System.out.println("IndentationFormatter usage:");
        System.out.println("\tjava -cp EmoLib-X.Y.Z.jar emolib.util.IndentationFormatter INPUT_FILE_LIST");
    }


    /**
     * Function to correct the indentation of a line of code.
     *
     * @param line The line of code.
     *
     * @return The line with correct indents.
     */
    public String correctIndentation(String line) {
        // Beware of comments (stars), include, package, and final braces
        boolean isComment = false;
        //
        // Remove leading and trailing spaces
        String text = line.trim();
        if (text.length() > 0) {
            if (text.startsWith("*")) {
                text = " " + text;
                isComment = true;
            }
            if ((text.endsWith("}")) || (text.startsWith("}"))) {
                if (!isComment) {
                    theIndent--;
                }
            }
            for (int level = theIndent; level > 0; level--) {
                text = "    " + text;
            }
            if (longLineIndent) {
                text = "    " + text;
                longLineIndent = false;
            }
            if (text.endsWith("{")) {
                if (!isComment) {
                    theIndent++;
                }
            }
            if ((text.endsWith("+")) || (text.endsWith("-"))) {
                longLineIndent = true;
            }
        }
        //
        return text;
    }


    public static void main(String[] args) throws Exception {
        IndentationFormatter indentForm = new IndentationFormatter();
        BufferedReader codeFileReader;
        BufferedWriter codeFileWriter;
        //
        if (args.length == 1) {
            BufferedReader listOfCodeFiles = new BufferedReader(new FileReader(args[0]));
            for (String  sourceCodeFile = listOfCodeFiles.readLine(); sourceCodeFile != null;
            sourceCodeFile = listOfCodeFiles.readLine()) {
                codeFileReader = new BufferedReader(new FileReader(sourceCodeFile));
                codeFileWriter = new BufferedWriter(new FileWriter(sourceCodeFile + ".indent"));
                // New file to process.
                indentForm.resetIndent();
                for (String codeLineRead = codeFileReader.readLine(); codeLineRead != null;
                codeLineRead = codeFileReader.readLine()) {
                    // Process the line for indentation mistakes
                    codeLineRead = indentForm.correctIndentation(codeLineRead);
                    codeFileWriter.write(codeLineRead);
                    codeFileWriter.newLine();
                }
                codeFileReader.close();
                codeFileWriter.close();
            }
            listOfCodeFiles.close();
        } else {
            indentForm.printSynopsis();
            System.exit(1);
        }
    }

}

