/**
 *  This is a basic Databases Connection Object
 *  Based upon how it is Instantiated  It will connect to a database.
 *  
 */
package com.gbli.connectors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

/**
 * @author dbigelow
 *
 */
public class Database {
	
	private final static Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());


	
    // In Use Boolean
    private boolean m_binuse = false;
    private String m_sName = "Database";
    private int m_iId = 0;

	private String m_sDriver = null;		// The Driver for the connection
	private String m_sURL = null;   		// The Connection URL..  "jdbc:sqlserver://airsk-sql-01:1433;databaseName=MEdat2011;"
	private String m_sUser = null;   		// The User Name to use in the connect
	private String m_sPwd = null;			// The Password for the connection

	// Declare the JDBC objects.
    private Connection m_con = null;
    private Statement m_stmt = null;
    private PreparedStatement m_pstmt = null;
    private ResultSet m_rs = null;
    private ResultSetMetaData m_rsmd = null;
    
    
    /**
     * Cannot simply instansiate this w/o parms
     */
    private Database() {
    	
    }
     
    public Database (int _iId, String _sDriver, String _sURL, String _sUser, String _sPwd) {
    	
    	
    	m_sName = this.getClass().getSimpleName();
    	m_iId = _iId;
    	
    	// Establish the connection.
		m_sDriver = _sDriver;
		m_sURL = _sURL;
		m_sUser = _sUser;
		m_sPwd = _sPwd;
		
		this.primeConnection();



    }
    
    public Database (String _sDriver, String _sURL, String _sUser, String _sPwd) {
    	
    		// Establish the connection.
    		m_sDriver = _sDriver;
    		m_sURL = _sURL;
    		m_sUser = _sUser;
    		m_sPwd = _sPwd;
    		
    		this.primeConnection();
    
    }
    
    /**
     * This returns the connection for the DataBase Object
     * 
     * @return
     */
    public final Connection getConnection() {
    	return m_con;
    }

    /**
     * This sets the Database Object as inUse
     */
    public void busy () {
    	m_binuse = true;
    }
    
    
    public void free () {
    	
    	
    	try {
    		m_rsmd = null;
    		if (m_rs != null)  {
    			m_rs.close();
    			m_rs = null;
    		}
    		if (m_stmt != null) {
    			m_stmt.close();
    		}
    		if (m_pstmt != null) {
    			m_pstmt.close();
    		}
    	} catch (SQLException ex) {
    		ex.printStackTrace();
    		LOGGER.info(this +  "DB Free SNAFU");
    	}
    	LOGGER.info(this +  " Freeing Up Connection.");
    	m_binuse = false;
    	
    }
    
 public void cleanup() {
    	
    	LOGGER.finest(this +  "Clean Up Connection:" + this.m_iId);

    	try {
    		m_rsmd = null;
    		if (m_rs != null)  {
    			m_rs.close();
    			m_rs = null;
    		}
    		if (m_stmt != null) {
    			m_stmt.close();
    		}
    		if (m_pstmt != null) {
    			m_pstmt.close();
    		}
    	} catch (SQLException ex) {
    		ex.printStackTrace();
    		LOGGER.info(this +  "Clean up SNAFU");
    	}

    }
    
    public void close() {
    	
    	LOGGER.finest(this +  "Close Out Connection:" + this.m_iId);

    	this.free();
    	try {
    		m_con.close();
       	} catch (SQLException ex) {
    		ex.printStackTrace();
    		LOGGER.info(this +  "DB Close SNAFU");
    	}
   	
    }

    /**
     * This is a method that assumes no variables or Parms that need to be set from the SQL.
     * @param _sPath
     * @return
     */
    public boolean getDataFromSQLFile(String _sPath) {
    	return this.getDataFromSQLFile(_sPath, null);
    }
    /**
     * getDataFromSQLFile
     * 
     * This method assumes you provide the file name as an absolute file path.. since this method needs to be context 
     * independent.
     * 
     * THis will populate the result set and its meta data counterpart for the caller to retrieve and do what they want
     * 
     * 
     * @param _str
     * @return
     */
    public boolean getDataFromSQLFile(String _sPath, Vector _vParms) {

    	String sAbsolutePath = ContextManager.getRealPath() + _sPath;
		LOGGER.info(this + ":" + sAbsolutePath);

		StringBuffer sb = new StringBuffer();
		
    	try (BufferedReader br = new BufferedReader(new FileReader(sAbsolutePath))) {
    		String sCurrentLine;
    		while ((sCurrentLine = br.readLine()) != null) {
    			sb.append(sCurrentLine);
    		}
    		br.close();
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} 
    	
		return getData(sb.toString(), _vParms);
    }
		

    /**
     * 
     * @param _sSQL
     * @return
     */
    public boolean getData(String _sSQL) {
    	return this.getData(_sSQL, null);
    }
    
    
    /**
     * This retrieves all data into the ResultSet and the ResultSetMetaData for the given SQL Statement
     * @param _strSQL
     * @return
     */
    public boolean getData(String _sSQL, Vector _vParms) {

    	try {
    		//
    		// If there are parms.. we need to set up the SQL statement abit better
    		//
    		if (_vParms != null) {
    			
    			//
    			// We set based upon the type of data here...
    			//
    			m_pstmt = m_con.prepareStatement(_sSQL);
    			for (int i = 0; i < _vParms.size(); i++) {
    				   m_pstmt.setObject(i+1,_vParms.elementAt(i));
    			}

    			//
    			// OK.. lets execute away
    			m_rs = m_pstmt.executeQuery();
    			// m_pstmt.closeOnCompletion();
    			
    		} else {
    		
    	   		m_stmt = m_con.createStatement();
    			//
    			// Here there are no parameters present.. 
    			// So we will simply create and execute..
    	   		m_rs = m_stmt.executeQuery(_sSQL);
    			// m_pstmt.closeOnCompletion();

    		}
	   		m_rsmd = m_rs.getMetaData();
    		return true;
    	} catch (SQLException ex) {
    		ex.printStackTrace();
    	}
    	
    	return false;
    	
    }
    
    public ResultSet getResultSet() {
    	return this.m_rs;
    }

    public ResultSetMetaData getResultSetMetaData() {
    	return this.m_rsmd;
    }
    
    public Statement createStatement() throws SQLException {
    	this.m_stmt =  m_con.createStatement();
    	return m_stmt;
    }
    public Statement getStatement() {
    	return this.m_stmt;
    }
    
    public void commit() throws SQLException {
    	if (!this.m_con.getAutoCommit())this.m_con.commit();
    	if (this.m_stmt != null) {
    		this.m_stmt.close();
    		this.m_stmt = null;
    	}
    	if (this.m_rs != null) {
    		this.m_rs.close();
    		this.m_rs = null;
    	}
    }
    
    
    /**
     * This will clean out the connection and reset it.. 
     * 
     */
    public void clean() {
    	
    	LOGGER.info(this + "Cleaning and Reseting Connection...");
    	this.close();
    	this.primeConnection();
    	
    }


	private void primeConnection() {

		try {
			
    		LOGGER.info(this + ":DRIVER:" + m_sDriver + ".  Starting to instanciate...");
    		Class.forName(m_sDriver).newInstance();
    		LOGGER.info(this + ":DRIVER:" + m_sDriver + ".  Driver okay...");
    		m_con = DriverManager.getConnection(m_sURL,m_sUser,m_sPwd);
    		LOGGER.info(this + "Connection okay..");
    		
    	
    	}	catch (Exception e) {
    			e.printStackTrace();
    	}
	}
	
	public boolean isInUse() {
		return this.m_binuse;
	}
	
	public void setInUse() {
		LOGGER.info(this + " is being placed in use");
		m_binuse = true;
	}
	
	public void checkHeath() {
		try {
			if (m_con.isClosed()) {
				this.primeConnection();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "DB(" + m_sName + ")[" + m_iId + "]:" + (m_binuse ? "busy" : "free") + ":";
	}
	
	
}

