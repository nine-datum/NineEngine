@echo off

rd /s /q build
mkdir build

echo build : collecting sources

dir /s /b *.java > build/sourcelist.txt

echo build : compiling classes

javac -target 11 -source  11 -classpath lib/* -d build/src @build/sourcelist.txt
del build/sourcelist.txt
cd build

echo build : writing manifest

(echo Main-Class: nine.main.Program & echo Class-Path: . & echo. ) > manifest.txt

echo build : including dependencies...

mkdir lib
cd lib

setlocal

for %%f in ("../../lib/*") do (
    echo extracting file: %%f
    jar xf "../../lib/%%f"
)

endlocal

echo build : compressing archive...

cd ..
jar cvfm nine.jar manifest.txt -C src/ . -C lib/ .
cd ..

echo build : done