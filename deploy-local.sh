name=$1
ver=$2
mvn install:install-file -Dfile=build/nine.jar -DgroupId=io.github.taqmuraz -DartifactId=$name -Dversion=$ver -Dpackaging=jar