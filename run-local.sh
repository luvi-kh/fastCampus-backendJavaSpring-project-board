#!/bin/bash

# 1. 빌드된 jar 파일 경로 명시 (실제 생성된 파일명과 일치해야 합니다)
JAR_FILE="./build/libs/project-board-v1.1.jar"

# 2. 현재 실행 중인 Docker MySQL 컨테이너 정보에 맞춰 환경 변수 주입
export LOCAL_DB_URL="jdbc:mysql://localhost:3306/board?allowPublicKeyRetrieval=true&useSSL=false"
export LOCAL_DB_USERNAME="uno"
export LOCAL_DB_PASSWORD="uno"

# 3. JAR 파일 존재 여부 확인 후 구동
if [ -f "$JAR_FILE" ]; then
    echo "🚀 Docker MySQL(포트: 3306)에 연결하여 스프링 부트를 실행합니다..."
    java -jar $JAR_FILE
else
    echo "❌ 에러: $JAR_FILE 파일을 찾을 수 없습니다."
    echo "💡 먼저 터미널에 './gradlew clean build -x test'를 입력해 빌드를 완료해 주세요."
fi
