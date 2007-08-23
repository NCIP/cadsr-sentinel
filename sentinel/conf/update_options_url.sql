/* Copyright ScenPro, Inc, 2005

   $Header: /share/content/gforge/sentinel/sentinel/conf/update_options_url.sql,v 1.1 2007-07-19 15:26:46 hebell Exp $
   $Name: not supported by cvs2svn $

   Author: Larry Hebel

   This script updates the Tool Options table with URL's for the Sandbox tier.
*/
set scan off;

delete from sbrext.tool_options_view_ext where value like '%http://%';

insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('BROWSER', 'URL', 'http://cdebrowser@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'ADMIN_TOOL_URL', 'http://cadsradmin@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'CURATION_TOOL_URL', 'http://cdecurate@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'GO_CODE', 'http://nciterms.nci.nih.gov:80/NCIBrowser/ConceptReport.jsp?dictionary=GO&code=', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'LOINC_CODE', 'http://nciterms.nci.nih.gov:80/NCIBrowser/ConceptReport.jsp?dictionary=LOINC&code=', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'MEDDRA_CODE', 'http://nciterms.nci.nih.gov:80/NCIBrowser/ConceptReport.jsp?dictionary=MedDRA&licensetag=true&code=', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'NCI_CONCEPT_CODE', 'http://nciterms.nci.nih.gov:80/NCIBrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&code=', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'NCI_METATHESAURUS_URL', 'http://ncimeta.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'NCI_META_CUI', 'http://ncimeta.nci.nih.gov/MetaServlet/ResultServlet?cui=', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'NCI_MO_CODE', 'http://nciterms.nci.nih.gov:80/NCIBrowser/ConceptReport.jsp?dictionary=MGED_Ontology&code=', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'NCI_TERMINOLOGY_SERVER_URL', 'http://nciterms.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'SENTINAL_API_URL', 'http://cadsrsentinel@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'SENTINEL_TOOL_URL', 'http://cadsrsentinel@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'UMLBROWSER_URL', 'http://umlmodelbrowser@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'UMLS_CUI', 'http://ncimeta.nci.nih.gov/MetaServlet/ResultServlet?cui=', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'URL', 'http://cdebrowser@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'UWD_VA_CODE', 'http://nciterms.nci.nih.gov:80/NCIBrowser/ConceptReport.jsp?dictionary=UWD_Visual_Anatomist&code=', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CDEBrowser', 'VA_NDF_CODE', 'http://nciterms.nci.nih.gov:80/NCIBrowser/ConceptReport.jsp?dictionary=VA_NDFRT&code=', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CURATION', 'REFDOC_FILEURL', 'http://cdecurate@tier@.nci.nih.gov/filecache/', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('CURATION', 'URL', 'http://cdecurate@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('EVS', 'URL', 'http://cabio.nci.nih.gov/cacore32/http/remoteService', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('FREESTYLE', 'URL', 'http://freestyle@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('SENTINEL', 'LINK.HTTP', 'http://cadsrsentinel@tier@.nci.nih.gov/AlertReports/', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('SENTINEL', 'URL', 'http://cadsrsentinel@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('UMLBrowser', 'ADMIN_TOOL_URL', 'http://cadsradmin@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('UMLBrowser', 'CACORE_URL', 'http://cabio.nci.nih.gov/cacore32/http/remoteService', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('UMLBrowser', 'CDEBROWSER_TOOL_URL', 'http://cdebrowser@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('UMLBrowser', 'CDEBROWSER_URL', 'http://cdebrowser@tier@.nci.nih.gov/CDEBrowser/', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('UMLBrowser', 'CURATION_TOOL_URL', 'http://cdecurate@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('UMLBrowser', 'NCI_METATHESAURUS_URL', 'http://ncimeta.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('UMLBrowser', 'NCI_TERMINOLOGY_SERVER_URL', 'http://nciterms.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('UMLBrowser', 'SENTINEL_TOOL_URL', 'http://cadsrsentinel@tier@.nci.nih.gov', 'US');
insert into sbrext.tool_options_view_ext (tool_name, property, value, locale) VALUES ('UMLBrowser', 'URL', 'http://umlmodelbrowser@tier@.nci.nih.gov', 'US');

/*
   Commit Settings.
*/
commit;