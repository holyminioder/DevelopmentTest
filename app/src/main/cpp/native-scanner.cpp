#include <jni.h>
#include <string.h>
#include <android/log.h>


#ifdef __cplusplus
extern "C" {
#endif
#define  LOG_TAG    "native-scanner"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO  , LOG_TAG, __VA_ARGS__)

#include "native-scanner.h"
#include "com_example_lederui_developmenttest_data_ScannerInterface.h"
#include <stdio.h>


/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SInit
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SInit
        (JNIEnv *, jclass){
    int scanRect = SInit(NULL, "/sdcard/", "/sdcard/");
    return scanRect;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SQueryCapability
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SQueryCapability
        (JNIEnv *, jclass){
    int result = SQueryCapability();
    return result;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SGetScanDpi
 * Signature: (Ljava/lang/Integer;Ljava/lang/Integer;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SGetScanDpi
        (JNIEnv *env, jclass, jobject widthDpi, jobject heightDpi){

    bool flag = false;
    jclass class_dpi = env->FindClass("java/lang/Integer");
    jfieldID id = env->GetFieldID(class_dpi, "value", "I");

    int w =0,h=0;
    flag = SGetScanDpi(&w, &h);

    env->SetIntField(widthDpi, id, w);
    env->SetIntField(heightDpi, id, h);

    return flag;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SGetBrandDpi
 * Signature: (Ljava/lang/Integer;Ljava/lang/Integer;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SGetBrandDpi
        (JNIEnv *env, jclass, jobject widthDpi, jobject heightDpi){

    bool flag = false;
    jclass class_dpi = env->FindClass("java/lang/Integer");
    jfieldID id = env->GetFieldID(class_dpi, "value", "I");

    int w =0,h=0;
    flag = SGetBrandDpi(&w, &h);

    env->SetIntField(widthDpi, id, w);
    env->SetIntField(heightDpi, id, h);

    return flag;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SGetHWInformation
 * Signature: ([BI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SGetHWInformation
        (JNIEnv *env, jclass, jbyteArray hwInfo, jint length){

    bool flag = false;
    char info[1024] = {0x00};
    flag = SGetHWInformation(info, sizeof(info));
    env->SetByteArrayRegion(hwInfo,0,strlen(info),(jbyte*)info);

    return flag;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SGetSWVersion
 * Signature: ([BI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SGetSWVersion
        (JNIEnv *env, jclass, jbyteArray swVersion, jint length){

    bool flag = false;
    char info[1024] = {0x00};
    flag = SGetSWVersion(info, sizeof(info));
    env->SetByteArrayRegion(swVersion,0,strlen(info),(jbyte*)info);

    return flag;

}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SGetLastErrorCode
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SGetLastErrorCode
        (JNIEnv *, jclass){
    int ret = SGetLastErrorCode();
    return ret;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SGetLastErrorStr
 * Signature: ([BI)V
 */
JNIEXPORT jstring JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SGetLastErrorStr
(JNIEnv *env, jclass, jbyteArray, jint){
    char err[256] = {0x00};
    SGetLastErrorStr(err, 256);
    return env->NewStringUTF(err);
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SStart
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SStart
        (JNIEnv *, jclass){
    bool flag = false;
    flag = SStart();
    return flag;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SStop
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SStop
        (JNIEnv *, jclass){
    bool flag = false;
    flag = SStop();
    return flag;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    ScanIsComplete
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_ScanIsComplete
        (JNIEnv *, jclass){
    bool flag = false;
    flag = ScanIsComplete();
    return flag;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    ScannerIsReady
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_ScannerIsReady
        (JNIEnv *, jclass){
    bool flag = false;
    flag = ScannerIsReady();
    return flag;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SGetOriginImageSize
 * Signature: (Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SGetOriginImageSize
        (JNIEnv *env, jclass, jobject width, jobject height, jobject bufsize){

    bool flag = false;
    jclass class_dpi = env->FindClass("java/lang/Integer");
    jfieldID id = env->GetFieldID(class_dpi, "value", "I");

    int w =0,h=0,size = 0;
    flag = SGetOriginImageSize(&w,&h,&size);

    env->SetIntField(width, id, w);
    env->SetIntField(height, id, h);
    env->SetIntField(bufsize, id, size);

    return flag;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SGetOriginImage
 * Signature: ([BLjava/lang/Integer;)I
 */
JNIEXPORT jint JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SGetOriginImage
        (JNIEnv *env, jclass, jbyteArray imageInfo, jobject bufferLength){
    char image[4096] = {0x00};
    int size = 0;
    int length =  SGetOriginImage(image, size);

    env->SetByteArrayRegion(imageInfo, 0, length, (jbyte*) image);

    jclass class_dpi = env->FindClass("java/lang/Integer");
    jfieldID id = env->GetFieldID(class_dpi, "value", "I");
    env->SetIntField(bufferLength, id, size);

    return  length;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SGetTicketInfo
 * Signature: ([BLjava/lang/Integer;)I
 */
JNIEXPORT jint JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SGetTicketInfo
        (JNIEnv *env, jclass, jbyteArray ticketinfo, jobject type){
    int returnValue = -1;
    unsigned char info[4096*3] = {0};
    memset(info, 0, sizeof(info));
    returnValue = SGetTicketInfo(info, 4096*3);
    LOGI("ticket length=%d,%s,%d\n",returnValue,info,info[1]);

    env->SetByteArrayRegion(ticketinfo,0,returnValue,(jbyte*)info);

    jclass class_mode = env->FindClass("java/lang/Integer");
    if(class_mode == NULL){
        return false;
    }
    jfieldID id = env->GetFieldID(class_mode, "value", "I");
    if(id == NULL){
        return false;
    }
    env->SetIntField(type, id, info[0]);

    return returnValue;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SPrintBrandImage
 * Signature: ([BIII)I
 */
JNIEXPORT jint JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SPrintBrandImage
        (JNIEnv *, jclass, jbyteArray, jint index, jint x, jint y){
    int result = SPrintBrandImage(NULL, index, x, y);
    return result;
}


bool getBmpData(char *fileName, unsigned char *bmpData)
{
    int i = 0;
    FILE *fp;

    fp = fopen(fileName, "r+");
    if (NULL == fp) {
        LOGI("not %s bmp file", fileName);
        return false;
    }
    while (!feof(fp))
    {
        fread(bmpData+i, sizeof(char), 1, fp);
        i++;
    }
    fclose(fp);

    return true;
}

JNIEXPORT jint JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SPrintSelfDefBrandImage
        (JNIEnv * env, jclass,jbyteArray jbyteArray1, jint xPos, jint yPos) {

    int ret = 0;
    char bmpPath[128];
    memset(bmpPath, 0, sizeof(bmpPath));
    sprintf(bmpPath, "/sdcard/BrandImage.bmp");

    char bmpData[1024 * 64];
    memset(bmpData, 0, sizeof(bmpData));

    if (getBmpData((char *) bmpPath, (unsigned char *) bmpData)) {
        LOGI("SPrintBrandImage =%d\n", sizeof(bmpData));
        ret = SPrintBrandImage(bmpData, 2, xPos, yPos);
        LOGI("SPrintBrandImage return =%d,xpos=%d,ypos=%d" ,ret,xPos,yPos );
    } else {
        LOGI("getBmpData fail");
    }

    return ret;
}



/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SRollBack
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SRollBack
        (JNIEnv *, jclass){
    bool flag = false;
    flag = SRollBack();
    return flag;
}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SRecognizeItem
 * Signature: (IIII[B[B)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SRecognizeItem
        (JNIEnv *env, jclass, jint, jint, jint, jint, jbyteArray, jbyteArray resultInfo){
    int x = 100, y = 100, width = 100, height = 100, bufsize = 0;
    char result[1024] = {0};
    SGetOriginImageSize(&width, &height, &bufsize);
    char image[bufsize];
    memset(image, 0, sizeof(image));
    SGetOriginImage(image, sizeof(image));
    bool flag = SRecognizeItem(x, y, width, height, image, result);
    env->SetByteArrayRegion(resultInfo,0, sizeof(result),(jbyte*)result);
    return flag;

}

/*
 * Class:     com_example_lederui_developmenttest_data_ScannerInterface
 * Method:    SAdjustSensibility
 * Signature: (Ljava/lang/Integer;Ljava/lang/Integer;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_ScannerInterface_SAdjustSensibility
        (JNIEnv *env, jclass, jobject currentSens, jobject adjustSens){
    bool flag = false;
    jclass class_dpi = env->FindClass("java/lang/Integer");
    jfieldID id = env->GetFieldID(class_dpi, "value", "I");

    int current =0,adjust=0;
    flag = SGetBrandDpi(&current, &adjust);

    env->SetIntField(currentSens, id, current);
    env->SetIntField(adjustSens, id, adjust);

    return flag;
}

#ifdef __cplusplus
}
#endif

