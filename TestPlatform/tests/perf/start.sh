#!/bin/sh

if [ "`id | grep "(system)"`" != "" ]; then
    #通知远程执行命令
    sh log.sh "adb shell sh "`pwd`"/performance/start.sh "`pwd`"/performance "$2
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

    #复制JAR文件和资源到指定目录
    cp -f $1/PERF_bundle.jar /data/local/tmp
    cp -f $1/PERF.jar /data/local/tmp
    cp -f $1/MTBF.jar /data/local/tmp
    cp -f $1/person.jpg /sdcard

    #启动开机系统内存使用情况测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.MeminfoDumper >> $2".txt" 2>$2".meminfo.txt"

    #启动应用启动速度和包大小等测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.AppAnalyser >> $2".txt" 2>$2".appinfo.txt"

    #启动安兔兔稳定性测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.AntutuStabilityTest -e jpg $2".antutu-stability.jpg" >> $2".txt"

    #启动MobileXPRT测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.MobileXPRTTest -e jpg $2".mobilexprt.jpg" >> $2".txt"

    #启动安兔兔视频测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.AntutuVideoTest -e jpg $2".antutu-video.jpg" >> $2".txt"

    #启动安兔兔跑分测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.AntutuBenchmarkTest -e jpg $2".antutu-benchmark.jpg" >> $2".txt"

    #启动Vellamo多核测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.VellamoCPUTest -e jpg $2".vellamo-cpu.jpg" >> $2".txt"

    #启动An3DBenchXL测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.An3DBenchXLTest -e jpg $2".an3dbenchxl.jpg" >> $2".txt"

    #启动安兔兔3D测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.Antutu3DRatingTest -e jpg $2".antutu-3drating.jpg" >> $2".txt"

    #启动Vellamo浏览器测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.VellamoBrowserTest -e jpg $2".vellamo-browser.jpg" >> $2".txt"

    #启动nenamark测试
    uiautomator runtest PERF.jar -c com.ztemt.test.perf.NenamarkTest -e jpg $2".nenamark.jpg" >> $2".txt"

    #启动TOP10应用安装、打开、卸载测试
    uiautomator runtest PERF.jar PERF_bundle.jar -c com.ztemt.test.perf.AppInstaller >> $2".txt" 2>$2".appinst.txt"

    #启动MTBF测试
    uiautomator runtest MTBF.jar >> $2".txt"

    #删除当前进程ID
    rm /data/local/tmp/pid.txt
fi
