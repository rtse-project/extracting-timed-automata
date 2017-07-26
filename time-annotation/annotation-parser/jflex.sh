#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "Lexer"
java -Xmx128m -jar ${DIR}/lib/jflex-1.6.1.jar -d ${DIR}/../src/main/java/timeannotation/parser ${DIR}/lexer/calc.flex
