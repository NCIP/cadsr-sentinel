// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/database/DBAlertOracle.java,v 1.12 2007-12-17 18:13:54 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.database;

import gov.nih.nci.cadsr.sentinel.audits.AuditReport;
import gov.nih.nci.cadsr.sentinel.tool.ACData;
import gov.nih.nci.cadsr.sentinel.tool.AlertRec;
import gov.nih.nci.cadsr.sentinel.tool.AutoProcessData;
import gov.nih.nci.cadsr.sentinel.tool.ConceptItem;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import gov.nih.nci.cadsr.sentinel.ui.AlertPlugIn;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.log4j.Logger;

/**
 * Encapsulate all database access to the tables relating to the Sentinel Alert
 * definitions.
 * <p>
 * For all access, the SQL statements are NOT placed in the properties file as
 * internationalization and translation should not affect them. We also want to
 * ease the maintenance by keeping the SQL with the database execute function
 * calls. If some SQL becomes duplicated, a single method with appropriate
 * parameters should be created to avoid difficulties with changing the SQL
 * statements over time as the table definitions evolve.
 * <p>
 * Start with setupPool() which only needs to be executed once. Then open() and
 * close() every time a new DBAlert object is created.
 * <p>
 * Also, just a reminder, all JDBC set...() and get...() methods use 1 (one)
 * based indexing unlike the Java language which uses 0 (zero) based.
 *
 * @author Larry Hebel
 * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(HttpServletRequest, String, String) open() with HTTP request
 * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(ServletContext, String, String) open() with Servlet Context
 * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(String, String, String) open()
 * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#close close()
 */
public class DBAlertOracle implements DBAlert
{
    // Class data
    private String              _namesList[];

    private String              _namesVals[];

    private String              _namesExempt;

    private String              _contextList[];

    private String              _contextVals[];

    private String              _schemeList[];

    private String              _schemeVals[];

    private String              _schemeContext[];

    private String              _protoList[];

    private String              _protoVals[];

    private String              _protoContext[];

    private String              _schemeItemList[];

    private String              _schemeItemVals[];

    private String              _schemeItemSchemes[];

    private Connection          _conn;

    private String              _user;

    private ServletContext      _sc;

    private boolean             _needCommit;

    private String              _groupsList[];

    private String              _groupsVals[];

    private String              _formsList[];

    private String              _formsVals[];

    private String              _formsContext[];

    private String              _workflowList[];

    private String              _workflowVals[];

    private String              _cworkflowList[];

    private String              _cworkflowVals[];

    private String              _regStatusList[];

    private String              _regStatusVals[];

    private String              _regCStatusList[];

    private String              _regCStatusVals[];

    private String              _nameID[];

    private String              _nameText[];

    private int                 _errorCode;

    private String              _errorMsg;

    private String              _actypesList[];

    private String              _actypesVals[];

    /**
     * The internal code for Version.
     */
    public static final String  _VERSION      = "VERSION";

    /**
     * The internal code for Workflow Status.
     */
    public static final String  _WFS          = "ASL_NAME";

    /**
     * The internal code for Registration Status.
     */
    public static final String  _RS           = "REGISTRATION_STATUS";

    /**
     * The internal code for User ID.
     */
    public static final String  _UNAME        = "UA_NAME";

    /**
     * Version Any Change value.
     */
    public static final char    _VERANYCHG    = 'C';

    /**
     * Version Major (whole) number change value.
     */
    public static final char    _VERMAJCHG    = 'M';

    /**
     * Version Ignore change value.
     */
    public static final char    _VERIGNCHG    = 'I';

    /**
     * Version Specific Value change value.
     */
    public static final char    _VERSPECHG    = 'S';

    /**
     * Maximum length of the Alert Definition Name.
     */
    public static final int     _MAXNAMELEN   = 30;

    /**
     * Maximum length of the Inaction Reason description.
     */
    public static final int     _MAXREASONLEN = 2000;

    /**
     * Maximum length of the Report Introduction description.
     */
    public static final int     _MAXINTROLEN  = 2000;

    /**
     * Maximum length of a freeform email address.
     */
    public static final int     _MAXEMAILLEN  = 255;

    /**
     * The Date comparison Created Only value.
     */
    public static final int     _DATECONLY = 0;

    /**
     * The Date comparison Modified Only value.
     */
    public static final int     _DATEMONLY = 1;

    /**
     * The Date comparison Created and Modified value.
     */
    public static final int     _DATECM = 2;

    private static boolean _poolWarning = true;

    private static final String _DATECHARS[][] = {
        {"<", ">", ">=", "<"},
        {">=", "<", "<", ">"},
        {">=", "<", ">=", "<"}
    };

    private static final String _CONTEXT      = "CONTEXT";

    private static final String _FORM         = "FORM";

    private static final String _PROTOCOL     = "PROTOCOL";

    private static final String _SCHEME       = "CS";

    private static final String _SCHEMEITEM   = "CSI";

    private static final String _CREATOR      = "CREATOR";

    private static final String _MODIFIER     = "MODIFIER";

    private static final String _REGISTER     = "REG_STATUS";

    private static final String _STATUS       = "AC_STATUS";

    private static final String _ACTYPE       = "ACTYPE";

    private static final String _DATEFILTER   = "DATEFILTER";

    private static final char   _CRITERIA     = 'C';

    private static final char   _MONITORS     = 'M';

    private static final String _orderbyACH =
        "order by id asc, "
//        + "cid asc, "
//        + "zz.date_modified asc, "
        + "ach.change_datetimestamp asc, "
        + "ach.changed_table asc, "
        + "ach.changed_table_idseq asc, "
        + "ach.changed_column asc";

    private static final DBAlertOracleMap1[] _DBMAP1 =
    {
        new DBAlertOracleMap1("ASL_NAME", "Workflow Status"),
        new DBAlertOracleMap1("BEGIN_DATE", "Begin Date"),
        new DBAlertOracleMap1("CDE_ID", "Public ID"),
        new DBAlertOracleMap1("CDR_IDSEQ", "Complex DE association"),
        new DBAlertOracleMap1("CD_IDSEQ", "Conceptual Domain association"),
        new DBAlertOracleMap1("CHANGE_NOTE", "Change Note"),
        new DBAlertOracleMap1("CONDR_IDSEQ", "Concept Class association"),
        new DBAlertOracleMap1("CON_IDSEQ", "Concept Class association"),
        new DBAlertOracleMap1("CREATED_BY", "Created By"),
        new DBAlertOracleMap1("CSTL_NAME", "Category"),
        new DBAlertOracleMap1("CS_ID", "Public ID"),
        new DBAlertOracleMap1("C_DEC_IDSEQ", "Child DEC association"),
        new DBAlertOracleMap1("C_DE_IDSEQ", "Child DE association"),
        new DBAlertOracleMap1("C_VD_IDSEQ", "Child VD association"),
        new DBAlertOracleMap1("DATE_CREATED", "Created Date"),
        new DBAlertOracleMap1("DATE_MODIFIED", "Modified Date"),
        new DBAlertOracleMap1("DECIMAL_PLACE", "Number of Decimal Places"),
        new DBAlertOracleMap1("DEC_ID", "Public ID"),
        new DBAlertOracleMap1("DEC_IDSEQ", "Data Element Concept association"),
        new DBAlertOracleMap1("DEC_REC_IDSEQ", "DEC_REC_IDSEQ"),
        new DBAlertOracleMap1("DEFINITION_SOURCE", "Definition Source"),
        new DBAlertOracleMap1("DELETED_IND", "Deleted Indicator"),
        new DBAlertOracleMap1("DESCRIPTION", "Description"),
        new DBAlertOracleMap1("DESIG_IDSEQ", "Designation association"),
        new DBAlertOracleMap1("DE_IDSEQ", "Data Element association"),
        new DBAlertOracleMap1("DE_REC_IDSEQ", "DE_REC_IDSEQ"),
        new DBAlertOracleMap1("DISPLAY_ORDER", "Display Order"),
        new DBAlertOracleMap1("DTL_NAME", "Data Type"),
        new DBAlertOracleMap1("END_DATE", "End Date"),
        new DBAlertOracleMap1("FORML_NAME", "Data Format"),
        new DBAlertOracleMap1("HIGH_VALUE_NUM", "Maximum Value"),
        new DBAlertOracleMap1("LABEL_TYPE_FLAG", "Label Type"),
        new DBAlertOracleMap1("LATEST_VERSION_IND", "Latest Version Indicator"),
        new DBAlertOracleMap1("LONG_NAME", "Long Name"),
        new DBAlertOracleMap1("LOW_VALUE_NUM", "Minimum Value"),
        new DBAlertOracleMap1("MAX_LENGTH_NUM", "Maximum Length"),
        new DBAlertOracleMap1("METHODS", "Methods"),
        new DBAlertOracleMap1("MIN_LENGTH_NUM", "Minimum Length"),
        new DBAlertOracleMap1("MODIFIED_BY", "Modified By"),
        new DBAlertOracleMap1("OBJ_CLASS_QUALIFIER", "Object Class Qualifier"),
        new DBAlertOracleMap1("OCL_NAME", "Object Class Name"),
        new DBAlertOracleMap1("OC_ID", "Public ID"),
        new DBAlertOracleMap1("OC_IDSEQ", "Object Class association"),
        new DBAlertOracleMap1("ORIGIN", "Origin"),
        new DBAlertOracleMap1("PREFERRED_DEFINITION", "Preferred Definition"),
        new DBAlertOracleMap1("PREFERRED_NAME", "Preferred Name"),
        new DBAlertOracleMap1("PROPERTY_QUALIFIER", "Property Qualifier"),
        new DBAlertOracleMap1("PROPL_NAME", "Property Name"),
        new DBAlertOracleMap1("PROP_ID", "Public ID"),
        new DBAlertOracleMap1("PROP_IDSEQ", "Property"),
        new DBAlertOracleMap1("PV_IDSEQ", "Permissible Value"),
        new DBAlertOracleMap1("P_DEC_IDSEQ", "Parent DEC association"),
        new DBAlertOracleMap1("P_DE_IDSEQ", "Parent DE association"),
        new DBAlertOracleMap1("P_VD_IDSEQ", "Parent VD association"),
        new DBAlertOracleMap1("QUALIFIER_NAME", "Qualifier"),
        new DBAlertOracleMap1("QUESTION", "Question"),
        new DBAlertOracleMap1("RD_IDSEQ", "Reference Document association"),
        new DBAlertOracleMap1("REP_IDSEQ", "Representation association"),
        new DBAlertOracleMap1("RL_NAME", "Relationship Name"),
        new DBAlertOracleMap1("RULE", "Rule"),
        new DBAlertOracleMap1("SHORT_MEANING", "Meaning"),
        new DBAlertOracleMap1("UOML_NAME", "Unit Of Measure"),
        new DBAlertOracleMap1("URL", "URL"),
        new DBAlertOracleMap1("VALUE", "Value"),
        new DBAlertOracleMap1("VD_ID", "Public ID"),
        new DBAlertOracleMap1("VD_IDSEQ", "Value Domain association"),
        new DBAlertOracleMap1("VD_REC_IDSEQ", "VD_REC_IDSEQ"),
        new DBAlertOracleMap1("VD_TYPE_FLAG", "Enumerated/Non-enumerated"),
        new DBAlertOracleMap1("VERSION", "Version")
    };

    private static final DBAlertOracleMap1[] _DBMAP1OTHER =
    {
        new DBAlertOracleMap1("CONTE_IDSEQ", "Owned By Context"),
        new DBAlertOracleMap1("LAE_NAME", "Language"),
        new DBAlertOracleMap1("NAME", "Name")
    };

    private static final DBAlertOracleMap1[] _DBMAP1DESIG =
    {
        new DBAlertOracleMap1("CONTE_IDSEQ", "Designation Context"),
        new DBAlertOracleMap1("DETL_NAME", "Designation Type"),
        new DBAlertOracleMap1("LAE_NAME", "Designation Language")
    };

    private static final DBAlertOracleMap1[] _DBMAP1CSI =
    {
        new DBAlertOracleMap1("CS_CSI_IDSEQ", "Classification Scheme Item association")
    };

    private static final DBAlertOracleMap1[] _DBMAP1RD =
    {
        new DBAlertOracleMap1("DCTL_NAME","Document Type"),
        new DBAlertOracleMap1("DISPLAY_ORDER", "Document Display Order"),
        new DBAlertOracleMap1("DOC_TEXT", "Document Text"),
        new DBAlertOracleMap1("RDTL_NAME", "Document Text Type"),
        new DBAlertOracleMap1("URL", "Document URL")
    };

    private static final DBAlertOracleMap1[] _DBMAP1COMPLEX =
    {
        new DBAlertOracleMap1("CONCAT_CHAR", "Concatenation Character"),
        new DBAlertOracleMap1("CRTL_NAME", "Complex Type")
    };

    private static final DBAlertOracleMap2[] _DBMAP2 =
    {
        new DBAlertOracleMap2("CD_IDSEQ", "sbr.conceptual_domains_view", "cd_idseq", "", "long_name || ' (' || cd_id || 'v' || version || ')' as label"),
        new DBAlertOracleMap2("CONDR_IDSEQ", "sbrext.component_concepts_view_ext ccv, sbrext.concepts_view_ext cv", "ccv.condr_idseq", " and cv.con_idseq = ccv.con_idseq order by ccv.display_order desc",
                        "cv.long_name || ' (' || cv.con_id || 'v' || cv.version || ') (' || cv.origin || ':' || cv.preferred_name || ')' as label"),
        new DBAlertOracleMap2("CONTE_IDSEQ", "sbr.contexts_view", "conte_idseq", "", "name || ' (v' || version || ')' as label"),
        new DBAlertOracleMap2("CON_IDSEQ", "sbrext.concepts_view_ext", "con_idseq", "", "long_name || ' (' || con_id || 'v' || version || ') (' || origin || ':' || preferred_name || ')' as label"),
        new DBAlertOracleMap2("CREATED_BY", "sbr.user_accounts_view", "ua_name", "", "name as label"),
        new DBAlertOracleMap2("CS_CSI_IDSEQ", "sbr.cs_csi_view cci, sbr.class_scheme_items_view csi", "cci.cs_csi_idseq", " and csi.csi_idseq = cci.csi_idseq","csi.csi_name as label"),
        new DBAlertOracleMap2("DEC_IDSEQ", "sbr.data_element_concepts_view", "dec_idseq", "", "long_name || ' (' || dec_id || 'v' || version || ')' as label"),
        new DBAlertOracleMap2("DE_IDSEQ", "sbr.data_elements_view", "de_idseq", "", "long_name || ' (' || cde_id || 'v' || version || ')' as label"),
        new DBAlertOracleMap2("MODIFIED_BY", "sbr.user_accounts_view", "ua_name", "", "name as label"),
        new DBAlertOracleMap2("OC_IDSEQ", "sbrext.object_classes_view_ext", "oc_idseq", "", "long_name || ' (' || oc_id || 'v' || version || ')' as label"),
        new DBAlertOracleMap2("PROP_IDSEQ", "sbrext.properties_view_ext", "prop_idseq", "", "long_name || ' (' || prop_id || 'v' || version || ')' as label"),
        new DBAlertOracleMap2("PV_IDSEQ", "sbr.permissible_values_view", "pv_idseq", "", "value || ' (' || short_meaning || ')' as label"),
        new DBAlertOracleMap2("RD_IDSEQ", "sbr.reference_documents_view", "rd_idseq", "", "name || ' (' || nvl(doc_text, url) || ')' as label"),
        new DBAlertOracleMap2("REP_IDSEQ", "sbrext.representations_view_ext", "rep_idseq", "", "long_name || ' (' || rep_id || 'v' || version || ')' as label"),
        new DBAlertOracleMap2("UA_NAME", "sbr.user_accounts_view", "ua_name", "", "name as label"),
        new DBAlertOracleMap2("VD_IDSEQ", "sbr.value_domains_view", "vd_idseq", "", "long_name || ' (' || vd_id || 'v' || version || ')' as label")
    };

    private static final DBAlertOracleMap3[] _DBMAP3 =
    {
        new DBAlertOracleMap3("cd", "Conceptual Domain", "sbr.conceptual_domains_view", null),
        new DBAlertOracleMap3("con", "Concept", "sbrext.concepts_view_ext", null),
        new DBAlertOracleMap3("conte", "Context", "sbr.contexts_view", null),
        new DBAlertOracleMap3("cs", "Classification Scheme", "sbr.classification_schemes_view", "CLASSIFICATION_SCHEMES"),
        new DBAlertOracleMap3("csi", "Classification Scheme Item", "sbr.class_scheme_items_view", null),
        new DBAlertOracleMap3("de", "Data Element", "sbr.data_elements_view", "DATA_ELEMENTS"),
        new DBAlertOracleMap3("dec", "Data Element Concept", "sbr.data_element_concepts_view", "DATA_ELEMENT_CONCEPTS"),
        new DBAlertOracleMap3("oc", "Object Class", "sbrext.object_classes_view_ext", "OBJECT_CLASSES_EXT"),
        new DBAlertOracleMap3("prop", "Property", "sbrext.properties_view_ext", "PROPERTIES_EXT"),
        new DBAlertOracleMap3("proto", "Protocol", "sbrext.protocols_view_ext", null),
        new DBAlertOracleMap3("pv", "Permissible Value", "sbr.permissible_values_view", "PERMISSIBLE_VALUES"),
        new DBAlertOracleMap3("qc", "Form/Template", "sbrext.quest_contents_view_ext",  null),
        new DBAlertOracleMap3("qcm", "Module", null, null),
        new DBAlertOracleMap3("qcq", "Question", null, null),
        new DBAlertOracleMap3("qcv", "Valid Value", null, null),
        new DBAlertOracleMap3("vd", "Value Domain", "sbr.value_domains_view", "VALUE_DOMAINS"),
        new DBAlertOracleMap3("vm", "Value Meaning", "sbr.value_meanings_view", null)
    };

    private static final Logger _logger = Logger.getLogger(DBAlert.class.getName());

    /**
     * Entry for development testing of the class
     *
     * @param args program arguments
     */
    public static void main(String args[])
    {
        DBAlertOracle var = new DBAlertOracle();
        var.concat(_DBMAP1, _DBMAP1DESIG,_DBMAP1RD, _DBMAP1CSI , _DBMAP1COMPLEX , _DBMAP1OTHER);
    }

    /**
     * Constructor.
     */
    public DBAlertOracle()
    {
        _errorCode = 0;
        _nameID = new String[1];
        _nameID[0] = "";
        _nameText = new String[1];
        _nameText[0] = "";
        _conn = null;
        _sc = null;
    }

    /**
     * Required prior to using any other methods within this class. This method
     * checks for the existence of the pool attached to the Servlet Context.
     * Once the pool is successfully created subsequent invocations perform no
     * action. The method is static and synchronized to allow for possible
     * multiple invocations of the Sentinel Tool simultaneously. Although the
     * data referenced is not static we don't want to take the chance that the
     * ServletContext.getAttribute() is called, we loose the time slice and upon
     * return from the VM one invocation thinks the pool is missing when another
     * invocation has just successfully created it. This is only called from the
     * Logon Action currently so the overhead inherit with synchronized
     * functions is minimized.
     * <p>
     * To use this from non-browser servlet logic use the method which requires
     * the driver as the first argument.
     *
     * @param session_
     *        The session object.
     * @param dsurl_
     *        The URL entry for the desired database.
     * @param username_
     *        The default database user logon id.
     * @param password_
     *        The password to match the user.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int setupPool(HttpSession session_,
        String dsurl_, String username_, String password_)
    {
        return setupPoolX(session_, dsurl_, username_, password_);
    }

    static private synchronized int setupPoolX(HttpSession session_,
                    String dsurl_, String username_, String password_)
    {
        // Get the Servlet Context and see if a pool already exists.
        ServletContext sc = session_.getServletContext();
        if (sc.getAttribute(DBAlert._DATASOURCE) != null)
            return 0;

        OracleConnectionPoolDataSource ocpds = (OracleConnectionPoolDataSource) sc
            .getAttribute(_DBPOOL);
        if (ocpds != null)
            return 0;

        ocpds = setupPool(dsurl_, username_, password_);
        if (ocpds != null)
        {
            // Remember the pool in the Servlet Context.
            sc.setAttribute(_DBPOOL + ".ds", ocpds);
            sc.setAttribute(_DBPOOL + ".user", username_);
            sc.setAttribute(_DBPOOL + ".pswd", password_);
            return 0;
        }
        return -1;
    }

    /**
     * Required prior to using any other methods within this class. This method
     * checks for the existence of the pool attached to the Servlet Context.
     * Once the pool is successfully created subsequent invocations perform no
     * action. The method is static and synchronized to allow for possible
     * multiple invocations of the Sentinel Tool simultaneously. Although the
     * data referenced is not static we don't want to take the chance that the
     * ServletContext.getAttribute() is called, we loose the time slice and upon
     * return from the VM one invocation thinks the pool is missing when another
     * invocation has just successfully created it. This is only called from the
     * Logon Action currently so the overhead inherit with synchronized
     * functions is minimized.
     * <p>
     * To use this from non-browser servlet logic use the method which requires
     * the driver as the first argument.
     *
     * @param request_
     *        The servlet request object.
     * @param dsurl_
     *        The URL entry for the desired database.
     * @param username_
     *        The default database user logon id.
     * @param password_
     *        The password to match the user.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int setupPool(HttpServletRequest request_,
        String dsurl_, String username_, String password_)
    {
        // Pass it on...
        return setupPool(request_.getSession(), dsurl_, username_,
            password_);
    }

    /**
     * Required prior to using any other methods within this class. This method
     * checks for the existence of the pool attached to the Servlet Context.
     * Once the pool is successfully created subsequent invocations perform no
     * action. The method is static and synchronized to allow for possible
     * multiple invocations of the Sentinel Tool simultaneously. Although the
     * data referenced is not static we don't want to take the chance that the
     * ServletContext.getAttribute() is called, we loose the time slice and upon
     * return from the VM one invocation thinks the pool is missing when another
     * invocation has just successfully created it. This is only called from the
     * Logon Action currently so the overhead inherit with synchronized
     * functions is minimized.
     * <p>
     * To use this from a browser servlet, use the method which requires an
     * HttpServletRequest as the first argument to the method.
     *
     * @param dsurl_
     *        The URL entry for the desired database.
     * @param username_
     *        The default database user logon id.
     * @param password_
     *        The password to match the user.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    private static synchronized OracleConnectionPoolDataSource setupPool(
        String dsurl_, String username_, String password_)
    {
        // First register the database driver.
        OracleConnectionPoolDataSource ocpds = null;
        int rc = 0;
        String rcTxt = null;
        try
        {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        }
        catch (SQLException ex)
        {
            rc = ex.getErrorCode();
            rcTxt = rc + ": " + ex.toString();
        }

        try
        {
            // Create an the connection pool data source and set the parameters.
            ocpds = new OracleConnectionPoolDataSource();
            if (dsurl_.indexOf(':') > 0)
            {
                String parts[] = dsurl_.split("[:]");
                ocpds.setDriverType("thin");
                ocpds.setServerName(parts[0]);
                ocpds.setPortNumber(Integer.parseInt(parts[1]));
                ocpds.setServiceName(parts[2]);
            }
            else
            {
                ocpds.setDriverType("oci8");
                ocpds.setTNSEntryName(dsurl_);
            }
            ocpds.setUser(username_);
            ocpds.setPassword(password_);
        }
        catch (SQLException ex)
        {
            // We have a problem.
            rc = ex.getErrorCode();
            rcTxt = rc + ": " + ex.toString();
            ocpds = null;
        }

        if (rc != 0)
        {
            // Send a user friendly message to the Logon window and the more
            // detailed
            // message to the console.
            _logger.error(rcTxt);
        }
        return ocpds;
    }

    /**
     * Create a connection from the pool. This is not part of the constructor to
     * allow the method to have return codes that can be interrogated by the
     * caller. If Exception are desired, appropriate wrapper methods can be
     * created to provide both features and give the caller the flexibility to
     * use either without additional coding.
     * <p>
     * Be sure to call DBAlert.close() to complete the request before returning
     * to the client or loosing the object focus in the caller to "new
     * DBAlert()".
     *
     * @param sc_
     *        The servlet context which holds the data source pool reference
     *        created by the DBAlert.setupPool() method.
     * @param user_
     *        The database user logon id.
     * @return 0 if successful, otherwise the Oracle error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#close close()
     */
    public int open(ServletContext sc_, String user_)
    {
        return open(sc_, user_, null);
    }

    /**
     * Create a connection from the pool. This is not part of the constructor to
     * allow the method to have return codes that can be interrogated by the
     * caller. If Exception are desired, appropriate wrapper methods can be
     * created to provide both features and give the caller the flexibility to
     * use either without additional coding.
     * <p>
     * Be sure to call DBAlert.close() to complete the request before returning
     * to the client or loosing the object focus in the caller to "new
     * DBAlert()".
     *
     * @param sc_
     *        The servlet context which holds the data source pool reference
     *        created by the DBAlert.setupPool() method.
     * @param user_
     *        The database user logon id.
     * @param pswd_
     *        The password to match the user.
     * @return 0 if successful, otherwise the Oracle error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#close close()
     */
    public int open(ServletContext sc_, String user_, String pswd_)
    {
        // If we already have a connection, don't bother.
        if (_conn != null)
            return 0;

        try
        {
            // Get a connection from the pool, if anything unexpected happens
            // the catch is
            // run.
            _sc = sc_;
            _user = user_;
            AlertPlugIn var = (AlertPlugIn)_sc.getAttribute(DBAlert._DATASOURCE);
            if (var == null)
            {
                OracleConnectionPoolDataSource ocpds =
                    (OracleConnectionPoolDataSource) _sc.getAttribute(_DBPOOL);
                _conn = ocpds.getConnection(user_, pswd_);
                if (_poolWarning)
                {
                    _poolWarning = false;
                    _logger.warn("============ Could not find JBoss datasource using internal connection pool.");
                }
            }
            else if (pswd_ == null)
                _conn = var.getDataSource().getConnection();
            else
                _conn = var.getAuthenticate().getConnection(user_, pswd_);

            // We handle the commit once in the close.
            _conn.setAutoCommit(false);
            _needCommit = false;

            return 0;
        }
        catch (SQLException ex)
        {
            // There seems to be a problem.
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + ex.toString();
            _logger.error(_errorMsg);
            _sc = null;
            _conn = null;
            return _errorCode;
        }
    }

    /**
     * Create a connection from the pool. This is not part of the constructor to
     * allow the method to have return codes that can be interrogated by the
     * caller. If Exception are desired, appropriate wrapper methods can be
     * created to provide both features and give the caller the flexibility to
     * use either without additional coding.
     * <p>
     * Be sure to call DBAlert.close() to complete the request before returning
     * to the client or loosing the object focus in the caller to "new
     * DBAlert()".
     *
     * @param ds_
     *        The datasource for database connections.
     * @param user_
     *        The database user logon id.
     * @return 0 if successful, otherwise the error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#close close()
     */
    public int open(DataSource ds_, String user_)
    {
        try
        {
            _user = user_;
            _conn = ds_.getConnection();
            _conn.setAutoCommit(false);
            _needCommit = false;
        }
        catch (SQLException ex)
        {
            _logger.error(ex.toString(), ex);
            return ex.getErrorCode();
        }
        return 0;
    }

    /**
     * Open a single simple connection to the database. No pooling is necessary.
     *
     * @param dsurl_
     *        The Oracle TNSNAME entry describing the database location.
     * @param user_
     *        The ORACLE user id.
     * @param pswd_
     *        The password which must match 'user_'.
     * @return The database error code.
     */
    public int open(String dsurl_, String user_, String pswd_)
    {
        // If we already have a connection, don't bother.
        if (_conn != null)
            return 0;

        try
        {
            OracleDataSource ods = new OracleDataSource();
            if (dsurl_.indexOf(':') > 0)
            {
                String parts[] = dsurl_.split("[:]");
                ods.setDriverType("thin");
                ods.setServerName(parts[0]);
                ods.setPortNumber(Integer.parseInt(parts[1]));
                ods.setServiceName(parts[2]);
            }
            else
            {
                ods.setDriverType("oci8");
                ods.setTNSEntryName(dsurl_);
            }
            _user = user_;
            _conn = ods.getConnection(user_, pswd_);
            _conn.setAutoCommit(false);
            _needCommit = false;
            return 0;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg =  _errorCode + ": " + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Create a connection from the pool. This is not part of the constructor to
     * allow the method to have return codes that can be interrogated by the
     * caller. If Exception are desired, appropriate wrapper methods can be
     * created to provide both features and give the caller the flexibility to
     * use either without additional coding.
     * <p>
     * Be sure to call DBAlert.close() to complete the request before returning
     * to the client or loosing the object focus in the caller to "new
     * DBAlert()".
     *
     * @param request_
     *        The servlet request object.
     * @param user_
     *        The database user logon id.
     * @param pswd_
     *        The password to match the user.
     * @return 0 if successful, otherwise the Oracle error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#close close()
     */
    public int open(HttpServletRequest request_, String user_, String pswd_)
    {
        return open(request_.getSession().getServletContext(), user_, pswd_);
    }

    /**
     * Create a connection from the pool. This is not part of the constructor to
     * allow the method to have return codes that can be interrogated by the
     * caller. If Exception are desired, appropriate wrapper methods can be
     * created to provide both features and give the caller the flexibility to
     * use either without additional coding.
     * <p>
     * Be sure to call DBAlert.close() to complete the request before returning
     * to the client or loosing the object focus in the caller to "new
     * DBAlert()".
     *
     * @param request_
     *        The servlet request object.
     * @param user_
     *        The database user logon id.
     * @return 0 if successful, otherwise the Oracle error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#close close()
     */
    public int open(HttpServletRequest request_, String user_)
    {
        return open(request_.getSession().getServletContext(), user_, null);
    }

    /**
     * Required upon a successful return from open. When all database access is
     * completed for this user request. To optimize the database access, all
     * methods which perform actions that require a commmit only set a flag. It
     * is in the close() method the flag is interrogated and the commit actually
     * occurs.
     *
     * @return 0 if successful, otherwise the Oracle error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(HttpServletRequest, String, String) open() with HTTP request
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(ServletContext, String, String) open() with Servlet Context
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(String, String, String) open()
     */
    public int close()
    {
/*
        try
        {
            throw new Exception("Trace");
        }
        catch (Exception ex)
        {
            _logger.debug("Trace", ex);
        }
*/

        // We only need to do something if a connection is obtained.
        if (_conn != null)
        {
            try
            {
                // Don't forget to commit if needed.
                if (_needCommit)
                    _conn.commit();

            }
            catch (SQLException ex)
            {
                // There seems to be a problem.
                _errorCode = DBAlertUtil.getSQLErrorCode(ex);
                _errorMsg = _errorCode + ": "
                    + ex.toString();
                _logger.error(_errorMsg);
            }
            try
            {
                // Close the connection and release all pointers.
                _conn.close();
                _conn = null;
                _sc = null;
            }
            catch (SQLException ex)
            {
                // There seems to be a problem.
                _errorCode = DBAlertUtil.getSQLErrorCode(ex);
                _errorMsg = _errorCode + ": "
                    + ex.toString();
                _logger.error(_errorMsg);
                _conn = null;
                _sc = null;
                return _errorCode;
            }
        }
        return 0;
    }

    /**
     * Get the database connection opened for this object.
     *
     * @return java.sql.Connection opened by this object.
     */
    public Connection getConnection()
    {
        return _conn;
    }

    /**
     * Retrieves the abbreviated list of all alerts. The AlertRec objects
     * returned are not fully populated with all the details of each alert. Only
     * basic information such as the database id, name, creator and a few other
     * basic properties are guaranteed.
     *
     * @param user_
     *        The user id with which to qualify the results. This must be as it
     *        appears in the "created_by" column of the Alert tables. If null is
     *        used, a list of all Alerts is retrieved.
     * @return The array of Alerts retrieved.
     */
    public AlertRec[] selectAlerts(String user_)
    {
        // Define the SQL Select
        String select = "select a.al_idseq, a.name, a.last_auto_run, a.auto_freq_unit, a.al_status, a.auto_freq_value, a.created_by, u.name "
            + "from sbrext.sn_alert_view_ext a, sbr.user_accounts_view u "
            + "where ";

        // If a user id was given, qualify the list with it.
        if (user_ != null)
            select = select + "a.created_by = ? and ";
        select = select + "u.ua_name = a.created_by";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector<AlertRec> results = new Vector<AlertRec>();

        try
        {
            // Prepare the statement.
            pstmt = _conn.prepareStatement(select);
            if (user_ != null)
                pstmt.setString(1, user_);

            // Get the list.
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                // For the list of alerts we only need basic information.
                AlertRec rec = new AlertRec();
                rec.setAlertRecNum(rs.getString(1));
                rec.setName(rs.getString(2));
                rec.setAdate(rs.getTimestamp(3));
                rec.setFreq(rs.getString(4));
                rec.setActive(rs.getString(5));
                rec.setDay(rs.getInt(6));
                rec.setCreator(rs.getString(7));
                rec.setCreatorName(rs.getString(8));

                // After much consideration, I thought it best to dynamically
                // generate the textual summary of the alert. This
                // could be done at the time the alert is saved and stored in
                // the database, however, the descriptions could become
                // very large and we have to worry about some things changing
                // and not being updated. For the first implementation
                // it seems best to generate it. In the future this may change.
                selectQuery(rec);
                select = rec.getSummary(false);
                rec.clearQuery();
                rec.setSummary(select);
                results.add(rec);
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select + "\n\n"
                + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }

        // Now that we have the full results we can create a single simple array
        // to contain
        // them. This greatly simplifies access throughout the code.
        AlertRec database[] = null;
        int count = results.size();
        if (count > 0)
        {
            database = new AlertRec[count];
            for (int ndx = 0; ndx < count; ++ndx)
                database[ndx] = (AlertRec) results.get(ndx);
        }

        // Return the results.
        return database;
    }

    /**
     * Pull the list of recipients for a specific alert.
     *
     * @param rec_
     *        The alert for the desired recipients. The alertRecNum must be set
     *        prior to this method.
     * @return 0 if successful, otherwise the database error code.
     */
    private int selectRecipients(AlertRec rec_)
    {
        // A Report has a list of one or more recipients.
        String select = "select ua_name, email, conte_idseq "
            + "from sbrext.sn_recipient_view_ext " + "where rep_idseq = ?";
        try
        {
            // Get ready...
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, rec_.getReportRecNum());

            // Go!
            Vector<String> rlist = new Vector<String>();
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                String temp = rs.getString(1);
                // We use the ua_name as is.
                if (temp != null)
                    rlist.add(temp);
                else
                {
                    temp = rs.getString(2);
                    // The email address must have an "@" in it somewhere so no
                    // change.
                    if (temp != null)
                        rlist.add(temp);
                    else
                    {
                        temp = rs.getString(3);
                        // To distinguish groups from a ua_name we use a "/" as
                        // a prefix.
                        if (temp != null)
                            rlist.add("/" + temp);
                    }
                }
            }
            rs.close();
            pstmt.close();

            // Move the data to an array and drop the Vector.
            if (rlist.size() > 0)
            {
                String temp[] = new String[rlist.size()];
                for (int ndx = 0; ndx < temp.length; ++ndx)
                {
                    temp[ndx] = (String) rlist.get(ndx);
                }
                rec_.setRecipients(temp);
            }
            else
            {
                rec_.setRecipients(null);
            }
            return 0;
        }
        catch (SQLException ex)
        {
            // We've got trouble.
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select + "\n\n"
                + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Pull the report information for a specific alert definition.
     *
     * @param rec_
     *        The alert for the desired report. The alertRecNum must be set
     *        prior to this method.
     * @return 0 if successful, otherwise the database error code.
     */
    private int selectReport(AlertRec rec_)
    {
        // Each Alert has one Report definition.
        String select = "select rep_idseq, include_property_ind, style, send, acknowledge_ind, comments, assoc_lvl_num "
            + "from sbrext.sn_report_view_ext " + "where al_idseq = ?";
        try
        {
            // Get ready...
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, rec_.getAlertRecNum());

            // Strictly speaking if a record is not found it is a violation of a
            // business rule, however,
            // the code is written to default all values to avoid these types of
            // quirks.
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                rec_.setReportRecNum(rs.getString(1));
                rec_.setIncPropSect(rs.getString(2));
                rec_.setReportAck(rs.getString(3));
                rec_.setReportEmpty(rs.getString(4));
                rec_.setReportAck(rs.getString(5));
                rec_.setIntro(rs.getString(6), true);
                rec_.setIAssocLvl(rs.getInt(7));
            }
            rs.close();
            pstmt.close();

            return selectRecipients(rec_);
        }
        catch (SQLException ex)
        {
            // We've got trouble.
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select + "\n\n"
                + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Pull the properties for a specific alert definition.
     *
     * @param rec_
     *        The alert for the desired property values. The alertRecNum must be
     *        set prior to this method.
     * @return 0 if successful, otherwise the database error code.
     */
    private int selectProperties(AlertRec rec_)
    {
        // Define the SQL Select. The column names are expanded to ensure the
        // order of retrieval. If asterisk (*) is used
        // and the database definition changes it could rearrange the columns
        // and the subsequent ResultSet.get...() method
        // calls will fail.
        String select = "select a.name, a.last_auto_run, a.last_manual_run, a.auto_freq_unit, a.al_status, a.begin_date, a.end_date, "
            + "a.status_reason, a.date_created, nvl(a.date_modified, a.date_created) as mdate, nvl(a.modified_by, a.created_by) as mby, "
            + "a.created_by, a.auto_freq_value, u1.name, nvl(u2.name, u1.name) as name2 "
            + "from sbrext.sn_alert_view_ext a, sbr.user_accounts_view u1, sbr.user_accounts_view u2 "
            + "where a.al_idseq = ? and u1.ua_name = a.created_by and u2.ua_name(+) = a.modified_by";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            // Get ready...
            pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, rec_.getAlertRecNum());

            // Go!
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                // As the where clause uses a specific ID we should only
                // retrieve one result. And there's the
                // one (1) based indexing again.
                rec_.setName(rs.getString(1));
                rec_.setAdate(rs.getTimestamp(2));
                rec_.setRdate(rs.getTimestamp(3));
                rec_.setFreq(rs.getString(4));
                rec_.setActive(rs.getString(5));
                rec_.setStart(rs.getTimestamp(6));
                rec_.setEnd(rs.getTimestamp(7));
                rec_.setInactiveReason(rs.getString(8));
                rec_.setCdate(rs.getTimestamp(9));
                rec_.setMdate(rs.getTimestamp(10));
                //rec_.setModifier(rs.getString(11));
                rec_.setCreator(rs.getString(12));
                rec_.setDay(rs.getInt(13));
                rec_.setCreatorName(rs.getString(14));
                //rec_.setModifierName(rs.getString(15));
            }
            else
            {
                // This shouldn't happen but just in case...
                rec_.setAlertRecNum(null);
            }
            rs.close();
            pstmt.close();
            return 0;
        }
        catch (SQLException ex)
        {
            // We've got trouble.
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select + "\n\n"
                + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Retrieve a full single Alert definition. All data elements of the
     * AlertRec will be populated to reflect the database content.
     *
     * @param id_
     *        The database id (al_idseq) of the Alert definitions.
     * @return A complete definition record if successful or null if an error
     *         occurs.
     */
    public AlertRec selectAlert(String id_)
    {
        AlertRec rec = new AlertRec();
        rec.setAlertRecNum(id_);
        if (selectProperties(rec) != 0)
        {
            rec = null;
        }
        else if (selectReport(rec) != 0)
        {
            rec = null;
        }
        else if (selectQuery(rec) != 0)
        {
            rec = null;
        }

        // Give 'em what we've got.
        return rec;
    }

    /**
     * Update the database with the Alert properties stored in a memory record.
     *
     * @param rec_
     *        The Alert definition to be stored.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On an error with rollback().
     */
    private int updateProperties(AlertRec rec_) throws SQLException
    {
        // Define the update statement. Some columns are not updated as they are
        // controlled
        // by triggers, specifically date_created, date_modified, creator and
        // modifier.
        String update = "update sbrext.sn_alert_view_ext set " + "name = ?, "
            + "auto_freq_unit = ?, " + "al_status = ?, " + "begin_date = ?, "
            + "end_date = ?, " + "status_reason = ?, " + "auto_freq_value = ?, "
            + "modified_by = ? "
            + "where al_idseq = ?";

        cleanRec(rec_);
        PreparedStatement pstmt = null;
        try
        {
            // Set all the SQL arguments.
            pstmt = _conn.prepareStatement(update);
            pstmt.setString(1, rec_.getName());
            pstmt.setString(2, rec_.getFreqString());
            pstmt.setString(3, rec_.getActiveString());
            pstmt.setTimestamp(4, rec_.getStart());
            pstmt.setTimestamp(5, rec_.getEnd());
            pstmt.setString(6, rec_.getInactiveReason(false));
            pstmt.setInt(7, rec_.getDay());
            pstmt.setString(8, _user);
            pstmt.setString(9, rec_.getAlertRecNum());

            // Send it to the database. And remember to flag a commit for later.
            pstmt.executeUpdate();
            pstmt.close();
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            // It's bad...
            _conn.rollback();
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + update
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Update the Report information for an Alert within the database.
     *
     * @param rec_
     *        The Alert definition to be written to the database.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int updateReport(AlertRec rec_) throws SQLException
    {
        // Update the related Report definition.
        String update = "update sbrext.sn_report_view_ext set "
            + "comments = ?, include_property_ind = ?, style = ?, send = ?, acknowledge_ind = ?, assoc_lvl_num = ?, "
            + "modified_by = ? "
            + "where rep_idseq = ?";
        try
        {
            // Set all the SQL arguments.
            PreparedStatement pstmt = _conn.prepareStatement(update);
            pstmt.setString(1, rec_.getIntro(false));
            pstmt.setString(2, rec_.getIncPropSectString());
            pstmt.setString(3, rec_.getReportStyleString());
            pstmt.setString(4, rec_.getReportEmptyString());
            pstmt.setString(5, rec_.getReportAckString());
            pstmt.setInt(6, rec_.getIAssocLvl());
            pstmt.setString(7, _user);

            pstmt.setString(8, rec_.getReportRecNum());

            pstmt.executeUpdate();
            pstmt.close();
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            // It's bad...
            _conn.rollback();
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + update
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Perform an update on the complete record. No attempt is made to isolate
     * the specific changes so many times values will not actually be changed.
     *
     * @param rec_
     *        The record containing the updated information. All data elements
     *        must be populated and correct.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int updateAlert(AlertRec rec_)
    {
        // Ensure data is clean.
        rec_.setDependancies();

        // Update database.
        try
        {
            int xxx = updateProperties(rec_);
            if (xxx != 0)
                return xxx;
            xxx = updateReport(rec_);
            if (xxx != 0)
                return xxx;
            xxx = updateRecipients(rec_);
            if (xxx != 0)
                return xxx;
            return updateQuery(rec_);
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Delete the Alert Definitions specified by the caller. The values must be
     * existing al_idseq values within the Alert table.
     *
     * @param list_
     *        The al_idseq values which identify the definitions to delete.
     *        Other dependant tables in the database will automatically be
     *        cleaned up via cascades and triggers.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int deleteAlerts(Vector list_)
    {
        // Be sure we have something to do.
        int count = list_.size();
        if (count == 0)
            return 0;

        // Create an array.
        String list[] = new String[count];
        for (count = 0; count < list_.size(); ++count)
        {
            list[count] = (String) list_.get(count);
        }
        return deleteAlerts(list);
    }

    /**
     * Delete the Alert Definitions specified by the caller. The values must be
     * existing al_idseq values within the Alert table.
     *
     * @param id_
     *        The al_idseq value which identifies the definition to delete.
     *        Other dependant tables in the database will automatically be
     *        cleaned up via cascades and triggers.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int deleteAlert(String id_)
    {
        // Be sure we have something to do.
        if (id_ == null || id_.length() == 0)
            return 0;
        String list[] = new String[1];
        list[0] = id_;
        return deleteAlerts(list);
    }

    /**
     * Delete the Alert Definitions specified by the caller. The values must be
     * existing al_idseq values within the Alert table.
     *
     * @param list_
     *        The al_idseq values which identify the definitions to delete.
     *        Other dependant tables in the database will automatically be
     *        cleaned up via cascades and triggers.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int deleteAlerts(String list_[])
    {
        // Be sure we have something to do.
        if (list_ == null || list_.length == 0)
            return 0;

        // Build the delete SQL statement.
        String delete = "delete " + "from sbrext.sn_alert_view_ext "
            + "where al_idseq in (?";

        for (int ndx = 1; ndx < list_.length; ++ndx)
        {
            delete = delete + ",?";
        }
        delete = delete + ")";

        // Delete all the specified definitions. We rely on cascades or triggers
        // to clean up
        // all related tables.
        PreparedStatement pstmt = null;
        try
        {
            // Set all the SQL arguments.
            pstmt = _conn.prepareStatement(delete);
            for (int ndx = 0; ndx < list_.length; ++ndx)
            {
                pstmt.setString(ndx + 1, list_[ndx]);
            }

            // Send it to the database. And remember to flag a commit for later.
            pstmt.executeUpdate();
            pstmt.close();
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            // It's bad...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + delete
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Add the Report display attributes to the sn_rep_contents_view_ext table.
     * One (1) row per attribute.
     *
     * @param rec_
     *        The Alert definition to store in the database.
     * @return 0 if successful, otherwise the Oracle error code.
     * @throws java.sql.SQLException
     */
    private int insertDisplay(AlertRec rec_) throws SQLException
    {
        return 0;
    }

    /**
     * Update the recipients list for the Alert report.
     *
     * @param rec_
     *        The Alert definition to be saved to the database.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int updateRecipients(AlertRec rec_) throws SQLException
    {
        // We do not try to keep up with individual changes to the list. We
        // simply
        // wipe out the existing list and replace it with the new one.
        String delete = "delete " + "from sn_recipient_view_ext "
            + "where rep_idseq = ?";
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(delete);
            pstmt.setString(1, rec_.getReportRecNum());

            pstmt.executeUpdate();
            pstmt.close();

            return insertRecipients(rec_);
        }
        catch (SQLException ex)
        {
            // Ooops...
            _conn.rollback();
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + delete
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Add the Report recipients to the sn_report_view_ext table. One (1) row
     * per recipient.
     *
     * @param rec_
     *        The Alert definition to store in the database.
     * @return 0 if successful, otherwise the Oracle error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int insertRecipients(AlertRec rec_) throws SQLException
    {
        // Add the Recipient record(s).
        String insert = "";
        try
        {
            for (int ndx = 0; ndx < rec_.getRecipients().length; ++ndx)
            {
                // As we only populate 1 of possible 3 columns, the Insert
                // statement
                // will be dynamically configured.
                insert = "insert into sbrext.sn_recipient_view_ext ";
                String temp = rec_.getRecipients(ndx);
                if (temp.charAt(0) == '/')
                {
                    // It must be a Context name.
                    insert = insert + "(rep_idseq, conte_idseq, created_by)";
                    temp = temp.substring(1);
                }
                else if (temp.indexOf('@') > -1)
                {
                    // It must be an email address.
                    insert = insert + "(rep_idseq, email, created_by)";
                    if (temp.length() > DBAlert._MAXEMAILLEN)
                    {
                        temp = temp.substring(0, DBAlert._MAXEMAILLEN);
                        rec_.setRecipients(ndx, temp);
                    }
                }
                else if (temp.startsWith("http://") || temp.startsWith("https://"))
                {
                    // It is an process URL remove the slash at the end of URL if it exists
                    if (temp.endsWith("/"))
                    {
                        temp = temp.substring(0, temp.lastIndexOf("/"));
                    }
                    insert = insert + "(rep_idseq, email, created_by)";
                    if (temp.length() > DBAlert._MAXEMAILLEN)
                    {
                        temp = temp.substring(0, DBAlert._MAXEMAILLEN);
                        rec_.setRecipients(ndx, temp);
                    }
                }
                else
                {
                    // It's a user name.
                    insert = insert + "(rep_idseq, ua_name, created_by)";
                }
                insert = insert + " values (?, ?, ?)";

                // Update
                PreparedStatement pstmt = _conn.prepareStatement(insert);
                pstmt.setString(1, rec_.getReportRecNum());
                pstmt.setString(2, temp);
                pstmt.setString(3, _user);
                pstmt.executeUpdate();
                pstmt.close();
            }
            // Remember to commit. It appears that we may flagging a commit when
            // the recipients list
            // is empty, however, the recipients list is never to be empty and
            // the other calling methods
            // depend on this to set the flag.
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            // Ooops...
            _conn.rollback();
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + insert
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * A utility function that will modify the "in" clause on a SQL select to
     * contain the correct number of argument replacements to match the value
     * array provided.
     *
     * @param select_
     *        The SQL select that must contain a single "?" replacement within
     *        an "in" clause as this is the placeholder for expansion to the
     *        appropriate number of "?" arguments to match the length of the
     *        values array. Of course if the array only has a single value the
     *        "in" can be an "=" (equals) operator.
     * @param values_
     *        The array of values to use as bind arguments in the select.
     * @return The comma separated string containing the concatenated results
     *         from the select query.
     */
    private String selectText(String select_, String values_[])
    {
        return selectText(select_, values_, 0);
    }

    /**
     * A utility function that will modify the "in" clause on a SQL select to
     * contain the correct number of argument replacements to match the value
     * array provided.
     *
     * @param select_
     *        The SQL select that must contain a single "?" replacement within
     *        an "in" clause as this is the placeholder for expansion to the
     *        appropriate number of "?" arguments to match the length of the
     *        values array. Of course if the array only has a single value the
     *        "in" can be an "=" (equals) operator.
     * @param values_
     *        The array of values to use as bind arguments in the select.
     * @param flag_
     *        The separator to use in the concatenated string.
     * @return The comma separated string containing the concatenated results
     *         from the select query.
     */
    private String selectText(String select_, String values_[], int flag_)
    {
        // There must be a single "?" in the select to start the method.
        int pos = select_.indexOf('?');
        if (pos < 0)
            return "";

        // As one "?" is already in the select we only add more if the array
        // length is greater than 1.
        String tSelect = select_.substring(0, pos + 1);
        for (int ndx = 1; ndx < values_.length; ++ndx)
            tSelect = tSelect + ",?";
        tSelect = tSelect + select_.substring(pos + 1);

        try
        {
            // Now bind each value in the array to the expanded "?" list.
            PreparedStatement pstmt = _conn.prepareStatement(tSelect);
            for (int ndx = 0; ndx < values_.length; ++ndx)
            {
                pstmt.setString(ndx + 1, values_[ndx]);
            }
            ResultSet rs = pstmt.executeQuery();

            // Concatenate the results into a single comma separated string.
            tSelect = "";
            String sep = (flag_ == 0) ? ", " : "\" OR \"";
            while (rs.next())
            {
                tSelect = tSelect + sep + rs.getString(1).replaceAll("[\\r\\n]", " ");
            }
            rs.close();
            pstmt.close();

            // We always start the string with a comma so be sure to remove it
            // before returning.
            if (tSelect.length() > 0)
            {
                tSelect = tSelect.substring(sep.length());
                if (flag_ == 1)
                    tSelect = "\"" + tSelect + "\"";
            }
            else
                tSelect = "\"(unknown)\"";
        }
        catch (SQLException ex)
        {
            tSelect = ex.toString();
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select_
                + "\n\n" + tSelect;
            _logger.error(_errorMsg);
        }
        return tSelect;
    }

    /**
     * Build the summary text from the content of the alert definition.
     *
     * @param rec_
     *        The alert definition.
     * @return The Alert Definition summary.
     */
    public String buildSummary(AlertRec rec_)
    {
        String select;

        // Build the Summary text. You may wonder why we access the database
        // multiple times when it
        // is possible to collapse all of this into a single query. The
        // additional complexity of the
        // SQL and resulting logic made it unmanageable. If this proves to be a
        // performance problem
        // we can revisit it again in the future. Remember as more features are
        // added for the criteria
        // and monitors the complexity lever will increase.
        String criteria = "";
        int specific = 0;
        int marker = 1;
        if (rec_.getContexts() != null)
        {
            if (rec_.isCONTall())
                criteria = criteria + "Context may be anything ";
            else
            {
                select = "select name || ' (v' || version || ')' as label from sbr.contexts_view "
                    + "where conte_idseq in (?) order by upper(name) ASC";
                criteria = criteria + "Context must be "
                    + selectText(select, rec_.getContexts(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getProtocols() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isPROTOall())
                criteria = criteria + "Protocols may be anything ";
            else
            {
                select = "select long_name || ' (' || proto_id || 'v' || version || ')' as label "
                    + "from sbrext.protocols_view_ext "
                    + "where proto_idseq in (?) order by upper(long_name) ASC";
                criteria = criteria + "Protocols must be "
                    + selectText(select, rec_.getProtocols(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getForms() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isFORMSall())
                criteria = criteria + "Forms / Templates may be anything ";
            else
            {
                select = "select long_name || ' (' || qc_id || 'v' || version || ')' as label "
                    + "from sbrext.quest_contents_view_ext "
                    + "where qc_idseq in (?) order by upper(long_name) ASC";
                criteria = criteria + "Forms / Templates must be "
                    + selectText(select, rec_.getForms(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getSchemes() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isCSall())
                criteria = criteria + "Classification Schemes may be anything ";
            else
            {
                select = "select long_name || ' (' || cs_id || 'v' || version || ')' as label "
                    + "from sbr.classification_schemes_view "
                    + "where cs_idseq in (?) order by upper(long_name) ASC";
                criteria = criteria + "Classification Schemes must be "
                    + selectText(select, rec_.getSchemes(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getSchemeItems() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isCSIall())
                criteria = criteria
                + "Classification Scheme Items may be anything ";
            else
            {
                select = "select csi_name "
                    + "from sbr.class_scheme_items_view "
                    + "where csi_idseq in (?) order by upper(csi_name) ASC";
                criteria = criteria + "Classification Scheme Items must be "
                    + selectText(select, rec_.getSchemeItems(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getACTypes() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isACTYPEall())
                criteria = criteria + "Administered Component Types may be anything ";
            else
            {
                criteria = criteria + "Administered Component Types must be ";
                String list = "";
                for (int ndx = 0; ndx < rec_.getACTypes().length; ++ndx)
                {
                    list = list + " OR \"" + DBAlertUtil.binarySearchS(_DBMAP3, rec_.getACTypes(ndx)) + "\"";
                }
                criteria = criteria + list.substring(4);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getCWorkflow() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isCWFSall())
                criteria = criteria + "Workflow Status may be anything ";
            else
            {
                select = "select asl_name from sbr.ac_status_lov_view "
                    + "where asl_name in (?) order by upper(asl_name) ASC";
                criteria = criteria + "Workflow Status must be "
                    + selectText(select, rec_.getCWorkflow(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getCRegStatus() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isCRSall())
                criteria = criteria + "Registration Status may be anything ";
            else
            {
                select = "select registration_status "
                    + "from sbr.reg_status_lov_view "
                    + "where registration_status in (?) "
                    + "order by upper(registration_status) ASC";
                String txt = selectText(select, rec_.getCRegStatus(), 1);
                criteria = criteria + "Registration Status must be ";
                if (rec_.isCRSnone())
                {
                    criteria = criteria + "\"(none)\"";
                    if (txt.length() > 0)
                        criteria = criteria + " OR ";
                }
                criteria = criteria + txt;
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getCreators() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.getCreators(0).charAt(0) == '(')
                criteria = criteria + "Created By may be anyone ";
            else
            {
                select = "select name from sbr.user_accounts_view "
                    + "where ua_name in (?) order by upper(name) ASC";
                criteria = criteria + "Created By must be "
                    + selectText(select, rec_.getCreators(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getModifiers() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.getModifiers(0).charAt(0) == '(')
                criteria = criteria + "Modified By may be anyone ";
            else
            {
                select = "select name from sbr.user_accounts_view "
                    + "where ua_name in (?) order by upper(name) ASC";
                criteria = criteria + "Modified By must be "
                    + selectText(select, rec_.getModifiers(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (criteria.length() > 0)
            criteria = criteria + " AND\n";
        switch (rec_.getDateFilter())
        {
            case _DATECONLY:
                criteria = criteria + "Reporting Dates are compared to Date Created only ";
                specific += marker;
                break;

            case _DATEMONLY:
                criteria = criteria + "Reporting Dates are compared to Date Modified only ";
                specific += marker;
                break;

            case _DATECM:
            default:
                criteria = criteria + "Reporting Dates are compared to Date Created and Date Modified ";
                break;
        }

        if (specific > 0)
            criteria = "Criteria:\n" + criteria + "\n";
        else
            criteria = "Criteria:\nAll records within the caDSR\n";

        String monitors = "";
        specific = 0;
        marker = 1;
        if (rec_.getAWorkflow() != null)
        {
            if (rec_.getAWorkflow(0).charAt(0) != '(')
            {
                select = "select asl_name from sbr.ac_status_lov_view "
                    + "where asl_name in (?) order by upper(asl_name) ASC";
                monitors = monitors + "Workflow Status changes to "
                    + selectText(select, rec_.getAWorkflow(), 1);
            }
            else if (rec_.getAWorkflow(0).equals("(Ignore)"))
                monitors = monitors + ""; // "Workflow Status changes are
            // ignored ";
            else
            {
                monitors = monitors + "Workflow Status changes to anything ";
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getARegis() != null)
        {
            if (rec_.getARegis(0).charAt(0) != '(')
            {
                select = "select registration_status "
                    + "from sbr.reg_status_lov_view "
                    + "where registration_status in (?) "
                    + "order by upper(registration_status) ASC";
                if (monitors.length() > 0)
                    monitors = monitors + " OR\n";
                monitors = monitors + "Registration Status changes to "
                    + selectText(select, rec_.getARegis(), 1);
            }
            else if (rec_.getARegis(0).equals("(Ignore)"))
                monitors = monitors + ""; // "Registration Status changes are
            // ignored ";
            else
            {
                if (monitors.length() > 0)
                    monitors = monitors + " OR\n";
                monitors = monitors
                    + "Registration Status changes to anything ";
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getAVersion() != DBAlert._VERIGNCHG)
        {
            if (rec_.getAVersion() == DBAlert._VERANYCHG)
                specific += marker;
            if (monitors.length() > 0)
                monitors = monitors + " OR\n";
            switch (rec_.getAVersion())
            {
                case DBAlert._VERANYCHG:
                    monitors = monitors + "Version changes to anything\n";
                    break;
                case DBAlert._VERMAJCHG:
                    monitors = monitors
                        + "Version Major Revision changes to anything\n";
                    break;
                case DBAlert._VERIGNCHG:
                    monitors = monitors + "";
                    break; // "Version changes are ignored\n"; break;
                case DBAlert._VERSPECHG:
                    monitors = monitors + "Version changes to \""
                        + rec_.getActVerNum() + "\"\n";
                    break;
            }
        }
        if (monitors.length() == 0)
            monitors = "\nMonitors:\nIgnore all changes. Reports are always empty.\n";
        else if (specific == 7)
            monitors = "\nMonitors:\nAll Change Activities\n";
        else
            monitors = "\nMonitors:\n" + monitors;

        return criteria + monitors;
    }

    /**
     * Set the owner of the Alert Definition.
     *
     * @param rec_ The Alert Definition with the new creator already set.
     */
    public void setOwner(AlertRec rec_)
    {
        // Ensure data is clean.
        rec_.setDependancies();

        // Update creator in database.
        String update = "update sbrext.sn_alert_view_ext set created_by = ?, modified_by = ? where al_idseq = ?";
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(update);
            pstmt.setString(1, rec_.getCreator());
            pstmt.setString(2, _user);
            pstmt.setString(3, rec_.getAlertRecNum());
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            int errorCode = ex.getErrorCode();
            String errorMsg = errorCode + ": " + update
                + "\n\n" + ex.toString();
            _logger.error(errorMsg);
        }
    }

    /**
     * Get the type of the AC id from the database.
     * @param id_ The AC id.
     * @return The [0] is the type and the [1] is the name of the AC.
     */
    public String [] getACtype(String id_)
    {
        String out[] = new String[2];
        String select =
            "select 'conte', name from sbr.contexts_view where conte_idseq = ? "
            + "union "
            + "select 'cs', long_name from sbr.classification_schemes_view where cs_idseq = ? "
            + "union "
            + "select 'csi', csi_name from sbr.class_scheme_items_view where csi_idseq = ? "
            + "union "
            + "select 'qc', long_name from sbrext.quest_contents_view_ext where qc_idseq = ? and qtl_name in ('TEMPLATE','CRF') "
            + "union "
            + "select 'proto', long_name from sbrext.protocols_view_ext where proto_idseq = ?";

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, id_);
            pstmt.setString(2, id_);
            pstmt.setString(3, id_);
            pstmt.setString(4, id_);
            pstmt.setString(5, id_);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                out[0] = rs.getString(1);
                out[1] = rs.getString(2);
            }
            else
            {
                out[0] = null;
                out[1] = null;
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            int errorCode = ex.getErrorCode();
            String errorMsg = errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(errorMsg);
        }
        return out;
    }

    /**
     * Look for an Alert owned by the user with a Query which
     * references the id specified.
     *
     * @param id_ The Context, Form, CS, etc ID_SEQ value.
     * @param user_ The user who should own the Alert if it exists.
     * @return true if the user already watches the id, otherwise false.
     */
    public String checkQuery(String id_, String user_)
    {
        String select = "select al.name "
            + "from sbrext.sn_alert_view_ext al, sbrext.sn_query_view_ext qur "
            + "where al.created_by = ? and qur.al_idseq = al.al_idseq and qur.value = ?";

        String rc = null;
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, user_);
            pstmt.setString(2, id_);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                rc = rs.getString(1);
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            int errorCode = ex.getErrorCode();
            String errorMsg = errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(errorMsg);
        }

        return rc;
    }

    /**
     * Check the specified user id for Sentinel Tool Administration privileges.
     *
     * @param user_ The user id as used during Sentinel Tool Login.
     * @return true if the user has administration privileges, otherwise false.
     */
    public boolean checkToolAdministrator(String user_)
    {
        String select = "select 1 from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' "
            + "and property like 'ADMIN.%' "
            + "and value like '%0%' "
            + "and ua_name = '" + user_ + "' "
            + "and rownum < 2";
        int rows = testDB(select);
        return rows == 1;
    }

    /**
     * Retrieve the CDE Browser URL if available.
     *
     * @return The URL string.
     */
    public String selectBrowserURL()
    {
        String select = "select value from sbrext.tool_options_view_ext "
            + "where tool_name = 'CDEBrowser' and property = 'URL'";

        String rc = null;

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                rc = rs.getString(1);
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
        }

        return rc;
    }

    /**
     * Retrieve the Report Threshold
     *
     * @return The number of rows to allow in a report.
     */
    public int selectReportThreshold()
    {
        String select = "select value from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' and property = 'REPORT.THRESHOLD.LIMIT'";

        int rc = 100;

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                rc = rs.getInt(1);
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
        }

        return rc;
    }

    /**
     * Read the Query clause from the database for the Alert definition
     * specified.
     *
     * @param rec_
     *        The Alert record to contain the Query clause.
     * @return 0 if successful, otherwise the database error code.
     */
    private int selectQuery(AlertRec rec_)
    {
        String select = "select record_type, data_type, property, value "
            + "from sbrext.sn_query_view_ext " + "where al_idseq = ?"; // order
        // by
        // record_type
        // ASC,
        // data_type
        // ASC,
        // property
        // ASC";
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, rec_.getAlertRecNum());
            ResultSet rs = pstmt.executeQuery();
            Vector<String> context = new Vector<String>();
            Vector<String> actype = new Vector<String>();
            Vector<String> scheme = new Vector<String>();
            Vector<String> schemeitem = new Vector<String>();
            Vector<String> form = new Vector<String>();
            Vector<String> protocol = new Vector<String>();
            Vector<String> creator = new Vector<String>();
            Vector<String> modifier = new Vector<String>();
            Vector<String> workflow = new Vector<String>();
            Vector<String> regis = new Vector<String>();
            Vector<String> cregis = new Vector<String>();
            Vector<String> cwork = new Vector<String>();

            // After reading the query set we have to partition it into the
            // appropriate individual
            // variables. As the data stored in the query is internal
            // representations there is
            // no point to attempting to order it. Also for any one Alert there
            // isn't enough rows
            // to warrant the extra coding or logical overhead.
            while (rs.next())
            {
                char rtype = rs.getString(1).charAt(0);
                String dtype = rs.getString(2);
                String value = rs.getString(4);
                if (rtype == _CRITERIA)
                {
                    if (dtype.equals(_CONTEXT))
                        context.add(value);
                    else if (dtype.equals(_FORM))
                        form.add(value);
                    else if (dtype.equals(_PROTOCOL))
                        protocol.add(value);
                    else if (dtype.equals(_SCHEME))
                        scheme.add(value);
                    else if (dtype.equals(_SCHEMEITEM))
                        schemeitem.add(value);
                    else if (dtype.equals(_CREATOR))
                        creator.add(value);
                    else if (dtype.equals(_MODIFIER))
                        modifier.add(value);
                    else if (dtype.equals(_ACTYPE))
                        actype.add(value);
                    else if (dtype.equals(_REGISTER))
                        cregis.add(value);
                    else if (dtype.equals(_STATUS))
                        cwork.add(value);
                    else if (dtype.equals(_DATEFILTER))
                    {
                        rec_.setDateFilter(value);
                    }
                }
                else if (rtype == _MONITORS)
                {
                    if (dtype.equals(_STATUS))
                        workflow.add(value);
                    else if (dtype.equals(_REGISTER))
                        regis.add(value);
                    else if (dtype.equals(_VERSION))
                    {
                        rec_.setAVersion(rs.getString(3));
                        rec_.setActVerNum(value);
                    }
                }
            }
            rs.close();
            pstmt.close();

            // Move the data into appropriate arrays within the Alert record to
            // simplify use
            // downstream.
            String list[] = null;
            if (context.size() == 0)
            {
                rec_.setContexts(null);
            }
            else
            {
                list = new String[context.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) context.get(ndx);
                rec_.setContexts(list);
            }

            if (actype.size() == 0)
            {
                rec_.setACTypes(null);
            }
            else
            {
                list = new String[actype.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) actype.get(ndx);
                rec_.setACTypes(list);
            }

            if (protocol.size() == 0)
            {
                rec_.setProtocols(null);
            }
            else
            {
                list = new String[protocol.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) protocol.get(ndx);
                rec_.setProtocols(list);
            }

            if (form.size() == 0)
            {
                rec_.setForms(null);
            }
            else
            {
                list = new String[form.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) form.get(ndx);
                rec_.setForms(list);
            }

            if (scheme.size() == 0)
            {
                rec_.setSchemes(null);
            }
            else
            {
                list = new String[scheme.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) scheme.get(ndx);
                rec_.setSchemes(list);
            }

            if (schemeitem.size() == 0)
            {
                rec_.setSchemeItems(null);
            }
            else
            {
                list = new String[schemeitem.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) schemeitem.get(ndx);
                rec_.setSchemeItems(list);
            }

            if (cregis.size() == 0)
            {
                rec_.setCRegStatus(null);
            }
            else
            {
                list = new String[cregis.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) cregis.get(ndx);
                rec_.setCRegStatus(list);
            }

            if (cwork.size() == 0)
            {
                rec_.setCWorkflow(null);
            }
            else
            {
                list = new String[cwork.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) cwork.get(ndx);
                rec_.setCWorkflow(list);
            }

            if (creator.size() == 0)
            {
                rec_.setCreators(null);
            }
            else
            {
                list = new String[creator.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) creator.get(ndx);
                rec_.setCreators(list);
            }

            if (modifier.size() == 0)
            {
                rec_.setModifiers(null);
            }
            else
            {
                list = new String[modifier.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) modifier.get(ndx);
                rec_.setModifiers(list);
            }

            if (workflow.size() == 0)
            {
                rec_.setAWorkflow(null);
            }
            else
            {
                list = new String[workflow.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) workflow.get(ndx);
                rec_.setAWorkflow(list);
            }

            if (regis.size() == 0)
            {
                rec_.setARegis(null);
            }
            else
            {
                list = new String[regis.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) regis.get(ndx);
                rec_.setARegis(list);
            }

            // Create the summary string now the data is loaded.
            rec_.setSummary(buildSummary(rec_));

            return 0;
        }
        catch (SQLException ex)
        {
            // Ooops...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Update the Query details for the Alert.
     *
     * @param rec_
     *        The Alert definition to be updated.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int updateQuery(AlertRec rec_) throws SQLException
    {
        // First we delete the existing Query details as it's easier to wipe out
        // the old and add
        // the new than trying to track individual changes.
        String delete = "delete " + "from sbrext.sn_query_view_ext "
            + "where al_idseq = ?";
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(delete);
            pstmt.setString(1, rec_.getAlertRecNum());
            pstmt.executeUpdate();
            pstmt.close();

            return insertQuery(rec_);
        }
        catch (SQLException ex)
        {
            // Ooops...
            _conn.rollback();
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + delete
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Add the Query details to the Alert in the database.
     *
     * @param rec_
     *        The Alert definition to be added to the database.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int insertQuery(AlertRec rec_) throws SQLException
    {
        String insert = "insert into sbrext.sn_query_view_ext (al_idseq, record_type, data_type, property, value, created_by) "
            + "values (?, ?, ?, ?, ?, ?)";

        int marker = 0;
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(insert);
            pstmt.setString(1, rec_.getAlertRecNum());
            pstmt.setString(2, "C");
            pstmt.setString(6, _user);

            // We only want to record those items selected by the user that
            // require special processing. For
            // example, if (All) contexts were selected by the user we do not
            // record (All) in the database
            // because the downstream processing of the Alert only cares about
            // looking for specific criteria
            // and monitors. In other words, we don't want to waste time
            // checking the context when (All) was
            // selected because it will always logically test true.
            ++marker;
            if (!rec_.isCONTall())
            {
                pstmt.setString(3, _CONTEXT);
                pstmt.setString(4, "CONTE_IDSEQ");
                for (int ndx = 0; ndx < rec_.getContexts().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getContexts(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isPROTOall())
            {
                pstmt.setString(3, _PROTOCOL);
                pstmt.setString(4, "PROTO_IDSEQ");
                for (int ndx = 0; ndx < rec_.getProtocols().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getProtocols(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isFORMSall())
            {
                pstmt.setString(3, _FORM);
                pstmt.setString(4, "QC_IDSEQ");
                for (int ndx = 0; ndx < rec_.getForms().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getForms(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isCSall())
            {
                pstmt.setString(3, _SCHEME);
                pstmt.setString(4, "CS_IDSEQ");
                for (int ndx = 0; ndx < rec_.getSchemes().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getSchemes(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isCSIall())
            {
                pstmt.setString(3, _SCHEMEITEM);
                pstmt.setString(4, "CSI_IDSEQ");
                for (int ndx = 0; ndx < rec_.getSchemeItems().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getSchemeItems(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (rec_.getCreators(0).equals(Constants._STRALL) == false)
            {
                pstmt.setString(3, _CREATOR);
                pstmt.setString(4, "UA_NAME");
                for (int ndx = 0; ndx < rec_.getCreators().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getCreators(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (rec_.getModifiers(0).equals(Constants._STRALL) == false)
            {
                pstmt.setString(3, _MODIFIER);
                pstmt.setString(4, "UA_NAME");
                for (int ndx = 0; ndx < rec_.getModifiers().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getModifiers(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isACTYPEall())
            {
                pstmt.setString(3, _ACTYPE);
                pstmt.setString(4, "ABBREV");
                for (int ndx = 0; ndx < rec_.getACTypes().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getACTypes(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (rec_.getDateFilter() != DBAlert._DATECM)
            {
                pstmt.setString(3, _DATEFILTER);
                pstmt.setString(4, "CODE");
                pstmt.setString(5, Integer.toString(rec_.getDateFilter()));
                pstmt.executeUpdate();
            }

            ++marker;
            if (!rec_.isCRSall())
            {
                pstmt.setString(3, _REGISTER);
                pstmt.setString(4, "REGISTRATION_STATUS");
                for (int ndx = 0; ndx < rec_.getCRegStatus().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getCRegStatus(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isCWFSall())
            {
                pstmt.setString(3, _STATUS);
                pstmt.setString(4, "ASL_NAME");
                for (int ndx = 0; ndx < rec_.getCWorkflow().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getCWorkflow(ndx));
                    pstmt.executeUpdate();
                }
            }

            marker += 100;
            pstmt.setString(2, "M");

            ++marker;
            if (rec_.getAWorkflow(0).equals(Constants._STRANY) == false)
            {
                pstmt.setString(3, _STATUS);
                pstmt.setString(4, "ASL_NAME");
                for (int ndx = 0; ndx < rec_.getAWorkflow().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getAWorkflow(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (rec_.getARegis(0).equals(Constants._STRANY) == false)
            {
                pstmt.setString(3, _REGISTER);
                pstmt.setString(4, "REGISTRATION_STATUS");
                for (int ndx = 0; ndx < rec_.getARegis().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getARegis(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (rec_.getAVersion() != DBAlert._VERANYCHG)
            {
                pstmt.setString(3, _VERSION);
                pstmt.setString(4, rec_.getAVersionString());
                pstmt.setString(5, rec_.getActVerNum());
                pstmt.executeUpdate();
            }

            // Remember to commit.
            ++marker;
            pstmt.close();
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            // Ooops...
            _conn.rollback();
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = "(" + marker + "): " + _errorCode + ": "
                + insert + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Clean the name of any illegal characters and truncate if needed.
     *
     * @param name_
     *        The name of the Alert.
     * @return The corrected name.
     */
    private String cleanName(String name_)
    {
        String name = null;
        if (name_ != null)
        {
            name = name_.replaceAll("[\"]", "");
            if (name.length() > DBAlert._MAXNAMELEN)
                name = name.substring(0, DBAlert._MAXNAMELEN);
        }
        return name;
    }

    /**
     * Clean the inactive reason and truncate if needed.
     *
     * @param reason_
     *        The reason message.
     * @return The corrected message.
    private String cleanReason(String reason_)
    {
        String reason = reason_;
        if (reason != null && reason.length() > DBAlert._MAXREASONLEN)
        {
            reason = reason.substring(0, DBAlert._MAXREASONLEN);
        }
        return reason;
    }
     */

    /**
     * Clean the Alert Report introduction and truncate if needed.
     *
     * @param intro_
     *        The introduction.
     * @return The cleaned introduction.
     */
    private String cleanIntro(String intro_)
    {
        String intro = intro_;
        if (intro != null && intro.length() > DBAlert._MAXINTROLEN)
        {
            intro = intro.substring(0, DBAlert._MAXINTROLEN);
        }
        return intro;
    }

    /**
     * Clean all the user enterable parts of an Alert and truncate to the
     * database allowed length.
     *
     * @param rec_
     *        The Alert to be cleaned.
     */
    private void cleanRec(AlertRec rec_)
    {
        String temp = cleanName(rec_.getName());
        rec_.setName(temp);
        temp = rec_.getInactiveReason(false);
        rec_.setInactiveReason(temp);
        temp = cleanIntro(rec_.getIntro(false));
        rec_.setIntro(temp, false);
    }

    /**
     * Insert the properties for the Alert definition and retrieve the new
     * database generated ID for this Alert.
     *
     * @param rec_
     *        The Alert to be stored in the database.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int insertProperties(AlertRec rec_) throws SQLException
    {
        // Define the SQL insert. Remember date_created, date_modified, creator
        // and modifier are controlled
        // by triggers. Also (as of 10/21/2004) after the insert the
        // date_modified is still set by the insert
        // trigger.
        String insert = "begin insert into sbrext.sn_alert_view_ext "
            + "(name, auto_freq_unit, al_status, begin_date, end_date, status_reason, auto_freq_value, created_by) "
            + "values (?, ?, ?, ?, ?, ?, ?, ?) return al_idseq into ?; end;";

        CallableStatement pstmt = null;
        cleanRec(rec_);
        try
        {
            // Set all the SQL arguments.
            pstmt = _conn.prepareCall(insert);
            pstmt.setString(1, rec_.getName());
            pstmt.setString(2, rec_.getFreqString());
            pstmt.setString(3, rec_.getActiveString());
            pstmt.setTimestamp(4, rec_.getStart());
            pstmt.setTimestamp(5, rec_.getEnd());
            pstmt.setString(6, rec_.getInactiveReason(false));
            pstmt.setInt(7, rec_.getDay());
            pstmt.setString(8, _user);
            pstmt.registerOutParameter(9, Types.CHAR);

            // Insert the new record and flag a commit for later.
            pstmt.executeUpdate();

            // We need the record id to populate the foreign keys for other
            // tables.
            rec_.setAlertRecNum(pstmt.getString(9));
            pstmt.close();
            return 0;
        }
        catch (SQLException ex)
        {
            // Ooops...
            rec_.setAlertRecNum(null);
            _conn.rollback();
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + insert
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Insert the Report details for the Alert definition into the database and
     * retrieve the new report id.
     *
     * @param rec_
     *        The Alert definition to be stored in the database.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int insertReport(AlertRec rec_) throws SQLException
    {
        // Add the Report record.
        String insert = "begin insert into sbrext.sn_report_view_ext "
            + "(al_idseq, comments, include_property_ind, style, send, acknowledge_ind, assoc_lvl_num, created_by) "
            + "values (?, ?, ?, ?, ?, ?, ?, ?) return rep_idseq into ?; end;";

        CallableStatement pstmt = null;
        try
        {
            pstmt = _conn.prepareCall(insert);
            pstmt.setString(1, rec_.getAlertRecNum());
            pstmt.setString(2, rec_.getIntro(false));
            pstmt.setString(3, rec_.getIncPropSectString());
            pstmt.setString(4, rec_.getReportStyleString());
            pstmt.setString(5, rec_.getReportEmptyString());
            pstmt.setString(6, rec_.getReportAckString());
            pstmt.setInt(7, rec_.getIAssocLvl());
            pstmt.setString(8, _user);
            pstmt.registerOutParameter(9, Types.CHAR);
            pstmt.executeUpdate();

            // We need the record id to populate the foreign keys for other
            // tables.
            rec_.setReportRecNum(pstmt.getString(9));
            pstmt.close();
            return 0;
        }
        catch (SQLException ex)
        {
            // Ooops...
            rec_.setAlertRecNum(null);
            _conn.rollback();
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + insert
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Insert a DE, DEC or VD into the user reserved CSI to be monitored.
     *
     * @param idseq_ the database id of the AC to be monitored.
     * @param user_ the user id for the reserved CSI
     * @return the id of the CSI if successful, null if a problem.
     */
    public String insertAC(String idseq_, String user_)
    {
        String user = user_.toUpperCase();

        try
        {
            CallableStatement stmt;
            stmt = _conn.prepareCall("begin SBREXT_CDE_CURATOR_PKG.ADD_TO_SENTINEL_CS(?,?,?); end;");
            stmt.registerOutParameter(3, java.sql.Types.VARCHAR);
            stmt.setString(2, user);
            stmt.setString(1, idseq_);
            stmt.execute();
            String csi = stmt.getString(3);
            stmt.close();
            _needCommit = true;

            return csi;
        }
        catch (SQLException ex)
        {
            // Ooops...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    /**
     * Perform an insert of a new record. The record number element of the class
     * is not used AND it is not returned by this method. All other elements
     * must be complete and correct.
     *
     * @param rec_
     *        The Alert definition to insert into the database table.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int insertAlert(AlertRec rec_)
    {
        // Ensure required data dependancies.
        rec_.setDependancies();

        // Update the database.
        try
        {
            int xxx = insertProperties(rec_);
            if (xxx == 0)
            {
                xxx = insertReport(rec_);
                if (xxx == 0)
                {
                    xxx = insertRecipients(rec_);
                    if (xxx == 0)
                    {
                        xxx = insertDisplay(rec_);
                        if (xxx == 0)
                        {
                            xxx = insertQuery(rec_);
                            if (xxx != 0)
                                rec_.setAlertRecNum(null);
                        }
                        else
                            rec_.setAlertRecNum(null);
                    }
                    else
                        rec_.setAlertRecNum(null);
                }
                else
                    rec_.setAlertRecNum(null);
            }
            else
                rec_.setAlertRecNum(null);
            return xxx;
        }
        catch (SQLException ex)
        {
            // Ooops...
            rec_.setAlertRecNum(null);
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Retrieve a more user friendly version of the user id.
     *
     * @param id_
     *        The id as would be entered at logon.
     * @return null if the user id was not found in the sbr.user_accounts table,
     *         otherwise the 'name' value of the matching row.
     */
    public String selectUserName(String id_)
    {
        // Define the SQL select.
        String select = "select uav.name "
            + "from sbr.user_accounts_view uav, sbrext.user_contexts_view ucv "
            + "where uav.ua_name = ? and ucv.ua_name = uav.ua_name and ucv.privilege = 'W'";
        PreparedStatement pstmt = null;
        String result;
        ResultSet rs = null;

        try
        {
            // Get ready...
            pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, id_);

            // Go!
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                // Get the name, remember 1 indexing.
                result = rs.getString(1);
            }
            else
            {
                // User must have some kind of write permissions.
                result = null;
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // We've got trouble.
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            result = null;
        }
        return result;
    }

    private class ResultsData1
    {
        /**
         * The label
         */
        public String _label;

        /**
         * The key
         */
        public String _val;
    }

    /**
     * Used for method return values.
     *
     */
    private class Results1
    {
        /**
         * The return code from the database.
         */
        public int    _rc;

        /**
         * The data
         */
        public ResultsData1[] _data;
    }

    /**
     * Do a basic search with a single column result.
     *
     * @param select_ the SQL select
     * @return the array of results
     */
    private String[] getBasicData0(String select_)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            // Prepare the statement.
            pstmt = _conn.prepareStatement(select_);

            // Get the list.
            rs = pstmt.executeQuery();
            Vector<String> data = new Vector<String>();
            while (rs.next())
            {
                data.add(rs.getString(1));
            }

            String[] list = new String[data.size()];
            for (int i = 0; i < list.length; ++i)
            {
                list[i] = data.get(i);
            }

            rs.close();
            pstmt.close();

            return (list.length > 0) ? list : null;
        }
        catch (SQLException ex)
        {
            // Bad...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    /**
     * Execute the specified SQL select query and return a label/value pair.
     *
     * @param select_
     *        The SQL select statement.
     * @param flag_
     *        True to prefix "(All)" to the result set. False to return the
     *        result set unaltered.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    private Results1 getBasicData1(String select_, boolean flag_)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector<ResultsData1> results = new Vector<ResultsData1>();
        Results1 data = new Results1();

        try
        {
            // Prepare the statement.
            pstmt = _conn.prepareStatement(select_);

            // Get the list.
            rs = pstmt.executeQuery();
            ResultsData1 rec;
            while (rs.next())
            {
                // Remember about the 1 (one) based indexing.
                rec = new ResultsData1();
                rec._val = rs.getString(1);
                rec._label = rs.getString(2);
                results.add(rec);
            }
            rs.close();
            pstmt.close();

            // We know there will always be someone in the table but we should
            // follow good
            // programming.
            if (results.size() == 0)
            {
                data._data = null;
            }
            else
            {
                // Move the list from a Vector to an array and add "(All)" to
                // the beginning.
                int count = results.size() + ((flag_) ? 1 : 0);
                data._data = new ResultsData1[count];
                int ndx;
                if (flag_)
                {
                    data._data[0] = new ResultsData1();
                    data._data[0]._label = Constants._STRALL;
                    data._data[0]._val = Constants._STRALL;
                    ndx = 1;
                }
                else
                {
                    ndx = 0;
                }
                int cnt = 0;
                for (; ndx < count; ++ndx)
                {
                    rec = (ResultsData1) results.get(cnt++);
                    data._data[ndx] = new ResultsData1();
                    data._data[ndx]._label = rec._label.replaceAll("[\\s]", " ");
                    data._data[ndx]._val = rec._val;
                }
            }
            data._rc = 0;
        }
        catch (SQLException ex)
        {
            // Bad...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            data._rc = _errorCode;
        }
        return data;
    }

    /**
     * Retrieve all the context id's for which a specific user has write
     * permission.
     *
     * @param user_
     *        The user id as stored in user_accounts_view.ua_name.
     * @return The array of context id values.
     */
    public String[] selectContexts(String user_)
    {
        String select = "select cv.conte_idseq "
            + "from sbr.contexts_view cv, sbrext.user_contexts_view ucv "
            + "where ucv.ua_name = ? and ucv.privilege = 'W' and ucv.name not in ('TEST','Test','TRAINING','Training') and cv.name = ucv.name";

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, user_);
            ResultSet rs = pstmt.executeQuery();
            Vector<String> list = new Vector<String>();
            while (rs.next())
            {
                list.add(rs.getString(1));
            }
            rs.close();
            pstmt.close();

            String temp[] = new String[list.size()];
            for (int ndx = 0; ndx < temp.length; ++ndx)
            {
                temp[ndx] = (String) list.get(ndx);
            }
            return temp;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    /**
     * Retrieve the list of contexts for which a user has write permission.
     *
     * @param user_
     *        The user id as stored in user_accounts_view.ua_name.
     * @return The concatenated comma separated string listing the context
     *         names.
     */
    public String selectContextString(String user_)
    {
        String select = "select name "
            + "from sbrext.user_contexts_view "
            + "where ua_name in (?) and privilege = 'W' and name not in ('TEST','Test','TRAINING','Training') "
            + "order by upper(name) ASC";
        String temp[] = new String[1];
        temp[0] = user_;
        return selectText(select, temp);
    }

    /**
     * Retrieve the list of all users from the database with a suffix of the
     * context names for which each has write permission. An asterisk following
     * the name indicates the email address is missing.
     * <p>
     * The getUsers, getUserList and getUserVals are a set of methods that must
     * be used in a specific way. The getUsers() method is called first to
     * populate a set of temporary data elements which can be retrieved later.
     * The getUserList() method accesses the user names of the returned data and
     * subsequently sets the data element to null so the memory may be
     * reclaimed. The getUserVals() method accesses the user ids of the returned
     * data and subsequently sets the data element to null so the memory may be
     * reclaimed. Consequently getUsers() must be called first, followed by
     * either getUserList() or getUserVals(). Further getUserList() and
     * getUserVals() should be called only once after each invocation of
     * getUsers() as additional calls will always result in a null return. See
     * the comments for these other methods for more details.
     *
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUserList getUserList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUserVals getUserVals()
     */
    public int getUsers()
    {
        // Get the user names and id's.
        String select = "select ua_name, nvl2(electronic_mail_address, 'y', 'n') || alert_ind as eflag, name "
            + "from sbr.user_accounts_view order by upper(name) ASC";
        Results2 rec2 = getBasicData2(select);
        if (rec2._rc == 0)
        {
            _namesList = new String[rec2._data.length];
            _namesVals = new String[rec2._data.length];
            for (int i = 0; i < _namesList.length; ++i)
            {
                _namesList[i] = rec2._data[i]._label;
                _namesVals[i] = rec2._data[i]._id1;
            }

            // Build the list of names that are exempt from Context Curator
            // Group broadcasts.
            _namesExempt = "";

            // Get the context names for which each id has write permission.
            select = "select distinct uav.name, ucv.name "
                + "from sbrext.user_contexts_view ucv, sbr.user_accounts_view uav "
                + "where ucv.privilege = 'W' and ucv.ua_name = uav.ua_name "
                + "order by upper(uav.name) ASC, upper(ucv.name) ASC";
            Results1 rec = getBasicData1(select, false);
            if (rec._rc == 0)
            {
                // Build the user list in the format <user name>[*] [(context
                // list)] where <user name> is the
                // in user presentable form followed by an optional asterisk to
                // indicate the email address is missing
                // from the user record followed by an optional list of context
                // names for those contexts which the
                // user has write permissions.
                int vcnt = 0;
                int ncnt = 1;
                String prefix = " (";
                String fixname = "";
                while (ncnt < _namesList.length && vcnt < rec._data.length)
                {
                    int test = _namesList[ncnt].compareToIgnoreCase(rec._data[vcnt]._val);

                    if (test < 0)
                    {
                        // Add the missing email flag to the suffix.
                        String suffix = "";
                        if (rec2._data[ncnt]._id2.charAt(0) == 'n')
                            suffix = "*";

                        // Add the Context list to the suffix.
                        if (prefix.charAt(0) == ',')
                        {
                            if (rec2._data[ncnt]._id2.charAt(1) == 'N')
                                _namesExempt = _namesExempt + ", "
                                    + _namesList[ncnt];
                            suffix = suffix + fixname + ")";
                            prefix = " (";
                            fixname = "";
                        }

                        // Add the suffix to the name.
                        _namesList[ncnt] = _namesList[ncnt] + suffix;
                        ++ncnt;
                    }
                    else if (test > 0)
                    {
                        ++vcnt;
                    }
                    else
                    {
                        fixname = fixname + prefix + rec._data[vcnt]._label;
                        prefix = ", ";
                        ++vcnt;
                    }
                }
            }

            // Fix the exempt list.
            if (_namesExempt.length() == 0)
                _namesExempt = null;
            else
                _namesExempt = _namesExempt.substring(2);
        }
        return rec2._rc;
    }

    /**
     * Retrieve the valid user list. The method getUsers() must be called first.
     * Once this method is used the internal copy is deleted to reclaim the
     * memory space.
     *
     * @return An array of strings from the sbr.user_accounts.name column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     */
    public String[] getUserList()
    {
        String temp[] = _namesList;
        _namesList = null;
        return temp;
    }

    /**
     * Retrieve the list of users exempt from Context Curator broadcasts. The
     * method getUsers() must be called first. Once this method is used the
     * internal copy is deleted to reclaim the memory space.
     *
     * @return A comma separated list of names.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     */
    public String getUserExempts()
    {
        String temp = _namesExempt;
        _namesExempt = null;
        return temp;
    }

    /**
     * Retrieve the valid user list. The method getUsers() must be called first.
     * Once this method is used the internal copy is deleted to reclaim the
     * memory space.
     *
     * @return An array of strings from the sbr.user_accounts.ua_name column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     */
    public String[] getUserVals()
    {
        String temp[] = _namesVals;
        _namesVals = null;
        return temp;
    }

    /**
     * Retrieve the Context names and id's from the database. This follows the
     * pattern documented with getUsers().
     *
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getGroupList getGroupList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getGroupVals getGroupVals()
     */
    public int getGroups()
    {
        String select = "select uav.ua_name, '/' || cv.conte_idseq as id, 0, "
            + "uav.name || nvl2(uav.electronic_mail_address, '', '*') as name, ucv.name "
            + "from sbrext.user_contexts_view ucv, sbr.user_accounts_view uav, sbr.contexts_view cv "
            + "where ucv.privilege = 'W' "
            + "and ucv.ua_name = uav.ua_name "
            + "and uav.alert_ind = 'Yes' "
            + "and ucv.name = cv.name "
            + "and cv.conte_idseq NOT IN ( "
            + "select tov.value "
            + "from sbrext.tool_options_view_ext tov "
            + "where tov.tool_name = 'SENTINEL' and "
            + "tov.property like 'BROADCAST.EXCLUDE.CONTEXT.%.CONTE_IDSEQ') "
            + "order by upper(ucv.name) ASC, upper(uav.name) ASC";

        Results3 rec = getBasicData3(select, false);
        if (rec._rc == 0)
        {
            // Count the number of groups.
            String temp = rec._data[0]._id2;
            int cnt = 1;
            for (int ndx = 1; ndx < rec._data.length; ++ndx)
            {
                if (!temp.equals(rec._data[ndx]._id2))
                {
                    temp = rec._data[ndx]._id2;
                    ++cnt;
                }
            }

            // Allocate space for the lists.
            _groupsList = new String[cnt + rec._data.length];
            _groupsVals = new String[_groupsList.length];

            // Copy the data.
            temp = "";
            cnt = 0;
            for (int ndx = 0; ndx < rec._data.length; ++ndx)
            {
                if (!temp.equals(rec._data[ndx]._id2))
                {
                    temp = rec._data[ndx]._id2;
                    _groupsList[cnt] = rec._data[ndx]._label2;
                    _groupsVals[cnt] = rec._data[ndx]._id2;
                    ++cnt;
                }
                _groupsList[cnt] = rec._data[ndx]._label1;
                _groupsVals[cnt] = rec._data[ndx]._id1;
                ++cnt;
            }
            return 0;
        }
        return rec._rc;
    }

    /**
     * Retrieve the valid context list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     *
     * @return An array of strings from the sbr.contexts_view.name column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getGroups getGroups()
     */
    public String[] getGroupList()
    {
        String temp[] = _groupsList;
        _groupsList = null;
        return temp;
    }

    /**
     * Retrieve the valid context id list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     *
     * @return An array of strings from the sbr.contexts_view.conte_idseq column
     *         and prefixed with a '/' character.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getGroups getGroups()
     */
    public String[] getGroupVals()
    {
        String temp[] = _groupsVals;
        _groupsVals = null;
        return temp;
    }

    /**
     * Get the list of unused concepts
     *
     * @param ids_ the list of unused concept ids
     * @return the list of name, public id and version
     */
    public String[] reportUnusedConcepts(String[] ids_)
    {
        String cs1 = AuditReport._ColSeparator;
        String cs2 = " || '" + cs1 + "' || ";
        String select =
            "SELECT 'Name" + cs1 + "Public ID" + cs1 + "Version" + cs1
            + "Date Accessed" + cs1 + "Workflow Status" + cs1 + "EVS Source" + cs1
            + "Concept Code' as title, ' ' as lname from dual UNION ALL "
            + "SELECT con.long_name" + cs2 + "con.con_id" + cs2 + "con.version" + cs2
            + "nvl(date_modified, date_created)" + cs2 + "con.asl_name" + cs2 + "nvl(con.evs_source, ' ')" + cs2
            + "nvl(con.preferred_name, ' ') as title, upper(con.long_name) as lname "
            + "FROM sbrext.concepts_view_ext con "
            + "WHERE con.asl_name NOT LIKE 'RETIRED%' and con.con_idseq in (";

        String temp = "";
        for (int i = 0; i < ids_.length && i < 1000; ++i)
        {
            temp += ",'" + ids_[i] + "'";
        }

        select += temp.substring(1) + ") order by lname asc";

        return getBasicData0(select);
    }

    /**
     * Get the list of unused property records.
     *
     * @return The list of name, public id and version
     */
    public String[] reportUnusedProperties()
    {
        String cs1 = AuditReport._ColSeparator;
        String cs2 = " || '" + cs1 + "' || ";
        String select =
            "SELECT 'Name" + cs1 + "Public ID" + cs1 + "Date Created" + cs1 + "Workflow Status" + cs1 + "Context" + cs1
            + "Display" + cs1 + "Concept" + cs1 + "Concept Code" + cs1 + "Origin" + cs1 + "Public ID" + cs1 + "Date Created" + cs1 + "Workflow Status' "
            + "as title, ' ' as lname, 0 as pid, ' ' as pidseq, 0 as dorder from dual UNION ALL "
            + "SELECT prop.long_name" + cs2 + "prop.prop_id || 'v' || prop.version" + cs2 + "prop.date_created" + cs2 + "prop.asl_name" + cs2 + "c.name " + cs2
            + "ccv.display_order" + cs2 + "con.long_name" + cs2 + "con.preferred_name" + cs2 + " con.origin" + cs2 + "con.con_id || 'v' || con.version" + cs2 + "con.date_created" + cs2 + "con.asl_name "
            + "as title, upper(prop.long_name) as lname, prop.prop_id as pid, prop.prop_idseq as pidseq, ccv.display_order as dorder "
            + "FROM sbrext.properties_view_ext prop, sbr.contexts_view c, "
            + "sbrext.component_concepts_view_ext ccv, sbrext.concepts_view_ext con "
            + "WHERE prop.asl_name NOT LIKE 'RETIRED%' and prop.prop_idseq NOT IN "
            + "(SELECT decv.prop_idseq "
            + "FROM sbr.data_element_concepts_view decv "
            + "WHERE decv.prop_idseq = prop.prop_idseq) "
            + "AND c.conte_idseq = prop.conte_idseq "
            + "AND ccv.condr_idseq = prop.condr_idseq "
            + "AND con.con_idseq = ccv.con_idseq "
            + "order by lname asc, pid ASC, pidseq ASC, dorder DESC";

        return getBasicData0(select);
    }

    /**
     * Get the list of Administered Component which do not have a public id.
     *
     * @return the list of ac type, name, and idseq.
     */
    public String[] reportMissingPublicID()
    {
        String cs1 = AuditReport._ColSeparator;
        String cs2 = " || '" + cs1 + "' || ";
        String select =
            "SELECT 'AC Type" + cs1 + "Name" + cs1 + "ID" + cs1 + "Context' as title, ' ' as tname, ' ' as lname from dual UNION ALL "
            + "select ac.actl_name" + cs2 + "ac.long_name" + cs2 + "ac.ac_idseq" + cs2 + "c.name as title, upper(ac.actl_name) as tname, upper(ac.long_name) as lname "
            + "from sbr.admin_components_view ac, sbr.contexts_view c where ac.public_id is null "
            + "and ac.asl_name NOT LIKE 'RETIRED%' "
            + "and c.conte_idseq = ac.conte_idseq "
            + "order by tname asc";

        return getBasicData0(select);
    }

    /**
     * Get the list of unused data element concept records.
     *
     * @return The list of name, public id and version
     */
    public String[] reportUnusedDEC()
    {
        String cs1 = AuditReport._ColSeparator;
        String cs2 = " || '" + cs1 + "' || ";
        String select =
            "SELECT 'Name" + cs1 + "Public ID" + cs1 + "Version" + cs1 + "Workflow Status" + cs1 + "Context' as title, ' ' as lname from dual UNION ALL "
            + "SELECT dec.long_name" + cs2 + "dec.dec_id" + cs2 + "dec.version" + cs2 + "dec.asl_name" + cs2 + "c.name as title, upper(dec.long_name) as lname "
            + "FROM sbr.data_element_concepts_view dec, sbr.contexts_view c "
            + "WHERE dec.asl_name NOT LIKE 'RETIRED%' and dec.dec_idseq NOT IN "
            + "(SELECT de.dec_idseq FROM sbr.data_elements_view de WHERE de.dec_idseq = dec.dec_idseq) "
            + "and c.conte_idseq = dec.conte_idseq "
            + "order by lname asc";

        return getBasicData0(select);
    }

    /**
     * Get the list of unused object class records.
     *
     * @return The list of name, public id and version
     */
    public String[] reportUnusedObjectClasses()
    {
        String cs1 = AuditReport._ColSeparator;
        String cs2 = " || '" + cs1 + "' || ";
        String select =
            "SELECT 'Name" + cs1 + "Public ID" + cs1 + "Date Created" + cs1 + "Workflow Status" + cs1 + "Context" + cs1
            + "Display" + cs1 + "Concept" + cs1 + "Concept Code" + cs1 + "Origin" + cs1 + "Public ID" + cs1 + "Date Created" + cs1 + "Workflow Status' "
            + "as title, ' ' as lname, 0 as ocid, ' ' as ocidseq, 0 as dorder from dual UNION ALL "
            + "SELECT oc.long_name" + cs2 + "oc.oc_id || 'v' || oc.version" + cs2 + "oc.date_created" + cs2 + "oc.asl_name" + cs2 + "c.name" + cs2
            + "ccv.display_order" + cs2 + "con.long_name" + cs2 + "con.preferred_name" + cs2 + " con.origin" + cs2 + "con.con_id || 'v' || con.version" + cs2 + "con.date_created" + cs2 + "con.asl_name "
            + "as title, upper(oc.long_name) as lname, oc.oc_id as ocid, oc.oc_idseq as ocidseq, ccv.display_order as dorder "
            + "FROM sbrext.object_classes_view_ext oc, sbr.contexts_view c, "
            + "sbrext.component_concepts_view_ext ccv, sbrext.concepts_view_ext con "
            + "WHERE oc.asl_name NOT LIKE 'RETIRED%' and oc.oc_idseq NOT IN "
            +    "(SELECT decv.oc_idseq "
            +    "FROM sbr.data_element_concepts_view decv "
            +    "WHERE decv.OC_IDSEQ = oc.oc_idseq) "
            + "AND c.conte_idseq = oc.conte_idseq "
            + "AND ccv.condr_idseq = oc.condr_idseq "
            + "AND con.con_idseq = ccv.con_idseq "
            + "order by lname asc, ocid ASC, ocidseq ASC, dorder DESC";

        return getBasicData0(select);
    }

    /**
     * Get the list of Data Elements which do not have question text and are referenced by a Form.
     *
     * @return the list of name, public id and version.
     */
    public String[] reportMissingQuestionText()
    {
        String cs1 = AuditReport._ColSeparator;
        String cs2 = " || '" + cs1 + "' || ";
        String select =
            "SELECT 'Name" + cs1 + "Public ID" + cs1 + "Version" + cs1 + "Workflow Status" + cs1 + "Context' as title, ' ' as lname from dual UNION ALL "
            + "select de.long_name" + cs2 + "de.cde_id" + cs2 + "de.version" + cs2 + "de.asl_name" + cs2 + "c.name as title, upper(de.long_name) as lname "
            + "from sbr.data_elements_view de, sbr.contexts_view c "
            + "where de.asl_name NOT LIKE 'RETIRED%' "
            + "and de.de_idseq in (select qc.de_idseq from sbrext.quest_contents_view_ext qc where qc.de_idseq = de.de_idseq) "
            + "and de.de_idseq not in (select rd.ac_idseq from sbr.reference_documents_view rd where rd.ac_idseq = de.de_idseq and dctl_name in ('Alternate Question Text','Preferred Question Text')) "
            + "and c.conte_idseq = de.conte_idseq "
            + "order by lname asc";

        return getBasicData0(select);
    }

    /**
     * Retrieve the Context names and id's from the database. Follows the
     * pattern documented in getUsers().
     *
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getContextList getContextList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getContextVals getContextVals()
     */
    public int getContexts()
    {
        // Get the context names and id's.
        String select = "select conte_idseq, name from sbr.contexts_view "
            + "order by upper(name) ASC";
        Results1 rec = getBasicData1(select, true);
        if (rec._rc == 0)
        {
            _contextList = new String[rec._data.length];
            _contextVals = new String[rec._data.length];
            for (int i = 0; i < _contextList.length; ++i)
            {
                _contextList[i] = rec._data[i]._label;
                _contextVals[i] = rec._data[i]._val;
            }
            return 0;
        }
        return rec._rc;
    }

    /**
     * Retrieve the EVS properties in the tool options table
     *
     * @return the array of properties.
     */
    public DBProperty[] selectEVSVocabs()
    {
        String select = "select opt.value, opt.property from sbrext.tool_options_view_ext opt where opt.tool_name = 'CURATION' and ("
            + "opt.property like 'EVS.VOCAB.%.PROPERTY.NAMESEARCH' or "
            + "opt.property like 'EVS.VOCAB.%.EVSNAME' or "
            + "opt.property like 'EVS.VOCAB.%.DISPLAY' or "
            + "opt.property like 'EVS.VOCAB.%.PROPERTY.DEFINITION' or "
            + "opt.property like 'EVS.VOCAB.%.ACCESSREQUIRED' "
            + ") order by opt.property";

        Results1 rs = getBasicData1(select, false);

        if (rs._rc == 0 && rs._data.length > 0)
        {
            DBProperty[] props = new DBProperty[rs._data.length];
            for (int i = 0; i < rs._data.length; ++i)
            {
                props[i] = new DBProperty(rs._data[i]._label, rs._data[i]._val);;
            }
            return props;
        }

        return null;
    }

    /**
     * Select all the caDSR Concepts
     *
     * @return the Concepts
     */
    public Vector<ConceptItem> selectConcepts()
    {
        // Get the context names and id's.
        String select = "SELECT con_idseq, con_id, version, evs_source, preferred_name, long_name, definition_source, preferred_definition "
            + "FROM sbrext.concepts_view_ext WHERE asl_name NOT LIKE 'RETIRED%' "
            + "ORDER BY upper(long_name) ASC";

        Statement stmt = null;
        Vector<ConceptItem> list = new Vector<ConceptItem>();
        try
        {
            // Prepare the statement.
            stmt = _conn.createStatement();
            ResultSet rs = stmt.executeQuery(select);

            // Get the list.
            while (rs.next())
            {
                ConceptItem rec = new ConceptItem();
                rec._idseq = rs.getString(1);
                rec._publicID = rs.getString(2);
                rec._version = rs.getString(3);
                rec._evsSource = rs.getString(4);
                rec._preferredName = rs.getString(5);
                rec._longName = rs.getString(6);
                rec._definitionSource = rs.getString(7);
                rec._preferredDefinition = rs.getString(8);
                list.add(rec);
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException ex)
        {
            // Bad...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
        return list;
    }

    /**
     * Retrieve the valid context list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     *
     * @return An array of strings from the sbr.contexts_view.name column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getContexts getContexts()
     */
    public String[] getContextList()
    {
        String temp[] = _contextList;
        _contextList = null;
        return temp;
    }

    /**
     * Retrieve the valid context id list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     *
     * @return An array of strings from the sbr.contexts_view.conte_idseq
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getContexts getContexts()
     */
    public String[] getContextVals()
    {
        String temp[] = _contextVals;
        _contextVals = null;
        return temp;
    }

    /**
     * Get the complete Workflow Status value list from the database. Follows
     * the pattern documented in getUsers().
     *
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflowList getWorkflowList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflowVals getWorkflowVals()
     */
    public int getWorkflow()
    {
        // For compatibility and consistency we treat this view as all others as
        // if it has id and name
        // columns. For some reason this view is designed to expose the real id
        // to the end user.
        String select = "select asl_name, 'C' "
            + "from sbr.ac_status_lov_view order by upper(asl_name) ASC";

        Results1 rec = getBasicData1(select, false);
        if (rec._rc == 0)
        {
            // Add the special values "(All)", "(Any Change)" and "(Ignore)"
            _workflowList = new String[rec._data.length + 3];
            _workflowVals = new String[rec._data.length + 3];
            int ndx = 0;
            _workflowList[ndx] = Constants._STRALL;
            _workflowVals[ndx++] = Constants._STRALL;
            _workflowList[ndx] = Constants._STRANY;
            _workflowVals[ndx++] = Constants._STRANY;
            _workflowList[ndx] = Constants._STRIGNORE;
            _workflowVals[ndx++] = Constants._STRIGNORE;
            for (int cnt = 0; cnt < rec._data.length; ++cnt)
            {
                _workflowList[ndx] = rec._data[cnt]._val;
                _workflowVals[ndx++] = rec._data[cnt]._val;
            }

            // Add the special values "(All)", "(Any Change)" and "(Ignore)"
            _cworkflowList = new String[rec._data.length + 1];
            _cworkflowVals = new String[rec._data.length + 1];
            ndx = 0;
            _cworkflowList[ndx] = Constants._STRALL;
            _cworkflowVals[ndx++] = Constants._STRALL;
            for (int cnt = 0; cnt < rec._data.length; ++cnt)
            {
                _cworkflowList[ndx] = rec._data[cnt]._val;
                _cworkflowVals[ndx++] = rec._data[cnt]._val;
            }
        }
        return rec._rc;
    }

    /**
     * Retrieve the valid workflow list. The method getWorkflow() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     *
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getWorkflowList()
    {
        String temp[] = _workflowList;
        _workflowList = null;
        return temp;
    }

    /**
     * Retrieve the valid workflow values. The method getWorkflow() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getWorkflowVals()
    {
        String temp[] = _workflowVals;
        _workflowVals = null;
        return temp;
    }

    /**
     * Retrieve the valid workflow list. The method getWorkflow() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     *
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getCWorkflowList()
    {
        String temp[] = _cworkflowList;
        _cworkflowList = null;
        return temp;
    }

    /**
     * Retrieve the valid workflow values. The method getWorkflow() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getCWorkflowVals()
    {
        String temp[] = _cworkflowVals;
        _cworkflowVals = null;
        return temp;
    }

    /**
     * Retrieve the valid registration statuses. Follows the pattern documented
     * in getUsers().
     *
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegStatusList getRegStatusList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegStatusVals getRegStatusVals()
     */
    public int getRegistrations()
    {
        // For compatibility and consistency we treat this view as all others as
        // if it has id and name
        // columns. For some reason this view is designed to expose the real id
        // to the end user.
        String select = "select registration_status, 'C' "
            + "from sbr.reg_status_lov_view "
            + "order by upper(registration_status) ASC";

        Results1 rec = getBasicData1(select, false);
        if (rec._rc == 0)
        {
            // Add the special values "(All)", "(Any Change)" and "(Ignore)"
            _regStatusList = new String[rec._data.length + 3];
            _regStatusVals = new String[rec._data.length + 3];
            int ndx = 0;
            _regStatusList[ndx] = Constants._STRALL;
            _regStatusVals[ndx++] = Constants._STRALL;
            _regStatusList[ndx] = Constants._STRANY;
            _regStatusVals[ndx++] = Constants._STRANY;
            _regStatusList[ndx] = Constants._STRIGNORE;
            _regStatusVals[ndx++] = Constants._STRIGNORE;
            for (int cnt = 0; cnt < rec._data.length; ++cnt)
            {
                _regStatusList[ndx] = rec._data[cnt]._val;
                _regStatusVals[ndx++] = rec._data[cnt]._val;
            }

            // Add the special value "(All)" and "(none)" for the Criteria entries
            _regCStatusList = new String[rec._data.length + 2];
            _regCStatusVals = new String[rec._data.length + 2];
            ndx = 0;
            _regCStatusList[ndx] = Constants._STRALL;
            _regCStatusVals[ndx++] = Constants._STRALL;
            _regCStatusList[ndx] = Constants._STRNONE;
            _regCStatusVals[ndx++] = Constants._STRNONE;
            for (int cnt = 0; cnt < rec._data.length; ++cnt)
            {
                _regCStatusList[ndx] = rec._data[cnt]._val;
                _regCStatusVals[ndx++] = rec._data[cnt]._val;
            }
        }
        return rec._rc;
    }

    /**
     * Retrieve the registration status list. The method getRegistrations() must
     * be called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegStatusList()
    {
        String temp[] = _regStatusList;
        _regStatusList = null;
        return temp;
    }

    /**
     * Retrieve the registration status values list. The method
     * getRegistrations() must be called first. Once this method is used the
     * internal copy is deleted to reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegStatusVals()
    {
        String temp[] = _regStatusVals;
        _regStatusVals = null;
        return temp;
    }

    /**
     * Retrieve the registration status list. The method getRegistrations() must
     * be called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegCStatusList()
    {
        String temp[] = _regCStatusList;
        _regCStatusList = null;
        return temp;
    }

    /**
     * Retrieve the registration status values list. The method
     * getRegistrations() must be called first. Once this method is used the
     * internal copy is deleted to reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegCStatusVals()
    {
        String temp[] = _regCStatusVals;
        _regCStatusVals = null;
        return temp;
    }

    /**
     * Retrieve the Protocols from the database. Follows the
     * pattern documented in getUsers().
     *
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtoList getProtoList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtoVals getProtoVals()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtoContext getProtoContext()
     */
    public int getProtos()
    {
        String select = "select pr.conte_idseq, pr.proto_idseq, pr.long_name || ' (v' || pr.version || ' / ' || CV.name || ')' AS lname "
            + "from sbrext.protocols_view_ext pr, sbr.contexts_view cv "
            + "where cv.conte_idseq = pr.conte_idseq order by UPPER(lname) asc";

        Results2 rec = getBasicData2(select);
        if (rec._rc == 0)
        {
            _protoList = new String[rec._data.length];
            _protoVals = new String[rec._data.length];
            _protoContext = new String[rec._data.length];
            for (int i = 0; i < _protoList.length; ++i)
            {
                _protoList[i] = rec._data[i]._label;
                _protoVals[i] = rec._data[i]._id2;
                _protoContext[i] = rec._data[i]._id1;
            }
        }
        return rec._rc;
    }

    /**
     * Retrieve the protocol list. The method getProtos() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbrext.protocols_view_ext.long_name, version and context
     *         columns.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtos getProtos()
     */
    public String[] getProtoList()
    {
        String temp[] = _protoList;
        _protoList = null;
        return temp;
    }

    /**
     * Retrieve the protocol list. The method getProtos() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbrext.protocols_view_ext.proto_idseq column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtos getProtos()
     */
    public String[] getProtoVals()
    {
        String temp[] = _protoVals;
        _protoVals = null;
        return temp;
    }

    /**
     * Retrieve the protocol list. The method getProtos() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbrext.protocols_view_ext.conte_idseq column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtos getProtos()
     */
    public String[] getProtoContext()
    {
        String temp[] = _protoContext;
        _protoContext = null;
        return temp;
    }

    /**
     * Retrieve the Classification Schemes from the database. Follows the
     * pattern documented in getUsers().
     *
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeList getSchemeList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeVals getSchemeVals()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeContext getSchemeContext()
     */
    public int getSchemes()
    {
        String select = "select csv.conte_idseq, csv.cs_idseq, csv.long_name || ' (v' || csv.version || ' / ' || cv.name || ')' as lname "
            + "from sbr.classification_schemes_view csv, sbr.contexts_view cv "
            + "where cv.conte_idseq = csv.conte_idseq order by upper(lname) ASC";

        Results2 rec = getBasicData2(select);
        if (rec._rc == 0)
        {
            _schemeList = new String[rec._data.length];
            _schemeVals = new String[rec._data.length];
            _schemeContext = new String[rec._data.length];
            for (int i = 0; i < _schemeList.length; ++i)
            {
                _schemeList[i] = rec._data[i]._label;
                _schemeVals[i] = rec._data[i]._id2;
                _schemeContext[i] = rec._data[i]._id1;
            }
        }
        return rec._rc;
    }

    /**
     * Retrieve the classification scheme list. The method getSchemes() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbr.classification_schemes_view.long_name, version and context
     *         columns.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemes getSchemes()
     */
    public String[] getSchemeList()
    {
        String temp[] = _schemeList;
        _schemeList = null;
        return temp;
    }

    /**
     * Retrieve the classification scheme id's. The method getSchemes() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbr.classification_schemes_view.cs_idseq column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemes getSchemes()
     */
    public String[] getSchemeVals()
    {
        String temp[] = _schemeVals;
        _schemeVals = null;
        return temp;
    }

    /**
     * Retrieve the context id's associated with the classification scheme id's
     * retrieved above. The method getSchemes() must be called first. Once this
     * method is used the internal copy is deleted to reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbr.classification_schemes_view.conte_idseq column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemes getSchemes()
     */
    public String[] getSchemeContext()
    {
        String temp[] = _schemeContext;
        _schemeContext = null;
        return temp;
    }

    private class schemeTree
    {
        /**
         * Constructor.
         *
         * @param name_ The composite name for sorting.
         * @param ndx_ The index of the scheme item in the original list.
         */
        public schemeTree(String name_, int ndx_)
        {
            _fullName = name_;
            _ndx = ndx_;
        }

        /**
         * The composite name used for sorting.
         */
        public String _fullName;

        /**
         * The index in the original list.
         */
        public int    _ndx;
    }

    /**
     * Build the concatenated strings for the Class Scheme Items display.
     *
     * @param rec_
     *        The data returned from Oracle.
     * @return An array of the full concatenated names for sorting later.
     */
    private schemeTree[] buildSchemeItemList(Results3 rec_)
    {
        // Get the maximum number of levels and the maximum length of a single
        // name.
        int maxLvl = 0;
        int maxLen = 0;
        for (int ndx = 1; ndx < rec_._data.length; ++ndx)
        {
            if (maxLvl < rec_._data[ndx]._id3)
                maxLvl = rec_._data[ndx]._id3;
            if (rec_._data[ndx]._label1 != null
                && maxLen < rec_._data[ndx]._label1.length())
                maxLen = rec_._data[ndx]._label1.length();
            if (rec_._data[ndx]._label2 != null
                && maxLen < rec_._data[ndx]._label2.length())
                maxLen = rec_._data[ndx]._label2.length();
        }
        ++maxLvl;

        // Build and array of prefixes for the levels.
        String prefix[] = new String[maxLvl];
        prefix[0] = "";
        if (maxLvl > 1)
        {
            prefix[1] = "";
            for (int ndx = 2; ndx < prefix.length; ++ndx)
            {
                prefix[ndx] = prefix[ndx - 1] + "    ";
            }
        }

        // In addition to creating the display labels we must also
        // create an array used to sort everything alphabetically.
        // The easiest is to create a fully concatenated label of a
        // individuals hierarchy.
        _schemeItemList = new String[rec_._data.length];
        maxLvl *= maxLen;
        StringBuffer fullBuff = new StringBuffer(maxLvl);
        fullBuff.setLength(maxLvl);
        schemeTree tree[] = new schemeTree[_schemeItemList.length];

        // Loop through the name list.
        _schemeItemList[0] = rec_._data[0]._label1;
        tree[0] = new schemeTree("", 0);
        for (int ndx = 1; ndx < _schemeItemList.length; ++ndx)
        {
            // Create the concatenated sort string.
            int buffOff = (rec_._data[ndx]._id3 < 2) ? 0 : (rec_._data[ndx]._id3 * maxLen);
            fullBuff.replace(buffOff, maxLvl, rec_._data[ndx]._label1);
            fullBuff.setLength(maxLvl);

            // Create the display label.
            if (rec_._data[ndx]._id3 == 1)
            {
                if (rec_._data[ndx]._label2 == null)
                {
                    _schemeItemList[ndx] = rec_._data[ndx]._label1;
                }
                else
                {
                    _schemeItemList[ndx] = rec_._data[ndx]._label1 + " ("
                        + rec_._data[ndx]._label2 + ")";
                    fullBuff.replace(buffOff + maxLen, maxLvl,
                        rec_._data[ndx]._label2);
                    fullBuff.setLength(maxLvl);
                }
            }
            else
            {
                _schemeItemList[ndx] = prefix[rec_._data[ndx]._id3]
                    + rec_._data[ndx]._label1;
            }
            tree[ndx] = new schemeTree(fullBuff.toString(), ndx);
        }
        return tree;
    }

    /**
     * Retrieve the Classification Scheme Items from the database. Follows the
     * pattern documented in getUsers().
     *
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItemList getSchemeItemList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItemVals getSchemeItemVals()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItemSchemes
     *      getSchemeItemScheme()
     */
    public int getSchemeItems()
    {
        String select = "select cv.cs_idseq, cv.csi_idseq, level as lvl, "
            + "(select csi.csi_name from sbr.class_scheme_items_view csi where csi.csi_idseq = cv.csi_idseq), "
            + "(select cs.long_name || ' / v' || cs.version as xname from sbr.classification_schemes_view cs where cs.cs_idseq = cv.cs_idseq) "
            + "from sbr.cs_csi_view cv "
            + "start with cv.p_cs_csi_idseq is null "
            + "connect by prior cv.cs_csi_idseq = cv.p_cs_csi_idseq";

        Results3 rec = getBasicData3(select, true);
        if (rec._rc == 0)
        {
            schemeTree tree[] = buildSchemeItemList(rec);

            _schemeItemVals = new String[rec._data.length];
            _schemeItemSchemes = new String[rec._data.length];
            for (int i = 0; i < rec._data.length; ++i)
            {
                _schemeItemVals[i] = rec._data[i]._id2;
                _schemeItemSchemes[i] = rec._data[i]._id1;
            }

            sortSchemeItems(tree);
        }
        return rec._rc;
    }

    /**
     * Sort the scheme items lists and make everything right on the display.
     *
     * @param tree_
     *        The concatenated name tree list.
     */
    private void sortSchemeItems(schemeTree tree_[])
    {
        // Too few items don't bother.
        if (tree_.length < 2)
            return;

        // The first element is the "All" indicator, so don't include it.
        schemeTree sort[] = new schemeTree[tree_.length];
        sort[0] = tree_[0];
        sort[1] = tree_[1];
        int top = 2;

        // Perform a binary search-insert.
        for (int ndx = 2; ndx < tree_.length; ++ndx)
        {
            int min = 1;
            int max = top;
            int check = 0;
            while (true)
            {
                check = (max + min) / 2;
                int test = tree_[ndx]._fullName
                    .compareToIgnoreCase(sort[check]._fullName);
                if (test == 0)
                    break;
                else if (test > 0)
                {
                    if (min == check)
                    {
                        ++check;
                        break;
                    }
                    min = check;
                }
                else
                {
                    if (max == check)
                        break;
                    max = check;
                }
            }
            // Add the record to the proper position in the sorted array.
            if (check < top)
                System.arraycopy(sort, check, sort, check + 1, top - check);
            ++top;
            sort[check] = tree_[ndx];
        }

        // Now arrange all the arrays based on the sorted index.
        String tempList[] = new String[_schemeItemList.length];
        String tempVals[] = new String[_schemeItemList.length];
        String tempSchemes[] = new String[_schemeItemList.length];
        for (int ndx = 0; ndx < sort.length; ++ndx)
        {
            int pos = sort[ndx]._ndx;
            tempList[ndx] = _schemeItemList[pos];
            tempVals[ndx] = _schemeItemVals[pos];
            tempSchemes[ndx] = _schemeItemSchemes[pos];
        }
        _schemeItemList = tempList;
        _schemeItemVals = tempVals;
        _schemeItemSchemes = tempSchemes;
    }

    /**
     * Retrieve the classification scheme item list. The method getSchemeItems()
     * must be called first. Once this method is used the internal copy is
     * deleted to reclaim the memory space.
     *
     * @return An array of strings from the sbr.class_scheme_items_view.csi_name
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItems getSchemeItems()
     */
    public String[] getSchemeItemList()
    {
        String temp[] = _schemeItemList;
        _schemeItemList = null;
        return temp;
    }

    /**
     * Retrieve the classification scheme item id's. The method getSchemeItems()
     * must be called first. Once this method is used the internal copy is
     * deleted to reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbr.class_scheme_items_view.csi_idseq column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItems getSchemeItems()
     */
    public String[] getSchemeItemVals()
    {
        String temp[] = _schemeItemVals;
        _schemeItemVals = null;
        return temp;
    }

    /**
     * Retrieve the class scheme id's associated with the classification scheme
     * item id's retrieved above. The method getSchemeItems() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     *
     * @return An array of strings from the sbr.class_scheme_items_view.cs_idseq
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItems getSchemeItems()
     */
    public String[] getSchemeItemSchemes()
    {
        String temp[] = _schemeItemSchemes;
        _schemeItemSchemes = null;
        return temp;
    }

    private class ResultsData2
    {
        /**
         * id1
         */
        public String _id1;

        /**
         * id2
         */
        public String _id2;

        /**
         * label
         */
        public String _label;
    }

    /**
     * Class used to return method results.
     */
    private class Results2
    {
        /**
         * The database return code.
         */
        public int    _rc;

        /**
         * data
         */
        public ResultsData2[] _data;
    }

    /**
     * Perform the database access for a simple query which results in a 3
     * column value per returned row.
     *
     * @param select_
     *        The SQL select to run.
     * @return 0 if successful, otherwise the database error code.
     */
    private Results2 getBasicData2(String select_)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector<ResultsData2> results = new Vector<ResultsData2>();
        Results2 data = new Results2();

        try
        {
            // Prepare the statement.
            pstmt = _conn.prepareStatement(select_);

            // Get the list.
            rs = pstmt.executeQuery();
            ResultsData2 rec;
            while (rs.next())
            {
                // Remember about the 1 (one) based indexing.
                rec = new ResultsData2();
                rec._id1 = rs.getString(1);
                rec._id2 = rs.getString(2);
                rec._label = rs.getString(3);
                results.add(rec);
            }
            rs.close();
            pstmt.close();

            // Move the list from a Vector to an array and add "(All)" to
            // the beginning.
            int count = results.size() + 1;
            data._data = new ResultsData2[count];
            data._data[0] = new ResultsData2();
            data._data[0]._label = Constants._STRALL;
            data._data[0]._id1 = Constants._STRALL;
            data._data[0]._id2 = Constants._STRALL;
            int cnt = 0;
            for (int ndx = 1; ndx < count; ++ndx)
            {
                rec = (ResultsData2) results.get(cnt++);
                data._data[ndx] = new ResultsData2();
                data._data[ndx]._label = rec._label.replaceAll("[\\s]", " ");
                data._data[ndx]._id1 = rec._id1;
                data._data[ndx]._id2 = rec._id2;
            }

            data._rc = 0;
        }
        catch (SQLException ex)
        {
            // Bad...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            data._rc = _errorCode;
        }
        return data;
    }

    class ResultsData3
    {
        /**
         *
         */
        public String _id1;

        /**
         *
         */
        public String _id2;

        /**
         *
         */
        public int    _id3;

        /**
         *
         */
        public String _label1;

        /**
         *
         */
        public String _label2;
    }
    ;

    /**
     * Class used to return method results.
     */
    private class Results3
    {
        /**
         * The database return code.
         */
        public int    _rc;

        /**
         * The data
         */
        public ResultsData3[] _data;
    }

    /**
     * Perform the database access for a simple query which results in a 4
     * column value per returned row.
     *
     * @param select_
     *        The SQL select to run.
     * @param flag_ true if the list should be prefixed with "All".
     * @return 0 if successful, otherwise the database error code.
     */
    private Results3 getBasicData3(String select_, boolean flag_)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector<ResultsData3> results = new Vector<ResultsData3>();
        Results3 data = new Results3();

        try
        {
            // Prepare the statement.
            pstmt = _conn.prepareStatement(select_);

            // Get the list.
            rs = pstmt.executeQuery();
            ResultsData3 rec;
            while (rs.next())
            {
                // Remember about the 1 (one) based indexing.
                rec = new ResultsData3();
                rec._id1 = rs.getString(1);
                rec._id2 = rs.getString(2);
                rec._id3 = rs.getInt(3);
                rec._label1 = rs.getString(4);
                rec._label2 = rs.getString(5);
                results.add(rec);
            }
            rs.close();
            pstmt.close();

            // Move the list from a Vector to an array and add "(All)" to
            // the beginning.
            int offset = (flag_) ? 1 : 0;
            int count = results.size() + offset;
            data._data = new ResultsData3[count];
            if (flag_)
            {
                data._data[0] = new ResultsData3();
                data._data[0]._label1 = Constants._STRALL;
                data._data[0]._label2 = Constants._STRALL;
                data._data[0]._id1 = Constants._STRALL;
                data._data[0]._id2 = Constants._STRALL;
                data._data[0]._id3 = 0;
            }
            int cnt = 0;
            for (int ndx = offset; ndx < count; ++ndx)
            {
                rec = (ResultsData3) results.get(cnt++);
                data._data[ndx] = new ResultsData3();
                data._data[ndx]._label1 = rec._label1.replaceAll("[\\s]", " ");
                data._data[ndx]._label2 = rec._label2.replaceAll("[\\s]", " ");
                data._data[ndx]._id1 = rec._id1;
                data._data[ndx]._id2 = rec._id2;
                data._data[ndx]._id3 = rec._id3;
            }
            data._rc = 0;
        }
        catch (SQLException ex)
        {
            // Bad...
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            data._rc = _errorCode;
        }
        return data;
    }

    /**
     * Retrieve the list of record types.  As this is coded in a constant
     * array, no database access is required.
     *
     * @return 0 if successful.
     */
    public int getACTypes()
    {
        String[] list = new String[_DBMAP3.length + 1];
        String[] vals = new String[_DBMAP3.length + 1];
        list[0] = Constants._STRALL;
        vals[0] = Constants._STRALL;
        list[1] = _DBMAP3[0]._val;
        vals[1] = _DBMAP3[0]._key;

        // Put the descriptive text in alphabetic order for display.
        // Of course we have to keep the key-value pairs intact.
        for (int ndx = 1; ndx < _DBMAP3.length; ++ndx)
        {
            int min = 1;
            int max = ndx + 1;
            int pos = 1;
            while (true)
            {
                pos = (max + min) / 2;
                int compare = _DBMAP3[ndx]._val.compareTo(list[pos]);
                if (compare == 0)
                {
                    // Can't happen.
                }
                else if (compare > 0)
                {
                    if (min == pos)
                    {
                        ++pos;
                        break;
                    }
                    min = pos;
                }
                else
                {
                    if (max == pos)
                    {
                        break;
                    }
                    max = pos;
                }
            }

            // Preserve existing entries - an insert.
            if (pos <= ndx)
            {
                System.arraycopy(list, pos, list, pos + 1, ndx - pos + 1);
                System.arraycopy(vals, pos, vals, pos + 1, ndx - pos + 1);
            }

            // Insert new item in list.
            list[pos] = _DBMAP3[ndx]._val;
            vals[pos] = _DBMAP3[ndx]._key;
        }

        // Keep the results.
        _actypesList = list;
        _actypesVals = vals;
        return 0;
    }

    /**
     * Return the descriptive names for the record types.
     *
     * @return The list of display values.
     */
    public String[] getACTypesList()
    {
        String temp[] = _actypesList;
        _actypesList = null;
        return temp;
    }

    /**
     * Return the internal values used to identify the record types.
     *
     * @return The list of internal record types.
     */
    public String[] getACTypesVals()
    {
        String temp[] = _actypesVals;
        _actypesVals = null;
        return temp;
    }

    /**
     * Retrieve the list of forms and templates from the database. Follows the
     * pattern documented in getUsers().
     *
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getFormsList getFormsList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getFormsVals getFormsVals()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getFormsContext getFormsContext()
     */
    public int getForms()
    {
        // Build a composite descriptive string for this form.
        String select =
            "select qcv.conte_idseq, qcv.qc_idseq, qcv.long_name || "
            + "' (v' || qcv.version || ' / ' || qcv.qtl_name || ' / ' || nvl(proto.long_name, '(' || cv.name || ')') || ')' as lname "
            + "from sbrext.quest_contents_view_ext qcv, sbr.contexts_view cv, "
            + "sbrext.protocol_qc_ext pq, sbrext.protocols_view_ext proto "
            + "where qcv.qtl_name in ('TEMPLATE','CRF') "
            + "and cv.conte_idseq = qcv.conte_idseq "
            + "and qcv.qc_idseq = pq.qc_idseq(+) "
            + "and pq.proto_idseq = proto.proto_idseq(+) "
            + "order by upper(lname)";

        Results2 rec = getBasicData2(select);
        if (rec._rc == 0)
        {
            _formsList = new String[rec._data.length];
            _formsVals = new String[rec._data.length];
            _formsContext = new String[rec._data.length];
            for (int ndx = 0; ndx < _formsList.length; ++ndx)
            {
                // Can you believe that some people put quotes in the name? We
                // have to escape them or it causes
                // problems downstream.
                _formsList[ndx] = rec._data[ndx]._label;
                _formsList[ndx] = _formsList[ndx].replaceAll("[\"]", "\\\\\"");
                _formsList[ndx] = _formsList[ndx].replaceAll("[\\r\\n]", " ");

                _formsVals[ndx] = rec._data[ndx]._id2;
                _formsContext[ndx] = rec._data[ndx]._id1;
            }
        }
        return rec._rc;
    }

    /**
     * Return the forms/templates composite names. The method getForms() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbrext.quest_contents_view_ext.long_name, ... columns.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getForms getForms()
     */
    public String[] getFormsList()
    {
        String temp[] = _formsList;
        _formsList = null;
        return temp;
    }

    /**
     * Return the forms/templates id values. The method getForms() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbrext.quest_contents_view_ext.qc_idseq columns.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getForms getForms()
     */
    public String[] getFormsVals()
    {
        String temp[] = _formsVals;
        _formsVals = null;
        return temp;
    }

    /**
     * Return the context id's associated with the forms/templates. The method
     * getForms() must be called first. Once this method is used the internal
     * copy is deleted to reclaim the memory space.
     *
     * @return An array of strings from the
     *         sbrext.quest_contents_view_ext.conte_idseq columns.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getForms getForms()
     */
    public String[] getFormsContext()
    {
        String temp[] = _formsContext;
        _formsContext = null;
        return temp;
    }

    /**
     * Return the last recorded database error message. If the current error
     * code is zero (0) an empty string is returned.
     *
     * @return The last database error message or an empty string.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getErrorCode getErrorCode()
     */
    public String getErrorMsg()
    {
        return (_errorCode != 0) ? _errorMsg : null;
    }

    /**
     * Return the last recorded database error message. If the current error
     * code is zero (0) an empty string is returned.
     *
     * @param flag_
     *        True if the new lines ('\n') should be expanded to text for use in
     *        script. False to return the message unaltered.
     * @return The last database error message or an empty string.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getErrorCode getErrorCode()
     */
    public String getErrorMsg(boolean flag_)
    {
        return (_errorCode != 0) ? ((flag_) ? _errorMsg.replaceAll("[\\n]",
            "\\\\n") : _errorMsg) : null;
    }

    /**
     * Return the last recorded database error code and then reset it to zero
     * (0).
     *
     * @return The database error code.
     */
    public int getErrorCode()
    {
        int rc = _errorCode;
        _errorCode = 0;
        return rc;
    }

    /**
     * Return any error message and reset the error code to zero for the next
     * possible error.
     *
     * @return The database error message.
     */
    public String getError()
    {
        String temp = getErrorMsg();
        if (temp != null)
            _errorCode = 0;
        return temp;
    }

    /**
     * Get the Alerts which are active for the target date provided.
     *
     * @param target_
     *        The target date, typically the date an Auto Run process is
     *        started.
     * @return null if an error, otherwise the list of valid alert definitions.
     */
    public AlertRec[] selectAlerts(Timestamp target_)
    {
        String select = "select al_idseq, name, created_by "
            + "from sbrext.sn_alert_view_ext " + "where al_status <> 'I' AND "
            + "(auto_freq_unit = 'D' OR "
            + "(auto_freq_unit = 'W' AND auto_freq_value = ?) OR "
            + "(auto_freq_unit = 'M' AND auto_freq_value = ?)) "
            + "order by upper(created_by) asc, upper(name) asc";

        // Get day and date from target to qualify the select.
        GregorianCalendar tdate = new GregorianCalendar();
        tdate.setTimeInMillis(target_.getTime());
        int dayWeek = tdate.get(Calendar.DAY_OF_WEEK);
        int dayMonth = tdate.get(Calendar.DAY_OF_MONTH);

        try
        {
            // Set SQL arguments
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setInt(1, dayWeek);
            pstmt.setInt(2, dayMonth);

            // Retrieve all applicable definition ids.
            ResultSet rs = pstmt.executeQuery();
            Vector<String> list = new Vector<String>();
            while (rs.next())
            {
                list.add(rs.getString(1));
            }
            rs.close();
            pstmt.close();

            // There may be nothing to do.
            if (list.size() == 0)
                return new AlertRec[0];

            // retrieve the full alert definition, we will need it.
            AlertRec recs[] = new AlertRec[list.size()];
            int keep = 0;
            int ndx;
            for (ndx = 0; ndx < recs.length; ++ndx)
            {
                // Be sure we can read the Alert Definition.
                recs[keep] = selectAlert((String) list.get(ndx));
                if (recs[keep] == null)
                    return null;

                // Check the date. We do this here and not in the SQL because
                // 99.99% of the time this will return true and complicating the
                // SQL isn't necessary.
                if (recs[keep].isActive(target_))
                    ++keep;

                // In the RARE situation that the alert is inactive at this
                // point,
                // we reset the object pointer to release the memory.
                else
                    recs[keep] = null;
            }

            // Return the results. It is possible that sometimes the last entry
            // in the
            // list will be null. Consequently the use of the list should be in
            // a loop
            // with the following condition: "cnt < recs.length && recs[cnt] !=
            // null"
            if (keep == ndx)
                return recs;

            // Only process the ones that are Active.
            AlertRec trecs[] = new AlertRec[keep];
            for (ndx = 0; ndx < keep; ++ndx)
                trecs[ndx] = recs[ndx];
            return trecs;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    /**
     * Convert a Vector of Strings to an array.
     *
     * @param list_
     *        The vector.
     * @return The string array.
     */
    private String[] paste(Vector<String> list_)
    {
        String temp[] = new String[list_.size()];
        for (int ndx = 0; ndx < temp.length; ++ndx)
            temp[ndx] = list_.get(ndx);
        return temp;
    }

    /**
     * Convert a Vector of Timestamps to an array.
     *
     * @param list_ The vector.
     * @return The Timestamp array.
     */
    private Timestamp[] paste(Vector<Timestamp> list_)
    {
        Timestamp temp[] = new Timestamp[list_.size()];
        for (int ndx = 0; ndx < temp.length; ++ndx)
            temp[ndx] = list_.get(ndx);
        return temp;
    }

    /**
     * Copy the result set to an ACData array.
     *
     * @param rs_
     *        The query result set.
     * @return The ACData array if successful, otherwise null.
     * @throws java.sql.SQLException
     *         When there is a problem with the result set.
     */
    private ACData[] copyResults(ResultSet rs_) throws SQLException
    {
        Vector<ACData> data = new Vector<ACData>();
        Vector<String> changes = new Vector<String>();
        Vector<String> oval = new Vector<String>();
        Vector<String> nval = new Vector<String>();
        Vector<String> tabl = new Vector<String>();
        Vector<String> chgby = new Vector<String>();
        Vector<Timestamp> dval = new Vector<Timestamp>();
        String clist[] = null;
        String olist[] = null;
        String nlist[] = null;
        String tlist[] = null;
        String blist[] = null;
        Timestamp dlist[] = null;
        ACData oldrec = null;
        int cols = rs_.getMetaData().getColumnCount();
        while (rs_.next())
        {
            ACData rec = new ACData();
            rec.set(
                rs_.getString(1).charAt(0),
                rs_.getInt(2),
                rs_.getString(3),
                rs_.getString(4),
                rs_.getString(5),
                rs_.getInt(6),
                rs_.getString(7),
                rs_.getString(8),
                rs_.getTimestamp(9),
                rs_.getTimestamp(10),
                rs_.getString(11),
                rs_.getString(12),
                rs_.getString(13),
                rs_.getString(14),
                rs_.getString(15));

            // We don't want to waste time or space with records that are
            // identical. We can't use a SELECT DISTINCT for this logic as the
            // ACData.equals doesn't look at all data elements but only specific ones.
            if (oldrec != null)
            {
                if (!oldrec.isEquivalent(rec))
                {
                    clist = paste(changes);
                    olist = paste(oval);
                    nlist = paste(nval);
                    dlist = paste(dval);
                    tlist = paste(tabl);
                    blist = paste(chgby);
                    oldrec.setChanges(clist, olist, nlist, dlist, tlist, blist);
                    data.add(oldrec);

                    changes = new Vector<String>();
                    oval = new Vector<String>();
                    nval = new Vector<String>();
                    dval = new Vector<Timestamp>();
                    tabl = new Vector<String>();
                    chgby = new Vector<String>();
                }
            }

            // Build the list of specific changes if we can get them. We must
            // save
            // always save the information if present.
            //
            // NOTE we only record the first 18 columns of the result set but
            // there may be
            // more to make the SQL work as desired.
            if (cols > 17)
            {
                String ctext = rs_.getString(16);

                // If the "change" column is blank don't waste the space.
                if (ctext != null && ctext.length() > 0)
                {
                    changes.add(ctext);
                    oval.add(rs_.getString(17));
                    nval.add(rs_.getString(18));
                    dval.add(rs_.getTimestamp(19));
                    tabl.add(rs_.getString(20));
                    chgby.add(rs_.getString(21));
                }
            }
            oldrec = rec;
        }
        if (oldrec != null)
        {
            clist = paste(changes);
            olist = paste(oval);
            nlist = paste(nval);
            dlist = paste(dval);
            tlist = paste(tabl);
            blist = paste(chgby);
            oldrec.setChanges(clist, olist, nlist, dlist, tlist, blist);
            data.add(oldrec);
        }

        ACData list[] = new ACData[data.size()];
        if (data.size() > 0)
        {
            for (int ndx = 0; ndx < list.length; ++ndx)
            {
                list[ndx] = (ACData) data.get(ndx);
            }
        }
        return list;
    }

    /**
     * Pull rows changed in the date range specified. There are 3 different
     * patterns to handle:
     * <p>
     * <ul>
     * <li>[from/to, from/to] {2, 4} - This represents the from/to date pair
     * which may occur 2 or 4 times in the SQL. This pattern is handled by this
     * method argument list.</li>
     * <li>[in, from/to, from/to] {2, 4} - This represents a single "in" clause
     * of creators or modifiers followed by the from/to pair which may occur 2
     * or 4 times in the SQL in this order.</li>
     * <li>[in, in, from/to, from/to] {2,4} - This represents an "in" clause
     * for the creators and an "in" clause for the modifiers followed by the
     * from/to pair which in total may appear 1 or 2 times.</li>
     * </ul>
     * </p>
     *
     * @param select_
     *        The SQL select for the specific data and table.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param pairs_
     *        The number of pairs of (start, end) that appear in the SQL.
     * @return 0 if successful, otherwise the database error code.
     */
    private ACData[] selectAC(String select_, Timestamp start_, Timestamp end_,
        int pairs_)
    {
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select_);
            for (int ndx = 0; ndx < pairs_; ++ndx)
            {
                pstmt.setTimestamp((ndx * 2) + 1, start_);
                pstmt.setTimestamp((ndx * 2) + 2, end_);
            }

            ResultSet rs = pstmt.executeQuery();
            ACData list[] = copyResults(rs);
            rs.close();
            pstmt.close();
            return list;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    /**
     * Generate a string of comma separated SQL arguments for us in an "in"
     * clause.
     *
     * @param cnt_
     *        The number of place holders needed.
     * @return String The comma separated string without parentheses.
     */
    private String expandMarkers(int cnt_)
    {
        String markers = "?";
        for (int ndx = 1; ndx < cnt_; ++ndx)
        {
            markers = markers + ",?";
        }
        return markers;
    }

    /**
     * Pull rows changed in the date range specified. There are 3 different
     * patterns to handle:
     * <p>
     * <ul>
     * <li>[from/to, from/to] {2, 4} - This represents the from/to date pair
     * which may occur 2 or 4 times in the SQL.</li>
     * <li>[in, from/to, from/to] {2, 4} - This represents a single "in" clause
     * of creators or modifiers followed by the from/to pair which may occur 2
     * or 4 times in the SQL in this order. This pattern is handled by this
     * method argument list.</li>
     * <li>[in, in, from/to, from/to] {2,4} - This represents an "in" clause
     * for the creators and an "in" clause for the modifiers followed by the
     * from/to pair which in total may appear 1 or 2 times.</li>
     * </ul>
     * </p>
     *
     * @param select_
     *        The SQL select for the specific data and table.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param pairs_
     *        The number of pairs of (start, end) that appear in the SQL.
     * @param vals_
     *        The additional values used by an "in" clause.
     * @return 0 if successful, otherwise the database error code.
     */
    private ACData[] selectAC(String select_, Timestamp start_, Timestamp end_,
        int pairs_, String vals_[])
    {
        // Expand the "in" clause.
        int loop = pairs_ / 2;
        String markers = expandMarkers(vals_.length);

        String parts[] = select_.split("\\?");
        int pos = 0;
        String select = parts[pos++];
        for (int cnt = 0; cnt < loop; ++cnt)
        {
            select = select + markers + parts[pos++];
            for (int ndx = 0; ndx < 2; ++ndx)
            {
                select = select + "?" + parts[pos++] + "?" + parts[pos++];
            }
        }

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            int arg = 1;
            for (int cnt = 0; cnt < loop; ++cnt)
            {
                for (int ndx = 0; ndx < vals_.length; ++ndx)
                {
                    pstmt.setString(arg++, vals_[ndx]);
                }
                for (int ndx = 0; ndx < 2; ++ndx)
                {
                    pstmt.setTimestamp(arg++, start_);
                    pstmt.setTimestamp(arg++, end_);
                }
            }
            ResultSet rs = pstmt.executeQuery();
            ACData list[] = copyResults(rs);
            rs.close();
            pstmt.close();
            return list;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    /**
     * Pull rows changed in the date range specified. There are 3 different
     * patterns to handle:
     * <p>
     * <ul>
     * <li>[from/to, from/to] {2, 4} - This represents the from/to date pair
     * which may occur 2 or 4 times in the SQL.</li>
     * <li>[in, from/to, from/to] {2, 4} - This represents a single "in" clause
     * of creators or modifiers followed by the from/to pair which may occur 2
     * or 4 times in the SQL in this order.</li>
     * <li>[in, in, from/to, from/to] {2,4} - This represents an "in" clause
     * for the creators and an "in" clause for the modifiers followed by the
     * from/to pair which in total may appear 1 or 2 times. This pattern is
     * handled by this method argument list.</li>
     * </ul>
     * </p>
     *
     * @param select_
     *        The SQL select for the specific data and table.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param pairs_
     *        The number of pairs of (start, end) that appear in the SQL.
     * @param vals1_
     *        The additional values used by an "in" clause.
     * @param vals2_
     *        The additional values used by a second "in" clause.
     * @return 0 if successful, otherwise the database error code.
     */
    private ACData[] selectAC(String select_, Timestamp start_, Timestamp end_,
        int pairs_, String vals1_[], String vals2_[])
    {
        // Expand the "in" clauses.
        String parts[] = select_.split("\\?");
        int loop = pairs_ / 2;

        String markers1 = expandMarkers(vals1_.length);
        String markers2 = expandMarkers(vals2_.length);
        int pos = 0;
        String select = parts[pos++];

        for (int cnt = 0; cnt < loop; ++cnt)
        {
            select = select + markers1 + parts[pos++] + markers2 + parts[pos++];
            for (int ndx = 0; ndx < 2; ++ndx)
            {
                select = select + "?" + parts[pos++] + "?" + parts[pos++];
            }
        }

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            int arg = 1;
            for (int cnt = 0; cnt < loop; ++cnt)
            {
                for (int ndx = 0; ndx < vals1_.length; ++ndx)
                {
                    pstmt.setString(arg++, vals1_[ndx]);
                }
                for (int ndx = 0; ndx < vals2_.length; ++ndx)
                {
                    pstmt.setString(arg++, vals2_[ndx]);
                }
                for (int ndx = 0; ndx < 2; ++ndx)
                {
                    pstmt.setTimestamp(arg++, start_);
                    pstmt.setTimestamp(arg++, end_);
                }
            }
            ResultSet rs = pstmt.executeQuery();
            ACData list[] = copyResults(rs);
            rs.close();
            pstmt.close();
            return list;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    private ACData[] selectAC(String select_)
    {
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select_);
            ResultSet rs = pstmt.executeQuery();
            ACData list[] = copyResults(rs);
            rs.close();
            pstmt.close();
            return list;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    private int selectChangedTableType(String idseq_)
    {
        String select = "select changed_table from sbrext.ac_change_history_ext "
            + "where changed_table_idseq = ? and rownum < 2";
        int itype = -1;

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, idseq_);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                String stype = rs.getString(1);

                if (stype.equals("CLASSIFICATION_SCHEMES"))
                    itype = _ACTYPE_CS;
                else if (stype.equals("DATA_ELEMENTS"))
                    itype = _ACTYPE_DE;
                else if (stype.equals("DATA_ELEMENT_CONCEPTS"))
                    itype = _ACTYPE_DEC;
                else if (stype.equals("OBJECT_CLASSES_EXT"))
                    itype = _ACTYPE_OC;
                else if (stype.equals("PROPERTIES_EXT"))
                    itype = _ACTYPE_PROP;
                else if (stype.equals("VALUE_DOMAINS"))
                    itype = _ACTYPE_VD;
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
        }
        return itype;
    }

    /**
     * Build a complex SQL from individual phrases. The calling method provides
     * an array of strings for the SQL SELECT with each representing a specific
     * part of the combined statement. Using the presence or absence of other
     * argument values, a composite statement is formed and executed.
     *
     * @param select_
     *        The array of component parts of the SQL SELECT.
     * @param start_
     *        The start date of the date range. The record date must be greater
     *        than or equal to this value.
     * @param end_
     *        The end date of the date range. The record date must be less than
     *        this value.
     * @param pairs_
     *        The number of date pairs that appear in the master array.
     * @param creators_
     *        The creator (created by) ids of the records or null.
     * @param modifiers_
     *        The modifier (modified by) ids of the records or null.
     * @return The result of the composite SQL.
     */
    private ACData[] selectAC(String select_[], Timestamp start_,
        Timestamp end_, int pairs_, String creators_[], String modifiers_[])
    {
        String select = select_[0];
        int pattern = 0;
        if (creators_ != null && creators_[0].charAt(0) != '(')
        {
            pattern += 1;
            select = select + select_[1];
        }
        if (modifiers_ != null && modifiers_[0].charAt(0) != '(')
        {
            pattern += 2;
            select = select + select_[2];
        }
        select = select + select_[3];
        if (pairs_ == 4)
        {
            if (creators_ != null && creators_[0].charAt(0) != '(')
                select = select + select_[4];
            if (modifiers_ != null && modifiers_[0].charAt(0) != '(')
                select = select + select_[5];
            select = select + select_[6];
        }

        switch (pattern)
        {
            case 1:
                return selectAC(select, start_, end_, pairs_, creators_);
            case 2:
                return selectAC(select, start_, end_, pairs_, modifiers_);
            case 3:
                return selectAC(select, start_, end_, pairs_, creators_,
                    modifiers_);
            default:
                return selectAC(select, start_, end_, pairs_);
        }
    }

    /**
     * Pull all Permissible Values changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectPV(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[])
    {
        // There's always one that doesn't fit the pattern. Any changes to
        // selectBuild() must also be checked here for consistency.

        String start = "to_date('" + start_.toString().substring(0, 10) + "', 'yyyy/mm/dd')";
        String end = "to_date('" + end_.toString().substring(0, 10) + "', 'yyyy/mm/dd')";

        String select =
            "select 'p', 1, 'pv', zz.pv_idseq as id, '', -1, zz.value, '', "
            + "zz.date_modified, zz.date_created, zz.modified_by, zz.created_by, '', "
            + "'', '', ach.changed_column, ach.old_value, ach.new_value, ach.change_datetimestamp, ach.changed_table, ach.changed_by "
            + "from sbrext.ac_change_history_ext ach, sbr.permissible_values_view zz ";

        select = select + "where ach.change_datetimestamp >= " + start + " and ach.change_datetimestamp < " + end + " ";
        if (modifiers_ != null && modifiers_.length > 0 && modifiers_[0].charAt(0) != '(')
            select = select + "AND ach.changed_by in " + selectIN(modifiers_);
        select = select + whereACH(_ACTYPE_PV)
            + "AND zz.pv_idseq = ach.ac_idseq ";
        if (creators_ != null && creators_.length > 0 && creators_[0].charAt(0) != '(')
            select = select + "AND zz.created_by in " + selectIN(creators_);

        if (dates_ == _DATECONLY)
            select = select + "AND zz.date_created >= " + start + " and zz.date_created < " + end + " ";
        else if (dates_ == _DATEMONLY)
            select = select + "AND zz.date_modified is not NULL ";

        select = select + _orderbyACH;

        return selectAC(select);
    }

    /**
     * Pull all Value Meanings changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectVM(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND zz.asl_name IN " + selectIN(wstatus_);
        }

        String[] select = new String[4];
        select[0] =
            "select 'p', 1, 'vm', zz.short_meaning as id, zz.version, zz.vm_id, zz.long_name, zz.conte_idseq as cid, "
            + "zz.date_modified as ctime, zz.date_created, zz.modified_by, zz.created_by, zz.comments, c.name, '' "
            + "from sbr.value_meanings_view zz, sbr.contexts_view c where ";
        select[1] = "zz.created_by in (?) and ";
        select[2] = "zz.modified_by in (?) and ";
        select[3] = "((zz.date_modified is not null and zz.date_modified "
            + _DATECHARS[dates_][0] + " ? and zz.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (zz.date_created is not null and zz.date_created "
            + _DATECHARS[dates_][2] + " ? and zz.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + "and c.conte_idseq = zz.conte_idseq " + wfs_clause
            + " order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Pull all Concepts changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectCON(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND zz.asl_name IN " + selectIN(wstatus_);
        }

        String[] select = new String[4];
        select[0] =
            "select 'p', 1, 'con', zz.con_idseq as id, zz.version, zz.con_id, zz.long_name, zz.conte_idseq as cid, "
            + "zz.date_modified as ctime, zz.date_created, zz.modified_by, zz.created_by, zz.change_note, c.name, '' "
            + "from sbrext.concepts_view_ext zz, sbr.contexts_view c where ";
        select[1] = "zz.created_by in (?) and ";
        select[2] = "zz.modified_by in (?) and ";
        select[3] = "((zz.date_modified is not null and zz.date_modified "
            + _DATECHARS[dates_][0] + " ? and zz.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (zz.date_created is not null and zz.date_created "
            + _DATECHARS[dates_][2] + " ? and zz.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + "and c.conte_idseq = zz.conte_idseq " + wfs_clause
            + " order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Pull all Value Domains changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectVD(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        return selectAC(
            selectBuild(null, _ACTYPE_VD,
                dates_, start_, end_, creators_, modifiers_, wstatus_, null));
    }

    /**
     * Pull all Conceptual Domain changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectCD(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND cd.asl_name IN " + selectIN(wstatus_);
        }

        int pairs;
        String select[] = new String[7];
        select[0] = "(select 'p', 1, 'cd', cd.cd_idseq as id, cd.version, cd.cd_id, cd.long_name, cd.conte_idseq as cid, "
            + "cd.date_modified as ctime, cd.date_created, cd.modified_by, cd.created_by, cd.change_note, c.name, '' "
            + "from sbr.conceptual_domains_view cd, sbr.contexts_view c "
            + "where c.conte_idseq = cd.conte_idseq and ";
        select[1] = "cd.created_by in (?) and ";
        select[2] = "cd.modified_by in (?) and ";

        pairs = 2;
        select[3] = "((cd.date_modified is not null and cd.date_modified "
            + _DATECHARS[dates_][0] + " ? and cd.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (cd.date_created is not null and cd.date_created "
            + _DATECHARS[dates_][2] + " ? and cd.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + ") order by id asc, cid asc";

        return selectAC(select, start_, end_, pairs, creators_, modifiers_);
    }

    /**
     * Pull all Classification Schemes changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectCS(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        return selectAC(
            selectBuild(null, _ACTYPE_CS,
                dates_, start_, end_, creators_, modifiers_, wstatus_, null));
    }

    /**
     * Pull all Property changes in the date range
     * specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectPROP(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        return selectAC(
            selectBuild(null, _ACTYPE_PROP,
                dates_, start_, end_, creators_, modifiers_, wstatus_, null));
    }

    /**
     * Pull all Object Class changes in the date range
     * specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectOC(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        return selectAC(
            selectBuild(null, _ACTYPE_OC,
                dates_, start_, end_, creators_, modifiers_, wstatus_, null));
    }

    /**
     * Pull all Forms/Templates Value Values changed in the date range
     * specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectQCV(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND qc.asl_name IN " + selectIN(wstatus_);
        }

        String select[] = new String[4];
        select[0] = "select 'p', 1, 'qcv', qc.qc_idseq as id, qc.version, qc.qc_id, "
            + "qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified as ctime, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, '' "
            + "from sbrext.quest_contents_view_ext qc, sbr.contexts_view c "
            + "where qc.qtl_name = 'VALID_VALUE' and c.conte_idseq = qc.conte_idseq and ";
        select[1] = "qc.created_by in (?) and ";
        select[2] = "qc.modified_by in (?) and ";
        select[3] = "((qc.date_modified is not null and qc.date_modified "
            + _DATECHARS[dates_][0] + " ? and qc.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (qc.date_created is not null and qc.date_created "
            + _DATECHARS[dates_][2] + " ? and qc.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + "order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Pull all Forms/Templates Questions changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectQCQ(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND qc.asl_name IN " + selectIN(wstatus_);
        }

        String select[] = new String[4];
        select[0] = "select 'p', 1, 'qcq', qc.qc_idseq as id, qc.version, qc.qc_id, "
            + "qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified as ctime, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, '' "
            + "from sbrext.quest_contents_view_ext qc, sbr.contexts_view c "
            + "where qc.qtl_name in ('QUESTION', 'QUESTION_INSTR') and c.conte_idseq = qc.conte_idseq and ";
        select[1] = "qc.created_by in (?) and ";
        select[2] = "qc.modified_by in (?) and ";
        select[3] = "((qc.date_modified is not null and qc.date_modified "
            + _DATECHARS[dates_][0] + " ? and qc.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (qc.date_created is not null and qc.date_created "
            + _DATECHARS[dates_][2] + " ? and qc.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + "order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Pull all Forms/Templates Modules changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectQCM(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND qc.asl_name IN " + selectIN(wstatus_);
        }

        String select[] = new String[4];
        select[0] = "select 'p', 1, 'qcm', qc.qc_idseq as id, qc.version, qc.qc_id, "
            + "qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified as ctime, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, '' "
            + "from sbrext.quest_contents_view_ext qc, sbr.contexts_view c "
            + "where qc.qtl_name = 'MODULE' and c.conte_idseq = qc.conte_idseq and ";
        select[1] = "qc.created_by in (?) and ";
        select[2] = "qc.modified_by in (?) and ";
        select[3] = "((qc.date_modified is not null and qc.date_modified "
            + _DATECHARS[dates_][0] + " ? and qc.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (qc.date_created is not null and qc.date_created "
            + _DATECHARS[dates_][2] + " ? and qc.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + "order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Pull all Protocols changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectPROTO(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND proto.asl_name IN " + selectIN(wstatus_);
        }

        String select[] = new String[4];
        select[0] = "select 'p', 1, 'proto', proto.proto_idseq as id, proto.version, proto.proto_id, "
            + "proto.long_name, proto.conte_idseq as cid, "
            + "proto.date_modified as ctime, proto.date_created, proto.modified_by, proto.created_by, proto.change_note, c.name, '' "
            + "from sbrext.protocols_view_ext proto, sbr.contexts_view c "
            + "where c.conte_idseq = proto.conte_idseq and ";
        select[1] = "proto.created_by in (?) and ";
        select[2] = "proto.modified_by in (?) and ";
        select[3] = "((proto.date_modified is not null and proto.date_modified "
            + _DATECHARS[dates_][0] + " ? and proto.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (proto.date_created is not null and proto.date_created "
            + _DATECHARS[dates_][2] + " ? and proto.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + "order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Pull all Forms/Templates changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectQC(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND qc.asl_name IN " + selectIN(wstatus_);
        }

        String select[] = new String[4];
        select[0] = "select 'p', 1, 'qc', qc.qc_idseq as id, qc.version, qc.qc_id, "
            + "qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified as ctime, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, '' "
            + "from sbrext.quest_contents_view_ext qc, sbr.contexts_view c "
            + "where qc.qtl_name in ('FORM', 'TEMPLATE') and c.conte_idseq = qc.conte_idseq and ";
        select[1] = "qc.created_by in (?) and ";
        select[2] = "qc.modified_by in (?) and ";
        select[3] = "((qc.date_modified is not null and qc.date_modified "
            + _DATECHARS[dates_][0] + " ? and qc.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (qc.date_created is not null and qc.date_created "
            + _DATECHARS[dates_][2] + " ? and qc.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + "order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Pull all Classification Scheme Items changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectCSI(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[])
    {
        String select[] = new String[4];
        select[0] = "select 'p', 1, 'csi', csi_idseq as id, '', -1, csi_name, '', "
            + "date_modified, date_created, modified_by, created_by, comments, '', '' "
            + "from sbr.class_scheme_items_view " + "where ";
        select[1] = "created_by in (?) and ";
        select[2] = "modified_by in (?) and ";
        select[3] = "((date_modified is not null and date_modified "
            + _DATECHARS[dates_][0] + " ? and date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (date_created is not null and date_created "
            + _DATECHARS[dates_][2] + " ? and date_created " + _DATECHARS[dates_][3] + " ?)) "
            + "order by id asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Expand the list to an IN clause.
     *
     * @param regs_ The list of DE registration statuses.
     * @return The expanded IN clause.
     */
    private String selectIN(String regs_[])
    {
        String temp = "";
        for (int ndx = 0; ndx < regs_.length; ++ndx)
        {
            temp = temp + ", '" + regs_[ndx] + "'";
        }
        return "(" + temp.substring(2) + ") ";
    }

    /**
     * Construct the standard change history table where clause.
     *
     * @param table_ The primary changed_table value, one of _ACTYPE_...
     * @return The where clause.
     */
    private String whereACH(int table_)
    {
        String temp = "AND ach.ac_idseq in "
        + "(select distinct ch2.changed_table_idseq from sbrext.ac_change_history_ext ch2 "
        + "where ch2.changed_table = '" + _DBMAP3[table_]._col + "' and ch2.changed_table_idseq = ach.ac_idseq) "
        + "and ach.changed_column not in ('DATE_CREATED', 'DATE_MODIFIED', 'LAE_NAME') "
        + "and (ach.changed_table = '" + _DBMAP3[table_]._col + "' or "
        + "(ach.changed_table = 'AC_CSI' and ach.changed_column = 'CS_CSI_IDSEQ') or "
        + "(ach.changed_table = 'DESIGNATIONS' and ach.changed_column in ('CONTE_IDSEQ', 'DETL_NAME', 'LAE_NAME')) or "
        + "(ach.changed_table = 'REFERENCE_DOCUMENTS' and ach.changed_column in ('DCTL_NAME', 'DISPLAY_ORDER', 'DOC_TEXT', 'RDTL_NAME', 'URL')) or "
        + "(ach.changed_table = 'VD_PVS' and ach.changed_column = 'PV_IDSEQ')) ";
        return temp;
    }

    /**
     * Pull all Data Elements changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @param rstatus_
     *        The list of desired Registration Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectDE(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[], String rstatus_[])
    {
        return selectAC(
            selectBuild(null, _ACTYPE_DE,
                dates_, start_, end_, creators_, modifiers_, wstatus_, rstatus_));
    }

    /**
     * Return the CON_IDSEQ for referenced (used) concepts.
     *
     * @return the con_idseq list
     */
    public String[] selectUsedConcepts()
    {
        String select = "select cv.con_idseq "
            + "from sbrext.component_concepts_view_ext cv, sbr.value_domains_view zz "
            + "where cv.condr_idseq = zz.condr_idseq "
            + "union "
            + "select cv.con_idseq "
            + "from sbrext.component_concepts_view_ext cv, sbrext.object_classes_view_ext zz "
            + "where cv.condr_idseq = zz.condr_idseq "
            + "union "
            + "select cv.con_idseq "
            + "from sbrext.component_concepts_view_ext cv, sbrext.properties_view_ext zz "
            + "where cv.condr_idseq = zz.condr_idseq "
            + "union "
            + "select cv.con_idseq "
            + "from sbrext.component_concepts_view_ext cv, sbr.value_meanings_view zz "
            + "where cv.condr_idseq = zz.condr_idseq "
            + "union "
            + "select cv.con_idseq "
            + "from sbrext.component_concepts_view_ext cv, sbrext.representations_ext zz "
            + "where cv.condr_idseq = zz.condr_idseq";

        return getBasicData0(select);
    }

    /**
     * Return the CON_IDSEQ for all concepts.
     *
     * @return the con_idseq list
     */
    public String[] selectAllConcepts()
    {
        String select = "select con_idseq from sbrext.concepts_view_ext order by con_idseq";

        return getBasicData0(select);
    }

    /**
     * Pull the change history log for a single record.
     *
     * @param idseq_ The idseq of the record.
     *
     * @return The data if any (array length of zero if none found).
     */
    public ACData[] selectWithIDSEQ(String idseq_)
    {
        int itype = selectChangedTableType(idseq_);
        if (itype < 0)
        {
            return new ACData[0];
        }
        return selectAC(
                        selectBuild(idseq_, itype, _DATECM, null, null, null, null, null, null));
    }

    /**
     * Pull all Contexts changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectCONTE(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[])
    {
        String select[] = new String[4];
        select[0] = "select 'p', 1, 'conte', conte_idseq as id, version, -1, name, '', "
            + "date_modified, date_created, modified_by, created_by, '', '', '' "
            + "from sbr.contexts_view " + "where ";
        select[1] = "created_by in (?) and ";
        select[2] = "modified_by in (?) and ";
        select[3] = "((date_modified is not null and date_modified "
            + _DATECHARS[dates_][0] + " ? and date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (date_created is not null and date_created "
            + _DATECHARS[dates_][2] + " ? and date_created " + _DATECHARS[dates_][3] + " ?)) "
            + "order by id asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Build the SQL select to retrieve changes for an Administered Component.
     *
     * @param idseq_ The idseq of a speciifc record of interest.
     * @param type_ The AC type, one of _ACTYPE_...
     * @param dates_ The flag for how dates are compared, _DATECM, _DATECONLY, _DATEMONLY
     * @param start_ The start date for the query.
     * @param end_ The end date for the query.
     * @param creators_ The specific created_by if any.
     * @param modifiers_ The specific modified_by if any.
     * @param wstatus_ The specific Workflow Status if any.
     * @param rstatus_ The specific Registration Status if any.
     * @return The SQL select string.
     */
    private String selectBuild(String idseq_, int type_,
        int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[], String rstatus_[])
    {
        // For consistency of reporting and ease of maintenance the idseq parameter
        // is provided to ignore the date range and pull all information about a
        // specific record.
        if (idseq_ != null && idseq_.length() > 0)
        {
            dates_ = _DATECM;
            start_ = new Timestamp(0);
            end_ = start_;
            creators_ = null;
            modifiers_ = null;
            wstatus_ = null;
            rstatus_ = null;
        }

        // Due to the very conditional nature of this logic, the SQL SELECT is built
        // without the use of substitution arguments ('?').
        String prefix = _DBMAP3[type_]._key;

        // The 'de' type is the only one that doesn't use the same prefix for the public id
        // database column - ugh.
        String prefix2 = (type_ == _ACTYPE_DE) ? "cde" : prefix;

        // Build the basic select and from.
        String select =
            "select 'p', 1, '" + prefix
            + "', zz." + prefix
            + "_idseq as id, zz.version, zz." + prefix2
            + "_id, zz.long_name, zz.conte_idseq, "
            + "zz.date_modified, zz.date_created, zz.modified_by, zz.created_by, zz.change_note, "
            + "c.name, '', ach.changed_column, ach.old_value, ach.new_value, ach.change_datetimestamp, ach.changed_table, ach.changed_by "
            + "from sbrext.ac_change_history_ext ach, " + _DBMAP3[type_]._table + " zz, ";

        // If registration status is not used we only need to add the context
        // table.
        String reg_clause;
        if (rstatus_ == null || rstatus_.length == 0)
        {
            select = select + "sbr.contexts_view c ";
            reg_clause = "";
        }
        // If registration status is used we need to add the context and registration
        // status tables.
        else
        {
            select = select + "sbr.contexts_view c, sbr.ac_registrations_view ar ";
            reg_clause = "AND ar.ac_idseq = zz." + prefix
                + "_idseq AND NVL(ar.registration_status, '(none)') IN " + selectIN(rstatus_);
        }

        // If workflow status is not used we need to be sure and use an empty
        // string.
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        // If workflow status is used we need to qualify by the content of the list.
        else
        {
            wfs_clause = "AND zz.asl_name IN " + selectIN(wstatus_);
        }

        // Building the 'where' clause should be done to keep all qualifications together, e.g.
        // first qualify all for ACH then join to the primary table (ZZ) complete the qualifications
        // then join to the context table.

        // Build the start and end dates.
        String start = "to_date('" + start_.toString().substring(0, 10) + "', 'yyyy/mm/dd')";
        String end = "to_date('" + end_.toString().substring(0, 10) + "', 'yyyy/mm/dd')";

        // Always checking the date range first.
        if (idseq_ == null || idseq_.length() == 0)
            select = select + "where ach.change_datetimestamp >= " + start + " and ach.change_datetimestamp < " + end + " ";
        else
            select = select + "where ach.ac_idseq = '" + idseq_ + "' ";

        // If modifiers are provided be sure to get everything.
        if (modifiers_ != null && modifiers_.length > 0 && modifiers_[0].charAt(0) != '(')
            select = select + "AND ach.changed_by in " + selectIN(modifiers_);

        // Now qualify by the record type of interest and join to the primary table.
        select = select + whereACH(type_)
            + "AND zz." + prefix + "_idseq = ach.ac_idseq ";

        // If creators are provided they must be qualified by the primary table not the change table.
        if (creators_ != null && creators_.length > 0 && creators_[0].charAt(0) != '(')
            select = select + "AND zz.created_by in " + selectIN(creators_);

        // When looking for both create and modified dates no extra clause is needed. For create
        // date only qualify against the primary table.
        if (dates_ == _DATECONLY)
            select = select + "AND zz.date_created >= " + start + " and zz.date_created < " + end + " ";

        // For modify date only qualify the primary table. The actual date is not important because we
        // qualified the records from the history table by date already.
        else if (dates_ == _DATEMONLY)
            select = select + "AND zz.date_modified is not NULL ";

        // Put everything together including the join to the context table and the sort order clause.
        return select + wfs_clause + reg_clause + "AND c.conte_idseq = zz.conte_idseq " + _orderbyACH;
    }

    /**
     * Pull all Data Element Concepts changed in the date range specified.
     *
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectDEC(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[])
    {
        return selectAC(
            selectBuild(null, _ACTYPE_DEC,
                dates_, start_, end_, creators_, modifiers_, wstatus_, null));
    }

    /**
     * Select the dependant data. In Oracle an "in" clause may have a maximum of
     * 1000 items. If the array length (ids_) is greater than 1000 it is broken
     * up into pieces. The result is that should an order by clause also appear,
     * the end result may not be correct as the SQL had to be performed in
     * multiple pieces.
     *
     * @param select_
     *        The SQL select.
     * @param ids_
     *        The array holding the id's for the query.
     * @return The ACData array of the results.
     */
    private ACData[] selectAC(String select_, ACData ids_[])
    {
        if (ids_ == null || ids_.length == 0)
            return new ACData[0];

        // Oracle limit on "in" clauses.
        int limit = 1000;
        if (ids_.length < limit)
            return selectAC2(select_, ids_);

        // When more than 1000 we have to break up the list
        // and merge the results together.
        ACData results[] = new ACData[0];
        int group = limit;
        int indx = 0;
        while (indx < ids_.length)
        {
            ACData tset[] = new ACData[group];
            System.arraycopy(ids_, indx, tset, 0, group);
            indx += group;
            ACData rset[] = selectAC2(select_, tset);
            tset = results;
            results = new ACData[tset.length + rset.length];

            // Now that we have a place to store the composite
            // list perform a simple merge as both are already
            // sorted.
            int tndx = 0;
            int rndx = 0;
            int ndx = 0;
            if (tset.length > 0 && rset.length > 0)
            {
                while (ndx < results.length)
                {
                    if (tset[tndx].compareUsingIDS(rset[rndx]) <= 0)
                    {
                        results[ndx++] = tset[tndx++];
                        if (tndx == tset.length)
                            break;
                    }
                    else
                    {
                        results[ndx++] = rset[rndx++];
                        if (rndx == rset.length)
                            break;
                    }
                }
            }

            // We've exhausted the 'temp' list so copy the rest of the
            // 'rc' list.
            if (tndx == tset.length)
                System.arraycopy(rset, rndx, results, ndx, rset.length - rndx);

            // We've exhausted the 'rc' list so copy the rest of the
            // 'temp' list.
            else
                System.arraycopy(tset, tndx, results, ndx, tset.length - tndx);

            // Do next group.
            tndx = ids_.length - indx;
            if (group > tndx)
                group = tndx;

            // Force conservation of memory.
            tset = null;
            rset = null;
        }
        return results;
    }

    /**
     * Select the dependant data. This method does not test the length of the
     * array (ids_) and therefore should only be called when 1000 ids or less
     * are needed.
     *
     * @param select_
     *        The SQL containing the "in" clause.
     * @param ids_
     *        The id values to be bound.
     * @return The result of the query.
     */
    private ACData[] selectAC2(String select_, ACData ids_[])
    {
        String markers = expandMarkers(ids_.length);

        // Split the string based on "?" markers.
        String parts[] = select_.split("\\?");
        String select = null;
        if (parts.length == 2)
        {
            select = parts[0] + markers + parts[1];
        }
        else if (parts.length == 3)
        {
            select = parts[0] + markers + parts[1] + markers + parts[2];
        }
        else
        {
            // Only needed during development.
            _logger.error("DEVELOPMENT ERROR 1: ==>\n" + select_
                + "\n<== unexpected SQL form.");
            return null;
        }

        try
        {
            // Build, bind and execute the statement.
            PreparedStatement pstmt = _conn.prepareStatement(select);

            int cnt = 1;
            for (int ndx = 0; ndx < ids_.length; ++ndx)
            {
                pstmt.setString(cnt++, ids_[ndx].getIDseq());
            }
            if (parts.length == 3)
            {
                for (int ndx = 0; ndx < ids_.length; ++ndx)
                {
                    pstmt.setString(cnt++, ids_[ndx].getIDseq());
                }
            }

            ResultSet rs = pstmt.executeQuery();
            ACData list[] = copyResults(rs);
            rs.close();
            pstmt.close();

            return list;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    /**
     * Find the Value Domains that are affected by changes to the Permissible
     * Values.
     *
     * @param pv_
     *        The list of permissible values identified as changed or created.
     * @return The array of value domains.
     */
    public ACData[] selectVDfromPV(ACData pv_[])
    {
        String select = "(select 's', 1, 'vd', vd.vd_idseq as id, vd.version, vd.vd_id, vd.long_name, vd.conte_idseq as cid, "
            + "vd.date_modified, vd.date_created, vd.modified_by, vd.created_by, vd.change_note, c.name, pv.pv_idseq "
            + "from sbr.value_domains_view vd, sbr.vd_pvs_view vp, sbr.permissible_values_view pv, sbr.contexts_view c "
            + "where pv.pv_idseq in (?) and vp.pv_idseq = pv.pv_idseq and vd.vd_idseq = vp.vd_idseq and "
            + "c.conte_idseq = vd.conte_idseq "
            + "union "
            + "select 's', 1, 'vd', ac.ac_idseq as id, ac.version, xx.vd_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, pv.pv_idseq "
            + "from sbr.permissible_values_view pv, sbr.vd_pvs_view vp, sbr.value_domains_view xx, "
            + "sbr.admin_components_view ac, sbr.designations_view dv, sbr.contexts_view c "
            + "where pv.pv_idseq in (?) and vp.pv_idseq = pv.pv_idseq and xx.vd_idseq = vp.vd_idseq "
            + "and ac.ac_idseq = xx.vd_idseq and ac.actl_name = 'VALUEDOMAIN' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, pv_);
    }

    /**
     * Find the Permissible Values that are affected by changes to the Value Meanings.
     *
     * @param vm_
     *        The list of value meanings identified as changed or created.
     * @return The array of value domains.
     */
    public ACData[] selectPVfromVM(ACData vm_[])
    {
        String select = "select 's', 1, 'pv', pv.pv_idseq as id, '', -1, pv.value, '', "
        + "pv.date_modified, pv.date_created, pv.modified_by, pv.created_by, '', '', vm.short_meaning "
        + "from sbr.permissible_values_view pv, sbr.value_meanings_view vm "
        + "where vm.short_meaning in (?) and pv.short_meaning = vm.short_meaning ";

        return selectAC(select, vm_);
    }

    /**
     * Find the Conceptual Domains affected by changes to the Value Domains
     * provided.
     *
     * @param vd_
     *        The list of value domains.
     * @return The array of conceptual domains.
     */
    public ACData[] selectCDfromVD(ACData vd_[])
    {
        String select = "(select 's', 1, 'cd', cd.cd_idseq as id, cd.version, cd.cd_id, cd.long_name, cd.conte_idseq as cid, "
            + "cd.date_modified, cd.date_created, cd.modified_by, cd.created_by, cd.change_note, c.name, vd.vd_idseq "
            + "from sbr.conceptual_domains_view cd, sbr.contexts_view c, sbr.value_domains_view vd "
            + "where vd.vd_idseq in (?) and cd.cd_idseq = vd.cd_idseq and c.conte_idseq = cd.conte_idseq "
            + "union "
            + "select 's', 1, 'cd', ac.ac_idseq as id, ac.version, xx.cd_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, vd.vd_idseq "
            + "from sbr.admin_components_view ac, sbr.conceptual_domains_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbr.value_domains_view vd "
            + "where vd.vd_idseq in (?) and xx.cd_idseq = vd.cd_idseq and ac.ac_idseq = xx.cd_idseq and "
            + "ac.actl_name = 'CONCEPTUALDOMAIN' and dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, vd_);
    }

    /**
     * Find the Conceptual Domains affected by changes to the Data Element Concepts
     * provided.
     *
     * @param dec_
     *        The list of data element concepts.
     * @return The array of conceptual domains.
     */
    public ACData[] selectCDfromDEC(ACData dec_[])
    {
        String select = "(select 's', 1, 'cd', cd.cd_idseq as id, cd.version, cd.cd_id, cd.long_name, cd.conte_idseq as cid, "
            + "cd.date_modified, cd.date_created, cd.modified_by, cd.created_by, cd.change_note, c.name, dec.dec_idseq "
            + "from sbr.conceptual_domains_view cd, sbr.contexts_view c, sbr.data_element_concepts_view dec "
            + "where dec.dec_idseq in (?) and cd.cd_idseq = dec.dec_idseq and c.conte_idseq = cd.conte_idseq "
            + "union "
            + "select 's', 1, 'cd', ac.ac_idseq as id, ac.version, xx.cd_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, dec.dec_idseq "
            + "from sbr.admin_components_view ac, sbr.conceptual_domains_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbr.data_element_concepts_view dec "
            + "where dec.dec_idseq in (?) and xx.cd_idseq = dec.cd_idseq and ac.ac_idseq = xx.cd_idseq and "
            + "ac.actl_name = 'CONCEPTUALDOMAIN' and dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, dec_);
    }

    /**
     * Select the Data Elements affected by the Value Domains provided.
     *
     * @param vd_
     *        The value domain list.
     * @return The array of related data elements.
     */
    public ACData[] selectDEfromVD(ACData vd_[])
    {
        String select = "(select 's', 1, 'de', de.de_idseq as id, de.version, de.cde_id, de.long_name, de.conte_idseq as cid, "
            + "de.date_modified, de.date_created, de.modified_by, de.created_by, de.change_note, c.name, vd.vd_idseq "
            + "from sbr.data_elements_view de, sbr.contexts_view c, sbr.value_domains_view vd "
            + "where vd.vd_idseq in (?) and de.vd_idseq = vd.vd_idseq and c.conte_idseq = de.conte_idseq "
            + "union "
            + "select 's', 1, 'de', ac.ac_idseq as id, ac.version, xx.cde_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, vd.vd_idseq "
            + "from sbr.admin_components_view ac, sbr.data_elements_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbr.value_domains_view vd "
            + "where vd.vd_idseq in (?) and xx.vd_idseq = vd.vd_idseq and xx.de_idseq = ac.ac_idseq and ac.actl_name = 'DATAELEMENT' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, vd_);
    }

    /**
     * Select the Data Element Concepts affected by the Properties provided.
     *
     * @param prop_
     *        The property list.
     * @return The array of related data element concepts.
     */
    public ACData[] selectDECfromPROP(ACData prop_[])
    {
        String select = "(select 's', 1, 'dec', dec.dec_idseq as id, dec.version, dec.dec_id, dec.long_name, dec.conte_idseq as cid, "
            + "dec.date_modified, dec.date_created, dec.modified_by, dec.created_by, dec.change_note, c.name, prop.prop_idseq "
            + "from sbr.data_element_concepts_view dec, sbr.contexts_view c, sbrext.properties_view_ext prop "
            + "where prop.prop_idseq in (?) and dec.prop_idseq = prop.prop_idseq and c.conte_idseq = dec.conte_idseq "
            + "union "
            + "select 's', 1, 'dec', ac.ac_idseq as id, ac.version, xx.dec_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, prop.prop_idseq "
            + "from sbr.admin_components_view ac, sbr.data_element_concepts_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbrext.properties_view_ext prop "
            + "where prop.prop_idseq in (?) and xx.prop_idseq = prop.prop_idseq and xx.dec_idseq = ac.ac_idseq and ac.actl_name = 'DE_CONCEPT' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, prop_);
    }

    /**
     * Select the Properties affected by the Concepts provided.
     *
     * @param con_
     *        The concept list.
     * @return The array of related properties.
     */
    public ACData[] selectPROPfromCON(ACData con_[])
    {
        String select = "select 's', 1, 'prop', prop.prop_idseq as id, prop.version, prop.prop_id, prop.long_name, prop.conte_idseq as cid, "
            + "prop.date_modified, prop.date_created, prop.modified_by, prop.created_by, prop.change_note, c.name, con.con_idseq "
            + "from sbrext.properties_view_ext prop, sbrext.component_concepts_view_ext ccv, sbrext.concepts_view_ext con, sbr.contexts_view c "
            + "where con.con_idseq in (?) and ccv.con_idseq = con.con_idseq and prop.condr_idseq = ccv.condr_idseq and c.conte_idseq = prop.conte_idseq  "
            + "order by id asc, cid asc";

        return selectAC(select, con_);
    }

    /**
     * Select the Object Classes affected by the Concepts provided.
     *
     * @param con_
     *        The concept list.
     * @return The array of related object classes.
     */
    public ACData[] selectOCfromCON(ACData con_[])
    {
        String select = "select 's', 1, 'oc', oc.oc_idseq as id, oc.version, oc.oc_id, oc.long_name, oc.conte_idseq as cid, "
            + "oc.date_modified, oc.date_created, oc.modified_by, oc.created_by, oc.change_note, c.name, con.con_idseq "
            + "from sbrext.object_classes_view_ext oc, sbrext.component_concepts_view_ext ccv, sbrext.concepts_view_ext con, sbr.contexts_view c "
            + "where con.con_idseq in (?) and ccv.con_idseq = con.con_idseq and oc.condr_idseq = ccv.condr_idseq and c.conte_idseq = oc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, con_);
    }

    /**
     * Select the Data Element Concepts affected by the Object Classes provided.
     *
     * @param oc_
     *        The object class list.
     * @return The array of related data element concepts.
     */
    public ACData[] selectDECfromOC(ACData oc_[])
    {
        String select = "(select 's', 1, 'dec', dec.dec_idseq as id, dec.version, dec.dec_id, dec.long_name, dec.conte_idseq as cid, "
            + "dec.date_modified, dec.date_created, dec.modified_by, dec.created_by, dec.change_note, c.name, oc.oc_idseq "
            + "from sbr.data_element_concepts_view dec, sbr.contexts_view c, sbrext.object_classes_view_ext oc "
            + "where oc.oc_idseq in (?) and dec.oc_idseq = oc.oc_idseq and c.conte_idseq = dec.conte_idseq "
            + "union "
            + "select 's', 1, 'dec', ac.ac_idseq as id, ac.version, xx.dec_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, oc.oc_idseq "
            + "from sbr.admin_components_view ac, sbr.data_element_concepts_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbrext.object_classes_view_ext oc "
            + "where oc.oc_idseq in (?) and xx.oc_idseq = oc.oc_idseq and xx.dec_idseq = ac.ac_idseq and ac.actl_name = 'DE_CONCEPT' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, oc_);
    }

    /**
     * Select the Data Elements affected by the Data Element Concepts provided.
     *
     * @param dec_
     *        The data element concepts list.
     * @return The array of related data elements.
     */
    public ACData[] selectDEfromDEC(ACData dec_[])
    {
        String select = "(select 's', 1, 'de', de.de_idseq as id, de.version, de.cde_id, de.long_name, de.conte_idseq as cid, "
            + "de.date_modified, de.date_created, de.modified_by, de.created_by, de.change_note, c.name, dec.dec_idseq "
            + "from sbr.data_elements_view de, sbr.contexts_view c, sbr.data_element_concepts_view dec "
            + "where dec.dec_idseq in (?) and de.dec_idseq = dec.dec_idseq and c.conte_idseq = de.conte_idseq "
            + "union "
            + "select 's', 1, 'de', ac.ac_idseq as id, ac.version, xx.cde_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, dec.dec_idseq "
            + "from sbr.admin_components_view ac, sbr.data_elements_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbr.data_element_concepts_view dec "
            + "where dec.dec_idseq in (?) and xx.dec_idseq = dec.dec_idseq and xx.de_idseq = ac.ac_idseq and ac.actl_name = 'DATAELEMENT' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, dec_);
    }

    /**
     * Select the Classification Scheme Item affected by the Data Elements
     * provided.
     *
     * @param de_
     *        The data element list.
     * @return The array of related classification scheme items.
     */
    public ACData[] selectCSIfromDE(ACData de_[])
    {
        String select = "select 's', 1, 'csi', civ.csi_idseq as id, '', -1, civ.csi_name, '', "
            + "civ.date_modified, civ.date_created, civ.modified_by, civ.created_by, civ.comments, '', de.de_idseq "
            + "from sbr.class_scheme_items_view civ, sbr.data_elements_view de, sbr.admin_components_view ac, "
            + "sbr.ac_csi_view ai, sbr.cs_csi_view ci "
            + "where de.de_idseq in (?) and ac.ac_idseq = de.de_idseq and ai.ac_idseq = ac.ac_idseq and "
            + "ci.cs_csi_idseq = ai.cs_csi_idseq and civ.csi_idseq = ci.csi_idseq "
            + "order by id asc";

        return selectAC(select, de_);
    }

    /**
     * Select the Classification Scheme Item affected by the Data Element Concepts
     * provided.
     *
     * @param dec_
     *        The data element concept list.
     * @return The array of related classification scheme items.
     */
    public ACData[] selectCSIfromDEC(ACData dec_[])
    {
        String select = "select 's', 1, 'csi', civ.csi_idseq as id, '', -1, civ.csi_name, '', "
            + "civ.date_modified, civ.date_created, civ.modified_by, civ.created_by, civ.comments, '', dec.dec_idseq "
            + "from sbr.class_scheme_items_view civ, sbr.data_element_concepts_view dec, sbr.admin_components_view ac, "
            + "sbr.ac_csi_view ai, sbr.cs_csi_view ci "
            + "where dec.dec_idseq in (?) and ac.ac_idseq = dec.dec_idseq and ai.ac_idseq = ac.ac_idseq and "
            + "ci.cs_csi_idseq = ai.cs_csi_idseq and civ.csi_idseq = ci.csi_idseq "
            + "order by id asc";

        return selectAC(select, dec_);
    }

    /**
     * Select the Classification Scheme Item affected by the Value Domains
     * provided.
     *
     * @param vd_
     *        The value domain list.
     * @return The array of related classification scheme items.
     */
    public ACData[] selectCSIfromVD(ACData vd_[])
    {
        String select = "select 's', 1, 'csi', civ.csi_idseq as id, '', -1, civ.csi_name, '', "
            + "civ.date_modified, civ.date_created, civ.modified_by, civ.created_by, civ.comments, '', vd.vd_idseq "
            + "from sbr.class_scheme_items_view civ, sbr.value_domains_view vd, sbr.admin_components_view ac, "
            + "sbr.ac_csi_view ai, sbr.cs_csi_view ci "
            + "where vd.vd_idseq in (?) and ac.ac_idseq = vd.vd_idseq and ai.ac_idseq = ac.ac_idseq and "
            + "ci.cs_csi_idseq = ai.cs_csi_idseq and civ.csi_idseq = ci.csi_idseq "
            + "order by id asc";

        return selectAC(select, vd_);
    }

    /**
     * Select the Forms/Templates affected by the Data Elements provided.
     *
     * @param de_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCQfromDE(ACData de_[])
    {
        String select = "select 's', 1, 'qcq', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, de.de_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbr.data_elements_view de, sbr.contexts_view c "
            + "where de.de_idseq in (?) and qc.de_idseq = de.de_idseq and qc.qtl_name in ('QUESTION', 'QUESTION_INSTR') and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, de_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     *
     * @param vd_
     *        The value domain list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCQfromVD(ACData vd_[])
    {
        String select = "select 's', 1, 'qcq', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, vd.vd_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbr.value_domains_view vd, sbr.contexts_view c "
            + "where vd.vd_idseq in (?) and qc.dn_vd_idseq = vd.vd_idseq and qc.qtl_name in ('QUESTION', 'QUESTION_INSTR') and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, vd_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     *
     * @param vd_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCVfromVD(ACData vd_[])
    {
        String select = "select 's', 1, 'qcv', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, vd.vd_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbr.value_domains_view vd, sbr.vd_pvs_view vp, sbr.contexts_view c "
            + "where vd.vd_idseq in (?) and vp.vd_idseq = vd.vd_idseq and qc.vp_idseq = vp.vp_idseq and "
            + "qc.qtl_name = 'VALID_VALUE' and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, vd_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     *
     * @param qcv_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCQfromQCV(ACData qcv_[])
    {
        String select = "select 's', 1, 'qcq', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, qc2.qc_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbrext.quest_contents_view_ext qc2, sbr.contexts_view c "
            + "where qc2.qc_idseq in (?) and qc2.qtl_name = 'VALID_VALUE' and qc.qc_idseq = qc2.p_qst_idseq and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, qcv_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     *
     * @param qcq_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCMfromQCQ(ACData qcq_[])
    {
        String select = "select 's', 1, 'qcm', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, qc2.qc_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbrext.quest_contents_view_ext qc2, sbr.contexts_view c "
            + "where qc2.qc_idseq in (?) and qc2.qtl_name in ('QUESTION', 'QUESTION_INSTR') and qc.qc_idseq = qc2.p_mod_idseq and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, qcq_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     *
     * @param qcm_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCfromQCM(ACData qcm_[])
    {
        String select = "select 's', 1, 'qc', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, qc2.qc_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbrext.quest_contents_view_ext qc2, sbr.contexts_view c "
            + "where qc2.qc_idseq in (?) and qc2.qtl_name = 'MODULE' and qc.qc_idseq = qc2.dn_crf_idseq and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, qcm_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     *
     * @param qcq_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCfromQCQ(ACData qcq_[])
    {
        String select = "select 's', 1, 'qc', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, qc2.qc_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbrext.quest_contents_view_ext qc2, sbr.contexts_view c "
            + "where qc2.qc_idseq in (?) and qc2.qtl_name in ('QUESTION', 'QUESTION_INSTR') and qc2.p_mod_idseq is null and "
            + "qc.qc_idseq = qc2.dn_crf_idseq and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, qcq_);
    }

    /**
     * Select the Classification Schemes affected by the Classification Scheme
     * Items provided.
     *
     * @param csi_
     *        The classification scheme items list.
     * @return The array of related classification schemes.
     */
    public ACData[] selectCSfromCSI(ACData csi_[])
    {
        String select = "(select 's', 1, 'cs', cs.cs_idseq as id, cs.version, cs.cs_id, cs.long_name, cs.conte_idseq as cid, "
            + "cs.date_modified, cs.date_created, cs.modified_by, cs.created_by, cs.change_note, c.name, civ.csi_idseq "
            + "from sbr.classification_schemes_view cs, sbr.contexts_view c, sbr.cs_csi_view ci, "
            + "sbr.class_scheme_items_view civ "
            + "where civ.csi_idseq in (?) and ci.csi_idseq = civ.csi_idseq and cs.cs_idseq = ci.cs_idseq and "
            + "c.conte_idseq = cs.conte_idseq "
            + "union "
            + "select 's', 1, 'cs', ac.ac_idseq as id, ac.version, xx.cs_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, civ.csi_idseq "
            + "from sbr.admin_components_view ac, sbr.classification_schemes_view xx, sbr.cs_csi_view ci, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbr.class_scheme_items_view civ "
            + "where civ.csi_idseq in (?) and ci.csi_idseq = civ.csi_idseq and xx.cs_idseq = ci.cs_idseq and "
            + "ac.ac_idseq = xx.cs_idseq and ac.actl_name = 'CLASSIFICATION' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, csi_);
    }

    /**
     * Select the Contexts affected by the Classification Schemes provided.
     *
     * @param cs_
     *        The classification schemes list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromCS(ACData cs_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', cs.cs_idseq "
            + "from sbr.contexts_view c, sbr.classification_schemes_view cs "
            + "where cs.cs_idseq in (?) and c.conte_idseq = cs.conte_idseq "
            + "order by id asc";

        return selectAC(select, cs_);
    }

    /**
     * Select the Contexts affected by the Conceptual Domains provided.
     *
     * @param cd_
     *        The conceptual domains list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromCD(ACData cd_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', cd.cd_idseq "
            + "from sbr.contexts_view c, sbr.conceptual_domains_view cd "
            + "where cd.cd_idseq in (?) and c.conte_idseq = cd.conte_idseq "
            + "order by id asc";

        return selectAC(select, cd_);
    }

    /**
     * Select the Contexts affected by the Value Domains provided.
     *
     * @param vd_
     *        The value domains list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromVD(ACData vd_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', vd.vd_idseq "
            + "from sbr.contexts_view c, sbr.value_domains_view vd "
            + "where vd.vd_idseq in (?) and c.conte_idseq = vd.conte_idseq "
            + "order by id asc";

        return selectAC(select, vd_);
    }

    /**
     * Select the Contexts affected by the Data Elements provided.
     *
     * @param de_
     *        The data elements list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromDE(ACData de_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', de.de_idseq "
            + "from sbr.contexts_view c, sbr.data_elements_view de "
            + "where de.de_idseq in (?) and c.conte_idseq = de.conte_idseq "
            + "order by id asc";

        return selectAC(select, de_);
    }

    /**
     * Select the Contexts affected by the Properties provided.
     *
     * @param prop_
     *        The properties list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromPROP(ACData prop_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', prop.prop_idseq "
            + "from sbr.contexts_view c, sbrext.properties_view_ext prop "
            + "where prop.prop_idseq in (?) and c.conte_idseq = prop.conte_idseq "
            + "order by id asc";

        return selectAC(select, prop_);
    }

    /**
     * Select the Contexts affected by the Object Classes provided.
     *
     * @param oc_
     *        The object class list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromOC(ACData oc_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', oc.oc_idseq "
            + "from sbr.contexts_view c, sbrext.object_classes_view_ext oc "
            + "where oc.oc_idseq in (?) and c.conte_idseq = oc.conte_idseq "
            + "order by id asc";

        return selectAC(select, oc_);
    }

    /**
     * Select the Contexts affected by the Concepts provided.
     *
     * @param con_
     *        The object class list.
     * @return The array of related concepts.
     */
    public ACData[] selectCONTEfromCON(ACData con_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', con.con_idseq "
            + "from sbr.contexts_view c, sbrext.concepts_view_ext con "
            + "where con.con_idseq in (?) and c.conte_idseq = con.conte_idseq "
            + "order by id asc";

        return selectAC(select, con_);
    }

    /**
     * Select the Contexts affected by the Protocols provided.
     *
     * @param proto_
     *        The protocols list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromPROTO(ACData proto_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', proto.proto_idseq "
            + "from sbr.contexts_view c, sbrext.protocols_view_ext proto "
            + "where proto.proto_idseq in (?) and c.conte_idseq = proto.conte_idseq "
            + "order by id asc";

        return selectAC(select, proto_);
    }

    /**
     * Select the Protocols affected by the Forms/Templates provided.
     *
     * @param qc_
     *        The forms/templates list.
     * @return The array of related contexts.
     */
    public ACData[] selectPROTOfromQC(ACData qc_[])
    {
        String select = "select 's', 1, 'proto', proto.proto_idseq as id, proto.version, proto.proto_id, proto.long_name, c.conte_idseq, "
            + "proto.date_modified, proto.date_created, proto.modified_by, proto.created_by, proto.change_note, c.name, qc.qc_idseq "
            + "from sbr.contexts_view c, sbrext.protocols_view_ext proto, sbrext.protocol_qc_ext pq, sbrext.quest_contents_view_ext qc "
            + "where qc.qc_idseq in (?) "
            + "and pq.qc_idseq = qc.qc_idseq "
            + "and proto.proto_idseq = pq.proto_idseq "
            + "and c.conte_idseq = proto.conte_idseq "
            + "order by id asc";

        return selectAC(select, qc_);
    }

    /**
     * Select the Contexts affected by the Forms/Templates provided.
     *
     * @param qc_
     *        The forms/templates list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromQC(ACData qc_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', qc.qc_idseq "
            + "from sbr.contexts_view c, sbrext.quest_contents_view_ext qc "
            + "where qc.qc_idseq in (?) and c.conte_idseq = qc.conte_idseq "
            + "order by id asc";

        return selectAC(select, qc_);
    }

    /**
     * Select the Contexts affected by the Data Element Concepts provided.
     *
     * @param dec_
     *        The data element concepts list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromDEC(ACData dec_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', dec.dec_idseq "
            + "from sbr.contexts_view c, sbr.data_element_concepts_view dec "
            + "where dec.dec_idseq in (?) and c.conte_idseq = dec.conte_idseq "
            + "order by id asc";

        return selectAC(select, dec_);
    }

    /**
     * For performance reasons as "names" are the most common data required, a
     * cache is created to avoid hitting the database with too many individual
     * requests. This cache is good for the life of this DBAlert object and will
     * be rebuilt as needed with each new DBAlert.
     *
     * @param id_
     *        The name id to look up in the database.
     * @return When > 0, the position of the name in the cache. When < 0, the
     *         position it should be in the cache when added later.
     */
    private int findName(String id_)
    {
        int min = 0;
        int max = _nameID.length;

        // Use a binary search. It seems the most efficient for this purpose.
        while (true)
        {
            int ndx = (max + min) / 2;
            int compare = id_.compareTo(_nameID[ndx]);
            if (compare == 0)
            {
                return ndx;
            }
            else if (compare > 0)
            {
                if (min == ndx)
                {
                    ++min;
                    return -min;
                }
                min = ndx;
            }
            else
            {
                if (max == ndx)
                    return -max;
                max = ndx;
            }
        }
    }

    /**
     * Cache names internally as they are encountered. If the findName() method
     * can not locate a name in the cache it will be added by this method.
     *
     * @param pos_
     *        The insert position returned from findName().
     * @param id_
     *        The name id to use as a key.
     * @param name_
     *        The name to return for this id.
     */
    private void cacheName(int pos_, String id_, String name_)
    {
        // Don't save null names, use the id if needed.
        if (name_ == null)
            name_ = id_;

        // Move all existing records down to make room for the new name.
        String nid[] = new String[_nameID.length + 1];
        String ntxt[] = new String[nid.length];

        int ndx;
        int ndx2 = 0;
        for (ndx = 0; ndx < pos_; ++ndx)
        {
            nid[ndx] = _nameID[ndx2];
            ntxt[ndx] = _nameText[ndx2++];
        }

        // Add the new name.
        nid[ndx] = new String(id_);
        ntxt[ndx] = new String(name_);

        // Copy the rest and reset the arrays.
        for (++ndx; ndx < nid.length; ++ndx)
        {
            nid[ndx] = _nameID[ndx2];
            ntxt[ndx] = _nameText[ndx2++];
        }
        _nameID = nid;
        _nameText = ntxt;
    }

    /**
     * Retrieve a string name representation for the "object" id provided.
     *
     * @param table_
     *        The known database table name or null if the method should use a
     *        default based on the col_ value.
     * @param col_
     *        The column name which corresponds to the id_ provided.
     * @param id_
     *        The id of the specific database record desired.
     * @return The "name" from the record, this may correspond to the long_name,
     *         prefferred_name, etc database columns depending on the table
     *         being used.
     */
    public String selectName(String table_, String col_, String id_)
    {
        // Can't work without a column name.
        if (col_ == null)
            return id_;
        if (id_ == null || id_.length() == 0)
            return null;

        // Determine the real table and column names to use.
        int npos = 0;
        String table = table_;
        String name = "long_name";
        String col = col_;
        String extra = "";
        if (table == null || table.length() == 0)
        {
            int ndx = DBAlertUtil.binarySearch(_DBMAP2, col);
            if (ndx == -1)
                return id_;
            table = _DBMAP2[ndx]._val;
            name = _DBMAP2[ndx]._subs;
            col = _DBMAP2[ndx]._col;
            extra = _DBMAP2[ndx]._xtra;
            if (col.equals("ua_name"))
            {
                // Is the name cached?
                npos = findName(id_);
                if (npos >= 0)
                    return _nameText[npos];
            }
        }

        // Build a select and retrieve the "name".
        String select = "select " + name + " from " + table + " where " + col
            + " = ?" + extra;
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, id_);
            ResultSet rs = pstmt.executeQuery();
            name = "";
            while (rs.next())
                name = name + "\n" + rs.getString(1);
            if (name.length() == 0)
                name = null;
            else
                name = name.substring(1);
            rs.close();
            pstmt.close();

            if (col.equals("ua_name") && npos < 0)
            {
                cacheName(-npos, id_, name);
            }
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            name = "(*error*)";
        }
        return (name == null) ? id_ : name;
    }

    /**
     * Retrieve the "names" for a list of columns and ids. WARNING this is a
     * destructive method. It changes the content of ids_ by replacing the
     * original value with the retrieved name.
     *
     * @param cols_
     *        The names of the columns corresponding to the ids.
     * @param ids_
     *        On input the ids of the specific records to query. On return the
     *        names of the records if they could be determined.
     */
    public void selectNames(String cols_[], String ids_[])
    {
        for (int ndx = 0; ndx < cols_.length; ++ndx)
        {
            ids_[ndx] = selectName(null, cols_[ndx], ids_[ndx]);
        }
    }

    /**
     * Update the Auto Run or Manual Run timestamp.
     *
     * @param id_
     *        The alert id to update.
     * @param stamp_
     *        The new time.
     * @param run_
     *        true to update the auto run time, false to update the manual run
     *        time
     * @param setInactive_
     *        true to set the alert status to inactive, false to leave the
     *        status unchanged
     * @return 0 if successful, otherwise the database error code.
     */
    public int updateRun(String id_, Timestamp stamp_, boolean run_,
        boolean setInactive_)
    {
        String update = "update sbrext.sn_alert_view_ext set "
            + ((run_) ? "last_auto_run" : "last_manual_run") + " = ?,"
            + ((setInactive_) ? " al_status = 'I', " : "")
            + "modified_by = ? where al_idseq = ?";
        try
        {
            PreparedStatement pstmt = null;
            // Set all the SQL arguments.
            pstmt = _conn.prepareStatement(update);
            pstmt.setTimestamp(1, stamp_);
            pstmt.setString(2, _user);
            pstmt.setString(3, id_);
            pstmt.executeUpdate();
            pstmt.close();
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + update
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Return the recipients names in ascending order by first name as a single
     * string. If the recipient is a broadcast context group the group is expanded.
     * Those who have elected not to receive broadcasts from a context group are
     * not included. All freeform email addresses are listed after the names
     * retrieved from the account table.
     *
     * @param recipients_ The Alert recipient list.
     * @return A single comma separate list of names and email addresses with
     *      the broadcast context groups expanded.
     */
    public String selectRecipientNames(String recipients_[])
    {
        // Check input.
        if (recipients_ == null || recipients_.length == 0)
            return "(none)";

        // Break the recipients list apart.
        String contexts = "";
        String users = "";
        String emails = "";
        for (int ndx = 0; ndx < recipients_.length; ++ndx)
        {
            if (recipients_[ndx].charAt(0) == '/')
                contexts = contexts + ", '" + recipients_[ndx].substring(1) + "'";
            else if (recipients_[ndx].indexOf('@') < 0)
                users = users + ", '" +  recipients_[ndx] + "'";
            else
                emails = emails + ", " +  recipients_[ndx];
        }

        // Build the select for user names
        String select = "";
        if (users.length() > 0)
            select += "select ua.name as lname from sbr.user_accounts_view ua where ua.ua_name in ("
                + users.substring(2)
                + ") and ua.electronic_mail_address is not null ";

        // Build the select for a Context group
        if (contexts.length() > 0)
        {
            if (select.length() > 0)
                select += "union ";

            select += "select ua.name as lname from sbr.user_accounts_view ua, sbrext.user_contexts_view uc, sbr.contexts_view c where c.conte_idseq in ("
                + contexts.substring(2)
                + ") and uc.name = c.name and uc.privilege = 'W' and ua.ua_name = uc.ua_name and ua.alert_ind = 'Yes' and ua.electronic_mail_address is not null ";
        }

        String names = "";
        if (select.length() > 0)
        {
            // Sort the results.
            select = "select lname from (" + select + ") order by upper(lname) asc";

            try
            {
                // Retrieve the user names from the database.
                PreparedStatement pstmt = _conn.prepareStatement(select);
                ResultSet rs = pstmt.executeQuery();

                // Make this a comma separated list.
                while (rs.next())
                {
                    names += ", " + rs.getString(1);
                }
                rs.close();
                pstmt.close();
            }
            catch (SQLException ex)
            {
                _errorCode = DBAlertUtil.getSQLErrorCode(ex);
                _errorMsg = _errorCode + ": " + select
                    + "\n\n" + ex.toString();
                _logger.error(_errorMsg);
                return null;
            }
        }

        // Append the freeform email addresses.
        if (emails.length() > 0)
            names += emails;
        return (names.length() > 0) ? names.substring(2) : "(none)";
    }

    /**
     * Given the idseq of a Context, retrieve all the users with write access to
     * that context.
     *
     * @param conte_
     *        The context idseq.
     * @return The array of user ids with write access.
     */
    public String[] selectEmailsFromConte(String conte_)
    {
        String select = "select ua.electronic_mail_address "
            + "from sbrext.user_contexts_view uc, sbr.user_accounts_view ua, sbr.contexts_view c "
            + "where c.conte_idseq = ? and uc.name = c.name and uc.privilege = 'W' and ua.ua_name = uc.ua_name "
            + "and ua.alert_ind = 'Yes'";

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            if (conte_.charAt(0) == '/')
                pstmt.setString(1, conte_.substring(1));
            else
                pstmt.setString(1, conte_);
            ResultSet rs = pstmt.executeQuery();
            Vector<String> temp = new Vector<String>();
            while (rs.next())
            {
                temp.add(rs.getString(1));
            }
            rs.close();
            pstmt.close();

            String curators[] = new String[temp.size()];
            for (int ndx = 0; ndx < curators.length; ++ndx)
                curators[ndx] = (String) temp.get(ndx);
            return curators;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    /**
     * Given the id for a user, retrieve the email address.
     *
     * @param user_
     *        The user id.
     * @return The array of user ids with write access.
     */
    public String selectEmailFromUser(String user_)
    {
        String select = "select ua.electronic_mail_address "
            + "from sbr.user_accounts_view ua " + "where ua.ua_name = ?";

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, user_);
            ResultSet rs = pstmt.executeQuery();
            String temp = null;
            if (rs.next())
            {
                temp = rs.getString(1);
            }
            rs.close();
            pstmt.close();
            return temp;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = _errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(_errorMsg);
            return null;
        }
    }

    /**
     * Run a specific SELECT for the testDBdependancies() method.
     *
     * @param select_
     *        The select statement.
     * @return >0 if successful with the number of rows returned, otherwise
     *         failed.
     */
    private int testDB(String select_)
    {
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select_);
            ResultSet rs = pstmt.executeQuery();
            int rows;
            for (rows = 0; rs.next(); ++rows)
                ;
            rs.close();
            pstmt.close();
            return rows;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = select_ + "\n" + ex.toString();
            return -1;
        }
    }

    /**
     * Run a specific SELECT for the testDBdependancies() method.
     *
     * @param select_
     *        The select statement.
     * @return the first row found
     */
    private String testDB2(String select_)
    {
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select_);
            ResultSet rs = pstmt.executeQuery();
            String result = null;
            int rows;
            for (rows = 0; rs.next(); ++rows)
                result = rs.getString(1);
            rs.close();
            pstmt.close();
            return result;
        }
        catch (SQLException ex)
        {
            _errorCode = DBAlertUtil.getSQLErrorCode(ex);
            _errorMsg = select_ + "\n" + ex.toString();
            return null;
        }
    }

    /**
     * Test the database dependencies within this class. This method will check
     * the existence of tables, columns and required values.
     *
     * @return null if all dependencies are present, otherwise a string
     *         detailing those that failed.
     */
    public String testDBdependancies()
    {
        String results = "";
        String select = "select ua_name, name, electronic_mail_address, alert_ind "
            + "from sbr.user_accounts_view "
            + "where (ua_name is null or name is null or alert_ind is null) and rownum < 2";
        int rows = testDB(select);
        if (rows != 0)
        {
            if (rows < 0)
                results += _errorMsg;
            else
                results += "One of the columns ua_name, name or alert_ind in the table sbr.user_accounts_view is NULL";
            results += "\n\n";
        }

        select = "select * from sbrext.sn_alert_view_ext where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        select = "select * from sbrext.sn_query_view_ext where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        select = "select * from sbrext.sn_recipient_view_ext where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        select = "select * from sbrext.sn_report_view_ext where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        select = "select ua_name, name, privilege from sbrext.user_contexts_view where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        select = "select tool_name, property, ua_name, value from sbrext.tool_options_view_ext where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        _errorCode = 0;
        _errorMsg = "";
        return (results.length() == 0) ? null : results;
    }

    /**
     * Test the content of the tool options table.
     *
     * @param url_ the URL used to access the Sentinel from a browser. If not null it is compared to the caDSR
     *          tool options entry to ensure they match.
     * @return null if no errors, otherwise the error message.
     */
    public String testSentinelOptions(String url_)
    {
        String results = "";
        int rows;

        AutoProcessData apd = new AutoProcessData();
        apd.getOptions(this);
        if (apd._adminEmail == null || apd._adminEmail.length == 0)
            results += "Missing the Sentinel Tool Alert Administrators email address.\n\n";
        if (apd._adminIntro == null || apd._adminIntro.length() == 0)
            results += "Missing the Sentinel Tool email introduction.\n\n";
        if (apd._adminIntroError == null || apd._adminIntroError.length() == 0)
            results += "Missing the Sentinel Tool email error introduction.\n\n";
        if (apd._adminName == null || apd._adminName.length() == 0)
            results += "Missing the Sentinel Tool Alert Administrators email name.\n\n";
        if (apd._dbname == null || apd._dbname.length() == 0)
            results += "Missing the Sentinel Tool database name.\n\n";
        if (apd._emailAddr == null || apd._emailAddr.length() == 0)
            results += "Missing the Sentinel Tool email Reply To address.\n\n";
        if (apd._emailHost == null || apd._emailHost.length() == 0)
            results += "Missing the Sentinel Tool email host address.\n\n";
        if (apd._emailUser != null && apd._emailUser.length() > 0)
        {
            if (apd._emailPswd == null || apd._emailPswd.length() == 0)
                results += "Missing the Sentinel Tool email host account password.\n\n";
        }
        if (apd._http == null || apd._http.length() == 0)
            results += "Missing the Sentinel Tool HTTP prefix.\n\n";
        if (apd._subject == null || apd._subject.length() == 0)
            results += "Missing the Sentinel Tool email subject.\n\n";
        if (apd._work == null || apd._work.length() == 0)
            results += "Missing the Sentinel Tool working folder prefix.\n\n";

        String select = "select value from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' and property = 'URL' and value is not null";
        select = testDB2(select);
        if (select == null)
            results += "Missing the Sentinel Tool URL setting.\n\n";
        else if (url_ != null)
        {
            int pos = url_.indexOf('/', 8);
            if (pos > 0)
                url_ = url_.substring(0, pos);
            if (url_.startsWith("http://localhost"))
                ;
            else if (url_.startsWith("https://localhost"))
                ;
            else if (select.startsWith(url_, 0))
                ;
            else
                results += "Sentinel Tool URL \"" + url_ + "\"does not match configuration value \"" + select + "\".\n\n";
        }

        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property LIKE 'ADMIN.%' and value like '%0%'";
        rows = testDB(select);
        if (rows < 1)
            results += "Missing the Sentinel Tool Alert Administrator setting.\n\n";

        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property LIKE 'ADMIN.%' and value like '%1%'";
        rows = testDB(select);
        if (rows < 1)
            results += "Missing the Sentinel Tool Report Administrator setting.\n\n";

        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property = 'ALERT.NAME.FORMAT' and value is not null";
        rows = testDB(select);
        if (rows != 1)
            results += "Missing the Sentinel Tool ALERT.NAME.FORMAT setting.\n\n";

        if (selectAlertReportAdminEmails() == null)
            results += "Missing email addresses for the specified Alert Report Administrator(s) setting.\n\n";

        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property LIKE 'BROADCAST.EXCLUDE.CONTEXT.%.NAME'";
        rows = testDB(select);
        if (rows > 0)
        {
            int optcnt = rows;
            select = "select cov.name "
                + "from sbrext.tool_options_view_ext to1, sbrext.tool_options_view_ext to2, sbr.contexts_view cov "
                + "where to1.tool_name = 'SENTINEL' AND to2.tool_name = to1.tool_name "
                + "and to1.property LIKE 'BROADCAST.EXCLUDE.CONTEXT.%.NAME' "
                + "and to2.property LIKE 'BROADCAST.EXCLUDE.CONTEXT.%.CONTE_IDSEQ' "
                + "and SUBSTR(to1.property, 1, 29) = SUBSTR(to2.property, 1, 29) "
                + "and to1.value = cov.name "
                + "and to2.value = cov.conte_idseq";
            rows = testDB(select);
            if (rows != optcnt)
                results += "Missing or invalid BROADCAST.EXCLUDE.CONTEXT settings.\n\n";
        }

        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property like 'RSVD.CS.%'";
        rows = testDB(select);
        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property = 'RSVD.CSI.FORMAT' AND value like '%$ua_name$%'";
        rows += testDB(select);
        if (rows > 0)
        {
            if (rows == 3)
            {
                select = "select cs.long_name "
                    + "from sbrext.tool_options_view_ext to1, sbrext.tool_options_view_ext to2, sbr.classification_schemes_view cs "
                    + "where to1.tool_name = 'SENTINEL' AND to2.tool_name = to1.tool_name "
                    + "and to1.property = 'RSVD.CS.LONG_NAME' "
                    + "and to2.property = 'RSVD.CS.CS_IDSEQ' "
                    + "and to1.value = cs.long_name "
                    + "and to2.value = cs.cs_idseq";
                rows = testDB(select);
                if (rows != 1)
                    results += "Missing or invalid RSVD.CS settings.\n\n";
            }
            else
                results += "Missing or invalid RSVD.CS settings.\n\n";
        }

        _errorCode = 0;
        _errorMsg = "";
        return (results.length() == 0) ? null : results;
    }

    /**
     * Return the email addresses for all the administrators that should receive a log report.
     *
     * @return The list of email addresses.
     */
    public String[] selectAlertReportAdminEmails()
    {
        String select = "select ua.electronic_mail_address "
            + "from sbr.user_accounts_view ua, sbrext.tool_options_view_ext opt "
            + "where opt.tool_name = 'SENTINEL' and "
            + "opt.property like 'ADMIN.%' and "
            + "opt.value like '%1%' and ua.ua_name = opt.ua_name "
            + "and ua.electronic_mail_address is not null "
            + "order by opt.property";

        String[] list = getBasicData0(select);
        if (list != null)
            return list;

        // Fall back to the default.
        select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'EMAIL.ADDR'";

        return getBasicData0(select);
    }

    /**
     * Return the Alert Report email reply to address
     *
     * @return The reply to address.
     */
    public String selectAlertReportEmailAddr()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'EMAIL.ADDR'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : "";
    }

    /**
     * Return the email introduction for the Alert Report
     *
     * @return The introduction.
     */
    public String selectAlertReportEmailIntro()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'EMAIL.INTRO'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : "";
    }

    /**
     * Return the email error introduction for the Alert Report
     *
     * @return The error introduction.
     */
    public String selectAlertReportEmailError()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'EMAIL.ERROR'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : "";
    }

    /**
     * Return the email admin title which appears in the "From:" field.
     *
     * @return The admin title.
     */
    public String selectAlertReportAdminTitle()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'EMAIL.ADMIN.NAME'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : "";
    }

    /**
     * Return the email SMTP host.
     *
     * @return The email SMTP host.
     */
    public String selectAlertReportEmailHost()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'EMAIL.HOST'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : "";
    }

    /**
     * Return the email SMTP host user account.
     *
     * @return The email SMTP host user account.
     */
    public String selectAlertReportEmailHostUser()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'EMAIL.HOST.USER'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : "";
    }

    /**
     * Return the email SMTP host user account password.
     *
     * @return The email SMTP host user account password.
     */
    public String selectAlertReportEmailHostPswd()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'EMAIL.HOST.PSWD'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : "";
    }

    /**
     * Return the email subject.
     *
     * @return The email subject.
     */
    public String selectAlertReportEmailSubject()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'EMAIL.SUBJECT'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : "";
    }

    /**
     * Return the HTTP link prefix for all report output references.
     *
     * @return The HTTP link prefix
     */
    public String selectAlertReportHTTP()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'URL'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] + "/AlertReports/" : "";
    }

    /**
     * Return the HTTP link prefix for all Sentinel DTD files.
     *
     * @return The HTTP link prefix
     */
    public String selectDtdHTTP()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'URL'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] + "/dtd/" : "";
    }

    /**
     * Return the output directory for all generated files.
     *
     * @return The output directory prefix
     */
    public String selectAlertReportOutputDir()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'OUTPUT.DIR'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : "";
    }

    /**
     * Return the database name as it should appear on reports.
     *
     * @return The database name
     */
    public String selectAlertReportDBName()
    {
        String select = "select opt.value from sbrext.tool_options_view_ext opt where opt.tool_name = 'SENTINEL' and opt.property = 'DB.NAME'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : "";
    }

    /**
     * Return the email addresses for all the recipients of the statistic report.
     *
     * @return The list of email addresses.
     */
    public String[] selectStatReportEmails()
    {
        String select = "select ua.electronic_mail_address "
            + "from sbr.user_accounts_view ua, sbrext.tool_options_view_ext opt "
            + "where opt.tool_name = 'SENTINEL' and "
            + "opt.property like 'ADMIN.%' and "
            + "opt.value like '%2%' and ua.ua_name = opt.ua_name "
            + "and ua.electronic_mail_address is not null "
            + "order by opt.property";

        return getBasicData0(select);
    }

    /**
     * Return the EVS URL from the tool options.
     *
     * @return The EVS URL.
     */
    public String selectEvsUrl()
    {
        String select = "select value from sbrext.tool_options_view_ext where tool_name = 'EVS' and property = 'URL'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : null;
    }

    /**
     * Return the Alert Definition name format string.
     *
     * @return The list of email addresses.
     */
    public String selectAlertNameFormat()
    {
        String select = "select opt.value "
            + "from sbrext.tool_options_view_ext opt "
            + "where opt.tool_name = 'SENTINEL' and "
            + "opt.property = 'ALERT.NAME.FORMAT' ";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : null;
    }

    /**
     * Return the reserved CS id if the reserved CSI is passed to the method.
     *
     * @param idseq_ The CSI id to check.
     *
     * @return The reserved CS id or null if the CSI is not reserved.
     */
    public String selectCSfromReservedCSI(String idseq_)
    {
        String select = "select opt.value "
            + "from sbrext.tool_options_view_ext opt, sbr.classification_schemes_view cs, "
            + "sbr.cs_csi_view ci "
            + "where ci.csi_idseq = '" + idseq_ + "' and cs.cs_idseq = ci.cs_idseq and opt.value = cs.cs_idseq and "
            + "opt.tool_name = 'SENTINEL' and opt.property = 'RSVD.CS.CS_IDSEQ'";

        String[] list = getBasicData0(select);

        return (list != null) ? list[0] : null;
    }

    /**
     * Format the integer to include comma thousand separators.
     *
     * @param val_ The number in string format.
     * @return the number in string format with separators.
     */
    private String formatInt(String val_)
    {
        int loop = val_.length() / 3;
        int start = val_.length() % 3;
        String text = val_.substring(0, start);
        for (int i = 0; i < loop; ++i)
        {
            text = text + "," + val_.substring(start, start + 3);
            start += 3;
        }
        return (text.charAt(0) == ',') ? text.substring(1) : text;
    }

    /**
     * Retrieve the row counts for all the tables used by the Alert Report.
     * The values may be indexed using the _ACTYPE_* variables and an index
     * of _ACTYPE_LENGTH is the count of the change history table.
     *
     * @return The numbers for each table.
     */
    public String[] reportRowCounts()
    {
        String[] extraTables = {"sbrext.ac_change_history_ext", "sbrext.gs_tokens", "sbrext.gs_composite" };
        String[] extraNames = {"History Table", "Freestyle Token Index", "Freestyle Concatenation Index" };
        int total = _DBMAP3.length + extraTables.length;
        String counts[] = new String[total];

        String select = "select count(*) from ";
        String table;
        String name;

        int extraNdx = 0;
        for (int ndx = 0; ndx < counts.length; ++ndx)
        {
            if (ndx >= _DBMAP3.length)
            {
                table = extraTables[extraNdx];
                name = extraNames[extraNdx];
                ++extraNdx;
            }
            else if (_DBMAP3[ndx]._table == null)
            {
                counts[ndx] = null;
                continue;
            }
            else
            {
                table = _DBMAP3[ndx]._table;
                name = _DBMAP3[ndx]._val;
            }

            String temp = select + table;
            try
            {
                PreparedStatement pstmt = _conn.prepareStatement(temp);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next())
                {
                    counts[ndx] = name + AuditReport._ColSeparator + formatInt(rs.getString(1));
                }
                rs.close();
                pstmt.close();
            }
            catch (SQLException ex)
            {
                _errorCode = DBAlertUtil.getSQLErrorCode(ex);
                _errorMsg = temp + "\n" + ex.toString();
                counts[ndx] = name + ": " + _errorMsg;
            }
        }

        return counts;
    }

    /**
     * Translate the internal column names to something the user can easily
     * read.
     *
     * @param namespace_
     *        The scope of the namespace to lookup the val_.
     * @param val_
     *        The internal column name.
     * @return The translated value.
     */
    public String translateColumn(String namespace_, String val_)
    {
        // First search global name space as most are consistent.
        String rc = DBAlertUtil.binarySearchS(_DBMAP1, val_);
        if (rc == val_)
        {
            // We didn't find it in global now look in the specific name space.
            if (namespace_.compareTo("DESIGNATIONS") == 0)
                rc = DBAlertUtil.binarySearchS(_DBMAP1DESIG, val_);

            else if (namespace_.compareTo("REFERENCE_DOCUMENTS") == 0)
                rc = DBAlertUtil.binarySearchS(_DBMAP1RD, val_);

            else if (namespace_.compareTo("AC_CSI") == 0)
                rc = DBAlertUtil.binarySearchS(_DBMAP1CSI, val_);

            else if (namespace_.compareTo("COMPLEX_DATA_ELEMENTS") == 0)
                rc = DBAlertUtil.binarySearchS(_DBMAP1COMPLEX, val_);

            else
                rc = DBAlertUtil.binarySearchS(_DBMAP1OTHER, val_);
            return rc;
        }
        return rc;
    }

    /**
     * Translate the table names for the user.
     *
     * @param val_
     *        The internal table name.
     * @return The user readable name.
     */
    public String translateTable(String val_)
    {
        if (val_ == null)
            return "<null>";
        return DBAlertUtil.binarySearchS(_DBMAP3, val_);
    }

    /**
     * Look for the selection of a specific record type.
     *
     * @param val_ The AC type code.
     * @return false if the record type is not found.
     */
    public int isACTypeUsed(String val_)
    {
        return DBAlertUtil.binarySearch(_DBMAP3, val_);
    }

    /**
     * Test if the string table code represents the record type of interest.
     *
     * @param type_ One of the DBAlert._ACTYPE* constants.
     * @param tableCode_ The string type to test.
     * @return true if the type and string are equivalent.
     */
    public boolean isACType(int type_, String tableCode_)
    {
        return tableCode_.equals(_DBMAP3[type_]._key);
    }

    /**
     * Get the used (referenced) RELEASED object classes not owned by caBIG
     *
     * @return the list of object classes
     */
    public String[] reportUsedObjectClasses()
    {
        String cs1 = AuditReport._ColSeparator;
        String cs2 = " || '" + cs1 + "' || ";
        String select =
            "SELECT 'Name" + cs1 + "Public ID" + cs1 + "Version" + cs1 + "Workflow Status" + cs1
            + "Short Name" + cs1 + "Context" + cs1 + "Created" + cs1
            + "Modified" + cs1 + "References" + cs1 + "Order" + cs1
            + "Concept" + cs1 + "Code" + cs1 + "Origin' as title, ' ' AS lname, 0 AS dorder, ' ' AS ocidseq "
            + "from dual UNION ALL "
            + "SELECT  oc.long_name" + cs2 + "oc.oc_id" + cs2 + "oc.VERSION" + cs2 + "oc.asl_name" + cs2
            + "oc.preferred_name" + cs2 + "c.NAME" + cs2 + "oc.date_created" + cs2
            + "oc.date_modified" + cs2 + "ocset.cnt" + cs2 + "cc.display_order" + cs2
            + "con.long_name" + cs2 + "con.preferred_name" + cs2 + "con.origin as title, "
            + "LOWER (oc.long_name) AS lname, cc.display_order AS dorder, oc.oc_idseq AS ocidseq "
            + "FROM (SELECT   ocv.oc_idseq, COUNT (*) AS cnt "
            + "FROM sbrext.object_classes_view_ext ocv, "
            + "sbr.data_element_concepts_view DEC "
            + "WHERE ocv.asl_name NOT LIKE 'RETIRED%' "
            + "AND ocv.conte_idseq NOT IN ( "
            + "SELECT VALUE "
            + "FROM sbrext.tool_options_view_ext "
            + "WHERE tool_name = 'caDSR' "
            + "AND property = 'DEFAULT_CONTEXT') "
            + "AND DEC.oc_idseq = ocv.oc_idseq "
            + "AND DEC.asl_name NOT LIKE 'RETIRED%' "
            + "GROUP BY ocv.oc_idseq) ocset, "
            + "sbrext.object_classes_view_ext oc, "
            + "sbrext.component_concepts_view_ext cc, "
            + "sbrext.concepts_view_ext con, "
            + "sbr.contexts_view c "
            + "WHERE oc.oc_idseq = ocset.oc_idseq "
            + "AND c.conte_idseq = oc.conte_idseq "
            + "AND cc.condr_idseq = oc.condr_idseq "
            + "AND con.con_idseq = cc.con_idseq "
            + "ORDER BY lname ASC, ocidseq ASC, dorder DESC";

        return getBasicData0(select);
    }

    /**
     * Pull the name and email address for all the recipients on a specific Alert Definition.
     *
     * @param idseq_ the database id of the Alert Definition
     */
    public void selectAlertRecipients(String idseq_)
    {
        //TODO use this method to retrieve the name and emails for the distribution. It will
        // guarantee that the pair only appears once in the list. The method signature must be
        // changed to return the values.
        String select = "SELECT ua.NAME, ua.electronic_mail_address "
            + "FROM sbr.user_accounts_view ua "
            + "WHERE ua.ua_name IN ( "
            + "SELECT rc.ua_name "
            + "FROM sbrext.sn_report_view_ext rep, "
            + "sbrext.sn_recipient_view_ext rc "
            + "WHERE rep.al_idseq = '"+ idseq_ + "' "
            + "AND rc.rep_idseq = rep.rep_idseq "
            + "UNION "
            + "SELECT uc.ua_name "
            + "FROM sbrext.user_contexts_view uc, "
            + "sbr.contexts_view c, "
            + "sbrext.sn_report_view_ext rep, "
            + "sbrext.sn_recipient_view_ext rc "
            + "WHERE rep.al_idseq = '"+ idseq_ + "' "
            + "AND rc.rep_idseq = rep.rep_idseq "
            + "AND c.conte_idseq = rc.conte_idseq "
            + "AND uc.NAME = c.NAME "
            + "AND uc.PRIVILEGE = 'W') "
            + "AND ua.electronic_mail_address IS NOT NULL "
            + "UNION "
            + "SELECT rc.email, rc.email "
            + "FROM sbrext.sn_report_view_ext rep, sbrext.sn_recipient_view_ext rc "
            + "WHERE rep.al_idseq = '"+ idseq_ + "' "
            + "AND rc.rep_idseq = rep.rep_idseq "
            + "AND email IS NOT NULL";

        Results1 temp = getBasicData1(select, false);
    }

    /**
     * Retrieve the database Registration Authority Identifier (RAI)
     *
     * @return the server value
     */
    public String getDatabaseRAI()
    {
        String[] list = getBasicData0("select value from sbrext.tool_options_view_ext where tool_name = 'caDSR' and property = 'RAI'");
        return (list != null) ? list[0] : null;
    }

    /**
     * Convert all meaning full names back to the internal codes for the XML generation
     *
     * @param changes -
     *        array of names for changes
     * @return - array of the corresponding key values
     */
    @SuppressWarnings("unchecked")
    public String[] getKeyNames(String[] changes)
    {
        // Convert to list
        List<DBAlertOracleMap1> list = new ArrayList<DBAlertOracleMap1>(Arrays.asList(concat(_DBMAP1, _DBMAP1DESIG, _DBMAP1RD, _DBMAP1CSI, _DBMAP1COMPLEX,
            _DBMAP1OTHER)));

        // Ensure list sorted
        Collections.sort(list);

        // Convert it back to array as this is how the binary search is implemented
        DBAlertOracleMap1[] tempMap = list.toArray(new DBAlertOracleMap1[list.size()]);

        // Store the values into the new array and return the array
        String[] temp = new String[changes.length];
        for (int i = 0; i < changes.length; i++)
        {
            int rowID = DBAlertUtil.binarySearchValues(tempMap, changes[i]);
            temp[i] = tempMap[rowID]._key;
        }
        return temp; // To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Concatenate all map arrays
     *
     * @param maps the list of maps to concatenate
     * @return a single map
     */
    private DBAlertOracleMap1[] concat(DBAlertOracleMap1[] ... maps)
    {
        int total = 0;
        for (DBAlertOracleMap1[] map : maps)
        {
            total += map.length;
        }

        DBAlertOracleMap1[] concatMap = new DBAlertOracleMap1[total];

        total = 0;
        for (DBAlertOracleMap1[] map : maps)
        {
            System.arraycopy(map, 0, concatMap, total, map.length);
            total += map.length;
        }
        return concatMap;
    }
}
