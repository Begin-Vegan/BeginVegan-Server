#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/spring-webapp.jar"

# 로그
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"
APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
TIME_NOW=$(date +%c)

CURRENT_PORT=$(cat /etc/nginx/conf.d/service-url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "$TIME_NOW > 현재 실행 중인 was 포트 : ${CURRENT_PORT}." >> $DEPLOY_LOG
echo "> Current port of running WAS is ${CURRENT_PORT}."

if [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
  TARGET_PORT=8081
else
  echo "$TIME_NOW > 현재 실행 중인 포트 없음" >> $DEPLOY_LOG
  echo "> No WAS is connected to nginx"
  TARGET_PORT=8081
fi

TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

if [ ! -z ${TARGET_PID} ]; then
  echo "$TIME_NOW > 포트 다운 : ${TARGET_PORT}." >> $DEPLOY_LOG
  echo "> Kill WAS running at ${TARGET_PORT}."
  sudo kill -9 ${TARGET_PID}
fi

# build 파일 복사
echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE

nohup java -jar -Dserver.port=${TARGET_PORT} $JAR_FILE > $APP_LOG 2> $ERROR_LOG &
echo "$TIME_NOW > 해당 포트에서 새로 실행 : ${TARGET_PORT}." >> $DEPLOY_LOG
echo "> Now new WAS runs at ${TARGET_PORT}."
exit 0