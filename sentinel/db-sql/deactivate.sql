/* Copyright 2007, ScenPro, Inc.

    $Header: /share/content/gforge/sentinel/sentinel/db-sql/deactivate.sql,v 1.2 2007-09-25 14:26:46 hebell Exp $
    $Name: not supported by cvs2svn $

    This script will set all Sentinel Alerts to INACTIVE except for those users listed.
*/
whenever sqlerror exit sql.sqlcode rollback;

update sbrext.sn_alert_view_ext set al_status = 'I', status_reason = 'Set by Alert Administrator'
where al_status <> 'I' and created_by <> 'HEBELL';

commit;
