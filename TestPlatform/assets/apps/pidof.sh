#!/bin/sh

line=`ps | grep -E $1"$"`

exp=(${line// / })

echo ${exp[1]}
