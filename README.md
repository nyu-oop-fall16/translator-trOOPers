This translator contains the following classes, all of which are either new or modified from their original state:
* JavaFiveImportParser.java 
* JavaAstVisitor.java
* HeaderAstMaker.java 
* DataLayout.java
* VTable.java
* ClassInfo.java
* MethodInfo.java 
* HeaderFileMaker.java 
* HeaderClassPrinter.java 
* AggregatedHeaderPrinter.java 
* Boot.java
* Translator.java

In order to run the translator WITHOUT using the shell script, the user must call sbt from the terminal in the translator directory and then call "runTranslator". The files output.h, output.cpp, and main.cpp in the output folder will then be modified accordingly.

In sbt do:
  
  &nbsp;&nbsp;&nbsp;`runxtc -runTranslator src/test/java/inputs/testxxx/Testxxx.java`
  
Replace the xxx in "testxxx" and "Testxxx.java" with the number of the Java class file you want to run. (The "runTranslator" tag only runs fully for tests 000 to 005).

The other possible commands are "printJavaAst", "printJavaCode", "printJavaImportCode", "generateListGnodes", "printHeaderAst", "printHeaderFile", "printMutatedAst", and "printImplementationFiles".

To run unit tests, in sbt do any of the following:

```
 runxtc -generateListGNodes src/test/java/inputs/testxxx/Testxxx.java
 runxtc -generateListGNodes -printHeaderAst src/test/java/inputs/testxxx/Testxxx.java
 runxtc -generateListGNodes -printHeaderAst -printHeaderFile src/test/java/inputs/testxxx/Testxxx.java
 runxtc -generateListGNodes -printMutatedAst src/test/java/inputs/testxxx/Testxxx.java
 runxtc -generateListGNodes -printMutatedAst -printImplementationFiles src/test/java/inputs/testxxx/Testxxx.java
```

In order, the tests are for Phase 1, Phase 2, Phase 3, Phase 4, and Phase 5.

To run the translator using the shell script, extract the shell script folder from src/main/java/edu/nyu/oop/util. Put it on the desktop or whatever location on the computer is preferred. Make a new folder in ScriptPrinter/ScripterCaller/src called "Translators" and clone the translator into that folder. Compile and run ScriptCaller.java and follow the instructors printed in the shell.

The translator supports inheritance and dynamic dispatch, Java package imports, standard Java field and method declarations, method overloading, constructor overloading, method overriding, and arrays to varying degrees in each of the phases. The translator does NOT support memory management; it implements the delegation design pattern instead.
Any files containing a main method must encapsulate that method in a class that contains nothing else. The name of this class must be the same as the name of the file.
