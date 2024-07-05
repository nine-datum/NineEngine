rd /s /q build
mkdir build
dir /s /b *.java > build/sourcelist.txt
javac -target 11 -source  11 -classpath lib/* -d build/src @build/sourcelist.txt
del build/sourcelist.txt
cd build
(echo Main-Class: nine.main.Program & echo Class-Path: . & echo. ) > manifest.txt
mkdir lib
cd lib

@echo off
setlocal

for %%f in ("../../lib/*") do (
    echo extracting file: %%f
    jar xf "../../lib/%%f"
)

endlocal

cd ..
jar cvfm nine.jar manifest.txt -C src/ . -C lib/ .
cd ..