#~/bin/sh
echo "we are in the /usr/src/sentinel/cadsr-sentinel directory"
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

echo "=> build application and copy artifacts to /local/content/sentinel"
ant -file build.xml build-product

cp deployment-artifacts/cadsrsentinel.war /local/content/cadsrsentinel/bin

echo "=> starting wildfly in background"
/opt/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &

echo "=> Waiting for the server to boot"
wait_for_server

echo "=> deploying modules"
/opt/wildfly/bin/jboss-cli.sh --file=cadsrsentinel_modules11.cli

echo "=> reloading wildfly"
/opt/wildfly/bin/jboss-cli.sh --connect controller=localhost:19990 command=:reload

echo "=> Waiting for the server to reload"
wait_for_server

echo "=> deploying"
/opt/wildfly/bin/jboss-cli.sh --file=cadsrsentinel_modules11.cli
/opt/wildfly/bin/jboss-cli.sh --file=cadsrsentinel_setup_deploy.cli

echo "=> shutting wildfly down"
/opt/wildfly/bin/jboss-cli.sh --connect controller=localhost:19990 command=:shutdown

echo "=> starting wildfly in foreground"
/opt/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 
