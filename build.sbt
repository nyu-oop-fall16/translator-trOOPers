name := "translator"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

parallelExecution in Test := false

mainClass in assembly := Some("edu.nyu.oop.Boot")

unmanagedResourceDirectories in Compile += { baseDirectory.value / "src/main/java" }

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.9"

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

scalariformSettings

// Custom tasks

compile <<= (compile in Compile) dependsOn(clean, compile in Test)

// Formats any C++ code located in the /output directory
val formatc = TaskKey[Unit]("formatc", "Code formatter for generated C++. Run it after translation.")
formatc := """astyle --suffix=none --style=allman output/* """.!

// Formats Java code located in the test directory
val formatjtests = TaskKey[Unit]("formatjtests", "Code formatter for Java Unit Tests. Run it regularly.")
formatjtests := """astyle --recursive --suffix=none --style=java src/test/java/* """.!

// Formats Java code located in the main directory
val formatj = TaskKey[Unit]("formatj", "Code formatter for Java. Run it regularly.")
formatj <<= formatjtests map { (e) => """astyle --recursive --suffix=none --style=java src/main/java/* """.! }

// Compiles C++ code located in the output directory
val compilec = TaskKey[Unit]("compilec", "Compile the generated C++.")
// Add any additional files you wish to be compiled by the compilec command here.
compilec := """g++ output/main.cpp output/java_lang.cpp output/output.cpp -o output/a.out """.!

// Executes C++ code located in the output directory
val execc = TaskKey[Unit]("execc", "Execute the generated C++.")
execc := """output/a.out""".!

// Shortcut for exectuting our root xtc Tool
val runxtc = inputKey[Unit]("Run a command on your Boot class.")
runxtc := Def.inputTaskDyn {
  val args = sbt.complete.DefaultParsers.spaceDelimited("<arg>").parsed
  val cmd = s" edu.nyu.oop.Boot ${args.mkString(" ")}"
  (runMain in Compile).toTask(cmd)
}.evaluated

// Dumps configuration located in the properties file to console
val conf = inputKey[Unit]("Output application configuration")
conf := Def.inputTaskDyn {
  (runMain in Compile).toTask(s" edu.nyu.oop.util.XtcProps")
}.evaluated