package org.csstudio.mps.sns.tools.database;

import org.csstudio.mps.sns.tools.database.DatabaseAdaptor;
import java.sql.*;
import javax.sql.DataSource;


/**
 * Provides a super class for all implementations of 
 * <CODE>DatabaseAdaptor</CODE> that center around the <CODE>DataSource</CODE>
 * object introduced in JDK 1.4.
 * 
 * @author Chris Fowlkes
 */
public abstract class CachingDatabaseAdaptor extends DatabaseAdaptor
{
  /**
   * Allows the user to interact directly with the JDBC data source.
   * 
   * @return The <CODE>DataSource</CODE> held by the adaptor.
   */
  abstract public DataSource getDataSource();
  
  /**
   * Convenience method to get a new <CODE>Connection</CODE> using the 
   * credentials already supplied.
   * 
   * @return A <CODE>Connection</CODE> to the database.
   */
  abstract public Connection getConnection() throws org.csstudio.mps.sns.tools.database.DatabaseException;
  
  /**
   * Convenience method to get a <CODE>Connection</CODE> using the given
   * credentials.
   * 
   * @return A <CODE>Connection</CODE> to the database.
   */
  abstract public Connection getConnection(String user, String password) throws org.csstudio.mps.sns.tools.database.DatabaseException;
  
  /**
   * Returns the roles to which a user belongs. If user roles are not supported
   * by the RDB, an empty array should be returned.
   * 
   * @return The database roles to which a user belongs.
   * @throws org.csstudio.mps.sns.tools.database.DatabaseException Thrown on database error.
   */
  abstract public String[] getUserRoles() throws org.csstudio.mps.sns.tools.database.DatabaseException;
}

