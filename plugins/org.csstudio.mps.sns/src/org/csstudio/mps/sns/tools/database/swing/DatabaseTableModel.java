package org.csstudio.mps.sns.tools.database.swing;

import javax.swing.table.*;

public interface DatabaseTableModel extends TableModel
{
  /**
   * Called when the commit button in the tool bar is clicked.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public abstract void commit() throws java.sql.SQLException;
  /**
   * Called when the rollback button in the tool bar is clicked.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public abstract void rollback() throws java.sql.SQLException;
//  /**
//   * Called when the filter button in the tool bar is clicked.
//   * 
//   * @throws java.sql.SQLException Thrown on sql error.
//   */
//  public abstract void filter() throws java.sql.SQLException;
//  /**
//   * Called when the remove filter button in the tool bar is clicked.
//   * 
//   * @throws java.sql.SQLException Thrown on sql error.
//   */
//  public abstract void removeFilter() throws java.sql.SQLException;
  /**
   * Called when the insert row button in the tool bar is clicked.
   */
  public abstract void insert();
  /**
   * Called when the delete row button in the tool bar is clicked.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public abstract void delete(int[] rows) throws java.sql.SQLException;
  /**
   * Called when the post button in the tool bar is clicked.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public abstract void post() throws java.sql.SQLException;
  /**
   * Called when the cancel button in the tool bar is clicked.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public abstract void cancel() throws java.sql.SQLException;
  /**
   * Called when the refresh button in the tool bar is clicked.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public abstract void refresh() throws java.sql.SQLException;
  /**
   * Used to determine if the data is filtered or not.
   * 
   * @return <CODE>true</CODE> if the data has been filtered, <CODE>false</CODE> otherwise.
   */
  public abstract boolean isFiltered();
  /**
   * Used to determine if the commit and rollback button should be enabled.
   * 
   * @return <CODE>true</CODE> if changes are pending, <CODE>false</CODE> if not.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public abstract boolean isCommitNeeded() throws java.sql.SQLException;
  /**
   * Used to determine if the data has been changed. This information is used to
   * determine if the post and cancel button should be enabled.
   * 
   * @return <CODE>true</CODE> if changes to the data are pending, <CODE>false</CODE> if not.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public abstract boolean isChanged() throws java.sql.SQLException;
}