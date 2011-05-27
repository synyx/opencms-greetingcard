/*
 * This file is part of the Synyx Greetingcard module for OpenCms.
 *
 * Copyright (c) 2007 Synyx GmbH & Co.KG (http://www.synyx.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


package com.synyx.greetingcard.dao;

import com.synyx.greetingcard.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dao for accessing the whitelist information.
 * @author Florian Hopf, Synyx GmbH &amp; Co. KG, hopf@synyx.de
 */
public class JdbcWhitelistDao implements WhitelistDao {
    
    private String connection = null;
    private String user = null;
    private String pass = null;
    private String tableName = null;
    private String column = null;
    
    
    private Log log = LogFactory.getLog(JdbcWhitelistDao.class);

    public JdbcWhitelistDao(GreetingcardConfig config) {
        this.connection = config.getWhitelistConnection();
        this.user = config.getWhitelistUser();
        this.pass = config.getWhitelistPass();
        this.tableName = config.getWhitelistTableName();
        this.column = config.getWhitelistColumnName();
    }
    
    /** Creates a new instance of WhitelistDao */
    public JdbcWhitelistDao(String connection, String user, String pass, String tableName, String column) {
        this.connection = connection;
        this.user = user;
        this.pass = pass;
        this.tableName = tableName;
        this.column = column;
    }
    
    /**
     * Returns a List of all domains configured in the whitelist.
     * This method returns an empty List if connection to the database fails.
     * @return a List of the domains
     */
    public List<String> getWhitelist() throws DataAccessException {
        List<String> domains = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connection, user, pass);
            if (conn != null) { 
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + tableName);
                ResultSet rs = stmt.executeQuery();
                while(rs.next()) {
                    domains.add(rs.getString(column));
                }
            } else {
                throw new DataAccessException("Couldn't connect to whitelist database");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Accessing whitelist DB failed", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    log.error("Closing connection failed", ex);
                }
            }
        }
        return domains;
    }
    
    /**
     * Returns whether the given value is a domain configured in the db.
     * @param domain the domain to check in the db
     * @return contained a boolean
     */
    public boolean contains(String domain) throws DataAccessException {
        boolean contained = false;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connection, user, pass);
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE " + column + "=?");
                stmt.setString(1, domain);
                ResultSet rs = stmt.executeQuery();
                contained = rs.next();
            } else {
                throw new DataAccessException("Couldn't connect to whitelist database");
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    log.error("Couldn't close connection for internal check", ex);
                }
            }
        }
        return contained;
    }
}
