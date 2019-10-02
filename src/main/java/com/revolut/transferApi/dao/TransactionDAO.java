package com.revolut.transferApi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.transferApi.dao.utils.DbAccess;
import com.revolut.transferApi.model.Transaction;

public class TransactionDAO {
	
	private static final Logger log = LoggerFactory.getLogger(TransactionDAO.class);
	
	private DbAccess dbAccess = DbAccess.getInstance();

	public Transaction createTransaction(Transaction transaction, Connection con) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into transaction ");
		sql.append(" (sender_id, receiver_id, amount, creation_date) ");
		sql.append(" values (?, ?, ?, ?) ");
			
		
		try {
			transaction = dbAccess.executeQueryInConnection(con, sql.toString(),
					new DbAccess.CreationQueryExecutor<>(transaction, TransactionDAO::fillPreparedStatement)).getResult();
		} catch (SQLException e) {
			log.error("There was an error while trying to insert bank account in the database.", e);
            throw new SQLException(e);
		}
		
		
		if (transaction == null || transaction.getId() == null) {
            throw new SQLException("Error while inserting transaction in the database. Could not obtain Id.");
        }
		
		return transaction;
	}

	public Collection<Transaction> getAllTransactions() throws SQLException {
		try {
			return dbAccess.executeQuery("select * from transaction", getTransactions -> {
				Collection<Transaction> transactions = new ArrayList<>();
				
				try (ResultSet transactionsRS = getTransactions.executeQuery()) {
					if (transactionsRS != null) {
						while (transactionsRS.next()) {
							transactions.add(getTransactionFromResultSet(transactionsRS));
						}
					}
				}
				
				return transactions;
			}).getResult();
		} catch (SQLException e) {
			log.error("There was an error while trying to get all bank accounts.", e);
            throw new SQLException(e);
		}
	}


	public Transaction getTransactionById(Long id) throws SQLException {
		Transaction transaction = new Transaction();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from transaction t");
		sql.append(" where t.id = ? ");

        try {
        	transaction = dbAccess.executeQuery(sql.toString(), getTransaction -> {
				getTransaction.setLong(1, id);
			    try (ResultSet transactionRS = getTransaction.executeQuery()) {
			        if (transactionRS != null && transactionRS.first()) {
			            return getTransactionFromResultSet(transactionRS);
			        }
			    }

			    return null;
			}).getResult();
		} catch (SQLException e) {
			log.error("There was an error while trying to get bank account with id: "+ id +".", e);
            throw new SQLException(e);
		}
        
        return transaction;
	}
	
	private static void fillPreparedStatement(PreparedStatement preparedStatement, Transaction transaction) {
		try {
			preparedStatement.setLong(1, transaction.getSenderId());
			preparedStatement.setLong(2, transaction.getReceiverId());
			preparedStatement.setBigDecimal(3, transaction.getAmount());
			preparedStatement.setDate(4, new java.sql.Date(new Date().getTime()));
		} catch (SQLException e) {
			log.error("Transactions prepared statement could not be initialized by values", e);
		}
		
	}
	
	private Transaction getTransactionFromResultSet(ResultSet transactionsRS) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(transactionsRS.getLong("id"));
        transaction.setSenderId(transactionsRS.getLong("sender_id"));
        transaction.setReceiverId(transactionsRS.getLong("receiver_id"));
        transaction.setAmount(transactionsRS.getBigDecimal("amount"));
        transaction.setDate(transactionsRS.getDate("creation_date"));
        return transaction;
    }
}
