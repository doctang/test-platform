#include <errno.h>
#include <fcntl.h>
#include <limits.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/system_properties.h>
#include <unistd.h>
#include "plat.h"

#define SLEEP_INTERVAL 60
#define KEY_CONNECTED "persist.sys.testplat.connected"

int main(int argc, char** argv) {
    if (argc < 3) {
        LOGE("Usage: %s <packagename> <ServiceNameOrActivityName>", argv[0]);
        return EXIT_FAILURE;
    }

    int ret = -1;
    FILE* g_lockfile = NULL;

    // 检查是否已经有一个platd进程在运行
    g_lockfile = fopen("platd.lock", "a+");
    if (g_lockfile == NULL) {
        LOGE("fopen(platd) failed");
        return EXIT_FAILURE;
    }

    ret = flock(fileno(g_lockfile), LOCK_EX | LOCK_NB);
    if (ret != 0) {
        LOGE("flock(platd) failed");
        return EXIT_FAILURE;
    }

    char string[BUFSIZ];
    char value[PROP_VALUE_MAX];

    while (1) {
        // 休眠60秒钟循环检测服务是否在运行
        sleep(SLEEP_INTERVAL);

        // 判断连接是否存在，如果已经断开，则退出
        if (!__system_property_get(KEY_CONNECTED, value) || strcmp(value, "true")) {
            LOGD("websocket connection is lost");
            break;
        }

        // 如果已卸载则重新安装
        if (!is_package_installed(argv[1])) {
            sprintf(string, "pm install -r %s.apk", argv[1]);
            execute(string);
        }

        // 启动服务
        sprintf(string, "am startservice -n %s/%s", argv[1], argv[2]);
        execute(string);
    }

    flock(fileno(g_lockfile), LOCK_UN);
    return EXIT_SUCCESS;
}
