package sto.poc.keycloak.usrstrgprov.dao;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="mst_employer_role")
public class MasterEmployerRole {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    @Column(name="name")
    private String name;
    @Column(name="head_office")
    private int headOffice;
    
    @OneToOne(mappedBy="mstEmployerRole", cascade=CascadeType.ALL, fetch=FetchType.LAZY, optional=false)
    private EmployerRole empRole;
    
    @OneToMany(mappedBy="employerRole")
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
	public int getHeadOffice() {
		return headOffice;
	}
	public void setHeadOffice(int headOffice) {
		this.headOffice = headOffice;
	}
	public EmployerRole getEmpRole() {
		return empRole;
	}
	public void setEmpRole(EmployerRole empRole) {
		this.empRole = empRole;
	}
	public Set<MasterEmployerRolePermission> getMasterEmployerRolePermission() {
		return masterEmployerRolePermission;
	}
	public void setMasterEmployerRolePermission(Set<MasterEmployerRolePermission> masterEmployerRolePermission) {
		this.masterEmployerRolePermission = masterEmployerRolePermission;
	}
	
}
