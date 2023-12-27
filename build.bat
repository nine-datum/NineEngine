rd /s /q build
mkdir build
dir /s /b *.java > build/sourcelist.txt
javac -target 11 -source  11 -classpath lib/* -d build @build/sourcelist.txt
del build/sourcelist.txt
cd build
(echo Main-Class: nine.main.Program & echo Class-Path: ../lib/jar12.jar ../lib/jar13.jar ../lib/jar.jar ../lib/jar1.jar ../lib/jar2.jar ../lib/jar3.jar ../lib/jar4.jar ../lib/jar5.jar ../lib/jar6.jar ../lib/jar7.jar ../lib/jar8.jar ../lib/jar9.jar ../lib/jar10.jar ../lib/jar11.jar ../lib/jar14.jar ../lib/jar15.jar ../lib/jar16.jar ../lib/jar17.jar ../lib/jena.jar & echo. ) > manifest.txt
jar -cvfm nine.jar manifest.txt nine
cd ..