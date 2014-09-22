package org.csstudio.mps.sns.apps.templateimport;

import org.csstudio.mps.sns.application.Parser;
import java.io.*;

import java.math.*;

import java.sql.*;

import java.util.*;
import java.util.regex.*;
import org.csstudio.mps.sns.tools.data.ArchiveRequest;
import org.csstudio.mps.sns.tools.data.ArchiveGroup;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.tools.data.SignalField;
import org.csstudio.mps.sns.tools.data.EpicsRecordType;
import org.csstudio.mps.sns.tools.data.Template;
import org.csstudio.mps.sns.tools.data.SignalType;
import org.csstudio.mps.sns.tools.data.SignalFieldType;
import org.csstudio.mps.sns.view.MPSBrowserView;


/**
 * Provides a class for parsing template files.
 * 
 * @author Chris Fowlkes
 */
public class TemplateParser extends Parser
{
  /**
   * Holds the tamplate files that are to be parsed.
   */
  private File[] templateFiles;
  /**
   * Holds the instances of <CODE>Template</CODE> found by invoking the 
   * <CODE>parse()</CODE> method.
   */
  private ArrayList templates = new ArrayList();
  /**
   * Holds the number of fields found by the <CODE>parse()</CODE> method.
   */
  private int importedFieldCount = 0;
  /**
   * Holds the number of macros found by the <CODE>parse()</CODE> method.
   */
  private int importedMacroCount = 0;
  /**
   * Holds the instances of <CODE>ArchiveRequest</CODE> found by the 
   * <CODE>parseFiles()</CODE> method.
   */
  private HashMap archiveRequests;
  /**
   * Holds the instances of <CODE>ArchiveGroup</CODE> found by the 
   * <CODE>parseFiles()</CODE> method.
   */
  private HashMap archiveGroups;
  /**
   * The <CODE>Pattern</CODE> used to extract an archive tag from a line. The
   * format of the arReq tag is # @arReq signl_id <arch_freq> <arch_type> 
   * if only one parameter then arch_freq is null and arch_type is the database 
   * default. If two then it is the arch_freq if numeric. (<> - indicates 
   * optional).
   */
  private Pattern archivePattern = Pattern.compile("#\\s*@\\s*arReq\\s+(\\S+)(?:\\s+(\\d+))*(?:\\s+(\\S+))*");
  /**
   * The <CODE>Pattern</CODE> used to extract the archive request file.
   */
  private Pattern archiveRequestPattern = Pattern.compile("#\\s*@\\s*arFile\\s+(\\S+)");
  /**
   * The <CODE>Pattern</CODE> used to extract the archive group file.
   */
  private Pattern archiveGroupPattern = Pattern.compile("#\\s*@\\s*arGroup\\s+(\\S+)");
  /**
   * The <CODE>Pattern</CODE> that looks for 'record(type, "signalname")'.
   */
  Pattern recordPattern = Pattern.compile("record\\s*\\(\\s*([^\\s,]+)\\s*,\\s*\\\"([^\\\"]+)\\\"\\s*\\)");
  /**
   * The <CODE>Pattern</CODE> that looks for 'field(fieldName, "value")'.
   */
  Pattern fieldPattern = Pattern.compile("field\\s*\\(\\s*([^\\s,]+)\\s*,\\s*\\\"([^\\\"]+)\\\"\\s*\\)");
  /**
   * The <CODE>Pattern</CODE> that looks for '$(macro)'.
   */
  Pattern macroPattern = Pattern.compile("(\\$\\(\\w+\\))");
  
  /**
   * Creates a new <CODE>TemplateParser</CODE>.
   */
  public TemplateParser()
  {
  }

  /**
   * Sets the files that will be parsed.
   * 
   * @param newDBFiles An array containing the instances of <CODE>File</CODE> reflecting the template files to import.
   */
  public void setTemplateFiles(File[] newTemplateFiles)
  {
    templateFiles = newTemplateFiles;
  }

  /**
   * Gets the instances of <CODE>File</CODE> that will be parsed.
   * 
   * @return The files to parse.
   */
  public File[] getTemplateFiles()
  {
    return templateFiles;
  }

  /**
   * Parses the files passed into the <CODE>setTemplateFiles</CODE> method. The 
   * data extracted from the template files is returned.
   */
  public void parse()
  {
    setMessage("Parsing Files");
    templates.clear();
    importedFieldCount = 0;
    importedMacroCount = 0;
    File[] templateFiles = getTemplateFiles();
    resetParseCanceled();
    int totalFileSize = 0;
    for(int i=0;i<templateFiles.length;i++)
      if(templateFiles[i].exists())
        totalFileSize += (int)templateFiles[i].length();
    setProgressMaximum(totalFileSize);
    int progress = 0;
    setProgressValue(0);
    setProgressIndeterminate(false);
    for(int i=0;i<templateFiles.length;i++)
    {
      String currentFilePath = templateFiles[i].getAbsolutePath();
      Timestamp modifiedDate = new Timestamp(templateFiles[i].lastModified());
      Template currentTemplate = new Template(currentFilePath, modifiedDate);
      String[] nameParts = templateFiles[i].getName().split("\\.");
      if(nameParts != null && nameParts.length > 0)
        currentTemplate.setID(nameParts[0]);
      templates.add(currentTemplate);
      try
      {
        BufferedReader iStream = new BufferedReader(new FileReader(templateFiles[i]));
        try
        {
          String currentLine = iStream.readLine();
          Signal currentSignal = null, archiveTag = null;
          ArchiveRequest request = null;
          ArchiveGroup group = null;
          HashMap archiveSignals = new HashMap();
          int lineNumber = 0;
          int braceCount = 0;
          while(currentLine != null)//null indicates EOF
          {
            lineNumber++;
            if(currentLine.trim().startsWith("#"))//Comments start with #
            {
              //Comments start with #. Archive information is embedded in comments.
              ArchiveGroup newGroup = parseArchiveGroupTag(currentLine, currentTemplate);
              if(newGroup != null)
                group = newGroup;
              else
              {
                ArchiveRequest newRequest = parseArchiveRequestTag(currentLine, group, currentTemplate);
                if(newRequest != null)
                  request = newRequest;
                else
                {
                  Signal newArchiveTag = parseArchiveTag(currentLine);
                  if(newArchiveTag != null)
                  {
                    if(archiveTag != null)//Tag was not used in request. Use for defaults.
                      archiveSignals.put(archiveTag.getID(), archiveTag);
                    archiveTag = newArchiveTag;
                  }
                }
              }
            }
            else
            {
              Matcher macroMatcher = macroPattern.matcher(currentLine);
              if(macroMatcher.find())
              {
                String macro = macroMatcher.group(1);
                if(! currentTemplate.containsMacro(macro))
                {
                  importedMacroCount++;
                  currentTemplate.addMacro(macro);
                }
              }
              int linePosition = 0;
              int lineLength = currentLine.length();
              while(linePosition < lineLength)
              {
                int openBracePosition = currentLine.indexOf('{', linePosition);
                int closeBracePosition = currentLine.indexOf('}', linePosition);
                if(currentSignal == null || braceCount == 0)
                {
                  //Got no signal or the brace was never opened...
                  Matcher recordMatcher = recordPattern.matcher(currentLine);
                  if(recordMatcher.find(linePosition))
                    if(openBracePosition < 0 || recordMatcher.start() < openBracePosition)
                    {
                      linePosition = recordMatcher.end();
                      SignalType currentSignalType = new SignalType();
                      String recordType = recordMatcher.group(1);
                      currentSignalType.setRecordType(new EpicsRecordType(recordType));
                      String signalID = recordMatcher.group(2);
                      currentSignal = new Signal(signalID);
                      currentSignal.setType(currentSignalType);
                      if(archiveTag != null)
                        archiveSignals.put(archiveTag.getID(), archiveTag);//Use as defaults.
                      archiveTag = (Signal)archiveSignals.get(signalID);
                      if(archiveTag != null)
                      {
                        currentSignal.setArchiveIndicator("Y");
                        currentSignal.setArchiveType(archiveTag.getArchiveType());
                        currentSignal.setArchiveFrequency(archiveTag.getArchiveFrequency());
                        //Must use a new instance of signal since each request has different
                        //values for type, frequency, etc.
                        if(request != null && request.getSignal(signalID) == null)
                          request.addSignal(new Signal(signalID));
                        currentSignal.setArchiveIndicator("Y");
                        currentSignal.setArchiveType(archiveTag.getArchiveType());
                        currentSignal.setArchiveFrequency(archiveTag.getArchiveFrequency());
                      }
                      currentTemplate.addSignal(currentSignal);
                      archiveTag = null;//Reset so is not used twice.
                      continue;//Go back and check the line position against length.
                    }
                }
                if(braceCount == 0 && currentSignal != null && openBracePosition >= linePosition)
                {
                  //Got the signal, need the open brace.
                  linePosition = openBracePosition + 1;
                  braceCount++;
                  continue;//Go back and check the line position against length.
                }
                if(braceCount > 0)
                {
                  //Looking for fields or the close brace.
                  Matcher fieldMatcher = fieldPattern.matcher(currentLine);
                  if(fieldMatcher.find(linePosition))
                    if(closeBracePosition < 0 || fieldMatcher.start() < closeBracePosition)
                    {
                      //Found a field...
                      linePosition = fieldMatcher.end();
                      SignalField currentField = new SignalField();
                      String currentFieldID = fieldMatcher.group(1);
                      currentField.setType(new SignalFieldType(currentFieldID));
                      currentField.setValue(fieldMatcher.group(2));
                      currentSignal.addField(currentField);
                      importedFieldCount++;
                      continue;
                    }
                  if(closeBracePosition >= 0)
                  {
                    //Found end of current signal.
                    braceCount--;
                    linePosition = closeBracePosition + 1;
                    currentSignal = null;
                    continue;
                  }
                }
                linePosition = lineLength;
                if(isParseCanceled())
                  break;
              }
            }
            progress += currentLine.length() + 1;
            setProgressValue(progress);
            currentLine = iStream.readLine();
            if(isParseCanceled())
              break;
          }
        }
        finally
        {
          iStream.close();
        }
      }
      catch(java.io.FileNotFoundException ex)
      {
        StringBuffer errorMessage = new StringBuffer("<HTML><FONT COLOR=RED>Unable to open file '");
        errorMessage.append(templateFiles[i].getAbsoluteFile());
        errorMessage.append("'.</FONT></HTML>");
        addMessage(errorMessage.toString());
      }
      catch(java.io.IOException ex)
      {
        ex.printStackTrace();
        StringBuffer errorMessage = new StringBuffer("<HTML><FONT COLOR=RED>IO Error: ");
        errorMessage.append(ex.getMessage());
        errorMessage.append("</FONT></HTML>");
        addMessage(errorMessage.toString());
      }
      if(isParseCanceled())
        break;
    }
  }

  /**
   * Gets the instances of <CODE>Template</CODE> created from the files. The 
   * instances of <CODE>Template</CODE> are created by calling the 
   * <CODE>parse</CODE> method.
   * 
   * @return An <CODE>ArrayList</CODE> containing the instances of <CODE>Template</CODE> created when the <CODE>parse</CODE> method was last called.
   */
  public ArrayList getTemplates()
  {
    return templates;
  }

  /**
   * Gets the instances of <CODE>Template</CODE> that have an ID that is already 
   * in the database.
   * 
   * @return An <CODE>ArrayList</CODE> containing the instances of <CODE>Template</CODE> already in the database.
   * @throws java.sql.SQLException Thrown on sql exception.
   */
  public ArrayList findTemplatesInDatabase() throws java.sql.SQLException
  {
    ArrayList templatesInDatabase = new ArrayList();
    Connection oracleConnection = getDataSource().getConnection();
    try
    {
      Statement query = oracleConnection.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT TMPL_ID FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TEMPLATE WHERE TMPL_ID IN (");
        ArrayList templates = getTemplates();
        int templateCount = templates.size();
        for(int i=0;i<templateCount;i++)
        {
          if(i > 0)
            sql.append(", ");
          sql.append("'");
          sql.append(((Template)templates.get(i)).getID());
          sql.append("'");
        }
        sql.append(")");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          while(result.next())
          {
            String templateID = result.getString("TMPL_ID");
            for(int i=0;i<templateCount;i++)
            {
              Template currentTemplate = (Template)templates.get(i);
              if(templateID.equals(currentTemplate.getID()))
              {
                templatesInDatabase.add(currentTemplate);
                currentTemplate.setInDatabase(true);
              }
            }
          }
        }
        finally
        {
          result.close();
        }
      }
      finally
      {
        query.close();
      }
    }
    finally
    {
      oracleConnection.close();
    }
    return templatesInDatabase;
  }

  /**
   * Saves the templates to the database.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void saveToDatabase() throws java.sql.SQLException
  {
    setProgressIndeterminate(true);
    setMessage("Saving Templates");
    ArrayList templates = getTemplates();
    Connection oracleConnection = getDataSource().getConnection();
    try
    {
      oracleConnection.setAutoCommit(false);
      Statement query = oracleConnection.createStatement();
      try
      {
        int templateCount = templates.size();
        //First remove any existing entries.
        StringBuffer sql = new StringBuffer("DELETE FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TMPL_SGNL_FLD");
        StringBuffer whereClause = new StringBuffer(" WHERE TMPL_ID IN (");
        for(int i=0;i<templateCount;i++)
        {
          if(i > 0)
            whereClause.append(", ");
          whereClause.append("'");
          whereClause.append(((Template)templates.get(i)).getID());
          whereClause.append("'");
        }
        whereClause.append(")");
        sql.append(whereClause);
        query.execute(sql.toString());
        sql = new StringBuffer("DELETE FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TMPL_MACRO");
        sql.append(whereClause);
        query.execute(sql.toString());
        sql = new StringBuffer("DELETE FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TMPL_SGNL_REC");
        sql.append(whereClause);
        query.execute(sql.toString());
        sql = new StringBuffer("DELETE FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TMPL_SGNL_FLD");
        sql.append(whereClause);
        query.execute(sql.toString());
        sql = new StringBuffer("DELETE FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TEMPLATE");
        sql.append(whereClause);
        query.execute(sql.toString());
        sql = new StringBuffer("DELETE FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TMPL_ARCH_REQ");
        sql.append(whereClause);
        query.execute(sql.toString());
        sql = new StringBuffer("DELETE FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TMPL_ARCH_REQ_GRP");
        sql.append(whereClause);
        query.execute(sql.toString());
        sql = new StringBuffer("DELETE FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TMPL_ARCH_REQ_GRP_ARCH_REQ");
        sql.append(whereClause);
        query.execute(sql.toString());
        sql = new StringBuffer("INSERT INTO ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TEMPLATE (TMPL_ID, TMPL_DESC, EXT_SRC_FILE_NM, EXT_SRC_FILE_MOD_DTE) VALUES (?, ?, ?, ?)");
        PreparedStatement templateInsertStatement = oracleConnection.prepareStatement(sql.toString());
        try
        {
          sql = new StringBuffer("INSERT INTO ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".TMPL_MACRO (TMPL_ID, MACRO_ID) VALUES (?, ?)");
          PreparedStatement macroInsertStatement = oracleConnection.prepareStatement(sql.toString());
          try
          {
            sql = new StringBuffer("INSERT INTO ");
            sql.append(MPSBrowserView.SCHEMA);
            sql.append(".TMPL_SGNL_REC (TMPL_ID, TMPL_SGNL_ID, REC_TYPE_ID, ARCH_IND, ARCH_FREQ, ARCH_TYPE) VALUES (?, ?, ?, ?, ?, ?)");
            PreparedStatement signalInsertStatement = oracleConnection.prepareStatement(sql.toString());
            try
            {
              sql = new StringBuffer("INSERT INTO ");
              sql.append(MPSBrowserView.SCHEMA);
              sql.append(".TMPL_SGNL_FLD (TMPL_ID, TMPL_SGNL_ID, FLD_ID, REC_TYPE_ID, VAL) VALUES (?, ?, ?, ?, ?)");
              PreparedStatement fieldInsertStatement = oracleConnection.prepareStatement(sql.toString());
              try
              {
                sql = new StringBuffer("INSERT INTO ");
                sql.append(MPSBrowserView.SCHEMA);
                sql.append(".TMPL_ARCH_REQ (TMPL_ID, ARCH_REQ_FILE_NM) VALUES (?, ?)");
                PreparedStatement requestInsertStatement = oracleConnection.prepareStatement(sql.toString());
                try
                {
                  sql = new StringBuffer("INSERT INTO ");
                  sql.append(MPSBrowserView.SCHEMA);
                  sql.append(".TMPL_ARCH_REQ_GRP (TMPL_ID, ARCH_REQ_GRP_FILE_NM) VALUES (?, ?)");
                  PreparedStatement groupInsertStatement = oracleConnection.prepareStatement(sql.toString());
                  try
                  {
                    sql = new StringBuffer("INSERT INTO ");
                    sql.append(MPSBrowserView.SCHEMA);
                    sql.append(".TMPL_ARCH_REQ_GRP_ARCH_REQ (TMPL_ID, ARCH_REQ_GRP_FILE_NM, ARCH_REQ_FILE_NM) VALUES (?, ?, ?)");
                    PreparedStatement requestGroupInsertStatement = oracleConnection.prepareStatement(sql.toString());
                    try
                    {
                      sql = new StringBuffer("UPDATE ");
                      sql.append(MPSBrowserView.SCHEMA);
                      sql.append(".TMPL_SGNL_REC SET ARCH_IND = ?, ARCH_FREQ = ?, ARCH_TYPE = ?, ARCH_REQ_FILE = ? WHERE TMPL_ID = ? AND TMPL_SGNL_ID = ? AND REC_TYPE_ID = ?");
                      PreparedStatement signalUpdateStatement = oracleConnection.prepareStatement(sql.toString());
                      try
                      {
                        int progress = 0;
                        setProgressMaximum(importedFieldCount + importedMacroCount);
                        setProgressValue(0);
                        setProgressIndeterminate(false);
                        for(int templateIndex=0;templateIndex<templateCount;templateIndex++)
                        {
                          Template currentTemplate = (Template)templates.get(templateIndex);
                          String currentTemplateID = currentTemplate.getID();
                          templateInsertStatement.setString(1, currentTemplateID);
                          String currentDescription = currentTemplate.getDescription();
                          if(currentDescription == null)
                            templateInsertStatement.setNull(2, Types.VARCHAR);
                          else
                            templateInsertStatement.setString(2, currentDescription);
                          templateInsertStatement.setString(3, currentTemplate.getFileName());
                          templateInsertStatement.setTimestamp(4, currentTemplate.getFileModifiedDate());
                          templateInsertStatement.execute();
                          //Need to insert macros.
                          int macroCount = currentTemplate.getMacroCount();
                          for(int macroIndex=0;macroIndex<macroCount;macroIndex++)
                          {
                            macroInsertStatement.setString(1, currentTemplateID);
                            String currentMacro = currentTemplate.getMacroAt(macroIndex);
                            macroInsertStatement.setString(2, currentMacro);
                            macroInsertStatement.execute();
                            setProgressValue(++progress);
                          }
                          int signalCount = currentTemplate.getSignalCount();
                          for(int signalIndex=0;signalIndex<signalCount;signalIndex++)
                          {
                            Signal currentSignal = currentTemplate.getSignalAt(signalIndex);
                            String currentSignalID = currentSignal.getID();
                            String currentRecordTypeID = currentSignal.getType().getRecordType().getID();
                            signalInsertStatement.setString(1, currentTemplateID);
                            signalInsertStatement.setString(2, currentSignalID);
                            signalInsertStatement.setString(3, currentRecordTypeID);
                            signalInsertStatement.setString(4, currentSignal.getArchiveIndicator());
                            BigDecimal currentFrequency = currentSignal.getArchiveFrequency();
                            if(currentFrequency == null)
                              currentFrequency = new BigDecimal("60");//60 default in RDB
                            signalInsertStatement.setBigDecimal(5, currentFrequency);
                            String currentType = currentSignal.getArchiveType();
                            if(currentType == null)
                              currentType = "Monitor";//'Monitor' default in RDB
                            signalInsertStatement.setString(6, currentType);
                            signalInsertStatement.execute();
                            int fieldCount = currentSignal.getFieldCount();
                            for(int fieldIndex=0;fieldIndex<fieldCount;fieldIndex++)
                            {
                              SignalField currentField = currentSignal.getFieldAt(fieldIndex);
                              fieldInsertStatement.setString(1, currentTemplateID);
                              fieldInsertStatement.setString(2, currentSignalID);
                              fieldInsertStatement.setString(3, currentField.getType().getID());
                              fieldInsertStatement.setString(4, currentRecordTypeID);
                              fieldInsertStatement.setString(5, currentField.getValue());
                              fieldInsertStatement.execute();
                              if(isParseCanceled())
                              {
                                oracleConnection.rollback();
                                return;
                              }
                              setProgressValue(++progress);
                            }
                          }
                          //Insert archive requests.
                          int requestCount = currentTemplate.getArchiveRequestCount();
                          for(int requestIndex = 0;requestIndex<requestCount;requestIndex++)
                          {
                            ArchiveRequest currentRequest = currentTemplate.getArchiveRequestAt(requestIndex);
                            String currentRequestFileName = currentRequest.getFileName();
                            requestInsertStatement.setString(1, currentTemplateID);
                            requestInsertStatement.setString(2, currentRequestFileName);
                            requestInsertStatement.execute();
                            signalCount = currentRequest.getSignalCount();
                            for(int signalIndex=0;signalIndex<signalCount;signalIndex++)
                            {
                              Signal currentSignal = currentRequest.getSignalAt(signalIndex);
                              String currentSignalID = currentSignal.getID();
                              signalUpdateStatement.setString(1, currentSignal.getArchiveIndicator());
                              signalUpdateStatement.setBigDecimal(2, currentSignal.getArchiveFrequency());
                              signalUpdateStatement.setString(3, currentSignal.getArchiveType());
                              signalUpdateStatement.setString(4, currentRequestFileName);
                              signalUpdateStatement.setString(5, currentTemplateID);
                              signalUpdateStatement.setString(6, currentSignalID);
                              signalUpdateStatement.setString(7, currentSignal.getType().getRecordType().getID());
                              signalUpdateStatement.execute();    
                              int fieldCount = currentSignal.getFieldCount();
                            }
                          }
                          //Insert archive groups.
                          int groupCount = currentTemplate.getArchiveGroupCount();
                          for(int groupIndex = 0;groupIndex<groupCount;groupIndex++)
                          {
                            ArchiveGroup currentGroup = currentTemplate.getArchiveGroupAt(groupIndex);
                            groupInsertStatement.setString(1, currentTemplateID);
                            String currentGroupFileName = currentGroup.getFileName();
                            groupInsertStatement.setString(2, currentGroupFileName);
                            groupInsertStatement.execute();
                            requestCount = currentGroup.getArchiveRequestCount();
                            for(int requestIndex = 0;requestIndex < requestCount;requestIndex++)
                            {
                              ArchiveRequest currentRequest = currentGroup.getArchiveRequestAt(requestIndex);
                              String currentRequestFileName = currentRequest.getFileName();
                              requestGroupInsertStatement.setString(1, currentTemplateID);
                              requestGroupInsertStatement.setString(2, currentGroupFileName);
                              requestGroupInsertStatement.setString(3, currentRequest.getFileName());
                            }
                          }
                        }
                      }
                      finally
                      {
                        signalUpdateStatement.close();
                      }
                    }
                    finally
                    {
                      requestGroupInsertStatement.close();
                    }
                  }
                  finally
                  {
                    groupInsertStatement.close();
                  }
                }
                finally
                {
                  requestInsertStatement.close();
                }
              }
              finally
              {
                fieldInsertStatement.close();
              }
            }
            finally
            {
              signalInsertStatement.close();
            }
          }
          finally
          {
            macroInsertStatement.close();
          }
        }
        finally
        {
          templateInsertStatement.close();
        }
      }
      catch(java.sql.SQLException ex)
      {
        oracleConnection.rollback();
        throw ex;
      }
      finally
      {
        query.close();
      }
      if(isParseCanceled())
        oracleConnection.rollback();
      else
        oracleConnection.commit();
    }
    finally
    {
      oracleConnection.close();
    }
  }
  
  /**
   * Takes a line and looks for an archive tag. If one is found, the information 
   * from the tag is returned as a <CODE>Signal</CODE>. If a signal ID is given
   * in the tag, the data in the tag is stored in the <CODE>HashMap</CODE> 
   * provided.
   * 
   * @param currentLine The line in which to look for the archive tag.
   * @return The archive tag data as an instance of <CODE>Signal</CODE>, <CODE>null</CODE> if the line passed in does not contain an arReq tag.
   */
  private Signal parseArchiveTag(String currentLine)
  {
    Signal archiveTag;
    Matcher currentMatcher = archivePattern.matcher(currentLine);
    if(currentMatcher.find())
    {
      archiveTag = new Signal(currentMatcher.group(1));
      String frequency = currentMatcher.group(2);
      BigDecimal frequencyValue;
      if(frequency == null)
        frequencyValue = null;
      else
        frequencyValue = new BigDecimal(frequency);
      archiveTag.setArchiveFrequency(frequencyValue);
      String type = currentMatcher.group(3);
      if(type == null)
        type = "Monitor";
      archiveTag.setArchiveType(type);
      archiveTag.setArchiveIndicator("Y");
    }
    else
      archiveTag = null;
    return archiveTag;
  }

  /**
   * Takes a line and looks for an archive request tag. If one is found, the 
   * information from the tag is returned as an <CODE>ArchiveRequest</CODE>. If 
   * a tag is found, the data in the tag is stored in the <CODE>HashMap</CODE> 
   * provided.
   * 
   * @param currentLine The line in which to look for the archive tag.
   * @param group The <CODE>ArchiveGroup</CODE> to which to add the <CODE>ArchiveRequest</CODE>. Can be <CODE>null</CODE>.
   * @param template The <CODE>Template</CODE> to which the <CODE>ArchiveRequest</CODE> belongs.
   * @return The archive request tag data as an instance of <CODE>ArchiveRequest</CODE>, <CODE>null</CODE> if the line passed in does not contain an arFile tag.
   */
  private ArchiveRequest parseArchiveRequestTag(String currentLine, ArchiveGroup group, Template template)
  {
    ArchiveRequest archiveTag;
    Matcher currentMatcher = archiveRequestPattern.matcher(currentLine);
    if(currentMatcher.find())
    {
      String fileName = currentMatcher.group(1);
      archiveTag = template.getArchiveRequest(fileName);
      if(archiveTag == null)
      {
        archiveTag = new ArchiveRequest(fileName);
        template.addArchiveRequest(archiveTag);
      }
      if(group != null)
      {
        //Creating a new request object because the requests
        //in the groups flagged as not in database will mean the 
        //group - request association is not in database. Requests
        //in the archiveRequests map flagged as not in database
        //means the request record is not there.
        String requestFileName = archiveTag.getFileName();
        String requestFileLocation = archiveTag.getFileLocation();
        ArchiveRequest groupRequest = new ArchiveRequest(requestFileLocation, requestFileName);
        group.addArchiveRequest(groupRequest);
      }
    }
    else
      archiveTag = null;
    return archiveTag;
  }

  /**
   * Takes a line and looks for an archive group tag. If one is found, the 
   * information from the tag is returned as an <CODE>ArchiveGroup</CODE>. If 
   * a tag is found, the data in the tag is stored in the <CODE>HashMap</CODE> 
   * provided.
   * 
   * @param currentLine The line in which to look for the archive tag.
   * @param template The <CODE>Template</CODE> to which the <CODE>ArchiveGroup</CODE> belongs.
   * @return The archive request tag data as an instance of <CODE>ArchiveGroup</CODE>, <CODE>null</CODE> if the line passed in does not contain an arFile tag.
   */
  private ArchiveGroup parseArchiveGroupTag(String currentLine, Template template)
  {
    ArchiveGroup archiveTag;
    Matcher currentMatcher = archiveRequestPattern.matcher(currentLine);
    if(currentMatcher.find())
    {
      String fileName = currentMatcher.group(1);
      archiveTag = template.getArchiveGroup(fileName);
      if(archiveTag == null)
      {
        File groupFile = new File(fileName);
        archiveTag = new ArchiveGroup(groupFile.getParent(), groupFile.getName());
        template.addArchiveGroup(archiveTag);
      }
    }
    else
      archiveTag = null;
    return archiveTag;
  }
}