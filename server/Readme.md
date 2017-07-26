# REST URLs
Default PORT: **9000**

Default IP: **127.0.0.1**

# Debug Mode   
If the server is lunched with the flag `-debug` it will print for each request it handles, a message of the selected route with its parameters.


| URL        | METHOD | Output        | Description  |
|------------|:------:|:-------------:| :------------|
| /                 | GET  | txt  | Check the connection with the server. |
| /getAllFiles      | POST | json | Return all the java file |
| /getFile          | POST | json | Return the IM of a file |
| /openProject      | POST | json | Calculate indexes of the project |
| /getFilesByType   | POST | json | Return the list of files that extends/implements the given type |
| /getThreads       | POST | json | Return the list of files that implements threads |
| /getMains         | POST | json | Return the list of files which contains a public void main |
| /getStatus        | POST | json | Return the status of the selected project |
| /clean            | POST | json | Remove a database |
| /cleanAll         | POST | json | Remove all databases |



# /getAllFiles

The route returns the list of all *java* files in the given project.

It expects two parameters: 
* `name` : project name
* `skipTest` : if equal to **1** it skips test files

The standard output is in `JSON`, to change to `YAML` set the parameter `format` to `yaml`: 
The output is a relative path with regarding of the project path.

e.g. 
```bash
curl -s http://localhost:9000/getFile -d 'format=yaml&filePath=file:///Users/giovanni/repository/java-xal/server/src/test/resources/progs/Attempt1.java'
```

# /getFile 
It returns the intermediate model in json format of the given file.

It expects one parameter: 
* `filePath`: Relative path (of the project path) of the java file
 
`filePath` supports only *file://* as URI protocol atm.

The output is a relative path with regarding of the project path.

The standard output is in `JSON`, to change to `YAML` set the parameter `format` to `yaml`: 

e.g. 
```bash
curl -s http://localhost:9000/getFile -d 'format=yaml&filePath=file:///Users/giovanni/repository/java-xal/server/src/test/resources/progs/Attempt1.java'
```

# /openProject

The following REST API creates a db with the types of the project. 
If we do not use this call we cannot create a proper **getThreads()** and **..()** functions. 
Functions that requires infos about types will return an error 406 instead of 400 if the project is not currently in the db. 
Process the project could take times, therefore the function only offer an eventually consistency. 
Users can check the ending of the indexing phases with the **isProjectOpen()** function.

It expects two parameters: 
* `name` : Name of the project
* `path` : Path of the project in URI format (only the *file://* protocol is supported atm.)

**HOWEVER** `path` can be optional if a project was already opened. The program will use the previously used one.
If a project was already opened and we specify the `path` parameter we use the value of the parameter overwriting previously values.

One optional parameter can be used:
* `invalidCache` : if equal to **1** it invalidates the cache and compute again the indexes. Default value is `0`.

The function returns a value with a status code:
* `0` : The path exists and the process started correctly
* `1` : There was an error, look `description` to understand which error was

`description` is a text field that shows an error message.


# /getFilesByType

Return the list of files in which each contains a class that extends or implements the given searched type.

It expects two parameters:
* `name` : Name of the project
* `type` : Type to use as filter

The output is a list of a structured data with the following format:
* `path` : Path of the file which contains the type relative to the project path
* `className` : name of the class that extends/implements the given type
* `packageName` : name of the package of the class that extends/implements the given type

A file can contains multiple classes. Therefore, to find the correct class users should use `packageName` and `className`.

The standard output is in `JSON`, to change to `YAML` set the parameter `format` to `yaml`.

e.g. 
```bash
curl -s http://localhost:9000/getFile -d 'format=yaml&filePath=file:///Users/giovanni/repository/java-xal/server/src/test/resources/progs/Attempt1.java'
```
# /getThreads

Return the list of files in which each contains a class that defines a Thread.

It expects one parameter:
* `name` : Name of the project

The output is a list of a structured data with the following format:
* `path` : Path of the file which contains the type relative to the project path
* `className` : name of the class that extends/implements the Thread Java API
* `packageName` : name of the package of the class that extends/implements the Thread Java API

A file can contains multiple classes. Therefore, to find the correct class users should use `packageName` and `className`.

# /getMains

Return the list of files in which each contains a class that defines a public static void main method.

It expects one parameter:
* `name` : Name of the project

The output is a list of a structured data with the following format:
* `path` : Path of the file which contains the type relative to the project path
* `className` : name of the class that extends/implements the Thread Java API
* `packageName` : name of the package of the class that extends/implements the Thread Java API

A file can contains multiple classes. Therefore, to find the correct class users should use `packageName` and `className`.

The standard output is in `JSON`, to change to `YAML` set the parameter `format` to `yaml`

e.g. 
```bash
curl -s http://localhost:9000/getFile -d 'format=yaml&filePath=file:///Users/giovanni/repository/java-xal/server/src/test/resources/progs/Attempt1.java'
```

# /getStatus

The route returns the status of the given project. 

It expects one parameter: 
* `name` : Name of the project

The return value is a status message:
* `open`    : The project has been opened correctly and the indexing phase ends
* `opening` : The indexes are currently on computing
* `closed`  : The indexes have never been created
* `error`   : The index procedure had an error. See the `description` field for the error log.

The standard output is in `JSON`, to change to `YAML` set the parameter `format` to `yaml`: 

e.g. 
```bash
curl -s http://localhost:9000/getStatus -d 'format=yaml&name=test'
```

# /clean

Given a project name, it clears every information stored in the database for that project.

It expects one parameter: 
* `name` : Name of the project

Return in output a status description
* `status`    : It is zero if everything was ok, greater than that if an error occurs
* `description` : In case of error, the description of the message

The standard output is in `JSON`, to change to `YAML` set the parameter `format` to `yaml`

# /cleanAll

Removes every information stored in the database. 

Return in output a status description
* `status`    : It is zero if everything was ok, greater than that if an error occurs
* `description` : In case of error, the description of the message

The standard output is in `JSON`, to change to `YAML` set the parameter `format` to `yaml`



