# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

# UHF jni
#include $(CLEAR_VARS)

#LOCAL_MODULE    := at_scanner_jni
#LOCAL_SRC_FILES := at_scanner_jni.c
#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
#LOCAL_CFLAGS := -DCONFIG_EMBEDDED\
				-DUSE_IND_THREAD\

#include $(BUILD_SHARED_LIBRARY)

# UHF jni

include $(CLEAR_VARS)

TARGET_PLATFORM := android-3
LOCAL_MODULE    := system_control
LOCAL_SRC_FILES := system_control.c
LOCAL_LDLIBS    := -llog

$(shell cp $(wildcard $(LOCAL_PATH)/lib/armeabi/*.so) $(TARGET_OUT_INTERMEDIATE_LIBRARIES))

#include $(PREBUILT_SHARED_LIBRARY)
include $(BUILD_SHARED_LIBRARY)