#!/bin/sh

ssh `echo $@ | sed 's/e\.//'`

