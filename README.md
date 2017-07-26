# JAVA-XAL 
[![build status](http://rtse-isys.aau.at/giovanni.liva/java-xal/badges/development/build.svg)](http://rtse-isys.aau.at/giovanni.liva/java-xal/commits/development)
[![coverage report](http://rtse-isys.aau.at/giovanni.liva/java-xal/badges/development/coverage.svg)](http://rtse-isys.aau.at/giovanni.liva/java-xal/commits/development)

# How to install
In order to package the project call 
```bash
mvn clean package
```

If you'd like to skip the test packege it with:
```bash
mvn clean package -DskipTests
```

# Known Issues


# Current Branch

The following branch will use to test new features.
When they are ready and without knowing issue, a merge request can be created.

#Extract all Classes and methods form Jar
```bash
find . | grep ".class" | xargs javap -p > _classes.txt
```




