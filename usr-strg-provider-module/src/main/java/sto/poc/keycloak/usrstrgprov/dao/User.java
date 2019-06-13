package sto.poc.keycloak.usrstrgprov.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="user")
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
	@Transient
    private String kcId;
	@Transient
    private String email;
	@Transient
    private String name;
    @Column(name="username")
    private String username;
    @Column(name="password")
    private String password;
    
    public User() {
    }
	public User(String kcId, String email, String name, String username, String password) {
		super();
		this.kcId = kcId;
		this.email = email;
		this.name = name;
		this.username = username;
		this.password = password;
	}

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

}
