/*L
  Copyright ScenPro Inc, SAIC-F

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
L*/

/* Copyright ScenPro, Inc, 2007

   $Header: /share/content/gforge/sentinel/sentinel/db-sql/fix_dev_idseq.sql,v 1.3 2007-12-06 20:52:07 hebell Exp $
   $Name: not supported by cvs2svn $

   Author: Larry Hebel

   This script fixes the CS_IDSEQ for the DEV tier because it is different than all the others. This is not necessary once DEV is refreshed with Production data.

*/
whenever sqlerror exit sql.sqlcode rollback;

update sbrext.tool_options_view_ext set value = '0734BFB3-5969-4B9B-E044-0003BA0B1A09' where tool_name = 'SENTINEL' and property = 'RSVD.CS.CS_IDSEQ';

commit;