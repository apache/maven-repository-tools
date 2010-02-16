#!/bin/bash
cd /home/maven/bin/synchronize/m2-sync
#svn up sync.csv
/opt/java/sdk/current/bin/java -jar maven-meeper-1-SNAPSHOT-jar-with-dependencies.jar sync.properties sync.csv
date > /home/maven/repository-staging/to-ibiblio/maven2/last_updated.txt
