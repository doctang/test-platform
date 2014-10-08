#!/bin/sh

path=$2".txt"

#获得签名类型
tags=`getprop ro.build.tags`

#安装测试用例
if [ "$tags" == "release-keys" ] ;then
    pm install -r contacts-robotium/TestContacts1_release.apk
    pm install -r contacts-robotium/TestContacts2_release.apk
    pm install -r contacts-robotium/TestContacts3_release.apk
else
    pm install -r contacts-robotium/TestContacts1_test.apk
    pm install -r contacts-robotium/TestContacts2_test.apk
    pm install -r contacts-robotium/TestContacts3_test.apk
fi

#启动测试用例 author by sun.fei
am instrument -r -w -e class com.android.contacts.test.MMSTest com.android.contacts.test/android.test.InstrumentationTestRunner > $path
#启动测试用例 author by gao.wei
am instrument -r -w -e class com.example.appadmination.test.TestMain com.example.appadmination.test/android.test.InstrumentationTestRunner >> $path
#启动测试用例 author by deng.hanmin
am instrument -r -w -e class com.example.asynctasktest.test.TestPhonefun com.example.asynctasktest.test/android.test.InstrumentationTestRunner >> $path

#修改文件权限
chmod 644 $path
