/*
 * 1.0.0		20190620		shirhan				Lazy initialized thread safe singleton class to handle connections to LDAP server.
 * 													ApacheDS (Apache Directory Server) is used as LDAP server and it's libraries are used
 * 													to create the connection pool. Will need to change according to the LDAP Server later on.
 * */
package sto.poc.keycloak.usrstrgprov.util;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.DefaultLdapConnectionFactory;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.ValidatingPoolableLdapConnectionFactory;

public class LDAPUtil {

	private static LdapConnectionPool ldapConPool;
	private static LDAPUtil ldapUtil;

	private LDAPUtil() {}

	//Thread safty is applied using synchronized, double checked locking is used.
    public static LDAPUtil getLDAPInstance( String ldapHost, 
    											int ldapPort,
    												String ldapUser,
    													String ldapPass,
    														long ldapConTimeout ){
		try {
	    	if(ldapUtil == null) {
	    		synchronized (LDAPUtil.class){
	    			if(ldapUtil == null) {
	    				//TODO :: Depending on the LDAP server MAY or MAY NOT need to change the helper libraries used here.
	    				//LDAP Server Configuration
	    				LdapConnectionConfig ldapConfig = new LdapConnectionConfig();
	    				ldapConfig.setLdapHost(ldapHost);
	    				ldapConfig.setLdapPort(ldapPort);
	    				ldapConfig.setName(ldapUser);
	    				ldapConfig.setCredentials(ldapPass);
	    				
	    				DefaultLdapConnectionFactory conFactory = new DefaultLdapConnectionFactory(ldapConfig);
	    				
	    				conFactory.setTimeOut(ldapConTimeout);
	    				
	    				//Connection pool configuration
	    				GenericObjectPoolConfig<LdapConnection> poolConfig = new GenericObjectPoolConfig<>();
	    				poolConfig.setLifo(true);
	    				poolConfig.setMaxTotal(15);
	    				poolConfig.setMaxIdle(8);
	    				poolConfig.setMaxWaitMillis(-1L);
	    				poolConfig.setMinEvictableIdleTimeMillis(1000L*60L*30L);
	    				poolConfig.setMinIdle(0);
	    				poolConfig.setNumTestsPerEvictionRun(4);
	    				poolConfig.setSoftMinEvictableIdleTimeMillis(-1L);
	    				poolConfig.setTestOnBorrow(false);
	    				poolConfig.setTestOnReturn(false);
	    				poolConfig.setTestWhileIdle(false);
	    				poolConfig.setTimeBetweenEvictionRunsMillis(-1L);
	    				poolConfig.setBlockWhenExhausted(true);
	    				
	    				ldapConPool = new LdapConnectionPool( new ValidatingPoolableLdapConnectionFactory(conFactory), poolConfig);
	    				ldapUtil = new LDAPUtil();
	    			}
	    		}
	    	}
		}catch(Exception e) {
			throw new RuntimeException("Exception occurred when creating LDAPUtil singleton instance");
		}
		return ldapUtil;
    }
    
    public static synchronized LdapConnection getConnection() {
    	try {
			return ldapConPool.getConnection();
		} catch (LdapException e) {
			e.printStackTrace();
			throw new RuntimeException("Exception occurred when acquiring LDAP connection");
		}
    }
}
