# How to install
In order to package the project call (it requires mongodb running with default settings)
```bash
mvn clean package
```

If you'd like to skip the test with:
```bash
mvn clean package -DskipTests
```

# How to extract automata
After the packaging, the jar can be found in the `./usage/target/` folder.
Run the command 
```bash 
java -cp usage-0.5.jar automata.ExtractModelAndCnst file class method automata constraints project
```
Where:
* `file` is the path of the file to analyze
* `class` is the name of the class to analyze
* `method` is the name of the method to analyze
* `automata` is the path of the file where to store the automata model
* `constraints` is the path of the file where to store the list of expressions to monitor with the dynamic analysis
* `project` is the root path of the project that contains the chosen `file`

# How to extract traces
Run your java program attaching the agent found in the `./code-instrumentation/target/` folder. 
As parameter to the agent, pass the file containing the expressions to monitor created with the previous command.
```bash
java -javaagent:code-instrumentation-0.1.jar=path/to/constraint.conf yourProgram.jar
```
It will creates a file with the name `traces[0-9]+.txt` in the working directory of where you start the command. 

# How to combine automata and traces
After the packaging, the jar can be found in the `./usage/target/` folder.
Run the command 
```bash 
java -cp usage-0.5.jar automata.ExampleCombineTraces automata traces outputDir
```
Where:
* `automata` is the path of the file which contains the automaton model
* `traces`  is the path of the file which is created with the dynamic analysis
* `outputDir` is the path of the directory in which store the results


