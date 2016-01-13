#! /bin/bash

# ATM run server
cd /tmp/ATM-SERVER/lib
runningJar=$(ls bluebank-atm-*.jar)
echo -- launching ATM SERVER : java -cp $CLASSPATH org.bluebank.Application
java -jar ${runningJar} org.bluebank.Application
