FROM openjdk:8

COPY target/hazelcast.jar /opt/hazelcast.jar

ENV LANG ru_RU.UTF-8
ENV LANGUAGE ru_RU:ru
ENV LC_ALL ru_RU.UTF-8

ENTRYPOINT ["java",  "-Xmx4g", "-Drun.jvmArguments=", "-Dfile.encoding=UTF-8", "-jar", "/opt/hazelcast.jar"]