package com.revolut.transferApi;

import static spark.Spark.*;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.transferApi.controller.BankAccountController;
import com.revolut.transferApi.controller.TransactionController;
import com.revolut.transferApi.model.ApiResponse;

public class App 
{
	public static void main(String[] args) {
		
		//Server configuration
		BasicConfigurator.configure();
		port(8080);
		Logger log = LoggerFactory.getLogger(App.class);
		
		//Set up before filter for logging
		before((request, response) -> log.info("Received api call. Path: " + request.pathInfo()));
		
		
		//Set up routes
        post("/initialData", BankAccountController.createInitialData); //Create initial data in the database
        post("/bankAccount", BankAccountController.createBankAccount); //Create a bank account
        get("/bankAccount", BankAccountController.getAllBankAccounts); //Get all bank accounts
        get("/bankAccount/:id", BankAccountController.getBankAccountById); //Get one bank account (by Id)
        put("/bankAccount", BankAccountController.updateBankAccount); //Update bank account's owner name
        delete("/bankAccount/:id", BankAccountController.deleteBankAccount); //Delete bank account
        post("/transaction", TransactionController.createTransaction); //Create a transaction between bank accounts
        get("/transaction", TransactionController.getAllTransactions); //Get all transactions
        get("/transaction", TransactionController.getTransactionById); //Get one transaction (by Id)
        
        
        //Set up not Found route
        notFound((request, response) -> ApiResponse.notFound("Context path " + request.pathInfo() + " not found.").toJson());
        
        //Set up after filter to guarantee the response type as application/json
        after((request, response) -> response.type("application/json"));
    }
}
