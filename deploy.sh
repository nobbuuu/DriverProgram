#!/bin/bash

DEPLOY_USER="xx"
DEPLOY_PASS="123456"
DEPLOY_TOKEN="=====token====="

LOCAL_APK_PATH='app/build/outputs/apk'
LOCAL_APK_VERSION=`egrep versionCode build.gradle | sed -e 's/.*: \(.*\),.*/\1/'`

API_ENDPOINT_DEV='https://mt-dev.haylion.cn/manage/'
API_ENDPOINT_TEST='https://mt-test.haylion.cn/manage/'
API_ENDPOINT_PROD='https://mt.haylion.cn/manage/'

LOGIN_API='login'
GET_APPS_API='member-app/list'
UPLOAD_APK_API='upload/apk'
SUBMIT_API='driver-app/add'

ANDROID_APP_TYPE=1

LOGIN_API_PAYLOAD='login.json'
SUBMIT_API_PAYLOAD='submit.json'

JQ_PROG="./jq-linux32"

##############################################
extract_json(){
    local json="$1"
    local filter="$2"
    echo "$json" | "$JQ_PROG" -r "$filter"
}

array_length(){
    local array="$1"
    echo "$array" | "$JQ_PROG" "length"
}

array_element(){
    local array="$1"
    local key="$2"
    local value="$3"
    echo "$array" | "$JQ_PROG" ".[] | select(.$key == $value)"
}

print_json(){
    local json="$1"
    echo "$json" | "$JQ_PROG" "."
}

print_red(){
    printf '\e[1;31m%s\e[0m\n' "$1"
}

print_green(){
    printf '\e[1;32m%s\e[0m\n' "$1"
}

print_yellow(){
    printf '\e[1;33m%s\e[0m\n' "$1"
}
##############################################

prepare_login(){
    local password=`echo -n $DEPLOY_PASS | md5sum | cut -d ' ' -f1`
    echo "{\"username\":\"$DEPLOY_USER\",\"password\":\"$password\"}" | tee $LOGIN_API_PAYLOAD
}

do_login(){
    curl -sS -X POST -H "Content-Type: application/json;charset=UTF-8" -d @$LOGIN_API_PAYLOAD $LOGIN_API
}

get_apps(){
    curl -sS -X GET -H "token: $DEPLOY_TOKEN" $GET_APPS_API
}

do_upload(){
    curl -sS -X POST -H "token: $DEPLOY_TOKEN" -F "file=@$1" $UPLOAD_APK_API
}

prepare_submit(){
    local type=$ANDROID_APP_TYPE
    local oldest=`egrep -m 1 'oldest_version' CHANGELOG | awk -F= '{print $2}'`
    local latest=$LOCAL_APK_VERSION
    local latestDownloadUrl=$1
    local updateMessage=`egrep -m 1 'update_message' CHANGELOG | awk -F= '{print $2}'`

    local forceUpdateMessage=`egrep -m 1 'force_message' CHANGELOG | awk -F= '{print $2}'`
    if [[ -z "$forceUpdateMessage" ]]; then
       forceUpdateMessage=$updateMessage
    fi

    echo "{\"type\":$type,\"latest\":$latest,\"oldest\":$oldest,\"updateMessage\":\"$updateMessage\",\"latestDownloadUrl\":\"$latestDownloadUrl\",\"forceUpdateMessage\":\"$forceUpdateMessage\"}" | tee $SUBMIT_API_PAYLOAD
}

do_submit(){
    curl -sS -X POST -H "token: $DEPLOY_TOKEN" -H "Content-Type: application/json;charset=UTF-8" -d @$SUBMIT_API_PAYLOAD $SUBMIT_API
}


case "$1" in
   'deploy:dev')
     print_green "开发环境"
     LOCAL_APK_PATH+='/debug/maas-taxi-driver-debug.apk'
     LOGIN_API="$API_ENDPOINT_DEV$LOGIN_API"
     GET_APPS_API="$API_ENDPOINT_DEV$GET_APPS_API"
     UPLOAD_APK_API="$API_ENDPOINT_DEV$UPLOAD_APK_API"
     SUBMIT_API="$API_ENDPOINT_DEV$SUBMIT_API"
     ;;
   'deploy:test')
     print_green "测试环境"
     LOCAL_APK_PATH+='/beta/maas-taxi-driver-beta.apk'
     LOGIN_API="$API_ENDPOINT_TEST$LOGIN_API"
     GET_APPS_API="$API_ENDPOINT_TEST$GET_APPS_API"
     UPLOAD_APK_API="$API_ENDPOINT_TEST$UPLOAD_APK_API"
     SUBMIT_API="$API_ENDPOINT_TEST$SUBMIT_API"
     ;;
   'deploy:prod')
     print_green "生产环境"
     LOCAL_APK_PATH+='/release/maas-taxi-driver-release.apk'
     LOGIN_API="$API_ENDPOINT_PROD$LOGIN_API"
     GET_APPS_API="$API_ENDPOINT_PROD$GET_APPS_API"
     UPLOAD_APK_API="$API_ENDPOINT_PROD$UPLOAD_APK_API"
     SUBMIT_API="$API_ENDPOINT_PROD$SUBMIT_API"
     ;;
   *)
     echo "Usage: $0 {deploy:dev|deploy:test|deploy:prod}"
     exit 1
     ;;
esac


prepare_login
print_green "准备登录……"
LOGIN_RESULT=$(do_login)
print_json "$LOGIN_RESULT"
RESULT_CODE=$(extract_json "$LOGIN_RESULT" '.code')
if [ "$RESULT_CODE" = "200" ]; then
    print_green "登录成功"
    DEPLOY_TOKEN=$(extract_json "$LOGIN_RESULT" '.data.token')
else
    ERROR_MESSAGE=$(extract_json "$LOGIN_RESULT" '.message');
    print_red "登录失败：$ERROR_MESSAGE"
    exit 1
fi

APPS_RESULT=$(get_apps)
print_json "$APPS_RESULT"
RESULT_CODE=$(extract_json "$APPS_RESULT" '.code')
if [ "$RESULT_CODE" = "200" ]; then
    remote_apk_version=$(extract_json "$APPS_RESULT" ".data[] | select(.type == $ANDROID_APP_TYPE) | .latest")
    if [[ -z "$remote_apk_version" ]]; then
        print_yellow "尚未添加Android应用版本信息"
    else
        if [ $LOCAL_APK_VERSION -le $remote_apk_version ]; then
            print_red "请修改versionCode，并确保大于$remote_apk_version"
            exit 1
        else
            print_green "获取版本信息成功"
        fi
    fi
else
    ERROR_MESSAGE=$(extract_json "$APPS_RESULT" '.message');
    print_red "获取版本信息失败：$ERROR_MESSAGE"
    exit 1
fi

UPLOAD_RESULT=$(do_upload $LOCAL_APK_PATH)
print_json "$UPLOAD_RESULT"
RESULT_CODE=$(extract_json "$UPLOAD_RESULT" '.code')
if [ "$RESULT_CODE" = "200" ]; then
    print_green "APK上传成功"
    APK_DOWNLOAD_URL=$(extract_json "$UPLOAD_RESULT" '.data.url')
    prepare_submit $APK_DOWNLOAD_URL
    print_green "准备提交新版本……"
    SUBMIT_RESULT=$(do_submit)
    print_json "$SUBMIT_RESULT"
    RESULT_CODE=$(extract_json "$SUBMIT_RESULT" '.code')
    if [ "$RESULT_CODE" = "200" ]; then
        print_green "APK更新提交成功"
    else
        ERROR_MESSAGE=$(extract_json "$SUBMIT_RESULT" '.message');
        print_red "APK更新提交失败：$ERROR_MESSAGE"
        exit 1
    fi
else
    ERROR_MESSAGE=$(extract_json "$UPLOAD_RESULT" '.message');
    print_red "APK上传失败：$ERROR_MESSAGE"
    exit 1
fi

exit 0
