#!/bin/sh

#获得SDK版本
sdk=`getprop ro.build.version.sdk`
if [ ${sdk:0:2} == "18" ] ;then
    ver="4.3"
elif [ ${sdk:0:2} == "19" ] ;then
    ver="4.4"
else
    echo "unsupport sdk version "$sdk
    exit 1
fi

#安装CTS管理工具
pm install -r cts/CtsDeviceAdmin_$ver.apk

#安装CTS设置工具
tags=`getprop ro.build.tags`
if [ "$tags" == "release-keys" ] ;then
    pm install -r cts/CtsSettings_release.apk
else
    pm install -r cts/CtsSettings_test.apk
fi

#开始CTS设置
am instrument -r -w -e class com.android.settings.test.SettingsTestCase\#testSwitchEnglish com.android.settings.test/android.test.InstrumentationTestRunner
am instrument -r -w -e class com.android.settings.test.SettingsTestCase\#testActiveCtsDeviceAdminReceiver com.android.settings.test/android.test.InstrumentationTestRunner
am instrument -r -w -e class com.android.settings.test.SettingsTestCase\#testSetMaxScreenTimeout com.android.settings.test/android.test.InstrumentationTestRunner
am instrument -r -w -e class com.android.settings.test.SettingsTestCase\#testShowDeveloperOptions com.android.settings.test/android.test.InstrumentationTestRunner
am instrument -r -w -e class com.android.settings.test.SettingsTestCase\#testSetDeveloperOptions com.android.settings.test/android.test.InstrumentationTestRunner
am instrument -r -w -e class com.android.settings.test.SettingsTestCase\#testSetDatetime com.android.settings.test/android.test.InstrumentationTestRunner

#通知执行命令
sh log.sh "./cts_"$ver".sh "`getprop ro.serialno`
