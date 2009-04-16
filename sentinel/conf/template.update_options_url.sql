/* Copyright ScenPro, Inc, 2005

   $Header: /share/content/gforge/sentinel/sentinel/conf/template.update_options_url.sql,v 1.14 2009-04-16 21:46:44 hebell Exp $
   $Name: not supported by cvs2svn $

   Author: Larry Hebel

   This script updates the Tool Options table with URL's for the @TIER.NAME@ tier.
*/
whenever sqlerror exit sql.sqlcode rollback;

set scan off;

update sbrext.tool_options_view_ext
set value = REGEXP_REPLACE(value, 'cadsradmin[^/.]*\.nci\.nih\.gov', 'cadsradmintool@TIER@.nci.nih.gov')
where REGEXP_LIKE(value, 'cadsradmin[^/.]*\.nci\.nih\.gov');

update sbrext.tool_options_view_ext
set value = REGEXP_REPLACE(value, 'cdebrowser[^/.]*\.nci\.nih\.gov', 'cdebrowser@TIER@.nci.nih.gov')
where REGEXP_LIKE(value, 'cdebrowser[^/.]*\.nci\.nih\.gov');

update sbrext.tool_options_view_ext
set value = REGEXP_REPLACE(value, 'cadsrapi[^/.]*\.nci\.nih\.gov', 'cadsrapi@TIER@.nci.nih.gov')
where REGEXP_LIKE(value, 'cadsrapi[^/.]*\.nci\.nih\.gov');

update sbrext.tool_options_view_ext
set value = REGEXP_REPLACE(value, 'cadsrsentinel[^/.]*\.nci\.nih\.gov', 'cadsrsentinel@TIER@.nci.nih.gov')
where REGEXP_LIKE(value, 'cadsrsentinel[^/.]*\.nci\.nih\.gov');

update sbrext.tool_options_view_ext
set value = REGEXP_REPLACE(value, 'cdecurate[^/.]*\.nci\.nih\.gov', 'cdecurate@TIER@.nci.nih.gov')
where REGEXP_LIKE(value, 'cdecurate[^/.]*\.nci\.nih\.gov');

update sbrext.tool_options_view_ext
set value = REGEXP_REPLACE(value, 'formbuilder[^/.]*\.nci\.nih\.gov', 'formbuilder@TIER@.nci.nih.gov')
where REGEXP_LIKE(value, 'formbuilder[^/.]*\.nci\.nih\.gov');

update sbrext.tool_options_view_ext
set value = REGEXP_REPLACE(value, 'freestyle[^/.]*\.nci\.nih\.gov', 'freestyle@TIER@.nci.nih.gov')
where REGEXP_LIKE(value, 'freestyle[^/.]*\.nci\.nih\.gov');

update sbrext.tool_options_view_ext
set value = REGEXP_REPLACE(value, 'objcart[^/.]*\.nci\.nih\.gov', 'objcart@TIER@.nci.nih.gov')
where REGEXP_LIKE(value, 'objcart[^/.]*\.nci\.nih\.gov');

update sbrext.tool_options_view_ext
set value = REGEXP_REPLACE(value, 'ocbrowser[^/.]*\.nci\.nih\.gov', 'ocbrowser@TIER@.nci.nih.gov')
where REGEXP_LIKE(value, 'ocbrowser[^/.]*\.nci\.nih\.gov');

update sbrext.tool_options_view_ext
set value = REGEXP_REPLACE(value, 'umlmodelbrowser[^/.]*\.nci\.nih\.gov', 'umlmodelbrowser@TIER@.nci.nih.gov')
where REGEXP_LIKE(value, 'umlmodelbrowser[^/.]*\.nci\.nih\.gov');

update ui_metadata_view
set host = REGEXP_REPLACE(host, 'cadsradmin[^/.]*\.nci\.nih\.gov', 'cadsradmintool@TIER@.nci.nih.gov')
where REGEXP_LIKE(host, 'cadsradmin[^/.]*\.nci\.nih\.gov');

update ui_metadata_view
set host = REGEXP_REPLACE(host, 'cdebrowser[^/.]*\.nci\.nih\.gov', 'cdebrowser@TIER@.nci.nih.gov')
where REGEXP_LIKE(host, 'cdebrowser[^/.]*\.nci\.nih\.gov');

update ui_metadata_view
set host = REGEXP_REPLACE(host, 'cadsrapi[^/.]*\.nci\.nih\.gov', 'cadsrapi@TIER@.nci.nih.gov')
where REGEXP_LIKE(host, 'cadsrapi[^/.]*\.nci\.nih\.gov');

update ui_metadata_view
set host = REGEXP_REPLACE(host, 'cadsrsentinel[^/.]*\.nci\.nih\.gov', 'cadsrsentinel@TIER@.nci.nih.gov')
where REGEXP_LIKE(host, 'cadsrsentinel[^/.]*\.nci\.nih\.gov');

update ui_metadata_view
set host = REGEXP_REPLACE(host, 'cdecurate[^/.]*\.nci\.nih\.gov', 'cdecurate@TIER@.nci.nih.gov')
where REGEXP_LIKE(host, 'cdecurate[^/.]*\.nci\.nih\.gov');

update ui_metadata_view
set host = REGEXP_REPLACE(host, 'formbuilder[^/.]*\.nci\.nih\.gov', 'formbuilder@TIER@.nci.nih.gov')
where REGEXP_LIKE(host, 'formbuilder[^/.]*\.nci\.nih\.gov');

update ui_metadata_view
set host = REGEXP_REPLACE(host, 'freestyle[^/.]*\.nci\.nih\.gov', 'freestyle@TIER@.nci.nih.gov')
where REGEXP_LIKE(host, 'freestyle[^/.]*\.nci\.nih\.gov');

update ui_metadata_view
set host = REGEXP_REPLACE(host, 'objcart[^/.]*\.nci\.nih\.gov', 'objcart@TIER@.nci.nih.gov')
where REGEXP_LIKE(host, 'objcart[^/.]*\.nci\.nih\.gov');

update ui_metadata_view
set host = REGEXP_REPLACE(host, 'ocbrowser[^/.]*\.nci\.nih\.gov', 'ocbrowser@TIER@.nci.nih.gov')
where REGEXP_LIKE(host, 'ocbrowser[^/.]*\.nci\.nih\.gov');

update ui_metadata_view
set host = REGEXP_REPLACE(host, 'umlmodelbrowser[^/.]*\.nci\.nih\.gov', 'umlmodelbrowser@TIER@.nci.nih.gov')
where REGEXP_LIKE(host, 'umlmodelbrowser[^/.]*\.nci\.nih\.gov');
/*
   Commit Settings.
*/
commit;

exit
