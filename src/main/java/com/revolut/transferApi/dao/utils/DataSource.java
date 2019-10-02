package com.revolut.transferApi.dao.utils;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;


public class DataSource {
    private static final Logger log = LoggerFactory.getLogger(DataSource.class);

//    private static final String DB_URL= "jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:schema.sql'\\;RUNSCRIPT FROM 'classpath:initialData.sql';TRACE_LEVEL_FILE=4";
    private static final String DB_URL= "jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:schema.sql';TRACE_LEVEL_FILE=4";
    private static final String USER = "admin";
    private static final String PASS = "Qp8GRgG2jCumZy4Q";

    
    private static final HikariDataSource ds;

    static {
        ds = new HikariDataSource();
        ds.setJdbcUrl(DB_URL);
        ds.setUsername(USER);
        ds.setPassword(PASS);
        ds.setAutoCommit(false);

        log.info("The database has been initialized");
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
            return ds.getConnection();
    }
}