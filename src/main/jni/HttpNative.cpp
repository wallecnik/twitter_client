
#include <jni.h>
//#include "../../../../../../java/jdk1.8.0_40/include/jni.h"
#include "HttpNative.h"
#include <curl/curl.h>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <string>
#include <iostream>

typedef char* old_string;
typedef const char* const_old_string;

const_old_string HttpNative_httpRequest(const_old_string method, const_old_string protocol, const_old_string jpath, const_old_string host, const_old_string usetAgent, const_old_string autorization, const_old_string content);

#ifdef __cplusplus
extern "C" {
#endif





JNIEXPORT jstring JNICALL Java_cz_muni_fi_ib053_twitter_client_twitterapi_HttpNative_httpRequest
  (JNIEnv *env, jclass obj, jstring jstr_method, jstring jstr_protocol, jstring jstr_path, jstring jstr_host, jstring jstr_usetAgent, jstring jstr_autorization, jstring jstr_content){
    if(env == NULL){
        //in future change to throwing exception
        return env->NewStringUTF("bed call of function");
    }
    const char* str_method           = env->GetStringUTFChars(jstr_method, 0);
    const char* str_protocol         = env->GetStringUTFChars(jstr_protocol, 0);
    const char* str_path             = env->GetStringUTFChars(jstr_path, 0);
    const char* str_host             = env->GetStringUTFChars(jstr_host, 0);
    const char* str_usetAgent        = env->GetStringUTFChars(jstr_usetAgent, 0);
    const char* str_autorization     = env->GetStringUTFChars(jstr_autorization, 0);
    const char* str_content          = env->GetStringUTFChars(jstr_content, 0);

    //TO DO: check if input is not null

    const_old_string ret_val = HttpNative_httpRequest(str_method, str_protocol, str_path, str_host, str_usetAgent, str_autorization, str_content);

    env->ReleaseStringUTFChars(jstr_method, str_method);
    env->ReleaseStringUTFChars(jstr_protocol, str_protocol);
    env->ReleaseStringUTFChars(jstr_path, str_path);
    env->ReleaseStringUTFChars(jstr_host, str_host);
    env->ReleaseStringUTFChars(jstr_usetAgent, str_usetAgent);
    env->ReleaseStringUTFChars(jstr_autorization, str_autorization);
    env->ReleaseStringUTFChars(jstr_content, str_content);

    return env->NewStringUTF(ret_val);

}

#ifdef __cplusplus
}
#endif


/*
 * function to save data from requested url as string
 * format is requested from curl library, parameters have same syntax as fwrite()
 */
size_t save_output(void *buffer, size_t size, size_t nmemb, std::string* msg_to_save){
    msg_to_save->clear();
    msg_to_save->append((char*)buffer, size * nmemb);
    return size * nmemb;
}

/*TO DO: add throwing exception
 *
 * method for calling http request, none of strings shouldn't be NULL 
 * return message that was requested
 */
const_old_string HttpNative_httpRequest(const_old_string str_method, const_old_string str_protocol, const_old_string str_path, const_old_string str_host, const_old_string str_usetAgent, const_old_string str_autorization, const_old_string str_content){
    CURL * curl;
    CURLcode res;
    std::string ret_val;

    std::string str_url;
    str_url.clear();
    str_url.append(str_host);
    str_url.append(str_path);
    //curl_httppost a ;
    curl = curl_easy_init();
    if(curl) {
        curl_easy_setopt(curl, CURLOPT_URL, str_url.c_str());
        //curl_easy_setopt(curl, CURLOPT_HTTPPOST, );
        /* example.com is redirected, so we tell libcurl to follow redirection */

        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, save_output);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &ret_val);
        /* Perform the request, res will get the return code */

        res = curl_easy_perform(curl);
        /* Check for errors */
        if(res != CURLE_OK)  {
           std::cout << "curl_easy_perform() failed: " <<  curl_easy_strerror(res) << std::endl;
        }
        /* do something */
        /* always cleanup */
        curl_easy_cleanup(curl);
    }
    return ret_val.c_str();
}



