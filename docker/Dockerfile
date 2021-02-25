FROM adoptopenjdk:11.0.6_10-jre-hotspot-bionic

RUN useradd -m poker
 
COPY --chown=poker:poker ./poker*.jar /opt/poker.jar

# Expose tomcat port
EXPOSE 8080

USER poker
ENTRYPOINT ["java", "-jar", "/opt/poker.jar"]