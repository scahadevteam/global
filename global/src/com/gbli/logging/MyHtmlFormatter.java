package com.gbli.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

//This custom formatter formats parts of a log record to a single line
class MyHtmlFormatter extends Formatter {
	
	private static String NEW_LINE = System.getProperty("line.separator");
	  
  // This method is called for every log records
  @Override
public String format(LogRecord rec)
  {
    StringBuffer buf = new StringBuffer(1000);
    // Bold any levels >= WARNING
    buf.append("<tr>");
    buf.append("<td>");

    if (rec.getLevel().intValue() >= Level.WARNING.intValue())
    {
      buf.append("<b>");
      buf.append(rec.getLevel());
      buf.append("</b>");
    } else
    {
      buf.append(rec.getLevel());
    }
    buf.append("</td>");
    buf.append("<td>");
    buf.append(calcDate(rec.getMillis()));
    buf.append(' ');
    buf.append("</td>");
    buf.append("<td>");
//    buf.append(formatMessage(rec));
//    buf.append(rec.getSourceClassName() + ":");
//    buf.append(rec.getSourceMethodName() + ":");
    buf.append(rec.getMessage());
    buf.append(NEW_LINE);
    buf.append("</td>");
    buf.append("</tr>\n");
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
    return "<HTML>\n<HEAD>\n" + (calcDate((new Date()).getTime()) + ":Open For Business..." + NEW_LINE) 
          + "</HEAD>\n<BODY>\n<PRE>" + NEW_LINE
          + "<table width=\"100%\" border>" + NEW_LINE
          + "<tr><th>Level</th>" +
          "<th>Time</th>" +
          "<th>Log Message</th>" +
          "</tr>" + NEW_LINE;
  }
    
  // This method is called just after the handler using this
  // formatter is closed
  @Override
public String getTail(Handler h)
  {
    return "</table>" + calcDate((new Date()).getTime()) + ":Closed For Business..." + NEW_LINE + "</PRE></BODY>" + NEW_LINE + "</HTML>" + NEW_LINE;
  }
} 