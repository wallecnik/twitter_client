
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

const_old_string HttpNative_httpRequest(const_old_string method, const_old_string protocol, const_old_string jpath, const_old_string host, const_old_string userAgent, const_old_string autorization, const_old_string content);

#ifdef __cplusplus
extern "C" {
#endif




/*
 *java to c transfer function
 * 
 * --log 1: add verification if attributes are not null
 * to do: change behavior of some cases when 
 */
JNIEXPORT jstring JNICALL Java_cz_muni_fi_ib053_twitter_client_twitterapi_HttpNative_httpRequest
  (JNIEnv *env, jclass obj, jstring jstr_method, jstring jstr_protocol, jstring jstr_path, jstring jstr_host, jstring jstr_userAgent, jstring jstr_authorization, jstring jstr_content){
    if(env == NULL){
        //in future change to throwing exception
        return env->NewStringUTF("bed call of function");
    }
    
    if(jstr_method == NULL){
        //in future change to throwing exception
        return env->NewStringUTF("java string method is null");
    }
    if(jstr_protocol == NULL){
        //in future change to throwing exception
        return env->NewStringUTF("java string protocol is null");
    }
    if(jstr_path == NULL){
        //in future change to throwing exception
        return env->NewStringUTF("java string patj is null");
    }
    if(jstr_host == NULL){
        //in future change to throwing exception
        return env->NewStringUTF("java string host is null");
    }
    if(jstr_userAgent == NULL){
        //in future change to throwing exception
        return env->NewStringUTF("java string userAgent is null");
    }
    if(jstr_authorization == NULL){
        //in future change to throwing exception
        return env->NewStringUTF("java string autorization is null");
    }
    if(jstr_content == NULL){
        //in future change to throwing exception
        return env->NewStringUTF("java string content is null");
    }

    //TO DO: change reaction on null input

    
    const char* str_method           = env->GetStringUTFChars(jstr_method, 0);
    const char* str_protocol         = env->GetStringUTFChars(jstr_protocol, 0);
    const char* str_path             = env->GetStringUTFChars(jstr_path, 0);
    const char* str_host             = env->GetStringUTFChars(jstr_host, 0);
    const char* str_userAgent        = env->GetStringUTFChars(jstr_userAgent, 0);
    const char* str_authorization     = env->GetStringUTFChars(jstr_authorization, 0);
    const char* str_content          = env->GetStringUTFChars(jstr_content, 0);


    const_old_string ret_val = HttpNative_httpRequest(str_method, str_protocol, str_path, str_host, str_userAgent, str_authorization, str_content);

    env->ReleaseStringUTFChars(jstr_method, str_method);
    env->ReleaseStringUTFChars(jstr_protocol, str_protocol);
    env->ReleaseStringUTFChars(jstr_path, str_path);
    env->ReleaseStringUTFChars(jstr_host, str_host);
    env->ReleaseStringUTFChars(jstr_userAgent, str_userAgent);
    env->ReleaseStringUTFChars(jstr_authorization, str_authorization);
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
 * c++ method for calling http request, none of strings shouldn't be NULL 
 * return message that was requested
 */
const_old_string HttpNative_httpRequest(const_old_string str_method, const_old_string str_protocol, const_old_string str_path, const_old_string str_host, const_old_string str_userAgent, const_old_string str_authorization, const_old_string str_content){

    CURL * curl;
    CURLcode res;
    std::string ret_val;

    curl_global_init(CURL_GLOBAL_DEFAULT);
    curl = curl_easy_init();
    
    //create url
    std::string str_url;
    str_url.clear();
    str_url.append("https://");
    str_url.append(str_host);
    str_url.append(str_path);
    
    //create headders 
    struct curl_slist *headers = NULL;
                    //???maybe application/json send as parameter???


    headers = curl_slist_append(headers, "Accept: application/json");
    headers = curl_slist_append(headers, "Content-Type: application/x-www-form-urlencoded; charset=UTF-8.");
    //headers = curl_slist_append(headers, "Content-Length: 29");
    headers = curl_slist_append(headers, "Accept-Encoding: application/json");

    std::string curl_authorization = "Authorization: ";
    curl_authorization.append(str_authorization);
    headers = curl_slist_append(headers, curl_authorization.c_str());

    //std::string curl_host = "Host: ";
    //curl_host.append(str_host);
    //headers = curl_slist_append(headers, curl_host.c_str());

    //std::string curl_user_agent = "User-Agent: ";
    //curl_user_agent.append(str_userAgent);
    //headers = curl_slist_append(headers, curl_user_agent.c_str());

    if(curl) {
        //set url
        curl_easy_setopt(curl, CURLOPT_URL, str_url.c_str());

        //set host and peer verification
        //curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0);
        //curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, true);

        //set headers
        //curl_easy_setopt(curl, CURLOPT_USERAGENT, str_userAgent);
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

        //set werification function and return variable
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, save_output);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &ret_val);
                
        //set protocol, default value is set automatically by curl if not one of bellow
        if(str_protocol == "HTTP/1.0"){
            curl_easy_setopt(curl, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_1_0);
        }else if(str_protocol == "HTTP/1.1"){
            curl_easy_setopt(curl, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_1_1);
        }

        //set method
        if(!strcmp(str_method, "POST")){
            //curl_easy_setopt(curl, CURLOPT_POSTFIELDS, "grant_type=client_credentials");
            curl_easy_setopt(curl, CURLOPT_POST, 1);
        }
        if(!strcmp(str_method, "GET")){
            curl_easy_setopt(curl, CURLOPT_HTTPGET, 1);
        }

        if(strlen(str_content) > 0){
            curl_easy_setopt(curl, CURLOPT_COPYPOSTFIELDS, str_content);
        }

        res = curl_easy_perform(curl);
        
        /* Check for errors */
        if(res != CURLE_OK)  {
           std::cout << "curl_easy_perform() failed: " <<  curl_easy_strerror(res) << std::endl;
        }
        
        curl_easy_cleanup(curl);
    }
    
    curl_slist_free_all(headers);
    curl_global_cleanup();
    
    
    return ret_val.c_str();
}



