FROM openjdk:11-slim
RUN mkdir -p /opt/myapp
COPY target/ignite-kubernetes-essentials-training-1.0-SNAPSHOT.jar /opt/myapp
WORKDIR /opt/myapp
CMD java -jar ignite-kubernetes-essentials-training-1.0-SNAPSHOT.jar
