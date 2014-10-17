# -*- coding:UTF-8 -*-

import codecs
import json
import os
import re
import subprocess
import sys
import threading
import time

class DumpsysMeminfoThread(threading.Thread):

    def __init__(self, package, interval, output):
        threading.Thread.__init__(self)
        self.package = package
        self.interval = interval
        self.output = output
        self.loop = True

    def run(self):
        while self.loop:
            time.sleep(self.interval)
            subprocess.Popen('adb shell dumpsys meminfo {0}'.format(self.package),
                    shell=True, stdout=self.output, stderr=self.output).wait();

    def stop(self):
        self.loop = False

def startActivity(packageName, activityName, clearTask):
    cmd = 'adb shell am start --user 0 -W {2} -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -n {0}/{1}'.format(
            packageName, activityName, '--activity-clear-task' if clearTask else '')
    lines = os.popen(cmd).readlines()
    for line in lines:
        if line.startswith('ThisTime:'):
            return int(line[10:])
    return 0

def getPackageSize(packageName):
    path = os.popen('adb shell pm path {0}'.format(packageName)).readline().strip()[8:]
    line = os.popen('adb shell ls -l {0}'.format(path)).readline().strip()
    m = re.search('\s+(\d+)\s+', line)
    return m.groups()[0] if m else '0'

def getLaunchTime(packageName, title, activityName, output):
    os.system('adb shell am force-stop {0}'.format(packageName))
    time1 = startActivity(packageName, activityName, False)
    time2 = startActivity(packageName, activityName, True)
    time3 = startActivity(packageName, activityName, True)
    time4 = startActivity(packageName, activityName, True)
    time5 = startActivity(packageName, activityName, True)
    time6 = startActivity(packageName, activityName, True)
    os.system('adb shell am force-stop {0}'.format(packageName))
    time = (time1, time2, time3, time4, time5, time6)

    output.write('{0},{1},{2[0]},{2[1]},{2[2]},{2[3]},{2[4]},{2[5]},{3},{4},{5}\n'.format(title, getPackageSize(packageName),
            time, min(time[1:]), max(time[1:]), sum(time[1:]) / (len(time) - 1)))
    output.flush()

def report_monkey(monkey, output):
    data = {'count': 0, 'event': 0, 'crash': 0, 'anr': 0}
    for line in monkey.readlines():
        if line.startswith(':Monkey:'):
            m = re.match(':Monkey: seed=(\d+) count=(\d+)', line)
            data['count'] = m.groups()[1]
        elif line.startswith('    // Sending event #'):
            m = re.match('    // Sending event #(\d+)', line)
            data['event'] = m.groups()[0]
        elif line.startswith('Events injected:'):
            m = re.match('Events injected: (\d+)', line)
            data['event'] = m.groups()[0]
        elif line.startswith('// CRASH:'):
            data['crash'] = data['crash'] + 1
        elif line.startswith('// NOT RESPONDING:'):
            data['anr'] = data['anr'] + 1

    output.write(codecs.BOM_UTF8)
    output.write('预期次数,实际次数,CRASH次数,ANR次数\n')
    output.write('{0},{1},{2},{3}\n'.format(data['count'], data['event'], data['crash'], data['anr']))

def report_meminfo(meminfo, output):
    list = []
    data = {}
    for line in meminfo.readlines():
        if line.strip().startswith('Native'):
            m = re.match('Native[ Heap]+\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)', line.strip())
            if m:
                data['native_heap_size'] = m.groups()[4]
                data['native_heap_alloc'] = m.groups()[5]
                data['native_heap_free'] = m.groups()[6]
        elif line.strip().startswith('Dalvik'):
            m = re.match('Dalvik[ Heap]+\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)', line.strip())
            if m:
                data['dalvik_heap_size'] = m.groups()[4]
                data['dalvik_heap_alloc'] = m.groups()[5]
                data['dalvik_heap_free'] = m.groups()[6]
        if len(data) == 6:
            list.append(data.copy())
            data.clear()

    if len(list) > 0:
        output.write(','.join(('Native Heap Size', 'Native Heap Alloc', 'Native Heap Free',
                'Dalvik Heap Size', 'Dalvik Heap Alloc', 'Dalvik Heap Free')) + '\n')
        for item in list:
            output.write(','.join((item['native_heap_size'], item['native_heap_alloc'], item['native_heap_free'],
                    item['dalvik_heap_size'], item['dalvik_heap_alloc'], item['dalvik_heap_free'])) + '\n')

def monkey(packageName):
    perf_out = os.path.join(os.path.dirname(os.path.realpath(sys.argv[0])), 'perf_out')
    if not os.path.exists(perf_out):
        os.mkdir(perf_out)
    outdir = os.path.join(perf_out, 'monkey-{0}'.format(packageName))
    if not os.path.exists(outdir):
        os.mkdir(outdir)

    meminfo = open(os.path.join(outdir, 'meminfo.txt'), 'w+')
    t = DumpsysMeminfoThread(packageName, 20, meminfo)
    t.start()

    monkey = open(os.path.join(outdir, 'monkey.txt'), 'w+')
    subprocess.Popen('adb shell monkey -p {0} -s 10 --ignore-timeouts --ignore-crashes -v 100'.format(
            packageName), shell=True, stdout=monkey, stderr=monkey).wait()
    monkey.flush()
    monkey.seek(0)
    report = open(os.path.join(outdir, 'monkey.csv'), 'w')
    report_monkey(monkey, report)
    report.close()
    monkey.close()

    t.stop()
    t.join()
    meminfo.flush()
    meminfo.seek(0)
    report = open(os.path.join(outdir, 'meminfo.csv'), 'w')
    report_meminfo(meminfo, report)
    report.close()
    meminfo.close()

def main():
    workdir = os.path.dirname(os.path.realpath(sys.argv[0]))
    perfout = os.path.join(workdir, 'perf_out')
    if not os.path.exists(perfout):
        os.mkdir(perfout)

    apkpath = os.path.join(workdir, 'TestCommon.apk')
    os.system('adb install -r \"{0}\"'.format(apkpath))
    os.system('adb shell am startservice -n com.ztemt.test.common/.PackageService --es command getLauncherList')
    time.sleep(2)
    line = os.popen('adb shell cat /data/data/com.ztemt.test.common/files/launcher').readline()
    jobj = eval(line)
    os.system('adb uninstall com.ztemt.test.common')

    launcher = file(os.path.join(perfout, 'appsinfo.csv'), 'w+')
    launcher.write(codecs.BOM_UTF8)
    launcher.write('名称,包大小,第一次,第二次,第三次,第四次,第五次,第六次,最小值,最大值,平均值\n')
    launcher.flush()

    for line in os.popen('adb shell pm list package -s').readlines():
        package = line[8:].strip()
        if package in jobj:
            for item in jobj[package]:
                getLaunchTime(package, item['title'], item['activity'], launcher)
            monkey(package)

    launcher.close()

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        pass
