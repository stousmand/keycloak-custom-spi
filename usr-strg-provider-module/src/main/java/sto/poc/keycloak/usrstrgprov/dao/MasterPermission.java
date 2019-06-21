package sto.poc.keycloak.usrstrgprov.dao;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="mst_permission")
public class MasterPermission {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    @Column(name="name")
    private String name;
    @Column(name="permission")
    private String permission;
    @Column(name="organization")
    private int organization;
    @Column(name="employer")
    private int employer;
    @Column(name="member")
    private int member;

    @OneToMany(mappedBy="permission")
    private Set<MasterEmployerRolePermission> masterEmployerRolePermission;
    
    //Getters & Setters
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public int getOrganization() {
		return organization;
	}
	public void setOrganization(int organization) {
		this.organization = organization;
	}
	public int getEmployer() {
		return employer;
	}
	public void setEmployer(int employer) {
		this.employer = employer;
	}
	public int getMember() {
		return member;
	}
	public void setMember(int member) {
		this.member = member;
	}
	public Set<MasterEmployerRolePermission> getMasterEmployerRolePermission() {
		return masterEmployerRolePermission;
	}
	public void setMasterEmployerRolePermission(Set<MasterEmployerRolePermission> masterEmployerRolePermission) {
		this.masterEmployerRolePermission = masterEmployerRolePermission;
	}
	
}
