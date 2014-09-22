package org.csstudio.mps.sns.tools.swing;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.csstudio.mps.sns.tools.data.Template;

/**
 * Provides a model for the <CODE>JTable</CODE> in the 
 * <CODE>TemplateImportDialog</CODE>.
 * 
 * @author Chris Fowlkes
 */
public class TemplateTableModel extends AbstractTableModel
{
  /**
   * Holds the instances of <CODE>Template</CODE> that comprise the data in the 
   * model.
   */
  private Template[] templates;
  /**
   * Holds the value of the editable property. This determines if the ID and 
   * description fields are editable. <CODE>true</CODE> by default.
   */
  private boolean editable = true;
  /**
   * Holds the value of the file details visible property. This determines if 
   * file name and modification date are visible. <CODE>true</CODE> by default.
   */
  private boolean fileDetailsVisible = true;
  
  /**
   * Creates a new <CODE>TemplateImportModel</CODE>.
   */
  public TemplateTableModel()
  {
  }

  /**
   * Gets the number of rows in the table.
   * 
   * @return The number of rows in the table.
   */
  public int getRowCount()
  {
    Template[] templates = getTemplates();
    if(templates == null)
      return 0;
    else
      return templates.length;
  }

  /**
   * returns the number of columns displayed in the table. This model has four 
   * columns.
   * 
   * @return The number of columns in the model, four.
   */
  public int getColumnCount()
  {
    if(isFileDetailsVisible())
      return 4;
    else
      return 2;
  }

  /**
   * Gets the value for the given cell.
   * 
   * @param rowIndex The row index of the cell for which to return the value.
   * @param columnIndex The column index of the cell for which to return the value.
   */
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    Template currentTemplate = getTemplateAt(rowIndex);
    Object currentValue;
    switch(columnIndex)
    {
      case(0):
        currentValue = currentTemplate.getID();
        break;
      case(1):
        currentValue = currentTemplate.getDescription();
        break;
      case(2):
        currentValue = currentTemplate.getFileName();
        break;
      case(3):
        currentValue = currentTemplate.getFileModifiedDate();
        break;
      default:
        throw new java.lang.IllegalArgumentException(columnIndex + " is not a valid column index.");
    }//switch(columnIndex)
    return currentValue;
  }

  /**
   * Sets the instances of <CODE>Template</CODE> to display in the table.
   * 
   * @param The instances of <CODE>Template</CODE> to display in the table.
   */
  public void setTemplates(Template[] newTemplates)
  {
    templates = newTemplates;
    fireTableDataChanged();
  }

  /**
   * Gets the name of the column at the given index.
   * 
   * @param column The index of the column of which to return the name.
   * @return The name of the column at the given index.
   */
  public String getColumnName(int column)
  {
    String columnName;
    switch(column)
    {
      case 0:
        columnName = "Template ID";
        break;
      case 1:
        columnName = "Description";
        break;
      case 2:
        columnName = "File Name";
        break;
      case 3:
        columnName = "File Date";
        break;
      default:
        throw new java.lang.IllegalArgumentException(column + " is not a valid column index.");
    }//switch(column)
    return columnName;
  }

  /**
   * Determines if the given cell is editable. Only cells in the last two 
   * columns are editable for this table.
   * 
   * @param rowIndex The row index of the cell.
   * @param columnIndex The column index of the cell.
   * @return <CODE>true</CODE> if the cell is editable, <CODE>false</CODE> if not.
   */
  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    boolean editable;
    switch(columnIndex)
    {
      case 0:
        editable = isEditable();
        break;
      case 1:
        editable = isEditable();
        break;
      case 2:
        editable = false;
        break;
      case 3:
        editable = false;
        break;
      default:
        throw new java.lang.IllegalArgumentException(columnIndex + " is not a valid column index.");
    }//switch(column)
    return editable;
  }

  /**
   * Sets the value for the given cell.
   * 
   * @param aValue The value to set for the given cell.
   * @param rowIndex The row index of the cell for which to set the value.
   * @param columnIndex The columnIndex of the cell for which to set the value.
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    Template currentTemplate = getTemplateAt(rowIndex);
    switch(columnIndex)
    {
      case(0):
        currentTemplate.setID(String.valueOf(aValue));
        break;
      case(1):
        currentTemplate.setDescription(String.valueOf(aValue));
        break;
      default:
        throw new java.lang.IllegalArgumentException(columnIndex + " is not the index of an editable column.");
    }//switch(columnIndex)
    fireTableCellUpdated(rowIndex, columnIndex);
  }

  /**
   * returns the <CODE>Template</CODE> represented by a given row.
   * 
   * @param rowIndex The index of the <CODE>Template</CODE> to return.
   * @return The <CODE>Template</CODE> at the given row.
   */
  public Template getTemplateAt(int rowIndex)
  {
    return getTemplates()[rowIndex];
  }

  /**
   * Gets the instances of <CODE>Template</CODE> displayed in the dialog.
   * 
   * @return The instances of <CODE>Template</CODE> displayed in the dialog.
   */
  public Template[] getTemplates()
  {
    return templates;
  }

  /**
   * Gets the value of the editable property. This determines if the ID and 
   * description fields are editable. <CODE>true</CODE> by default.
   * 
   * @return <CODE>true</CODE> if the ID and description fields are editable, <CODE>false</CODE> if not.
   */
  public boolean isEditable()
  {
    return editable;
  }

  /**
   * Sets the value of the editable property. This determines if the ID and 
   * description fields are editable. <CODE>true</CODE> by default.
   * 
   * @param newEditable <CODE>true</CODE> if the ID and description fields are editable, <CODE>false</CODE> if not.
   */
  public void setEditable(boolean newEditable)
  {
    editable = newEditable;
  }

  /**
   * Gets the value of the file details visible property. This determines if the 
   * file name and modified date are visible. <CODE>true</CODE> by default.
   * 
   * @return <CODE>true</CODE> if the file name and date fields are visible, <CODE>false</CODE> if not.
   */
  public boolean isFileDetailsVisible()
  {
    return fileDetailsVisible;
  }

  /**
   * Sets the value of the file details visible property. This determines if the 
   * file name and modified date are visible. <CODE>true</CODE> by default.
   * 
   * @param newFileDetailsVisible <CODE>true</CODE> if the file name and date fields are visible, <CODE>false</CODE> if not.
   */
  public void setFileDetailsVisible(boolean newFileDetailsVisible)
  {
    fileDetailsVisible = newFileDetailsVisible;
    fireTableStructureChanged();
  }
}