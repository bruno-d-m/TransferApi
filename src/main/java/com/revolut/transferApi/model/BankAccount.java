package com.revolut.transferApi.model;

import java.math.BigDecimal;
import java.util.Random;

public class BankAccount implements ObjectWithId {
	private Long id;
    private String owner;
    private BigDecimal balance;
    
    public BankAccount() {
    }
    
    public BankAccount(String owner, BigDecimal balance) {
        this(new Random().nextLong(), owner, balance);
    }

    public BankAccount(Long id, String owner, BigDecimal balance) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
