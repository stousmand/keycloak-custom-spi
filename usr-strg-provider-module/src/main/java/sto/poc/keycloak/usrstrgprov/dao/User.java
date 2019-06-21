package sto.poc.keycloak.usrstrgprov.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="user")
public class User {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="category_id")
	private MasterUsrCategory mstUsrCategory;
	
    @Column(name="username")
    private String username;
    
    @Column(name="password")
    private String password;
    
    @Column(name="display_name")
    private String displayName;
    
    @Column(name="active")
    private boolean active;
    
	@Transient
    private String kcId;
	@Transient
    private String email;
	@Transient
    private String name;
	
    @OneToOne(mappedBy="employer", cascade=CascadeType.ALL, fetch=FetchType.LAZY, optional=false)
    private EmployerRole empRole;
	
    public User() {}
	public User(String kcId, String email, String name, String username, String password) {
		super();
		this.kcId = kcId;
		this.email = email;
		this.name = name;
		this.username = username;
		this.password = password;
	}

	//Getters & Setters
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getKcId() {
		return kcId;
	}
	public void setKcId(String kcId) {
		this.kcId = kcId;
	}
	public MasterUsrCategory getMstUsrCategory() {
		return mstUsrCategory;
	}
	public void setMstUsrCategory(MasterUsrCategory mstUsrCategory) {
		this.mstUsrCategory = mstUsrCategory;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public EmployerRole getEmpRole() {
		return empRole;
	}
	public void setEmpRole(EmployerRole empRole) {
		this.empRole = empRole;
	}
	
}
