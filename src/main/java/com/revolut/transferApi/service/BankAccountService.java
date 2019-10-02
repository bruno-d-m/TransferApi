package com.revolut.transferApi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.google.gson.Gson;
import com.revolut.transferApi.dao.BankAccountDAO;
import com.revolut.transferApi.model.ApiResponse;
import com.revolut.transferApi.model.BankAccount;

public class BankAccountService {
	BankAccountDAO bankAccountDAO = new BankAccountDAO();
	
	public ApiResponse createBankAccount(String body){
		BankAccount account = new Gson().fromJson(body, BankAccount.class);
		
		if(account.getOwner() == null || account.getBalance() == null) {
			return ApiResponse.badRequest();
		}
		
		try {
			bankAccountDAO.createBankAccount(account);
		} catch (Exception e) {
			return ApiResponse.internalError(e.getMessage());
		}
		
		Collection<BankAccount> result = new ArrayList<BankAccount>();
		result.add(account);
		return ApiResponse.created(result, null);
	}
	
	public ApiResponse getAllBankAccounts() {
		Collection<BankAccount> accounts = Collections.emptyList();
		try {
			accounts = bankAccountDAO.getAllBankAccounts();
		} catch (Exception e) {
			return ApiResponse.internalError(e.getMessage());
		}
		
		return ApiResponse.ok(accounts, null);
    }

    public ApiResponse getBankAccountById(Long id) {
    	BankAccount account = new BankAccount();
		
		try {
			account = bankAccountDAO.getBankAccountById(id);
		} catch (Exception e) {
			return ApiResponse.internalError(e.getMessage());
		}
		
		if(account == null || account.getId() == null || account.getOwner() == null || account.getBalance() == null) {
			return ApiResponse.notFound("No account found with id: "+ id +".");
		}
		Collection<BankAccount> result = new ArrayList<BankAccount>();
		result.add(account);
		return ApiResponse.ok(result, null);
    }

    public ApiResponse updateBankAccount(String body) {
    	BankAccount accountModifications = new Gson().fromJson(body, BankAccount.class);
		
		if (accountModifications.getId() == null || accountModifications.getOwner() == null) {
			return ApiResponse.badRequest();
        }
		
		try {
			bankAccountDAO.updateBankAccountSafe(accountModifications);
		} catch (Exception e) {
			if(e.getMessage().contains("No bank account found with id: ")) {
				return ApiResponse.notFound(e.getMessage());
			}
			return ApiResponse.internalError(e.getMessage());
		}
		return new ApiResponse(204, null, null, "Account with id "+ accountModifications.getId() +" successfully updated.");
    }
    
    public ApiResponse deleteBankAccount(Long id) {
		try {
			bankAccountDAO.deleteBankAccount(id);
		} catch (Exception e) {
			if(e.getMessage().contains("No bank account found with id: ")) {
				return ApiResponse.notFound(e.getMessage());
			}
			return ApiResponse.internalError(e.getMessage());
		}
		
		return new ApiResponse(204, null, null, "Account with id "+ id +" removed successfully.");
    }

	public ApiResponse createInitialData() {
		BankAccount account1= new BankAccount(1L, "Bruno Martins", new BigDecimal(5000));
		BankAccount account2= new BankAccount(2L, "John Oliver", new BigDecimal(2000));
		BankAccount account3= new BankAccount(3L, "John Snow", new BigDecimal(3000));
		try {
			bankAccountDAO.createBankAccount(account1);
			bankAccountDAO.createBankAccount(account2);
			bankAccountDAO.createBankAccount(account3);
		}catch (Exception e) {
			return ApiResponse.internalError(e.getMessage());
		}
		
		return ApiResponse.created("Initial data created successfully.");
	}
}
