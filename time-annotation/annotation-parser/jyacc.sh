#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "Parser"
${DIR}/lib/yacc.macosx -J -Jpackage=timeannotation.parser ${DIR}/parser/calc.y
mv ${DIR}/*.java ${DIR}/../src/main/java/timeannotation/parser
