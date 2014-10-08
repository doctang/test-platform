#!/bin/sh

str=$1
arr=(${str// / })

#包名
package=${arr[0]}
#测试次数
count=${arr[1]}
#循环次数
loop=${arr[2]}
#日志输出路径
logpath=$2".txt"
#内存信息输出文件名
mempath=$2".meminfo.txt"
#系统内存信息输出文件名
srvpath=$2".srvinfo.txt"

#开始记录内存信息
sh single/meminfo.sh $package $mempath $srvpath &
pid1=$!

#创建空日志输出文件
touch $logpath
chmod 644 $logpath

i=0
while [ $i != $loop ]
do
    i=$(($i+1))
    monkey -p $package -s 10 --ignore-timeouts --ignore-crashes -v $count >> $logpath 2>&1
    am force-stop $package
    sleep 3
done

#杀掉在后台运行的记录内存信息进程
kill $pid1
