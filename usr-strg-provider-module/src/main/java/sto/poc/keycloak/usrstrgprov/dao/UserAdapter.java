package sto.poc.keycloak.usrstrgprov.dao;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
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
