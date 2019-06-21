/*
 * 1.0.0			20190606			shirhan			An instance of this class is created for each Txn.
 * 
 * */
package sto.poc.keycloak.usrstrgprov;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
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
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import sto.poc.keycloak.usrstrgprov.dao.MasterEmployerRolePermission;
import sto.poc.keycloak.usrstrgprov.dao.MasterPermission;
import sto.poc.keycloak.usrstrgprov.dao.User;
import sto.poc.keycloak.usrstrgprov.dao.UserAdapter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Query;

public class CustomUserStorageProvider implements 
	UserStorageProvider,
	UserLookupProvider,
	UserQueryProvider,
	CredentialInputUpdater,
	CredentialInputValidator{

	private final KeycloakSession session;
	private final ComponentModel model;
	private Session hibernateSession;
	private LdapConnection ldapConnection;
	
	public CustomUserStorageProvider(KeycloakSession session,
										ComponentModel model,
											Session hibernateSession,
												LdapConnection ldapConnection) {
		super();
		this.session = session;
		this.model = model;
		this.hibernateSession = hibernateSession;
		this.ldapConnection = ldapConnection;
	}

	//Note :: Once a Txn is completed this method is called and the object provider object is garbage collected
	public void close() {
		System.out.println("Closing hibernate session and LDAP connections..");
		try {
			this.hibernateSession.close();
			this.ldapConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error closing hibernate session and LDAP connections..");
		}
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
	    
	    List<User> result = null;
	    boolean isLDAPAuth = false;
	    boolean isValid = false;
	    
	    UserCredentialModel cred = (UserCredentialModel) input;
	    
	    //1) DB user name is available in the DB at this stage, checks if the password is empty in the DB then, need to go through LDAP
    	Query query = hibernateSession.createQuery("FROM User WHERE username=:username AND password=''");
		query.setParameter("username", user.getUsername());
		result = query.getResultList();
	    if((result!=null)&&(result.size()>0)) {
	    	isLDAPAuth=true;
	    }
	    
	    if(isLDAPAuth) {
	    	//System.out.println("User password is empty! making request to LDAP");
	    	try {
		    	SearchRequestImpl userSearch = new SearchRequestImpl();
		    	userSearch.setScope(SearchScope.SUBTREE);
		    	userSearch.addAttributes("*");
		    	userSearch.setTimeLimit(0);
				userSearch.setBase(new Dn("ou=Employees,dc=teafactory"));	//TODO:: Find if there is a way to go through all dc's
				userSearch.setFilter("(uid="+user.getUsername()+")");

				if(this.ldapConnection.isConnected()) {
					SearchCursor searchCursor = this.ldapConnection.search(userSearch);
					
					while(searchCursor.next()) {
						Response ldapResponse = searchCursor.get();
						//TODO :: Match the provided password here, need to get the hashing method of the existing LDAP to match
						//		  passwords.
						if(ldapResponse instanceof SearchResultEntry) {
							Entry resultEntry = ((SearchResultEntry)ldapResponse).getEntry();
							//System.out.println("LDAP Entry :"+resultEntry);
							Attribute attrib = resultEntry.get("userPassword");
							//ApacheDS appends the algo infront of the hash, removing using below RegEx
							String strPass = attrib.getString().replaceAll("(\\{(\\w*)\\})|(\\{(\\w*\\-\\w*)\\})","");
							System.out.println("Hash ->"+strPass);

					        //Calculate hash value
					        MessageDigest md = MessageDigest.getInstance("SHA512");
					        md.update(cred.getValue().getBytes());
					        String hash = Base64.getEncoder().encodeToString(md.digest()); 
					        
					        isLDAPAuth=hash.equals(strPass);
					        
						}else {
							isLDAPAuth=false;
						}
					}
				}else {
					//Not connected to LDAP server
					isLDAPAuth=false;
				}

	    	} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				isLDAPAuth=false;
			} catch (CursorException e) {
				e.printStackTrace();
				isLDAPAuth=false;
			} catch (LdapInvalidDnException e) {
				e.printStackTrace();
				isLDAPAuth=false;
	    	} catch (LdapException e) {
	    		e.printStackTrace();
	    		isLDAPAuth=false;
	    	}
	    	
	    	isValid=isLDAPAuth;

	    }else{
	    	
	    	//System.out.println("User password is not empty! making request to DB");
	    	query = hibernateSession.createQuery("FROM User WHERE username=:username AND password=:password");
			query.setParameter("username", user.getUsername());
			//TODO :: Need to use the BCrypt encoded value here
			query.setParameter("password", cred.getValue());
			result = query.getResultList();
			hibernateSession.clear();
			
			isValid=((result!=null)&&(result.size()>0));
	    }
	    
	    //3) Send OTP to user mobile while saving it in custom DB
	    if(isValid) {
	    	//TODO :: Need to implement another Util class with capability of sending OTPs
	    	// IF the OTP fails need to set isValid = false
	    }
	    
	    return isValid;
	}
	
	//Note :: After credentials are provided in the Keycloak login screen first the user is queried from db then 
	//			this method is called to check the password. After this the isValid method is invoked.
	public boolean supportsCredentialType(String credentialType) {
		return CredentialModel.PASSWORD.equals(credentialType);
	}

	public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
		System.out.println("\nCustomUserStorageProvider::updateCredential\n");
		if (input.getType().equals(CredentialModel.PASSWORD)) throw new ReadOnlyException("User is read only for this update");
        return false;
	}

	public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
		System.out.println("\nCustomUserStorageProvider::disableCredentialType\n");
	}
	
	//Note :: When clicking on the User id in Keycloak Admin portal where the users are listed this method is called
	public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
		return Collections.emptySet();
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

	//Note :: This method is called when the clicked on "View All Users" button in the Keycloak Admin panel
	public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult,
			int maxResults) {
		List<UserModel> lstModel = new ArrayList<UserModel>();
		
		hibernateSession.beginTransaction();
		Query query1 = hibernateSession.createQuery("FROM User");
		List<User> lstUser = query1.getResultList();

		if((lstUser!=null) && (lstUser.size()>0)) {
			for(int i = 0; i<lstUser.size(); i++){
				List<MasterPermission> lstPerm = new ArrayList<>();
				User objUsr = lstUser.get(i);
				int iRoleId = objUsr.getEmpRole().getMstEmployerRole().getId();
				
				//Retrieve permissions for Roles
				Query query2 = hibernateSession.createQuery("FROM MasterEmployerRolePermission m WHERE m.employerRole.id = :emp_role");
				query2.setParameter("emp_role", iRoleId);
				List<MasterEmployerRolePermission> lstRolePerm = query2.getResultList();

				if((lstRolePerm!=null) && (lstRolePerm.size()>0))
					for(int j = 0; j<lstRolePerm.size(); j++){
						//TODO :: Have to find a way to get this done... so far no luck
						//Populating user permissions
						//System.out.println(lstRolePerm.get(j).getPermission().getName());
						lstPerm.add(lstRolePerm.get(j).getPermission());
						//TODO :: Either set the permissions in the User object or find an alternative for this.
					}
				UserModel um = new UserAdapter(session, realm, model, objUsr);
				lstModel.add(um);
			}
			return lstModel;
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
