#!/bin/sh

ssh -p 2222 `echo $@ | sed 's/e\.//'`

