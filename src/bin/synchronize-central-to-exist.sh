#!/bin/bash

[ "$1" = "" ] && echo && echo "You must pass in the synchronize.properties file!" && echo && exit

. $1

echo ">>>>>>>>>>>>>>>>>> Syncing Maven 2.x repository to Exist"

rsync -e $HOME/bin/cmd-for-rssh.sh --include='.index' --include='*.asc' --include='*.md5' --include='*.sha1' -v -z -riplt $MAVEN2_REPO/ $EXIST_USERNAME@$EXIST_SYNC_HOST:$M2_EXIST_SYNC_DIR

rsync -e $HOME/bin/cmd-for-rssh.sh --exclude='.index' --exclude='*.asc' --exclude='*.md5' --exclude='*.sha1' --delete --max-delete=10 -v -z -riplt $MAVEN2_REPO/ $EXIST_USERNAME@$EXIST_SYNC_HOST:$M2_EXIST_SYNC_DIR
