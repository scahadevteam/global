/**
 * 
 */
package com.gbli.connectors;

import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

/**
 * @author dbigelow
 *
 */
public class DatabasePool implements Runnable {
	
	private final static Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private Object c_olock = 0;
	
	// The number of database connections in the pool
	private int m_iCount =5;

	// not available when starting up, flag for shutting down.
	private boolean m_bavail = false;
	private boolean m_bshutdown = false;
	private String m_sName = null;
	private Vector<Database> m_vConnections = new Vector<Database>();
	
	private Thread m_thMyThread = null;
	
	public DatabasePool (String _str) {

		//
		// Lets set up all the information here
		//
	
		synchronized (c_olock) {
			
			m_sName = _str;
			
			if (_str.equals(GDatabase.class.getSimpleName())) {
				for (int i=0; i < m_iCount;i++) {
					m_vConnections.add(new GDatabase(i,
							"com.mysql.jdbc.Driver",
							"jdbc:mysql://localhost:3306/gbli",
							"root", "shiloh24"));
				}
				
			}
			
		}
		
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

		//
		// Initialize the pool
		
		// Sleep and wake up to report on Pool

		
		while (!m_bshutdown) {
			
			for (int i=0; i < m_iCount;i++) {
				LOGGER.info(this.m_vConnections.get(i).toString());
			}
			
			try {
				Thread.sleep(60000);
			} catch (InterruptedException  ex) {
				LOGGER.info("Database Pool Interrupt on Thread Detected..");
				//ex.printStackTrace();
			}
		}
		
		LOGGER.info("Database Pool Shudown Request Detected..");
		
		//
		// Lets close out the connections..
		//
		for (int i=0; i < m_iCount;i++) {
			Database db = m_vConnections.get(i);
			db.close();
			LOGGER.info(this.m_vConnections.get(i).toString() + ":" + "Closing out connection..");
		}
	
		

		//
		//  gracefull shutdown.
		//
		
	}
	
	/**
	 * Provide a reference to my own thread..
	 */

	public void setMyThread(Thread _th) {
		this.m_thMyThread = _th;
	}

	/**
	 * What is the Thread my watch dog is running under?
	 * @return
	 */
	public Thread getMyThread() {
		return m_thMyThread;
	}
	
	/**
	 * This will set the flag for a shutdown request...
	 */
	public void shutdown() {
		
		this.m_bshutdown = true;
		
	}

	/**
	 * Here we search for the next available Database Connection
	 * Mark it as inuse
	 * And hand it back
	 * 
	 * The caller is responsible for freeing releasing the database back to the pool.
	 * 
	 * @return
	 */
	public Database getDatabase() {
		int icount = 0;
		Database db = null;
		while (db == null && icount < 10) {
			for (int i=0; i < this.m_iCount;i++) {
				db = this.m_vConnections.get(i);
				if (!db.isInUse()) {
					db.checkHeath();
					db.setInUse();
					return db;
				}
			}
			icount++;
			try {
            //thread to sleep for the specified number of milliseconds
             Thread.sleep(1000);
            } catch ( java.lang.InterruptedException ie) {
                ie.printStackTrace();
            };
		}
		return null;
	}

	
}
