# ============================================
# Stage 1: Maven Build
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# 先复制 POM 文件，利用 Docker 缓存加速依赖下载
COPY pom.xml .
COPY learning-common/pom.xml learning-common/
COPY learning-model/pom.xml learning-model/
COPY learning-service/pom.xml learning-service/
COPY learning-admin/pom.xml learning-admin/

RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY . .
RUN mvn clean package -DskipTests -B

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:17-jre

LABEL maintainer="polikm"
LABEL description="学情数据统计分析平台 - 后端服务"

WORKDIR /app

# 从构建阶段复制 JAR
COPY --from=builder /build/learning-admin/target/learning-admin-1.0.0-SNAPSHOT.jar app.jar

# 创建上传文件目录
RUN mkdir -p /app/uploads

EXPOSE 8080

# JVM 参数优化
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=docker"]
