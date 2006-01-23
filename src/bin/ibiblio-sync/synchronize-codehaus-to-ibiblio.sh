#!/bin/bash

dest=/home/projects/maven/repository-staging/to-ibiblio

# NO MORE m1 SYNC

#date > $dest/maven/last-sync.txt
#chmod a+r $dest/maven/last-sync.txt
#rsync -e ssh --delete --max-delete=10 -v -rplt $dest/maven/ login.ibiblio.org:/public/html/maven

# EXCEPT FOR PLUGINS
# TODO - FIX
rsync -e ssh --delete --max-delete=10 -v -rplt $dest/maven/ login.ibiblio.org:/public/html/maven
#(
  #cd $dest/maven
  #rsync -n -e ssh --delete --max-delete=10 -v -rplt --include='*/plugins/*' ./ login.ibiblio.org:/public/html/maven
  #for i in */plugins
  #do
    #rsync -e ssh --delete --max-delete=10 -v -rplt $i login.ibiblio.org:/public/html/maven/$i
  #done
#)

date > $dest/maven2/last-sync.txt
chmod a+r $dest/maven2/last-sync.txt
rsync -e ssh --delete --max-delete=10 -v -rplt $dest/maven2/ login.ibiblio.org:/public/html/maven2
