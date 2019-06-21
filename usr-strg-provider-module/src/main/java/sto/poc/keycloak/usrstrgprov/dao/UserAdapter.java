/*
 * 1.0.0		20190619			shirhan							UserAdapter is a construct that should be implemented, to 
 * 																	plug in with the Keycloak UI. It's given method implementation
 * 																	serves different purposes.
 * */
package sto.poc.keycloak.usrstrgprov.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class UserAdapter extends AbstractUserAdapterFederatedStorage {

	private final User user;
	private final String keycloakId;
	
	public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, User user) {
		super(session, realm, model);
		this.user = user;
		this.keycloakId = StorageId.keycloakId(model, Integer.toString(user.getId()));
	}

	@Override
	public String getId() {
		return keycloakId;
	}
	@Override
	public String getUsername() {
		return user.getUsername();
	}
	@Override
	public void setUsername(String username) {
		user.setUsername(username);
	}
	/*
	@Override
	public String getEmail() {
		return user.getEmail();
	}
	@Override
	public void setEmail(String email) {
		user.setEmail(email);
	}
	*/
	
	 /*
	 * Gets role mappings from federated storage and automatically appends default roles.
	 * Also calls getRoleMappingsInternal() method to pull role mappings from provider.
	 * Implementors can override that method
	 */
	/*
  	@Override
	public Set<RoleModel> getRoleMappings() {
  		Set<RoleModel> set = new HashSet<>();
  		return Collections.EMPTY_SET;
  		
		return set;
	}
  	*/
	
    protected Set<RoleModel> getRoleMappingsInternal() {
    	Set<RoleModel> set = new HashSet<>();
        //System.out.println("UserAdapter::getRoleMappingsInternal()");
  		/*if(user.getLstPermissions().size()>0) {
  	  		for(MasterPermission perm : user.getLstPermissions()) {
  	  			String roleName = perm.getName();
  	  			RoleModel modelRole = roleContainer.getRole(perm.getName());
  	  			if (modelRole == null) {
  	  				modelRole = roleContainer.addRole(roleName);
  	  			}
  	  			set.add(modelRole);
  	  		}
  		}
  		*/
        return set;
    }
    
	//TODO :: Need to find how this needs to be handled
	@Override
	public String getFirstName() {
		return user.getName();
	}
	@Override
	public void setFirstName(String firstName) {
		user.setName(firstName);
	}
	@Override
	public String getLastName() {
		return user.getName();
	}
	@Override
	public void setLastName(String lastName) {
		user.setName(lastName);
	}

}
