#!/bin/sh

#安装安兔兔
pm install -r antutu-benchmark/AnTuTuBenchmark.apk

#启动安兔兔
am start -n com.antutu.ABenchMark/.ABenchMarkStart

#等待安兔兔初始化
sleep 10

#获取屏幕分辨率
str=`dumpsys window displays | grep "init="`
exp=(${str// / })
eval ${exp}

#获取屏幕密度
density=`getprop ro.sf.lcd_density`

#进入相应分辨率点击处理
if [ "${init}" == "1080x1920" ] && [ "${density}" == "320" ] ;then
    sh antutu-benchmark/antutu_benchmark_1920_1080_320.sh
elif [ "${init}" == "1080x1920" ] && [ "${density}" == "400" ] ;then
    sh antutu-benchmark/antutu_benchmark_1920_1080_400.sh
elif [ "${init}" == "1080x1920" ] && [ "${density}" == "480" ] ;then
    sh antutu-benchmark/antutu_benchmark_1920_1080_480.sh
elif [ "${init}" == "720x1280" ] ;then
    sh antutu-benchmark/antutu_benchmark_1280_720.sh
else
    echo "Not match "${init}
fi

#截图
screencap -p $2".png"

#等待截图完成
sleep 3

#修改文件权限
chmod 644 $2".png"
