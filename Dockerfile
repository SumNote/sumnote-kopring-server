# 사용할 Java의 베이스 이미지 선택
FROM openjdk:17-slim

# 애플리케이션 파일 복사
COPY app.jar /app.jar

# 실행할 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]