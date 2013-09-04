/*L
  Copyright ScenPro Inc, SAIC-F

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
L*/

/* Copyright ScenPro, Inc, 2005

   $Header: /share/content/gforge/sentinel/sentinel/conf/template.load_options.sql,v 1.24 2009-06-08 20:38:41 hebell Exp $
   $Name: not supported by cvs2svn $

   Author: Larry Hebel

   This script loads the Tool Options table with required and optional values
   for the Sentinel Tool.

   Each is described briefly below. A full description of each can be found in
   the Sentinel Tool Installation Guide (file:
   distrib/doc/Installation Guide.doc). These values must be reviewed and
   changed as needed per the local installation and database instance.  If
   required values are missing or something is miscoded or invalid appropriate
   error messages are displayed via the Sentinel Tool Login page.

    WHEN DEPLOYING AT NCI ON THE DEV TIER THE sentinel/db-sql/fix_dev_idseq.sql MUST BE
    RUN AFTER THIS SCRIPT!!!
*/
whenever sqlerror exit sql.sqlcode rollback;

delete from sbrext.tool_options_view_ext where tool_name = 'SENTINEL';

/*
  ==============================================================================
  Required Settings (do not comment or remove)
  ==============================================================================

  The Reserved Classification Scheme is used as the parent CS for account
  specific Scheme Items. This provides an arbitrary collection of Administered
  Components which an Alert can monitor.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'RSVD.CS.LONG_NAME', 'RESERVED FOR SENTINEL MONITOR',
'The reserved classification scheme long name which must match the CS IDSEQ to pass validation.');

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'RSVD.CS.CS_IDSEQ', '0FC32A7D-67F5-4259-E044-0003BA3F9857',
'The reserved classification scheme database id which must match the CS LONG_NAME to pass validation.');

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'RSVD.CSI.FORMAT', 'Monitor for $ua_name$',
'The reserved scheme item name format string used to identify the CSI for a specific user.');

/*
   When the Sentinel has to create a name for an Alert Definition, it uses the following
   pattern. $ac_type$ is a macro for the type of object requested to monitor, $ac_name$ is
   the name of the object requested, $ua_name$ is the user account id.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'ALERT.NAME.FORMAT', '$ac_name$',
'The format for system generated Alert Definition Names.');

/*
   There may be any number of Administrators but there must be at least one of
   each type. The value column defines the type of Administrator and is fully
   documented in the Installation Guide.
*/

MERGE INTO sbrext.tool_options_view_ext s
   USING (SELECT hits.tool_name, hits.property, hits.VALUE, hits.ua_name, hits.description
            FROM (SELECT 'SENTINEL' AS tool_name, 'ADMIN.00' AS property, '01' AS VALUE, 'HEBELL' AS ua_name, 'An account given full Sentinel Administrator privileges.' AS description, 'ALL' AS tier FROM DUAL
                  UNION
                  SELECT 'SENTINEL', 'ADMIN.01', '2', 'AZIEN', 'An account to receive the caDSR Audit Report.', 'ALL' FROM DUAL
                  UNION
                  SELECT 'SENTINEL', 'ADMIN.02', '2', 'DWARZEL', 'An account to receive the caDSR Audit Report.', 'PROD' FROM DUAL
                  UNION
                  SELECT 'SENTINEL', 'ADMIN.02', '2', 'DWARZEL', 'An account to receive the caDSR Audit Report.', 'STAGE' FROM DUAL
                  UNION
                  SELECT 'SENTINEL', 'ADMIN.02', '2', 'DWARZEL', 'An account to receive the caDSR Audit Report.', 'SANDBOX' FROM DUAL
                  UNION
                  SELECT 'SENTINEL', 'ADMIN.03', '2', 'REEVESD', 'An account to receive the caDSR Audit Report.', 'PROD' FROM DUAL
                  UNION
                  SELECT 'SENTINEL', 'ADMIN.04', '2', 'HAUD', 'An account to receive the caDSR Audit Report.', 'PROD' FROM DUAL
                  UNION
                  SELECT 'SENTINEL', 'ADMIN.05', '2', 'CURTIST', 'An account to receive the caDSR Audit Report.', 'PROD' FROM DUAL
                  UNION
                  SELECT 'SENTINEL', 'ADMIN.06', '01', 'DAVET', 'An account given full Sentinel Administrator privileges.', 'ALL' FROM DUAL
                  ) hits
           WHERE hits.tier IN ('ALL', '@TIER.UPPER@')) t
   ON (s.tool_name = t.tool_name AND s.property = t.property)
   WHEN MATCHED THEN
      UPDATE
         SET s.VALUE = t.VALUE, s.ua_name = t.ua_name
   WHEN NOT MATCHED THEN
      INSERT (tool_name, property, VALUE, ua_name, description)
      VALUES (t.tool_name, t.property, t.VALUE, t.ua_name, t.description);

/*
   The URL should be retrieved and passed to the Sentinel API
   DSRAlertAPI.factory() method. By placing this value in the database, the
   URL may be dynamically changed as needed without the need to build and
   deploy new WAR and JAR files.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'URL', 'https://cadsrsentinel@TIER@.nci.nih.gov',
'Deprecated, please use URN.');

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'URN', 'https://cadsrsentinel@TIER@.nci.nih.gov/cadsrsentinel/',
'The URN for the Sentinel Tool referencing this caDSR database.');

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'VERSION', '@appl.version@',
'The version identification for the current Sentinel Tool.');

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'NAME', 'caDSR Sentinel Tool',
'The common display name for the tool.');

/*
   The report threshold controls when a single report is broken into multiple
   reports to ensure the Browser can handle the volume of data. When the limit
   is reached, the report will begin with the closest primary change prior to
   the limit row.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'REPORT.THRESHOLD.LIMIT', '500',
'The maximum number of rows permitted in a report.');

/*
    Report Generation

    The Alert Administrators email address is used to receive logs and as the
    "Reply To" address on all Alert distributions.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'EMAIL.ADDR', 'sentinel@scenpro.com',
'The Alert Report Administrators email address.');

/*
    Report Generation

    The Alert Administrators Name is the text that will appear in the "From" of
    all Alert distributions.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'EMAIL.ADMIN.NAME', 'caDSR Alert @TIER.UPPER@ Administrator',
'The Alert Report Administrators name/title.');

/*
    Report Generation

    The email host is the identification of the SMTP server.  This can be an IP
    address or DNS lookup.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'EMAIL.HOST', 'mailfwd.nih.gov',
'The email host SMTP server.');

/*
    Report Generation

    The email user name to connect to the SMTP server. (Uncomment as needed by
    the email host above.)

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'EMAIL.HOST.USER', '',
'The email user name to connect to the SMTP server.');
*/

/*
    Report Generation

    The email password to connect to the SMTP server. (Uncomment as needed by
    the email user above.)

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'EMAIL.HOST.PSWD', '',
'The email password to connect to the SMTP server.');
*/

/*
    Report Generation

    This is the subject line used as a prefix in all distribution emails.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'EMAIL.SUBJECT', 'caDSR Alert Report',
'This is the subject line used as a prefix in all distribution emails.');

/*
    Report Generation

    This is the subject line used as a prefix in all distribution emails. The value column
    is NULL as it is too short to hold the amount of text required.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'EMAIL.INTRO', 'Following are the links to the most recent caDSR Alert Activity Reports. ' ||
    'Please select the link or copy and paste the URL into your browser.  If you have problems accessing these files, ' ||
    'please reply to this email and the Alert Administrator will assist you.' || chr(10) || chr(10) ||
    'If you wish to save a local copy of the ' ||
    'report, after opening the report in your browser select File, Save As from the menu and be sure to use a ' ||
    'meaningful location and file name.  Also be sure to use the file extension ".html" as this tells the operating ' ||
    'system it should be opened for viewing using a browser.',
    'The report email body introduction.');

/*
    Report Generation

    This is the subject line used as a prefix in all distribution emails. The value column
    is NULL as it is too short to hold the amount of text required.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'EMAIL.ERROR', 'One or more errors occurred generating the activity ' ||
    'reports as noted below.  Contact the Alert Administrator to determine the ' ||
    'cause. You may attempt to Run the Alert Definition manually, however ' ||
    'until the cause for the error is determined this may also fail.',
    'The error report email body additional text.');

/*
    Report Generation

    The directory prefix for all file output.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'OUTPUT.DIR', '/local/content/cadsrsentinel/reports/',
'The directory prefix for all file output.');

/*
    Report Generation

    The name to identify this database on report output.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'DB.NAME', 'NCICB @TIER.NAME@ caDSR',
'The name to identify this database on report output.');

/*
  Online Help URL

   The URL hosting the Sentinel Tool online Help. This must include the full URI and file name for the first page.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'HELP.ROOT', 'https://cadsrsentinel@TIER@.nci.nih.gov/help/', 'The Sentinel Tool online help URL root');
insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'HELP.HOME', 'index.html', 'The Sentinel Tool online help main page.');
insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'HELP.RUN', 'index.html' || chr(63) || 'context=caDSR_Sentinel_Tool_Help' || chr(38) || 'topic=manual_run', 'The Run page help');

/*
  ==============================================================================
  Optional Settings (may be commented or deleted)
  ==============================================================================

   The Broadcast Exclude Context controls the content of the Edit, Report
   Details, Recipient Context Group list. All Contexts appear in the list
   unless excluded using this setting.
*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'BROADCAST.EXCLUDE.CONTEXT.00.NAME', 'TEST',
'The context name to be excluded from the Alert Report Recipients list. This must match the IDSEQ to pass validation.');

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('SENTINEL', 'BROADCAST.EXCLUDE.CONTEXT.00.CONTE_IDSEQ', '29A8FB18-0AB1-11D6-A42F-0010A4C1E842',
'The context database id to be excluded from the Alert Report Recipients list. This must match the NAME to pass validation.');

/*
   Commit Settings.
*/

commit;

exit
