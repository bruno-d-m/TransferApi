package com.revolut.transferApi;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.revolut.transferApi.model.ApiResponse;
import com.revolut.transferApi.model.BankAccount;
import com.revolut.transferApi.service.BankAccountService;


public class BankAccountServiceTest{
	
	private static BankAccountService bankAccountService = new BankAccountService();
	private static String creationSuccessfulRequest;
	private static String creationBadRequest1;
	private static String creationBadRequest2;
	private static String updateSuccessfulRequest;
	private static String updateBadRequest1;
	private static String updateBadRequest2;
	private static String updateAccountNotFound1;
	private static String updateAccountNotFound2;
	
    //Do before testing
	@BeforeAll
    static void setUp() {
		BasicConfigurator.configure();
		bankAccountService.createInitialData();
		creationSuccessfulRequest = new Gson().toJson(new BankAccount("Jack Napier", new BigDecimal(10000.80)));
		creationBadRequest1 = new Gson().toJson(new BankAccount(null, new BigDecimal(5000.00)));
		creationBadRequest2 = new Gson().toJson(new BankAccount("Jack Napier", null));
		updateSuccessfulRequest = new Gson().toJson(new BankAccount(1L, "Bruno Dias", null));
		updateBadRequest1 = new Gson().toJson(new BankAccount(null, "Bruno Dias", null));
		updateBadRequest2 = new Gson().toJson(new BankAccount(1L, null, null));
		updateAccountNotFound1 = new Gson().toJson(new BankAccount(0L, "Wally Waldo", null));
		updateAccountNotFound2 = new Gson().toJson(new BankAccount(10000L, "Carmen Sandiego", null));
    }
    
    @Test
    void testCreationSuccessfulRequest() {
		ApiResponse response = bankAccountService.createBankAccount(creationSuccessfulRequest);
		assertNotNull(response);
    	assertEquals(response.getCode(), 201);
    	assertNotNull(response.getAccounts());
    	assertTrue(response.getAccounts().size() == 1);
    	response.getAccounts().forEach(account -> {
    		assertNotNull(account);
    		assertNotNull(account.getId());
    		assertNotNull(account.getOwner());
    		assertNotNull(account.getBalance());
    	});
    }
    
    @Test 
    void testCreationBadRequest() {
    	ApiResponse response = bankAccountService.createBankAccount(creationBadRequest1);
		assertNotNull(response);
    	assertEquals(response.getCode(), 400);
    	assertNotNull(response.getMessage());
    	assertNull(response.getAccounts());
    	
    	ApiResponse response2 = bankAccountService.createBankAccount(creationBadRequest2);
    	assertNotNull(response2);
    	assertEquals(response2.getCode(), 400);
    	assertNotNull(response2.getMessage());
    	assertNull(response2.getAccounts());
    }
    
    @Test
    void testGetAllBankAccounts() {
    	ApiResponse response = bankAccountService.getAllBankAccounts();
    	assertNotNull(response);
    	assertEquals(response.getCode(), 200);
    	assertNotNull(response.getAccounts());
    	response.getAccounts().forEach(account -> {
    		assertNotNull(account);
    		assertNotNull(account.getId());
    		assertNotNull(account.getOwner());
    		assertNotNull(account.getBalance());
    	});
    }
    
    @Test
    void testGetBankAccountByExistingId() {
    	ApiResponse response = bankAccountService.getBankAccountById(1L);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 200);
    	assertNotNull(response.getAccounts());
    	response.getAccounts().forEach(account -> {
    		assertNotNull(account);
    		assertNotNull(account.getId());
    		assertNotNull(account.getOwner());
    		assertNotNull(account.getBalance());
    	});
    }
    
    @Test
    void testGetBankAccountByNonExistingId() {
    	ApiResponse response = bankAccountService.getBankAccountById(0L);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 404);
    	assertNull(response.getAccounts());
    	assertNotNull(response.getMessage());
    	
    	ApiResponse response2 = bankAccountService.getBankAccountById(10000L);
    	assertNotNull(response2);
    	assertEquals(response2.getCode(), 404);
    	assertNull(response2.getAccounts());
    	assertNotNull(response.getMessage());
    }
    
    @Test
    void testUpdateSuccessfulRequest() {
    	ApiResponse response = bankAccountService.updateBankAccount(updateSuccessfulRequest);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 204);
    	assertNotNull(response.getMessage());
    }
    
    @Test
    void testUpdateBadRequest() {
    	ApiResponse response = bankAccountService.updateBankAccount(updateBadRequest1);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 400);
    	assertNotNull(response.getMessage());
    	
    	ApiResponse response2 = bankAccountService.updateBankAccount(updateBadRequest2);
    	assertNotNull(response2);
    	assertEquals(response2.getCode(), 400);
    	assertNotNull(response2.getMessage());
    }
    
    @Test
    void testUpdateNotFound() {
    	ApiResponse response = bankAccountService.updateBankAccount(updateAccountNotFound1);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 404);
    	assertNotNull(response.getMessage());
    	
    	ApiResponse response2 = bankAccountService.updateBankAccount(updateAccountNotFound2);
    	assertNotNull(response2);
    	assertEquals(response2.getCode(), 404);
    	assertNotNull(response2.getMessage());
    }
    
    @Test
    void testDeleteExistingId() {
    	ApiResponse response = bankAccountService.deleteBankAccount(3L);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 204);
    	assertNotNull(response.getMessage());
    	ApiResponse expectNotFound = bankAccountService.getBankAccountById(3L);
    	assertEquals(expectNotFound.getCode(), 404);
    }
    
    @Test
    void testDeleteNonExistingId() {
    	ApiResponse response = bankAccountService.deleteBankAccount(0L);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 404);
    	assertNotNull(response.getMessage());
    	
    	ApiResponse response2 = bankAccountService.deleteBankAccount(10000L);
    	assertNotNull(response2);
    	assertEquals(response2.getCode(), 404);
    	assertNotNull(response.getMessage());
    }
}
