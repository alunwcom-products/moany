package com.alunw.moany.model;

//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.Table;

//import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "users")
public class User {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
//	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	private String id;

	@Column(nullable = false, unique = true)
	private String username;

	private String password;

	@Column(nullable = false)
	private boolean enabled;

	public User() {
		this.enabled = true;
	}

	public User(String username, String password) {
		this();
		setUsername(username);
		setPassword(password);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	/*
	 * encode password
	 */
	public void setPassword(String password) {
		PasswordEncoder encoder = new BCryptPasswordEncoder(11);
		this.password = encoder.encode(password);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
