/*L
  Copyright ScenPro Inc, SAIC-F

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
L*/

/* Copyright 2007, ScenPro, Inc

    $Header: /share/content/gforge/sentinel/sentinel/db-sql/recipients.sql,v 1.1 2007-07-19 15:26:46 hebell Exp $
    $Name: not supported by cvs2svn $
    
    This script will return all recipients of Sentinel reports for which a User Name or Email address
    is used. It does NOT include Context Curator Groups
*/
(select ua.electronic_mail_address as ename
from sbr.user_accounts_view ua, sbrext.sn_recipient_view_ext sr, sbrext.sn_report_view_ext rp, sbrext.sn_alert_view_ext al
where al.al_status <> 'I' and rp.al_idseq = al.al_idseq and sr.rep_idseq = rp.rep_idseq and ua.ua_name = sr.ua_name and ua.electronic_mail_address is not null
union
select sr.email as ename
from sbrext.sn_recipient_view_ext sr
where sr.email is not null)
order by ename
