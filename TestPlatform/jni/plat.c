#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
#include <android/log.h>
#include "plat.h"

char* execute(const char* cmd) {
    FILE* fp;
    char* ret = NULL;
    char tmp[BUFSIZ];
    if ((fp = popen(cmd, "r")) != NULL && (ret = fgets(tmp, BUFSIZ, fp)) != NULL) {
        //LOGD("%s=%s", cmd, tmp);
    } else {
        //LOGD("%s=%s", cmd, "NULL");
    }
    pclose(fp);
    return ret;
}

int is_process_exist(char* process_name) {
    char cmd[BUFSIZ];
    sprintf(cmd, "sh pidof.sh %s", process_name);
    return execute(cmd) != NULL;
}

int is_package_installed(char* package_name) {
    char cmd[BUFSIZ];
    sprintf(cmd, "pm list packages %s", package_name);
    return execute(cmd) != NULL;
}
