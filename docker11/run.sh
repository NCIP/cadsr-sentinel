#~/bin/sh
echo "we are in the /usr/src/sentinel/cadsr-sentinel directory"
echo "tag: " $tag
git pull
if [ $tag != 'origin/master'  ] && [ $tag != 'master' ]; then
#  git checkout tags/$tag
#this is for branch checkout for now
	git checkout $tag
fi
git pull

# Function to check if wildfly is up #
function wait_for_server() {
  until `/opt/wildfly/bin/jboss-cli.sh -c --controller=localhost:19990 ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do
    sleep 1
  done
}

cd software
echo "we are in the /usr/src/sentinel/cadsr-sentinel/software directory"
echo "=> build application and copy artifacts to /local/content/sentinel"

echo ant -file build.xml -Ddebug=false -DBRANCH_OR_TAG=${BRANCH_OR_TAG} -Dtiername=${tiername} -DCADSR.DS.USER=${CADSR_DS_USER} -DCADSR.DS.PSWD=******** -DCADSR.DS.TNS.HOST=${CADSR_DS_TNS_HOST} -DTOOL.BASE.DIR=${TOOL_BASE_DIR} -DTEST=false -DCADSR.DS.TNS.SID=${CADSR_DS_TNS_SID} -DJDEBUG=off -DCADSR.DS.TNS.PORT=${CADSR_DS_TNS_PORT} build-product
ant -file build.xml -Ddebug=false -DBRANCH_OR_TAG=${BRANCH_OR_TAG} -Dtiername=${tiername} -DCADSR.DS.USER=${CADSR_DS_USER} -DCADSR.DS.PSWD=${CADSR_DS_PSWD} -DCADSR.DS.TNS.HOST=${CADSR_DS_TNS_HOST} -DTOOL.BASE.DIR=${TOOL_BASE_DIR} -DTEST=false -DCADSR.DS.TNS.SID=${CADSR_DS_TNS_SID} -DJDEBUG=off -DCADSR.DS.TNS.PORT=${CADSR_DS_TNS_PORT} build-product

cp deployment-artifacts/cadsrsentinel.war /local/content/cadsrsentinel/bin
cp lib/ojdbc7-12.1.0.2.0.jar /local/content/cadsrsentinel/bin

echo "=> starting wildfly in background"
/opt/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &

echo "=> Waiting for the server to boot"
wait_for_server

echo "=> deploying modules"
/opt/wildfly/bin/jboss-cli.sh --file=deployment-artifacts/jboss/cadsrsentinel_modules.cli

echo "=> reloading wildfly"
/opt/wildfly/bin/jboss-cli.sh --connect controller=localhost:19990 command=:reload

echo "=> Waiting for the server to reload"
wait_for_server

echo "=> shutting wildfly down"
/opt/wildfly/bin/jboss-cli.sh --connect controller=localhost:19990 command=:shutdown

echo "=> starting wildfly in foreground"
/opt/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 
