LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := platd
LOCAL_SRC_FILES := plat.c platd.c
LOCAL_CFLAGS    += -pie -fPIE
LOCAL_LDFLAGS   += -pie -fPIE
LOCAL_LDLIBS    := -llog

include $(BUILD_EXECUTABLE)

####################################

include $(CLEAR_VARS)

LOCAL_MODULE    := plate
LOCAL_SRC_FILES := plat.c plate.c
LOCAL_CFLAGS    += -pie -fPIE
LOCAL_LDFLAGS   += -pie -fPIE
LOCAL_LDLIBS    := -llog

include $(BUILD_EXECUTABLE)

####################################

include $(CLEAR_VARS)

LOCAL_MODULE    := monkey
LOCAL_SRC_FILES := plat.c monkey.c
LOCAL_LDLIBS    := -llog

include $(BUILD_EXECUTABLE)
