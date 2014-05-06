package com.gbli.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

//This custom formatter formats parts of a log record to a single line
class MyTxtFormatter extends SimpleFormatter  {
	
  private static String NEW_LINE = System.getProperty("line.separator");
  
  // This method is called for every log records
  @Override
public String format(LogRecord rec) {
    StringBuffer buf = new StringBuffer(1000);
    buf.append(calcDate(rec.getMillis()));
    buf.append(":");
    buf.append(rec.getLevel());
    buf.append(":");
    buf.append(rec.getSourceClassName() + ":>");
   // buf.append(rec.getSourceMethodName() + ":");
    buf.append(rec.getMessage());
    buf.append(NEW_LINE);
    return buf.toString(); 
  }

  private String calcDate(long millisecs)
  {
    SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    Date resultdate = new Date(millisecs);
    return date_format.format(resultdate);
  }

  // This method is called just after the handler using this
  // formatter is created
  @Override
public String getHead(Handler h) {
    return calcDate((new Date()).getTime()) + ": Open For Business..." + NEW_LINE;
    
  }
    
  // This method is called just after the handler using this
  // formatter is closed
  @Override
public String getTail(Handler h)
  {
    return calcDate((new Date()).getTime()) + ":Closed For Business...\n";
  }
} 