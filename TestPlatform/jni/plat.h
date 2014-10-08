#ifndef PLAT_H_
#define PLAT_H_

#include <android/log.h>

#define LOG_TAG "plat"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))

char* execute(const char*);

int is_process_exist(char*);

int is_package_installed(char*);

#endif /* PLAT_H_ */
