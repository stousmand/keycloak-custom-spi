package sto.poc.keycloak.usrstrgprov.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="mst_employer_role_permission")
public class MasterEmployerRolePermission {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

	@ManyToOne
    @JoinColumn(name="permission_id")
    private MasterPermission permission;
    
    @ManyToOne
    @JoinColumn(name="employer_role_id")
    private MasterEmployerRole employerRole;

    //Getters & Setters
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public MasterPermission getPermission() {
		return permission;
	}
	public void setPermission(MasterPermission permission) {
		this.permission = permission;
	}
	public MasterEmployerRole getEmployerRole() {
		return employerRole;
	}
	public void setEmployerRole(MasterEmployerRole employerRole) {
		this.employerRole = employerRole;
	}
    
}
