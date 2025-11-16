FROM corretto-24

VOLUME /tmp

EXPOSE 8181

COPY target/*.jar cloud-storage-back-diploma-app.jar

ADD src/main/resources/application.properties src/main/resources/application.properties

ENTRYPOINT ["java", "-jar", "/cloud-storage-back-diploma-app.jar"]