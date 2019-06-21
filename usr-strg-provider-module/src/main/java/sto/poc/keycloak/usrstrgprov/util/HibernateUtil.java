package sto.poc.keycloak.usrstrgprov.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import sto.poc.keycloak.usrstrgprov.dao.EmployerRole;
import sto.poc.keycloak.usrstrgprov.dao.MasterEmployerRole;
import sto.poc.keycloak.usrstrgprov.dao.MasterEmployerRolePermission;
import sto.poc.keycloak.usrstrgprov.dao.MasterPermission;
import sto.poc.keycloak.usrstrgprov.dao.MasterUsrCategory;
import sto.poc.keycloak.usrstrgprov.dao.User;

public class HibernateUtil {
	
	private static SessionFactory factoryHiberSession;

	private HibernateUtil() {}
	
	public static SessionFactory getSessionFactory(String strJDBC, String dbUsr, String dbPass) {
		try {
	    	if(factoryHiberSession == null) {
	    		synchronized (HibernateUtil.class){
	    			if(factoryHiberSession == null) {
	    				//TODO :: Pass necessary Hibernate configuration, will need to implement a connection pool
	    				Configuration conf = new Configuration()
	    										.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect")
	    										.setProperty("hibernate.connection.url", strJDBC)
	    										.setProperty("hibernate.connection.username", dbUsr)
	    										.setProperty("hibernate.connection.password", dbPass)
	    										.setProperty("hibernate.c3p0.min_size", "10")
	    										.setProperty("hibernate.c3p0.max_size", "30")
	    										.setProperty("hibernate.c3p0.timeout", "1800")
	    										.setProperty("hibernate.c3p0.max_statements", "75")
	    										.setProperty("hibernate.show_sql", "true")
	    										.addAnnotatedClass(User.class)
	    										.addAnnotatedClass(MasterUsrCategory.class)
	    										.addAnnotatedClass(EmployerRole.class)
	    										.addAnnotatedClass(MasterEmployerRole.class)
	    										.addAnnotatedClass(MasterEmployerRolePermission.class)
	    										.addAnnotatedClass(MasterPermission.class);

	    				factoryHiberSession = conf.buildSessionFactory();
	    			}
	    		}
	    	}
		}catch(Exception e) {
			throw new RuntimeException("Exception occurred when creating HibernateUtil singleton instance");
		}
		return factoryHiberSession;
	}

	public static synchronized Session getSession() {
		return factoryHiberSession.openSession();
	}

}
