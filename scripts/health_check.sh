#!/usr/bin/env bash

# Crawl current connected port of WAS
CURRENT_PORT=$(cat /etc/nginx/conf.d/service-url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

PROJECT_ROOT="/home/ubuntu/app"

#로그
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"
TIME_NOW=$(date +%c)

# Toggle port Number
if [ ${CURRENT_PORT} -eq 8081 ]; then
    TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
    TARGET_PORT=8081
else
    echo "$TIME_NOW > 현재 실행 중인 포트 없음" >> $DEPLOY_LOG
    echo "> No WAS is connected to nginx"
    TARGET_PORT=8081
    exit 1
fi

echo "$TIME_NOW > health check 시작 'http://127.0.0.1:${TARGET_PORT}'에서 ..." >> $DEPLOY_LOG
echo "> Start health check of WAS at 'http://127.0.0.1:${TARGET_PORT}' ..."

for RETRY_COUNT in 1 2 3 4 5 6 7 8 9 10
do
    echo "> #${RETRY_COUNT} trying..."
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/ping) # Nginx와 연결되어 있는 스프링 부트가 정상 작동 중인지 확인

    if [ ${RESPONSE_CODE} -eq 200 ]; then
        echo "$TIME_NOW > Health check 성공" >> $DEPLOY_LOG
        echo "> New WAS successfully running"
        exit 0
    elif [ ${RETRY_COUNT} -eq 10 ]; then
        echo "$TIME_NOW > Health check 실패" >> $DEPLOY_LOG
        echo "> Health check failed."
        exit 1
    fi
    sleep 10
done