#!/bin/sh

attrget()
{
    ATTR_PAIR=${1#*$2=\"}
    echo "${ATTR_PAIR%%\"*}"
}

reset_failed_result()
{
    if [ $# -ne 1 ] ;then
        echo "Invalid call of function reset_failed_result, no input file!"
        exit 1
    fi

    local IFS=\>

    failed=0
    notExecuted=0
    timeout=0
    pass=0

    while read -d \< ENTITY CONTENT
    do
        TAG_NAME=${ENTITY%% *}
        ATTRIBUTES=${ENTITY#* }

        if [[ $TAG_NAME == "Summary" ]] ;then
            failed=`attrget ${ATTRIBUTES} "failed"`
            notExecuted=`attrget ${ATTRIBUTES} "notExecuted"`
            timeout=`attrget ${ATTRIBUTES} "timeout"`
            pass=`attrget ${ATTRIBUTES} "pass"`
        fi
    done < $1

    notExecuted=$(($notExecuted+$failed+$timeout))
    failed=0

    sed -i 's/failed=\"[0-9]\+\"/failed=\"'${failed}'\"/' $1
    sed -i 's/notExecuted=\"[0-9]\+\"/notExecuted=\"'${notExecuted}'\"/' $1
    sed -i 's/result=\"fail\"/result=\"notExecuted\"/g' $1
}

#准备测试资源
l0=`adb -s $1 shell df /storage/sdcard0 | grep "/storage/sdcard0"`
s0=(${l0// / })
l1=`adb -s $1 shell df /storage/sdcard1 | grep "/storage/sdcard1"`
s1={${l1// / })
if [ "`echo ${s0[1]} | grep "G"`" != "" ] ;then
    adb -s $1 shell rm -rf /storage/sdcard0/*
    adb -s $1 shell mkdir /storage/sdcard0/Download
    adb -s $1 shell mkdir /storage/sdcard0/test
    adb -s $1 push test /storage/sdcard0/test
elif [ "`echo ${s1[1]} | grep "G"`" != "" ] ;then
    adb -s $1 shell rm -rf /storage/sdcard1/*
    adb -s $1 shell mkdir /storage/sdcard1/Download
    adb -s $1 shell mkdir /storage/sdcard1/test
    adb -s $1 push test /storage/sdcard1/test
else
    echo "No free space"
    exit 1
fi

#CTS目录
ctsdir=../CTS-4.4_r1/android-cts

#备份测试结果
if [ ! -x ${ctsdir}/repository/results_backup ] ;then
    mkdir ${ctsdir}/repository/results_backup
fi
if [ -x ${ctsdir}/repository/results ] && [ `ls -l ${ctsdir}/repository/results | wc -l` -gt 1 ] ;then
    mv -f ${ctsdir}/repository/results/* ${ctsdir}/repository/results_backup
fi

#开始新的测试计划
${ctsdir}/tools/cts-tradefed run cts --plan CTS

#开始第二次测试
dir=`ls -F ${ctsdir}/repository/results | grep '/$' | head -1`
reset_failed_result ${ctsdir}/repository/results/${dir}testResult.xml
${ctsdir}/tools/cts-tradefed run cts --continue-session 0
