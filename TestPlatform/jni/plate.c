#include <errno.h>
#include <fcntl.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/stat.h>
#include "plat.h"

int main(int argc, char** argv) {
    if (argc < 4) {
        LOGE("Usage: %s <module> <extras> <path>", argv[0]);
        return EXIT_FAILURE;
    }

    int ret = -1;
    FILE* g_lockfile = NULL;

    // 检查是否已经有一个plate进程在运行
    g_lockfile = fopen("plate.lock", "a+");
    if (g_lockfile == NULL) {
        LOGE("fopen(plate) failed");
        return EXIT_FAILURE;
    }

    ret = flock(fileno(g_lockfile), LOCK_EX | LOCK_NB);
    if (ret != 0) {
        LOGE("flock(plate) failed");
        return EXIT_FAILURE;
    }

    char string[BUFSIZ];
    sprintf(string, "sh %s/start.sh '%s' '%s'", argv[1], argv[2], argv[3]);
    system(string);

    flock(fileno(g_lockfile), LOCK_UN);
    return EXIT_SUCCESS;
}
