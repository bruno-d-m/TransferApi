package com.revolut.transferApi.model;

import java.util.Collection;

import com.google.gson.Gson;

public class ApiResponse {
	private int code;
	private Collection<BankAccount> accounts;
	private Collection<Transaction> transactions;
	private String message;

	public ApiResponse(int code, Collection<BankAccount> accounts, Collection<Transaction> transactions, String message){
		this.code = code;
		this.accounts = accounts;
		this.transactions = transactions;
		this.message = message;
	}
	
    public ApiResponse(int code) {
        this.code = code;
        this.accounts = null;
		this.transactions = null;
		this.message = null;
    }

    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
    	this.code = code;
    }
    
    public String getMessage() {
    	return message;
    }
    
    public void setMessage(String message) {
    	this.message = message;
    }
    

	public Collection<BankAccount> getAccounts() {
		return accounts;
	}

	public void setAccount(Collection<BankAccount> accounts) {
		this.accounts = accounts;
	}

	public Collection<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Collection<Transaction> transactions) {
		this.transactions = transactions;
	}

    public static ApiResponse ok(Collection<BankAccount> accounts, Collection<Transaction> transactions) {
        return new ApiResponse(200, accounts, transactions, null);
    }
    
    public static ApiResponse created(Collection<BankAccount> accounts, Collection<Transaction> transactions) {
    	return new ApiResponse(201, accounts, transactions, null);
    }
    
    public static ApiResponse ok(String message) {
    	return new ApiResponse(200, null, null, message);
    }
    
    public static ApiResponse created(String message) {
    	return new ApiResponse(201, null, null, message);
    }
    
    public static ApiResponse internalError(String error) {
    	return new ApiResponse(500, null, null, error);
    }
    
    public static ApiResponse notFound(String error) {
    	return new ApiResponse(404, null, null, error);
    }
    
    public static ApiResponse badRequest() {
    	return new ApiResponse(400, null, null, "Malformed request.");
    }
    
    public String toJson() {
    	return new Gson().toJson(this);
    }
}