rd /s /q build
mkdir build
dir /s /b *.java > build/sourcelist.txt
javac -target 11 -source  11 -classpath lib/* -d build/src @build/sourcelist.txt
del build/sourcelist.txt
cd build
(echo Main-Class: nine.main.Program & echo Class-Path: . & echo. ) > manifest.txt
mkdir lib
cd lib
jar xf ../../lib/jena.jar
jar xf ../../lib/jar.jar
jar xf ../../lib/jar1.jar
jar xf ../../lib/jar2.jar
jar xf ../../lib/jar3.jar
jar xf ../../lib/jar4.jar
jar xf ../../lib/jar5.jar
jar xf ../../lib/jar6.jar
jar xf ../../lib/jar7.jar
jar xf ../../lib/jar8.jar
jar xf ../../lib/jar9.jar
jar xf ../../lib/jar10.jar
jar xf ../../lib/jar11.jar
jar xf ../../lib/jar12.jar
jar xf ../../lib/jar13.jar
jar xf ../../lib/jar14.jar
jar xf ../../lib/jar15.jar
jar xf ../../lib/jar16.jar
jar xf ../../lib/jar17.jar
cd ..
jar cvfm nine.jar manifest.txt -C src/ . -C lib/ .
cd ..