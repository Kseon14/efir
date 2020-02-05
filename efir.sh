#!/bin/sh

SERVICE_NAME=efir
PATH_TO_JAR=/opt/$SERVICE_NAME/backend-0.0.1-SNAPSHOT.jar
JAVA_PATH=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/bin/java

$JAVA_PATH -jar $PATH_TO_JAR
