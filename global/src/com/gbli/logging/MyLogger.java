package com.gbli.logging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.gbli.context.ContextManager;

/**
 * MyLogger
 * 
 * This handles all aspects of system logging.
 * Currently.. Everything is hard coded
 * We will need a resource/properties file to allow for on the fly configuration
 * 
 * @author dbigelow
 *
 */
public class MyLogger {
	
	
  static private FileHandler m_fileTxt;
  static private SimpleFormatter m_formatterTxt;

  static private FileHandler m_fileHTML;
  static private Formatter m_formatterHTML;
  
  
  private final static Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
  
  static public void setup() throws IOException {
    
    LOGGER.setLevel(Level.FINE);
  
    SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-ddHHmmss");
    String sTimeStamp = date_format.format(new Date());
    System.out.println(sTimeStamp);
//    m_fileTxt = new FileHandler(ContextManager.getRealPath() + "c:/temp/logs/Logging." + sTimeStamp + ".txt");
//    m_fileHTML = new FileHandler(ContextManager.getRealPath() + "c:temp/logs/Logging." + sTimeStamp + ".html");
    m_fileTxt = new FileHandler("c:/temp/logs/Logging." + sTimeStamp + ".txt");
    m_fileHTML = new FileHandler("c:/temp/logs/Logging." + sTimeStamp + ".html");

    
   // m_fileTxt = new FileHandler("/Logging.txt");
   // m_fileHTML = new FileHandler("/Logging.html");

    // Create text Formatter
    m_formatterTxt = new MyTxtFormatter();
    m_fileTxt.setFormatter(m_formatterTxt);
    LOGGER.addHandler(m_fileTxt);

    // Create HTML Formatter
    m_formatterHTML = new MyHtmlFormatter();
    m_fileHTML.setFormatter(m_formatterHTML);
    LOGGER.addHandler(m_fileHTML);
    
    LOGGER.info("Logger set to: " + Level.FINEST);
    
  }
  
 } 

