package sto.poc.keycloak.usrstrgprov.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="employer_role")
public class EmployerRole {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="employer")
	private User employer;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="employer_role")
    private MasterEmployerRole mstEmployerRole;
    
    //Getters & Setters
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public User getEmployer() {
		return employer;
	}
	public void setEmployer(User employer) {
		this.employer = employer;
	}
	public MasterEmployerRole getMstEmployerRole() {
		return mstEmployerRole;
	}
	public void setMstEmployerRole(MasterEmployerRole mstEmployerRole) {
		this.mstEmployerRole = mstEmployerRole;
	}

}
