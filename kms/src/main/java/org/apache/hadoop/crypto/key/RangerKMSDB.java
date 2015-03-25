package org.apache.hadoop.crypto.key;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;
import org.apache.ranger.kms.dao.DaoManager;

public class RangerKMSDB {
	
	static final Logger logger = Logger.getLogger(RangerKMSDB.class);
	
	private EntityManagerFactory entityManagerFactory;
	private DaoManager daoManager;
	
	private static Map<String, String> DB_PROPERTIES = null;
	
	private static final String PROPERTY_PREFIX = "ranger.db.ks.";
	private static final String DB_DIALECT = "javax.persistence.jdbc.dialect";
	private static final String DB_DRIVER = "javax.persistence.jdbc.driver";
	private static final String DB_URL = "javax.persistence.jdbc.url";
	private static final String DB_USER = "javax.persistence.jdbc.user";
	private static final String DB_PASSWORD = "javax.persistence.jdbc.password";
	
	private final Configuration conf;
	
	public RangerKMSDB(){
		conf = new Configuration();
	}
	
	public RangerKMSDB(Configuration conf){		
		this.conf = conf;		
		initDBConnectivity();
	}
	
	public DaoManager getDaoManager(){
		return daoManager;
	}

	private void initDBConnectivity(){
		try {
			DB_PROPERTIES = new HashMap<String, String>();
			DB_PROPERTIES.put(DB_DIALECT, conf.get(PROPERTY_PREFIX+DB_DIALECT));
			DB_PROPERTIES.put(DB_DRIVER, conf.get(PROPERTY_PREFIX+DB_DRIVER));
			DB_PROPERTIES.put(DB_URL, conf.get(PROPERTY_PREFIX+DB_URL));
			DB_PROPERTIES.put(DB_USER, conf.get(PROPERTY_PREFIX+DB_USER));
			DB_PROPERTIES.put(DB_PASSWORD, conf.get(PROPERTY_PREFIX+DB_PASSWORD));
				
			entityManagerFactory = Persistence.createEntityManagerFactory("persistence_ranger_server", DB_PROPERTIES);

	   	    daoManager = new DaoManager();
	   	    daoManager.setEntityManagerFactory(entityManagerFactory);

	   	    daoManager.getEntityManager(); // this forces the connection to be made to DB
	   	    logger.info("Connected to DB : "+isDbConnected());	   	    
		} catch(Exception excp) {
			excp.printStackTrace();
		}
	}
	
	private boolean isDbConnected() {
		EntityManager em = getEntityManager();
		
		return em != null && em.isOpen();
	}
	
	private EntityManager getEntityManager() {
		DaoManager daoMgr = daoManager;

		if(daoMgr != null) {
			try {
				return daoMgr.getEntityManager();
			} catch(Exception excp) {
				excp.printStackTrace();
			}
		}

		return null;
	}
}
