#!/bin/sh

#获得参数数组
params=$1
params=(${params// / })

#参数0：循环次数
loop=${params[0]}

#参数1：休眠几秒
rest=${params[1]}

#参数2：每次启动应用前是否强制停止
force=${params[2]}

#日志存放路径
path=$2".txt"

#删除原来的日志
rm -f $path

while read line
do
    i=0
    eval $line

    #判断包是否存在
    if [ "`pm list package $package`" == "" ] ;then
        continue
    fi

    #记录应用名称
    echo "["$title"]" >> $path

    while [ $i != $loop ]
    do
        #第一次或者指定强制停止
        if [ $i == 0 ] || [ "$force" == "true" ] ;then
            am force-stop $package
            sleep 1
        fi

        #启动应用
        am start -W --activity-clear-task -n $package/$name >> $path
        sleep $rest

        #增加计数
        i=$(($i+1))

        #最后一次时强制停止
        if [ $i == $loop ] ;then
            am force-stop $package
        fi
    done
done < launch-time/apps.txt
