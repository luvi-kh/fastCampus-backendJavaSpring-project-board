#!/bin/bash

echo "🛑 1. 기존 가동 중인 자바 프로세스가 있다면 안전하게 종료합니다..."
pkill -f 'java -jar' 2>/dev/null

echo "🧹 2. 혹시 모를 구버전 JAR 파일과 컴파일 캐시를 완전히 청소(Clean)합니다..."
./gradlew clean

echo "📦 3. 새롭게 수정된 자바 코드를 바탕으로 최신 JAR 파일을 새로 빌드합니다..."
./gradlew build -x test

# 빌드 폴더 내부에 생성된 정확한 jar 파일명을 동적으로 찾아냅니다.
JAR_FILE=$(ls build/libs/*.jar | grep -v 'plain' | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "❌ 에러: 빌드된 .jar 파일을 찾을 수 없습니다. 자바 코드가 정상인지 확인해 주세요."
    exit 1
fi

echo "🎯 발견된 최신 실행 파일: $JAR_FILE"

# 4. Docker MySQL 연결을 위한 환경 변수 세팅
export LOCAL_DB_URL="jdbc:mysql://localhost:3306/board?allowPublicKeyRetrieval=true&useSSL=false"
export LOCAL_DB_USERNAME="uno"
export LOCAL_DB_PASSWORD="uno"

# 5. 카카오 로그인 및 베이스 URL 환경 변수 세팅
# (※ 주의: 아래 따옴표 안에 실제 카카오 개발자 센터의 키 값들을 넣어주세요!)
export KAKAO_OAUTH_CLIENT_ID=""
export KAKAO_OAUTH_CLIENT_SECRET=""
export BASE_URL="http://localhost:8080"
export BASEURL="http://localhost:8080"

echo "🚀 6. 모든 최신 환경 변수를 주입하여 스프링 부트를 정직하게 실행합니다!"
echo "------------------------------------------------------------------------"
java -jar "$JAR_FILE"

