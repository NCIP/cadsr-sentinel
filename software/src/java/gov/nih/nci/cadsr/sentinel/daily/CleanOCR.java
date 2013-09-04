/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

package gov.nih.nci.cadsr.sentinel.daily;

import gov.nih.nci.cadsr.sentinel.tool.Constants;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class CleanOCR {
	
	    private static Logger       _logger     = Logger.getLogger(CleanOCR.class);

	    private String              _dsurl;

	    private String              _user;

	    private String              _pswd;

	    private Properties          _propList;

	    private Connection          _conn;

	    private static final String _sqlSelectOCR = "SELECT * FROM oc_recs_view_ext ocr " +
	    											"WHERE ocr.ocr_idseq NOT IN (SELECT acs.ac_idseq" +
	    											" FROM ac_csi_view acs)";
	    
	    private static final String _sqlDeleteOCR = "DELETE FROM oc_recs_view_ext " +
	    		"WHERE ocr_idseq IN " +
	    		"(SELECT ocr.ocr_idseq FROM oc_recs_view_ext ocr WHERE ocr.ocr_idseq NOT IN " +
	    		"(SELECT acs.ac_idseq FROM ac_csi_view acs))";	

	    /**
	     * @param args_
	     */
	    public static void main(String[] args_)
	    {
	        if (args_.length != 2)
	        {
	            System.err.println(CleanOCR.class.getName() + " log4j.xml config.xml");
	            return;
	        }

	        DOMConfigurator.configure(args_[0]);

	        CleanOCR ss = new CleanOCR();

	        try
	        {
	            _logger.info("");
	            _logger.info(CleanStrings.class.getClass().getName() + " begins");
	            ss.doAll(args_[1]);
	        }
	        catch (Exception ex)
	        {
	            _logger.error(ex.toString(), ex);
	        }
	    }
	    
	    /**
	     * A convenience method to do all the work.
	     * 
	     * @param propFile_ configuration profile
	     * @throws Exception
	     */
	    public void doAll(String propFile_) throws Exception
	    {
	        loadProp(propFile_);
	        open();

	        try
	        {
	            _logger.info("Do " + _sqlSelectOCR);
	            int resultSet = selectOCR();
	            if (resultSet > 0){	            	
	            	deleteOCR();
	            }
	            
	        }
	        catch (Exception ex)
	        {
	            _logger.error(ex.toString(), ex);
	        }
	        finally
	        {
	        	if (_conn != null)
		        {
		            _conn.close();
		            _conn = null;
		        }
	        }        
	    }

	    /**
	     * Select OCR to be deleted
	     * 
	     * @throws Exception
	     */
	    private int selectOCR() throws Exception
	    {
	        Statement cs = null;
	        ResultSet rs = null;
	        ResultSetMetaData rsMetaData = null;
	        int rsCount = 0;
	        try
	        {	        	
	            cs = _conn.createStatement();
	            rs = cs.executeQuery(_sqlSelectOCR);
	            while (rs.next()){
	            	if (rsCount == 0){
	            		rsMetaData = rs.getMetaData();
	            		String columnHeader = "";
	            		for(int i = 0; i< rsMetaData.getColumnCount(); ++i ){
	            			columnHeader = columnHeader + "\t" + rsMetaData.getColumnLabel(i+1);	            			
	            		}
	            		_logger.info(columnHeader);	            		
	            	}
	            	String dataRow = "" ;
	            	for (int j= 0; j< rsMetaData.getColumnCount(); ++j){
	            		dataRow = dataRow + "\t" + rs.getString(j+1);
	            	}
	            	_logger.info(dataRow);	            	
	            	rsCount++;
	            }
	            _logger.info(rsCount+", Records found for deleting.");
	        }
	        finally
	        {
	        	if (rs != null)
	                rs.close();
	            if (cs != null)
	                cs.close();
	            _logger.info("End " + _sqlSelectOCR);
	        }
	        return rsCount;
	    }
	    
	    private void deleteOCR() throws Exception {
	    	
	    	Statement cs = null;
	    	int rsCount = 0;
	    	try
	        {	        	
	            cs = _conn.createStatement();     
	            rsCount = cs.executeUpdate(_sqlDeleteOCR);
	            _logger.info(rsCount+", Records Deleted.");
	            
	        }
	        finally
	        {	        	
	            if (cs != null)
	                cs.close();
	            _logger.info("End " + _sqlDeleteOCR);
	            _logger.info("Delete executed for Orphan OCRs");
	        }
	    }

	    /**
	     * Load the properties from the XML file specified.
	     *
	     * @param propFile_ the properties file.
	     */
	    private void loadProp(String propFile_) throws Exception
	    {
	        _propList = new Properties();

	        _logger.info("\n\nLoading properties " + gov.nih.nci.cadsr.common.Constants.BUILD_TAG + " ...\n\n");

	        try
	        {
	            FileInputStream in = new FileInputStream(propFile_);
	            _propList.loadFromXML(in);
	            in.close();
	        }
	        catch (Exception ex)
	        {
	            throw ex;
	        }

	        _dsurl = _propList.getProperty(Constants._DSURL);
	        if (_dsurl == null)
	            _logger.error("Missing " + Constants._DSURL + " connection string in " + propFile_);

	        _user = _propList.getProperty(Constants._DSUSER);
	        if (_user == null)
	            _logger.error("Missing " + Constants._DSUSER + " in " + propFile_);

	        _pswd = _propList.getProperty(Constants._DSPSWD);
	        if (_pswd == null)
	            _logger.error("Missing " + Constants._DSPSWD + " in " + propFile_);
	    }

	    /**
	     * Open a single simple connection to the database. No pooling is necessary.
	     *
	     * @param _dsurl
	     *        The Oracle TNSNAME entry describing the database location.
	     * @param user_
	     *        The ORACLE user id.
	     * @param pswd_
	     *        The password which must match 'user_'.
	     * @return The database error code.
	     */
	    private int open() throws Exception
	    {
	        // If we already have a connection, don't bother.
	        if (_conn != null)
	            return 0;

	        try
	        {
	            OracleDataSource ods = new OracleDataSource();

	            String parts[] = _dsurl.split("[:]");
	            ods.setDriverType("thin");
	            ods.setServerName(parts[0]);
	            ods.setPortNumber(Integer.parseInt(parts[1]));
	            ods.setServiceName(parts[2]);

	            _conn = ods.getConnection(_user, _pswd);
	            _conn.setAutoCommit(false);
	            return 0;
	        }
	        catch (SQLException ex)
	        {
	            throw ex;
	        }
	    }
	
}
