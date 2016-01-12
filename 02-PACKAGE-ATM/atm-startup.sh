#! /bin/bash

# ATM run server
cd /tmp/ATM-SERVER
for lib in WEB-INF/lib/*.jar; do CLASSPATH=${CLASSPATH}:${lib} ; done
echo -- launching ATM SERVER : java -cp $CLASSPATH org.bluebank.Application
nohup java -cp $CLASSPATH org.bluebank.Application &

# ATM client run
cd node-client
echo -- launching ATM CLIENT : npm start
npm start
