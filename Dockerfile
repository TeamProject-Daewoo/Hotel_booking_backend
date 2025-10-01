# 베이스 이미지로 Java 17 버전을 사용
FROM eclipse-temurin:17-jdk-slim

RUN apt-get update && apt-get install -y curl

# JAR 파일이 위치할 디렉토리 지정 (Maven 기준)
ARG JAR_FILE=target/*.jar

# 위에서 지정한 JAR 파일을 app.jar 라는 이름으로 컨테이너에 복사
COPY ${JAR_FILE} app.jar

# 컨테이너가 시작될 때 실행할 명령어
ENTRYPOINT ["java","-jar","/app.jar"]
