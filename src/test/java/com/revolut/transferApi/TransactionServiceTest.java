package com.revolut.transferApi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.revolut.transferApi.model.ApiResponse;
import com.revolut.transferApi.model.Transaction;
import com.revolut.transferApi.service.BankAccountService;
import com.revolut.transferApi.service.TransactionService;

public class TransactionServiceTest {
	
	private static BankAccountService bankAccountService = new BankAccountService();
	private static TransactionService transactionService = new TransactionService();
	private static String creationSuccessfulRequest;
	private static String creationBadRequest1;
	private static String creationBadRequest2;
	private static String creationBadRequest3;
	private static String creationNoFunds;
	private static String creationNotFound1;
	private static String creationNotFound2;
	private static String creationNotFound3;
	private static String creationNotFound4;
	
    //Do before testing
	@BeforeAll
    static void setUp() {
		BasicConfigurator.configure();
		bankAccountService.createInitialData();
		creationSuccessfulRequest = new Gson().toJson(new Transaction(3L, 1L, new BigDecimal(50.73)));
		creationBadRequest1 = new Gson().toJson(new Transaction(null, 1L, new BigDecimal(2000.00)));
		creationBadRequest2 = new Gson().toJson(new Transaction(3L, null, new BigDecimal(2000.00)));
		creationBadRequest3 = new Gson().toJson(new Transaction(3L, 1L, null));
		creationNoFunds = new Gson().toJson(new Transaction(3L, 1L, new BigDecimal(10000.00)));
		creationNotFound1 = new Gson().toJson(new Transaction(0L, 1L, new BigDecimal(2000.00)));
		creationNotFound2 = new Gson().toJson(new Transaction(1L, 0L, new BigDecimal(2000.00)));
		creationNotFound3 = new Gson().toJson(new Transaction(1000L, 1L, new BigDecimal(2000.00)));
		creationNotFound4 = new Gson().toJson(new Transaction(1L, 1000L, new BigDecimal(2000.00)));
    }
    
    @Test
    @Order(1)
    void testCreationSuccessfulRequest() {
    	BigDecimal senderBalanceBefore = bankAccountService.getBankAccountById(3L).getAccounts().iterator().next().getBalance();
    	BigDecimal receiverBalanceBefore = bankAccountService.getBankAccountById(1L).getAccounts().iterator().next().getBalance();
    	
		ApiResponse response = transactionService.createTransaction(creationSuccessfulRequest);
		assertNotNull(response);
    	assertEquals(response.getCode(), 201);
    	assertNotNull(response.getTransactions());
    	assertTrue(response.getTransactions().size() == 1);
    	response.getTransactions().forEach(transaction -> {
    		assertNotNull(transaction);
    		assertNotNull(transaction.getId());
    		assertNotNull(transaction.getSenderId());
    		assertNotNull(transaction.getReceiverId());
    		assertNotNull(transaction.getAmount());
    		assertNotNull(transaction.getDate());
    	});
    	
    	BigDecimal amount = response.getTransactions().iterator().next().getAmount();
    	
    	BigDecimal senderBalanceAfter = bankAccountService.getBankAccountById(3L).getAccounts().iterator().next().getBalance();
    	BigDecimal receiverBalanceAfter = bankAccountService.getBankAccountById(1L).getAccounts().iterator().next().getBalance();
    	assertTrue(senderBalanceBefore.subtract(amount).compareTo(senderBalanceAfter) == 0);
    	assertTrue(receiverBalanceBefore.add(amount).compareTo(receiverBalanceAfter) == 0);
    }
    
    @Test
    @Order(2)
    void testCreationBadRequest() {
    	ApiResponse response = transactionService.createTransaction(creationBadRequest1);
		assertNotNull(response);
    	assertEquals(response.getCode(), 400);
    	assertNotNull(response.getMessage());
    	assertNull(response.getTransactions());
    	
    	ApiResponse response2 = transactionService.createTransaction(creationBadRequest2);
    	assertNotNull(response2);
    	assertEquals(response2.getCode(), 400);
    	assertNotNull(response2.getMessage());
    	assertNull(response2.getTransactions());
    	
    	ApiResponse response3 = transactionService.createTransaction(creationBadRequest3);
    	assertNotNull(response3);
    	assertEquals(response3.getCode(), 400);
    	assertNotNull(response3.getMessage());
    	assertNull(response3.getTransactions());
    }
    
    @Test
    @Order(3)
    void testCreationNoFunds() {
    	ApiResponse response = transactionService.createTransaction(creationNoFunds);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 500);
    	assertNotNull(response.getMessage());
    	assertNull(response.getTransactions());
    }
    
    @Test
    @Order(4)
    void testCreationNotFound() {
    	ApiResponse response = transactionService.createTransaction(creationNotFound1);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 404);
    	assertNotNull(response.getMessage());
    	assertNull(response.getTransactions());
    	
    	ApiResponse response2 = transactionService.createTransaction(creationNotFound2);
    	assertNotNull(response2);
    	assertEquals(response2.getCode(), 404);
    	assertNotNull(response2.getMessage());
    	assertNull(response2.getTransactions());
    	
    	ApiResponse response3 = transactionService.createTransaction(creationNotFound3);
    	assertNotNull(response3);
    	assertEquals(response3.getCode(), 404);
    	assertNotNull(response3.getMessage());
    	assertNull(response3.getTransactions());
    	
    	ApiResponse response4 = transactionService.createTransaction(creationNotFound4);
    	assertNotNull(response4);
    	assertEquals(response4.getCode(), 404);
    	assertNotNull(response4.getMessage());
    	assertNull(response4.getTransactions());
    }
    
    @Test
    @Order(5)
    void testGetAllBankAccounts() {
    	ApiResponse response = transactionService.getAllTransactions();
    	assertNotNull(response);
    	assertEquals(response.getCode(), 200);
    	assertNotNull(response.getTransactions());
    	response.getTransactions().forEach(transaction -> {
    		assertNotNull(transaction);
    		assertNotNull(transaction.getId());
    		assertNotNull(transaction.getSenderId());
    		assertNotNull(transaction.getReceiverId());
    		assertNotNull(transaction.getAmount());
    		assertNotNull(transaction.getDate());
    	});
    }
    
    @Test
    @Order(6)
    void testGetTransactionByExistingId() {
    	transactionService.createTransaction(creationSuccessfulRequest);
    	ApiResponse response = transactionService.getTransactionById(1L);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 200);
    	assertNotNull(response.getTransactions());
    	response.getTransactions().forEach(transaction -> {
    		assertNotNull(transaction);
    		assertNotNull(transaction.getId());
    		assertNotNull(transaction.getSenderId());
    		assertNotNull(transaction.getReceiverId());
    		assertNotNull(transaction.getAmount());
    		assertNotNull(transaction.getDate());
    	});
    }
    
    @Test
    @Order(7)
    void testGetTransactionByNonExistingId() {
    	ApiResponse response = transactionService.getTransactionById(0L);
    	assertNotNull(response);
    	assertEquals(response.getCode(), 404);
    	assertNull(response.getTransactions());
    	assertNotNull(response.getMessage());
    	
    	ApiResponse response2 = transactionService.getTransactionById(10000L);
    	assertNotNull(response2);
    	assertEquals(response2.getCode(), 404);
    	assertNull(response2.getTransactions());
    	assertNotNull(response.getMessage());
    }
}
