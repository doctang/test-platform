#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#include "plat.h"

//#define MEMINFO_OUTPUT "/data/local/tmp/meminfo.txt"

int main(int argc, char** argv) {
    if (argc < 3) {
        LOGE("Usage: %s <monkey> <output>", argv[0]);
        return EXIT_FAILURE;
    }

    FILE* g_lockfile = fopen("monkey.lock", "a+");
    if (g_lockfile == NULL) {
        LOGE("fopen(monkey) failed");
        return EXIT_FAILURE;
    }

    if (flock(fileno(g_lockfile), LOCK_EX | LOCK_NB)) {
        LOGE("flock(monkey) failed");
        return EXIT_FAILURE;
    }

/*    pid_t pid;
    u_long count = 0;
    int stat;
    char string[BUFSIZ];
    pid = fork();
    if (pid < 0) {
        LOGE("fork() failed");
        return EXIT_FAILURE;
    } else if (pid == 0) {
        sprintf(string, "%s", argv[1]);
        system(string);
    } else {
        remove(MEMINFO_OUTPUT);
        sprintf(string, "date >> %s", MEMINFO_OUTPUT);
        system(string);
        while (waitpid(-1, &stat, WNOHANG) <= 0) {
            count++;
            sleep(10);
            sprintf(string, "dumpsys meminfo >> %s", MEMINFO_OUTPUT);
            system(string);
        }
    }*/
    char string[BUFSIZ], buf[BUFSIZ];
    FILE *fp, *ff;

    ff = fopen(argv[2], "w+");
    if (ff != NULL) {
        chmod(argv[2], S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH);
    } else {
        return EXIT_FAILURE;
    }

    sprintf(string, "%s 2>&1", argv[1]);
    fp = popen(string, "r");
    if (fp == NULL) {
        fclose(ff);
        return EXIT_FAILURE;
    }

    while (fgets(buf, BUFSIZ, fp)) {
        fputs(buf, ff);
    }

    fclose(ff);
    pclose(fp);

    flock(fileno(g_lockfile), LOCK_UN);
    return EXIT_SUCCESS;
}
