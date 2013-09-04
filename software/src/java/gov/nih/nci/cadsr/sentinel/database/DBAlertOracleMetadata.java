/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

package gov.nih.nci.cadsr.sentinel.database;

import gov.nih.nci.cadsr.sentinel.tool.AlertRec;
import gov.nih.nci.cadsr.sentinel.tool.ConceptItem;
import gov.nih.nci.cadsr.sentinel.util.SentinelToolProperties;

import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * Encapsulate all database access to the tables relating to metadata cleanup.
 * Update concept information if there is a conflict between EVS and caDSR
 * Insert long name and preferred definition into designation and definition tables respectively.
 * 
 * @author Archana Sahu
 */
public class DBAlertOracleMetadata extends DBAlertOracle
{
	private static final Logger _logger = Logger.getLogger(DBAlertOracleMetadata.class.getName());
	
    /**
     * Select all the caDSR Concepts with a certain origin sent as parameter
     *
     * @return the Concepts
     */
    public Vector<ConceptItem> selectEVSConcepts(String origin, java.sql.Connection oraConn)
    {
        // Get the context names and id's.
        String select = "SELECT con_idseq, conte_idseq, con_id, version, evs_source, preferred_name, long_name, definition_source, preferred_definition, origin, asl_name "
            + "FROM sbrext.concepts_view_ext WHERE asl_name NOT LIKE 'RETIRED%' "
        	+ "and origin LIKE '" + origin + "' "
        	//+ "and ROWNUM <= 20 "
            + "ORDER BY upper(long_name) ASC";

        Statement stmt = null;
        ResultSet rs = null;
        Vector<ConceptItem> list = null;
        
        try
        {
            // Prepare the statement.
            stmt = oraConn.createStatement();
            rs = stmt.executeQuery(select);

            // Get the list.
            list = new Vector<ConceptItem>();
            while (rs.next())
            {
            	ConceptItem rec = new ConceptItem();
                rec._idseq = rs.getString(1);      //con_idseq same as ac_idseq
                rec._conteidseq = rs.getString(2); //conte_idseq is context id
                rec._publicID = rs.getString(3);   //con_id is public id
                rec._version = rs.getString(4);
                rec._evsSource = rs.getString(5);
                rec._preferredName = rs.getString(6);
                rec._longName = rs.getString(7);
                rec._definitionSource = rs.getString(8);
                rec._preferredDefinition = rs.getString(9);
                rec._origin = rs.getString(10);
                rec._workflow_status = rs.getString(11);
                list.add(rec);
            }
        }
        catch (SQLException ex)
        {
        	int _errorCode;
        	String _errorMsg;
            // Bad...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
        }
        finally
        {
        	if (rs != null)
            {
                try { rs.close(); } catch(Exception ex) { }
            }
            if (stmt != null)
            {
                try { stmt.close(); } catch(Exception ex) { }
            }
        }
        
        return list;
    }
    
    /**
     * Finds caDSR Concept details with a certain origin and preferred_name (conceptid) sent as parameter
     *
     * @return the Concept
     */
    public ConceptItem findConceptDetails(String conceptid, String origin, java.sql.Connection oraConn)
    {
        // Get the context names and id's.
        String select = "SELECT con_idseq, conte_idseq, con_id, version, evs_source, preferred_name, long_name, definition_source, preferred_definition, origin, asl_name "
            + "FROM sbrext.concepts_view_ext WHERE preferred_name LIKE '" + conceptid + "' "
        	+ "and origin LIKE '" + origin + "' ";

        Statement stmt = null;
        ResultSet rs = null;
        ConceptItem rec = new ConceptItem();
        
        try
        {
            // Prepare the statement.
            stmt = oraConn.createStatement();
            rs = stmt.executeQuery(select);

            if (rs != null && rs.next())
            {
                rec._idseq = rs.getString(1);      //con_idseq same as ac_idseq
                rec._conteidseq = rs.getString(2); //conte_idseq is context id
                rec._publicID = rs.getString(3);   //con_id is public id
                rec._version = rs.getString(4);
                rec._evsSource = rs.getString(5);
                rec._preferredName = rs.getString(6);
                rec._longName = rs.getString(7);
                rec._definitionSource = rs.getString(8);
                rec._preferredDefinition = rs.getString(9);
                rec._origin = rs.getString(10);
                rec._workflow_status = rs.getString(11);
            }
        }
        catch (SQLException ex)
        {
        	int _errorCode;
        	String _errorMsg;
            // Bad...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
        }
        finally
        {
        	if (rs != null)
            {
                try { rs.close(); } catch(Exception ex) { }
            }
            if (stmt != null)
            {
                try { stmt.close(); } catch(Exception ex) { }
            }
        }
        
        return rec;
    }
    
    /**
     * Updates the caDSR Concepts having conflict with EVS (long name, preferred definition, definition source, workflow status).
     * Keep "Prior Preferred Definition" in definitions table
     * Keep "Prior Preferred Name" in designations table
     *
     * @return boolean (true if the caDSR database updated successfully)
     */
    public boolean updateCADSRConcept(ConceptItem rec, ConceptItem evsrec, boolean updateLongName, boolean updateDefn, boolean updateStatus, boolean updateDefnSrc, String[] retr_info, java.sql.Connection oraConn) throws SQLException
    {
    	PreparedStatement pstmt = null;
    	int rc = 0;
    	boolean success = false;
    	Date now = new Date();
        Timestamp today = new Timestamp(now.getTime());
        String today_str = AlertRec.dateToString(today, false);
        String change_note = "Updated caDSR information to match EVS";
        
        String update = "update sbrext.concepts_view_ext set" ;
        boolean hasParam = false;
        if (updateLongName) {
        	update += " LONG_NAME = ?";
        	change_note += " concept name ";
        	hasParam = true;
        }
        if(updateDefnSrc){
            if(hasParam){
            	update += ", ";
            }
            update += " DEFINITION_SOURCE = ?";
            change_note += " definition source ";
            hasParam = true;
        }
        if (updateDefn){
            if(hasParam){
            	update += ", ";
            }
            update += " PREFERRED_DEFINITION = ?";
            change_note += " definition ";
            hasParam = true;
        }
        
        if(updateStatus){
            if(hasParam){
            	update += ", ";
            }
            update += " ASL_NAME = ?, END_DATE=?";
            change_note += " retirement status";
            if (retr_info != null && retr_info.length == 2 && !retr_info[0].isEmpty())  //retirement data
            	change_note += ", concept was retired on " + retr_info[0];
            if (retr_info != null && retr_info.length == 2 && retr_info[1] != null && !retr_info[1].isEmpty() && !retr_info[1].equals("null")) //replacement concept
            	change_note += ", replaced with " + retr_info[1];
            change_note += ". Updated ";
        }
        update += ", CHANGE_NOTE = ? where PREFERRED_NAME = ? and EVS_SOURCE like 'NCI_CONCEPT_CODE' and ORIGIN like 'NCI Thesaurus'";
        change_note += "on " + today_str + " by sentinel cleanup script";
        //System.out.println ("Change note: " + change_note);
        //System.out.println ("Update Stmt: " + update);
       
        try
        {
        	oraConn.setAutoCommit(false);

            // Set all the SQL arguments.
            pstmt = oraConn.prepareStatement(update);
            
            int index = 1;
            if (updateLongName)
            	pstmt.setString(index++, evsrec._longName);
            if(updateDefnSrc)
            	pstmt.setString(index++, evsrec._definitionSource);
            if (updateDefn)
				pstmt.setString(index++, evsrec._preferredDefinition);
        	if(updateStatus) {
            	pstmt.setString(index++, evsrec._workflow_status);
            	pstmt.setDate(index++, new java.sql.Date(new java.util.Date().getTime()));
        	}
            pstmt.setString(index++, change_note);
            pstmt.setString(index, evsrec._preferredName);
            
            // Send it to the database. 
            rc = pstmt.executeUpdate();
            //System.out.println("After PreparedStatement execution (update concept): " + rc);
            
            success = (rc==1);  //if concept information updated successfully
            
            int rc1 = 0;
            int rc2 = 0;
            if (updateLongName) {
            	//If the same preferred name already exists in designation, skip adding it.
            	if (!ifAltenateNameExists(rec._idseq, rec._conteidseq, rec._longName, "Prior Preferred Name", oraConn)) {
            		rc1 = insertAltName(rec._idseq, rec._conteidseq, rec._longName, "Prior Preferred Name", "ENGLISH", oraConn);
            		//System.out.println ("After calling insertAltName: " + rc1);
            		success = success && (rc1==1);
            	}
            	else success=true; //just skip adding new alternate designation
            }
            if (updateDefn) {
            	rc2 = insertAltDef(rec._idseq, rec._conteidseq, rec._preferredDefinition, "Prior Preferred Definition", "ENGLISH", oraConn);
            	//System.out.println("After calling insertAltDef: " + rc2);
            	success = success && (rc2==1);
            }
            if(success)
            	oraConn.commit();
        }
        catch (SQLException ex)
        {
            // It's bad...
        	oraConn.rollback();
        	int _errorCode = -1;
        	String _errorMsg;
            _errorMsg = _errorCode + ": " + update + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            //System.out.println("Rolled back update/insert concept: " + _errorMsg);
            rc = _errorCode;
        }
        finally
        {
        	oraConn.setAutoCommit(true);
            if (pstmt != null)
            {
                try { pstmt.close(); } catch(Exception ex) {_logger.error(ex.toString()); }
            }
        }
    
        return success;
    }
    
    /**
     * Stores "Prior Preferred Name" in designations table
     *
     * @return number of rows inserted (1 if insertion successful)
     */
    private int insertAltName(String ac_idSeq, String conte_idseq, String prior_longName, String type, String lang, java.sql.Connection oraConn) throws SQLException
    {
    	CallableStatement cstmt = null;
    	int rc  = 0;
    	
    	String insert = "begin insert into sbr.designations_view "
                + "(ac_idseq, conte_idseq, name, detl_name, lae_name) "
                + "values (?, ?, ?, ?, ?) return desig_idseq into ?; end;";
    	
    	//System.out.println (ac_idSeq + " : " + conte_idseq + " : " + prior_longName + " : " + type +  " : " + lang +  " : " + insert);
    	
        try
        {
            cstmt = oraConn.prepareCall(insert);
            cstmt.setString(1, ac_idSeq);
            cstmt.setString(2, conte_idseq);
            cstmt.setString(3, prior_longName);
            cstmt.setString(4, type);
            cstmt.setString(5, lang);
            cstmt.registerOutParameter(6, java.sql.Types.VARCHAR);
            rc = cstmt.executeUpdate();
         }
        catch (SQLException ex)
        {
        	//System.out.println("caught exception while inserting to designations: " + ex.toString());
        	oraConn.rollback();
        	int _errorCode = -1;
        	String _errorMsg;
            _errorMsg = _errorCode + ": " + insert+ "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            rc = _errorCode;
        } finally {
        	if (cstmt != null) {
                try {
                	cstmt.close();
               
                } catch (SQLException e1) {
                	_logger.error("Failed to close CallableStatement", e1);
                }
             	cstmt = null;
            }
        }
        
        return rc;
    }
    
    /**
     * Stores "Prior Preferred Definition" in definitions table
     *
     * @return number of rows inserted (1 if insertion successful)
     */
    private int insertAltDef(String ac_idSeq, String conte_idseq, String prior_prefDefn, String type, String lang, java.sql.Connection oraConn) throws SQLException
    {
    	CallableStatement cstmt = null;
    	int rc = 0;
    	
        String insert = "begin insert into sbr.definitions_view "
            + "(ac_idseq, conte_idseq, definition, defl_name, lae_name) "
            + "values (?, ?, ?, ?, ?) return defin_idseq into ?; end;";
        
        //System.out.println (ac_idSeq + " : " + conte_idseq + " : " + prior_prefDefn + " : " + type +  " : " + lang +  " : " + insert);
        
        try
        {
            cstmt = oraConn.prepareCall(insert);
            cstmt.setString(1, ac_idSeq);
            cstmt.setString(2, conte_idseq);
            cstmt.setString(3, prior_prefDefn);
            cstmt.setString(4, type);
            cstmt.setString(5, lang);
            cstmt.registerOutParameter(6, java.sql.Types.VARCHAR);
            rc = cstmt.executeUpdate();
         }
        catch (SQLException ex)
        {
        	//System.out.println("caught exception while inserting into definitions: " + ex.toString());
        	oraConn.rollback();
        	int _errorCode = -1;
        	String _errorMsg;
            _errorMsg = _errorCode + ": " + insert + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            rc = _errorCode;
        } finally {
        	if (cstmt != null) {
                try {
                	cstmt.close();
               
                } catch (SQLException e1) {
                	_logger.error("Failed to close CallableStatement", e1);
                }
             	cstmt = null;
            }
        }
        
        return rc;
    }
    
    //
    public boolean ifAltenateNameExists(String ac_idSeq, String conte_idseq, String prior_longName, String type, java.sql.Connection oraConn)
    {
        String select = "select NAME from sbr.designations_view "
            + "where AC_IDSEQ = ? and CONTE_IDSEQ = ? and NAME = ? and DETL_NAME= ?";

        String name = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            pstmt = oraConn.prepareStatement(select);
            pstmt.setString(1, ac_idSeq);
            pstmt.setString(2, conte_idseq);
            pstmt.setString(3, prior_longName);
            pstmt.setString(4, type);
            rs = pstmt.executeQuery();
            if (rs.next()) 
                name = rs.getString(1);
        }
        catch (SQLException ex)
        {
            // Ooops...
        	//System.out.println("caught exception while finding to designation: " + ex.toString());
        	int _errorCode;
        	String _errorMsg;
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
        }
        finally
        {
        	if (rs != null)
            {
                try { rs.close(); } catch(Exception ex) { }
            }
        	if (pstmt != null)
            {
                try { pstmt.close(); } catch(Exception ex) {_logger.error(ex.toString()); }
            }
        }
        
        //System.out.println ("Name in ifAltenateNameExists: " + name);
        
        if (name != null)
			return true;
		else
			return false;
    }
    
    /**
     * Get the Maximum number of concepts to update through metadata cleanup effort
     * 
     * @param staticLimit defined in class
     * @param total number of concepts found in database
     * 
     * @return the maximum number of concepts to update
     */
    public int getMaxNumMsgs(int staticLimit, int totalConcepts)
    {
    	int numConceptstoUpdate = -1;
    	
    	try {
    		FileInputStream propFile = new FileInputStream("/local/content/cadsrsentinel/config/sentinel.properties");
	        
	        Properties p = new Properties(System.getProperties());
	        p.load(propFile);
	        System.setProperties(p);  // set the system properties
	        //System.out.println("Metadata Property: " + System.getProperty("TOOL.METADATA.MAXMSG"));
	    	
	        String max_msg = System.getProperty("TOOL.METADATA.MAXMSG");
		    if (max_msg != null ) {
		    	numConceptstoUpdate = Integer.parseInt(max_msg);
		    	if (numConceptstoUpdate == 0)
		    		numConceptstoUpdate = totalConcepts; // 0 means update all concepts at once, otherwise set the user-defined limit
		    }
		    else //if max_msg is null (that means not set properly through property file), set it to staticLimit defined in caDSRConceptCleanupEVS class
		    	numConceptstoUpdate = staticLimit; 
		    
		    //System.out.println("numConceptsUpdate: " + numConceptstoUpdate);
    
	    } catch (Exception e) {
	    	numConceptstoUpdate = staticLimit; //set it to staticLimit defined in caDSRConceptCleanupEVS class
	    	_logger.error("Unable to load properties from sentinel.properties : " + e);
		}
    	_logger.info("Number of concepts to update (set as property): " + numConceptstoUpdate);	  
    	
	    return numConceptstoUpdate;
    }
}
