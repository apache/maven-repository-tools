
############
# README.txt
############

# Explanation of files found within maven-meeper

crontab.txt has the crontab entries necessary to make everything run

##########################
# Maven 2 synchronizing
##########################

In ./synchronize/m2-sync

## Uses the java code in https://svn.apache.org/repos/asf/maven/archiva/tools/trunk/maven-meeper
java -jar maven-meeper-1-SNAPSHOT-jar-with-dependencies.jar sync.properties sync.csv

## sync.properties has the configuration options
## sync.csv has the list of repositorios that get synced

####################################################
# Upload bundles from the Maven JIRA to a repository
####################################################

## See: http://maven.apache.org/guides/mini/guide-ibiblio-upload.html
## Pulls a bundle down via wget, deploys jar, pom, license file, java-sources, javadocs
./bundle-upload/deploy-bundle

##############################################
# Sync to primary mirrors
##############################################

## rsyncs the central m2 repository over to the cica.es mirror
./sync-central-to-cica.sh synchronize.properties

## rsyncs the central m2 repository over to the ibiblio mirror
./sync-central-to-ibiblio.sh synchronize.properties


##################################################
# Maven 1
##################################################

## all in one script
./synchronize-m1.sh
## and to update the rewrite rules
./synchronize-rewrite-rules-to-ibiblio.sh synchronize.properties

## Lately we just need to sync and convert to M2 some Maven 1 groups on demand, using
./synchronize-m1-apache.sh groupId

##################################################
# M1 synchronize from upstream repositories script
##################################################
## INI configuration files
./synchronize/syncopate/conf/apache.conf
./synchronize/syncopate/conf/codehaus.conf
./synchronize/syncopate/conf/maven-plugins-sf.conf
./synchronize/syncopate/conf/mortbay.conf
./synchronize/syncopate/conf/objectweb.conf
./synchronize/syncopate/conf/opensymphony.conf
./synchronize/syncopate/conf/osjava.conf
./synchronize/syncopate/conf/stage.conf
./synchronize/syncopate/conf/test.conf

## standard exclusions. you can add to it in the configuration files
./synchronize/syncopate/exclusions.txt

## INI perl parser - pulled down from CPAN etc
./synchronize/syncopate/IniFiles.pm

## The perl synchronization file
./synchronize/syncopate/sync

## top level configuration
./synchronize/syncopate/syncopate.conf

##############################################
# Convert an m1 repository to an m2 repository
##############################################

## Uses http://svn.apache.org/repos/asf/maven/sandbox/repoclean/src/main/bash/repoclean.sh
## which runs Java code to do the bulk of the work.

./m1-m2-conversion/sync-repoclean.sh

## configuration for the above
./m1-m2-conversion/synchronize.properties

## Maven 1 -> Maven 2 mod-rewriting
./synchronize/m1-m2-mod-rewrite-rules.txt

###############################
# DELETE THESE IF INDEED UNUSED
###############################
./unused/artifact-map.txt
./unused/rewrite.conf
