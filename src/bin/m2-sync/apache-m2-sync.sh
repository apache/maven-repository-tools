#!/bin/sh

FROM=jvanzyl@cvs.apache.org:/www/www.apache.org/dist/maven-repository/*
TO=.

./m2-sync.sh