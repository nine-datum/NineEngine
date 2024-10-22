#!/bin/bash

# Удаление и создание папки build
rm -rf build
mkdir build

echo "build : collecting sources"

# Поиск всех .java файлов и запись их в sourcelist.txt
find . -name "*.java" > build/sourcelist.txt

echo "build : compiling classes"

# Компиляция .java файлов
javac -target 11 -source 11 -classpath lib/* -d build/src @build/sourcelist.txt

# Удаление списка исходников
rm build/sourcelist.txt
cd build

echo "build : writing manifest"

# Запись манифеста
echo "Main-Class: nine.main.Program\nClass-Path: .\n" > manifest.txt

echo "build : including dependencies..."

# Создание папки lib и переход в неё
mkdir lib
cd lib

# Извлечение файлов из jar-файлов в ../../lib/*
for f in ../../lib/*; do
    echo "extracting file: $f"
    jar xf "$f"
done

echo "build : compressing archive..."

cd ..
# Создание jar-архива с манифестом
jar cvfm nine.jar manifest.txt -C lib/ .
echo "current dir : $(pwd)"

# Удаление временных папок
rm -rf lib src
cd ..

echo "build : done"
