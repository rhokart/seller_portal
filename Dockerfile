FROM openjdk:17-alpine
EXPOSE 8090
COPY target/rhokart-seller-portal-0.0.1-SNAPSHOT.jar seller_portal.jar
ENTRYPOINT ["java","-jar","seller_portal.jar"]
