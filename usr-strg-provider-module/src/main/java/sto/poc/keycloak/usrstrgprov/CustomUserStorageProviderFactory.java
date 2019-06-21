/*
 * 1.0.0		20190606		shirhan			Creation of custom provider via provider factory
 *                                              https://www.keycloak.org/docs/3.1/server_development/topics/user-storage.html
 */
package sto.poc.keycloak.usrstrgprov;

import java.util.List;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.hibernate.Session;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import sto.poc.keycloak.usrstrgprov.util.HibernateUtil;
import sto.poc.keycloak.usrstrgprov.util.LDAPUtil;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {

	@Override
	public void init(Config.Scope config) {
		//This configuration is pulled from the SPI configuration of this provider in the standalone[-ha] / domain.xml
		//see setup.cli
		//String someProperty = config.get("someProperty");
		//log.infov("Configured {0} with someProperty: {1}", this, someProperty);
	}
	
	//Note :: This method is responsible for creating the instance of the customized provider class
	public CustomUserStorageProvider create(KeycloakSession session, ComponentModel model) {
		//Here you can setup the user storage provider, initiate some connections, etc.
		
		//ComponentModel - is a representation of how the provider was enabled and configured in a specific realm
		HibernateUtil.getSessionFactory(model.get("mySQLConnectionString"), 
											model.get("mySQLUser"),
												model.get("mySQLPassword"));
		Session hibernateSession = HibernateUtil.getSession();
		
		//LDAP Connection
		LDAPUtil.getLDAPInstance( model.get("ldapHost"),
									Integer.parseInt(model.get("ldapPort")),
										model.get("ldapUser"),
											model.get("ldapPassword"),
												Long.parseLong(model.get("ldapConTimeout")));
		
		LdapConnection ldapConnection = LDAPUtil.getConnection();
		
		return new CustomUserStorageProvider(session, model, hibernateSession, ldapConnection);
	}
	
	//Note :: This is the display name in the user federation dropdown in keycloak portal
	public String getId() {
		return "custom-jdbc-user-provider";
	}

	//Note :: configuration is configurable in the keycloak admin-console. If additional configuration need to be provided 
	//		  in a customizable way then need to add more of these, yo!
	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		
		return ProviderConfigurationBuilder.create()
											.property()
											.name("mySQLConnectionString")
											.label("MySQL Connection String")
											.helpText("The MySQL jdbc connection string should be entered here. Eg: jdbc:mysql://localhost:3306/<dbname>")
											.type(ProviderConfigProperty.STRING_TYPE)
											.defaultValue("jdbc:mysql://localhost:3306/<dbname>")
											.add()
											.property()
											.name("mySQLUser")
											.label("MySQL User")
											.helpText("Username that has access to specified databse")
											.type(ProviderConfigProperty.STRING_TYPE)
											.defaultValue("root")
											.add()
											.property()
											.name("mySQLPassword")
											.label("MySQL Password")
											.helpText("Password that has access to specified databse")
											.type(ProviderConfigProperty.PASSWORD)
											.defaultValue("root")
											.add()
											.property()
											.name("ldapHost")
											.label("LDAP Host Server")
											.helpText("The LDAP server that needs to be connected")
											.type(ProviderConfigProperty.STRING_TYPE)
											.defaultValue("localhost")
											.add()
											.property()
											.name("ldapPort")
											.label("LDAP Host Server Port")
											.helpText("The LDAP server port that needs to be connected")
											.type(ProviderConfigProperty.STRING_TYPE)
											.defaultValue("10389")
											.add()
											.property()
											.name("ldapUser")
											.label("LDAP Username")
											.helpText("The LDAP server user name")
											.type(ProviderConfigProperty.STRING_TYPE)
											.defaultValue("uid=admin,ou=system")
											.add()
											.property()
											.name("ldapPassword")
											.label("LDAP Password")
											.helpText("The LDAP server user password")
											.type(ProviderConfigProperty.PASSWORD)
											.defaultValue("secret")
											.add()
											.property()
											.name("ldapConTimeout")
											.label("LDAP Connection Timeout")
											.helpText("The LDAP server connection timeout")
											.type(ProviderConfigProperty.STRING_TYPE)
											.defaultValue("120000")
											.add()
											.build();
		
	}
	
}
