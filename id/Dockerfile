FROM adoptopenjdk:8-hotspot
#RUN apt update && apt install -y vim

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ARG JAR_FILE=target/id-*.jar
COPY ${JAR_FILE} /app/app.jar
#COPY target/lib/ /app/
#COPY target/resources /app/

ARG SERVER_PORT=8080
ENV SERVER_PORT="${SERVER_PORT}"

ARG NODE_ID=0
ENV NODE_ID="${NODE_ID}"

ARG SPRING_PROFILE=k8s
ENV SPRING_PROFILE="${SPRING_PROFILE}"
ENTRYPOINT ["java",\
                    "-Djava.security.egd=file:/dev/./urandom",\
                    "-jar","/app/app.jar",\
                    "--node.id=${NODE_ID}",\
                    "--server.port=${SERVER_PORT}",\
                    "--spring.profiles.active=${SPRING_PROFILE}"\
            ]
