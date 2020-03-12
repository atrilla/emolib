/*
 * File    : ProcessingService.java
 * Created : 17-Nov-2011
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

package emolib.util.servlet.es;

import java.lang.InstantiationException;
import java.lang.reflect.*;
import java.io.*;
import java.util.List;
import java.net.URL;
import java.net.Inet4Address;
import java.net.InetAddress;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.Date;

import java.lang.Math;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import emolib.*;
import emolib.util.*;
import emolib.util.conf.*;
import emolib.util.proc.*;

/**
 * The <i>ProcessingService</i> is a servlet class that acts as a web interface to
 * perform the emotional tagging of the posted text in Spanish.
 *
 * <p>This is a copy of the ProcessingService translated into Spanish by
 * Isaac Lozano.
 * </p>
 *
 * @see emolib.util.servlet.en.ProcessingService ProcessingService
 *
 * @author Alexandre Trilla (atrilla@salle.url.edu)
 */
public class ProcessingService extends HttpServlet {

    private URL configFile;
    private ConfigurationManager cm;
    private AffectiveTagger tagger;
    private boolean outOfMemory;


    /**
     * Main constructor of the servlet service.
     * It loads the needed resources once.
     */
    public ProcessingService() throws Exception {
        // Gets the default "conf/emolib.config.xml" file and loads the "spanish_tagger".
        configFile = this.getClass().getResource("/conf/emolib.config.xml");
        cm = new ConfigurationManager(configFile);
        tagger = (AffectiveTagger)cm.lookup("spanish_tagger");
        outOfMemory = false;
    }


    /**
     * Function that determines if the input text contains sensible data to process (letters, and thus
     * words).
     *
     * @param theText The text to analyse.
     *
     * @return True if the text is valid for further analysis.
     */
    private boolean containsLetters(String theText) {
        boolean hasLetters = false;

        Pattern pattern = Pattern.compile("([a-z]|[A-Z])+");
        Matcher matcher = pattern.matcher(theText);
        if (matcher.find()) {
            hasLetters = true;
        }

        return hasLetters;
    }


    /**
     * Function to determine if the demo limits are exceeded.
     * The demo is restricted to 50 words max or more than 10 connections per natural day.
     * The privileged user has unrestricted access.
     * The logs are registered in the data/servlog folder. Privileged users are
     * identified by their name instead of by their IP.
     *
     * @param text The text to process.
     * @param date The date of the request.
     * @param ip The IP of the client.
     *
     * @return True if the request is valid.
     */
    private boolean demoLimitsExceeded(String text, String date, String ip) throws IOException {
        boolean limits = false;
        // First check if it's a privileged user
        boolean privils = false;
        String path = tagger.getEmoLibPath();
        BufferedReader acl = new BufferedReader(new FileReader(path + "/data/service.acl"));
        // IP:MASK:Name_Of_The_Privileged_User
        String privilegedAddress = acl.readLine();
        String[] privChunk = null;
        boolean exists = false;
        boolean success = false;
        while (privilegedAddress != null) {
            privChunk = privilegedAddress.split(":");
            // Seen in Stackoverflow:
            // Validate an IP Address (with Mask)
            if (checkAddressing(privChunk[0], privChunk[1], ip)) {
                privils = true;
                // Register this privileged request
                String[] today = date.split(" ");
                String folderToday = path + "/data/servlog/" + today[5] + "/" + today[1] + "/" + today[2];
                exists = (new File(folderToday + "/" + privChunk[2] + ".log")).exists();
                if (!exists) {
                    // No service has been requested by this privileged
                    // IP yet.
                    // Let's create its log file for today
                    // Ensures that the folder for today is created
                    success = (new File(folderToday)).mkdirs();
                    BufferedWriter out = new BufferedWriter(
                        new FileWriter(folderToday + "/" +
                        privChunk[2] + ".log"));
                    out.write(ip + ":" + "1");
                    out.close();
                } else {
                    // Check permitted number of requests
                    BufferedReader ipFile = new BufferedReader(
                        new FileReader(folderToday + "/" +
                        privChunk[2] + ".log"));
                    String[] pieces = (ipFile.readLine()).split(":");
                    int hits = Integer.parseInt(pieces[1]);
                    ipFile.close();
                    BufferedWriter out = new BufferedWriter(
                        new FileWriter(folderToday + "/" +
                        privChunk[2] + ".log"));
                    out.write(ip + ":" + Integer.toString(hits + 1));
                    out.close();
                }
                break;
            }
            privilegedAddress = acl.readLine();
        }
        acl.close();
        if (!privils) {
            // Check if the text to analyse contains more than 50 words.
            String[] words = text.split(" ");
            if (words.length > 50) {
                limits = true;
            } else {
                // Check that the this request without privileges does not exceed 10 hits per day.
                String[] today = date.split(" ");
                String folderToday = path + "/data/servlog/" + today[5] + "/" + today[1] + "/" + today[2];
                exists = (new File(folderToday + "/" + ip + ".log")).exists();
                if (!exists) {
                    // No service has been requested by this IP yet.
                    // Let's create its log file for today
                    // Ensures that the folder for today is created
                    success = (new File(folderToday)).mkdirs();
                    BufferedWriter out = new BufferedWriter(new FileWriter(folderToday + "/" + ip + ".log"));
                    out.write(ip + ":" + "1");
                    out.close();
                } else {
                    // Check permitted number of requests
                    BufferedReader ipFile = new BufferedReader(new FileReader(folderToday + "/" + ip + ".log"));
                    String[] pieces = (ipFile.readLine()).split(":");
                    int hits = Integer.parseInt(pieces[1]);
                    ipFile.close();
                    if (hits > 10) {
                        limits = true;
                    } else {
                        BufferedWriter out = new BufferedWriter(new FileWriter(folderToday + "/" + ip + ".log"));
                        out.write(ip + ":" + Integer.toString(hits + 1));
                        out.close();
                    }
                }
            }
        }
        //
        return limits;
    }


    /**
     * Chech the addressing of the two IP's.
     * If they belong to the same network, yield TRUE.
     *
     * @param privilIP The privileged IP address.
     * @param mask The mask.
     * @param inputIP The IP address to check.
     *
     * @return TRUE if the two addresses belong to the same network.
     */
    private boolean checkAddressing(String privilIP, String mask,
            String inputIP) {
        boolean sameNetwork = true;
        String[] privChunk = privilIP.split("[.]");
        String[] inputChunk = inputIP.split("[.]");
        int theMask = Integer.parseInt(mask);
        int whole = theMask / 8;
        int part = theMask % 8;
        for (int cBytes = 0; cBytes < whole; cBytes++) {
            if (!privChunk[cBytes].equals(inputChunk[cBytes])) {
                sameNetwork = false;
                break;
            }
        }
        if (sameNetwork && (part != 0)) {
            int lastBytePriv = Integer.parseInt(privChunk[whole]);
            int lastByteInput = Integer.parseInt(inputChunk[whole]);
            int maskFrame = 0;
            for (int cPart = 0; cPart < part; cPart++) {
                maskFrame += (int)Math.pow((double)2, (double)(7 - cPart));
            }
            if ((lastBytePriv & maskFrame) != (lastByteInput & maskFrame)) {
                sameNetwork = false;
            }
        }
        return sameNetwork;
    }


    /**
     * Overrides the <i>service</i> procedure of the servlet specification in
     * order to include the EmoLib processing facilities.
     */
    public void service(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException, OutOfMemoryError {
        try {
            Runtime runtime = Runtime.getRuntime();
            // At least, let half the memory be available
            if (runtime.freeMemory() < runtime.totalMemory() * 0.2) {
                System.gc();
            }
            req.setCharacterEncoding("UTF-8");
            PrintWriter output = res.getWriter();
            res.setContentType("text/html; charset=UTF-8");
            String textToProcess = req.getParameter("TEXTOPROCESS");
            textToProcess = textToProcess.trim();
            String doDown = req.getParameter("DOWNLOAD");
            String parToBeProcessed;
            String ipClient = req.getRemoteAddr();
            Date theDate = new Date();
            String wholeDate = theDate.toString();
            // Determine if demo limits are exceeded
            boolean exceed = demoLimitsExceeded(textToProcess, wholeDate, ipClient);
            //
            if (exceed) {
                try {
                    printHeader(output);
                    colourParagraphHeader(output, "WARNING", "NONE", "NONE", "NONE");
                    colourSentence(output, "La demo del servicio de análisis EmoLib permite un máximo " +
                        "de 50 palabras por petición y 10 peticiones por día natural, " +
                        "y éstos límites han sido rebasados! Puede contactar con " +
                        "<a href=\"mailto:atrilla@salle.url.edu\">Alexandre Trilla</a> " +
                        "para contratar privilegios de acceso.", "neutral", "NONE", "NONE", "NONE");
                    colourParagraphFooter(output);
                    printFooter(output);
                } catch (Exception e) {
                    output.println("<p>Un problema ha ocurrido con los límites de la demo!</p>");
                    e.printStackTrace();
                }
            } else {
                // Creates a temporal file to save the results.
                try {
                    String[] parToProcess = textToProcess.split("\n");
                    for (int numPar = 0; numPar < parToProcess.length; numPar++) {
                        // Void paragraph make the system crash.
                        if (containsLetters(parToProcess[numPar])) {
                            tagger.inputData(parToProcess[numPar]);
                        }
                    }
                    File resultsTempFile = File.createTempFile("emolib.results.xml", ".temp");
                    URL tempResultsURL = resultsTempFile.toURL();
                    // Writes the temp file.
                    tagger.outputData(resultsTempFile);
                    // Decide whether downloading the results file or showing the nice GUI.
                    if (doDown != null) {
                        doDownload(req, res, resultsTempFile.getAbsolutePath(), "emolib.results.xml");
                    } else {
                        printHeader(output);
    
                        // Extracts the info from the XML results file.
                        InputStream is = new FileInputStream(resultsTempFile);
                        SAXBuilder saxBuilder = new SAXBuilder();
                        Document doc = saxBuilder.build(is);
                        Element root = doc.detachRootElement();
                        Element paragraph, sentence;
                        List sentencesList;
                        String sentenceText, sentenceCategory, paragraphCategory, valence, activation, control;
                        // The XML hierarchy starts here.
                        List paragraphsList = root.getChildren("paragraph");
                        for (int parNum = 0; parNum < paragraphsList.size(); parNum++) {
                            paragraph = (Element)paragraphsList.get(parNum);
                            paragraphCategory = paragraph.getAttributeValue("cat");
                            valence = paragraph.getAttributeValue("val");
                            activation = paragraph.getAttributeValue("act");
                            control = paragraph.getAttributeValue("con");
                            colourParagraphHeader(output, paragraphCategory, valence, activation, control);
                            sentencesList = paragraph.getChildren("sentence");
                            for (int sentNum = 0; sentNum < sentencesList.size(); sentNum++) {
                                sentence = (Element)sentencesList.get(sentNum);
                                sentenceText = sentence.getText();
                                sentenceCategory = sentence.getAttributeValue("cat");
                                valence = sentence.getAttributeValue("val");
                                activation = sentence.getAttributeValue("act");
                                control = sentence.getAttributeValue("con");
                                colourSentence(output, sentenceText, sentenceCategory, valence, activation, control);
                            }
                            colourParagraphFooter(output);
                        }
                        printFooter(output);
                    }
                    resultsTempFile.delete();
                    tagger.flush();
                } catch (Exception e) {
                    output.println("<p>Un problema ha ocurrido con los archivos temporales!</p>");
                    e.printStackTrace();
                }
            }
        } catch (OutOfMemoryError err) {
            if (!outOfMemory) {
                outOfMemory = true;
                System.gc();
                // Let's try if this is enough to provide the service
                service(req, res);
            } else {
                PrintWriter output = res.getWriter();
                res.setContentType("text/html; charset=UTF-8");
                output.println("<p>El servicio EmoLib está temporalmente " +
                    "fuera de servicio. Por favor, inténtelo de nuevo en unos minutos.</p>");
            }
        }
    }


    /**
     * Method to print the header of the results page.
     *
     * @param output The output stream of the servlet.
     *
     * @throws Exception.
     */
    private void printHeader(PrintWriter output) throws Exception {
        output.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " +
            "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        output.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        output.println("<head>");
        output.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />");
        output.println("<title>Servicio de análisis EmoLib</title>");
        output.println("<meta name=\"keywords\" content=\"\" />");
        output.println("<meta name=\"description\" content=\"\" />");
        output.println("<link href=\"../default.css\" rel=\"stylesheet\" type=\"text/css\" media=\"screen\" />");
        output.println("</head>");
        output.println("<body>");
        output.println("<!-- start header -->");
        output.println("<div id=\"header\">");
        output.println("        <div id=\"logo\">");
        output.println("                <h1><font color=\"steelblue\">Emo</font><font color=" +
            "\"lightsteelblue\">Lib</font></h1>");
        output.println("                <p>Identificación de emociones a partir de texto</p>");
        output.println("        </div>");
        output.println("        <div id=\"menu\">");
        output.println("                <ul>");
        output.println("                        <li class=\"current_page_item\"><a href=\"index.html\">Demo</a></li>");
        output.println("                        <li><a href=\"about.html\">Acerca de</a></li>");
        output.println("                </ul>");
        output.println("        </div>");
        output.println("</div>");
        output.println("<!-- end header -->");
        output.println("<!-- start page -->");
        output.println("<div id=\"page\">");
        output.println("        <!-- start content -->");
        output.println("        <div id=\"content\">");
    }


    /**
     * Method to print the footer of the results page.
     *
     * @param output The output stream of the servlet.
     *
     * @throws Exception.
     */
    private void printFooter(PrintWriter output) throws Exception {
        output.println("        </div>");
        output.println("        <!-- end content -->");
        output.println("</div>");
        output.println("<!-- end page -->");
        output.println("<div style=\"clear: both;\">&nbsp;</div>");
        output.println("<div id=\"footer\">");
        output.println("        <p>Copyright &copy; 2007-2012 Enginyeria i Arquitectura La Salle " +
            "(Universitat Ramon Llull)<br>");
        output.println("        Éste servicio es mantenido por <a href=\"http://www.salle.url.edu/~atrilla/" +
            "\">Alexandre Trilla</a> <a href=\"mailto:atrilla@salle.url.edu\">(atrilla@salle.url.edu)</a></p>");
        output.println("</div>");
        output.println("</body>");
        output.println("</html>");
    }


    /**
     * Header function to colour the paragraph according to its emotional category.
     *
     * @param output The output stream.
     * @param cat The emotional category.
     * @param val The emotional valence.
     * @param act The emotional activation.
     * @param con The emotional control.
     *
     * @return The coloured sentence.
     *
     * @throws Exception.
     */
    private void colourParagraphHeader(PrintWriter output, String cat, String val,
    String act, String con) throws Exception {
        // Sentiment classification
        if (cat.equals("N")) {
            output.println("<div class=\"post redbox\">");
            output.println("        <div class=\"title\" title=\"Valencia: " + val + " Activación: " + act +
                " Control: " + con + "\">");
            output.println("                <h1>Párrafo negativo</h1>");
        } else if (cat.equals("neutral")) {
            output.println("<div class=\"post whitebox\">");
            output.println("        <div class=\"title\" title=\"Valencia: " + val + " Activación: " + act +
                " Control: " + con + "\">");
            output.println("                <h1>Párrafo neutro</h1>");
        } else if (cat.equals("P")) {
            output.println("<div class=\"post bluebox\">");
            output.println("        <div class=\"title\" title=\"Valencia: " + val + " Activación: " + act +
                " Control: " + con + "\">");
            output.println("                <h1>Párrafo positivo</h1>");
        } else if (cat.equals("WARNING")) {
            output.println("<div class=\"post yellowbox\">");
            output.println("        <div class=\"title\" title=\"Valencia: " + val + " Activación: " + act +
                " Control: " + con + "\">");
            output.println("                <h1>ADVERTENCIA</h1>");
        } else {
            output.println("<div class=\"post greenbox\">");
            output.println("        <div class=\"title\">");
            output.println("                <h1>Párrafo mal clasificado</h1>");
        }
        // This is common to all paragraphs.
        output.println("        </div>");
        output.println("        <div class=\"entry\"><p>");
    }


    /**
     * Footer function to colour the paragraph according to its emotional category.
     *
     * @param output The output stream.
     * @param cat The emotional category.
     *
     * @return The coloured sentence.
     *
     * @throws Exception.
     */
    private void colourParagraphFooter(PrintWriter output) throws Exception {

        output.println("        </p></div>");
        output.println("        <div class=\"btm\">");
        output.println("                <div class=\"l\">");
        output.println("                        <div class=\"r\">");
        output.println("                                <p class=\"meta\"></p>");
        output.println("                        </div>");
        output.println("                </div>");
        output.println("        </div>");
        output.println("</div>");
    }


    /**
     * Function to colour the sentence according to its emotional category.
     *
     * @param output The output stream.
     * @param text The text of the sentence.
     * @param cat The emotional category.
     * @param val The emotional valence.
     * @param act The emotional activation.
     * @param con The emotional control.
     *
     * @return The coloured sentence.
     *
     * @throws Exception.
     */
    private void colourSentence(PrintWriter output, String text, String cat, String val,
    String act, String con) throws Exception {
        String colouredText = text;

        if (cat.equals("N")) {
            colouredText = "<FONT color=\"white\" style=\"BACKGROUND-COLOR: orangered\" ";
        } else if (cat.equals("neutral")) {
            colouredText = "<FONT color=\"black\" style=\"BACKGROUND-COLOR: white\" ";
        } else if (cat.equals("P")) {
            colouredText = "<FONT color=\"white\" style=\"BACKGROUND-COLOR: steelblue\" ";
        }
        colouredText = colouredText + "title=\"Valencia: " + val + " Activación: " + act + " Control: " +
            con + "\">" + text + "</FONT>";
        output.println(colouredText);
    }


    /**
     *  Sends a file to the ServletResponse output stream.
     *  Typically you want the browser to receive a different name than the
     *  name the file has been saved in your local database, since
     *  your local names need to be unique.
     *
     *  @param req The req
     *  @param resp The res
     *  @param filename The name of the file you want to download.
     *  @param original_filename The name the browser should receive.
     */
    private void doDownload( HttpServletRequest req, HttpServletResponse res, String filename,
    String original_filename ) throws IOException {
        FileInputStream fileToDownload = new FileInputStream(filename);
        PrintWriter out = res.getWriter();
        res.setContentType("application/xml; charset=UTF-8");
        res.setHeader("Content-Disposition", "attachment; filename=" + original_filename);
        res.setContentLength(fileToDownload.available());
        int c;
        while ((c = fileToDownload.read()) != -1) {
            out.write(c);
        }
        out.flush();
        out.close();
        fileToDownload.close();
    }

}

