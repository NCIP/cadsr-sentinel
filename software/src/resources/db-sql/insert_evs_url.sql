/*
Run the below statement for a new environment/DB only.
*/
INSERT INTO "SBREXT"."TOOL_OPTIONS_EXT" (TOOL_NAME, PROPERTY, VALUE, DATE_CREATED, CREATED_BY, DESCRIPTION, LOCALE) VALUES ('SENTINEL', 'LexEVSAPI-URL', 'https://lexevsapi65.nci.nih.gov/lexevsapi65', sysdate, 'SBREXT', 'The URL for EVS API access used in Sentinel.', 'US')
/
select tool_name, property, VALUE from sbrext.tool_options_view_ext where Tool_name = 'SENTINEL' and Property = 'LexEVSAPI-URL'
/
commit
/
