#!/bin/sh

#包名
package=$1
#内存信息输出路径
mempath=$2
#系统内存信息输出路径
srvpath=$3

#创建空内存信息输出文件
touch $mempath
chmod 644 $mempath
touch $srvpath
chmod 644 $srvpath

while [ true ]
do
    sleep 20
    dumpsys meminfo $package | grep -E "Native [ | H]|Dalvik [ |H]" >> $mempath
    dumpsys meminfo system | grep -E "Native [ | H]|Dalvik [ |H]" >> $srvpath
done
