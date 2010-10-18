#!/bin/sh

# ------------------------------------------------------------------------
# 1. Sync Maven 1.x Apache repository to central, just for one groupId
# 2. Convert Maven 1.x repository to Maven 2.x repository
# 3. Manual fixes
# ------------------------------------------------------------------------

# check if script is already running
PID=$$
RUNNING=`ps -ef | grep synchronize.sh | grep -v 'sh -c' | grep -v grep | grep -v $PID`


dir=`pwd`
syncProperties=$dir/synchronize.properties
source $syncProperties

((

MODE=$2
GROUPID=$1

[ "${GROUPID}" = "" ] && echo && echo "You must specify the groupId!" && echo && exit

echo "Using the following settings:"
echo "CENTRAL_HOST = $CENTRAL_HOST"
echo "TOOLS_BASE = $TOOLS_BASE"
echo "SYNC_TOOLS = $SYNC_TOOLS"
echo "SYNCOPATE = $SYNCOPATE"
echo "REPOCLEAN = $REPOCLEAN"
echo "M1_M2_REWRITE_RULES = $M1_M2_REWRITE_RULES"
echo "SYNC_REPORTS = $SYNC_REPORTS"
echo "JAVA = $JAVA"

# exit if already running
echo $RUNNING
if [ ! -z "$RUNNING" ]; then
  date >> $SYNC_REPORTS/synchronize.log
  echo Sync already running... exiting >> $SYNC_REPORTS/synchronize.log
  echo $RUNNING >> $SYNC_REPORTS/synchronize.log
  exit 1
fi


[ "$MODE" = "batch" ] && echo && echo "Press any key to continue, or hit ^C to quit." && echo

# ------------------------------------------------------------------------
# Syncopate: Sync the Maven 1.x repositories 
# ------------------------------------------------------------------------

[ "$MODE" = "batch" ] && echo && echo "Press any key to run syncopate, or hit ^C to quit." && echo

echo ">>>>>>>>>>>>>>>>>> Running sync for group " $GROUPID

(
#  cd $SYNCOPATE
#  ./sync
  mkdir $STAGING_DIR/../maven1-sync/$GROUPID/;
  rsync --ignore-existing --exclude-from=$SYNCOPATE/exclusions.txt -Lrtivz "--rsh=ssh " jvanzyl@people.apache.org:/www/people.apache.org/repo/m1-ibiblio-rsync-repository/$GROUPID/ $STAGING_DIR/../maven1-sync/$GROUPID/
  retval=$?; if [ $retval != 0 ]; then exit $retval; fi
)
retval=$?; if [ $retval != 0 ]; then exit $retval; fi

# ------------------------------------------------------------------------
# Repoclean: converting the Maven 1.x repository to Maven 2.x 
# ------------------------------------------------------------------------

[ "$MODE" = "batch" ] && echo && echo "Press any key to run the m1 to m2 conversion, or hit ^C to quit." && echo

echo ">>>>>>>>>>>>>>>>>> Running Maven 1.x to Maven 2.x conversion ..."

(
  cd $REPOCLEAN
  ./convert-m1-m2.sh $syncProperties
  retval=$?; if [ $retval != 0 ]; then exit $retval; fi
)
retval=$?; if [ $retval != 0 ]; then exit $retval; fi

# ------------------------------------------------------------------------
# Manual fixes
# ------------------------------------------------------------------------

[ "$MODE" = "batch" ] && echo && echo "Press any key to run manual fixes, or hit ^C to quit." && echo

echo ">>>>>>>>>>>>>>>>>> Removing commons-logging 1.1-dev"

# hack prevent commons-logging-1.1-dev
CL=$MAVEN2_REPO/commons-logging/commons-logging
rm -rf $CL/1.1-dev
grep -v 1.1-dev $CL/maven-metadata.xml > $CL/maven-metadata.xml.tmp
mv $CL/maven-metadata.xml.tmp $CL/maven-metadata.xml
md5sum $CL/maven-metadata.xml > $CL/maven-metadata.xml.md5
sha1sum $CL/maven-metadata.xml > $CL/maven-metadata.xml.sha1

) | tee $SYNC_REPORTS/last-sync-results.txt ) 2>&1