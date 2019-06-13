/*
 * 1.0.0			20190606			shirhan@inovaitsys.com			An instance of this class is created for each 
 * 																		Txn.
 * */
package sto.poc.keycloak.usrstrgprov;

import org.hibernate.Session;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import sto.poc.keycloak.usrstrgprov.dao.User;
import sto.poc.keycloak.usrstrgprov.dao.UserAdapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

public class CustomUserStorageProvider implements 
	UserStorageProvider,
	UserLookupProvider,
	UserQueryProvider,
	CredentialInputUpdater,
	CredentialInputValidator {

	private final KeycloakSession session;
	private final ComponentModel model;
	private Session hibernateSession;

	public CustomUserStorageProvider(KeycloakSession session, ComponentModel model, Session hibernateSession) {
		super();
		this.session = session;
		this.model = model;
		this.hibernateSession = hibernateSession;
	}

	//Note :: Once a Txn is completed this method is called and the object provider object is garbage collected
	public void close() {
		System.out.println("\nCustomUserStorageProvider::close()\n");
		this.hibernateSession.close();
	}

	public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
		System.out.println("\nCustomUserStorageProvider::isConfiguredFor\n");
		return false;
	}
	
	//Validating the user credentials against the username and password provided
	public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
	    if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
	    	return false;
	    }

	    UserCredentialModel cred = (UserCredentialModel) input;

		Query query = hibernateSession.createQuery("FROM User WHERE username=:username AND password=:password");
		query.setParameter("username", user.getUsername());
		query.setParameter("password", cred.getValue());
		List<User> result = query.getResultList();
		hibernateSession.clear();
		
		return((result!=null)&&(result.size()>0))?true:false;

	}
	
	//Note :: After credentials are provided in the Keycloak login screen first the user is queried from db then 
	//			this method is called to check the password. After this the isValid method is invoked.
	public boolean supportsCredentialType(String credentialType) {
		return CredentialModel.PASSWORD.equals(credentialType);
	}

	public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
		System.out.println("\nCustomUserStorageProvider::updateCredential\n");
		return false;
	}

	public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
		System.out.println("\nCustomUserStorageProvider::disableCredentialType\n");
	}

	public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
		System.out.println("\nCustomUserStorageProvider::getDisableableCredentialTypes\n");
		return null;
	}

	public int getUsersCount(RealmModel realm) {
		System.out.println("\nCustomUserStorageProvider::getUsersCount\n");
		return 0;
	}

	public List<UserModel> getUsers(RealmModel realm) {
		System.out.println("\nCustomUserStorageProvider::getUsers1\n");
		return null;
	}

	public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
		System.out.println("\nCustomUserStorageProvider::getUsers2\n");
		return null;
	}

	public List<UserModel> searchForUser(String search, RealmModel realm) {
		System.out.println("\nCustomUserStorageProvider::searchForUser1\n");
		return null;
	}

	public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
		System.out.println("\nCustomUserStorageProvider::searchForUser2\n");
		return null;
	}

	public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
		System.out.println("\nCustomUserStorageProvider::searchForUser3\n");
		return null;
	}
	
	//TODO :: Need to implement this
	//Note :: This method is called when the clicked on "View All Users" button in the Keycloak Admin panel
	public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult,
			int maxResults) {
		System.out.println("\nCustomUserStorageProvider::searchForUser4\n");
		
		hibernateSession.beginTransaction();
		Query query = hibernateSession.createQuery("FROM User");
		List<User> result = query.getResultList();

		if((result!=null) && (result.size()>0)) {
			//return new UserAdapter(session, realm, model, result);
			return null;
		} else {
			return null;
		}

	}

	public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
		System.out.println("\nCustomUserStorageProvider::getGroupMembers1\n");
		return null;
	}

	public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
		System.out.println("\nCustomUserStorageProvider::getGroupMembers2\n");
		return null;
	}

	public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
		System.out.println("\nCustomUserStorageProvider::searchForUserByUserAttribute\n");
		return null;
	}
	
	//Note :: After user is authenticated and isValid method is called this method is called.
	public UserModel getUserById(String id, RealmModel realm) {
	    
		String externalId = StorageId.externalId(id);
	    Query query = hibernateSession.createQuery("FROM User WHERE id = :id");
	    query.setParameter("id", Integer.parseInt(externalId));
		List<User> result = query.getResultList();
		hibernateSession.clear();
	    
	    return new UserAdapter(session, realm, model, result.get(0));
	}
	
	//Note :: This method gets invoked when Keycloak login screen is visible. The parameters captured from the login screen
	//		  If username is found then supportsCredentialType method is invoked next.
	public UserModel getUserByUsername(String username, RealmModel realm) {

		Query query = hibernateSession.createQuery("FROM User WHERE username = :username");
		query.setParameter("username", username);
		List<User> result = query.getResultList();
		hibernateSession.clear();
		
		if((result!=null) && (result.size()>0)) {
			return new UserAdapter(session, realm, model, result.get(0));
		} else {
			return null;
		}
	}

	public UserModel getUserByEmail(String email, RealmModel realm) {
		System.out.println("\nCustomUserStorageProvider::getUserByEmail\n");
		return null;
	}

}
