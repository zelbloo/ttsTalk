#!/usr/bin/env sh

export GRADLE_USER_HOME="$HOME/.gradle"

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS="-Xmx2048m -Dfile.encoding=UTF-8"

# Determine the path to the gradle-wrapper.jar file
DIRNAME=$(dirname "$0")
JARFILE="$DIRNAME/gradle/wrapper/gradle-wrapper.jar"

# Execute the Gradle wrapper
java $DEFAULT_JVM_OPTS -jar "$JARFILE" "$@"