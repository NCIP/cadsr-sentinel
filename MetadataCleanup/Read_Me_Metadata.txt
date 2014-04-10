1. Metadata cleanup task has a script(runScript.sh) that initiates the metadata cleanup process.
2. The script accepts three parameters one each for database URL, Username and Password.
3. The output directory for the reports is a property under metadata.properties file.
4. Use the DSURL,username and password from cadsrsentinel.xml for the respective tier(ncidb-dsr-d.nci.nih.gov:1551:dsrdev.NCI.NIH.GOV for DEV) for the database properties.
5. Usage ./runScript.sh DSURL DSUsername DSPassword