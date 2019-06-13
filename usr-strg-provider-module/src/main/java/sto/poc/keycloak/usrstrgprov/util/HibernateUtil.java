package sto.poc.keycloak.usrstrgprov.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import sto.poc.keycloak.usrstrgprov.dao.User;

public class HibernateUtil {
	
	private static SessionFactory factoryHiberSession;

	private HibernateUtil() {}
	
	public static synchronized void createSessionFactory(String strJDBC, String dbUsr, String dbPass) {
		System.out.println("\ncreateSessionFactory()\n");
		
		if(factoryHiberSession==null) {
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
									.addAnnotatedClass(User.class);
			
			factoryHiberSession = conf.buildSessionFactory();
		}
	}

	public static synchronized Session getSession() {
		System.out.println("\ngetSession()\n");
		
		return factoryHiberSession.openSession();
	}

	public static synchronized boolean checkSessionFactory() {
		System.out.println("\ncheckSessionFactory()\n");
		return (factoryHiberSession==null)?false:true;
	}
}
