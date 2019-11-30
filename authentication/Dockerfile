FROM adoptopenjdk:8-hotspot
#RUN apt update && apt install -y vim

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ARG JAR_FILE=target/authentication-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} /app/app.jar
#COPY target/lib/ /app/
#COPY target/resources /app/

ARG SERVER_PORT=8080
ENV SERVER_PORT="${SERVER_PORT}"

ARG SPRING_PROFILE=k8s
ENV SPRING_PROFILE="${SPRING_PROFILE}"
ENTRYPOINT ["java",\
                    "-Dloader.path=/app/resources",\
                    "-Djava.security.egd=file:/dev/./urandom",\
                    "-jar","/app/app.jar",\
                    "--server.port=${SERVER_PORT}",\
                    "--spring.profiles.active=${SPRING_PROFILE}"\
            ]
