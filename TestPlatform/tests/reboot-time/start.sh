#!/bin/sh

#获得签名类型
tags=`getprop ro.build.tags`

#安装重启时间测试
if [ "$tags" == "release-keys" ] ;then
    pm install -r reboot-time/RebootTime_release.apk
else
    pm install -r reboot-time/RebootTime_test.apk
fi

#启动重启时间测试
am startservice --user 0 -n com.ztemt.test.reboottime/.BootCompletedService --ei loop $1
