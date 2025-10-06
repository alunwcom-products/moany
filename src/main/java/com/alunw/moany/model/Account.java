package com.alunw.moany.model;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

//import ja.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;

import jakarta.persistence.*;
//import org.hibernate.annotations.GenericGenerator;
//import org.hibernate.annotations.Type;

@Entity
@Table(name = "accounts")
public class Account {

	/**
	 * Primary identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "uuid", nullable = false, unique = true)
	private String id;

	/**
	 * Account name
	 */
	@Column(name = "name", nullable = false)
//	@Type(type = "string")
	private String name;

	/**
	 * Account number
	 */
	@Column(name = "account_num", nullable = false, unique = true)
//	@Type(type = "string")
	private String accNum;

	/** Account type (AccountType): {DEBIT|CREDIT} */
	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private AccountType type;

	@Column(name = "starting_balance")
	private BigDecimal startBalance;

	/**
	 * No accessor methods for this property - only included to allow cascading (i.e. deletion of transactions when
	 * account is deleted).
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "account")
	private Set<Transaction> transactions;

	public Account() {
		// Set defaults
		setType(AccountType.DEBIT);
		setStartBalance(new BigDecimal(0));
	}

	public Account(String name, String accNum, AccountType type) {
		this();
		this.name = name;
		this.accNum = accNum;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccNum() {
		return accNum;
	}

	public void setAccNum(String accNum) {
		this.accNum = accNum;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public BigDecimal getStartBalance() {
		return startBalance;
	}

	public void setStartBalance(BigDecimal startBalance) {
		this.startBalance = startBalance;
	}

	@Override
	public String toString() {
		return "Account[" + id + "|" + accNum + "|" + type + "]";
	}

}