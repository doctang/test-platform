#!/bin/sh

while [ true ]
do
    pid=`sh pidof.sh $1`
    if [ "$pid" == "" ] ;then
        break
    else
        sleep 3
    fi
done
