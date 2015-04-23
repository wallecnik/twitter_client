
#include <jni.h>
#include "HttpNative.h"
#include <curl/curl.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_cz_muni_fi_ib053_twitter_client_twitterapi_HttpNative_httpRequest (
    JNIEnv * env,
    jclass class,
    jstring method,
    jstring protocol,
    jstring path,
    jstring host,
    jstring user_agent,
    jstring authorization,
    jstring content
    ) {

    return 0;

}

#ifdef __cplusplus
}
#endif