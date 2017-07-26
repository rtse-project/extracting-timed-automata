# Java8 Parser w/ ANTLR4.2 - JavaParser - JDT

The different parsers with the benefits and drawback are explained in [this wiki page](https://rtse-isys.aau.at/giovanni.liva/java-parser/wikis/home)

# Test Classes

Here the list of the classes with the feature tested.

## Antlr4Mojo.java 
It has annotation with parameters.


## Calculator.java
It defines some lambda expressions. It was taken form the Sun tutorial.

## HeavyParsing.java
Copy/Paste twice of the source in LocalDiscovery. It serves to check the performance.

## HelloWord.java
Simple hello world

## LocalDiscovery.java
Class taken form the elastic search project [on github repository](https://github.com/elastic/elasticsearch/tree/master/core/src/main/java/org/elasticsearch/discovery).
It is normal java code but taken from a real project.

## OnlyMethod.java
Class with just one method without the class declaration. It is supposed to test the parse error recovery of ANTLR

## PredictionModule.java
A huge class to test its performances. Moreover it testsRoutes the support to <? extends T> type.

## RandomLongFile.java
Copy/Paste three times the content of PredictionModule.java in order to check the performance with huge files.


# ANTLR Result

At the moment, it seems to be the best parser among the ones tested. The only drawback is the performance, since it is 100x slower than the others.
The main problem of the other parses is that they could not handle correctly the comments. Thus a round trip is impossible to achieve :( 

## Errors
With wrong defined classes, it managed to parse them with few errors, but still it creates a roughly, but correct enough, tree representation.
It has a really good parse error recovery.

## Performance w/ ANTLRv4
It seems that the number of lines of code does not influence the performance. 
Instead, the parsing is slower if we have many nested block of code.
This is confirmed by the RandomLongFile where we have c/p the content of PredictionModule three times where the increasing time required to parse the file is only of 25.4%.
Nonetheless, c/p the content of LocalDiscovery two times in HeavyParsing.java increase the parsing time only by 15%.
The first results are available in the following table.



| File             | # Row           | Time (ms)  | Time w/Comment (ms) |
|------------------|:---------------:|-----------:|--------------------:|
| Antlr4Mojo       | 522             | 6654       | 6799                |
| Calculator       | 24              | 510        | 488                 | 
| HeavyParsing     | 790             | 16213      | 16455               |
| HelloWorld       | 3               | 235        | 229                 |
| LocalDiscovery   | 404             | 14092      | 14549               |
| OnlyMethod       | 86              | 34         | 33                  |
| PredictionModule | 380             | 1716       | 1765                |  
| RandomLongFile   | 1070            | 2152       | 1921                |

They are calculated with the class TimeParsing runned 10 times (with bash for to avoid that jit kicks in) and averaging the results.

## Strategies
A performance bottleneck is the strategy used to handle errors and how to construct the DFA.
As presented in [issue #192 and issue #400](https://github.com/antlr/antlr4/issues/192) do not use error recovery helps the performance.
The SSL strategy improved by 5s (+ ~33%) the performance in **LocalDiscovery** class but, as expected, it cannot handle correctly the full java grammar.
For some classes it throws some errors and it do not manage to parse them correctly. 

The strategy `LL_EXACT_AMBIG_DETECTION` does not look like to improve the parsing time.

## Expresiveness
At the moment we can make round trip transformations: `src -> java8CommentSupportedAST -> src`. A new grammar supports the handling of the comment. 
It correctness was tested over jhotdraw, hadoop and spark.
It manages to parse correctly every single java file.
