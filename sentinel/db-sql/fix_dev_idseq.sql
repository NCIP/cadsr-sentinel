/* Copyright ScenPro, Inc, 2007

   $Header: /share/content/gforge/sentinel/sentinel/db-sql/fix_dev_idseq.sql,v 1.1 2007-07-19 15:26:46 hebell Exp $
   $Name: not supported by cvs2svn $

   Author: Larry Hebel

   This script fixes the CS_IDSEQ for the DEV tier because it is different than all the others.

*/

update sbrext.tool_options_view_ext set value = '0734BFB3-5969-4B9B-E044-0003BA0B1A09' where tool_name = 'SENTINEL' and property = 'RSVD.CS.CS_IDSEQ';

commit;