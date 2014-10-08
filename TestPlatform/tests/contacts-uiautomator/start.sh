#!/bin/sh

if [ "`id | grep "(system)"`" != "" ] ;then
    #通知远程执行命令
    sh log.sh "adb shell sh "`pwd`"/contacts-uiautomator/start.sh "`pwd`"/contacts-uiautomator "$2".txt"
    sleep 10

    #等待测试结束
    while [ -f /data/local/tmp/pid.txt ]
    do
        pid=`cat /data/local/tmp/pid.txt`
        if [ -d /proc/$pid ] ;then
            sleep 3
        else
            break
        fi
    done
else
    #记录当前进程ID
    echo $$ > /data/local/tmp/pid.txt

    #复制JAR文件到指定目录
    cp -f $1/TestContacts1.jar /data/local/tmp
    cp -f $1/TestContacts2.jar /data/local/tmp

    #启动测试 author by sun.fei
    uiautomator runtest TestContacts1.jar -c com.seon.ui.test.TestUI > $2
    #启动测试 author by deng.hanmin
    uiautomator runtest TestContacts2.jar -c com.test.uiautomator.MyUiTest >> $2

    #删除当前进程ID
    rm /data/local/tmp/pid.txt
fi
