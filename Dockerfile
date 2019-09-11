FROM openjdk:8
ADD target/assignment-1.0.jar assignment-1.0.jar
ENTRYPOINT ["java","-jar","assignment-1.0.jar"]
