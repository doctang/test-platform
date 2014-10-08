import commands
import os
import subprocess
import sys
import threading
import time

onlines = []

def popen(cmd):
    return subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

class LogcatThread(threading.Thread):

    def __init__(self, device):
        threading.Thread.__init__(self)
        self.device = device

    def run(self):
        onlines.append(self.device)
        p = popen('adb -s {0} logcat -v raw -s TestPlat:V I:s'.format(self.device))
        f = time.time()
        line = p.stdout.readline().strip()
        while line and line != '^C':
            s = time.time()
            if s - f > 0.5:
                print('{0} {1}: {2}'.format(time.strftime('%Y-%m-%d %H:%M:%S'), self.device, line))
                if line.startswith('adb '):
                    os.system('adb -s {0} {1}'.format(self.device, line[4:]))
                else:
                    os.system(line)
            f = s
            line = p.stdout.readline().strip()
        onlines.remove(self.device)

def main():
    if os.name == 'posix' and commands.getoutput('ps -ef | grep "python start.py" | grep -v grep'):
        print('process is running')
        sys.exit(2)

    while True:
        p1 = popen('adb status-window')
        l1 = p1.stdout.readline().strip()
        while l1:
            if l1.startswith('State:'):
                p2 = popen('adb devices')
                devices = p2.stdout.readlines()[1:]
                for l2 in [device for device in devices if device.strip()]:
                    device = l2.strip().split(None, 1)
                    if device[1] == b'device' and device[0].decode() not in onlines:
                        t = LogcatThread(device[0].decode())
                        t.start()
            l1 = p1.stdout.readline().strip()

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        pass
