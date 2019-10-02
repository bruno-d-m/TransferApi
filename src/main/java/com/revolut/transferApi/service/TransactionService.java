package com.revolut.transferApi.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.revolut.transferApi.dao.BankAccountDAO;
import com.revolut.transferApi.dao.TransactionDAO;
import com.revolut.transferApi.dao.utils.DataSource;
import com.revolut.transferApi.dao.utils.DbAccess;
import com.revolut.transferApi.model.ApiResponse;
import com.revolut.transferApi.model.BankAccount;
import com.revolut.transferApi.model.Transaction;;

public class TransactionService {
	
	private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
	
	BankAccountDAO bankAccountDAO = new BankAccountDAO();
	TransactionDAO transactionDAO = new TransactionDAO();

	public ApiResponse createTransaction(String body) {
		Transaction transaction = new Gson().fromJson(body, Transaction.class);
		
		if(transaction.getSenderId() == null || transaction.getReceiverId() == null || transaction.getAmount() == null) {
			return ApiResponse.badRequest();
		}
		
		Connection con = null;
		try {
			con = DataSource.getConnection();
            BankAccount sender = bankAccountDAO.getBankAccountById(con, transaction.getSenderId());
            
            if(sender == null || sender.getId() == null || sender.getOwner() == null || sender.getBalance() == null) {
            	throw new Exception("No account found with id: "+ transaction.getSenderId() +".");
            }


            //Check if bank account has enough money for the transaction
            if (sender.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new Exception("The account doesn't have funds for the transaction.");
            }

            BankAccount receiver = bankAccountDAO.getBankAccountById(con, transaction.getReceiverId());
            
            if(receiver == null || receiver.getId() == null || receiver.getOwner() == null || receiver.getBalance() == null) {
            	throw new Exception("No account found with id: "+ transaction.getReceiverId() +".");
            }
            
            sender.setBalance(sender.getBalance().subtract(transaction.getAmount()));
            receiver.setBalance(receiver.getBalance().add(transaction.getAmount()));
            
            bankAccountDAO.updateBankAccount(sender, con);
            bankAccountDAO.updateBankAccount(receiver, con);

            
            transaction = transactionDAO.createTransaction(transaction, con);
            transaction.setAmount(transaction.getAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN));

            con.commit();
		} catch (Exception e) {
			DbAccess.safeRollback(con);
			DbAccess.quietlyClose(con);
			if(e.getMessage().contains("No account found with id: ")) {
				return ApiResponse.notFound(e.getMessage());
			}
            log.error("Unexpected exception", e);
			return ApiResponse.internalError(e.getMessage());
		} finally {
			DbAccess.quietlyClose(con);
        }
		
		Collection<Transaction> result = new ArrayList<Transaction>();
		result.add(transaction);
		return ApiResponse.created(null, result);
	}

	public ApiResponse getAllTransactions() {
		Collection<Transaction> transactions = Collections.emptyList();
		try {
			transactions = transactionDAO.getAllTransactions();
		} catch (Exception e) {
			return ApiResponse.internalError(e.getMessage());
		}
		
		return ApiResponse.ok(null, transactions);
	}

	public ApiResponse getTransactionById(Long id) {
		Transaction transaction = new Transaction();
		try {
			transaction = transactionDAO.getTransactionById(id);
		} catch (Exception e) {
			return ApiResponse.internalError(e.getMessage());
		}
		
		if(transaction == null || transaction.getId() == null || transaction.getSenderId() == null || transaction.getReceiverId() == null || transaction.getAmount() == null) {
			return ApiResponse.notFound("No account found with id: "+ id +".");
		}
		Collection<Transaction> result = new ArrayList<Transaction>();
		result.add(transaction);
		return ApiResponse.ok(null, result);
	}

}
