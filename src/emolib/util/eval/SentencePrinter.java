/*
 * File    : SentencePrinter.java
 * Created : 26-Feb-2010
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

package emolib.util.eval.fwf;

import java.io.*;
import java.util.List;

import org.jdom.*;
import org.jdom.output.*;
import org.jdom.input.*;

/**
 * The <i>SentencePrinter</i> class prints the sentences of the XML
 * document that is distributed with the FWF corpus.
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class SentencePrinter {

    public void printAll(String path) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(path));
            Element root = doc.detachRootElement();

            // Get the number of dimensions.
            List listTexts = root.getChildren("text");
            Element presentText;
            List listSentences;
            Element presentSent;
            List listTokens;
            Element presentToken;

            for (int textNum = 0; textNum < listTexts.size(); textNum++) {
                presentText = (Element)listTexts.get(textNum);
                listSentences = presentText.getChildren("sentence");
                for (int sentNum = 0; sentNum < listSentences.size(); sentNum++) {
                    presentSent = (Element)listSentences.get(sentNum);
                    listTokens = presentSent.getChildren("token");
                    for (int tokenNum = 0; tokenNum < listTokens.size(); tokenNum++) {
                        presentToken = (Element)listTokens.get(tokenNum);
                        System.out.print(presentToken.getText() + " ");
                    }
                    if (presentSent.getAttributeValue("sentiment").equals("negative")) {
                        System.out.println("N");
                    } else if (presentSent.getAttributeValue("sentiment").equals("positive")) {
                        System.out.println("P");
                    } else {
                        System.out.println("neutral");
                    }
                }
            }
        } catch (JDOMException e) {
            System.out.println("SentencePrinter: a problem occurred while loading the XML!");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("SentencePrinter: a problem occurred while loading the XML!");
            e.printStackTrace();
        }
    }


    /**
     * Main method to run the SentencePrinter.
     */
    public static void main(String[] args) throws Exception {
        SentencePrinter sp = new SentencePrinter();

        if ((args.length == 1) && (args[0].equals("-h") == false)) {
            sp.printAll(args[0]);
        } else if ((args.length == 1) && (args[0].equals("-h") == true)) {
            sp.printSynopsis();
        } else {
            System.out.println("SentencePrinter: Please enter the correct parameter!");
            System.out.println("");
            System.out.println("SentencePrinter: a problem accurred with the parameter!");
            sp.printSynopsis();
        }
    }


    /**
     * Prints the synopsis.
     */
    public void printSynopsis() {
        System.out.println("SentencePrinter usage:");
        System.out.println("\tjava " + "[-Xmx256m] -cp EmoLib-X.Y.Z.jar emolib.util.eval.fwf.SentencePrinter " +
            "FWF_XML_FILE");
    }

}

