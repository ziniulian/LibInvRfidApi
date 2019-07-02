#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <jni.h>
#include <errno.h>

#include "scan_dev.h"

#include <sys/system_properties.h>

#include "android/log.h"

static const char *TAG="system_control";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#define NODE_FILE_AT911 			"/dev/scan_dev"
#define NODE_FILE_AT911N_AT511 	"/dev/gpio_dev"
#define DEVICE_FILE_UHF_AT311		"/dev/uhf" //device point

#define RUN_TYPE_ALLOFF	0x10
#define RUN_TYPE_ALLON		0x13
#define OPER_GPIO_97_ON	0x14
#define OPER_GPIO_97_OFF	0x15
#define OPER_GPIO_99_ON	0x16
#define OPER_GPIO_99_OFF	0x17

#define PLATFORM_UNKNOWN				0
#define PLATFORM_AT911					1
#define PLATFORM_AT911N					2
#define PLATFORM_AT511					3
#define PLATFORM_AT311					4
#define PLATFORM_AT312					5
#define PLATFORM_AT911_HILTI_US			6
#define PLATFORM_AT911_HILTI_EU			7
#define PLATFORM_XCRF1003				8
#define PLATFORM_AT911N_HILTI_US		9
#define PLATFORM_AT911N_HILTI_EU		10
#define PLATFORM_AT870A					11
#define PLATFORM_XC2910					12 // add by invengo at 2017.04.26
#define PLATFORM_XC9910					17

#define UHF_NONE				0
#define UHF_I900MA				1
#define UHF_AT6EM_1				2
#define UHF_AT9200P_1			3
#define UHF_AT500S_1			4
#define UHF_AT2000S_1			5

/*
 * 1.10.2015100100 : getHwVersion �Լ� �߰�. H/W���� ��� GPIO ȣ�� �б�
 * 1.11.2015110300 : powerRfidEx gpio control �� ����� �޼��� ��Ÿ ����.
 * 1.11.2015111301 : powerRfidEx gpio �ٽ� ����
 * 1.11.2016012500 : enable2dScannerEx에서 AT870A일 때, AT511 루틴이 실행되도록 수정.
 * 1.12.2016041800 : AT870A RFID 적용.
 */
#define LIB_VERSION 			"1.12.2016041800"

/*
 * Class:     com_invengo_lib_system_SystemControl
 * Method:    resetRfid
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_resetRfid(JNIEnv *env , jclass this) {

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	int gfd_dev = open(NODE_FILE_AT911, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. resetRfid() - Failed to open GPIO port [%s]", NODE_FILE_AT911);
		return;
	}

	ioctl(gfd_dev, IOCTL_GPB_OUT_CLR, 0); // reset low
	LOGI("INFO. resetRfid() - RFID Device Reset Low");

	usleep(100000);

	ioctl(gfd_dev, IOCTL_GPB_OUT_SET, 0); // reset high
	LOGI("INFO. resetRfid() - RFID Device Reset High");

	close(gfd_dev);
}


/*
 * Class:     com_invengo_lib_system_SystemControl
 * Method:    powerRfid
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_powerRfid(
		JNIEnv *env, jclass this, jboolean enabled) {

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	int gfd_dev = 0;
	gfd_dev = open(NODE_FILE_AT911, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. powerRfid(%s) - Failed to open GPIO port [%s]",
				enabled ? "enabled" : "disabled", NODE_FILE_AT911);
		return;
	}

	ioctl(gfd_dev, enabled ? IOCTL_GPB_OUT_SET : IOCTL_GPB_OUT_CLR, 0); // reset
	LOGI("INFO. powerRfid(%s) - Reset", enabled ? "enabled" : "disabled");
	ioctl(gfd_dev, enabled ? IOCTL_GPB_OUT_SET : IOCTL_GPB_OUT_CLR, 1); // uhf1
	ioctl(gfd_dev, enabled ? IOCTL_GPB_OUT_SET : IOCTL_GPB_OUT_CLR, 2); // uhf2
	LOGI("INFO. powerRfid(%s) - Power RFID Device", enabled ? "enabled" : "disabled");

	close(gfd_dev);

}

/*
 * Class:     com_invengo_lib_system_SystemControl
 * Method:    powerScan
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_powerScan(
		JNIEnv *env, jclass this, jboolean enabled) {

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	int gfd_dev = open(NODE_FILE_AT911, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. powerScan(%s) - Failed to open GPIO port [%s]",
				enabled ? "enabled" : "disabled", NODE_FILE_AT911);
		return;
	}

	ioctl(gfd_dev, enabled ? IOCTL_GPJ0_OUT_SET : IOCTL_GPJ0_OUT_CLR, 4); // scan on
	LOGI("INFO. powerScan(%s)", enabled ? "enabled" : "disabled");

	close(gfd_dev);

}

/*
 * Class:     com_invengo_lib_system_SystemControl
 * Method:    enable2dScanner
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_enable2dScanner(
		JNIEnv *env, jclass this, jboolean enabled) {

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	int gfd_dev = open(NODE_FILE_AT911, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. enable2dScanner(%s) - Failed to open GPIO port [%s]",
				enabled ? "enabled" : "disabled", NODE_FILE_AT911);
		return;
	}

	ioctl(gfd_dev, enabled ? IOCTL_GPB_OUT_SET : IOCTL_GPB_OUT_CLR, 0);

	LOGI("INFO. enable2dScanner(%s)", enabled ? "enabled" : "disabled");

	close(gfd_dev);

}

/*
 * Class:     com_invengo_lib_system_SystemControl
 * Method:    enableScanTrigger
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_enableScanTrigger(
		JNIEnv *env, jclass this, jboolean enabled) {

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	int gfd_dev = open(NODE_FILE_AT911, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. enableScanTrigger(%s) - Failed to open GPIO port [%s]",
				enabled ? "enabled" : "disabled", NODE_FILE_AT911);
		return;
	}

	ioctl(gfd_dev, enabled ? IOCTL_GPJ3_OUT_SET : IOCTL_GPJ3_OUT_CLR, 2);
	LOGI("INFO. enableScanTrigger(%s)", enabled ? "enabled" : "disabled");

	close(gfd_dev);

}

/*
 * Class:     com_invengo_lib_system_SystemControl
 * Method:    enableScanAim
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_enableScanAim(
		JNIEnv *env, jclass this, jboolean enabled) {

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	int gfd_dev = open(NODE_FILE_AT911, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. enableScanAim(%s) - Failed to open GPIO port [%s]",
				enabled ? "enabled" : "disabled", NODE_FILE_AT911);
		return;
	}

	ioctl(gfd_dev, enabled ? IOCTL_GPJ3_OUT_SET : IOCTL_GPJ3_OUT_CLR, 1);
	LOGI("INFO. enableScanAim(%s)", enabled ? "enabled" : "disabled");

	close(gfd_dev);

}

/*
 * Class:     com_invengo_lib_system_SystemControl
 * Method:    enableFlashPower
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_powerExtDevice(
		JNIEnv *env, jclass this, jboolean enabled) {

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	int fd;
    int ret;

    if ((fd = open(DEVICE_FILE_UHF_AT311, O_RDWR)) == -1) {
        LOGE("ERROR. powerExtDevice(%s) - Failed to open UHF devices [%s]",
        		enabled ? "enabled" : "disabled", DEVICE_FILE_UHF_AT311);
    	return;
    }

	ioctl(fd, enabled ? RUN_TYPE_ALLON : RUN_TYPE_ALLOFF);
	LOGI("INFO. powerExtDevice(%s)", enabled ? "enabled" : "disabled");

	usleep(100000);

	ret = close(fd);
}

/*
 * Class:     com_invengo_lib_system_SystemControl
 * Method:    enableFlashPower
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_powerExt97Device(
		JNIEnv *env, jclass this, jboolean enabled) {

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	int fd;
    int ret;

    if ((fd = open(DEVICE_FILE_UHF_AT311, O_RDWR)) == -1) {
        LOGE("ERROR. enableExt97Device(%s) - Failed to open UHF device [%s]",
        		enabled ? "enabled" : "disabled", DEVICE_FILE_UHF_AT311);
    	return;
    }
    ioctl(fd, enabled ? OPER_GPIO_97_ON : OPER_GPIO_97_OFF);
	LOGI("INFO. enableExt97Device(%s)", enabled ? "enabled" : "disabled");

    usleep(100000);
    ret = close(fd);

}

/*
 * Class:     com_invengo_lib_system_SystemControl
 * Method:    enableFlashPower
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_powerExt99Device(
		JNIEnv *env, jclass this, jboolean enabled) {

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	int fd;
    int ret;

    if ((fd = open(DEVICE_FILE_UHF_AT311,O_RDWR)) == -1) {
        LOGE("ERROR. enableExtDevice(%s) - Failed to open UHF device [%s]",
        		enabled ? "enabled" : "disabled", DEVICE_FILE_UHF_AT311);
        return;
    }

    if (enabled) {
        ioctl(fd, RUN_TYPE_ALLOFF);
		LOGI("INFO. enableExtDevice(disabled)");
		ioctl(fd, OPER_GPIO_99_OFF);
		LOGI("INFO. enableExt99Device(disabled)");
    }

    ioctl(fd, enabled ? RUN_TYPE_ALLON : RUN_TYPE_ALLOFF);
	LOGI("INFO. enableExtDevice(%s)", enabled ? "enabled" : "disabled");
    ioctl(fd, enabled ? OPER_GPIO_99_ON : OPER_GPIO_99_OFF);
	LOGI("INFO. enableExt99Device(%s)", enabled ? "enabled" : "disabled");

    ret = close(fd);

}

JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_resetRfidEx(
		JNIEnv *env , jclass this, jint platform)
{

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	char * gpioName = NULL;
	int pinNameLow;
	int pinNameHigh;
	int ctrlCode;
	int gfd_dev;

	if(platform >= PLATFORM_AT911N) {
		gpioName = NODE_FILE_AT911N_AT511;
		pinNameLow = IOCTL_GPIOG_OUT_CLR;
		pinNameHigh = IOCTL_GPIOG_OUT_SET;
		ctrlCode = 11;
	} else {
		gpioName = NODE_FILE_AT911;
		pinNameLow = IOCTL_GPB_OUT_CLR;
		pinNameHigh = IOCTL_GPB_OUT_SET;
		ctrlCode = 0;
	}

	gfd_dev = open(gpioName, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. resetRfidEx() - Failed to open GPIO port [%s]", gpioName);
		return;
	}

	ioctl(gfd_dev, pinNameLow, ctrlCode); // reset low
	LOGI("INFO. resetRfidEx() - RFID Device Reset Low");

	usleep(100000);

	ioctl(gfd_dev, pinNameHigh, ctrlCode); // reset high
	LOGI("INFO. resetRfidEx() - RFID Device Reset High");

	close(gfd_dev);

}

JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_powerRfidEx(
		JNIEnv *env, jclass this, jboolean enabled, jint platform, jint module)
{

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	char * gpioName = NULL;

	if(platform >= PLATFORM_AT911N)
		gpioName = NODE_FILE_AT911N_AT511;
	else
		gpioName = NODE_FILE_AT911;

	int gfd_dev = open(gpioName, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. powerRfidEx(%s, %d, %d) - Failed to open GPIO port [%s]",
				enabled ? "enabled" : "disabled", platform, module, gpioName);
		return;
	}

	if(platform >= PLATFORM_AT911N) {
		switch (platform) {
		case PLATFORM_AT870A: // 2016.01.25
		case PLATFORM_AT911N:
		case PLATFORM_XC2910://add by invengo at 2017.02.26
		case PLATFORM_XC9910:
			if (enabled) {
				ioctl(gfd_dev, IOCTL_GPIOH_OUT_CLR, 24); // uhf off
				ioctl(gfd_dev, IOCTL_GPIOG_OUT_CLR, 11); // reset low // 2015.11.03 test
				usleep(50000);
			}
			if (enabled) {
				ioctl(gfd_dev, IOCTL_GPIOG_OUT_SET, 11); // reset high // chip power
				ioctl(gfd_dev, IOCTL_GPIOH_OUT_SET, 24); // uhf on // module power
				LOGD("@@@@ DEBUG powerRfid(%s, %d, %d) - IOCTL_GPIOG", enabled ? "enabled" : "disabled", platform, module);
			} else {
				ioctl(gfd_dev, IOCTL_GPIOG_OUT_CLR, 11); // reset high // chip power
				ioctl(gfd_dev, IOCTL_GPIOH_OUT_CLR, 24); // uhf on // module power
				LOGD("@@@@ DEBUG powerRfid(%s, %d, %d) - IOCTL_GPIOG", enabled ? "enabled" : "disabled", platform, module);
			}
//			ioctl(gfd_dev, enabled ? IOCTL_GPIOG_OUT_SET : IOCTL_GPIOG_OUT_CLR, 11); // reset high // chip power
//			LOGD("@@@@ DEBUG powerRfid(%s, %d, %d) - IOCTL_GPIOG", enabled ? "enabled" : "disabled", platform, module);
//
//			ioctl(gfd_dev, enabled ? IOCTL_GPIOH_OUT_SET : IOCTL_GPIOH_OUT_CLR, 24); // uhf on // module power
//			LOGD("@@@@ DEBUG powerRfid(%s, %d, %d) - IOCTL_GPIOG", enabled ? "enabled" : "disabled", platform, module);

			LOGI("INFO. powerRfidEx(%s, %d, %d) - Power On RFID Device [XC2910]",
					enabled ? "enabled" : "disabled", platform, module);
			break;
		case PLATFORM_AT911N_HILTI_US:
		case PLATFORM_AT911N_HILTI_EU:
			ioctl(gfd_dev, enabled ? IOCTL_GPIOH_OUT_SET : IOCTL_GPIOH_OUT_CLR, 20); // uhf on
			LOGI("INFO. powerRfidEx(%s, %d, %d) - Power On RFID Device [AT911N-HILTI]",
					enabled ? "enabled" : "disabled", platform, module);
			break;
		case PLATFORM_AT511:
			ioctl(gfd_dev, enabled ? IOCTL_GPIOH_OUT_SET : IOCTL_GPIOH_OUT_CLR, 24); // uhf on
			LOGI("INFO. powerRfidEx(%s, %d, %d) - Power On RFID Device [AT511]",
					enabled ? "enabled" : "disabled", platform, module);
			break;
		}
	} else {
		ioctl(gfd_dev, enabled ? IOCTL_GPB_OUT_SET : IOCTL_GPB_OUT_CLR, 0); // reset high
		LOGI("INFO. powerRfidEx(%s, %d, %d) - RFID Device Reset",
				enabled ? "enabled" : "disabled", platform, module);
		ioctl(gfd_dev, enabled ? IOCTL_GPB_OUT_SET : IOCTL_GPB_OUT_CLR, 1); // uhf on
		ioctl(gfd_dev, enabled ? IOCTL_GPB_OUT_SET : IOCTL_GPB_OUT_CLR, 2); // uhf on
		LOGI("INFO. powerRfidEx(%s, %d, %d) - Power RFID Device",
				enabled ? "enabled" : "disabled", platform, module);
	}

	close(gfd_dev);
}

JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_powerScanEx(
		JNIEnv *env, jclass this, jboolean enabled, jint platform)
{

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	char * gpioName = NULL;

	if(platform >= PLATFORM_AT911N)
		gpioName = NODE_FILE_AT911N_AT511;
	else
		gpioName = NODE_FILE_AT911;

	int gfd_dev = open(gpioName, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. powerScanEx(%s, %d) - Failed to open GPIO port [%s]",
				enabled ? "enabled" : "disabled", platform, gpioName);
		return;
	}

	if(platform >= PLATFORM_AT911N) {
		if (getHwVersion() >= 2)
			ioctl(gfd_dev, enabled ? IOCTL_GPIOH_OUT_SET : IOCTL_GPIOH_OUT_CLR, 24); // gun off
		ioctl(gfd_dev, enabled ? IOCTL_GPIOH_OUT_SET : IOCTL_GPIOH_OUT_CLR, 11); // scan on
	} else {
		ioctl(gfd_dev, enabled ? IOCTL_GPJ0_OUT_SET : IOCTL_GPJ0_OUT_CLR, 4); // scan on
	}
	LOGI("INFO. powerScanEx(%s, %d)",
			enabled ? "enabled" : "disabled", platform);

	close(gfd_dev);

}

JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_enable2dScannerEx(
		JNIEnv *env, jclass this, jboolean enabled, jint platform)
{

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	char * gpioName = NULL;

	if(platform >= PLATFORM_AT911N)
		gpioName = NODE_FILE_AT911N_AT511;
	else
		gpioName = NODE_FILE_AT911;

	int gfd_dev = open(gpioName, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. enable2dScannerEx(%s, %d) - Failed to open GPIO port [%s]",
				enabled ? "enabled" : "disabled", platform, gpioName);
		return;
	}

	if(platform >= PLATFORM_AT911N) {
		switch (platform) {
		case PLATFORM_AT911N:
		case PLATFORM_XC2910://add by invengo at 2017.02.26
		case PLATFORM_XC9910:
		case PLATFORM_AT911N_HILTI_US:
		case PLATFORM_AT911N_HILTI_EU:
			ioctl(gfd_dev, enabled ? IOCTL_GPIOI_OUT_CLR : IOCTL_GPIOI_OUT_SET, 3); // AT911N
			LOGI("INFO. enable2dScannerEx(%s, %d) - Power On RFID Device [XC2910]",
					enabled ? "enabled" : "disabled", platform);
			break;
		case PLATFORM_AT511:
		case PLATFORM_AT870A: // 2016.01.25
			ioctl(gfd_dev, enabled ? IOCTL_GPIOC_OUT_CLR : IOCTL_GPIOC_OUT_SET, 21); // AT511
			LOGI("INFO. enable2dScannerEx(%s, %d) - Power On RFID Device [AT511]",
					enabled ? "enabled" : "disabled", platform);
			break;
		}

	} else {
		ioctl(gfd_dev, enabled ? IOCTL_GPB_OUT_SET : IOCTL_GPB_OUT_CLR, 0);
	}
	LOGI("INFO. enable2dScannerEx(%s, %d)",
			enabled ? "enabled" : "disabled", platform);

	close(gfd_dev);

}

JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_enableScanTriggerEx(
		JNIEnv *env, jclass this, jboolean enabled, jint platform)
{

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	char * gpioName = NULL;

	if(platform >= PLATFORM_AT911N)
		gpioName = NODE_FILE_AT911N_AT511;
	else
		gpioName = NODE_FILE_AT911;

	int gfd_dev = open(gpioName, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. enableScanTriggerEx(%s, %d) - Failed to open GPIO port [%s]",
				enabled ? "enabled" : "disabled", platform, gpioName);
		return;
	}

	if(platform >= PLATFORM_AT911N) {
		ioctl(gfd_dev, enabled ? IOCTL_GPIOH_OUT_SET : IOCTL_GPIOH_OUT_CLR, 10);
	} else {
		ioctl(gfd_dev, enabled ? IOCTL_GPJ3_OUT_SET : IOCTL_GPJ3_OUT_CLR, 2);
	}
	LOGI("INFO. enableScanTriggerEx(%s, %d)",
			enabled ? "enabled" : "disabled", platform);

	close(gfd_dev);

}

JNIEXPORT void JNICALL Java_com_invengo_lib_system_ModuleControl_enableScanAimEx(
		JNIEnv *env, jclass this, jboolean enabled, jint platform)
{

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	char * gpioName = NULL;

	if(platform >= PLATFORM_AT911N)
		gpioName = NODE_FILE_AT911N_AT511;
	else
		gpioName = NODE_FILE_AT911;

	int gfd_dev = open(gpioName, O_RDWR);
	if(gfd_dev < 0) {
		LOGE("ERROR. enableScanAimEx(%s, %d) - Failed to open GPIO port [%s]",
				enabled ? "enabled" : "disabled", platform, gpioName);
		return;
	}

	if(platform >= PLATFORM_AT911N) {
		ioctl(gfd_dev, enabled ? IOCTL_GPIOH_OUT_SET : IOCTL_GPIOH_OUT_CLR, 18);
	} else {
		ioctl(gfd_dev, enabled ? IOCTL_GPJ3_OUT_SET : IOCTL_GPJ3_OUT_CLR, 1);
	}
	LOGI("INFO. enableScanAimEx(%s, %d)",
			enabled ? "enabled" : "disabled", platform);

	close(gfd_dev);

}

JNIEXPORT jstring JNICALL Java_com_invengo_lib_system_ModuleControl_getLibVersion (
		JNIEnv *env, jclass this, jobject fileDesc) {

	LOGI("INFO. Library Version [invengo.system.ctrl] : %s", LIB_VERSION);

	return (*env)->NewStringUTF(env, LIB_VERSION);
}


JNIEXPORT jint JNICALL Java_com_invengo_lib_system_ModuleControl_getHwVersion (
		JNIEnv *env, jclass this, jobject fileDesc) {

	int ver = getHwVersion();
	LOGI("INFO. Hardware Version : %d", ver);
	return (jint)ver;
}

int getHwVersion() {
	int nRet = -1;
	int nTemp = 0;

	char buf[256];
	FILE *fp;
	char BuildId[128] = {0};

    sprintf(buf, "/sys/class/system_info/hw_version");
    fp = fopen(buf, "r");
    if (!fp) {
    	LOGE("ERROR. getHwVersion() - Failed to invalid file if gpio was not exported!!!");
    	return -1;
    } else {
		if (fread(buf, 1, sizeof(buf), fp) > 0) {
			nRet = atoi(buf);
	    	LOGI("INFO. getHwVersion() - [%d]", nRet);
		} else {
			LOGE("ERROR. getHwVersion() - Failed to read");
		}
		fclose(fp);
    }
    return nRet;
}
