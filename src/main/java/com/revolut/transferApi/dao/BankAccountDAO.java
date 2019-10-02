package com.revolut.transferApi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.transferApi.dao.utils.DbAccess;
import com.revolut.transferApi.model.BankAccount;

public class BankAccountDAO {
	
	private static final Logger log = LoggerFactory.getLogger(BankAccountDAO.class);
	
	private DbAccess dbAccess = DbAccess.getInstance();

	public BankAccount createBankAccount(BankAccount bankAccount) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into bank_account ");
        sql.append(" (owner_name, balance) ");      
        sql.append(" values ");      
        sql.append(" (?, ?) ");      

        try {
			bankAccount = dbAccess.executeQuery(sql.toString(),
			        new DbAccess.CreationQueryExecutor<>(bankAccount, BankAccountDAO::fillPreparedStatement)).getResult();
		} catch (SQLException e) {
			log.error("There was an error while trying to insert bank account in the database.", e);
            throw new SQLException(e);
		}

        if (bankAccount == null || bankAccount.getId() == null) {
            throw new SQLException("Error while inserting bank account in the database. Could not obtain Id.");
        }

        return bankAccount;
	}

	public Collection<BankAccount> getAllBankAccounts() throws SQLException {
		try {
			return dbAccess.executeQuery("select * from bank_account", getBankAccounts -> {
				Collection<BankAccount> bankAccounts = new ArrayList<>();
				
				try (ResultSet bankAccountsRS = getBankAccounts.executeQuery()) {
					if (bankAccountsRS != null) {
						while (bankAccountsRS.next()) {
							bankAccounts.add(getBankAccountFromResultSet(bankAccountsRS));
						}
					}
				}
				
				return bankAccounts;
			}).getResult();
		} catch (SQLException e) {
			log.error("There was an error while trying to get all bank accounts.", e);
            throw new SQLException(e);
		}
	}

	public BankAccount getBankAccountById(Long id) throws SQLException {
		BankAccount account = new BankAccount();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from bank_account ba");
		sql.append(" where ba.id = ? ");

        try {
			account = dbAccess.executeQuery(sql.toString(), getBankAccount -> {
			    getBankAccount.setLong(1, id);
			    try (ResultSet bankAccountRS = getBankAccount.executeQuery()) {
			        if (bankAccountRS != null && bankAccountRS.first()) {
			            return getBankAccountFromResultSet(bankAccountRS);
			        }
			    }

			    return null;
			}).getResult();
		} catch (SQLException e) {
			log.error("There was an error while trying to get bank account with id: "+ id +".", e);
            throw new SQLException(e);
		}
        
        return account;
	}
	
	public BankAccount getBankAccountById(Connection con, Long id) throws SQLException {
		BankAccount account = new BankAccount();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from bank_account ba");
		sql.append(" where ba.id = ? ");
		
		try {
			account = dbAccess.executeQueryInConnection(con, sql.toString(), getBankAccount -> {
				getBankAccount.setLong(1, id);
				try (ResultSet bankAccountRS = getBankAccount.executeQuery()) {
					if (bankAccountRS != null && bankAccountRS.first()) {
						return getBankAccountFromResultSet(bankAccountRS);
					}
				}
				
				return null;
			}).getResult();
		} catch (SQLException e) {
			log.error("There was an error while trying to get bank account with id: "+ id +".", e);
			throw new SQLException(e);
		}
		
		return account;
	}

	public void updateBankAccountSafe(BankAccount bankAccount) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" update bank_account ");
		sql.append(" set ");
		sql.append(" owner_name = ? ");
		sql.append(" where id = ?");

		DbAccess.QueryExecutor<Integer> queryExecutor = updateBankAccount -> {
            updateBankAccount.setString(1, bankAccount.getOwner());
            updateBankAccount.setLong(2, bankAccount.getId());

            return updateBankAccount.executeUpdate();
        };

        int result;
		try {
			result = dbAccess.executeQuery(sql.toString(), queryExecutor).getResult();
		} catch (SQLException e) {
			log.error("There was an error while trying to update bank account with id: "+ bankAccount.getId() +".", e);
            throw new SQLException(e);
		}

        if (result == 0) {
            throw new Exception("No bank account found with id: " + bankAccount.getId() +".");
        }
		
	}
	
	public void updateBankAccount(BankAccount bankAccount, Connection con) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" update bank_account ");
		sql.append(" set ");
		sql.append(" owner_name = ?, ");
		sql.append(" balance = ? ");
		sql.append(" where id = ?");
		
		DbAccess.QueryExecutor<Integer> queryExecutor = updateBankAccount -> {
			updateBankAccount.setString(1, bankAccount.getOwner());
			updateBankAccount.setBigDecimal(2, bankAccount.getBalance());
			updateBankAccount.setLong(3, bankAccount.getId());
			
			return updateBankAccount.executeUpdate();
		};
		
		int result;
		try {
			result = dbAccess.executeQueryInConnection(con, sql.toString(), queryExecutor).getResult();
		} catch (SQLException e) {
			log.error("There was an error while trying to update bank account with id: "+ bankAccount.getId() +".", e);
			throw new SQLException(e);
		}
		
		if (result == 0) {
			throw new Exception("No bank account found with id: " + bankAccount.getId() +".");
		}
		
	}
	
	public void deleteBankAccount(Long id) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" delete from bank_account ");
		sql.append(" where id = ? ");
		
		DbAccess.QueryExecutor<Integer> queryExecutor = deleteBankAccount -> {
            deleteBankAccount.setLong(1, id);

            return deleteBankAccount.executeUpdate();
        };
        
        int result;
		try {
			result = dbAccess.executeQuery(sql.toString(), queryExecutor).getResult();
		} catch (SQLException e) {
			log.error("There was an error while trying to delete bank account with id: "+ id +".", e);
            throw new SQLException(e);
		}

        if (result == 0) {
            throw new Exception("No bank account found with id: " + id +".");
        }
	}
	
	private BankAccount getBankAccountFromResultSet(ResultSet bankAccountsRS) throws SQLException {
		BankAccount bankAccount = new BankAccount();
		bankAccount.setId(bankAccountsRS.getLong("id"));
		bankAccount.setOwner(bankAccountsRS.getString("owner_name"));
		bankAccount.setBalance(bankAccountsRS.getBigDecimal("balance"));
		
		return bankAccount;
	}
	
	private static void fillPreparedStatement(PreparedStatement preparedStatement, BankAccount bankAccount) {
        try {
            preparedStatement.setString(1, bankAccount.getOwner());
            preparedStatement.setBigDecimal(2, bankAccount.getBalance());
        } catch (SQLException e) {
            log.error("BankAccount prepared statement could not be initialized by values", e);
        }
    }
	
}
