
#include <jni.h>
#include "cz_muni_fi_ib053_twitter_client_twitterapi_HttpNative.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_cz_muni_fi_ib053_twitter_client_twitterapi_HttpNative_hello
    (JNIEnv *env, jclass class) {

        return (*env)->NewStringUTF(env, "Hello from HttpNative");

    }

#ifdef __cplusplus
}
#endif
#endif
