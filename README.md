This translator contains the following classes, all of which are either new or modified from their original state:
JavaFiveImportParser.java
JavaAstVisitor.java
HeaderAstMaker.java
ClassInfo.java
MethodInfo.java
HeaderFileMaker.java
HeaderClassPrinter.java
AggregatedHeaderPrinter.java
MutateJavaAst.java
OutputCppMaker.java
MainCppMaker.java
Boot.java

In order to run the translator, the user must call sbt from the terminal in the translator directory and then call "runTranslator". The files output.h, output.cpp, and main.cpp in the output folder will then be modified accordingly. 
The other possible commands are "printJavaAst", "printJavaCode", "printJavaImportCode", "printHeaderAst", "printHeaderFile", "printMutatedAst", and "printImplementationFiles".

The translator supports inheritance and dynamic dispatch, Java package imports, and standard Java field and method declarations. 
Any files containing a main method must encapsulate that method in a class that contains nothing else. The name of this class must be the same as the name of the file.

The translator does NOT support complex types like arrays. 

