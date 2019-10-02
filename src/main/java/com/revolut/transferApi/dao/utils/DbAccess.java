package com.revolut.transferApi.dao.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.transferApi.model.ObjectWithId;

import java.sql.*;
import java.util.function.BiConsumer;

public class DbAccess {
	
	private static final Logger log = LoggerFactory.getLogger(DbAccess.class);

    private static final DbAccess dbAccess = new DbAccess();

    private DbAccess() {
    }

    public static DbAccess getInstance() {
        return dbAccess;
    }

    //Executes query with provided execute method
    //Handles Connection, transaction and prepared statement
    public <E> QueryResult<E> executeQuery(String query, QueryExecutor<E> queryExecutor) throws SQLException {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = DataSource.getConnection();
            preparedStatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            QueryResult<E> qr = new QueryResult<>(queryExecutor.execute(preparedStatement));

            con.commit();

            return qr;
        } catch (Exception e) {
            safeRollback(con);
            log.error("Unexpected exception", e);
            throw new SQLException(e);
        } finally {
            quietlyClose(preparedStatement);

            quietlyClose(con);
        }
    }

    //Same as executeQuery, but without correctly closing and rollbacking the provided connection
    public <E> QueryResult<E> executeQueryInConnection(Connection con, String query, QueryExecutor<E> queryExecutor) throws SQLException {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            return new QueryResult<>(queryExecutor.execute(preparedStatement));
        } catch (Exception e) {
            log.error("Unexpected exception", e);
            throw new SQLException(e);
        } finally {
            quietlyClose(preparedStatement);
        }
    }

    //Closes the prepared statement
    private static void quietlyClose(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                log.error("Unexpected exception", e);
            }
        }

    }

    //Closes the connection
    public static void quietlyClose(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("Unexpected exception", e);
            }
        }
    }

    //Rollback the connection
    public static void safeRollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e) {
                log.error("Unexpected exception", e);
            }
        }
    }

    
    //Interface for the implemented logic that'll be used in executeQuery
    public interface QueryExecutor<T> {
        T execute(PreparedStatement preparedStatement) throws SQLException;
    }

    
    //Generic class for the result provided by the QueryExecutor
    public static class QueryResult<T> {
        private T result;

        public QueryResult(T result) {
            this.result = result;
        }

        public T getResult() {
            return result;
        }
    }

    //Implemented query executor to ease creating objects and updating its ID
    public static class CreationQueryExecutor<T extends ObjectWithId> implements QueryExecutor<T> {
        private T object;
        private BiConsumer<PreparedStatement, T> fillInPreparedStatement;

        public CreationQueryExecutor(T object, BiConsumer<PreparedStatement, T> fillInPreparedStatement) {
            this.object = object;
            this.fillInPreparedStatement = fillInPreparedStatement;
        }

        @Override
        public T execute(PreparedStatement preparedStatement) throws SQLException {
            fillInPreparedStatement.accept(preparedStatement, object);

            int res = preparedStatement.executeUpdate();

            Long obtainedId = null;

            if (res != 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        obtainedId = generatedKeys.getLong(1);
                    }
                }
            }

            if (obtainedId == null) {
                return null;
            }

            object.setId(obtainedId);

            return object;

        }
    }
}