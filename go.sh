#!/bin/bash
javac -cp json.jar *.java
#java -classpath .;json.jar Challenge
java -cp json.jar:. Challenge