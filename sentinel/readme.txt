*** Setup ***

1. To properly access the database, please change the password in template.build.properties
and make sure that it is properly populated in cadsrsentinel.xml.

2. For dev, change TOOL.BASE.DIR in local.build.properties too. Create the directory manually if you have to
e.g. mkdir -p \local\content\cadsrsentinel\reports or sudo mkdir -p /local/content/cadsrsentinel/reports.

3. To generate the report, please make sure TOOL.BASE.DIR is set properly in [tier].property

*** Build ***

To build the cron job:

ant or ant build-product

To build and deploy JBoss app:

ant deploy-local

To access the localhost URL:

http://localhost:8080/cadsrsentinel/do/logon

and

http://localhost:8080/AlertReports/