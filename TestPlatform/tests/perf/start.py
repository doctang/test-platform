import json
import os
import subprocess
import sys
import time

def startActivity(packageName, activityName, clearTask):
    cmd = 'adb shell am start --user 0 -W {2} -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -n {0}/{1}'.format(
            packageName, activityName, '--activity-clear-task' if clearTask else '')
    lines = os.popen(cmd).readlines()
    for line in lines:
        if line.startswith('ThisTime:'):
            return int(line[10:])
    return 0

def getLaunchTime(packageName, activityName):
    os.system('adb shell am force-stop {0}'.format(packageName))
    time1 = startActivity(packageName, activityName, False)
    time2 = startActivity(packageName, activityName, True)
    time3 = startActivity(packageName, activityName, True)
    time4 = startActivity(packageName, activityName, True)
    time5 = startActivity(packageName, activityName, True)
    time6 = startActivity(packageName, activityName, True)
    os.system('adb shell am force-stop {0}'.format(packageName))
    time = (time1, time2, time3, time4, time5, time6)
    print('{0[0]:<8}{0[1]:<8}{0[2]:<8}{0[3]:<8}{0[4]:<8}{0[5]:<8}{1:<8}{2:<8}{3:<8}'.format(
            time, min(time[1:]), max(time[1:]), sum(time[1:]) / (len(time) - 1)))

def monkey(packageName):
    p = subprocess.Popen('adb shell monkey -p {0} -s 10 --ignore-timeouts --ignore-crashes -v 1000'.format(packageName),
            shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    print('start')
    lines = p.stderr.readlines()
    print(lines)

def main():
    workdir = os.path.dirname(os.path.realpath(sys.argv[0]))
    apkpath = os.path.join(workdir, 'TestCommon.apk')
    os.system('adb install -r {0}'.format(apkpath))
    for line in os.popen('adb shell pm list package -s').readlines():
        packageName = line[8:].strip()
        os.system('adb shell am startservice -n com.ztemt.test.common/.PackageService --es package {0}'.format(packageName))
        time.sleep(1.5)
        info = os.popen('adb shell cat /sdcard/launcher.info').readline()
        array = eval(info)
        if len(array) > 0:
            #for item in array:
                #getLaunchTime(packageName, item['activity'])
            monkey(packageName)
    os.system('adb uninstall com.ztemt.test.common')

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        pass
