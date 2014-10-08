#!/bin/sh

#获得签名类型
tags=`getprop ro.build.tags`

#安装压力测试
if [ "$tags" == "release-keys" ] ;then
    pm install -r stress/SettingsTest_release.apk
    pm install -r stress/AutoTest_release.apk
else
    pm install -r stress/SettingsTest_test.apk
    pm install -r stress/AutoTest_test.apk
fi

#启动压力测试
am start --user 0 -n com.ztemt.test.auto/.AutoTestActivity --es mode auto $1
