#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
NAME=UndefinedTimeBehaviour

#clean run
rm -f traces.txt
rm -fR logs/
cd ../../../../
mvn clean package -DskipTests=true
cd ${DIR}

#copy
cp ${DIR}/../java/${NAME}.java ./${NAME}.java
cp ${DIR}/../../../target/code-instrumentation*.jar ./code-instrumentation.jar
#recompile
javac -g ${NAME}.java
#exec with agent
java -javaagent:${DIR}/code-instrumentation.jar=${DIR}/constraint.conf ${NAME}


